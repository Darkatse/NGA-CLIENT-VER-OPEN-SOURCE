package com.client.androidnga.core.data.bean

import com.client.androidnga.core.data.model.ThreadPageInfo
import gov.anzong.androidnga.common.base.JavaBean

data class ThreadBean(
    @JvmField
    var __GLOBAL: String? = null,
    @JvmField
    var __T: ThreadPageInfo? = null,
    @JvmField
    var __U: Map<String, String> = emptyMap(),
    @JvmField
    var __R: Map<String, ThreadPostBean> = emptyMap(),
    @JvmField
    var __R__ROWS: Int = 0,
    @JvmField
    var __R__ROWS_PAGE: Int = 0,
    @JvmField
    var __ROWS: Int = 0,
    @JvmField
    var __PAGE: Int = 0,
) : JavaBean

data class ThreadUserBean(
    @JvmField
    var avatar: String? = null,
    @JvmField
    var bit_data: Int? = null,
    @JvmField
    var buffs: Map<String, String> = emptyMap(),
    @JvmField
    var credit: Int? = null,
    @JvmField
    var groupid: Int? = null,
    @JvmField
    var honor: String? = null,
    @JvmField
    var medal: String? = null,
    @JvmField
    var memberid: String? = null,
    @JvmField
    var money: Int? = null,
    @JvmField
    var mute_time: String? = null,
    @JvmField
    var nickname: String = "",
    @JvmField
    var postnum: String? = null,
    @JvmField
    var regdate: Int? = null,
    @JvmField
    var reputation: String? = null,
    @JvmField
    var rvrc: String? = null,
    @JvmField
    var signature: String? = null,
    @JvmField
    var site: String? = null,
    @JvmField
    var thisvisit: Int? = null,
    @JvmField
    var uid: Int? = null,
    @JvmField
    var username: String = "",
    @JvmField
    var yz: String? = null,
) : JavaBean
