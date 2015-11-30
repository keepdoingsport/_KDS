package com.example.administrator.kdsdemo01.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.kdsdemo01.R;
import com.example.administrator.kdsdemo01.api.KdsApi;
import com.example.administrator.kdsdemo01.model.MyVolley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 小 明 on 2015/11/10.
 */
public class RegisterActivity extends AppCompatActivity {
    private String key = KdsApi.getSmsKey();
    private String secret = KdsApi.getSmsSecret();
    CoordinatorLayout _container;
    EventHandler eh;
    private RequestQueue mrequest;
    private JSONObject params;
    private JsonObjectRequest jsonObjectRequest;
    private String username;
    private String password;
    private String againpass;
    private String userphone;

    private boolean _error;
    private boolean _succ;

    String regExp = "^1[3|4|5|8][0-9]\\d{8}$";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        SMSSDK.initSDK(this, key, secret);
        eh = new EventHandler(){
            public void afterEvent(int event, int result, Objects data){
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eh);

        _container = (CoordinatorLayout) findViewById(R.id.reg_coordinatorLayout);
        final EditText et_user = (EditText) findViewById(R.id.reg_user);
        final EditText et_pass = (EditText) findViewById(R.id.reg_pass);
        final EditText et_again = (EditText) findViewById(R.id.reg_again);
        final EditText et_phone = (EditText) findViewById(R.id.reg_phone);
        final Pattern pattern = Pattern.compile(regExp);

        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                userphone = et_phone.getText().toString();
            }
        });


        Button test_btn = (Button) findViewById(R.id.reg_test);
        test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Matcher matcher = pattern.matcher(userphone);
                if (matcher.matches()) {
                    SMSSDK.getVerificationCode("86", userphone);
                } else {
                    Snackbar.make(_container, userphone+"手机号有误", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        final Button reg_btn = (Button) findViewById(R.id.reg_reg);
        reg_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                final EditText et_code = (EditText) findViewById(R.id.reg_code);
                username = et_user.getText().toString();
                password = et_pass.getText().toString();
                againpass = et_again.getText().toString();
                userphone = et_phone.getText().toString();
                String code = et_code.getText().toString();
                if(userphone.isEmpty()||username.isEmpty()||password.isEmpty()||code.isEmpty())
                    Snackbar.make(_container, "请填写完整", Snackbar.LENGTH_LONG).show();
                else {
                    if(password.equals(againpass)){
                        register(username, password, userphone, code);
                    } else{
                        Snackbar.make(_container, "两次密码不匹配", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
        reg_btn.setClickable(false);

        final CheckBox cb = (CheckBox) findViewById(R.id.reg_check);
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb.isChecked()){
                    reg_btn.setClickable(true);
                }else reg_btn.setClickable(false);
            }
        });

        final Button reg_return = (Button) findViewById(R.id.reg_return);
        reg_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Back();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
                Back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
        }
    };

    public void register(String _username,String _password,String _userphone,String _code){
        String url = KdsApi.KDS_USER_REGISTER;
        params = new JSONObject();
        try {
            params.put("username",_username);
            params.put("password",_password);
            params.put("userphone",_userphone);
            params.put("code", _code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mrequest = MyVolley.getInstance(this.getApplicationContext()).getRequestQueue();
        mrequest.start();
        jsonObjectRequest = new JsonObjectRequest(url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    _error = jsonObject.getBoolean("error");
                    _succ = jsonObject.getBoolean("succ");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SMSSDK.unregisterEventHandler(eh);
                if(_error&&!_succ) Snackbar.make(_container, "验证码错误", Snackbar.LENGTH_LONG).show();
                else if(_succ && !_error)  Snackbar.make(_container, "注册成功", Snackbar.LENGTH_LONG).show();
                else if(!_succ &&!_error) Snackbar.make(_container,"手机号已存在", Snackbar.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        mrequest.add(jsonObjectRequest);
    }

    public void Back(){
        Intent intent = new Intent();
        intent.setClass(RegisterActivity.this,LoginActivity.class);
        RegisterActivity.this.startActivity(intent);
        finish();
    }
}
