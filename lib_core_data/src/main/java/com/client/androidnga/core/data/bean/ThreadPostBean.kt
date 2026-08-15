package com.client.androidnga.core.data.bean

import com.client.androidnga.core.data.model.ThreadPostInfo
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
    var signature: String? = null

    @JvmField
    var comments: List<ThreadPostBean>? = null

    @JvmField
    var comment: Map<String, ThreadPostBean> = emptyMap()

    @JvmField
    val threadPostInfo = ThreadPostInfo()

    @JvmField
    var from_client: String? = null

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


}
