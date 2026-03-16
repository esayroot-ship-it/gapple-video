package com.ap.apservice.service;

import com.ap.apcommon.tools.R;

public interface commentService {

    /**
     * 用户对指定视频发表评论
     */
    R addComment(Integer userId, Integer videoId, String content);

    /**
     * 获取对应视频的所有评论
     */
    R getCommentsByVideoId(Integer videoId);
}
