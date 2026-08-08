package com.client.androidnga.core.data.model

import com.client.androidnga.core.data.bean.ThreadPostBean
import gov.anzong.androidnga.common.base.JavaBean

class ThreadInfo : JavaBean {
    @JvmField
    var rowList: List<ThreadPostBean>? = null

    @JvmField
    var threadInfo: ThreadPageInfo? = null

    @JvmField
    var threadPostList = mutableListOf<ThreadPostInfo>()

    private var __ROWS = 0

    @JvmField
    var rowNum: Int = 0

    /**
     * 从服务端获取的原始数据
     */
    var rawData: String? = null

    fun get__ROWS(): Int {
        return __ROWS
    }

    fun set__ROWS(__ROWS: Int) {
        this.__ROWS = __ROWS
    }


}

class ThreadPostInfo : JavaBean {

    @JvmField
    var formatHtml: String? = null

    /**
     * 客户端类型
     */
    @JvmField
    var clientModel: ClientModel? = null

    /**
     * 是否被本地屏蔽
     */
    @JvmField
    var isBlocked = false

    /**
     * 是否匿名
     */
    @JvmField
    var isAnonymous = false

    /**
     * 是否被禁言
     */
    @JvmField
    var isMuted = false

    /**
     * 禁言时间
     */
    @JvmField
    var muteTime: String? = null

    /**
     * 发帖数
     */
    @JvmField
    var postCount: String? = null

    /**
     * 威望
     */
    @JvmField
    var reputation: Float = 0f

    /**
     * 用户组
     */
    @JvmField
    var memberGroup: String? = null
}

enum class ClientModel(val modelName: String) {
    ANDROID("安卓客户端"),
    ANDROID_BROWSER("安卓浏览器"),
    IOS("苹果客户端"),
    IOS_BROWSER("苹果浏览器"),
    WP("Windows Phone客户端"),
    UNKNOWN("未知客户端"),
    UNKNOWN_BROWSER("未知浏览器")
}
