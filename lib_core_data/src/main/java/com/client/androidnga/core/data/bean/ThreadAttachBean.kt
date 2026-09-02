package com.client.androidnga.core.data.bean

import gov.anzong.androidnga.common.base.JavaBean

class ThreadAttachBean : JavaBean {
    var aid: String? = null
    var url_utf8_org_name: String? = null
    var dscp: String? = null
    var size: Int = 0
    var ext: String? = null
    var name: String? = null
    @JvmField
    var thumb: String? = null
    @JvmField
    var attachurl: String? = null
    var type: String? = null
    var subid: Int = 0
}