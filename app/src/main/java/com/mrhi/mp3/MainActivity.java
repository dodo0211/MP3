package com.mrhi.mp3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.os.Bundle;

import me.relex.circleindicator.CircleIndicator3;

public class MainActivity extends AppCompatActivity {
    //viewPager에 필요한
    public static ViewPager2 viewPager2;
    private FragmentStateAdapter pagerAdapter;
    private int numberPage = 3;
    private CircleIndicator3 indicator;

    private String id;
    private String artist;
    private String title;
    private String albumArt;
    private String duration;
    private int liked;

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

        //sdCard를 사용하는 앱이기 때문에 권한설정
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

    }//end of onCreate

}//end of class
