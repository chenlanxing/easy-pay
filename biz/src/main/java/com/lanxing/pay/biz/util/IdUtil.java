package com.lanxing.pay.biz.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.RandomUtil;

/**
 * ID工具
 *
 * @author chenlanxing
 */
public class IdUtil {

    public static String generate(String prefix) {
        return prefix + new Snowflake().nextIdStr() + RandomUtil.randomNumbers(10);
    }
}
