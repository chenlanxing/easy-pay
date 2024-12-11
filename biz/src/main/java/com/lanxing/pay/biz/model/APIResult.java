package com.lanxing.pay.biz.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * API结果
 *
 * @author chenlanxing
 */
@Data
@Accessors(chain = true)
public class APIResult {

    private Integer code;

    private String msg;

    private Object data;

    public static APIResult success(Object data) {
        return new APIResult().setCode(1).setMsg("OK").setData(data);
    }

    public static APIResult success() {
        return success(null);
    }

    public static APIResult fail(String msg) {
        return new APIResult().setCode(0).setMsg(msg).setData(null);
    }

    public static APIResult error() {
        return new APIResult().setCode(-1).setMsg("发生错误，已上报开发者").setData(null);
    }
}
