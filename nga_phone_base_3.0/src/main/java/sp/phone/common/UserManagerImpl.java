package sp.phone.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.common.PreferenceKey;

import com.justwent.androidnga.bu.UserManager;

import sp.phone.http.bean.ThreadData;
import sp.phone.http.bean.ThreadRowInfo;


public class UserManagerImpl implements sp.phone.common.UserManager {

    private List<User> mBlackList;

    private SharedPreferences mPrefs;

    private SharedPreferences mAvatarPreferences;

    private static class SingletonHolder {

        static UserManagerImpl sInstance = new UserManagerImpl();
    }

    public static UserManagerImpl  getInstance() {
        return SingletonHolder.sInstance;
    }


    private UserManagerImpl() {
    }

    @Override
    public void initialize(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        mAvatarPreferences = context.getSharedPreferences(PreferenceKey.PREFERENCE_AVATAR, Context.MODE_PRIVATE);


        String blackListStr = mPrefs.getString(PreferenceKey.BLACK_LIST, "");
        if (TextUtils.isEmpty(blackListStr)) {
            mBlackList = new ArrayList<>();
        } else {
            mBlackList = JSON.parseArray(blackListStr, User.class);
            if (mBlackList == null) {
                mBlackList = new ArrayList<>();
            }
        }
        transformData();
    }

    private void transformData() {
        mBlackList.removeIf(user -> user.getUserId() == null);
    }

    @Override
    public int getActiveUserIndex() {
        return UserManager.INSTANCE.getActiveIndex();
    }

    @Nullable
    @Override
    public User getActiveUser() {
        return UserManager.INSTANCE.getActiveUser();
    }

    @Override
    public List<User> getUserList() {
        return UserManager.INSTANCE.getUserList();
    }

    @Override
    public boolean hasValidUser() {
        return UserManager.INSTANCE.hasValidUser();
    }

    @Override
    public String getCid() {
        User user = UserManager.INSTANCE.getActiveUser();
        return user != null ? user.getCid() : "";
    }

    @Override
    public String getUserName() {
        User user = UserManager.INSTANCE.getActiveUser();
        return user != null ? user.getNickName() : "";
    }

    @Override
    public void setAvatarUrl(int userId, String url) {
        for (User user : mBlackList) {
            if (user.getUserId().equals(String.valueOf(userId))) {
                if (user.getAvatarUrl() == null) {
                    user.setAvatarUrl(url);
                    commit();
                }
                return;
            }
        }
        UserManager.INSTANCE.setAvatarUrl(String.valueOf(userId), url);
    }

    @Override
    public String getUserId() {
        User user = UserManager.INSTANCE.getActiveUser();
        return user != null ? user.getUserId() : "";
    }

    @Override
    public void setActiveUser(int index) {
        UserManager.INSTANCE.setActiveIndex(index);
    }

    @Override
    public int toggleUser(boolean isNext) {
        return UserManager.INSTANCE.toggleUser(isNext);

    }

    private int getNextActiveIndex(boolean isNext) {
        return UserManager.INSTANCE.getNextActiveIndex(isNext);
    }

    @Override
    public void addUser(User user) {
        UserManager.INSTANCE.addUser(user);
    }

    @Override
    public void addUser(String uid, String cid, String name) {
        User user = new User();
        user.setCid(cid);
        user.setUserId(uid);
        user.setNickName(name);
        addUser(user);
    }

    @Override
    public void removeUser(int index) {
        UserManager.INSTANCE.removeUser(index);
    }

    private void commit() {
        mPrefs.edit()
                .putString(PreferenceKey.BLACK_LIST, JSON.toJSONString(mBlackList))
                .apply();
    }

    @Override
    public String getCookie() {
        return UserManager.INSTANCE.getCookie(UserManager.INSTANCE.getActiveUser());
    }

    @Override
    public String getCookie(User user) {
        return UserManager.INSTANCE.getCookie(user);
    }

    @Override
    public String getNextCookie() {
        return UserManager.INSTANCE.getNextCookie();
    }

    @Override
    public void swapUser(int from, int to) {
        //if (from < to) {
        //    for (int i = from; i < to; i++) {
        //        Collections.swap(mUserList, i, i + 1);
        //    }
        //} else {
        //    for (int i = from; i > to; i--) {
        //        Collections.swap(mUserList, i, i - 1);
        //    }
        //}
        //commit();
    }

    @Override
    public void addToBlackList(String authorName, String authorId) {
        for (int i = 0; i < mBlackList.size(); i++) {
            sp.phone.common.User user = mBlackList.get(i);
            if (user.getUserId().equals(authorId)) {
                return;
            }
        }
        User user = new User();
        user.setUserId(authorId);
        user.setNickName(authorName);
        mBlackList.add(user);
        mPrefs.edit().putString(PreferenceKey.BLACK_LIST, JSON.toJSONString(mBlackList)).apply();
    }

    @Override
    public void addToBlackList(User user) {
        if (!mBlackList.contains(user)) {
            mBlackList.add(user);
        }
        mPrefs.edit().putString(PreferenceKey.BLACK_LIST, JSON.toJSONString(mBlackList)).apply();
    }

    @Override
    public void removeFromBlackList(String authorId) {
        for (int i = 0; i < mBlackList.size(); i++) {
            User user = mBlackList.get(i);
            if (user.getUserId().equals(authorId)) {
                mBlackList.remove(i);
                mPrefs.edit().putString(PreferenceKey.BLACK_LIST, JSON.toJSONString(mBlackList)).apply();
                return;
            }
        }
    }

    @Override
    public int getUserSize() {
        return UserManager.INSTANCE.getUserList().size();
    }

    @Override
    public boolean checkBlackList(String authorId) {
        for (User user : mBlackList) {
            if (user.getUserId().equals(authorId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<User> getBlackList() {
        return mBlackList;
    }

    @Override
    public void removeAllBlackList() {
        mBlackList.clear();
        mPrefs.edit().putString(PreferenceKey.BLACK_LIST, JSON.toJSONString(mBlackList)).apply();
    }

    @Override
    public void putAvatarUrl(String uid, String url) {
        if (!TextUtils.isEmpty(url)) {
            mAvatarPreferences.edit().putString(uid, url).apply();
        }
    }

    @Override
    public void putAvatarUrl(ThreadData info) {
        if (info.getRowList() == null) {
            return;
        }
        SharedPreferences.Editor editor = mAvatarPreferences.edit();
        for (ThreadRowInfo rowInfo : info.getRowList()) {
            String uid = String.valueOf(rowInfo.getAuthorid());
            String url = rowInfo.getJs_escap_avatar();
            if (!TextUtils.isEmpty(uid) && !uid.equals("0") && !TextUtils.isEmpty(url)) {
                editor.putString(uid, url);
                setAvatarUrl(Integer.parseInt(uid), url);
            }
        }
        editor.apply();
    }

    @Override
    public String getAvatarUrl(String uid) {
        return TextUtils.isEmpty(uid) || uid.equals("0") ? "" : mAvatarPreferences.getString(uid, "");
    }

    @Override
    public void clearAvatarUrl() {
        mAvatarPreferences.edit().clear().apply();
    }
}
