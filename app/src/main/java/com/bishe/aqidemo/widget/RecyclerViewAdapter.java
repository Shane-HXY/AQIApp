package com.bishe.aqidemo.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bishe.aqidemo.activity.WeatherItemActivity;
import com.bishe.aqidemo.model.WeatherData;
import com.bishe.aqidemo.R;

import java.util.List;

/**
 * Created by huangxiangyu on 16/4/29.
 * In AQIDemo
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.WeatherDataViewHolder> {
    private List<WeatherData> weatherDatas;
    private Context context;

    public RecyclerViewAdapter(List<WeatherData> weatherDatas, Context context) {
        this.weatherDatas = weatherDatas;
        this.context = context;
    }

    @Override
    public WeatherDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        WeatherDataViewHolder viewHolder = new WeatherDataViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WeatherDataViewHolder holder, final int position) {
        final int j = position;
        int id = context.getResources().getIdentifier("a"+String.valueOf(weatherDatas.get(position).getCode()), "drawable", context.getPackageName());
        //Log.i("TAG", String.valueOf(id)+"xc"+String.valueOf(weatherDatas.get(position).getCode()));
        holder.cityName.setText(weatherDatas.get(position).getCityName());
        holder.nodeName.setText(weatherDatas.get(position).getNodeName());
        holder.code.setImageResource(id);
        holder.aqi.setText(weatherDatas.get(position).getAqi() + "");
        holder.qlty.setText(weatherDatas.get(position).getQuality());
        holder.update.setText(weatherDatas.get(position).getUpdate());
        holder.pm2_5.setText(weatherDatas.get(position).getPm25() + "");
        holder.pm10.setText(weatherDatas.get(position).getPm10() + "");
        holder.tmp.setText(weatherDatas.get(position).getTmp() + "");
        holder.hum.setText(weatherDatas.get(position).getHum() + "");

        //为CardView添加点击事件
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WeatherItemActivity.class);
                intent.putExtra("WeatherData", weatherDatas.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (weatherDatas!=null && !weatherDatas.isEmpty()) {
            return weatherDatas.size();
        }
        return 0;
    }

    static class WeatherDataViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView cityName;
        TextView nodeName;
        ImageView code;
        TextView aqi;
        TextView qlty;
        TextView update;
        TextView pm2_5;
        TextView pm10;
        TextView tmp;
        TextView hum;

        public WeatherDataViewHolder(final View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cityName = (TextView) itemView.findViewById(R.id.city_name);
            nodeName = (TextView) itemView.findViewById(R.id.node);
            code = (ImageView) itemView.findViewById(R.id.code);
            aqi = (TextView) itemView.findViewById(R.id.aqi);
            qlty = (TextView) itemView.findViewById(R.id.qlty);
            update = (TextView) itemView.findViewById(R.id.update);
            pm2_5 = (TextView) itemView.findViewById(R.id.pm25);
            pm10 = (TextView) itemView.findViewById(R.id.pm10);
            tmp = (TextView) itemView.findViewById(R.id.tmp);
            hum = (TextView) itemView.findViewById(R.id.hum);
        }
    }


}