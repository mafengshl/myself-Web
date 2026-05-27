import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class update_password {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("admin: " + encoder.encode("123456"));
        System.out.println("馬小風: " + encoder.encode("shl13539755908"));
    }
}
