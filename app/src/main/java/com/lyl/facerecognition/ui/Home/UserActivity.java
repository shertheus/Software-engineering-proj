package com.lyl.facerecognition.ui.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.lyl.facerecognition.R;
import com.lyl.facerecognition.Utils.Adapter.PagerAdapter;
import com.lyl.facerecognition.Utils.BitmapUtil;
import com.lyl.facerecognition.Utils.userCon;
import com.lyl.facerecognition.mycallback.get_records;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity implements get_records {
    private ImageView imageView;
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private AccessFragment af = new AccessFragment();
    private StaticsFragment sf = new StaticsFragment();
    private static List<String> datas = new ArrayList<>();
//    private static List<String> records = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private TextView tv_id, tv_name, tv_birth, tv_gender;
    private TabLayout tabLayout;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    adapter = new PagerAdapter(getSupportFragmentManager(), datas, fragments);
                    viewPager.setAdapter(adapter);
                    tabLayout.setupWithViewPager(viewPager);
                    adapter.notifyDataSetChanged();
            }
        };
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        imageView = findViewById(R.id.u_img);
        tv_id = findViewById(R.id.u_id);
        tv_gender = findViewById(R.id.u_gender);
        tv_name = findViewById(R.id.u_name);
        tv_birth = findViewById(R.id.u_birth);
        tabLayout = findViewById(R.id.charts_tabLayout);
        viewPager = findViewById(R.id.acces_vp);

        Intent intent = getIntent();
        init(intent);
    }
    private void init(Intent intent){
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String birth = intent.getStringExtra("birth");
        String gender = intent.getStringExtra("gender");

        tv_id.setText(id);
        tv_name.setText(name);
        tv_gender.setText(gender);
        tv_birth.setText(birth);

        Bitmap bitmap = BitmapUtil.getBitmapFromFile("user_pic" , UserActivity.this);
        bitmap = Bitmap.createScaledBitmap(bitmap,360, 480, true);
        imageView.setImageBitmap(bitmap);

        tabLayout.addTab(tabLayout.newTab().setText("记录"));
        tabLayout.addTab(tabLayout.newTab().setText("统计"));
        datas.clear();
        datas.add("记录");
        datas.add("统计");

        userCon uc = new userCon();
        uc.getRecord(id, UserActivity.this);
    }

    private my_turple parseList(List<String> list){
        String fir = list.get(0).split(" ")[0];
        int tmp = 0;
        List<String> date = new ArrayList<>();
        List<Integer> cnt  = new ArrayList<>();
        date.add(fir);
        for (String str : list){
            String[] l = str.split(" ");
            if (l[0].equals(fir)){
                tmp++;
            }else {
                cnt.add(tmp);
                tmp = 1;
                fir = l[0];
                date.add(fir);
            }
        }
        cnt.add(tmp);
        return new my_turple(date, cnt);
    }

    @Override
    public void got(List<String> list) {

        af.setList(list);
        fragments.clear();
        fragments.add(af);

        if (list.size() != 0){
            my_turple turple = parseList(list);
            sf.setCntlist(turple.cnt);
            sf.setDatedata(turple.date);
        }

        fragments.add(sf);
        adapter = new PagerAdapter(getSupportFragmentManager(), datas, fragments);
        mHandler.sendEmptyMessage(1);
    }

    @Override
    public void got_failed() {
        System.out.println("record failed");
    }
    static class my_turple{
        public List<String> date;
        public List<Integer> cnt;
        public my_turple(List<String> date, List<Integer> cnt){
            this.date = date;
            this.cnt = cnt;
        }
    }
}
