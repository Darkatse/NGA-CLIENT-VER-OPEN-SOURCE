package com.client.androidnga.core.data.model

import gov.anzong.androidnga.common.base.JavaBean

class ThreadPageInfo : JavaBean {
    var tid: Int = 0

    var author: String? = null

    var fid: Int = 0

    var authorId: Int = 0

    var lastPoster: String? = null

    var replies: Int = 0

    var subject: String? = null

    var titleFont: String? = null

    var type: Int = 0

    var topicMisc: String? = null

    var page: Int = 0

    var pid: Int = 0

    var position: Int = 0

    var isAnonymity: Boolean = false

    var postDate: Int = 0

    var replyInfo: ReplyInfo? = null

    private var mBoard: String? = null

    /**
     * 是否是版面镜像
     */
    var isMirrorBoard: Boolean = false
        private set

    var board: String?
        get() = mBoard
        set(parentBoard) {
            mBoard = parentBoard
            isMirrorBoard = "版面镜像" == parentBoard
        }

    class ReplyInfo : JavaBean {
        var pidStr: String? = null

        var content: String? = null

        var subject: String? = null

        var postDate: String? = null

        var authorId: String? = null

        var tidStr: String? = null
    }

    override fun equals(other: Any?): Boolean {
        return other is ThreadPageInfo
                && tid == other.tid && pid == other.pid
    }

    override fun toString(): String {
        return "tid = $tid  pid = $pid"
    }
}
