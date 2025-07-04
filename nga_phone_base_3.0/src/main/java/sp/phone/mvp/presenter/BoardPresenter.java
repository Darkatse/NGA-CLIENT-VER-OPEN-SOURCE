package sp.phone.mvp.presenter;

import android.app.Activity;

import java.util.List;

import gov.anzong.androidnga.arouter.ARouterConstants;
import gov.anzong.androidnga.base.util.ToastUtils;
import sp.phone.common.User;
import sp.phone.common.UserManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.mvp.contract.BoardContract;
import sp.phone.mvp.model.BaseModel;
import sp.phone.ui.fragment.NavigationDrawerFragment;
import sp.phone.util.ARouterUtils;
import sp.phone.util.ActivityUtils;

/**
 * 版块管理
 * Created by Justwen on 2017/6/29.
 */

public class BoardPresenter extends BasePresenter<NavigationDrawerFragment, BaseModel> implements BoardContract.Presenter {

    private UserManager mUserManager;

    public BoardPresenter() {
        super();
        mUserManager = UserManagerImpl.getInstance();
    }


    @Override
    public void loadBoardInfo() {

    }


    @Override
    public void toggleUser(List<User> userList) {
        if (userList != null && userList.size() > 1) {
            int index = mBaseView.switchToNextUser();
            mUserManager.setActiveUser(index);
            ToastUtils.showToast("切换账户成功,当前账户名:" + mUserManager.getActiveUser().getNickName());
        } else {
            mBaseView.jumpToLogin();
        }
    }

    @Override
    public void startUserProfile(String uid) {
        ARouterUtils.build(ARouterConstants.ACTIVITY_PROFILE)
                .withString("mode", "uid")
                .withString("uid", uid)
                .navigation(mBaseView.getContext());
    }

    @Override
    public void startLogin() {
        ARouterUtils.build(ARouterConstants.ACTIVITY_LOGIN).navigation((Activity) mBaseView.getContext(), ActivityUtils.REQUEST_CODE_LOGIN);
    }

    @Override
    protected BaseModel onCreateModel() {
        return null;
    }
}
