package com.mallotec.reb.socketdemo.tool;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * Created by reborn on 2019-05-20.
 */
public class StringUtil {

    public static String mapToJson(Map<String, String> stringMap) {
        JSONObject jsonObject = new JSONObject();
        for (String key :
                stringMap.keySet()) {
            jsonObject.put(key, stringMap.get(key));
        }
        return jsonObject.toJSONString();
    }
}
