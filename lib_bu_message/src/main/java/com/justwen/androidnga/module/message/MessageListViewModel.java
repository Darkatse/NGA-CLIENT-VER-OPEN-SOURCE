package com.justwen.androidnga.module.message;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.justwen.androidnga.core.data.MessageListInfo;

import gov.anzong.androidnga.http.OnHttpCallBack;
import com.justwen.androidnga.module.message.MessageListModel;

/**
 * @author yangyihang
 */
public class MessageListViewModel extends ViewModel {

    private final MutableLiveData<MessageListInfo> mMessageListData = new MutableLiveData<>();

    private final MutableLiveData<String> mErrorData = new MutableLiveData<>();

    private final MessageListModel mMessageModel = new MessageListModel();

    public void observeMessageList(LifecycleOwner owner, Observer<MessageListInfo> observer) {
        mMessageListData.observe(owner, observer);
    }

    public void observeErrorInfo(LifecycleOwner owner, Observer<String> observer) {
        mErrorData.observe(owner, observer);
    }

    public void getMessageList(int page) {
        mMessageModel.loadPage(page, new OnHttpCallBack<MessageListInfo>() {
            @Override
            public void onError(String text) {
                mErrorData.postValue(text);
            }

            @Override
            public void onSuccess(MessageListInfo data) {
                mMessageListData.postValue(data);
            }
        });

    }
}
