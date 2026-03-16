package com.ap.apadmin.service;

import com.ap.apcommon.entity.user.User;
import com.ap.apcommon.tools.R;


public interface AdminService {

    R createAdmin(String username, String password);

    R adminLogin(String username, String password);


    R getallAdmin();

    R deleteAdmin(String username);
}
