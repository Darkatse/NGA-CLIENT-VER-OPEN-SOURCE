package sp.phone.mvp.contract;

import java.util.List;

import sp.phone.common.User;

/**
 * Created by Justwen on 2017/6/29.
 */

public interface BoardContract {

    interface Presenter {

        void loadBoardInfo();

        void toggleUser(List<User> userList);

        void startUserProfile(String uid);

        void startLogin();

    }

    interface View {

        int switchToNextUser();

        void jumpToLogin();

        void updateHeaderView();

    }

}
