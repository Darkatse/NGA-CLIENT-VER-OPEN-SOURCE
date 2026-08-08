package com.client.androidnga.core.data.bean

import gov.anzong.androidnga.common.base.JavaBean

class ThreadBean {
}

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
    var nickname: String? = null,
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
