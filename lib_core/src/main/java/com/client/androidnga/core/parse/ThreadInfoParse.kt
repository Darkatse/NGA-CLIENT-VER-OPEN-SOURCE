package com.client.androidnga.core.parse

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import com.client.androidnga.core.data.bean.ThreadBean
import com.client.androidnga.core.data.bean.ThreadPageBean
import com.client.androidnga.core.data.bean.ThreadPostBean
import com.client.androidnga.core.data.bean.ThreadUserBean
import com.client.androidnga.core.data.html.AttachmentData
import com.client.androidnga.core.data.model.ThreadBasicInfo
import com.client.androidnga.core.data.model.ThreadInfo
import com.client.androidnga.core.data.model.ThreadPageInfo
import com.client.androidnga.core.data.model.ThreadPostInfo
import gov.anzong.androidnga.core.HtmlConvertFactory
import gov.anzong.androidnga.core.IHtmlConfigService

class ThreadInfoParse() {

    companion object {
        @JvmStatic
        fun parse(rawData: String, config: IHtmlConfigService?): ThreadInfo? {
            return ThreadInfoParse().parseThreadInfo(rawData, config = config)
        }
    }

    var threadBean: ThreadBean? = null

    val userMap = mutableMapOf<String, ThreadUserBean>()

    var threadInfo: ThreadInfo? = null

    var parseConfig: IHtmlConfigService? = null

    fun parseThreadInfo(
        jsStr: String,
        bean: ThreadBean? = null,
        config: IHtmlConfigService? = null
    ): ThreadInfo? {
        threadBean = bean ?: parserThreadBean(jsStr)
        if (threadBean == null) {
            return null
        }
        parseConfig = config

        threadInfo = ThreadInfo().apply {
            rawData = jsStr
            basicInfo = parseBasicInfo(threadBean?.__GLOBAL)
            pageInfo = parsePageInfo(threadBean?.__T)
            totalRows = threadBean?.__ROWS ?: 0
            postInfoList =
                parsePostInfoList(
                    threadBean!!.__R,
                    basicInfo,
                    threadBean!!.__ROWS,
                )
        }
        return threadInfo
    }

    private fun getUserBean(authorId: String): ThreadUserBean {
        var userBean = userMap[authorId]
        if (userBean == null) {
            userBean = JSON.parseObject(
                threadBean!!.__U[authorId],
                ThreadUserBean::class.java
            )
            userMap[authorId] = userBean
        }
        return userBean
    }

    private fun parsePostInfoList(
        postMap: Map<String, ThreadPostBean>,
        basicInfo: ThreadBasicInfo?,
        count: Int = 0,
    ): List<ThreadPostInfo> {
        val postInfoList = mutableListOf<ThreadPostInfo>()
        for (i in 0..<count) {
            val row: ThreadPostBean = postMap[i.toString()] ?: continue
            val authorId = row.authorid
            val userBean = getUserBean(authorId.toString())
            val postInfo = parseThreadPostInfo(row, userBean)
            if (row.comment.isNotEmpty()) {
                postInfo.comments.addAll(parseCommentInfo(row, basicInfo))
            }
            postInfo.isBlocked = parseConfig?.isBlocked(authorId.toString()) ?: false
            postInfo.formatHtml = HtmlConvertFactory.convert(
                postInfo,
                basicInfo,
                parseConfig
            )
            postInfo.attachInfo = parsePostAttachInfo(row)
            postInfo.isComment = isComment(postInfo, row)
            postInfoList.add(postInfo)
        }
        return postInfoList
    }

    private fun parsePostAttachInfo(postBean: ThreadPostBean): List<AttachmentData> {
        if (postBean.attachs != null) {
            val attachments: MutableList<AttachmentData> = ArrayList()
            for (entry in postBean.attachs!!.entries) {
                val data = AttachmentData()
                data.attachUrl = entry.value.attachurl
                data.thumb = entry.value.thumb
                data.attachmentHost = threadInfo?.basicInfo?.attachHost
                attachments.add(data)
            }
            return attachments
        } else {
            return emptyList()
        }
    }

    private fun isComment(row: ThreadPostInfo, bean: ThreadPostBean): Boolean {
        return row.alterInfo.isNullOrEmpty()
                && row.attachInfo.isEmpty()
                && row.comments.isEmpty()
                && row.fid == 0
                && bean.level == null
                && row.signature.isNullOrEmpty()
    }

    private fun parseCommentInfo(
        postBean: ThreadPostBean,
        basicInfo: ThreadBasicInfo?,
    ): List<ThreadPostInfo> {
        val comments =
            parsePostInfoList(postBean.comment, basicInfo, postBean.comment.size)
        return comments
    }

    private fun parsePageInfo(pageBean: ThreadPageBean?): ThreadPageInfo? {
        if (pageBean == null) {
            return null
        }
        return ThreadPageInfo().apply {
            tid = pageBean.tid
            fid = pageBean.fid
            authorId = pageBean.authorid
            subject = pageBean.subject
        }
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
        postInfo: ThreadPostInfo,
        userBean: ThreadUserBean,
    ) {
        val buffs = userBean.buffs
        val yz = userBean.yz
        val mutedTime = userBean.mute_time
        if ("-1" == yz) {
            postInfo.isNuked = true
        } else if (buffs.containsKey("117") || buffs.containsKey("105") || !mutedTime.isNullOrEmpty() && "0" != mutedTime) {
            postInfo.isMuted = true
        }

        val userName = userBean.username
        if (userName.length == 39 && userName.startsWith("#anony_")) {
            postInfo.isAnonymous = true
        }

        if (threadBean!!.__T?.authorid == userBean.uid) {
            postInfo.isThreadAuthor = true
        }
        postInfo.apply {
            avatarUrl = ForumParsekUtils.parseAvatarUrl(userBean.avatar)
            val groupObj = JSON.parseObject(threadBean!!.__U["__GROUPS"])
            memberGroup = groupObj.getJSONObject(userBean.memberid).getString("0")
            postCount = userBean.postnum;
            reputation = userBean.rvrc?.toFloatOrNull()?.div(10.0f) ?: 0f
            author = if (isAnonymous) {
                ThreadParseUtils.parseAnonymousName(userBean.username)
            } else {
                userBean.username
            }
            signature = userBean.signature
        }

    }


    fun parseThreadPostInfo(row: ThreadPostBean, userBean: ThreadUserBean): ThreadPostInfo {
        val postInfo = ThreadPostInfo().apply {
            tid = row.tid
            pid = row.pid
            fid = row.fid
            authorId = row.authorid
            clientModel = ThreadParseUtils.parseClientModel(row.from_client)
            lou = row.lou
            vote = row.vote
            postDate = row.postdate
            alterInfo = row.alterinfo
            if (row.content.isNullOrEmpty()) {
                rawContent = row.subject
            } else {
                rawContent = row.content
                subject = row.subject
            }
            score = row.score
        }
        parseUserInfo(postInfo, userBean)
        return postInfo
    }

}