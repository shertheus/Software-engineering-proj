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

public class TerAdapter extends ArrayAdapter<String> {

    public TerAdapter(@NonNull Context context, int resource, @NonNull  List<String> objects) {
        super(context, resource, objects);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.my_refresh_textview,null);
            viewHolder = new ViewHolder();
            viewHolder.tv = view.findViewById(R.id.list_name);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv.setText(getItem(position));

        return view;
    }

    static class ViewHolder{
        TextView tv;

    }
}
