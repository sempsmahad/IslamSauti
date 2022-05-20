package com.example.myapplication.utils;

import com.dinuscxj.progressbar.CircleProgressBar;

public class MyProgressFromatter implements CircleProgressBar.ProgressFormatter {
    private static final String DEFAULT_PATTERN = "%d%%";

    @Override
    public CharSequence format(int progress, int max) {
        return String.format(DEFAULT_PATTERN, (int) ((float) progress / (float) max * 100));
    }
}
