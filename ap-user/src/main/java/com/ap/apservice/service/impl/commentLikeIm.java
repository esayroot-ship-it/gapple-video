package com.ap.apservice.service.impl;

import com.ap.apcommon.entity.user.commentlike;
import com.ap.apcommon.tools.R;
import com.ap.apservice.dao.commentLikeMapper;
import com.ap.apservice.dao.commentMapper;
import com.ap.apservice.service.commentLikeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("user-commentLikeIm")
public class commentLikeIm implements commentLikeService {

    @Autowired
    private commentLikeMapper commentLikeMapper;

    @Override
    public R likeComment(Integer userId, Integer commentId) {
        // 1. 检查是否重复点赞
        QueryWrapper<commentlike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("comment_id", commentId);

        Long count = commentLikeMapper.selectCount(queryWrapper);
        if (count > 0) {
            return R.fail("您已经点赞过了");
        }

        // 2. 插入点赞记录
        commentlike like = new commentlike(
                null,           // id
                commentId,      // comment_id
                userId,         // user_id
                LocalDateTime.now() // created_at
        );

        int rows = commentLikeMapper.insert(like);
        return rows > 0 ? R.ok("点赞成功") : R.fail("点赞失败");
    }

    @Override
    public R getLikeCount(Integer commentId) {
        QueryWrapper<commentlike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("comment_id", commentId);

        Long count = commentLikeMapper.selectCount(queryWrapper);
        return R.ok("获取成功", count);
    }
}
