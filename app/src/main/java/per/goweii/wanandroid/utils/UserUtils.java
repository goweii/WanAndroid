package per.goweii.wanandroid.utils;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;

import per.goweii.basic.utils.SPUtils;
import per.goweii.wanandroid.module.login.activity.AuthActivity;
import per.goweii.wanandroid.module.login.model.CmsLoginResp;
import per.goweii.wanandroid.module.login.model.UserEntity;
import per.goweii.wanandroid.module.mine.model.CmsUserResp;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * GitHub: https://github.com/goweii
 */
public class UserUtils {

    private static final String KEY_LOGIN_USER_ENTITY = "KEY_LOGIN_USER_ENTITY";

    private UserEntity mUserEntity = null;

    private static class Holder {
        private static final UserUtils INSTANCE = new UserUtils();
    }

    public static UserUtils getInstance() {
        return Holder.INSTANCE;
    }

    private UserUtils() {
        getLoginUser();
    }

    public UserEntity getLoginUser() {
        if (mUserEntity == null) {
            String json = SPUtils.getInstance().get(KEY_LOGIN_USER_ENTITY, "");
            if (!TextUtils.isEmpty(json)) {
                try {
                    mUserEntity = new Gson().fromJson(json, UserEntity.class);
                } catch (Exception ignore) {
                }
            }
        }
        return mUserEntity;
    }

    public void login(UserEntity userEntity) {
        mUserEntity = userEntity;
        SPUtils.getInstance().save(KEY_LOGIN_USER_ENTITY, new Gson().toJson(userEntity));
    }

    public void login(CmsLoginResp loginResp) {
        CmsUserResp userResp = loginResp.getUser();
        UserEntity userEntity = new UserEntity(
                userResp.getEmail(),
                userResp.getUsername(),
                userResp.getWanid(),
                userResp.getId(),
                loginResp.getJwt(),
                userResp.getSex(),
                userResp.getSignature(),
                userResp.getAvatar(),
                userResp.getCover()
        );
        login(userEntity);
    }

    public void logout() {
        mUserEntity = null;
        SPUtils.getInstance().clear();
    }

    public void update(CmsUserResp userResp) {
        UserEntity userEntity = new UserEntity(
                userResp.getEmail(),
                userResp.getUsername(),
                userResp.getWanid(),
                userResp.getId(),
                getCmsJwt(),
                userResp.getSex(),
                userResp.getSignature(),
                userResp.getAvatar(),
                userResp.getCover()
        );
        update(userEntity);
    }

    public void update(UserEntity userEntity) {
        mUserEntity = userEntity;
        SPUtils.getInstance().save(KEY_LOGIN_USER_ENTITY, new Gson().toJson(userEntity));
    }

    public boolean isLogin() {
        UserEntity loginBean = getLoginUser();
        if (loginBean == null) {
            return false;
        }
        if (loginBean.getWanid() <= 0) {
            return false;
        }
        return !loginBean.getCmsid().isEmpty();
    }

    public int getWanId() {
        UserEntity loginBean = getLoginUser();
        if (loginBean == null) {
            return 0;
        }
        return loginBean.getWanid();
    }

    public String getCmsId() {
        UserEntity loginBean = getLoginUser();
        if (loginBean == null) {
            return "";
        }
        return loginBean.getCmsid();
    }

    public String getCmsJwt() {
        UserEntity loginBean = getLoginUser();
        if (loginBean == null) {
            return "";
        }
        return loginBean.getJwt();
    }

    public boolean doIfLogin(Context context) {
        if (isLogin()) {
            return true;
        } else {
            AuthActivity.startQuickLogin(context);
            return false;
        }
    }

}
