package com.mrhi.mp3;

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class FragmentSecond extends Fragment {
    private int fragNumber;
    private RecyclerView recyclerList;
    private ImageButton toolbarLike;

    public static FragmentSecond newInstance(int fragNumber){
        FragmentSecond fragmentThird = new FragmentSecond();
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager
                (view.getContext(), LinearLayoutManager.VERTICAL, false);

        ArrayList<MusicData> musicList = findContentProviderMP3ToArrayList(view);

        recyclerList.setLayoutManager(linearLayoutManager);
        recyclerList.setAdapter(new MusicAdapter(view.getContext(), musicList));

        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

        toolbarLike = view.findViewById(R.id.toolbarLike);
        toolbarLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerList.getAdapter().notifyItemRangeChanged(0, MusicAdapter.musicList.size());
                MusicAdapter.likeFlag = !MusicAdapter.likeFlag;

                if(toolbarLike.isActivated() == false){
                    toolbarLike.setActivated(true);
                }else{
                    toolbarLike.setActivated(false);
                }
            }
        });

        return view;
    }//end of onCreateView

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstancdState){
        super.onViewCreated(view,savedInstancdState);
    }//end of viewCreate

    @Override
    public void onStop() {super.onStop();}

    // ????????????????????????????????? ??????????????? ?????????????????? ??????????????? ?????? ???????????? ??????.
    public static ArrayList<MusicData> findContentProviderMP3ToArrayList(View view) {
        ArrayList<MusicData> musicList = new ArrayList<>();
        String[] data = {
                //????????? ????????? ????????? ?????? ????????? ????????? ???????????? ?????? ??????????????????
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION};

        // ?????? ???????????? ???????????? ????????????.
        Cursor cursor = view.getContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                data, null, null, data[2] + " ASC");
        // Cursor????????? ?????????, ????????? ????????? ?????????
        // data??? ?????? ?????? ?????? ??????, ????????? TITLE ???????????? ?????????????????? ????????????
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(data[0]));
                String artist = cursor.getString(cursor.getColumnIndex(data[1]));
                String title = cursor.getString(cursor.getColumnIndex(data[2]));
                String albumArt = cursor.getString(cursor.getColumnIndex(data[3]));
                int duration = cursor.getInt(cursor.getColumnIndex(data[4]));

                MusicData musicData = new MusicData(id, artist, title, albumArt, duration);
                musicList.add(musicData);
            }   // end of while
        }//end of if
        return musicList;
    }//end of findContentProviderMP3ToArrayList



}
