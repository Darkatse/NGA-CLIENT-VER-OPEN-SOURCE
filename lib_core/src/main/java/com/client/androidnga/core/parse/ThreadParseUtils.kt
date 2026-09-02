package com.client.androidnga.core.parse

import com.client.androidnga.core.data.model.ClientModel

object ThreadParseUtils {

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
                builder.append(t1[pos])
            } else {
                pos = userName.substring(i, i + 2).toInt(16)
                builder.append(t2[pos])
            }
            i += 2
        }
        return builder.toString()
    }

    fun parseClientModel(client: String?): ClientModel? {
        return client?.let {
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

}