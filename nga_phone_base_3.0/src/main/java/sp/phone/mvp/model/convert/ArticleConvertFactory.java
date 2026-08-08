package sp.phone.mvp.model.convert;

import android.text.TextUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.base.logger.Logger;
import gov.anzong.androidnga.core.HtmlConvertFactory;
import gov.anzong.androidnga.common.util.NLog;
import gov.anzong.androidnga.core.data.AttachmentData;
import gov.anzong.androidnga.core.data.CommentData;
import gov.anzong.androidnga.core.data.HtmlData;
import sp.phone.common.ForumConstants;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.UserManagerImpl;
import com.client.androidnga.core.data.bean.ThreadAttachBean;
import com.client.androidnga.core.data.bean.ThreadUserBean;
import com.client.androidnga.core.data.model.ClientModel;
import com.client.androidnga.core.data.model.ThreadInfo;
import com.client.androidnga.core.data.bean.ThreadPostBean;
import com.client.androidnga.core.data.model.ThreadPageInfo;
import com.client.androidnga.core.parse.ThreadInfoParse;

import sp.phone.theme.ThemeManager;
import sp.phone.util.FunctionUtils;
import sp.phone.util.StringUtils;

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
            if (js.isEmpty()) {
                return null;
            } else if (js.contains("/*error fill content")) {
                js = js.substring(0, js.indexOf("/*error fill content"));
            }

            js = js.replaceAll("/\\*\\$js\\$\\*/", "")
                    .replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",")
                    .replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",")
                    .replaceAll("\"content\":(0\\d+),", "\"content\":\"$1\",")
                    .replaceAll("\"subject\":(0\\d+),", "\"subject\":\"$1\",")
                    .replaceAll("\"author\":(0\\d+),", "\"author\":\"$1\",")
                    .replaceAll("\"alterinfo\":\"\\[(\\w|\\s)+\\]\\s+\",", ""); //部分页面打不开的问题
//            NLog.e(js);
            JSONObject obj = (JSONObject) JSON.parseObject(js).get("data");
            NLog.d(TAG, "js = :\n" + js);
            if (obj == null) {
                return null;
            }
            int allRows = (Integer) obj.get("__ROWS");
            data = new ThreadInfo();
            data.setRawData(js);
            data.threadInfo = buildThreadPageInfo(obj);
            data.rowList = buildThreadRowList(obj);
            data.set__ROWS(allRows);
            data.rowNum = data.rowList.size();
        } catch (Exception e) {
            NLog.e(TAG, "can not parse :\n" + js);
            Logger.d(e);
        }
        return data;
    }

    private static ThreadPageInfo buildThreadPageInfo(JSONObject obj) {
        JSONObject subObj = (JSONObject) obj.get("__T");
        if (subObj == null) {
            return null;
        }
        try {
            return subObj.toJavaObject(ThreadPageInfo.class);
        } catch (RuntimeException e) {
            NLog.e(TAG, subObj.toJSONString());
        }
        return null;
    }

    private static List<ThreadPostBean> buildThreadRowList(JSONObject obj) {
        JSONObject subObj = (JSONObject) obj.get("__R");
        int rows = (Integer) obj.get("__R__ROWS");
        JSONObject userInfoMap = (JSONObject) obj.get("__U");
        JSONObject attachGlobal = obj.getJSONObject("__GLOBAL");
        if (subObj == null) {
            return new ArrayList<>();
        }
        return convertJsObjToList(subObj, rows, userInfoMap, attachGlobal);
    }


    private static List<ThreadPostBean> convertJsObjToList(JSONObject rowMap, int count, JSONObject userInfoMap, JSONObject global) {
        List<ThreadPostBean> rowList = new ArrayList<>();
        NLog.d("ArticleUtil", "convertJsObjToList");
        for (int i = 0; i < count; i++) {
            Object obj = rowMap.get(String.valueOf(i));
            JSONObject rowObj;
            if (obj instanceof JSONObject) {
                rowObj = (JSONObject) obj;
            } else {
                continue;
            }
            ThreadPostBean row = rowObj.toJavaObject(ThreadPostBean.class);
            row.attachmentHost = getAttachmentHost(global);
            buildRowHotReplay(row, rowObj);
            buildRowComment(row, rowObj, userInfoMap, global);
            buildRowClientInfo(row, rowObj);
            buildRowUserInfo(row, userInfoMap);
            buildRowVote(row, rowObj);
            buildRowContent(row);
            rowList.add(row);
        }
        return rowList;
    }

    private static String getAttachmentHost(JSONObject global) {
        String data =  global.getString("_ATTACH_BASE_VIEW");
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
        if (!StringUtils.isEmpty(row.from_client)
                && row.from_client.startsWith("103 ")
                && !StringUtils.isEmpty(row.content)) {
            row.content = StringUtils.unescape(row.content);
        }
        List<String> imageUrls = new ArrayList<>();
        String ngaHtml = HtmlConvertFactory.convert(buildHtmlData(row), imageUrls);
        row.getImageUrls().addAll(imageUrls);
        row.threadPostInfo.formatHtml= ngaHtml;
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
                comment.setAvatarUrl(FunctionUtils.parseAvatarUrl(value.js_escap_avatar));
                comments.add(comment);
            }
            htmlData.setCommentList(comments);
        }
        return htmlData;
    }

    private static void buildRowVote(ThreadPostBean row, JSONObject rowObj) {
        String vote = rowObj.getString("vote");
        if (!StringUtils.isEmpty(vote)) {
            row.vote = vote;
        }
    }

    //热门回复
    private static void buildRowHotReplay(ThreadPostBean row, JSONObject rowObj) {
        String hotObj = rowObj.getString("17");
        if (hotObj != null) {
            row.hotReplies = new ArrayList<>();
            String[] hots = hotObj.split(",");
            for (String hot : hots) {
                if (!TextUtils.isEmpty(hot)) {
                    row.hotReplies.add(hot);
                }
            }
        }
    }

    //解析贴条
    private static void buildRowComment(ThreadPostBean row, JSONObject rowObj, JSONObject userInfoMap, JSONObject global) {
        JSONObject commObj = (JSONObject) rowObj.get("comment");
        if (commObj != null) {
            row.comments = convertJsObjToList(commObj, commObj.size(), userInfoMap, global);
        }
    }

    private static void buildRowClientInfo(ThreadPostBean row, JSONObject rowObj) {
        row.threadPostInfo.clientModel = ThreadInfoParse.INSTANCE.parseClientModel(row);
    }

    private static void buildRowUserInfo(ThreadPostBean row, JSONObject userInfoMap) {
        if (row.authorid == 0) {
            return;
        }
        JSONObject userInfo = (JSONObject) userInfoMap.get(String.valueOf(row.authorid));
        JSONObject groupObj = userInfoMap.getJSONObject("__GROUPS");

        if (userInfo == null) {
            return;
        }
        ThreadUserBean userBean = userInfo.toJavaObject(ThreadUserBean.class);
        int uid = row.authorid;
        row.threadPostInfo.isBlocked = UserManagerImpl.getInstance().checkBlackList(String.valueOf(uid));
        if (userBean.username.length() == 39
                && userBean.username.startsWith("#anony_")) {
            row.author = ThreadInfoParse.INSTANCE.parseAnonymousName(userBean.username);
            row.threadPostInfo.isAnonymous = true;
        } else {
            row.author = userBean.username;
        }
        row.js_escap_avatar = userBean.avatar;
        row.yz = userBean.yz;
        row.threadPostInfo.muteTime = userBean.mute_time;
        row.signature = userBean.signature;
        row.threadPostInfo.postCount = userBean.postnum;

        try {
            if (userBean.rvrc != null) {
                row.threadPostInfo.reputation = Float.parseFloat(userBean.rvrc) / 10.0f;
            }
            row.threadPostInfo.memberGroup = groupObj.getJSONObject(userBean.memberid).getString("0");
        } catch (Exception ignore) {
        }

        var obj = userBean.buffs;
        for (String id : ForumConstants.BUFF_MUTE_IDS) {
            if (obj.containsKey(id)) {
                row.threadPostInfo.isMuted = true;
                break;
            }
        }
    }

}

