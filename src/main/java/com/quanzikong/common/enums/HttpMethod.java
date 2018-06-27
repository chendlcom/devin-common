package com.quanzikong.common.enums;

/**
 * Http方法
 * link: https://restfulapi.net/http-methods/
 */
public enum HttpMethod {
    // name code Description
    /**
     * GET - Read
     */
    GET("GET", "Read"),
    /**
     * POST - Update/Replace
     */
    POST("POST", "Update/Replace"),
    /**
     * PUT - Update/Replace
     */
    PUT("PUT", "Update/Replace"),
    /**
     * DELETE - Delete
     */
    DELETE("DELETE", "Delete"),
    /**
     * PATCH - Partial Update/Modify
     */
    PATCH("PATCH", "Partial Update/Modify"),
    // end
    ;

    /**
     * 枚举编码
     */
    private final String code;

    /**
     * 描述说明
     */
    private final String description;

    /**
     * 默认构造器
     *
     * @param code        枚举编码
     * @param description 描述说明
     */
    private HttpMethod(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 通过枚举<code>code</code>获得枚举
     *
     * @param code 枚举编码
     *
     * @return 状态枚举
     */
    public static HttpMethod getByCode(String code) {
        for (HttpMethod item : values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 通过枚举<code>description</code>获得枚举
     *
     * @param description 枚举编码
     *
     * @return 状态枚举
     */
    public static HttpMethod getByDescription(String description) {
        for (HttpMethod item : values()) {
            if (item.getDescription().equals(description)) {
                return item;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
