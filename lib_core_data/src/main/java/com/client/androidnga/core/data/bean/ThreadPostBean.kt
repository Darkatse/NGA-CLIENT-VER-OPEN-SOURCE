package com.client.androidnga.core.data.bean

import gov.anzong.androidnga.common.base.JavaBean

/**
 * 每一行的内容
 */
class ThreadPostBean : JavaBean {
    @JvmField
    var tid: Int = 0
    @JvmField
    var fid: Int = 0
    @JvmField
    var author: String? = null //user name
    @JvmField
    var authorid: Int = 0
    @JvmField
    var subject: String? = null
    @JvmField
    var vote: String? = null
    @JvmField
    var postdate: String? = null
    @JvmField
    var pid: Int = 0
    var iSANONYMOUS: Boolean = false
    @JvmField
    var alterinfo: String? = null // something like "edited by ..."
    @JvmField
    var content: String? = null
    @JvmField
    var lou: Int = 0
    @JvmField
    var attachs: Map<String, ThreadAttachBean>? = null
    @JvmField
    var level: String? = null
    @JvmField
    var yz: String? = null //negative integer if user is nuked
    @JvmField
    var js_escap_avatar: String? = null //avatar url
    @JvmField
    var muteTime: String? = null
    @JvmField
    var aurvrc: Int = 0 //prestige
    @JvmField
    var signature: String? = null
    @JvmField
    var comments: List<ThreadPostBean>? = null
    @JvmField
    var hotReplies: List<String>? = null //热门回复

    var isInBlackList: Boolean = false

    var formattedHtmlData: String? = null

    var fromClient: String? = null
    var fromClientModel: String? = null

    var isMuted: Boolean = false

    var postCount: String? = null

    var reputation: Float = 0f

    var memberGroup: String? = null

    @JvmField
    var attachmentHost: String? = null

    var mImageUrlList: MutableList<String> = ArrayList()

    @JvmField
    var score: Int = 0

    fun addImageUrl(url: String) {
        mImageUrlList.add(url)
    }

    val imageUrls: List<String>
        get() = mImageUrlList


    fun set_IsInBlackList(isin: Boolean) {
        this.isInBlackList = isin
    }

    fun get_isInBlackList(): Boolean {
        return isInBlackList
    }
}
