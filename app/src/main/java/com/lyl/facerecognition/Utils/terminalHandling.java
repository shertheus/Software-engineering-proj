package com.lyl.facerecognition.Utils;

import com.lyl.facerecognition.mycallback.c_user;
import com.lyl.facerecognition.mycallback.get_list;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class terminalHandling {
    private ArrayList<String> terList = new ArrayList<>();
    public String responseString = "";
    public ArrayList<String> getTerList(){
        return this.terList;
    }

    public void getTer(final get_list callback, final String nameNow, final int type){
        new Thread() {
            @Override
            public void run() {
                try {
                    responseString = "";
                    getIt(callback, nameNow, type);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void getIt(final get_list callback, String nameNow, int type) throws JSONException, IOException{
        URL url = new URL(API.home + API.editnova);
        //URL url = new URL("https://www.baidu.com");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setConnectTimeout(10000);
        conn.setReadTimeout(1000);
        conn.setRequestMethod("GET");
        System.out.println(222);

        InputStream in = conn.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) response.append(line);


        String getString = response.toString();

        System.out.println(getString);
        JSONObject jsonObject = new JSONObject(getString);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            //System.out.println(json.get("novaid").toString());
            if(json.get("name").toString().equals(nameNow))//System.out.println(json.get("novaid").toString());
                terList.add(json.get("novaid").toString());
        }
        this.responseString = response.toString();
        System.out.println(conn.getResponseCode());
        if (conn.getResponseCode() == 200){
            callback.got(type);
        }else {
            callback.got_failed(-1);
        }
    }

    public void sendCode(final String code, final String id, final c_user callback){
        new Thread() {
            @Override
            public void run() {
                try {
                    responseString = "";
                    sendIt(code,id, callback);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void sendIt(String code,String id, final c_user callback) throws JSONException, IOException{
        URL url = new URL(API.home + API.editnova);
        //URL url = new URL("https://www.baidu.com");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setConnectTimeout(10000);
        conn.setReadTimeout(1000);
        conn.setRequestMethod("POST");

        JSONObject object = new JSONObject();
        object.put("name",id);
        object.put("novaid",code);

        System.out.println(object.toString());

        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.writeBytes(object.toString());


        InputStream in = conn.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) response.append(line);

        this.responseString = response.toString();
        System.out.println(conn.getResponseCode());
        System.out.println(responseString);
        if (responseString.equals("success")){
            callback.sendQRr();
        }else {
            callback.sendFail(responseString +  " " + code + " " + id);
        }
    }
}
