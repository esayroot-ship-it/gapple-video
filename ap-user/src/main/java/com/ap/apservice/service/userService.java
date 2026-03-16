package com.ap.apservice.service;

import com.ap.apcommon.tools.R;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MultipartFilter;

public interface userService {
    R register(String username, String password);

    R login(String username, String password);

    R getById(Long id);

    R setnickname(String nickname, String username);

    R setuserimg(Long id , MultipartFile file);
}
