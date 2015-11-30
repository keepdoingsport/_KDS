package com.example.administrator.kdsdemo01.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.kdsdemo01.R;
import com.example.administrator.kdsdemo01.adapter.GymListAdapter;
import com.example.administrator.kdsdemo01.api.KdsApi;
import com.example.administrator.kdsdemo01.model.Gym;
import com.example.administrator.kdsdemo01.model.MyVolley;
import com.example.administrator.kdsdemo01.ui.activity.GymDetailActivity;
import com.example.administrator.kdsdemo01.widget.RecyclerItemClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2015/9/12.
 */
public class GymListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<Gym> mGymList= new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private GymListAdapter mAdapter;

    private JSONObject jsonObject;
    private Gym gym;
    private int id;
    private String name;
    private String info;
    private String photos;
    private String photos1;
    private String photos2;
    private String phone;
    private String address;

    private String url = KdsApi.getKdsGymList();
    private RequestQueue mrequest;

    protected JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray jsonArray) {

            try {
                for(int i=0;i<jsonArray.length();i++){
                    gym = new Gym();
                    jsonObject = jsonArray.getJSONObject(i);
                    id = jsonObject.getInt("id");
                    name = jsonObject.getString("name");
                    info = jsonObject.getString("info");
                    photos = jsonObject.getString("photos");
                    photos1 = jsonObject.getString("photos1");
                    photos2 = jsonObject.getString("photos2");
                    phone = jsonObject.getString("phone");
                    address = jsonObject.getString("address");
                    gym.setId(id);
                    gym.setName(name);
                    gym.setPhotos(photos);
                    gym.setPhoto1(photos1);
                    gym.setPhoto2(photos2);
                    gym.setPhone(phone);
                    gym.setAddress(address);
                    mGymList.add(gym);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    },new Response.ErrorListener(){
        @Override
        public void onErrorResponse(VolleyError error){
            Snackbar.make(getView(), "加载失败，请重试", Snackbar.LENGTH_LONG).show();
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mrequest = MyVolley.getInstance(this.getActivity().getApplicationContext()).getRequestQueue();

        //设置frame内的内容:下拉和list
        View view = inflater.inflate(R.layout.fragment_gymlist, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.cardList);
        //高度不变时提高性能
        mRecyclerView.setHasFixedSize(true);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //设置list的点击事件
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary);

        mrequest.start();
        mrequest.add(jsonArrayRequest);

        mAdapter = new GymListAdapter(getActivity(), mGymList);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onRefresh() {

        mrequest.start();
        mrequest.add(jsonArrayRequest);
    }

    //list的点击事件
    private RecyclerItemClickListener.OnItemClickListener onItemClickListener=new RecyclerItemClickListener.OnItemClickListener(){
        @Override
        public void onItemClick(View view, int position) {
            //点击的是哪个
            Gym gym=mAdapter.getGym(position);
            //把选中体育馆的信息保存
            Intent intent=new Intent(getActivity(), GymDetailActivity.class);
            intent.putExtra("gym",gym);

            //切换效果
            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                            view.findViewById(R.id.gym_cover), "transition_gym_img");

            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());

        }
    };
}
