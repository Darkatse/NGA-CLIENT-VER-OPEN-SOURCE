package sp.phone.mvp.model.convert;

import android.text.TextUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.client.androidnga.core.data.bean.ThreadAttachBean;
import com.client.androidnga.core.data.bean.ThreadBean;
import com.client.androidnga.core.data.bean.ThreadPostBean;
import com.client.androidnga.core.data.bean.ThreadUserBean;
import com.client.androidnga.core.data.model.ThreadInfo;
import com.client.androidnga.core.parse.ThreadInfoParse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.base.logger.Logger;
import gov.anzong.androidnga.common.util.NLog;
import gov.anzong.androidnga.core.HtmlConvertFactory;
import gov.anzong.androidnga.core.data.AttachmentData;
import gov.anzong.androidnga.core.data.CommentData;
import gov.anzong.androidnga.core.data.HtmlData;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.UserManagerImpl;
import sp.phone.theme.ThemeManager;

/**
 * Created by Justwen on 2017/12/3.
 */

public class ArticleConvertFactory {

    private static final String TAG = ArticleConvertFactory.class.getSimpleName();

    public static ThreadInfo getArticleInfo(String js) {
        return parseJsonThreadPage(js);
    }

    private static ThreadInfo parseJsonThreadPage(String js) {
        ThreadInfo data = null;
        try {
            ThreadBean threadBean = ThreadInfoParse.INSTANCE.parserThreadBean(js);
            if (threadBean == null) {
                return null;
            }

            int allRows = threadBean.__ROWS;
            data = new ThreadInfo();
            data.setRawData(js);
            data.threadInfo = threadBean.__T;
            data.rowList = buildThreadRowList(threadBean);
            data.set__ROWS(allRows);
            data.rowNum = data.rowList.size();
        } catch (Exception e) {
            NLog.e(TAG, "can not parse :\n" + js);
            Logger.d(e);
        }
        return data;
    }

    private static List<ThreadPostBean> buildThreadRowList(ThreadBean threadBean) {
        JSONObject attachGlobal = JSONObject.parseObject(threadBean.__GLOBAL);
        return convertJsObjToList(threadBean, threadBean.__R, threadBean.__R__ROWS, attachGlobal);
    }


    private static List<ThreadPostBean> convertJsObjToList(ThreadBean threadBean, Map<String, ThreadPostBean> rowMap, int count, JSONObject global) {
        List<ThreadPostBean> rowList = new ArrayList<>();
        NLog.d("ArticleUtil", "convertJsObjToList");
        for (int i = 0; i < count; i++) {
            ThreadPostBean row = rowMap.get(String.valueOf(i));
            if (row == null) {
                continue;
            }
            row.attachmentHost = getAttachmentHost(global);
            buildRowComment(threadBean, row, global);
            buildRowClientInfo(row);
            buildRowUserInfo(threadBean, row);
            buildRowContent(row);
            rowList.add(row);
        }
        return rowList;
    }

    private static String getAttachmentHost(JSONObject global) {
        String data = global.getString("_ATTACH_BASE_VIEW");
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        return data.split("/")[0];
    }

    private static void buildRowContent(ThreadPostBean row) {
        if (row.content == null) {
            row.content = row.subject;
            row.subject = null;
        }
        List<String> imageUrls = new ArrayList<>();
        String ngaHtml = HtmlConvertFactory.convert(buildHtmlData(row), imageUrls);
        row.getImageUrls().addAll(imageUrls);
        row.threadPostInfo.formatHtml = ngaHtml;
    }

    private static HtmlData buildHtmlData(ThreadPostBean row) {
        HtmlData htmlData = new HtmlData(row.content);
        htmlData.attachmentHost = row.attachmentHost;
        htmlData.setAlertInfo(row.alterinfo);
        htmlData.setDarkMode(ThemeManager.getInstance().isNightMode());
        htmlData.setInBackList(row.threadPostInfo.isBlocked);
        htmlData.setTextSize(PhoneConfiguration.getInstance().getTopicContentSize());
        htmlData.setEmotionSize(PhoneConfiguration.getInstance().getEmoticonSize());
        htmlData.setSignature(PhoneConfiguration.getInstance().isShowSignature() ? row.signature : null);
        htmlData.setVote(row.vote);
        htmlData.setSubject(row.subject);
        htmlData.setShowImage(PhoneConfiguration.getInstance().isImageLoadEnabled());
        htmlData.setNGAHost(Utils.getNGAHost());
        htmlData.pid = String.valueOf(row.pid);
        htmlData.tid = String.valueOf(row.tid);
        htmlData.uid = String.valueOf(row.authorid);
        if (row.attachs != null) {
            List<AttachmentData> attachments = new ArrayList<>();
            for (Map.Entry<String, ThreadAttachBean> entry : row.attachs.entrySet()) {
                AttachmentData data = new AttachmentData();
                data.setAttachUrl(entry.getValue().attachurl);
                data.setThumb(entry.getValue().thumb);
                data.setAttachmentHost(row.attachmentHost);
                attachments.add(data);
            }
            htmlData.setAttachmentList(attachments);
        }

        if (row.comments != null) {
            List<CommentData> comments = new ArrayList<>();
            for (ThreadPostBean value : row.comments) {
                CommentData comment = new CommentData();
                comment.setAuthor(value.author);
                comment.setContent(value.content);
                comment.setPostTime(value.postdate);
                comment.setAvatarUrl(row.threadPostInfo.avatarUrl);
                comments.add(comment);
            }
            htmlData.setCommentList(comments);
        }
        return htmlData;
    }

    //解析贴条
    private static void buildRowComment(ThreadBean threadBean, ThreadPostBean row, JSONObject global) {
        row.comments = convertJsObjToList(threadBean, row.comment, row.comment.size(), global);
    }

    private static void buildRowClientInfo(ThreadPostBean row) {
        row.threadPostInfo.clientModel = ThreadInfoParse.INSTANCE.parseClientModel(row);
    }

    private static void buildRowUserInfo(ThreadBean threadBean, ThreadPostBean row) {
        if (row.authorid == 0) {
            return;
        }
        ThreadUserBean userBean = JSON.parseObject(threadBean.__U.get(String.valueOf(row.authorid)), ThreadUserBean.class);

        if (userBean == null) {
            return;
        }
        int uid = row.authorid;
        row.threadPostInfo.isBlocked = UserManagerImpl.getInstance().checkBlackList(String.valueOf(uid));

        ThreadInfoParse.INSTANCE.parseUserInfo(threadBean, row.threadPostInfo, userBean);

        if (row.threadPostInfo.isAnonymous) {
            row.author = ThreadInfoParse.INSTANCE.parseAnonymousName(userBean.username);
        } else {
            row.author = userBean.username;
        }
        row.signature = userBean.signature;

    }

}

