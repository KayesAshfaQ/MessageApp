package com.codingnub.messageapp.adapter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final String[] titles;
    private final Fragment[] frags;


    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior, String[] titles, Fragment[] frags) {
        super(fm, behavior);
        this.titles = titles;
        this.frags = frags;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return frags[position];
    }

    @Override
    public int getCount() {
        return frags.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
