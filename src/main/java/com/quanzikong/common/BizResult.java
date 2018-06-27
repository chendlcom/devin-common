package com.quanzikong.common;

import java.util.Arrays;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;

/**
 * 类BizResult.java的实现描述<br/>
 * 方便业务层处理完成后返回多值对象
 *
 * @author Devin 2016年2月2日 下午3:02:46
 */
public class BizResult extends JSONObject {

    public BizResult() {
        //处理结果。true：成功，false：失败
        this.put("success", false);

        //处理结果信息
        this.put("msg", "操作失败");
    }

    public BizResult(boolean success, String msg) {
        //处理结果。true：成功，false：失败
        this.put("success", success);

        //处理结果信息
        this.put("msg", msg);
    }

    /**
     * 成功(success=true), 支持String.format(tpl, aprs)模式
     *
     * @param args String...
     *
     * @return BizResult
     */
    public BizResult toSuccess(String... args) {
        this.put("success", true);
        if (args.length > 1) {
            String msg = String.format(args[0], Arrays.asList(args).subList(1, args.length).toArray(new Object[0]));
            this.put("msg", msg);
        } else {
            this.put("msg", args.length > 0 ? args[0] : "操作成功");
        }
        return this;
    }

    /**
     * 失败(success=false), 支持String.format(tpl, aprs)模式
     *
     * @param args String...
     *
     * @return BizResult
     */
    public BizResult toFail(String... args) {
        this.put("success", false);
        if (args.length > 1) {
            String msg = String.format(args[0], Arrays.asList(args).subList(1, args.length).toArray(new Object[0]));
            this.put("msg", msg);
        } else {
            this.put("msg", args.length > 0 ? args[0] : "操作失败");
        }
        return this;
    }

    public BizResult xPut(String key, Object val) {
        this.put(key, val);
        return this;
    }

    public boolean contains(String key) {
        return this.getKeys().contains(key);
    }

    // setters & getters

    public boolean isSuccess() {
        return this.getBoolean("success");
    }

    public boolean isFailed() {
        return !this.isSuccess();
    }

    public void setSuccess(boolean success) {
        this.put("success", success);
    }

    public String getMsg() {
        return this.getString("msg");
    }

    public void setMsg(String msg) {
        this.put("msg", msg);
    }

    public Set<String> getKeys() {
        return this.keySet();
    }
}
