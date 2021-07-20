package per.goweii.wanandroid.module.login.presenter;

import org.json.JSONObject;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import per.goweii.basic.core.base.BasePresenter;
import per.goweii.rxhttp.request.exception.ExceptionHandle;
import per.goweii.wanandroid.http.CmsApi;
import per.goweii.wanandroid.http.CmsBaseRequest;
import per.goweii.wanandroid.http.RequestListener;
import per.goweii.wanandroid.module.login.model.CmsLoginRequest;
import per.goweii.wanandroid.module.login.model.CmsLoginResp;
import per.goweii.wanandroid.module.login.model.LoginBean;
import per.goweii.wanandroid.module.login.model.LoginRequest;
import per.goweii.wanandroid.module.login.view.LoginView;
import per.goweii.wanandroid.module.mine.model.CmsMineRequest;
import per.goweii.wanandroid.module.mine.model.CmsUserResp;
import per.goweii.wanandroid.utils.UserUtils;

/**
 * @author CuiZhen
 * @date 2019/5/15
 * GitHub: https://github.com/goweii
 */
public class LoginPresenter extends BasePresenter<LoginView> {

    public void login(String username, String password, boolean isBiometric) {
        addToRxLife(LoginRequest.login(username, password, new RequestListener<LoginBean>() {
            @Override
            public void onStart() {
                showLoadingDialog();
            }

            @Override
            public void onSuccess(int code, LoginBean data) {
                if (CmsApi.Companion.isEnabled()) {
                    cmsLogin(data.getId(), username, password, isBiometric);
                } else {
                    UserUtils.getInstance().login(data);
                    if (isAttach()) {
                        getBaseView().loginSuccess(0, UserUtils.getInstance().getLoginUser(), username, password, isBiometric);
                    }
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                UserUtils.getInstance().logout();
                if (isAttach()) {
                    getBaseView().loginFailed(code, msg);
                }
                dismissLoadingDialog();
            }

            @Override
            public void onError(ExceptionHandle handle) {

            }

            @Override
            public void onFinish() {
                if (!CmsApi.Companion.isEnabled()) {
                    dismissLoadingDialog();
                }
            }
        }));
    }

    private void cmsLogin(int wanid, String username, String password, boolean isBiometric) {
        addToRxLife(CmsLoginRequest.INSTANCE.login(username, password, new CmsBaseRequest.Listener<>(
                null, null,
                new Function1<CmsLoginResp, Unit>() {
                    @Override
                    public Unit invoke(CmsLoginResp resp) {
                        UserUtils.getInstance().login(resp);
                        if (resp.getUser().getWanid() > 0) {
                            if (isAttach()) {
                                getBaseView().loginSuccess(0, UserUtils.getInstance().getLoginUser(), username, password, isBiometric);
                            }
                            dismissLoadingDialog();
                        } else {
                            cmsBindWanId(resp.getUser().getId(), wanid, username, password, isBiometric);
                        }
                        return null;
                    }
                },
                new Function1<String, Unit>() {
                    @Override
                    public Unit invoke(String s) {
                        UserUtils.getInstance().logout();
                        if (isAttach()) {
                            getBaseView().getEmailAndThenRegisterCms(wanid, username, password, isBiometric);
                        }
                        dismissLoadingDialog();
                        return null;
                    }
                }
        )));
    }

    public void cmsRegister(int wanid, String email, String username, String password, boolean isBiometric) {
        addToRxLife(CmsLoginRequest.INSTANCE.register(wanid, username, password, email, new CmsBaseRequest.Listener<CmsLoginResp>(
                null, null,
                new Function1<CmsLoginResp, Unit>() {
                    @Override
                    public Unit invoke(CmsLoginResp resp) {
                        UserUtils.getInstance().login(resp);
                        cmsBindWanId(resp.getUser().getId(), wanid, username, password, isBiometric);
                        return null;
                    }
                },
                new Function1<String, Unit>() {
                    @Override
                    public Unit invoke(String s) {
                        UserUtils.getInstance().logout();
                        if (isAttach()) {
                            getBaseView().loginFailed(0, "用户名或邮箱被占用");
                        }
                        dismissLoadingDialog();
                        return null;
                    }
                }
        )));
    }

    private void cmsBindWanId(String cmsid, int wanid, String username, String password, boolean isBiometric) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("wanid", wanid);
        } catch (Exception e) {
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObj.toString());
        addToRxLife(CmsMineRequest.INSTANCE.updateInfo(cmsid, body, new CmsBaseRequest.Listener<CmsUserResp>(
                null, null,
                new Function1<CmsUserResp, Unit>() {
                    @Override
                    public Unit invoke(CmsUserResp resp) {
                        UserUtils.getInstance().update(resp);
                        if (isAttach()) {
                            getBaseView().loginSuccess(0, UserUtils.getInstance().getLoginUser(), username, password, isBiometric);
                        }
                        dismissLoadingDialog();
                        return null;
                    }
                },
                new Function1<String, Unit>() {
                    @Override
                    public Unit invoke(String s) {
                        UserUtils.getInstance().logout();
                        if (isAttach()) {
                            getBaseView().loginFailed(0, "账号关联失败");
                        }
                        dismissLoadingDialog();
                        return null;
                    }
                }
        )));
    }
}
