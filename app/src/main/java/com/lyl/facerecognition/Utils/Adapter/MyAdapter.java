package com.lyl.facerecognition.Utils.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.lyl.facerecognition.R;
import com.lyl.facerecognition.Utils.BitmapToBase64;
import com.lyl.facerecognition.ui.Home.userMsg;

import java.util.List;

public class MyAdapter extends ArrayAdapter<userMsg> {

    public MyAdapter(@NonNull Context context, int resource, @NonNull  List<userMsg> objects) {
        super(context, resource, objects);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        userMsg msg = getItem(position);
        ViewHolder viewHolder;
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.my_refresh_textview,null);
            viewHolder = new ViewHolder();
            viewHolder.tv = view.findViewById(R.id.list_name);
            viewHolder.tv2 = view.findViewById(R.id.list_id);
            viewHolder.tv3 = view.findViewById(R.id.list_gender);
            viewHolder.img = view.findViewById(R.id.list_img);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv.setText(msg.id);
        viewHolder.tv2.setText(msg.username);
        viewHolder.tv3.setText(msg.gender);
        viewHolder.img.setImageBitmap(BitmapToBase64.base64ToBitmap(msg.photo));
        return view;
    }

    static class ViewHolder{
        TextView tv;
        TextView tv2;
        TextView tv3;
        ImageView img;
    }
}
