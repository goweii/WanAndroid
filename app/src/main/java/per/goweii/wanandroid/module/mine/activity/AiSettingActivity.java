package per.goweii.wanandroid.module.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import per.goweii.basic.core.base.BaseActivity;
import per.goweii.basic.core.base.BasePresenter;
import per.goweii.basic.ui.dialog.ListDialog;
import per.goweii.basic.utils.listener.SimpleCallback;
import per.goweii.wanandroid.R;
import per.goweii.wanandroid.databinding.ActivityAiSettingBinding;
import per.goweii.wanandroid.module.mine.dialog.InfoEditDialog;
import per.goweii.wanandroid.utils.SettingUtils;
import per.goweii.wanandroid.utils.ai.AiModel;
import per.goweii.wanandroid.utils.ai.AiProvider;

public class AiSettingActivity extends BaseActivity<BasePresenter, ActivityAiSettingBinding> {
    @BindView(R.id.sc_enable_ai)
    SwitchCompat sc_enable_ai;
    @BindView(R.id.tv_ai_provider)
    TextView tv_ai_provider;
    @BindView(R.id.tv_ai_api_key)
    TextView tv_ai_api_key;
    @BindView(R.id.tv_ai_model)
    TextView tv_ai_model;

    private boolean mAiEnabled;
    private String mAiProvider;
    private String mAiApiKey;
    private String mAiModel;

    public static void start(Context context) {
        Intent intent = new Intent(context, AiSettingActivity.class);
        context.startActivity(intent);
    }

    @Nullable
    @Override
    protected ActivityAiSettingBinding initViewBinding(@NonNull LayoutInflater inflater) {
        return ActivityAiSettingBinding.inflate(inflater);
    }

    @Nullable
    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        mAiEnabled = SettingUtils.getInstance().isAiEnabled();
        mAiProvider = SettingUtils.getInstance().getAiProvider();
        mAiApiKey = SettingUtils.getInstance().getAiApiKey();
        mAiModel = SettingUtils.getInstance().getAiModel();

        AiProvider aiProvider = AiProvider.findProvider(mAiProvider);
        if (aiProvider == null) {
            aiProvider = AiProvider.getProviders().get(0);
        }
        AiModel aiModel = aiProvider.findModel(mAiModel);
        if (aiModel == null) {
            aiModel = aiProvider.getModels().get(0);
        }

        sc_enable_ai.setChecked(mAiEnabled);
        tv_ai_provider.setText(aiProvider.getName());
        tv_ai_api_key.setText("***");
        tv_ai_model.setText(aiModel.getName());

        sc_enable_ai.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                SettingUtils.getInstance().setAiEnabled(isChecked);
            }
        });
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @OnClick({
            R.id.ll_ai_provider, R.id.ll_ai_api_key, R.id.ll_ai_model,
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    protected void onClick2(View v) {
        switch (v.getId()) {
            case R.id.ll_ai_provider: {
                final List<AiProvider> providers = AiProvider.getProviders();
                final List<String> nameList = new ArrayList<>(providers.size());
                for (AiProvider aiProvider : providers) {
                    nameList.add(aiProvider.getName());
                }
                final String aiProviderId = SettingUtils.getInstance().getAiProvider();
                int selectedPos = -1;
                for (int i = 0; i < providers.size(); i++) {
                    if (providers.get(i).getId().equals(aiProviderId)) {
                        selectedPos = i;
                    }
                }
                ListDialog.with(getContext())
                        .cancelable(true)
                        .title(getString(R.string.ai_provider))
                        .datas(nameList)
                        .currSelectPos(selectedPos)
                        .onItemSelectedListener(new ListDialog.OnItemSelectedListener() {
                            @Override
                            public void onSelect(String data, int pos) {
                                AiProvider aiProvider = providers.get(pos);
                                if (aiProvider.getId().equals(aiProviderId)) {
                                    return;
                                }
                                mAiProvider = aiProvider.getId();
                                SettingUtils.getInstance().setAiProvider(aiProvider.getId());
                                tv_ai_provider.setText(aiProvider.getName());
                                AiModel aiModel = aiProvider.getModels().get(0);
                                mAiModel = aiModel.getId();
                                tv_ai_model.setText(aiModel.getName());
                            }
                        })
                        .show();
            }
            break;
            case R.id.ll_ai_model: {
                final String aiProviderId = SettingUtils.getInstance().getAiProvider();
                final String aiModelId = SettingUtils.getInstance().getAiModel();
                final AiProvider provider = AiProvider.findProvider(aiProviderId);
                if (provider == null) {
                    return;
                }
                final List<AiModel> models = provider.getModels();
                final List<String> nameList = new ArrayList<>(models.size());
                for (AiModel aiModel : models) {
                    nameList.add(aiModel.getName());
                }
                int selectedPos = -1;
                for (int i = 0; i < models.size(); i++) {
                    if (models.get(i).getId().equals(aiModelId)) {
                        selectedPos = i;
                    }
                }
                ListDialog.with(getContext())
                        .cancelable(true)
                        .title(getString(R.string.ai_model))
                        .datas(nameList)
                        .currSelectPos(selectedPos)
                        .onItemSelectedListener(new ListDialog.OnItemSelectedListener() {
                            @Override
                            public void onSelect(String data, int pos) {
                                AiModel aiModel = models.get(pos);
                                if (aiModel.getId().equals(aiModelId)) {
                                    return;
                                }
                                mAiModel = aiModel.getId();
                                SettingUtils.getInstance().setAiModel(aiModel.getId());
                                tv_ai_model.setText(aiModel.getName());
                            }
                        })
                        .show();
            }
            break;
            case R.id.ll_ai_api_key: {
                final String apiKey = SettingUtils.getInstance().getAiApiKey();
                InfoEditDialog.with(getContext())
                        .title(getString(R.string.ai_api_key))
                        .text(apiKey)
                        .lines(3)
                        .show(new SimpleCallback<String>() {
                            @Override
                            public void onResult(String data) {
                                if (TextUtils.equals(data, apiKey)) {
                                    return;
                                }
                                mAiApiKey = data;
                                SettingUtils.getInstance().setAiApiKey(data);
                            }
                        });
            }
            break;
            default:
                break;
        }
    }

}
