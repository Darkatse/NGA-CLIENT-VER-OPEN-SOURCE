package com.client.androidnga.core.data.bean

import gov.anzong.androidnga.common.base.JavaBean

data class ThreadPageBean(
    @JvmField
    val author: String?,
    @JvmField
    val authorid: Int,
    @JvmField
    val digest: Int,
    @JvmField
    val fid: Int,
    @JvmField
    val jdata: String?,
    @JvmField
    val lastmodify: Int,
    @JvmField
    val lastpost: Int,
    @JvmField
    val lastposter: String?,
    @JvmField
    val locked: Int,
    @JvmField
    val post_misc_var: String?,
    @JvmField
    val postdate: Int,
    @JvmField
    val quote_from: Int,
    @JvmField
    val quote_to: String?,
    @JvmField
    val recommend: Int,
    @JvmField
    val replies: Int,
    @JvmField
    val subject: String?,
    @JvmField
    val this_visit_rows: Int,
    @JvmField
    val tid: Int,
    @JvmField
    val topic_misc: String?,
    @JvmField
    val tpid: Int,
    @JvmField
    val type: Int
) : JavaBean