package com.example.matth.project4;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.example.matth.project4.Database.DatabaseAccess;
import com.example.matth.project4.Database.P4_Database;
import com.example.matth.project4.Database.Step_history;
import com.example.matth.project4.Database.User_table;

import java.util.LinkedList;

/**
 * Class that uses the interface DatabaseAccess for communicating with the database
 * @author Matthias Falk
 */
public class DBAccess implements DatabaseAccess {
    private static final String DATABASE_Name = "P4_Database";
    private P4_Database database;
    private DatabaseAccess p4Access;

    public DBAccess(Context context){
        database = Room.databaseBuilder(context,
                P4_Database.class,
                DATABASE_Name)
                .fallbackToDestructiveMigration()
                .build();
        p4Access = database.databaseAccess();
    }
    @Override
    public void insertUser(User_table... user) {
        p4Access.insertUser(user);
    }

    @Override
    public String getPassword(String userId) {
        return p4Access.getPassword(userId);
    }

    @Override
    public void insertNewUserStep(Step_history... step) {
        p4Access.insertNewUserStep(step);
    }

    @Override
    public void insertStep(int step, String userName) {
        p4Access.insertStep(step, userName);
    }

    @Override
    public void insertTimestamp(long timestamp, String userName) {
        p4Access.insertTimestamp(timestamp, userName);
    }

    @Override
    public long getTimestamp(String userName) {
        return p4Access.getTimestamp(userName);
    }

    @Override
    public int getSteps(String userName) {
        return p4Access.getSteps(userName);
    }

    @Override
    public String[] usernames() {
        return p4Access.usernames();
    }

    @Override
    public void deleteAllSteps(String userName) {

    }
}
