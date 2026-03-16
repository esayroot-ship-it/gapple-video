package com.ap.apcommon.tools;

import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;

public class PinYinUtils {
    /**
     * 将汉字转为全拼（极简模式，无空格，无声调）
     * 示例："中国" -> "zhongguo"
     */
    public static String toPinyin(String chinese) {
        if (chinese == null || chinese.isEmpty()) {
            return "";
        }
        return PinyinHelper.toPinyin(chinese, PinyinStyleEnum.NORMAL).replace(" ", "");
    }
}
