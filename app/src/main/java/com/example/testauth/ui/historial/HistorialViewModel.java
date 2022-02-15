package com.example.testauth.ui.historial;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HistorialViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HistorialViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Historial fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}