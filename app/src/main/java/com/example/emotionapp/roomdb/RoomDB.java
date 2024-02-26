package com.example.emotionapp.roomdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {Quotes.class},version = 1)
public abstract class RoomDB extends RoomDatabase{

    public abstract MyDao getContactDAO();

    private static RoomDB dbInstance;


    public static synchronized RoomDB getInstance(Context context){
        if(dbInstance == null){
            dbInstance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            RoomDB.class,
                            "quotes_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return dbInstance;
    }





}
