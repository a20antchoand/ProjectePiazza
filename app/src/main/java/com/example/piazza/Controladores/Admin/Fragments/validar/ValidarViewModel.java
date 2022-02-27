package com.example.piazza.Controladores.Admin.Fragments.validar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ValidarViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ValidarViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hores per validar");
    }

    public LiveData<String> getText() {
        return mText;
    }
}