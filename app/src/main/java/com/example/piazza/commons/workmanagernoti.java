package com.example.piazza.commons;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


public class workmanagernoti extends Worker {
    public workmanagernoti(
            @NonNull Context appContext,
            @NonNull WorkerParameters workerParam) {
        super(appContext, workerParam);
    }
            @NonNull
            @Override
    public Result doWork() {

        Notificacio.Notificar(getApplicationContext(), "HEY", "HEY", 10);

        return Result.success();
    }
}
