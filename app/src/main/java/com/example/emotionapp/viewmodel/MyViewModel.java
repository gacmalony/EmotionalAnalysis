package com.example.emotionapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.emotionapp.model.MyRepository;
import com.example.emotionapp.roomdb.Quotes;

import java.util.List;

public class MyViewModel extends AndroidViewModel {
    private final MyRepository repository;
    private LiveData<List<Quotes>> quotes;
    public MyViewModel(@NonNull Application application) {
        super(application);

        this.repository = new MyRepository(application);

    }

    public LiveData<List<Quotes>> getAllQuotes(){
        quotes = repository.getAllQuotes();
        return quotes;
    }

    public void addNewQuotes(Quotes quotes){
        repository.addQuotes(quotes);

    }

    public void deleteQuotes(Quotes quotes){
        repository.deleteQuotes(quotes);
    }
}