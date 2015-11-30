package com.example.administrator.kdsdemo01.model;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by 小 明 on 2015/11/18.
 */
public class MyVolley {
    private static MyVolley myInstance;
    private RequestQueue requestQueue;
    private static Context myContext;

    private  MyVolley(Context context){
        myContext = context;
        requestQueue = getRequestQueue();

    }

    public static synchronized MyVolley getInstance(Context context){
        if(myInstance==null){
            myInstance = new MyVolley(context);
        }
        return myInstance;
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue==null){
           requestQueue = Volley.newRequestQueue(myContext);
        }
        return requestQueue;
    }
}
