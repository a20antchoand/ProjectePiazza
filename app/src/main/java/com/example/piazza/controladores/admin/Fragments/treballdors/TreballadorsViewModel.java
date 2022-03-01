package com.example.piazza.controladores.admin.Fragments.treballdors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TreballadorsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TreballadorsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}