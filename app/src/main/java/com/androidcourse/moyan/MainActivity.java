package com.androidcourse.moyan;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.androidcourse.moyan.ui.DiscoverFragment;
import com.androidcourse.moyan.ui.HomeFragment;
import com.androidcourse.moyan.ui.NotificationFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment homeFragment;
    private Fragment discoverFragment;
    private Fragment notificationFragment;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragments();
        initViews();
    }

    private void initFragments() {
        homeFragment = new HomeFragment();
        discoverFragment = new DiscoverFragment();
        notificationFragment = new NotificationFragment();
        currentFragment = homeFragment;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, homeFragment)
                .commit();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                switchFragment(homeFragment);
                return true;
            } else if (itemId == R.id.nav_discover) {
                switchFragment(discoverFragment);
                return true;
            } else if (itemId == R.id.nav_notification) {
                switchFragment(notificationFragment);
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void switchFragment(Fragment targetFragment) {
        if (currentFragment == targetFragment) return;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (currentFragment != null) transaction.hide(currentFragment);
        if (!targetFragment.isAdded()) {
            transaction.add(R.id.container, targetFragment);
        } else {
            transaction.show(targetFragment);
        }
        transaction.commit();
        currentFragment = targetFragment;
    }
}