package com.bishe.aqidemo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bishe.aqidemo.R;
import com.bishe.aqidemo.model.PersonalData;

import java.util.List;

/**
 * Created by huangxiangyu on 16/5/18.
 * In AQIDemo
 */
public class MyPostAdapter extends BaseAdapter {
    private Context context;
    private List<PersonalData> personalDataList;
    private LayoutInflater layoutInflater;
    TextView tvCity;
    TextView tvDate;
    TextView tvPm25;
    TextView tvPm10;

    public MyPostAdapter(Context context, List<PersonalData> personalDataList) {
        this.context = context;
        this.personalDataList = personalDataList;
        layoutInflater = LayoutInflater.from(this.context);
    }
    @Override
    public int getCount() {
        if (personalDataList.size() > 0) {
            return personalDataList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if (personalDataList.size() > 0) {
            return personalDataList.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.mypost_item, null);
        }
        tvCity = (TextView) view.findViewById(R.id.my_city);
        tvDate = (TextView) view.findViewById(R.id.my_date);
        tvPm10 = (TextView) view.findViewById(R.id.my_pm10);
        tvPm25 = (TextView) view.findViewById(R.id.my_pm25);

        tvCity.setText(personalDataList.get(i).getCity());
        tvDate.setText(personalDataList.get(i).getTime());
        tvPm25.setText(personalDataList.get(i).getPm25() + "");
        tvPm10.setText(personalDataList.get(i).getPm10() + "");
        return view;
    }
}
