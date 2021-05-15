package per.goweii.wanandroid.module.main.model;

import per.goweii.rxhttp.request.base.BaseBean;

public class BetaUserBean extends BaseBean {
    private int userId;
    private String userName;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
