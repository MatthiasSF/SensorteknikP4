package com.example.matth.project4.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.LinkedList;

/**
 * Interface used for communicating with the database
 * @author Matthias Falk
 */
@Dao
public interface DatabaseAccess {
    @Insert
    void insertUser(User_table... user);

    @Query("SELECT Password FROM user_table where Username = :userName")
    String getPassword(String userName);

    @Insert
    void insertNewUserStep (Step_history... step);

    @Query("UPDATE Step_history_table set Steps = :step where Username = :userName")
    void insertStep (int step, String userName);

    @Query("UPDATE Step_history_table set Timestamp = :timestamp where Username = :userName")
    void insertTimestamp (long timestamp, String userName);

    @Query("SELECT Timestamp FROM step_history_table where Username = :userName")
    long getTimestamp(String userName);

    @Query("SELECT Steps FROM step_history_table where Username = :userName")
    int getSteps(String userName);

    @Query("SELECT Username FROM user_table")
    String[] usernames();

    @Query("DELETE FROM step_history_table where Username = :userName")
    void deleteAllSteps(String userName);
}
