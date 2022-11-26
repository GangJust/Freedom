package com.ss.android.ugc.aweme.main;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

//抖音 主页
public class MainActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        throw new RuntimeException("subApp");
    }

    @Override
    protected void onResume() {
        super.onResume();
        throw new RuntimeException("subApp");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        throw new RuntimeException("subApp");
    }
}
