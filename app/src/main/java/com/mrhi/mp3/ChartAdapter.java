package com.mrhi.mp3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ItemViewHolder> {
    private TextView txt_ranktNum, txt_chartName, txt_chartTitle;
    private ImageView img_chart;

    private ArrayList<ChartData> listData = new ArrayList<>();

    @NonNull
    @Override
    public ChartAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.chart_item, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChartAdapter.ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.onBind(listData.get(i));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(ChartData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_chartTitle = itemView.findViewById(R.id.txt_chartTitle);
            txt_chartName = itemView.findViewById(R.id.txt_chartName);
            txt_ranktNum = itemView.findViewById(R.id.txt_ranktNum);
            img_chart = itemView.findViewById(R.id.img_chart);
        }//end of ItemViewHolder

        void onBind(ChartData data) {
            txt_ranktNum.setText(data.getRankNum());
            txt_chartName.setText(data.getName());
            txt_chartTitle.setText(data.getTitle());

            Glide.with(itemView.getContext()).load(data.getImageUrl()).into(img_chart);
        }//end of onBind

    }//end of ItemViewHolder

}
