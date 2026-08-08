package sp.phone.mvp.model.convert;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

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
import com.client.androidnga.core.data.model.ThreadInfo;
import com.client.androidnga.core.data.bean.ThreadPostBean;
import com.client.androidnga.core.data.model.ThreadPageInfo;
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
            return JSONObject.toJavaObject(subObj, ThreadPageInfo.class);
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
            ThreadPostBean row = JSONObject.toJavaObject(rowObj, ThreadPostBean.class);
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
        if (!StringUtils.isEmpty(row.getFromClient())
                && row.getFromClient().startsWith("103 ")
                && !StringUtils.isEmpty(row.content)) {
            row.content = StringUtils.unescape(row.content);
        }
        List<String> imageUrls = new ArrayList<>();
        String ngaHtml = HtmlConvertFactory.convert(buildHtmlData(row), imageUrls);
        row.getImageUrls().addAll(imageUrls);
        row.setFormattedHtmlData(ngaHtml);
    }

    private static HtmlData buildHtmlData(ThreadPostBean row) {
        HtmlData htmlData = new HtmlData(row.content);
        htmlData.attachmentHost = row.attachmentHost;
        htmlData.setAlertInfo(row.alterinfo);
        htmlData.setDarkMode(ThemeManager.getInstance().isNightMode());
        htmlData.setInBackList(row.get_isInBlackList());
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
        String client = rowObj.getString("from_client");
        if (!StringUtils.isEmpty(client)) {
            row.setFromClient(client);
            if (!client.trim().equals("")) {
                String clientAppCode;
                if (client.contains(" ")) {
                    clientAppCode = client.substring(0, client.indexOf(' '));
                } else {
                    clientAppCode = client;
                }
                if (clientAppCode.equals("1") || clientAppCode.equals("7") || clientAppCode.equals("101")) {
                    row.setFromClientModel("ios");
                } else if (clientAppCode.equals("103") || clientAppCode.equals("9")) {
                    row.setFromClientModel("wp");
                } else if (!clientAppCode.equals("8") && !clientAppCode.equals("100")) {
                    row.setFromClientModel("unknown");
                } else {
                    row.setFromClientModel("android");
                }
            }
        }
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
        int uid = row.authorid;
        row.set_IsInBlackList(UserManagerImpl.getInstance().checkBlackList(String.valueOf(uid)));
        String t1 = "甲乙丙丁戊己庚辛壬癸子丑寅卯辰巳午未申酉戌亥";
        String t2 = "王李张刘陈杨黄吴赵周徐孙马朱胡林郭何高罗郑梁谢宋唐许邓冯韩曹曾彭萧蔡潘田董袁于余叶蒋杜苏魏程吕丁沈任姚卢傅钟姜崔谭廖范汪陆金石戴贾韦夏邱方侯邹熊孟秦白江阎薛尹段雷黎史龙陶贺顾毛郝龚邵万钱严赖覃洪武莫孔汤向常温康施文牛樊葛邢安齐易乔伍庞颜倪庄聂章鲁岳翟殷詹申欧耿关兰焦俞左柳甘祝包宁尚符舒阮柯纪梅童凌毕单季裴霍涂成苗谷盛曲翁冉骆蓝路游辛靳管柴蒙鲍华喻祁蒲房滕屈饶解牟艾尤阳时穆农司卓古吉缪简车项连芦麦褚娄窦戚岑景党宫费卜冷晏席卫米柏宗瞿桂全佟应臧闵苟邬边卞姬师和仇栾隋商刁沙荣巫寇桑郎甄丛仲虞敖巩明佘池查麻苑迟邝 ";
        if (userInfo.getString("username").length() == 39
                && userInfo.getString("username").startsWith("#anony_")) {
            StringBuilder builder = new StringBuilder();
            String username = userInfo.getString("username");
            int i = 6;
            for (int j = 0; j < 6; j++) {
                int pos;
                if (j == 0 || j == 3) {
                    pos = Integer.valueOf(username.substring(i + 1, i + 2), 16);
                    builder.append(t1.charAt(pos));
                } else {
                    pos = Integer.valueOf(username.substring(i, i + 2), 16);
                    builder.append(t2.charAt(pos));
                }
                i += 2;
            }
            row.author = builder.toString();
            row.setISANONYMOUS(true);
        } else {
            row.author = userInfo.getString("username");
        }
        row.js_escap_avatar = userInfo.getString("avatar");
        row.yz = userInfo.getString("yz");
        row.muteTime = userInfo.getString("mute_time");
        try {
            row.aurvrc = Integer.valueOf(userInfo.getString("rvrc"));
        } catch (Exception e) {
            row.aurvrc = 0;
        }
        row.signature = userInfo.getString("signature");

        try {
            row.setPostCount(userInfo.getString("postnum"));
            row.setReputation(Float.parseFloat(userInfo.getString("rvrc")) / 10.0f);
            row.setMemberGroup(groupObj.getJSONObject(userInfo.getString("memberid")).getString("0"));
        } catch (Exception e) {
        }

        JSONObject obj = userInfo.getJSONObject("buffs");
        if (obj != null) {
            for (String id : ForumConstants.BUFF_MUTE_IDS) {
                if (obj.containsKey(id)) {
                    row.setMuted(true);
                    break;
                }
            }
        }
    }

}

