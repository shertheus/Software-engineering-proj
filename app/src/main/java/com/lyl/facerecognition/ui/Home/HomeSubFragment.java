package com.lyl.facerecognition.ui.Home;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.lyl.facerecognition.R;

import java.util.ArrayList;
import java.util.List;

import com.lyl.facerecognition.TerminalActivity;
import com.lyl.facerecognition.Utils.Adapter.TerAdapter;
import com.lyl.facerecognition.Utils.terminalHandling;
import com.lyl.facerecognition.Utils.BitmapToBase64;
import com.lyl.facerecognition.Utils.BitmapUtil;
import com.lyl.facerecognition.Utils.Adapter.MyAdapter;
import com.lyl.facerecognition.Utils.View.RefreshListView;
import com.lyl.facerecognition.Utils.userCon;
import com.lyl.facerecognition.mycallback.get_list;

public class HomeSubFragment extends Fragment implements RefreshListView.OnRefreshListener, RefreshListView.OnLoadMoreListener, get_list {
    private String label;
    userCon uc = new userCon();
    terminalHandling tmh = new terminalHandling();
    private RefreshListView mListView;
    private MyAdapter mAdapter;
    private TerAdapter tAdapter;
    private String name = "";
    private List<userMsg> list_u = new ArrayList<>();
    private List<String> list_t = new ArrayList<>();
    private final static int REFRESH_COMPLETE = 0;
    private final static int LOAD_COMPLETE = 1;
    private final static int INIT_SUCC = 2;
    private final static int TER_SUCC = 3;
    private final static int TER_REFRESH = 4;
    private final static int TER_LOAD = 5;

    @SuppressLint("HandlerLeak")
    //传送刷新动作信息
    private final Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    mListView.setOnRefreshComplete();
                    mAdapter.notifyDataSetChanged();
                    break;
                case LOAD_COMPLETE:
                    mListView.setOnLoadMoreComplete();
                    mAdapter.notifyDataSetChanged();
                    break;
                case INIT_SUCC:
                    mListView.setAdapter(mAdapter);
                    break;
                case TER_SUCC:
                    mListView.setAdapter(tAdapter);
                    break;
                case TER_REFRESH:
                    mListView.setOnRefreshComplete();
                    tAdapter.notifyDataSetChanged();
                    break;
                case TER_LOAD:
                    mListView.setOnLoadMoreComplete();
                    tAdapter.notifyDataSetChanged();
                    break;
            }
        };
    };

    void init() {// init page information
        SharedPreferences preferences = getActivity().getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
        name = preferences.getString("admin_name", "");
        if (label.equals("用户")){
            uc.getMsg(HomeSubFragment.this, INIT_SUCC);
            list_u = uc.getUserList();
        }else {
            tmh.getTer(HomeSubFragment.this, name, INIT_SUCC);
        }
    }

    private void getaccess(int position){
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},11004);
            return;
        }
        if (label.equals("用户")){
            Intent intent = new Intent(getActivity(), UserActivity.class);
            userMsg msg = list_u.get(position-1);
            intent.putExtra("id", msg.id);
            intent.putExtra("name", msg.username);
            intent.putExtra("birth", msg.birth);
            intent.putExtra("gender", msg.gender);
            Bitmap bitmap = BitmapToBase64.base64ToBitmap(msg.photo);
            System.out.println(bitmap);
            BitmapUtil.saveBitmap2file(bitmap, "user_pic", requireContext());
            startActivity(intent);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_subhome, container, false);
        mListView = root.findViewById(R.id.ListView);
        init();
        mListView.setOnRefreshListener(this);
        mListView.setOnLoadMoreListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               getaccess(position);
            }
        });

        if (label.equals("用户")){
            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    new AlertDialog.Builder(requireContext()).setTitle("确定删除该用户信息吗")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.out.println(list_u.get(position-1).id);
                                    uc.deleteUser(list_u.get(position-1).id);
                                    list_u.remove(position-1);
                                    mAdapter.notifyDataSetChanged();

                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();

                    return true;
                }
            });
        }

        return root;
    }


    @Override
    public void onRefresh() {
        if (mAdapter != null){
            Toast.makeText(getContext(),"Wow onRefresh!!",Toast.LENGTH_SHORT).show();
            uc.getMsg(HomeSubFragment.this, REFRESH_COMPLETE);
            list_u.clear();
            list_u = uc.getUserList();
        }
        if (tAdapter != null){
            tmh.getTer(HomeSubFragment.this, name, REFRESH_COMPLETE);
            list_t.clear();
        }
    }

    @Override
    public void onLoadMore() {
        if (mAdapter != null){
            Toast.makeText(getContext(),"Wow onLoadMore complete!!",Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                       if (label.equals("用户")){
                           mHandler.sendEmptyMessage(LOAD_COMPLETE);
                       }else {
                           mHandler.sendEmptyMessage(TER_LOAD);
                       }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void setLabel(String s){
        label = s;
    }


    public void callRefresh() {
        if (label.equals("用户")){
            uc.getMsg(HomeSubFragment.this, REFRESH_COMPLETE);
            list_u.clear();
            list_u = uc.getUserList();
        }else {
            tmh.getTer(HomeSubFragment.this, name, REFRESH_COMPLETE);
            list_t.clear();
        }
    }

    @Override
    public void got(int type) {
        if (type == INIT_SUCC){
            if (label.equals("用户")){
                mAdapter = new MyAdapter(requireContext(), R.layout.single_textview, list_u);
                mHandler.sendEmptyMessage(INIT_SUCC);
            }else {
                list_t = tmh.getTerList();
                tAdapter = new TerAdapter(requireContext(), R.layout.single_textview, list_t);
                mHandler.sendEmptyMessage(TER_SUCC);
            }

        }else if (type == REFRESH_COMPLETE){
            if (label.equals("用户")){
                mHandler.sendEmptyMessage(REFRESH_COMPLETE);
            }else {
//                list_t.clear();
                list_t = tmh.getTerList();
                tAdapter = new TerAdapter(requireContext(), R.layout.single_textview, list_t);
                mHandler.sendEmptyMessage(TER_REFRESH);
            }
        }
    }

    @Override
    public void got_failed(int type) {
        Looper.prepare();
        if (type == REFRESH_COMPLETE){
//            mHandler.sendEmptyMessage(type);
            Toast.makeText(requireContext(),"超时"+label + type,Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(requireContext(),"管理员名下没有终端" + type,Toast.LENGTH_SHORT).show();
        }
        Looper.loop();
    }

}
