package com.quanzikong.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * util base fastjson
 *
 * @author Devin on 2018-06-27 20:06.
 */
public class JsonUtil {

    /**
     * 判断给定字符串是否JSONObject String
     *
     * @param str String
     *
     * @return boolean
     */
    public static boolean isJSONObject(String str) {
        return (StringUtil.isNotBlank(str) && str.matches("^\\{.*\\}$"));
    }

    /**
     * 判断给定字符串是否JSONArray String
     *
     * @param str String
     *
     * @return boolean
     */
    public static boolean isJSONArray(String str) {
        return (StringUtil.isNotBlank(str) && str.matches("^\\[.*\\]$"));
    }

    /**
     * 判断给定字符串是否JSONObject或者JSONArray String
     *
     * @param str String
     *
     * @return boolean
     */
    public static boolean isJSON(String str) {
        return isJSONObject(str) || isJSONArray(str);
    }

    public static JSONObject toCamelJSONObject(String jsonObjectStr) {
        if (isJSONObject(jsonObjectStr)) {
            JSONObject rst = new JSONObject();

            JSONObject o = JSONObject.parseObject(jsonObjectStr);
            String rstKey, rstVal;
            for (String key : o.keySet()) {
                rstKey = StringUtil.toHump1Lower(key);
                rstVal = o.getString(key);

                rst.put(rstKey, toCamelJSON(rstVal));
            }

            return rst;
        }
        return new JSONObject();
    }

    public static JSONArray toCamelJSONArray(String jsonArrayStr) {
        if (isJSONArray(jsonArrayStr)) {
            JSONArray rst = new JSONArray();

            JSONArray a = JSONObject.parseArray(jsonArrayStr);
            for (int i = 0; i < a.size(); i++) {
                String elStr = a.getString(i);
                rst.add(toCamelJSON(elStr));
            }

            return rst;
        }
        return new JSONArray();
    }

    /**
     * 将json字符串中所有的key转换成camel case
     *
     * @param jsonStr String
     *
     * @return JSON
     */
    public static JSON toCamelJSON(String jsonStr) {
        if (StringUtil.isBlank(jsonStr)) {
            return null;
        }

        String str = jsonStr.trim();
        JSON json = null;
        try {
            if (isJSONObject(str)) {
                json = toCamelJSONObject(str);
            } else if (isJSONArray(str)) {
                json = toCamelJSONArray(str);
            }
        } catch (Exception e) {
        }

        return json;
    }

}
