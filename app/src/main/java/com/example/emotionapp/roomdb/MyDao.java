package com.example.emotionapp.roomdb;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import java.util.List;
@androidx.room.Dao
public interface MyDao {
    @Insert
    void insert(Quotes quotes);
    @Delete
    void delete(Quotes quotes);

    @Query("SELECT * FROM quotes")
    LiveData<List<Quotes>> getAllQuotes();
}
