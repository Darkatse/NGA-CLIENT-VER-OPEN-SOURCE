package com.client.androidnga.core.parse

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
}