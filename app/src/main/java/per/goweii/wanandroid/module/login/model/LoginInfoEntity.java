package per.goweii.wanandroid.module.login.model;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import per.goweii.basic.core.base.BaseEntity;

public class LoginInfoEntity extends BaseEntity {
    private final String username;
    private final String password;

    public static LoginInfoEntity fromJson(String json) {
        try {
            JSONObject o = new JSONObject(json);
            String username = o.getString("username");
            String password = o.getString("password");
            return new LoginInfoEntity(username, password);
        } catch (JSONException e) {
            return null;
        }
    }

    public LoginInfoEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(username) || TextUtils.isEmpty(password);
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String toJson() {
        try {
            JSONObject o = new JSONObject();
            o.put("username", username);
            o.put("password", password);
            return o.toString();
        } catch (JSONException e) {
            return "";
        }
    }
}
