package com.group2.papertrail.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.group2.papertrail.R;
import com.group2.papertrail.databinding.ActivitySplashScreenBinding;
import com.group2.papertrail.ui.auth.LoginActivity;
import com.group2.papertrail.ui.auth.RegisterActivity;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;
    private static final int SPLASH_DURATION = 3000; // 3 sec
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handler.postDelayed(() -> {
            splashScreen.setKeepOnScreenCondition(() -> false);

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        binding = null;
    }
}
