package per.goweii.basic.ui.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.Layer;
import per.goweii.basic.ui.R;
import per.goweii.basic.utils.ResUtils;
import per.goweii.basic.utils.listener.SimpleCallback;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2019/3/14
 */
public class PermissionDialog {

    private final Context mContext;
    private boolean isGoSetting = false;
    private GroupType mGroupType = null;
    private SimpleCallback<Void> onNextListener = null;
    private SimpleCallback<Void> onCloseListener = null;

    public static PermissionDialog with(Context context) {
        return new PermissionDialog(context);
    }

    private PermissionDialog(Context context) {
        this.mContext = context;
    }

    public PermissionDialog setGoSetting(boolean goSetting) {
        isGoSetting = goSetting;
        return this;
    }

    public PermissionDialog setGroupType(GroupType groupType) {
        mGroupType = groupType;
        return this;
    }

    public PermissionDialog setOnNextListener(SimpleCallback<Void> onNextListener) {
        this.onNextListener = onNextListener;
        return this;
    }

    public PermissionDialog setOnCloseListener(SimpleCallback<Void> onCloseListener) {
        this.onCloseListener = onCloseListener;
        return this;
    }

    public void show() {
        AnyLayer.dialog(mContext)
                .contentView(R.layout.basic_ui_dialog_permission)
                .gravity(Gravity.CENTER)
                .backgroundDimDefault()
                .cancelableOnTouchOutside(false)
                .cancelableOnClickKeyBack(false)
                .bindData(new Layer.DataBinder() {
                    @Override
                    public void bindData(Layer layer) {
                        ImageView iv = layer.getView(R.id.basic_ui_iv_dialog_permission);
                        TextView tvTitle = layer.getView(R.id.basic_ui_tv_dialog_permission_title);
                        TextView tvDescription = layer.getView(R.id.basic_ui_tv_dialog_permission_description);
                        TextView tvNext = layer.getView(R.id.basic_ui_tv_dialog_permission_next);

                        int iconResId = getIconResId();
                        if (iconResId > 0) {
                            iv.setImageResource(iconResId);
                            iv.setVisibility(View.VISIBLE);
                        } else {
                            iv.setVisibility(View.GONE);
                        }
                        int titleResId = getTitleResId();
                        if (titleResId > 0) {
                            tvTitle.setText(titleResId);
                            tvTitle.setVisibility(View.VISIBLE);
                        } else {
                            tvTitle.setVisibility(View.GONE);
                        }
                        tvDescription.setText(getDescription());
                        if (isGoSetting) {
                            tvNext.setText(R.string.basic_ui_dialog_permission_next_go_setting);
                        } else {
                            tvNext.setText(R.string.basic_ui_dialog_permission_next);
                        }
                    }
                })
                .onClickToDismiss(new Layer.OnClickListener() {
                    @Override
                    public void onClick(Layer layer, View v) {
                        if (onCloseListener != null) {
                            onCloseListener.onResult(null);
                        }
                    }
                }, R.id.basic_ui_tv_dialog_permission_close)
                .onClickToDismiss(new Layer.OnClickListener() {
                    @Override
                    public void onClick(Layer layer, View v) {
                        if (onNextListener != null) {
                            onNextListener.onResult(null);
                        }
                    }
                }, R.id.basic_ui_tv_dialog_permission_next)
                .show();
    }

    private int getIconResId() {
        int resId = R.drawable.basic_ui_dialog_permission_unknow;
        if (mGroupType == null) {
            return resId;
        }
        switch (mGroupType) {
            default:
                break;
            case CALENDAR:
                resId = R.drawable.basic_ui_dialog_permission_calendar;
                break;
            case CAMERA:
                resId = R.drawable.basic_ui_dialog_permission_camera;
                break;
            case CONTACTS:
                resId = R.drawable.basic_ui_dialog_permission_contacts;
                break;
            case LOCATION:
                resId = R.drawable.basic_ui_dialog_permission_location;
                break;
            case MICROPHONE:
                resId = R.drawable.basic_ui_dialog_permission_microphone;
                break;
            case PHONE:
                resId = R.drawable.basic_ui_dialog_permission_phone;
                break;
            case SMS:
                resId = R.drawable.basic_ui_dialog_permission_sms;
                break;
            case SENSORS:
                resId = R.drawable.basic_ui_dialog_permission_sensors;
                break;
            case STORAGE:
                resId = R.drawable.basic_ui_dialog_permission_storage;
                break;
        }
        return resId;
    }
    
    private int getTitleResId(){
        int resId = R.string.basic_ui_dialog_permission_title_unknow;
        if (mGroupType == null) {
            return resId;
        }
        switch (mGroupType) {
            default:
                break;
            case CALENDAR:
                resId = R.string.basic_ui_dialog_permission_title_calendar;
                break;
            case CAMERA:
                resId = R.string.basic_ui_dialog_permission_title_camera;
                break;
            case CONTACTS:
                resId = R.string.basic_ui_dialog_permission_title_contacts;
                break;
            case LOCATION:
                resId = R.string.basic_ui_dialog_permission_title_location;
                break;
            case MICROPHONE:
                resId = R.string.basic_ui_dialog_permission_title_microphone;
                break;
            case PHONE:
                resId = R.string.basic_ui_dialog_permission_title_phone;
                break;
            case SMS:
                resId = R.string.basic_ui_dialog_permission_title_sms;
                break;
            case SENSORS:
                resId = R.string.basic_ui_dialog_permission_title_sensors;
                break;
            case STORAGE:
                resId = R.string.basic_ui_dialog_permission_title_storage;
                break;
        }
        return resId;
    }

    private String getDescription() {
        String description;
        String descriptionRes;
        if (isGoSetting) {
            descriptionRes = ResUtils.getString(R.string.basic_ui_dialog_permission_description_go_setting);
        } else {
            descriptionRes = ResUtils.getString(R.string.basic_ui_dialog_permission_description);
        }
        int titleResId = getTitleResId();
        if (titleResId <= 0) {
            description = String.format(descriptionRes, ResUtils.getString(R.string.basic_ui_dialog_permission_description_title_holder));
        } else {
            description = String.format(descriptionRes, ResUtils.getString(titleResId));
        }
        return description;
    }

    public enum GroupType {
        /**
         * 权限组类别，根据类别显示不同的图标和提示语
         */
        CALENDAR,
        CAMERA,
        CONTACTS,
        LOCATION,
        MICROPHONE,
        PHONE,
        SMS,
        SENSORS,
        STORAGE
    }
}
