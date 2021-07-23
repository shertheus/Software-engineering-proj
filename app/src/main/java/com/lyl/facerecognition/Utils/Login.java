package com.lyl.facerecognition.Utils;

import com.lyl.facerecognition.mycallback.inter_login;
import com.lyl.facerecognition.mycallback.inter_signup;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login {
    private String userName;
    private String key;
    private String responseString = "";
    public void reg(final inter_signup callback) throws IOException, JSONException {
        new Thread(){
            @Override
            public void run() {
                try {
                    responseString = "";
                    sign_up();
                    if (responseString.equals("success sign_up")){
                        callback.sign_up();
                    }else {
                        callback.failed();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private void sign_up() throws JSONException, IOException {
        System.out.println(111);


        URL url = new URL(API.home+API.sign_up);
        //URL url = new URL("https://www.baidu.com");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setConnectTimeout(10000);
        conn.setReadTimeout(1000);
        conn.setRequestMethod("POST");
        System.out.println(222);

        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.writeBytes("username="+this.userName+"&password="+this.key);

        if (conn.getResponseCode() < 400){
            InputStream in = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line);

            this.responseString = response.toString();
            System.out.println(response.toString());
        }else {
            System.out.println("failed get inputstream error" + conn.getResponseCode());
        }
    }


    public void log(final inter_login callback, final boolean auto, final boolean saving) {
        new Thread(){
            @Override
            public void run() {
                try {
                    responseString = "";
                    login();
                    if(responseString.equals("success login")){
                        callback.getpass(auto, saving);
                    }else {
                        callback.failed();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void login() throws JSONException, IOException {


        URL url = new URL(API.home+API.login);
        //URL url = new URL("http://www.baidu.com");
        System.out.println(111);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(1000);
        conn.setRequestMethod("POST");
        System.out.println(222);

        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.writeBytes("username="+this.userName+"&password="+this.key);


        if (conn.getResponseCode() < 400){
            InputStream in = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line);

            this.responseString = response.toString();
            System.out.println(response.toString());
        }else {
            System.out.println("failed get inputstream error" + conn.getResponseCode());
        }
    }



    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getResponseString() {
        return responseString;
    }
}
