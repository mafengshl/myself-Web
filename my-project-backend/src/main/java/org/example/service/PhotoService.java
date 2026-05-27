package org.example.service;

import org.example.entity.dto.Photo;

import java.util.List;

public interface PhotoService {
    Photo savePhoto(Photo photo);
    boolean deletePhoto(Integer id, Integer userId);
    List<Photo> getAllPhotos();
    List<Photo> getPhotosByUserId(Integer userId);
    Photo getPhotoById(Integer id);
    boolean updateLikes(Integer id);
}
