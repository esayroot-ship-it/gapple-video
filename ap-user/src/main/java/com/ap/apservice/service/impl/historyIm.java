package com.ap.apservice.service.impl;

import com.ap.apcommon.entity.user.history;
import com.ap.apcommon.tools.R;
import com.ap.apservice.dao.historyMapper;
import com.ap.apservice.service.historyService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service("user-history")
public class historyIm implements historyService {

    @Autowired
    private historyMapper historyMapper;

    @Override
    public R getHistorysByUserId(Long userId) {
        LambdaQueryWrapper<history> qw = new LambdaQueryWrapper<>();
        // Entity field 'uid' maps to 'user_id'
        qw.eq(history::getUid, userId.intValue());
        // Order by last play time descending usually makes sense for history
        qw.orderByDesc(history::getLasttime);
        return R.ok("成功", historyMapper.selectList(qw));
    }

    @Override
    @Transactional
    public R addHistory(Long userId, Long videoId) {
        LambdaQueryWrapper<history> qw = new LambdaQueryWrapper<>();
        qw.eq(history::getUid, userId.intValue());
        qw.eq(history::getVid, videoId.intValue());

        history exist = historyMapper.selectOne(qw);
        String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (exist != null) {
            // Update existing history
            exist.setLasttime(nowStr);
            historyMapper.updateById(exist);
        } else {
            // Create new history
            history newHistory = new history();
            newHistory.setUid(userId.intValue());
            newHistory.setVid(videoId.intValue());
            newHistory.setLasttime(nowStr);
            historyMapper.insert(newHistory);
        }
        return R.ok("成功");
    }
}
