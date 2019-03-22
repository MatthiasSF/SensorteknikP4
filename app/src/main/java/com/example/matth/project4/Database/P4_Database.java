package com.example.matth.project4.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {User_table.class, Step_history.class}, version = 3, exportSchema = false)
public abstract class P4_Database extends RoomDatabase {
    public abstract DatabaseAccess databaseAccess();
}
