package gov.anzong.androidnga.core

interface IHtmlConfigService {

    companion object {
        const val ROUTER_PATH = "/service/html"
    }

    fun isBlocked(uid: String): Boolean

    fun isShowSignature(): Boolean

    fun getEmoticonSize(): Int

    fun isImageEnabled(): Boolean

    fun getNGAHost(): String

    fun isDarkMode(): Boolean
}