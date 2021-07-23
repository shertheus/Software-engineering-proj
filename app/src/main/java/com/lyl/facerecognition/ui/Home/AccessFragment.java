package com.lyl.facerecognition.ui.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lyl.facerecognition.R;
import com.lyl.facerecognition.mycallback.get_records;

import java.util.ArrayList;
import java.util.List;

public class AccessFragment extends Fragment implements get_records {
    private List<String> recordList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecordAdapter adapter;
    private final static int INIT_SUCC = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_access, container, false);
        recyclerView = root.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
//        init();
        adapter = new RecordAdapter(recordList);
        recyclerView.setAdapter(adapter);
        return root;
    }

    public void setList(List<String> list){
        recordList = list;
    }

    void init(){
        for (int i = 0; i < 20; i++){
            recordList.add(i + "");
        }
    }

    @Override
    public void got(List <String> list) {
        this.recordList = list;
    }

    @Override
    public void got_failed() {

    }

    class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
        private List<String> mRecordList;
        private Context context = getContext();

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView Record_time;

            public ViewHolder(View view) {
                super(view);
                Record_time = (TextView) view.findViewById(R.id.record_time);

            }

        }

        public RecordAdapter(List<String> List) {
            mRecordList = List;
        }

        @Override

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final String time = mRecordList.get(position);
            holder.Record_time.setText(time);
        }

        @Override
        public int getItemCount() {
            return mRecordList.size();
        }
    }
}
