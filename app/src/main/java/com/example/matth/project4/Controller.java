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
    private String[] userNames;
    private String userName;
    private String password;
    private static Controller instance;
    private UIActivity uiActivity;

    /**
     * Constructor fro the class
     * @param context - the active context
     */
    public Controller(Context context){
        this.dbAccess = new DBAccess(context);
    }
    public Controller(UIActivity uiActivity){
        this.dbAccess = new DBAccess(uiActivity);
        this.uiActivity = uiActivity;
        instance = this;
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
    public static Controller getInstance(Context context){
        if (instance == null){
            instance = new Controller(context);
        }
        return instance;
    }

    /**
     * @return all of the usernames saved in the database
     */
    public String[] getUsernames(){
        t = new Thread(new Runnable() {
            public void run() {
                userNames = dbAccess.usernames();
            }
        });
        t.start();
        try{
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return userNames;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }
    public String getUserName(){
        return this.userName;
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
                uiActivity.setStepsTV(steps[0]);
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
                getSteps(userName);
            }
        });
        t.start();
        try{
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public long getStepTimestamp(){
        final long timestamp[] = new long[1];
        t = new Thread(new Runnable() {
            public void run() {
                timestamp[0] = dbAccess.getTimestamp(userName);
            }
        });
        t.start();
        try{
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return timestamp[0];
    }
    public void setTimeStamp(final long stamp){
        t = new Thread(new Runnable() {
            public void run() {
                dbAccess.insertTimestamp(stamp, userName);
            }
        });
        t.start();
        try{
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void deleteSteps(){
        t = new Thread(new Runnable() {
            public void run() {
                dbAccess.deleteAllSteps(userName);
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
