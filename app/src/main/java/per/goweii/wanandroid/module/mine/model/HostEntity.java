package per.goweii.wanandroid.module.mine.model;

import android.text.TextUtils;

import java.util.Objects;

import per.goweii.basic.core.base.BaseEntity;

/**
 * @author CuiZhen
 * @date 2019/10/13
 * GitHub: https://github.com/goweii
 */
public class HostEntity extends BaseEntity {
    private String host;
    private boolean custom;
    private boolean enable;

    public HostEntity(String host, boolean custom) {
        this.host = host;
        this.custom = custom;
        this.enable = true;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HostEntity entity = (HostEntity) o;
        return TextUtils.equals(host, entity.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host);
    }
}
