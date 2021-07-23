package com.lyl.facerecognition.Utils;

import android.widget.Toast;

import com.lyl.facerecognition.mycallback.c_user;
import com.lyl.facerecognition.mycallback.get_list;
import com.lyl.facerecognition.mycallback.get_records;
import com.lyl.facerecognition.ui.Home.userMsg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class userCon {
    private boolean timeout = false;
    public  ArrayList<String> timeList = new ArrayList<>();
    private String responseString = "";
    private ArrayList<userMsg> userList = new ArrayList<>();

    public void create(final userMsg um, final c_user callback) {
        new Thread(){
            @Override
            public void run() {
                try {
                    responseString = "";
                    sendCreate(um);
                    System.out.println(responseString);
                    if (timeout){
                        callback.failed("timeout");
                    }
                    else if (responseString.equals("success create")){
                        callback.success();
                    }
                    else {
                        callback.failed(responseString);
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void sendCreate(userMsg um) throws JSONException, IOException{
        URL url = new URL(API.home+API.create);
        //URL url = new URL("https://www.baidu.com");
        HttpURLConnection conn_create = (HttpURLConnection) url.openConnection();

        conn_create.setConnectTimeout(10000);
        conn_create.setReadTimeout(3000);
        conn_create.setRequestMethod("POST");
        try {
            DataOutputStream out = new DataOutputStream(conn_create.getOutputStream());

            out.writeBytes("userid="+ um.id +"&name="+um.username+"&photo="+um.photo+"&gender="+um.gender+"&birth="+um.birth);

            System.out.println(conn_create.getResponseCode());
            InputStream in = conn_create.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line);

            this.responseString = response.toString();
            System.out.println(response.toString());
        }
        catch (SocketTimeoutException e){
            timeout = true;
            e.printStackTrace();
        }
    }

    public void getMsg(final get_list callback, final int type) {
        new Thread(){
            @Override
            public void run() {
                try {
                    responseString = "";
                    get();
                    if (responseString.equals("200")){
                        callback.got(type);
                    }else {
                        callback.got_failed(type);
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private void get() throws JSONException, IOException {
        URL url = new URL(API.home+API.create);
        //URL url = new URL("https://www.baidu.com");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setConnectTimeout(10000);
        conn.setReadTimeout(100000);
        conn.setRequestMethod("GET");

        try {
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line);

            String getString = response.toString();
            JSONObject jsonObject = new JSONObject(getString);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject json = jsonArray.getJSONObject(i);
                userMsg um = new userMsg(json.get("userid").toString(), json.get("name").toString(), json.get("photo").toString(), json.get("gender").toString(), json.get("birth").toString());
                um.setLaster(json.get("latest").toString());
                userList.add(um);
            }
            responseString = conn.getResponseCode() + "";
//            System.out.println(response.toString());
        }catch (SocketTimeoutException e){
            e.printStackTrace();
        }
    }

    public ArrayList<userMsg> getUserList() {
        return userList;
    }

    public void deleteUser(final String username) {
        new Thread(){
            @Override
            public void run() {
                try {
                    responseString = "";
                    del(username);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void del(String userid) throws IOException {
        URL url = new URL(API.home+API.delete);
        //URL url = new URL("https://www.baidu.com");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setConnectTimeout(10000);
        conn.setReadTimeout(1000);
        conn.setRequestMethod("POST");

        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.writeBytes("userid="+userid);

        InputStream in = conn.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) response.append(line);

        this.responseString = response.toString();
        System.out.println(response.toString());
    }
    public String getResponseString(){
        return responseString;
    }
    public void getRecord(final String id, final get_records callback) {

        new Thread() {
            @Override
            public void run() {
                responseString = "";
                try {
                    conRes(id);
                    callback.got(timeList);
                } catch (IOException | JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
    private void conRes(String id) throws IOException, JSONException, ParseException {
        URL url = new URL(API.home + API.showrecord);
        //URL url = new URL("https://www.baidu.com");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setConnectTimeout(10000);
        conn.setReadTimeout(1000);
        conn.setRequestMethod("POST");

        System.out.println(id);
        /*DataOutputStream out = new DataOutputStream(conn.getOutputStream());

        out.writeBytes("userid=" + id);*/

        InputStream in = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder response = new StringBuilder();


        String line;
        while ((line = reader.readLine()) != null) response.append(line);

        String getString = response.toString();

        System.out.println(response.toString());
        JSONObject jsonObject = new JSONObject(getString);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject json = jsonArray.getJSONObject(i);
//            SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
//            Date dateTime = SDF.parse((String) json.get("intime"));
//            SimpleDateFormat Time3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String tmp = (String) json.get("intime");
//            System.out.println(tmp.substring(0, 10) +" " + tmp.substring(11, tmp.length() - 1));
            if(json.get("userid").toString().equals(id)) timeList.add(tmp.substring(0, 10) + " " + tmp.substring(11, tmp.length() - 4));
        }


       /* int MAX = 10,MIN = 1;
        Random r = new Random();
        int LL = r.nextInt(MAX - MIN + 1) + MIN;
        for (int i = 0; i < LL; i++) {
            int tt = r.nextInt(10);
           // System.out.println(i);
            Date date = randomDate("2019-01-01", "2019-01-31");
            @SuppressLint("SimpleDateFormat") String s = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(date);
            //System.out.println(s);
            timeList.add(s);
        }*/
        //System.out.println(response.toString());
    }
}
