package com.example.administrator.kdsdemo01.api;

/**
 * Created by Administrator on 2015/9/16.
 */

//接口

public final class KdsApi {
    public static final String KDS_GYM_LIST="http://keepdoingsport.sinaapp.com/primary.php";
    public static final String KDS_GYM_RECRUIT="http://keepdoingsport.sinaapp.com/recruits.php";//传入gymid，获得json数组
    public static final String KDS_USER_LOGIN="http://keepdoingsport.sinaapp.com/login.php";
    public static final String KDS_USER_REGISTER="http://keepdoingsport.sinaapp.com/register.php";
    private static final String SMS_KEY = "c1d96dbc6d39";
    private static final String SMS_SECRET = "d3676195673c891774eb63dcc0374414";
    public static String getKdsGymList(){
        return KDS_GYM_LIST;
    }
    public static String getSmsKey() {return SMS_KEY;}
    public static String getSmsSecret() {return SMS_SECRET;}
}
