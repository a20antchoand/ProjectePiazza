package com.example.piazza.ui.introduir_hores;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IntroduirHoresViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public IntroduirHoresViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is introduir hores fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}