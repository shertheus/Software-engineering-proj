package com.lyl.facerecognition.ui.Home;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.lyl.facerecognition.R;
import com.lyl.facerecognition.Utils.View.TableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StaticsFragment extends Fragment {
    private LineChart lineChart;
    private List<Integer> Cntlist = new ArrayList<>();
    private List<String> datedata = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);
        lineChart = (LineChart) root.findViewById(R.id.lineChart);
//        List<Integer> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++){
////            list.add(i);
//            datedata.add("2019-08-"+(i+1));
//        }
//        datedata.add("2019-08-"+(11));
//        initLineChart(list);
        if (Cntlist.size() > 0){
            initLineChart(Cntlist);
        }
        return root;
    }

    public void setCntlist(List<Integer> list){
        if (list.size() == 1){
            this.Cntlist.add(0);
            this.Cntlist.add(list.get(0));
        }
        else {
            this.Cntlist = list;
        }

    }

    public void setDatedata(List<String> list){
        if (list.size() == 1){
            String[] tmp = list.get(0).split("-");
            int d = Integer.parseInt(tmp[2]);
            this.datedata.add(tmp[0] + "-" + tmp[1] + "-" + (d-1));
            this.datedata.add(list.get(0));
            this.datedata.add(tmp[0] + "-" + tmp[1] + "-" + (d+1));
        }
        else {
            String[] tmp = list.get(list.size() - 1).split("-");
            int d = Integer.parseInt(tmp[2]);
            this.datedata = list;
            this.datedata.add(tmp[0] + "-" + tmp[1] + "-" + (d+1));
        }
    }

    private void initLineChart(final List<Integer> list) {
        //显示边界
        lineChart.setDrawBorders(false);
        //设置数据
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            entries.add(new Entry(i, (float) list.get(i)));
        }
        //一个LineDataSet就是一条线
        LineDataSet lineDataSet = new LineDataSet(entries, "");
        //线颜色
        lineDataSet.setColor(Color.parseColor("#F15A4A"));
        //线宽度
        lineDataSet.setLineWidth(1.6f);
        //不显示圆点
        lineDataSet.setDrawCircles(true);
        //线条平滑
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        //设置折线图填充
//        lineDataSet.setDrawFilled(true);
        LineData data = new LineData(lineDataSet);
        //无数据时显示的文字
        lineChart.setNoDataText("暂无数据");
        //折线图不显示数值
        data.setDrawValues(false);
        //得到X轴
        XAxis xAxis = lineChart.getXAxis();
        //设置X轴的位置（默认在上方)
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //设置X轴坐标之间的最小间隔
        xAxis.setGranularity(1f);
        //设置X轴的刻度数量，第二个参数为true,将会画出明确数量（带有小数点），但是可能值导致不均匀，默认（6，false）
        xAxis.setLabelCount(list.size() - 1, false);
        //设置X轴的值（最小值、最大值、然后会根据设置的刻度数量自动分配刻度显示）
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum((float) (list.size() - 1));
        //不显示网格线
        xAxis.setDrawGridLines(true);
        // 标签倾斜
        xAxis.setLabelRotationAngle(65);
        //设置X轴值为字符串
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return datedata.get((int) value);
            }
        });
        //得到Y轴
        YAxis yAxis = lineChart.getAxisLeft();
        YAxis rightYAxis = lineChart.getAxisRight();
        //设置Y轴是否显示
        rightYAxis.setEnabled(false); //右侧Y轴不显示
        //设置y轴坐标之间的最小间隔
        //不显示网格线
        yAxis.setDrawGridLines(true);
        //设置Y轴坐标之间的最小间隔
        yAxis.setGranularity(1);
        //设置y轴的刻度数量
        //+2：最大值n就有n+1个刻度，在加上y轴多一个单位长度，为了好看，so+2
        yAxis.setLabelCount(Collections.max(list) + 1, false);
        //设置从Y轴值
        yAxis.setAxisMinimum(0f);
        //+1:y轴多一个单位长度，为了好看
        yAxis.setAxisMaximum(Collections.max(list) + 1);

        //y轴
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int IValue = (int) value;
                return String.valueOf(IValue);
            }
        });
        //图例：得到Lengend
        Legend legend = lineChart.getLegend();
        //隐藏Lengend
        legend.setEnabled(false);
        //隐藏描述
        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);
        //折线图点的标记
        MyMarkerView mv = new MyMarkerView(requireContext());
        lineChart.setMarker(mv);
        //设置数据
        lineChart.setData(data);
        //图标刷新
        lineChart.invalidate();
    }

    public static class MyMarkerView extends MarkerView {

        private final TextView tvContent;

        public MyMarkerView(Context context) {
            super(context, R.layout.layout_markerview);//这个布局自己定义
            tvContent = (TextView) findViewById(R.id.tvContent);
        }

        //显示的内容
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            tvContent.setText(e.toString());
            super.refreshContent(e, highlight);
        }

        //标记相对于折线图的偏移量
        @Override
        public MPPointF getOffset() {
            return new MPPointF(-(getWidth() / 2), -getHeight());
        }

//        //时间格式化（显示今日往前30天的每一天日期）
//        public String format(float x)
//        {
//            CharSequence format = DateFormat.format("MM月dd日",
//                    System.currentTimeMillis()-(long) (30-(int)x)*24*60*60*1000);
//            return format.toString();
//        }
    }

}
