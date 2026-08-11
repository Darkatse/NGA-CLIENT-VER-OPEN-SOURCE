package com.client.androidnga.core.data.html

class HtmlData(var rawData: String?) : Cloneable {
    @JvmField
    var uid: String? = null

    @JvmField
    var pid: String? = null

    @JvmField
    var tid: String? = null

    @JvmField
    var attachmentHost: String? = null

    var isInBackList: Boolean = false

    var isDarkMode: Boolean = false

    var subject: String? = null

    var alertInfo: String? = null

    private var mTextSize = 0

    var emotionSize: Int = 0

    var tableTextSize: Int = 0
        private set

    var isShowImage: Boolean = true

    var vote: String? = null

    var signature: String? = null

    var nGAHost: String? = null

    var commentList: MutableList<CommentData?>? = null

    var attachmentList: MutableList<AttachmentData?>? = null

    var textSize: Int
        get() = mTextSize
        set(textSize) {
            mTextSize = textSize
            this.tableTextSize = (textSize * 0.9).toInt()
        }

    companion object {
        @JvmStatic
        fun create(rawData: String?, host: String?): HtmlData {
            val htmlData = HtmlData(rawData)
            htmlData.nGAHost = host
            return htmlData
        }
    }
}
