package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.dto.Photo;
import org.example.mapper.PhotoMapper;
import org.example.service.PhotoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements PhotoService {

    @Override
    public Photo savePhoto(Photo photo) {
        try {
            if (this.save(photo)) {
                return photo;
            }
            return null;
        } catch (Exception e) {
            log.error("保存照片失败", e);
            return null;
        }
    }

    @Override
    public boolean deletePhoto(Integer id, Integer userId) {
        try {
            QueryWrapper<Photo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", id).eq("user_id", userId);
            return this.remove(queryWrapper);
        } catch (Exception e) {
            log.error("删除照片失败", e);
            return false;
        }
    }

    @Override
    public List<Photo> getAllPhotos() {
        try {
            QueryWrapper<Photo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("del_flag", "0").orderByDesc("created_at");
            return this.list(queryWrapper);
        } catch (Exception e) {
            log.error("获取所有照片失败", e);
            return null;
        }
    }

    @Override
    public List<Photo> getPhotosByUserId(Integer userId) {
        try {
            QueryWrapper<Photo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("del_flag", "0").orderByDesc("created_at");
            return this.list(queryWrapper);
        } catch (Exception e) {
            log.error("获取用户照片失败", e);
            return null;
        }
    }

    @Override
    public Photo getPhotoById(Integer id) {
        try {
            QueryWrapper<Photo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", id).eq("del_flag", "0");
            return this.getOne(queryWrapper);
        } catch (Exception e) {
            log.error("获取照片失败", e);
            return null;
        }
    }

    @Override
    public boolean updateLikes(Integer id) {
        try {
            UpdateWrapper<Photo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", id).setSql("likes = likes + 1");
            return this.update(updateWrapper);
        } catch (Exception e) {
            log.error("更新点赞数失败", e);
            return false;
        }
    }
}
