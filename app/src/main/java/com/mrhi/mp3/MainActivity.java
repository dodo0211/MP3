package com.mrhi.mp3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;

public class MainActivity extends AppCompatActivity {
    //viewPager에 필요한
    private ViewPager2 viewPager2;
    private FragmentStateAdapter pagerAdapter;
    private int numberPage = 4;
    private CircleIndicator3 indicator;
    
    //////////////////////////////////////////////////////////////////////////////////////////

    //activity_fragment01
    private RecyclerView recyclerMelon;
    //activity_fragment02
    private RecyclerView recyclerLike;
    //activity_fragment03
    private RecyclerView recyclerList;

    private ImageButton ibPlay, ibPause;
    private Button ibStop;
    private ImageButton ibPrevious, ibNext;
    private TextView tvCurrentTime;
    private SeekBar seekBar;

    private ArrayList<MusicData> sdCardList = new ArrayList<MusicData>();

    private MediaPlayer mPlayer;

    //노래의 현재 위치
    private int selectPosition;

    private MusicAdapter musicAdapter;

    private boolean flag = false;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ViewPager2
        viewPager2 = findViewById(R.id.viewPager2);
        //Adapter
        pagerAdapter = new FragmentAdapter(this, numberPage);
        viewPager2.setAdapter(pagerAdapter);
        //Indicator
        indicator = findViewById(R.id.indicator);
        indicator.setViewPager(viewPager2);
        indicator.createIndicators(numberPage, 0);

        //ViewPager Setting
        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        //ViewPager2 Item을 200개 만들었으니 현재 위치를 100으로setCurrentItem(100) 하여 좌우로 슬라이딩 가능
        viewPager2.setCurrentItem(100);
        viewPager2.setOffscreenPageLimit(3);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            //ViewPager2의 registerOnPageChangeCallback
            //추상클래스로 필요한 onPageSelected, onPageScrolled 메서드를 재정의
            //이는 인디케이터를 페이지에 맞게 잘 사용하기 위해서 정의

            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    viewPager2.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                indicator.animatePageSelected(position % numberPage);
            }

        });

        final float pageMargin = getResources().getDimensionPixelOffset(R.dimen.pageMargin);
        final float pageOffset = getResources().getDimensionPixelOffset(R.dimen.offset);

        ///////////////////////////////////////////////////////////////////////////////////////

        //sdCard를 사용하는 앱이기 때문에 권한설정
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

        findViewByIdFunc();

        findContentProviderMP3ToArrayList();

        musicAdapter = new MusicAdapter(getApplicationContext(), sdCardList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerList.setLayoutManager(linearLayoutManager);
        recyclerList.setAdapter(musicAdapter);

        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (ibPlay.isEnabled() == true) {
                    selectPosition = position;
                    ibPlay.setEnabled(true);
                    ibStop.setEnabled(false);
                    ibPause.setEnabled(false);

//                    tvMP3.setText("선택된 음악" + sdCardList.get(position).getTitle());
                } else {
                    Toast.makeText(MainActivity.this, "노래중지하고 선택요망", Toast.LENGTH_SHORT).show();
                }
            }
        });//end of setOnItemClickListener

        //음악을 선택하지 않으면 기본적으로 position이 0
        selectPosition = 0;
//        ibPlay.setEnabled(true);
//        ibStop.setEnabled(false);
//        ibPause.setEnabled(false);

//        ibPlay.setOnClickListener(v -> {
//            mPlayer = new MediaPlayer();
//            MusicData musicData = sdCardList.get(selectPosition);
//            Uri musicUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicData.getId());
//
//            try {
//                mPlayer.setDataSource(MainActivity.this, musicUri);
//                mPlayer.prepare();
//                mPlayer.start();
//
//                ibPlay.setEnabled(false);
//                ibStop.setEnabled(true);
//                ibPause.setEnabled(true);
////                tvMP3.setText("실행중인 음악" + musicData.getTitle());
//
//                //seekbar도 같이 움직여야 함
//                Thread thread = new Thread() {
//                    @Override
//                    public void run() {
//                        //할 일이 없으면 종료
//                        if (mPlayer == null) return;
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
////                                //progrssBar의 전체시간을 가져와서 셋팅
////                                pbMP3.setMax(mPlayer.getDuration());
//                            }
//                        });//end of runOnUiThread
//
//                        //음악이 돌고있는가?
//                        while (mPlayer.isPlaying()) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
////                                    pbMP3.setProgress(mPlayer.getCurrentPosition());
//                                    tvCurrentTime.setText("진행시간: " + simpleDateFormat.format(mPlayer.getCurrentPosition()));
//                                }
//                            });//end of runOnUiThread
//
//                            SystemClock.sleep(1000);
//                        }//end of while
//
//                    }//end of run
//                };//end of thread
//                thread.start();
//
//            } catch (IOException e) {
//                Log.d("MainActivity", "음악파일로드실패");
//            }
//
//        });//end of btnPlay
//
//        ibStop.setOnClickListener(v -> {
//            mPlayer.stop();
//            mPlayer.reset();
//
//            ibPlay.setEnabled(true);
//            ibStop.setEnabled(false);
//            ibPause.setEnabled(false);
////            tvMP3.setText("음악선택 기다리는 중");
//        });//end of btnStop
//
//        ibPause.setOnClickListener(v -> {
//            if(flag == false){
//                mPlayer.pause();
////                ibPause.setText("이어듣기");
////                pbMP3.setVisibility(View.INVISIBLE);
//                flag = true;
//            }else{
//                mPlayer.start();
////                ibPause.setText("일시정지");
////                pbMP3.setVisibility(View.VISIBLE);
//                flag = false;
//            }
//
//        });//end of btnPause
//
//        //seekBar를 움직이면 해당 시간이 플레이
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean myTouchStart) {
//                //progrss값이 움직일 때 마다 onProgressChange함수를 부름
//                if(myTouchStart == true){   //이걸 안하면 매끄럽지가 않음
//                    //현재 boolean는 flase(progressBar를 조작x)
//                    //boolean이 true일때만! 그때가 progressBar를 조작하는 때
//                    mPlayer.seekTo(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });//end of seekBarChange


    }//end of onCreate

    private void findContentProviderMP3ToArrayList() {
        // 컨텐트프로바이더에서는 핸드폰에서 다운로드했던 음악파일은 모두 관리되고 있다.
        String[] data = {
                //이름은 우리가 정하는 것이 아니라 정보의 이름으로 이미 정해져있는것
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION};

        // 전체 영역에서 음악파일 가져온다.
        Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                data, null, null, data[2] + " ASC");    // Cursor타입이 반환됨, 원하는 정보를 찾아줌
        // data가 내가 보고 싶은 항목, 그리고 TITLE 항목으로 오름차순으로 가져와라
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(data[0]));
                String artist = cursor.getString(cursor.getColumnIndex(data[1]));
                String title = cursor.getString(cursor.getColumnIndex(data[2]));
                String albumArt = cursor.getString(cursor.getColumnIndex(data[3]));
                String duration = cursor.getString(cursor.getColumnIndex(data[4]));

                MusicData musicData = new MusicData(id, artist, title, albumArt, duration);
                sdCardList.add(musicData);
            }   // end of while
        }//end of if
    }//end of findContentProviderMP3ToArrayList

    private void findViewByIdFunc() {
        recyclerList = findViewById(R.id.recyclerList);
        ibPause = findViewById(R.id.ibPause);
        ibPlay = findViewById(R.id.ibPlay);
        ibStop = findViewById(R.id.ibStop);
        ibPrevious = findViewById(R.id.ibPrevious);
        ibNext = findViewById(R.id.ibNext);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        seekBar = findViewById(R.id.seekBar);
    }//end of findIDFunc

}//end of class
