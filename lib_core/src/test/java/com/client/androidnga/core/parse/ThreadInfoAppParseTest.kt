package com.client.androidnga.core.parse

import org.junit.Test

class ThreadInfoAppParseTest {

    @Test
    fun parse() {
        val rawDataWithAppApi  = ClassLoader.getSystemResource("tem.json").readText()
        val threadInfoWithAppApi = ThreadInfoAppParse.parse(rawDataWithAppApi)
//        val rawData = ClassLoader.getSystemResource("thread_info_app.json").readText()
//        val threadInfo = ThreadInfoParse.parse(rawData, null)
        print(threadInfoWithAppApi)
    }


}