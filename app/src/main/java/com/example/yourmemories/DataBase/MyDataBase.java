package com.example.yourmemories.DataBase;

import android.content.Context;

import com.example.yourmemories.DataBase.DAOs.MemoryDao;
import com.example.yourmemories.DataBase.Model.Memory;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Memory.class}, version = 1, exportSchema = false)
public abstract class MyDataBase extends RoomDatabase {
    private static MyDataBase dataBase;

    private static final String DB_NAME = "MemoriesDataBase";

    public abstract MemoryDao memoryDao();

    public MyDataBase() {
    }

    public static MyDataBase getInstance(Context context) {
        if (dataBase == null) {
            // Create database
            dataBase = Room.databaseBuilder(context, MyDataBase.class,
                    DB_NAME).fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return dataBase;
    }

}