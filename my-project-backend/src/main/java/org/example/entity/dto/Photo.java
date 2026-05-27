package org.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("db_photos")
public class Photo {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer userId;
    String username;
    String avatar;
    String url;
    String description;
    Integer likes;
    Date createdAt;
    String delFlag;
}
