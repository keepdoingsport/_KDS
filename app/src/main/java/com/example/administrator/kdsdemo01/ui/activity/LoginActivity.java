package com.example.administrator.kdsdemo01.ui.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.administrator.kdsdemo01.R;
import com.example.administrator.kdsdemo01.api.KdsApi;
import com.example.administrator.kdsdemo01.model.MyVolley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vincent_lbj on 2015/10/1.
 */

//登录界面

public class LoginActivity extends AppCompatActivity {
    private CoordinatorLayout container;
    private SharedPreferences preferences;
    private RequestQueue mrequest;
    private JsonObjectRequest jsonrequest;
    private JSONObject params;
    private String username;
    private String password;
    private boolean re;
    private String por;
    private String nickname;
    private long exitTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        container = (CoordinatorLayout) findViewById(R.id.log_coordinatorLayout);
        final EditText et_username = (EditText) findViewById(R.id.et_user);
        final EditText et_password = (EditText) findViewById(R.id.et_password);

        Button login_btn = (Button) findViewById(R.id.log_log);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = et_username.getText().toString();
                password = et_password.getText().toString();
                if(username.isEmpty()||password.isEmpty())
                    Snackbar.make(container, "账号密码不能为空", Snackbar.LENGTH_LONG).show();
                else{
                    login(username, password);
                    preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("islogin", true);
                    editor.commit();
                }
            }
        });

        Button reg_btn = (Button) findViewById(R.id.log_reg);
        reg_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this,RegisterActivity.class);
                    LoginActivity.this.startActivity(intent);
                    finish();
                }
            }
        );

        //显示密码
        final CheckBox cb = (CheckBox) findViewById(R.id.show_pass);
        final EditText pass = (EditText) findViewById(R.id.et_password);
        cb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(cb.isChecked()){
                    pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 3000) {
                Snackbar.make(container, "再按一次退出", Snackbar.LENGTH_LONG).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void login(String user, String password) {
        String url = KdsApi.KDS_USER_LOGIN;
        params = new JSONObject();
        try {
            params.put("username", user);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mrequest = MyVolley.getInstance(this.getApplicationContext()).getRequestQueue();
        mrequest.start();
        jsonrequest = new JsonObjectRequest(url, params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            re = jsonObject.getBoolean("re");
                            por = jsonObject.getString("por");
                            nickname = jsonObject.getString("nick");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (re) {
                            preferences = getSharedPreferences("user",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("por",por);
                            editor.putString("nickname", nickname);
                            editor.commit();
                            Intent intent = new Intent();
                            intent.setClass(LoginActivity.this, MainActivity.class);
                            LoginActivity.this.startActivity(intent);
                            finish();
                        } else {
                            Snackbar.make(container, "账号密码错误", Snackbar.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                    }
                });
        mrequest.add(jsonrequest);
    }

}
