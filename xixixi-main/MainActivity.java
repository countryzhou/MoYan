package com.androidcourse.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout tabHome = findViewById(R.id.tab_home);
        LinearLayout tabPost = findViewById(R.id.tab_post);
        LinearLayout tabMsg = findViewById(R.id.tab_msg);
        LinearLayout tabMine = findViewById(R.id.tab_mine);

        showFragment(new HomeFragment());

        tabHome.setOnClickListener(v -> showFragment(new HomeFragment()));
        tabPost.setOnClickListener(v -> showFragment(new PostFragment()));
        tabMsg.setOnClickListener(v -> showFragment(new MsgFragment()));
        tabMine.setOnClickListener(v -> showFragment(new MineFragment()));
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) ft.hide(currentFragment);
        if (!fragment.isAdded()) ft.add(R.id.container, fragment);
        ft.show(fragment);
        ft.commit();
        currentFragment = fragment;
    }
}
