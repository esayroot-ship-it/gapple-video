package com.ap.apservice.service;

import com.ap.apcommon.tools.R;

public interface commentLikeService{

    /**
     * 用户给指定评论点赞
     */
    R likeComment(Integer userId, Integer commentId);

    /**
     * 获取对应评论的点赞数
     */
    R getLikeCount(Integer commentId);
}
