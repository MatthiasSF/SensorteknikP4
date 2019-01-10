package com.example.matth.project4;

import android.content.Context;

import com.example.matth.project4.Database.Step_history;
import com.example.matth.project4.Database.User_table;

/**
 * Controller class that handles the communication with the database
 * @author Matthias Falk
 */
public class Controller {
    private DBAccess dbAccess;
    private Thread t;
    private String[] usernames;
    private String password;

    /**
     * Constructor fro the class
     * @param context - the active context
     */
    public Controller(Context context){
        this.dbAccess = new DBAccess(context);
    }

    /**
     * Gets the password for username set in the parameter
     * @param username - the username
     * @return the password
     */
    public String getPassword(final String username){
        t = new Thread(new Runnable() {
            public void run() {
        password = dbAccess.getPassword(username);
            }
        });
        t.start();
        try{
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(password);
        return password;
    }

    /**
     * @return all of the usernames saved in the database
     */
    public String[] getUsernames(){
        t = new Thread(new Runnable() {
            public void run() {
                usernames = dbAccess.usernames();
            }
        });
        t.start();
        try{
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return usernames;
    }

    /**
     * Registers an new user in the usertable
     * @param userName the username of the user
     * @param password the password of the user
     */
    public void registerNewUser(final String userName, final String password){
        t = new Thread(new Runnable() {
            public void run() {
                User_table user = new User_table(userName, password);
                dbAccess.insertUser(user);
                Step_history step = new Step_history(userName, 0, 0);
                dbAccess.insertNewUserStep(step);
            }
        });
        t.start();
        try{
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the steps saved under the username
     * @param userName the username of the current user
     * @return the steps taken
     */
    public int getSteps(final String userName){
        final int[] steps = new int[1];
        t = new Thread(new Runnable() {
            public void run() {
                steps[0] = dbAccess.getSteps(userName);
            }
        });
        t.start();
        try{
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return steps[0];
    }

    /**
     * Inserts an new step to the assigned user
     * @param steps the number of steps
     * @param userName the user where the steps will be saved to
     */
    public void setSteps(final int steps, final String userName){
        t = new Thread(new Runnable() {
            public void run() {
                dbAccess.insertStep(steps, userName);
            }
        });
        t.start();
        try{
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
