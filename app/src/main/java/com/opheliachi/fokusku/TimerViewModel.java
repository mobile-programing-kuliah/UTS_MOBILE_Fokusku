package com.opheliachi.fokusku;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class TimerViewModel extends ViewModel {

    public LiveData<String> getTimeText() {
        return TimerService.timeText;
    }

    public LiveData<Integer> getProgress() {
        return TimerService.progress;
    }

    public LiveData<Boolean> getIsFinished() {
        return TimerService.isFinished;
    }
}