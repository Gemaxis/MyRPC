package com.custom.common.service;

import com.custom.common.pojo.User;


public interface UserService {
    User getUserByUserId(Integer id);

    Integer insertUserId(User user);
}
