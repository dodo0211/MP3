package com.mrhi.mp3;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class FragmentFourth extends Fragment {
    private int fragNumber;
    private ImageView ivAlbum;
    private TextView tvTitle, tvArtist;
    private ImageButton ibLike;
    private TextView tvCurrentTime;
    private SeekBar seekBar;
    private TextView tvDuration;
    private ImageButton ibPrevious, ibPause, ibPlay, ibNext;
    private Button btnStop;

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

    //슬라이드에 필요한
    public static FragmentFourth newInstance(int fragNumber){
        FragmentFourth fragmentFourth = new FragmentFourth();
        Bundle bundle=new Bundle();
        bundle.putInt("fragNumber", fragNumber);
        fragmentFourth.setArguments(bundle);

        return fragmentFourth;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        //화면 세팅
        initUI();

    }//end of resume

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(R.layout.fragment04, container, false);

        // 뷰 아이디
        initializeView(view);

        eventHandlerFunc();

        return view;
    }//end of onCreate

    private void eventHandlerFunc() {
        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    Log.d("debug", "position"+mediaPlayer.getCurrentPosition());
                    if(mediaPlayer.getCurrentPosition() == 0){
                        MusicData musicData = MusicAdapter.getSelectedMusic();
                        Uri musicUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicData.getId());
                        mediaPlayer.setDataSource(getContext(), musicUri);
                        mediaPlayer.prepare();
                    }
                    mediaPlayer.start();

                    //seekBar가 음악에 맞춰 움직임
                    new Thread(){
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                        public void run(){
                            if(mediaPlayer == null) return;
                            seekBar.setMax(mediaPlayer.getDuration());
                            while(mediaPlayer.isPlaying()){
                                    getActivity().runOnUiThread(() ->{
                                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                        tvCurrentTime.setText(secondsTommssFormat(mediaPlayer.getCurrentPosition()));
                                    });
                                SystemClock.sleep(200);
                            }//end of while
                        }//end of run
                    }.start();

                    ibPlay.setEnabled(false);
                    ibPause.setEnabled(true);
                    btnStop.setEnabled(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception s){
                    s.printStackTrace();
                }
            }
        });//end of ibPlay

        //seekBar를 움직이면 해당 시간이 플레이
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean myTouchStart) {
                //progrss값이 움직일 때 마다 onProgressChange함수를 부름
                if(myTouchStart == true){   //이걸 안하면 매끄럽지가 않음
                    //현재 boolean는 flase(progressBar를 조작x)
                    //boolean이 true일때만! 그때가 progressBar를 조작하는 때
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });//end of seekBarChange

        ibPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();

                ibPlay.setEnabled(true);
                ibPause.setEnabled(false);
                btnStop.setEnabled(true);
            }
        });//end of ibPause

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.reset();
                mediaPlayer.stop();

                ibPlay.setEnabled(true);
                ibPause.setEnabled(false);
                btnStop.setEnabled(true);
            }
        });//end of btnStop

        ibPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.reset();

                MusicAdapter.selectedPosition--;

                initUI();
            }
        });//end of ibPrevious

        ibNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.reset();

                MusicAdapter.selectedPosition++;

                initUI();
            }
        });

        ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicData musicData = MusicAdapter.getSelectedMusic();
                if(ibLike.isActivated()){
                    ibLike.setActivated(false);
                    musicData.setLiked(false);
                    Toast.makeText(getActivity(), "좋아요 취소", Toast.LENGTH_SHORT).show();
                }else{
                    ibLike.setActivated(true);
                    musicData.setLiked(true);
                    Toast.makeText(getActivity(), "좋아요❤", Toast.LENGTH_SHORT).show();
                }
                /////////아쉽
//                FragmentThird.recyclerList.getAdapter().notifyDataSetChanged();
            }
        });//end of ibLike

    }//end of eventHandle

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstancdState){
        super.onViewCreated(view,savedInstancdState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //화면 셋팅
    private void initUI() {
        MusicData musicData = MusicAdapter.getSelectedMusic();

        tvTitle.setText(musicData.getTitle());
        tvArtist.setText(musicData.getArtist());
        tvDuration.setText(secondsTommssFormat(musicData.getDuration()));

        // 앨범 이미지 세팅
        Bitmap albumImg = MusicAdapter.getAlbumImg(getContext(), Integer.parseInt(musicData.getAlbumArt()), 200);

        if(albumImg != null){
            ivAlbum.setImageBitmap(albumImg);
        }else{
            ivAlbum.setImageResource(R.drawable.album);
        }

//        //좋아요
//        if(musicData.getLiked() == 1){
//            ibLike.setActivated(true);
//        }else{
//            ibLike.setActivated(false);
//        }

    }

    private void initializeView(View view) {
        ivAlbum = view.findViewById(R.id.ivAlbum);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvArtist = view.findViewById(R.id.tvArtist);
        ibLike = view.findViewById(R.id.ibLike);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);
        seekBar = view.findViewById(R.id.seekBar);
        tvDuration = view.findViewById(R.id.tvDuration);

        ibPrevious = view.findViewById(R.id.ibPrevious);
        ibPause = view.findViewById(R.id.ibPause);
        ibPlay = view.findViewById(R.id.ibPlay);
        ibNext = view.findViewById(R.id.ibNext);
        btnStop = view.findViewById(R.id.btnStop);
    }//end of findIDFunc

    public static String secondsTommssFormat(int seconds) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
        return timeFormat.format(seconds);
    }

}
