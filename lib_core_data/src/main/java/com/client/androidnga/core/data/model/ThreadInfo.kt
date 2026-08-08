package com.client.androidnga.core.data.model

import com.client.androidnga.core.data.bean.ThreadPostBean
import gov.anzong.androidnga.common.base.JavaBean

class ThreadInfo : JavaBean {
    @JvmField
    var rowList: List<ThreadPostBean>? = null
    @JvmField
    var threadInfo: ThreadPageInfo? = null
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
