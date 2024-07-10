package com.custom.common.service;

import com.custom.common.pojo.User;

/**
 * @author Gemaxis
 * @date 2024/07/10 11:46
 **/
public interface UserService {
    User getUserByUserId(Integer id);

    Integer insertUserId(User user);
}
