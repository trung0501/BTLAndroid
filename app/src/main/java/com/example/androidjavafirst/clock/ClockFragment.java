package com.example.androidjavafirst.clock;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidjavafirst.databinding.FragmentClockBinding;

public class ClockFragment extends Fragment {
    private FragmentClockBinding binding;
    private Handler handler;
    private boolean isRunning = false;
    private boolean isPaused = false; // Track if timer is paused
    private long startTime = 0L, timeInMillis = 0L;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentClockBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler = new Handler();
        binding.startButton.setOnClickListener(v -> startTimer());
        binding.stopButton.setOnClickListener(v -> togglePauseResume());
        binding.resetButton.setOnClickListener(v -> resetTimer());
        updateButtonStates();
    }

    private void startTimer() {
        if (!isRunning) {
            startTime = SystemClock.elapsedRealtime() - timeInMillis;
            handler.postDelayed(updateTimerRunnable, 0);
            isRunning = true;
            isPaused = false;
            updateButtonStates();
        }
    }

    @SuppressLint("SetTextI18n")
    private void togglePauseResume() {
        if (isRunning) {
            if (isPaused) {
                // Resume Timer
                startTime = SystemClock.elapsedRealtime() - timeInMillis;
                handler.postDelayed(updateTimerRunnable, 0);
                binding.stopButton.setText("Dừng");
                isPaused = false;
            } else {
                // Pause Timer
                handler.removeCallbacks(updateTimerRunnable);
                binding.stopButton.setText("Tiếp Tục");
                isPaused = true;
            }
            updateButtonStates();
        }
    }

    private void stopTimer() {
        if (isRunning) {
            handler.removeCallbacks(updateTimerRunnable);
            isRunning = false;
            updateButtonStates();
        }
    }

    private void resetTimer() {
        handler.removeCallbacks(updateTimerRunnable);
        timeInMillis = 0L;
        binding.timerText.setText("00:00:00");
        isRunning = false;
        isPaused = false;
        updateButtonStates();
    }

    private final Runnable updateTimerRunnable = new Runnable() {
        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            timeInMillis = SystemClock.elapsedRealtime() - startTime;
            int seconds = (int) (timeInMillis / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds %= 60;
            minutes %= 60;
            String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            binding.timerText.setText(time);
            handler.postDelayed(this, 1000);
        }
    };

    private void updateButtonStates() {
        binding.startButton.setEnabled(!isRunning);
        binding.stopButton.setEnabled(isRunning);
        binding.resetButton.setEnabled(isRunning || timeInMillis > 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateTimerRunnable);
    }
}
