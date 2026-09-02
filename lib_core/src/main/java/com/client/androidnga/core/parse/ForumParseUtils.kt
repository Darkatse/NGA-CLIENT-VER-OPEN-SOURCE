package com.client.androidnga.core.parse

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import gov.anzong.androidnga.common.util.NLog

object ForumParsekUtils {
    @JvmStatic
    fun parseAvatarUrl(avatarUrl: String?): String? {
        if (null == avatarUrl) {
            return null
        }

        val start = avatarUrl.indexOf("http")
        if (start == 0 || start == -1) {
            return avatarUrl
        }
        var end = avatarUrl.indexOf("\"", start)
        if (end == -1) end = avatarUrl.length
        var ret: String? = null
        try {
            ret = avatarUrl.substring(start, end)
        } catch (_: Exception) {
            NLog.e("FunctionUtils", "cann't handle avatar url $avatarUrl")
        }
        return ret
    }

    @JvmStatic
    fun parseErrorMsg(js: String): String? {
        if (js.isEmpty()) {
            return "网络错误"
        } else if (js.contains("未登录")) {
            return "请重新登录"
        } else if (js.contains("无此页")) {
            return "已加载到最后一页"
        } else {
            try {
                var obj = JSON.parse(js) as JSONObject
                obj = obj.get("data") as JSONObject
                obj = obj.get("__MESSAGE") as JSONObject
                return obj.getString("1")
            } catch (e: java.lang.Exception) {
                return null
            }
        }
    }
}