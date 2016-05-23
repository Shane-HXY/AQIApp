package com.bishe.aqidemo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.bishe.aqidemo.R;
import com.bishe.aqidemo.model.MeasureData;
import com.bishe.aqidemo.model.WeatherData;
import com.bishe.aqidemo.util.HttpUtil;
import com.bishe.aqidemo.util.Utility;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

/**
 * Created by huangxiangyu on 16/4/29.
 * In AQIDemo
 */
public class WeatherItemActivity extends AppCompatActivity {
    //private LinearLayout chart;
    private LineChart lineChart;
    private GraphicalView chartView;
    private WeatherData weatherData;
    private List<MeasureData> measureDataList;

    String nodeName;
    String timeSpan;
    String[] dateFormat = new String[]{"天", "周", "月"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_detail);
        //chart = (LinearLayout) findViewById(R.id.chart);
        lineChart = (LineChart) findViewById(R.id.list_chart);
        Intent intent = getIntent();
        weatherData = (WeatherData) intent.getSerializableExtra("WeatherData");
//        Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("折线图");
        }
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        nodeName = weatherData.getNodeName();
        timeSpan = "month";
        String url = "http://10.0.2.2:8080/AqiWeb/detailData?nodeName=" + nodeName + "&timeSpan=" + timeSpan;
        new HttpUtil().runOkHttpGet(new OkHttpClient(), url, handler, 0);
        //showChart();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String chooseFormat(String[] format, String timeSpan) {
        int i;
        switch (timeSpan) {
            case "day":
                i = 0;
                break;
            case "week":
                i = 1;
                break;
            case "month":
                i = 2;
                break;
            default:
                i = 0;
                break;
        }
        return format[i];
    }

    private void showChart() {
        XYMultipleSeriesDataset mDataSet = getDataSet();
        XYMultipleSeriesRenderer mRefender = getRefender();
        chartView = ChartFactory.getLineChartView(this, mDataSet, mRefender);
        //chart.addView(chartView);
    }

    private XYMultipleSeriesRenderer getRefender() {
        /*描绘器，设置图表整体效果，比如x,y轴效果，缩放比例，颜色设置*/
        XYMultipleSeriesRenderer seriesRenderer=new XYMultipleSeriesRenderer();

        seriesRenderer.setChartTitleTextSize(60);//设置图表标题的字体大小(图的最上面文字)
        seriesRenderer.setMargins(new int[] { 50, 50, 20, 20 });//设置外边距，顺序为：上左下右
        //坐标轴设置
        seriesRenderer.setAxisTitleTextSize(40);//设置坐标轴标题字体的大小
        seriesRenderer.setYAxisMin(0);//设置y轴的起始值
        seriesRenderer.setYAxisMax(500);//设置y轴的最大值
        seriesRenderer.setXAxisMin(0.5);//设置x轴起始值
        seriesRenderer.setXAxisMax(7.5);//设置x轴最大值
        seriesRenderer.setXTitle("日期");//设置x轴标题
        seriesRenderer.setYTitle("℃");//设置y轴标题
        //颜色设置
        seriesRenderer.setApplyBackgroundColor(true);//是应用设置的背景颜色
        seriesRenderer.setLabelsColor(0xFF85848D);//设置标签颜色
        seriesRenderer.setBackgroundColor(Color.argb(100, 255, 231, 224));//设置图表的背景颜色
        //缩放设置
        seriesRenderer.setZoomButtonsVisible(false);//设置缩放按钮是否可见
        seriesRenderer.setZoomEnabled(false); //图表是否可以缩放设置
        seriesRenderer.setZoomInLimitX(7);
//      seriesRenderer.setZoomRate(1);//缩放比例设置
        //图表移动设置
        seriesRenderer.setPanEnabled(false);//图表是否可以移动

        //legend(最下面的文字说明)设置
//      seriesRenderer.setShowLegend(true);//控制legend（说明文字 ）是否显示
//      seriesRenderer.setLegendHeight(80);//设置说明的高度，单位px
//      seriesRenderer.setLegendTextSize(16);//设置说明字体大小
        //坐标轴标签设置
        seriesRenderer.setLabelsTextSize(30);//设置标签字体大小
        seriesRenderer.setXLabelsAlign(Paint.Align.CENTER);
        seriesRenderer.setYLabelsAlign(Paint.Align.LEFT);
        seriesRenderer.setXLabels(0);//显示的x轴标签的个数
        seriesRenderer.addXTextLabel(1, "6/24");//针对特定的x轴值增加文本标签
        seriesRenderer.addXTextLabel(2, "6/25");
        seriesRenderer.addXTextLabel(3, "6/26");
        seriesRenderer.addXTextLabel(4, "6/27");
        seriesRenderer.addXTextLabel(5, "6/28");
        seriesRenderer.addXTextLabel(6, "6/29");
        seriesRenderer.addXTextLabel(7, "今天");
        seriesRenderer.setPointSize(8);//设置坐标点大小

        seriesRenderer.setMarginsColor(Color.WHITE);//设置外边距空间的颜色
        seriesRenderer.setClickEnabled(false);
        seriesRenderer.setChartTitle(nodeName + "最近一" + chooseFormat(dateFormat, timeSpan) + "空气质量变化趋势图");

        /*某一组数据的描绘器，描绘该组数据的个性化显示效果，主要是字体跟颜色的效果*/
        XYSeriesRenderer xySeriesRenderer1=new XYSeriesRenderer();
        xySeriesRenderer1.setAnnotationsColor(0xFFFF0000);//设置注释（注释可以着重标注某一坐标）的颜色
        xySeriesRenderer1.setAnnotationsTextAlign(Paint.Align.CENTER);//设置注释的位置
        xySeriesRenderer1.setAnnotationsTextSize(12);//设置注释文字的大小
        xySeriesRenderer1.setPointStyle(PointStyle.CIRCLE);//坐标点的显示风格
        xySeriesRenderer1.setPointStrokeWidth(3);//坐标点的大小
        xySeriesRenderer1.setColor(0xFFF46C48);//表示该组数据的图或线的颜色
        xySeriesRenderer1.setDisplayChartValues(false);//设置是否显示坐标点的y轴坐标值
        xySeriesRenderer1.setChartValuesTextSize(12);//设置显示的坐标点值的字体大小

        /*某一组数据的描绘器，描绘该组数据的个性化显示效果，主要是字体跟颜色的效果*/
        XYSeriesRenderer xySeriesRenderer2=new XYSeriesRenderer();
        xySeriesRenderer2.setPointStyle(PointStyle.CIRCLE);//坐标点的显示风格
        xySeriesRenderer2.setPointStrokeWidth(3);//坐标点的大小
        xySeriesRenderer2.setColor(0xFF00C8FF);//表示该组数据的图或线的颜色
        xySeriesRenderer2.setDisplayChartValues(false);//设置是否显示坐标点的y轴坐标值
        xySeriesRenderer2.setChartValuesTextSize(12);//设置显示的坐标点值的字体大小

        seriesRenderer.addSeriesRenderer(xySeriesRenderer1);
        seriesRenderer.addSeriesRenderer(xySeriesRenderer2);
        return seriesRenderer;
    }

    private XYMultipleSeriesDataset getDataSet() {
        XYMultipleSeriesDataset seriesDataset = new XYMultipleSeriesDataset();
        XYSeries xySeries1 = new XYSeries("北京最近7天最高温度变化趋势");
        xySeries1.add(1, 36);
        xySeries1.add(2, 30);
        xySeries1.add(3, 27);
        xySeries1.add(4, 29);
        xySeries1.add(5, 34);
        xySeries1.add(6, 28);
        xySeries1.add(7, 33);
        seriesDataset.addSeries(xySeries1);

        XYSeries xySeries2=new XYSeries("北京最近7天最低温度变化趋势");
        xySeries2.add(1, 27);
        xySeries2.add(2, 22);
        xySeries2.add(3, 20);
        xySeries2.add(4, 21);
        xySeries2.add(5, 25);
        xySeries2.add(6, 22);
        xySeries2.add(7, 23);
        seriesDataset.addSeries(xySeries2);

        return seriesDataset;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    measureDataList = Utility.handleMeasureData(msg.obj.toString());
                    //showChart();
                    if (measureDataList != null) {
                        initChart(lineChart);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void initChart(LineChart mChart) {
        ArrayList<Entry> entriesPm25 = new ArrayList<>();
        ArrayList<Entry> entriesPm10 = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();
        for (int i = 0; i < measureDataList.size(); i++) {
            MeasureData measureData = measureDataList.get(i);
            entriesPm25.add(new Entry((float) measureData.getPm2_5(), i));
            entriesPm10.add(new Entry((float) measureData.getPm10(), i));
            labels.add(i + chooseFormat(dateFormat, timeSpan));
        }
        LineDataSet dataset1 = new LineDataSet(entriesPm25, "# of Calls");
        LineDataSet dataset2 = new LineDataSet(entriesPm10, "# of Calls");
        ArrayList<LineDataSet> datasets = new ArrayList<>();
        datasets.add(dataset1);
        datasets.add(dataset2);
        LineData data = new LineData(labels, datasets);
        dataset1.setColors(ColorTemplate.PASTEL_COLORS); //
        dataset1.setDrawCubic(true);
        dataset1.setDrawFilled(true);
        dataset2.setColors(ColorTemplate.LIBERTY_COLORS); //
        dataset2.setDrawCubic(true);
        dataset2.setDrawFilled(true);
        lineChart.setData(data);
        lineChart.animateY(5000);
        mChart.setDragEnabled(true);
    }
}
