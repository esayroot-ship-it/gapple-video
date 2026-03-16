package com.ap.apservice.service.impl;

import com.ap.apcommon.entity.user.favorite;
import com.ap.apcommon.tools.R;
import com.ap.apservice.dao.favoriteMapper;
import com.ap.apservice.service.favoriteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("user-favoriteIm")
public class favoriteIm implements favoriteService {
    // Entity field 'uid' maps to 'user_id'

    @Autowired
    private favoriteMapper favoriteMapper;

    public R addFavorite(Long userId, Long videoId) {
        favorite f = new favorite();
        f.setUid(userId.intValue());
        f.setVid(videoId.intValue());
        favoriteMapper.insert(f);
        return R.ok("成功");
    }

    @Override
    public R getFavoritesByUserId(Long userId) {
        LambdaQueryWrapper<favorite> qw = new LambdaQueryWrapper<>();
        qw.eq(favorite::getUid, userId.intValue());
        return R.ok("成功", favoriteMapper.selectList(qw));
    }

    @Override
    @Transactional
    public R deleteFavorite(Long favoriteId) {
        favoriteMapper.deleteById(favoriteId);
        return R.ok("成功");
    }
}
