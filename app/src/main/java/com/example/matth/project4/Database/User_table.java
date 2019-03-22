package com.example.matth.project4.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Table for the user. Contains the usernames and the password saved.
 * @author Matthias Falk
 */
@Entity(tableName = "user_table")
public class User_table {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "Username")
    private String userName;

    @NonNull
    @ColumnInfo(name = "Password")
    private String password;

    public User_table(String userName, String password){
        this.userName = userName;
        this.password = password;
    }
    public String getUserName() {
        return userName;
    }
    public String getPassword(){
        return password;
    }
}
