package com.mrhi.mp3;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class FragmentFirst extends Fragment {
    private int fragNumber;
    private RecyclerView recyclerChart;
    private ChartAdapter chartAdapter;
    public static String melon_chart_url = "https://www.melon.com/chart/";

    public static FragmentFirst newInstance(int fragNumber) {
        FragmentFirst fragmentFirst = new FragmentFirst();
        Bundle bundle = new Bundle();
        bundle.putInt("fragNumber", fragNumber);
        fragmentFirst.setArguments(bundle);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragNumber = getArguments().getInt("fragNumber", 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(R.layout.fragment01, container, false);

        recyclerChart = view.findViewById(R.id.recyclerChart);

        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerChart.setLayoutManager(linearLayoutManager);

        chartAdapter = new ChartAdapter();  //이건 안해도 될것 같은데
        recyclerChart.setAdapter(chartAdapter);

        getData();

        return view;
    }//end of onCreateView

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstancdState) {
        super.onViewCreated(view, savedInstancdState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void getData() {
        MelonJsoup jsoupAsyncTask = new MelonJsoup();
        jsoupAsyncTask.execute();
    }

    private class MelonJsoup extends AsyncTask<Void, Void, Void> {
        ArrayList<String> listTitle = new ArrayList<>();
        ArrayList<String> listName = new ArrayList<>();
        ArrayList<String> listUrl = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(melon_chart_url).get();
                final Elements rank_list1 = doc.select("div.wrap_song_info div.ellipsis.rank01 span a");
                final Elements rank_list_name = doc.select("div.wrap_song_info div.ellipsis.rank02");

                final Elements image_list1 = doc.select("div.wrap a.image_typeAll img");
                Handler handler = new Handler(Looper.getMainLooper()); // 객체생성
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //순위정보
                        for (Element element : rank_list1) {
                            listTitle.add(element.text());
                        }
                        //가수정보
                        for (Element element : rank_list_name) {
                            listName.add(element.text());
                        }
                        // 이미지정보
                        for (Element element : image_list1) {
                            listUrl.add(element.attr("src"));
                            Log.d("jsoup", "jsoup");
                        }

                        for (int i = 0; i < 100; i++) {
                            ChartData data = new ChartData();
                            if(listTitle.size() != 0){
                                data.setTitle(listTitle.get(i));
                            }

                            if(listUrl.size() != 0){
                                data.setImageUrl(listUrl.get(i));
                            }

                            if(listName.size() != 0){
                                data.setName(listName.get(i).substring(0,listName.get(i).length()/2));
                            }

                            data.setRankNum(String.valueOf(i + 1));

                            chartAdapter.addItem(data);
                        }
                        chartAdapter.notifyDataSetChanged();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


}
