package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.dto.Account;
import org.example.entity.vo.request.ConfirmResetVO;
import org.example.entity.vo.request.EmailRegisterVO;
import org.example.entity.vo.request.EmailResetVO;
import org.example.mapper.AccountMapper;
import org.example.service.AccountService;
import org.example.utils.Const;
import org.example.utils.FlowUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.Query;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 内存缓存用于当 Redis 不可用时的降级存储
 */
class MemoryCache {
    private static final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> expireTimes = new ConcurrentHashMap<>();

    public static void set(String key, String value, long minutes) {
        cache.put(key, value);
        expireTimes.put(key, System.currentTimeMillis() + minutes * 60 * 1000);
    }

    public static String get(String key) {
        Long expireTime = expireTimes.get(key);
        if (expireTime != null && System.currentTimeMillis() > expireTime) {
            cache.remove(key);
            expireTimes.remove(key);
            return null;
        }
        return cache.get(key);
    }

    public static void delete(String key) {
        cache.remove(key);
        expireTimes.remove(key);
    }

    public static boolean exists(String key) {
        return get(key) != null;
    }
}

@Slf4j
@Service
public class AccountIServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    FlowUtils flowUtils;
    @Autowired(required = false)
    AmqpTemplate amqpTemplate;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    PasswordEncoder passwordEncoder;
    @Resource
    JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    String mailUsername;
    
    private boolean redisAvailable = true;

    //验证邮件发送冷却时间限制，秒为单位
    @Value("${spring.web.verify.mail-limit}")
    int verifyLimit;

    //用户登录
    @Override
    public UserDetails loadUserByUsername(String test) throws UsernameNotFoundException {
        Account account =this.findAccountByNameOrEmail(test);
        if (account == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User
                .withUsername(test)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }
    public Account findAccountByNameOrEmail(String test) {
        return this.query()
                .eq("username", test).or()
                .eq("email",test)
                .one();

    }

    //注册创建邮件
    @Override
    public String registerEmailVerifyCode(String type, String email, String ip) {
        synchronized (ip.intern()) { //流量锁 防止同一时间内大量请求同时访问改接口
            if (!this.verifyLimit(ip)) {  // 流量限制 针对统一ip的用户 单线程防止多请求
                return "请求频繁，请稍后再试";
            }
            Random random = new Random();
            int code = random.nextInt(899999) + 100000;
            Map<String, Object> date = Map.of("type", type, "email", email, "code", code);//type 注册或者重置或者修改..
            if (amqpTemplate != null) {
                try {
                    amqpTemplate.convertAndSend("mail", date);
                } catch (Exception e) {
                    log.warn("邮件队列不可用，尝试直接发送邮件，验证码为: {}", code);
                    sendEmailDirectly(type, email, code);
                }
            } else {
                log.warn("RabbitMQ未配置，直接发送邮件，验证码为: {}", code);
                sendEmailDirectly(type, email, code);
            }
            
            // 尝试使用 Redis，失败则降级到内存缓存
            try {
                stringRedisTemplate.opsForValue().
                        set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
                redisAvailable = true;
            } catch (Exception e) {
                log.warn("Redis不可用，降级到内存缓存，验证码: {}", code);
                redisAvailable = false;
                MemoryCache.set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3);
            }
            return null;
        }
    }
    
    private void sendEmailDirectly(String type, String email, int code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            String templatePath = "";
            String subject = "";
            
            switch (type) {
                case "register":
                    templatePath = "mail/register.html";
                    subject = "欢迎注册我们的网站";
                    break;
                case "reset":
                    templatePath = "mail/reset.html";
                    subject = "您的密码重置邮件";
                    break;
                default:
                    return;
            }
            
            String content = loadEmailTemplate(templatePath, code);
            
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setTo(email);
            helper.setFrom(new InternetAddress(mailUsername, "Godplace", "UTF-8"));
            
            mailSender.send(message);
            log.info("邮件发送成功，收件人: {}", email);
        } catch (Exception e) {
            log.error("发送邮件失败，收件人: {}, 错误: {}", email, e.getMessage());
        }
    }
    
    private String loadEmailTemplate(String templatePath, Integer code) throws Exception {
        ClassPathResource resource = new ClassPathResource(templatePath);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String content = reader.lines().collect(Collectors.joining("\n"));
            return content.replace("{{code}}", code.toString());
        }
    }

    @Override
    public String registerEmailAccount(EmailRegisterVO vo) {
        String email = vo.getEmail();
        String redisKey = Const.VERIFY_EMAIL_DATA + email;
        String username = vo.getUsername();
        
        // 获取验证码（支持 Redis 和内存缓存）
        String code;
        try {
            code = stringRedisTemplate.opsForValue().get(redisKey);
        } catch (Exception e) {
            code = MemoryCache.get(redisKey);
        }
        
        if (code == null) return "请先获取验证码";
        if(!code.equals(vo.getCode())) return "验证码错误";
        if(this.existsAccountByEmail(email)) return "此邮件已被注册";
        if(this.existsAccountByUsername(username)) return "此用户名已被注册";
        String password = passwordEncoder.encode(vo.getPassword());
        Account account = new Account(null,username,password,email,"user",new Date(),"0");
        if (this.save(account)) {
            // 删除验证码缓存
            try {
                stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
            } catch (Exception e) {
                MemoryCache.delete(Const.VERIFY_EMAIL_DATA + email);
            }
            return  null;
        }else return "内部错误，请联系管理员";

    }


    /**
     * 邮件验证码重置密码操作，需要检查验证码是否正确
     * @param info 重置基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */

    @Override
    public String resetEmailAccountPassword(EmailResetVO info) {
        String email = info.getEmail();
        String verify = this.resetConfirm(new ConfirmResetVO(email,info.getCode()));
        if (verify!=null) return verify;
        String password = passwordEncoder.encode(info.getPassword());
        boolean update = this.update()
                .eq("email",email)
                .set("password",password).update();
        if (update) {
            // 删除验证码缓存
            try {
                stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
            } catch (Exception e) {
                MemoryCache.delete(Const.VERIFY_EMAIL_DATA + email);
            }
        }
        return null;
    }

    /**
     * 执行密码重置确认，检查验证码是否正确
     * @param info 密码重置信息
     * @return 是否操作成功
     */
    @Override
    public String resetConfirm(ConfirmResetVO info) {
        String email = info.getEmail();
        String redisKey = Const.VERIFY_EMAIL_DATA + email;
        
        // 获取验证码（支持 Redis 和内存缓存）
        String code;
        try {
            code = stringRedisTemplate.opsForValue().get(redisKey);
        } catch (Exception e) {
            code = MemoryCache.get(redisKey);
        }
        
        if(code == null) return "请先获取验证码";
        if(!code.equals(info.getCode())) return "验证码错误";
        return null;
    }

    @Override
    public String verifyRole(String username) {
        log.info("查询用户角色，用户名: {}", username);

        if (username == null || username.isEmpty()) {
            log.warn("用户名为空");
            return "user";
        }

        try {
            // 查询完整对象而不是只查询role字段
            Account account = this.getOne(
                    new QueryWrapper<Account>()
                            .eq("username", username)
            );

            log.info("查询结果 - account: {}", account);

            if (account != null && account.getRole() != null) {
                log.info("用户角色: {}", account.getRole());
                return account.getRole();
            } else {
                log.warn("未找到用户名为 {} 的账户或角色为空，返回默认角色", username);
                return "user";
            }
        } catch (Exception e) {
            log.error("查询用户角色时发生异常", e);
            return "user";
        }
    }




    //针对ip地址来对邮件验证码进行限流操作
    private boolean verifyLimit(String ip) {
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        return flowUtils.limitOnceCheck(key,verifyLimit);
    }

    /**
     * 查询指定邮箱的用户是否已经存在
     * @param email 邮箱
     * @return 是否存在
     */
    private boolean existsAccountByEmail(String email){
        return this.baseMapper.exists(Wrappers.<Account>query().eq("email", email));
    }

    /**
     * 查询指定用户名的用户是否已经存在
     * @param username 用户名
     * @return 是否存在
     */
    private boolean existsAccountByUsername(String username){
        return this.baseMapper.exists(Wrappers.<Account>query().eq("username", username));
    }

}
