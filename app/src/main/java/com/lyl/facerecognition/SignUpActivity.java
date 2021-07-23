package com.lyl.facerecognition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lyl.facerecognition.R;
import com.lyl.facerecognition.Utils.Login;
import com.lyl.facerecognition.mycallback.inter_signup;

import org.json.JSONException;

import java.io.IOException;

public class SignUpActivity extends AppCompatActivity implements inter_signup{
    private Button signup;
    private EditText mUsername, mPassword;
    private final Login login = new Login();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signup = findViewById(R.id.signBtn);
        mUsername = findViewById(R.id.signName);
        mPassword = findViewById(R.id.signPass);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mUsername.getText().toString();
                String pass = mPassword.getText().toString();
                login.setKey(pass);
                login.setUserName(name);
                try {
                    login.reg(SignUpActivity.this);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void sign_up() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void failed() {
        Looper.prepare();
        Toast.makeText(SignUpActivity.this, login.getResponseString(), Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (SignUpActivity.this.getCurrentFocus() != null) {
                if (SignUpActivity.this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(SignUpActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
