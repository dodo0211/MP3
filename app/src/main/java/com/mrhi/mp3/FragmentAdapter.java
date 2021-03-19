package com.mrhi.mp3;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mrhi.mp3.FragmentFirst;
import com.mrhi.mp3.FragmentFourth;
import com.mrhi.mp3.FragmentSecond;
import com.mrhi.mp3.FragmentThird;


public class FragmentAdapter extends FragmentStateAdapter {
    private int count;

    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity, int count) {
        super(fragmentActivity);
        this.count = count;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int index = position % count;

        if(index==0) return FragmentFirst.newInstance(index+1);
        else if(index==1) return FragmentSecond.newInstance(index+1);
        else if(index==2) return FragmentThird.newInstance(index+1);
        else return FragmentFourth.newInstance(index+1);
    }

    @Override
    public int getItemCount() {
        return 200;
    }

}
