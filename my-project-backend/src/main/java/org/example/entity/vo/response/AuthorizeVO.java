package org.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class AuthorizeVO {
    Integer id;
    String username;
    String role;
    String token;
    Date expire;//过期时间
}
