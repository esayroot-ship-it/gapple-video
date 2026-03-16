package com.ap.apadmin.service;

import com.ap.apcommon.entity.user.User;
import com.ap.apcommon.tools.R;

public interface userService {
    R createUser(User user);

    R getUserByUsername(String username);

    R updateUser(User user);

    R deleteUser(Long id);

    R deleteUserByname(String username);

    R getallUser();

    R banUserByUsername(String username);

    R rebanUserByUsername(String username);
}
