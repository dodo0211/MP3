package com.mrhi.mp3;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;
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
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.mrhi.mp3.MusicAdapter.getAlbumImg;
import static com.mrhi.mp3.MusicAdapter.selectedPosition;

public class FragmentFourth extends Fragment {
    private int fragNumber;
    private ImageView ivAlbum;
    private TextView tvPlayCount, tvTitle, tvArtist;
    private ImageButton ibLike;
    private TextView tvCurrentTime;
    private SeekBar seekBar;
    private TextView tvDuration;
    private ImageButton ibPrevious, ibPause, ibPlay, ibNext;
    private Button btnStop;

    private int position;

    ///////////////////////////////////////////////////

    private ArrayList<MusicData> sdCardList = new ArrayList<MusicData>();

    private MainActivity mainActivity;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    private int index;
    private ArrayList<MusicData> likeArrayList = new ArrayList<>();
    private MusicAdapter musicAdapter;
    private MusicData musicData;

    private boolean flag = false;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");


    //슬라이드에 필요한
    public static FragmentFourth newInstance(int fragNumber){
        FragmentFourth fragmentFourth = new FragmentFourth();
        Bundle bundle=new Bundle();
        bundle.putInt("fragNumber", fragNumber);
        fragmentFourth.setArguments(bundle);

        return fragmentFourth;
    }

    //슬라이드에 필요한
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        musicData = MusicAdapter.musicList.get(MusicAdapter.selectedPosition);

        tvTitle.setText(musicData.getTitle());
        tvArtist.setText(musicData.getArtist());
        tvDuration.setText(musicData.getDuration());

        //////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////
        // 앨범 이미지 세팅
        byte[] decodedString = Base64.decode(musicData.getAlbumArt().getBytes(), Base64.DEFAULT);
        Bitmap albumImg = getAlbumImg(getContext(), Integer.parseInt(musicData.getAlbumArt()), 200);

        if(albumImg != null){
            ivAlbum.setImageBitmap(albumImg);
        }else{
            ivAlbum.setImageResource(R.drawable.album);
        }

    }

    //프래그먼트가 엑티비트와 연결되어 있었던 경우 호출
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)getActivity();
    }

    //프래그먼트가 엑티비티와 연결이 끊긴 상태일 때 호출
    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(R.layout.fragment04, container, false);

        // 뷰 아이디
        initializeView(view);

        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mediaPlayer = new MediaPlayer();
                    MusicData musicData = sdCardList.get(selectedPosition);
                    Uri musicUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicData.getId());
                    mediaPlayer.setDataSource(getContext(), musicUri);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    ibPlay.setEnabled(false);
                    ibPause.setEnabled(true);
                    btnStop.setEnabled(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception s){
                    s.printStackTrace();
                }
            }
        });



//            //시크바도 같이 움직여야 함
//            Thread thread = new Thread() {
//                @Override
//                public void run() {
//                    //할 일이 없으면 종료
//                    if (mediaPlayer == null) return;
//
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            //progrssBar의 전체시간을 가져와서 셋팅
////                            pbMP3.setMax(mPlayer.getDuration());
////                        }
////                    });//end of runOnUiThread
//
//                    //음악이 돌고있는가?
//                    while (mediaPlayer.isPlaying()) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
////                                pbMP3.setProgress(mediaPlayer.getCurrentPosition());
//                                tvDuration.setText(simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
//                            }
//                        });//end of runOnUiThread
//
//                        SystemClock.sleep(1000);
//                    }//end of while
//
//                }//end of run
//
//                /////////////////////////////////////////////////////////////////////////////////
//                private void runOnUiThread(Runnable runnable) {
//                }
//            };//end of thread
//            thread.start();
//        });//end of ibPlay
//
//        btnStop.setOnClickListener(v -> {
//            mediaPlayer.stop();
//            mediaPlayer.reset();
//
//            ibPlay.setEnabled(true);
//            btnStop.setEnabled(false);
//            ibPause.setEnabled(false);
//        });//end of btnStop
//
//        ibPause.setOnClickListener(v -> {
//            if(flag == false){
//                mediaPlayer.pause();
//                flag = true;
//            }else{
//                mediaPlayer.start();
//                flag = false;
//            }
//        });//end of btnPause

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
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });//end of seekBarChange

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstancdState){
        super.onViewCreated(view,savedInstancdState);
    }

    //시크바 변경에 관한 함수
    private void seekBarChangeMethod() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // 사용자가 움직였을시, seekbar 이동
                if(b){
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    //시크바 스레드에 관한 함수
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setSeekBarThread(){
        Thread thread = new Thread(new Runnable() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

            @Override
            public void run() {
                while(mediaPlayer.isPlaying()){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvCurrentTime.setText(simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
                        }
                    });
                    SystemClock.sleep(100);
                }
            }
        });
        thread.start();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void initializeView(View view) {
        ivAlbum = view.findViewById(R.id.ivAlbum);
        tvPlayCount = view.findViewById(R.id.tvPlayCount);
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


}
