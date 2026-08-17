package gov.anzong.androidnga.service

import gov.anzong.androidnga.Utils
import gov.anzong.androidnga.core.IHtmlConfigService
import sp.phone.common.PhoneConfiguration
import sp.phone.common.UserManagerImpl
import sp.phone.theme.ThemeManager

class HtmlConfigService : IHtmlConfigService {

    override fun isBlocked(uid: String): Boolean {
        return UserManagerImpl.getInstance().checkBlackList(uid);
    }

    override fun isShowSignature(): Boolean {
        return PhoneConfiguration.getInstance().isShowSignature
    }


    override fun getEmoticonSize(): Int {
        return PhoneConfiguration.getInstance().emoticonSize;
    }

    override fun isImageEnabled(): Boolean {
        return PhoneConfiguration.getInstance().isImageLoadEnabled;
    }

    override fun getNGAHost(): String {
        return Utils.getNGAHost();
    }

    override fun isDarkMode(): Boolean {
        return ThemeManager.getInstance().isNightMode;
    }

}