package com.example.stocks;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class Fragment_adapter extends FragmentPagerAdapter {
    ArrayList<Fragment> fragmentArraylist = new ArrayList<>();

    public Fragment_adapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public Fragment_adapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArraylist.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArraylist.size();
    }

    public void addToFragment(Fragment fragment){
        fragmentArraylist.add(fragment);

    }
}
