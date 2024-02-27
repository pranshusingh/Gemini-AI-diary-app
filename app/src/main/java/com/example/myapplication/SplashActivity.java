package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.viewpager.widget.ViewPager;

public class SplashActivity extends AppCompatActivity {
    private GestureDetectorCompat gestureDetector;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        viewPager = findViewById(R.id.viewPager);
        SwipePagerAdapter adapter = new SwipePagerAdapter(this);
        viewPager.setAdapter(adapter);

        gestureDetector = new GestureDetectorCompat(this, new SwipeGestureListener());
        viewPager.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            // Check if the absolute difference in X and Y exceeds the threshold
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD &&
                    Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    // Horizontal swipe detected
                    if (diffX > 0) {
                        // Swipe from left to right, navigate to the main activity
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // Swipe from right to left
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    // Vertical swipe detected
                    if (diffY > 0) {
                        // Swipe from top to bottom
                    } else {
                        // Swipe from bottom to top
                    }
                }
            }
            return true;
        }
    }

}