package com.example.emotionapp.model;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;


import com.example.emotionapp.roomdb.MyDao;
import com.example.emotionapp.roomdb.Quotes;
import com.example.emotionapp.roomdb.RoomDB;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyRepository {

    public ExecutorService executorService;
    public Handler handler;

    private final MyDao myDao;


    public MyRepository(Application application) {
        RoomDB roomDB = RoomDB.getInstance(application);
        this.myDao = roomDB.getContactDAO();
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
    }



    public void addQuotes(Quotes quotes){

        executorService.execute(() -> myDao.insert(quotes));

    }
    public void deleteQuotes(Quotes quotes){
        executorService.execute(() -> myDao.delete(quotes));

    }

    public LiveData<List<Quotes>> getAllQuotes(){
        return myDao.getAllQuotes();

    }
}