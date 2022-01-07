package com.example.chat.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.chat.Fragments.ProfileFragment;
import com.example.chat.Fragments.UsersFragment;
import com.example.chat.Fragments.chatFragment;
import com.example.chat.Model.User;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle){
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new chatFragment();
            case 2:
                return new ProfileFragment();
        }
        return new UsersFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
