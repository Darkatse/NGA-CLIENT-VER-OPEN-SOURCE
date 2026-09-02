package com.client.androidnga.core.data.bean

import gov.anzong.androidnga.common.base.JavaBean

data class ThreadAppBean(
    @JvmField
    var attachPrefix: String?,
    @JvmField
    var code: Int?,
    @JvmField
    var currentPage: Int?,
    @JvmField
    var fid: Int,
    @JvmField
    var forum_bit: Int?,
    @JvmField
    var forum_name: String?,
    @JvmField
    var hot_post: String?,
    @JvmField
    var html_head_extra: String?,
    @JvmField
    var is_forum_admin: Int,
    @JvmField
    var msg: String = "",
    @JvmField
    var perPage: Int,
    @JvmField
    var result: List<Result> = mutableListOf(),
    @JvmField
    var tauthor: String?,
    @JvmField
    var tauthorid: Int = 0,
    @JvmField
    var tmisc_bit1: Int = 0,
    @JvmField
    var totalPage: Int = 0,
    @JvmField
    var tsubject: String?,
    @JvmField
    var vrows: Int = 0
) : JavaBean {
    class Author(
        @JvmField
        var __initialized__: Boolean = false,
        @JvmField
        var avatar: String? = null,
        @JvmField
        var bit_data: Int = 0,
        @JvmField
        var buffs: Map<String, String>? = null,
        @JvmField
        var conferred_title: String? = null,
        @JvmField
        var credit: Int = 0,
        @JvmField
        var gender: Int = 0,
        @JvmField
        var groupid: Int = 0,
        @JvmField
        var honor: String? = null,
        @JvmField
        var medal: List<String>? = null,
        @JvmField
        var member: String? = null,
        @JvmField
        var memberid: Int = 0,
        @JvmField
        var money: Int = 0,
        @JvmField
        var mute_status: Int = 0,
        @JvmField
        var mute_time: Int = 0,
        @JvmField
        var nickname: String? = null,
        @JvmField
        var postnum: Int = 0,
        @JvmField
        var regdate: Int = 0,
        @JvmField
        var reputation: String? = null,
        @JvmField
        var rvrc: String? = null,
        @JvmField
        var signature: String? = null,
        @JvmField
        var site: String? = null,
        @JvmField
        var thisvisit: Int = 0,
        @JvmField
        var uid: Int = 0,
        @JvmField
        var username: String? = null,
        @JvmField
        var annoy: String? = null,
        @JvmField
        var yz: Int = 0,
    ) : JavaBean

    data class Result(
        @JvmField
        var alterinfo: String?,
        @JvmField
        var attches: String?,
        @JvmField
        var author: Author? = null,
        @JvmField
        var comment_to_id: String?,
        @JvmField
        var content: String = "",
        @JvmField
        var fid: Int = 0,
        @JvmField
        var follow: Int = 0,
        @JvmField
        var from_client: String = "",
        @JvmField
        var isTieTiao: Boolean = false,
        @JvmField
        var is_user_quote: Int = 0,
        @JvmField
        var lou: Int = 0,
        @JvmField
        var pid: Int = 0,
        @JvmField
        var postdate: String?,
        @JvmField
        var postdatetimestamp: Int = 0,
        @JvmField
        var subject: String?,
        @JvmField
        var tid: Int = 0,
        @JvmField
        var type: Int = 0,
        @JvmField
        var vote: String?,
        @JvmField
        var vote_bad: Int = 0,
        @JvmField
        var vote_good: Int = 0,
    ) : JavaBean
}


