package per.goweii.wanandroid.module.main.model;

import per.goweii.rxhttp.request.base.BaseBean;

/**
 * @author CuiZhen
 * @date 2019/5/19
 * GitHub: https://github.com/goweii
 */
public class UpdateBean extends BaseBean {

    private String url;
    private String url_backup;
    private String version_name;
    private int version_code;
    private String last_force_version_name;
    private int last_force_version_code;
    private boolean force;
    private String desc;
    private String time;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl_backup() {
        return url_backup;
    }

    public void setUrl_backup(String url_backup) {
        this.url_backup = url_backup;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    public String getLast_force_version_name() {
        return last_force_version_name;
    }

    public void setLast_force_version_name(String last_force_version_name) {
        this.last_force_version_name = last_force_version_name;
    }

    public int getLast_force_version_code() {
        return last_force_version_code;
    }

    public void setLast_force_version_code(int last_force_version_code) {
        this.last_force_version_code = last_force_version_code;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
