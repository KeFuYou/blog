package com.kfy2020.blog.service;

import com.kfy2020.blog.po.User;
import org.springframework.stereotype.Service;

public interface UserService {

    User checkUser(String username, String password);

}
