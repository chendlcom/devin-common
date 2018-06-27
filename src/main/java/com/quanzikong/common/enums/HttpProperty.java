package com.quanzikong.common.enums;

/**
 * Http Properties key names
 */
public enum HttpProperty {
    // -------- you can modify start ---------
    //         name code Description
    /**
     * Content-Type
     */
    ContentType("Content-Type", "send params type"),
    /**
     * Content-Length
     */
    ContentLength("Content-Length", "content length"),
    /**
     * Connection
     */
    Connection("Connection", "connection"),
    /**
     * Charset
     */
    Charset("Charset", "Charset"),
    /**
     * Charset
     */
    UserAgent("User-Agent", "user agent"),
    // --------  you can modify end  ---------
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
    private HttpProperty(String code, String description) {
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
    public static HttpProperty getByCode(String code) {
        for (HttpProperty item : values()) {
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
    public static HttpProperty getByDescription(String description) {
        for (HttpProperty item : values()) {
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
