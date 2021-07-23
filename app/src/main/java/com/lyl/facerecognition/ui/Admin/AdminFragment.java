package com.lyl.facerecognition.ui.Admin;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.lyl.facerecognition.LoginActivity;
import com.lyl.facerecognition.MainActivity;
import com.lyl.facerecognition.Utils.ItemGroup;
import com.lyl.facerecognition.Utils.View.RoundImageView;
import com.lyl.facerecognition.Utils.TitleLayout;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lyl.facerecognition.R;
import com.lyl.facerecognition.WelcomeActivity;

import static android.app.Activity.RESULT_OK;

public class AdminFragment extends Fragment {

    private ItemGroup ig_id,ig_name,ig_gender,ig_region,ig_brithday;
    private User mUser = User.getInstance();
    private LinearLayout ll_portrait;
    private SharedPreferences mPrefer;
    private SharedPreferences.Editor editor;

    private RoundImageView ri_portrati;
    private Uri imageUri;  //拍照功能的地址
    private static final int TAKE_PHOTO = 1;
    private static final int FROM_ALBUMS = 2;
    private PopupWindow popupWindow;

    private static final int EDIT_NAME = 3;
    private TitleLayout titleLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_admin, container, false);

        ig_id = root.findViewById(R.id.ig_id);
        ig_name = root.findViewById(R.id.ig_name);
        ig_gender = root.findViewById(R.id.ig_gender);
        ig_region = root.findViewById(R.id.ig_region);
        ig_brithday = root.findViewById(R.id.ig_brithday);
        ll_portrait = root.findViewById(R.id.ll_portrait);
        ri_portrati = root.findViewById(R.id.ri_portrait);
        titleLayout = root.findViewById(R.id.tl_title);
        Button siginout_btn = root.findViewById(R.id.signout);

        mPrefer = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        siginout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("isout", true);
                startActivity(intent);
                getActivity().finish();
            }
        });
        ll_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_popup_windows();
            }
        });

        //设置点击保存的逻辑
        titleLayout.getTextView_forward().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"save", Toast.LENGTH_SHORT).show();
            }
        });

        initInfo();
        return root;
    }

    //从数据库中初始化数据并展示
    private void initInfo(){
        mPrefer = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
//        LoginUser loginUser = LoginUser.getInstance();
        ig_id.getContentEdt().setText(mPrefer.getString("name", "id"));  //ID是int，转string
        ig_name.getContentEdt().setText("Admin");
        ig_gender.getContentEdt().setText("male");
        ig_region.getContentEdt().setText("China");
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        ig_brithday.getContentEdt().setText(formatter.format(date));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap)bundle.get("data");
            ri_portrati.setImageBitmap(bitmap);
        } else if(requestCode == FROM_ALBUMS && resultCode == RESULT_OK){
            ContentResolver resolver = getActivity().getContentResolver();
            try {
                imageUri = data.getData(); // 获得图片的uri
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, imageUri);
                ri_portrati.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    //展示修改头像的选择框，并设置选择框的监听器
    private void show_popup_windows(){
        RelativeLayout layout_photo_selected = (RelativeLayout) getLayoutInflater().inflate(R.layout.photo_select,null);
        if(popupWindow==null){
            popupWindow = new PopupWindow(layout_photo_selected, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        }
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        //显示popupwindows
        popupWindow.showAtLocation(layout_photo_selected, Gravity.CENTER, 0, 0);
        //设置监听器
        TextView take_photo =  (TextView) layout_photo_selected.findViewById(R.id.take_photo);
        TextView from_albums = (TextView)  layout_photo_selected.findViewById(R.id.from_albums);
        LinearLayout cancel = (LinearLayout) layout_photo_selected.findViewById(R.id.cancel);
        //拍照按钮监听
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(popupWindow != null && popupWindow.isShowing()) {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(intent, TAKE_PHOTO);
                    //去除选择框
                    popupWindow.dismiss();
                }
            }
        });
        //相册按钮监听
        from_albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //申请权限
                if(ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    //打开相册
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, FROM_ALBUMS);
                }
                //去除选择框
                popupWindow.dismiss();
            }
        });
        //取消按钮监听
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow.update();
    }
}