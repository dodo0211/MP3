package com.mrhi.mp3;

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class FragmentThird extends Fragment {
    private int fragNumber;
    private TextView tvName3;
    private RecyclerView recyclerList;
    private MusicAdapter musicAdapter;
    private ArrayList<MusicData> sdCardList = new ArrayList<MusicData>();

    public static FragmentThird newInstance(int fragNumber){
        FragmentThird fragmentThird = new FragmentThird();
        Bundle bundle=new Bundle();
        bundle.putInt("fragNumber", fragNumber);
        fragmentThird.setArguments(bundle);
        return fragmentThird;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragNumber = getArguments().getInt("fragNumber",0);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(
                R.layout.fragment03, container, false);

        recyclerList = view.findViewById(R.id.recyclerList);

        musicAdapter = new MusicAdapter(view.getContext(), sdCardList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerList.setLayoutManager(linearLayoutManager);
        recyclerList.setAdapter(musicAdapter);

        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
        findContentProviderMP3ToArrayList(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstancdState){
        super.onViewCreated(view,savedInstancdState);
//        tvName3 = (TextView) view.findViewById(R.id.tvName3);
//        tvName3.setText("Page " + fragNumber);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void findContentProviderMP3ToArrayList(View view) {
        // 컨텐트프로바이더에서는 핸드폰에서 다운로드했던 음악파일은 모두 관리되고 있다.
        String[] data = {
                //이름은 우리가 정하는 것이 아니라 정보의 이름으로 이미 정해져있는것
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION};

        // 전체 영역에서 음악파일 가져온다.
        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                data, null, null, data[2] + " ASC");
        // Cursor타입이 반환됨, 원하는 정보를 찾아줌
        // data가 내가 보고 싶은 항목, 그리고 TITLE 항목으로 오름차순으로 가져와라
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(data[0]));
                String artist = cursor.getString(cursor.getColumnIndex(data[1]));
                String title = cursor.getString(cursor.getColumnIndex(data[2]));
                String albumArt = cursor.getString(cursor.getColumnIndex(data[3]));
                String duration = cursor.getString(cursor.getColumnIndex(data[4]));

                Log.d("Fragment03", "title : "+title);

                MusicData musicData = new MusicData(id, artist, title, albumArt, duration);
                sdCardList.add(musicData);
            }   // end of while
        }//end of if
    }//end of findContentProviderMP3ToArrayList

}
