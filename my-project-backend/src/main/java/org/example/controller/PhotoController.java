package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.RestBean;
import org.example.entity.dto.Account;
import org.example.entity.dto.Photo;
import org.example.service.AccountService;
import org.example.service.PhotoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    @Resource
    private PhotoService photoService;

    @Resource
    private AccountService accountService;

    @Value("${server.port:8080}")
    private String serverPort;

    private static final String UPLOAD_DIR = "./uploads/photos";

    @GetMapping("/list")
    public RestBean<List<Photo>> getAllPhotos() {
        List<Photo> photos = photoService.getAllPhotos();
        if (photos != null) {
            return RestBean.success(photos);
        }
        return RestBean.failure(500, "获取照片列表失败");
    }

    @GetMapping("/my")
    public RestBean<List<Photo>> getMyPhotos() {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Account account = accountService.findAccountByNameOrEmail(user.getUsername());
            if (account == null) {
                return RestBean.unauthorized("用户未登录");
            }
            List<Photo> photos = photoService.getPhotosByUserId(account.getId());
            if (photos != null) {
                return RestBean.success(photos);
            }
            return RestBean.failure(500, "获取照片列表失败");
        } catch (Exception e) {
            log.error("获取我的照片失败", e);
            return RestBean.unauthorized("用户未登录");
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestBean<Photo> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Account account = accountService.findAccountByNameOrEmail(user.getUsername());
            if (account == null) {
                return RestBean.unauthorized("用户未登录");
            }

            if (file.isEmpty()) {
                return RestBean.failure(400, "请选择要上传的文件");
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String newFilename = UUID.randomUUID().toString() + extension;
            Path uploadPath = Paths.get(UPLOAD_DIR);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);

            String photoUrl = "http://localhost:" + serverPort + "/uploads/photos/" + newFilename;

            Photo photo = Photo.builder()
                    .userId(account.getId())
                    .username(account.getUsername())
                    .avatar(account.getRole() != null ? "Avatar.png" : null)
                    .url(photoUrl)
                    .description(description)
                    .likes(0)
                    .createdAt(new Date())
                    .delFlag("0")
                    .build();

            Photo savedPhoto = photoService.savePhoto(photo);
            if (savedPhoto != null) {
                return RestBean.success(savedPhoto);
            }
            return RestBean.failure(500, "保存照片失败");
        } catch (IOException e) {
            log.error("上传照片失败", e);
            return RestBean.failure(500, "上传照片失败");
        } catch (Exception e) {
            log.error("上传照片失败", e);
            return RestBean.unauthorized("用户未登录");
        }
    }

    @DeleteMapping("/{id}")
    public RestBean<Void> deletePhoto(@PathVariable Integer id) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Account account = accountService.findAccountByNameOrEmail(user.getUsername());
            if (account == null) {
                return RestBean.unauthorized("用户未登录");
            }

            boolean success = photoService.deletePhoto(id, account.getId());
            if (success) {
                return RestBean.success();
            }
            return RestBean.failure(400, "删除失败，可能不是您的照片");
        } catch (Exception e) {
            log.error("删除照片失败", e);
            return RestBean.unauthorized("用户未登录");
        }
    }

    @PostMapping("/{id}/like")
    public RestBean<Void> likePhoto(@PathVariable Integer id) {
        try {
            boolean success = photoService.updateLikes(id);
            if (success) {
                return RestBean.success();
            }
            return RestBean.failure(500, "点赞失败");
        } catch (Exception e) {
            log.error("点赞失败", e);
            return RestBean.failure(500, "点赞失败");
        }
    }
}
