package com.lyl.facerecognition.ui.Home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.lyl.facerecognition.QrCodeActivity;
import com.lyl.facerecognition.R;
import com.lyl.facerecognition.Utils.BitmapToBase64;
import com.lyl.facerecognition.Utils.Adapter.PagerAdapter;
import com.lyl.facerecognition.Utils.userCon;
import com.lyl.facerecognition.mycallback.c_user;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import com.lyl.facerecognition.Utils.terminalHandling;
import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements c_user {
    private TabLayout tabLayout;
    private Button button;
    AlertDialog dialog;
    private userMsg msg;
    private String date = "";
    private Bitmap mBitmap;
    private static final int TAKE_PHOTO = 1;
    private static final int FROM_ALBUMS = 2;
    private ViewPager viewPager;
    private static List<String> datas = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private PagerAdapter adapter;
    public static boolean flag = false;
    HomeSubFragment hu = new HomeSubFragment();
    HomeSubFragment ht = new HomeSubFragment();
    ImageView user_im;

    public static final int REQ_QR_CODE = 11002; // // 打开扫描界面请求码
    public static final int REQ_PERM_CAMERA = 11003; // 打开摄像头
    public static final int REQ_PERM_EXTERNAL_STORAGE = 11004; // 读写文件

    public static final String INTENT_EXTRA_KEY_QR_SCAN = "qr_scan_result";
    @SuppressLint("HandlerLeak")
    //传送刷新动作信息
    private final Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                adapter.notifyDataSetChanged();
            }
        };
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initDatas();
        flag = false;
        tabLayout = root.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("tab"));
        viewPager = root.findViewById(R.id.viewPager);
        button = root.findViewById(R.id.qr_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startQrCode();
                showPopupMenu(v);
            }
        });


        try {
            initViews();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return root;
    }

    private void initDatas() {
        if (!flag){
            datas.clear();
            datas.add("用户");
            datas.add("终端");
        }
    }
    public void  initViews() throws InterruptedException {
        //从数据库获取新闻title信息

        if (datas.size() > 4){
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        if (datas.size() == 0){
            Toast.makeText(getContext(),"take the tags back please :(",Toast.LENGTH_SHORT).show();
        }

        //循环注入标签
        fragments.clear();
        hu.setLabel("用户");
        ht.setLabel("终端");
        fragments.add(hu);
        fragments.add(ht);
        //tabLayout.addOnTabSelectedListener((TabLayout.BaseOnTabSelectedListener) this);
        adapter = new PagerAdapter(getChildFragmentManager(), datas, fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    // 开始扫码
    private void startQrCode() {
        // 申请相机权限
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA},REQ_PERM_CAMERA);
            return;
        }
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQ_PERM_EXTERNAL_STORAGE);
            return;
        }
        // 二维码扫码
        Intent intent = new Intent(getActivity(), QrCodeActivity.class);
        startActivityForResult(intent,REQ_QR_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (requestCode == REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("SCAN_RESULT");
//            TODO 二维码结果在下方
//            Toast.makeText(getContext(), scanResult, Toast.LENGTH_LONG).show();
            terminalHandling tmh = new terminalHandling();
            SharedPreferences preferences = getActivity().getSharedPreferences("adminInfo", Context.MODE_PRIVATE);
            String name = preferences.getString("admin_name", "");
            tmh.sendCode(scanResult, name, HomeFragment.this);
            //将扫描出的信息显示出来
        }else if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            mBitmap = (Bitmap)bundle.get("data");
            Bitmap  bitmap = Bitmap.createScaledBitmap(mBitmap,360, 480, true);
            user_im.setImageBitmap(bitmap);
        } else if(requestCode == FROM_ALBUMS && resultCode == RESULT_OK){
            ContentResolver resolver = getActivity().getContentResolver();
            try {
                Uri imageUri = data.getData(); // 获得图片的uri
                mBitmap = MediaStore.Images.Media.getBitmap(resolver, imageUri);
                Bitmap  bitmap = Bitmap.createScaledBitmap(mBitmap,360, 480, true);
                user_im.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERM_CAMERA:
                // 摄像头权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                } else {
                    // 被禁止授权
                    Toast.makeText(getContext(), "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_LONG).show();
                }
                break;
            case REQ_PERM_EXTERNAL_STORAGE:
                // 文件读写权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                } else {
                    // 被禁止授权
                    Toast.makeText(getContext(), "请至权限中心打开本应用的文件读写权限", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.creat_qr, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                if (item.getTitle().equals("添加用户")) {
                    createDialog();
                } else {
                    startQrCode();
                }
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
//                Toast.makeText(getContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
            }
        });

        popupMenu.show();
    }

    public void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        dialog = builder.create();
        View dialogView = View.inflate(requireContext(), R.layout.createuser_dialog, null);
        dialog.setView(dialogView);
        dialog.show();

        final EditText et_name = dialogView.findViewById(R.id.et_name);
        final TextView tv_date = dialogView.findViewById(R.id.dateTv);
        final Button btn_img = dialogView.findViewById(R.id.btn_img);
        final RadioButton men = dialogView.findViewById(R.id.sex_man);
        user_im = dialogView.findViewById(R.id.creeate_img);
        final Button btn_confirm = dialogView.findViewById(R.id.btn_confirm);
        final Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);

        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("ResourceType") DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), 2);
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        int m = month + 1;
                        tv_date.setText(year + "-" + m + "-" + dayOfMonth);
                        date = year + "-" + m + "-" + dayOfMonth;
                    }
                });
                datePickerDialog.show();
            }
        });

        btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_popup_pic(v);
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_name.getText().toString().equals("")){
                    Toast.makeText(getContext(), "用户名不能为空", Toast.LENGTH_SHORT).show();
                }else if (date.equals("")){
                    Toast.makeText(getContext(), "生日不能为空", Toast.LENGTH_SHORT).show();
                }else if (user_im.getDrawable() == null || mBitmap == null){
                    Toast.makeText(getContext(), "用户图片不能为空", Toast.LENGTH_SHORT).show();
                }else {
                    String a = BitmapToBase64.bitmapToBase64(mBitmap);
                    String gender = men.isChecked() ? "male":"female";

                    try {
                        msg = new userMsg(et_name.getText().toString(), et_name.getText().toString(), URLEncoder.encode(a, "utf-8"), gender, date);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    userCon uc = new userCon();

                    uc.create(msg, HomeFragment.this);

                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
    private void setTakePhoto(){
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA},REQ_PERM_CAMERA);
            return;
        }
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQ_PERM_EXTERNAL_STORAGE);
            return;
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, TAKE_PHOTO);
    }
    private void setFromAlbums(){
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA},REQ_PERM_CAMERA);
            return;
        }
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQ_PERM_EXTERNAL_STORAGE);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, FROM_ALBUMS);
    }

    //展示修改头像的选择框，并设置选择框的监听器
    private void show_popup_pic(View view){
        final PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.creat_user, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                if (item.getTitle().equals("拍照")) {
                    setTakePhoto();

                }else if (item.getTitle().equals("从图库选择")){
                    setFromAlbums();
                }else{
                    popupMenu.dismiss();
                }
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
//                Toast.makeText(getContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
            }
        });

        popupMenu.show();
    }

    @Override
    public void success() {
        dialog.dismiss();
        hu.callRefresh();
    }

    @Override
    public void failed(String s) {
        Looper.prepare();
        Toast.makeText(getContext(), s+ "用户创建失败请重试", Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    @Override
    public void sendQRr() {
        ht.callRefresh();
    }

    @Override
    public void sendFail(String text) {
        Looper.prepare();
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
