package com.example.yourmemories.DataBase.DAOs;

import com.example.yourmemories.DataBase.Model.Memory;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface MemoryDao {
    @Insert
    void addMemory(Memory memory);

     @Query("select * from memory")
    List<Memory> getMmories();

}
