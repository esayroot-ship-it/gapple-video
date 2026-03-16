package com.ap.apservice.service.impl;

import com.ap.apcommon.entity.user.comment;
import com.ap.apcommon.tools.R;
import com.ap.apservice.dao.commentMapper;
import com.ap.apservice.service.commentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("user-commentIm")
public class commentIm implements commentService {

    @Autowired
    private commentMapper commentMapper;

    @Override
    public R addComment(Integer userId, Integer videoId, String content) {
        comment newComment = new comment(
                null,           // id
                videoId,        // vid
                userId,         // uid
                content,        // content
                (byte) 0,       // like_count
                1,              // status
                LocalDateTime.now() // created_at
        );

        int  saved = commentMapper.insert(newComment);
        return saved > 0 ? R.ok("评论发布成功") : R.fail("评论发布失败");
    }

    @Override
    public R getCommentsByVideoId(Integer videoId) {
        LambdaQueryWrapper<comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(comment::getVid, videoId)
                .eq(comment::getStatus, 1)
                .orderByDesc(comment::getCreatedat);

        List<comment> list = commentMapper.selectList(queryWrapper);
        if (list.isEmpty()) {
            return R.fail("暂无评论");
        }
        return R.ok("成功", list);
    }
}
