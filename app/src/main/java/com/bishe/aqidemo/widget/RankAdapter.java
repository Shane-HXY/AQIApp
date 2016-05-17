package com.bishe.aqidemo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bishe.aqidemo.R;
import com.bishe.aqidemo.model.Rank;

import java.util.List;

/**
 * Created by huangxiangyu on 16/5/16.
 * In AQIDemo
 */
public class RankAdapter extends ArrayAdapter<Rank> {
    public RankAdapter(Context context, List<Rank> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Rank rank = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
        }

        TextView tvRank = (TextView) convertView.findViewById(R.id.rank_list);
        TextView tvLoc = (TextView) convertView.findViewById(R.id.area_list);
        TextView tvAqi = (TextView) convertView.findViewById(R.id.aqi_list);
        TextView tvPm10 = (TextView) convertView.findViewById(R.id.pm10_list);
        TextView tvPm25 = (TextView) convertView.findViewById(R.id.pm25_list);

        tvRank.setText(rank.getRank());
        tvLoc.setText(rank.getLoc());
        tvAqi.setText(rank.getAqi());
        tvPm10.setText(rank.getPm10());
        tvPm25.setText(rank.getPm25());
        return convertView;
    }
}
