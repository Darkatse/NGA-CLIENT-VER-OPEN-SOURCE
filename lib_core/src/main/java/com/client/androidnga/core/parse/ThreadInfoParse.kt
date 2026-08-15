package com.client.androidnga.core.parse

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import com.client.androidnga.core.data.bean.ThreadBean
import com.client.androidnga.core.data.bean.ThreadPostBean
import com.client.androidnga.core.data.bean.ThreadUserBean
import com.client.androidnga.core.data.model.ClientModel
import com.client.androidnga.core.data.model.ThreadBasicInfo
import com.client.androidnga.core.data.model.ThreadInfo
import com.client.androidnga.core.data.model.ThreadPageInfo
import com.client.androidnga.core.data.model.ThreadPostInfo

object ThreadInfoParse {

    fun parseThreadInfo(jsStr: String, bean: ThreadBean?): ThreadInfo {

        val threadBean = bean ?: parserThreadBean(jsStr)

        val threadInfo = ThreadInfo()
        threadInfo.rawData = jsStr
        threadInfo.basicInfo = parseBasicInfo(threadBean?.__GLOBAL)
        threadInfo.pageInfo = threadBean?.__T
        return threadInfo
    }

    private fun parseBasicInfo(globalStr: String?): ThreadBasicInfo? {
        if (globalStr == null) {
            return null
        }
        val global = JSONObject.parseObject(globalStr)
        val basicInfo = ThreadBasicInfo()
        val data = global.getString("_ATTACH_BASE_VIEW")
        if (!data.isNullOrEmpty()) {
            basicInfo.attachHost =
                data.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        }
        return basicInfo
    }


    fun parserThreadBean(jsStr: String): ThreadBean? {
        var js = jsStr
        if (js.isEmpty()) {
            return null
        } else if (js.contains("/*error fill content")) {
            js = js.substringBefore("/*error fill content")
        }

        js = js.replace("/\\*\\\$js\\$\\*/".toRegex(), "")
            .replace("\"content\":\\+(\\d+),".toRegex(), "\"content\":\"+$1\",")
            .replace("\"subject\":\\+(\\d+),".toRegex(), "\"subject\":\"+$1\",")
            .replace("\"content\":(0\\d+),".toRegex(), "\"content\":\"$1\",")
            .replace("\"subject\":(0\\d+),".toRegex(), "\"subject\":\"$1\",")
            .replace("\"author\":(0\\d+),".toRegex(), "\"author\":\"$1\",")
            .replace("\"alterinfo\":\"\\[(\\w|\\s)+]\\s+\",".toRegex(), "") //部分页面打不开的问题

        val objStr = JSON.parseObject(js).getString("data")
        return JSON.parseObject(objStr, ThreadBean::class.java)
    }

    fun parseUserInfo(
        threadBean: ThreadBean,
        postInfo: ThreadPostInfo,
    ) {
        val userBean = JSON.parseObject(
            threadBean.__U[postInfo.authorId.toString()],
            ThreadUserBean::class.java
        )
        if (userBean == null) {
            return
        }
        parseUserState(postInfo, userBean, threadBean.__T)

        postInfo.apply {
            avatarUrl = ForumParsekUtils.parseAvatarUrl(userBean.avatar)
            val groupObj = JSON.parseObject(threadBean.__U["__GROUPS"])
            memberGroup = groupObj.getJSONObject(userBean.memberid).getString("0")
            postCount = userBean.postnum;
            reputation = userBean.rvrc?.toFloatOrNull()?.div(10.0f) ?: 0f
            author = if (isAnonymous) {
                parseAnonymousName(userBean.username)
            } else {
                userBean.username
            }
            signature = userBean.signature
        }
    }

    fun parseUserState(
        postInfo: ThreadPostInfo,
        userBean: ThreadUserBean,
        pageInfo: ThreadPageInfo?,
    ) {
        val buffs = userBean.buffs
        val yz = userBean.yz
        val mutedTime = userBean.mute_time
        if ("-1" == yz) {
            postInfo.isNuked = true
        } else if (buffs.containsKey("117") || buffs.containsKey("105") || !mutedTime.isNullOrEmpty() && "0" != mutedTime) {
            postInfo.isMuted = true
        }

        val userName = userBean.nickname
        if (userName.length == 39 && userName.startsWith("#anony_")) {
            postInfo.isAnonymous = true
        }

        if (pageInfo?.authorid == userBean.uid) {
            postInfo.isThreadAuthor = true
        }

    }

    fun parseClientModel(postBean: ThreadPostBean): ClientModel? {
        return postBean.from_client?.let {
            if (it.trim().isEmpty()) {
                return null
            }
            val clientAppCode = if (it.contains(" ")) {
                it.substring(0, it.indexOf(' '))
            } else {
                it
            }
            when (clientAppCode) {
                "1", "7" -> {
                    return ClientModel.IOS
                }

                "101" -> {
                    return ClientModel.IOS_BROWSER
                }

                "8" -> {
                    return ClientModel.ANDROID
                }

                "9", "103" -> {
                    return ClientModel.WP
                }

                "100" -> {
                    return ClientModel.ANDROID_BROWSER
                }

                else -> {
                    return ClientModel.UNKNOWN_BROWSER
                }
            }
        }
    }

    fun parseAnonymousName(userName: String): String {
        val builder = StringBuilder()
        val t1 = "甲乙丙丁戊己庚辛壬癸子丑寅卯辰巳午未申酉戌亥"
        val t2 =
            "王李张刘陈杨黄吴赵周徐孙马朱胡林郭何高罗郑梁谢宋唐许邓冯韩曹曾彭萧蔡潘田董袁于余叶蒋杜苏魏程吕丁沈任姚卢傅钟姜崔谭廖范汪陆金石戴贾韦夏邱方侯邹熊孟秦白江阎薛尹段雷黎史龙陶贺顾毛郝龚邵万钱严赖覃洪武莫孔汤向常温康施文牛樊葛邢安齐易乔伍庞颜倪庄聂章鲁岳翟殷詹申欧耿关兰焦俞左柳甘祝包宁尚符舒阮柯纪梅童凌毕单季裴霍涂成苗谷盛曲翁冉骆蓝路游辛靳管柴蒙鲍华喻祁蒲房滕屈饶解牟艾尤阳时穆农司卓古吉缪简车项连芦麦褚娄窦戚岑景党宫费卜冷晏席卫米柏宗瞿桂全佟应臧闵苟邬边卞姬师和仇栾隋商刁沙荣巫寇桑郎甄丛仲虞敖巩明佘池查麻苑迟邝 "
        var i = 6
        for (j in 0..5) {
            var pos: Int
            if (j == 0 || j == 3) {
                pos = userName.substring(i + 1, i + 2).toInt(16)
                builder.append(t1.get(pos))
            } else {
                pos = userName.substring(i, i + 2).toInt(16)
                builder.append(t2.get(pos))
            }
            i += 2
        }
        return builder.toString()
    }

    fun parseThreadPostInfo(row: ThreadPostBean): ThreadPostInfo {
        return ThreadPostInfo().apply {
            tid = row.tid
            pid = row.pid
            fid = row.fid
            authorId = row.authorid
            clientModel = parseClientModel(row)
            lou = row.lou
            vote = row.vote
            subject = row.subject
            postDate = row.postdate
            alterInfo = row.alterinfo
            rawContent = row.content ?: row.subject
            score = row.score
        }


    }

}