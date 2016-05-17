package com.bishe.aqidemo.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bishe.aqidemo.R;
import com.bishe.aqidemo.model.PersonalData;

import java.util.List;

/**
 * Created by huangxiangyu on 16/5/17.
 * In AQIDemo
 */
public class CommViewAdapter extends RecyclerView.Adapter<CommViewAdapter.PersonalDataViewHolder> {
    private List<PersonalData> personalDatas;
    private Context context;

    public CommViewAdapter(List<PersonalData> personalDatas, Context context) {
        this.personalDatas = personalDatas;
        this.context = context;
    }

    @Override
    public CommViewAdapter.PersonalDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_peer, parent, false);
        PersonalDataViewHolder viewHolder = new PersonalDataViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CommViewAdapter.PersonalDataViewHolder holder, int position) {
        final int i = position;
        holder.userName.setText(personalDatas.get(i).getUname());
        holder.cityName.setText(personalDatas.get(i).getCity());
        holder.Pm25Data.setText(personalDatas.get(i).getPm25() + "");
        holder.Pm10Data.setText(personalDatas.get(i).getPm10() + "");
        holder.timeData.setText(personalDatas.get(i).getTime());
    }

    @Override
    public int getItemCount() {
        if (personalDatas != null && !personalDatas.isEmpty()) {
            return personalDatas.size();
        }
        return 0;
    }

    static class PersonalDataViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView userName;
        TextView cityName;
        TextView Pm25Data;
        TextView Pm10Data;
        TextView timeData;
        public PersonalDataViewHolder(final View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view_peer);
            userName = (TextView) itemView.findViewById(R.id.username_peer);
            cityName = (TextView) itemView.findViewById(R.id.city_peer);
            Pm25Data = (TextView) itemView.findViewById(R.id.pm25_peer);
            Pm10Data = (TextView) itemView.findViewById(R.id.pm10_peer);
            timeData = (TextView) itemView.findViewById(R.id.time_peer);
        }
    }
}
