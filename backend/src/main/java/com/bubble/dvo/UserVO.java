package com.bubble.dvo;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private Integer gender;
    private Integer age;
    private String email;
    private String phone;
    private String bio;
}
