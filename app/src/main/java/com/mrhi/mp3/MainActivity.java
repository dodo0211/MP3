package com.mrhi.mp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import me.relex.circleindicator.CircleIndicator3;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private FragmentStateAdapter pagerAdapter;
    private int numberPage = 4;
    private CircleIndicator3 indicator;

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
        //ViewPager2 Item을 200개 만들었으니 현재 위치를 100으로setCurrentItem(100) 하여 좌우로 슬라이딩 가능하도록 하였습니다.
        viewPager2.setCurrentItem(100);
        viewPager2.setOffscreenPageLimit(3);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            //ViewPager2의 registerOnPageChangeCallback
            //추상클래스로 필요한 onPageSelected, onPageScrolled 메서드를 재정의
            //이는 인디케이터를 페이지에 맞게 잘 사용하기 위해서 정의했습니다.

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


        final float pageMargin =
                getResources().getDimensionPixelOffset(R.dimen.pageMargin);
        final float pageOffset =
                getResources().getDimensionPixelOffset(R.dimen.offset);

//        //setPageTransformer를 통해 프래그먼트간 애니메이션 맞춤설정도 가능합니다.
//        viewPager2.setPageTransformer(new ViewPager2.PageTransformer() {
//            @Override
//            public void transformPage(@NonNull View page, float position) {
//                float myOffset = position * -(2 * pageOffset + pageMargin);
//                if (viewPager2.getOrientation() ==
//                        ViewPager2.ORIENTATION_HORIZONTAL) {
//                    if (ViewCompat.getLayoutDirection(viewPager2) ==
//                            ViewCompat.LAYOUT_DIRECTION_RTL) {
//                        page.setTranslationX(-myOffset);
//                    } else {
//                        page.setTranslationX(myOffset);
//                    }
//                } else {
//                    page.setTranslationY(myOffset);
//                }
//            }
//        });

    }//end of onCreate

}//end of class