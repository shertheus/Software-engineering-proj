package com.lyl.facerecognition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.lyl.facerecognition.Utils.Login;
import com.journeyapps.barcodescanner.BarcodeView;
import org.json.JSONException;
import com.lyl.facerecognition.mycallback.inter_login;

import java.io.IOException;
public class LoginActivity extends AppCompatActivity implements inter_login{
    private EditText mUsername;
    private EditText mPassWord;
    CheckBox mCheckboxA;
    CheckBox mCheckboxS;
    private SharedPreferences mPrefer;
    private SharedPreferences.Editor editor;
    private Login login = new Login();
//    private BarcodeView barcodeView;
    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button mButtonIn = findViewById(R.id.mSignIn);
        Button mButtonUp = findViewById(R.id.mSignUp);
        mUsername = findViewById(R.id.mUserName);
        mPassWord = findViewById(R.id.mPassWord);
        mCheckboxA = findViewById(R.id.mAuto);
        mCheckboxS = findViewById(R.id.mSave);

        Intent intent= this.getIntent();
        boolean isout = intent.getBooleanExtra("isout", true);

        mButtonIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPass();
            }
        });
        mButtonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this,"注册",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
//                finish();
            }
        });
        mPrefer = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = mPrefer.edit();

        if (!isout){
            boolean mAuto = mPrefer.getBoolean("auto", false);
            boolean mSaving = mPrefer.getBoolean("saving", false);
            if (mSaving){
                mUsername.setText(mPrefer.getString("name", ""));
                mCheckboxS.setChecked(true);
            }
            if (mAuto){
                mUsername.setText(mPrefer.getString("name", ""));
                mPassWord.setText(mPrefer.getString("pass", ""));
                mCheckboxA.setChecked(true);
                checkPass();
            }
        }
    }

    private void checkPass() {//check password and username
        String password;
        String username;
        password = mPassWord.getText().toString();
        username = mUsername.getText().toString();
        login.setKey(password);
        login.setUserName(username);
        new Thread(new Runnable() {
            @Override
            public void run() {
                login.log(LoginActivity.this, mCheckboxA.isChecked(), mCheckboxS.isChecked());
            }
        }).start();
    }

    @Override
    public void getpass(boolean auto, boolean saving) {
        editor.putString("name", mUsername.getText().toString());
        System.out.println(mUsername.getText().toString());
        if(auto){
            editor.putString("pass", mPassWord.getText().toString());
            editor.putBoolean("auto", true);
            System.out.println("auto");
        }
        if (saving){
            editor.putBoolean("saving", true);
            System.out.println("saving");
        }
        editor.apply();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("admin_name", mUsername.getText().toString());
        startActivity(intent);
        finish();
    }

    @Override
    public void failed() {
        Looper.prepare();
        Toast.makeText(LoginActivity.this, login.getResponseString(), Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (LoginActivity.this.getCurrentFocus() != null) {
                if (LoginActivity.this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(),
                               InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
