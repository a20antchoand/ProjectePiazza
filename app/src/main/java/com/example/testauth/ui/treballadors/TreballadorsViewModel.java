package com.example.testauth.ui.treballadors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TreballadorsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TreballadorsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Treballadors fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}