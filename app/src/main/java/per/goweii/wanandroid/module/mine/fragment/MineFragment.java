package per.goweii.wanandroid.module.mine.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.listener.OnMultiListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import per.goweii.basic.core.base.BaseFragment;
import per.goweii.basic.core.utils.SmartRefreshUtils;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.common.Config;
import per.goweii.wanandroid.event.LoginEvent;
import per.goweii.wanandroid.event.NotificationEvent;
import per.goweii.wanandroid.event.SettingChangeEvent;
import per.goweii.wanandroid.event.UserInfoUpdateEvent;
import per.goweii.wanandroid.module.login.model.UserEntity;
import per.goweii.wanandroid.module.mine.activity.AboutMeActivity;
import per.goweii.wanandroid.module.mine.activity.CoinActivity;
import per.goweii.wanandroid.module.mine.activity.CollectionActivity;
import per.goweii.wanandroid.module.mine.activity.MineShareActivity;
import per.goweii.wanandroid.module.mine.activity.NotificationActivity;
import per.goweii.wanandroid.module.mine.activity.OpenActivity;
import per.goweii.wanandroid.module.mine.activity.ReadLaterActivity;
import per.goweii.wanandroid.module.mine.activity.ReadRecordActivity;
import per.goweii.wanandroid.module.mine.activity.SettingActivity;
import per.goweii.wanandroid.module.mine.activity.UserInfoActivity;
import per.goweii.wanandroid.module.mine.model.UserInfoBean;
import per.goweii.wanandroid.module.mine.presenter.MinePresenter;
import per.goweii.wanandroid.module.mine.view.MineView;
import per.goweii.wanandroid.utils.ImageLoader;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.utils.UserUtils;

/**
 * @author CuiZhen
 * @date 2019/5/12
 * GitHub: https://github.com/goweii
 */
public class MineFragment extends BaseFragment<MinePresenter> implements MineView {

    @BindView(R.id.aiv_notification)
    ImageView aiv_notification;
    @BindView(R.id.tv_notification)
    TextView tv_notification;
    @BindView(R.id.srl)
    SmartRefreshLayout srl;
    @BindView(R.id.nsv)
    NestedScrollView nsv;
    @BindView(R.id.iv_blur)
    ImageView iv_blur;
    @BindView(R.id.rl_user_info)
    RelativeLayout rl_user_info;
    @BindView(R.id.civ_user_icon)
    ImageView civ_user_icon;
    @BindView(R.id.tv_user_name)
    TextView tv_user_name;
    @BindView(R.id.tv_user_signature)
    TextView tv_user_signature;
    @BindView(R.id.ll_user_signature)
    LinearLayout ll_user_signature;
    @BindView(R.id.ll_user_level_ranking)
    LinearLayout ll_user_level_ranking;
    @BindView(R.id.ll_read_later)
    LinearLayout ll_read_later;
    @BindView(R.id.ll_read_record)
    LinearLayout ll_read_record;
    @BindView(R.id.ll_open)
    LinearLayout ll_open;
    @BindView(R.id.ll_about_me)
    LinearLayout ll_about_me;
    @BindView(R.id.tv_user_level)
    TextView tv_user_level;
    @BindView(R.id.tv_user_ranking)
    TextView tv_user_ranking;
    @BindView(R.id.tv_coin)
    TextView tv_coin;

    private SmartRefreshUtils mSmartRefreshUtils;

    public static MineFragment create() {
        return new MineFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
        if (isDetached()) {
            return;
        }
        setRefresh();
        refreshUserInfo();
        loadUserInfo();
        loadNotificationCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserInfoUpdateEvent(UserInfoUpdateEvent event) {
        if (isDetached()) {
            return;
        }
        refreshUserInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingChangeEvent(SettingChangeEvent event) {
        if (isDetached()) {
            return;
        }
        if (event.isShowReadLaterChanged() || event.isShowReadRecordChanged() || event.isHideAboutMeChanged() || event.isHideOpenChanged()) {
            changeMenuVisible();
        }
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_mine;
    }

    @Nullable
    @Override
    protected MinePresenter initPresenter() {
        return new MinePresenter();
    }

    @Override
    protected void initView() {
        aiv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationActivity.start(getContext());
                loadNotificationCount();
            }
        });
        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (nsv == null || rl_user_info == null) return;
                setIvBlurHeight(rl_user_info.getMeasuredHeight() - scrollY);
            }
        });
        srl.setOnMultiListener(new OnMultiListener() {
            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                if (nsv == null || rl_user_info == null) return;
                setIvBlurHeight(rl_user_info.getMeasuredHeight() - nsv.getScrollY() + offset);
            }

            @Override
            public void onHeaderReleased(RefreshHeader header, int headerHeight, int maxDragHeight) {
            }

            @Override
            public void onHeaderStartAnimator(RefreshHeader header, int headerHeight, int maxDragHeight) {
            }

            @Override
            public void onHeaderFinish(RefreshHeader header, boolean success) {
            }

            @Override
            public void onFooterMoving(RefreshFooter footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight) {
                setIvBlurHeight(rl_user_info.getMeasuredHeight() - nsv.getScrollY() - offset);
            }

            @Override
            public void onFooterReleased(RefreshFooter footer, int footerHeight, int maxDragHeight) {
            }

            @Override
            public void onFooterStartAnimator(RefreshFooter footer, int footerHeight, int maxDragHeight) {
            }

            @Override
            public void onFooterFinish(RefreshFooter footer, boolean success) {
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            }

            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
            }
        });
        mSmartRefreshUtils = SmartRefreshUtils.with(srl);
        mSmartRefreshUtils.pureScrollMode();
        setRefresh();
        changeMenuVisible();
    }

    private void setIvBlurHeight(int h) {
        if (iv_blur == null) return;
        if (h >= 0) {
            iv_blur.getLayoutParams().height = h;
        } else {
            iv_blur.getLayoutParams().height = 0;
        }
        iv_blur.requestLayout();
    }

    private void setRefresh() {
        if (UserUtils.getInstance().isLogin()) {
            mSmartRefreshUtils.setRefreshListener(new SmartRefreshUtils.RefreshListener() {
                @Override
                public void onRefresh() {
                    loadUserInfo();
                    loadNotificationCount();
                }
            });
        } else {
            mSmartRefreshUtils.setRefreshListener(null);
        }
    }

    @Override
    protected void loadData() {
        refreshUserInfo();
        loadNotificationCount();
    }

    private void loadUserInfo() {
        if (UserUtils.getInstance().isLogin()) {
            presenter.getUserInfo();
        }
    }

    private void loadNotificationCount() {
        presenter.getNotificationCount();
    }

    @Override
    public void onVisible(boolean isFirstVisible) {
        super.onVisible(isFirstVisible);
        if (isFirstVisible) {
            loadUserInfo();
        }
    }

    private void changeMenuVisible() {
        SettingUtils settingUtils = SettingUtils.getInstance();
        if (settingUtils.isShowReadLater()) {
            ll_read_later.setVisibility(View.VISIBLE);
        } else {
            ll_read_later.setVisibility(View.GONE);
        }
        if (settingUtils.isShowReadRecord()) {
            ll_read_record.setVisibility(View.VISIBLE);
        } else {
            ll_read_record.setVisibility(View.GONE);
        }
        if (!settingUtils.isHideAboutMe()) {
            ll_about_me.setVisibility(View.VISIBLE);
        } else {
            ll_about_me.setVisibility(View.GONE);
        }
        if (!settingUtils.isHideOpen()) {
            ll_open.setVisibility(View.VISIBLE);
        } else {
            ll_open.setVisibility(View.GONE);
        }
    }

    private void refreshUserInfo() {
        if (UserUtils.getInstance().isLogin()) {
            UserEntity bean = UserUtils.getInstance().getLoginUser();
            ImageLoader.userIcon(civ_user_icon, bean.getAvatar());
            ImageLoader.userBlur(iv_blur, bean.getCover());
            tv_user_name.setText(bean.getUsername());
            ll_user_signature.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(bean.getSignature())) {
                tv_user_signature.setText("写个签名鼓励下自己吧");
            } else {
                tv_user_signature.setText(bean.getSignature());
            }
            ll_user_level_ranking.setVisibility(View.VISIBLE);
            if (presenter.mUserInfoBean != null) {
                tv_coin.setText(presenter.mUserInfoBean.getCoinCount() + "");
                tv_user_level.setText(presenter.mUserInfoBean.getLevel() + "");
                tv_user_ranking.setText(presenter.mUserInfoBean.getRank() + "");
            } else {
                tv_user_level.setText("--");
                tv_user_ranking.setText("--");
                tv_coin.setText("");
            }
        } else {
            civ_user_icon.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
            iv_blur.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
            tv_user_name.setText("去登陆");
            ll_user_signature.setVisibility(View.INVISIBLE);
            tv_user_signature.setText("");
            ll_user_level_ranking.setVisibility(View.INVISIBLE);
            tv_user_level.setText("--");
            tv_user_ranking.setText("--");
            tv_coin.setText("");
        }
    }

    @OnClick({
            R.id.civ_user_icon, R.id.tv_user_name,
            R.id.ll_collect, R.id.ll_read_later, R.id.ll_read_record, R.id.ll_about_me,
            R.id.ll_open, R.id.ll_setting, R.id.ll_coin, R.id.ll_share
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    protected void onClick2(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.ll_coin:
                if (UserUtils.getInstance().doIfLogin(getContext())) {
                    CoinActivity.start(getContext());
                }
                break;
            case R.id.civ_user_icon:
            case R.id.tv_user_name:
                if (UserUtils.getInstance().doIfLogin(getContext())) {
                    UserInfoActivity.start(getContext());
                }
                break;
            case R.id.ll_share:
                if (UserUtils.getInstance().doIfLogin(getContext())) {
                    MineShareActivity.start(getContext());
                }
                break;
            case R.id.ll_collect:
                if (UserUtils.getInstance().doIfLogin(getContext())) {
                    CollectionActivity.start(getContext());
                }
                break;
            case R.id.ll_read_later:
                ReadLaterActivity.start(getContext());
                break;
            case R.id.ll_read_record:
                ReadRecordActivity.start(getContext());
                break;
            case R.id.ll_about_me:
                AboutMeActivity.start(getContext());
                break;
            case R.id.ll_open:
                OpenActivity.start(getContext());
                break;
            case R.id.ll_setting:
                SettingActivity.start(getContext());
                break;
        }
    }

    @Override
    public void getUserInfoSuccess(int code, UserInfoBean data) {
        mSmartRefreshUtils.success();
        tv_coin.setText(data.getCoinCount() + "");
        ll_user_level_ranking.setVisibility(View.VISIBLE);
        tv_user_level.setText(data.getLevel() + "");
        tv_user_ranking.setText(data.getRank() + "");
    }

    @Override
    public void getUserInfoFail(int code, String msg) {
        mSmartRefreshUtils.fail();
        tv_coin.setText("");
        tv_user_level.setText("--");
        tv_user_ranking.setText("--");
    }

    @Override
    public void getNotificationCountSuccess(int count) {
        NotificationEvent.post(count);
        if (count > 0) {
            tv_notification.setVisibility(View.VISIBLE);
            if (count > Config.NOTIFICATION_MAX_SHOW_COUNT) {
                tv_notification.setText(Config.NOTIFICATION_MAX_SHOW_COUNT + "+");
            } else {
                tv_notification.setText(count + "");
            }
        } else {
            tv_notification.setVisibility(View.GONE);
        }
    }

}
