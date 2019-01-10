package com.example.matth.project4.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * The table which contains the stephistory
 * @author Matthias Falk
 */
@Entity(tableName = "Step_history_table")
public class Step_history {
    @NonNull
    @PrimaryKey
    @ColumnInfo (name = "Username")
    @ForeignKey(entity = User_table.class, parentColumns = "Username", childColumns = "Username")
    private String userName;

    @ColumnInfo (name = "Steps")
    private int steps;

    @ColumnInfo (name = "Timestamp")
    private int timestamp;

    public Step_history (String userName, int steps, int timestamp){
        this.userName = userName;
        this.steps = steps;
        this.timestamp = timestamp;
    }
    public int getSteps(int userName){
        return steps;
    }
    public void setSteps(int steps){
        this.steps = steps;
    }
    public int getTimestamp(int userName) {
        return timestamp;
    }
    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
    public void setUserName(@NonNull String userName) {
        this.userName = userName;
    }
    public String getUserName(){
        return userName;
    }
    public int getSteps() {
        return steps;
    }

    public int getTimestamp() {
        return timestamp;
    }

}
