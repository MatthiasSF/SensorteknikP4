package com.example.matth.project4;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * The first activity of the app. Let's the user login with username - password or choose to register an new account
 * @author Matthias Falk
 */
public class StartActivity extends AppCompatActivity {
    private EditText nameInput;
    private EditText passInput;
    private Button btnLogIn;
    private Button btnRegister;
    private Controller controller;

    /**
     * Basic onCreate
     * Calls the method initialize
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initialize();
    }

    /**
     * Initializes all of the components of the app
     */
    private void initialize(){
        controller = new Controller(this);
        nameInput = findViewById(R.id.startAc_nameInput);
        passInput = findViewById(R.id.startAc_password);
        btnLogIn = findViewById(R.id.startAc_buttonLogin);
        btnRegister = findViewById(R.id.startAc_buttonReg);
        btnLogIn.setOnClickListener(new ButtonListener(this));
        btnRegister.setOnClickListener(new ButtonListener(this));
    }

    /**
     * Starts up the RegisterActivity
     */
    public void startRegActivity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Starts up the UIActivity
     * @param userName - username of the current user
     */
    public void startUiActivity(String userName){
        Intent intent = new Intent(this, UIActivity.class);
        intent.putExtra("Username", userName);
        startActivity(intent);
    }

    /**
     * Gets all the registered usernames from the db via the controller class
     * @return
     */
    public String[] getUsernames(){
        return controller.getUsernames();
    }

    /**
     * Gets the password for the username from the db via the controller class
     * @param userName - the targeted username
     * @return
     */
    public String getPassword(String userName){
        return controller.getPassword(userName);
    }

    /**
     * Inner class that handles the log in button and the register button.
     * If the login button is pressed it checks if the user is registered and if the password matches the user
     */
    private class ButtonListener implements View.OnClickListener{
        Context context;
        private ButtonListener(Context context){
            this.context = context;
        }
        @Override
        public void onClick(View v) {
            if (v.getId() == btnLogIn.getId()){
                String userName = String.valueOf(nameInput.getText());
                String password = String.valueOf(passInput.getText());
                String[] userNames = getUsernames();
                if (userNames.length >= 1) {
                    for (int i = 0; i < userNames.length; i++) {
                        if (userNames[i].equals(userName)) {
                            if (password.equals(getPassword(userName))) {
                                startUiActivity(userName);
                            } else {
                                Toast.makeText(context, "Password didn't match", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(context, "Username doesn't exist", Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    Toast.makeText(context, "You have to register an user first", Toast.LENGTH_LONG).show();
                }

            }
            if (v.getId() == btnRegister.getId()){
                startRegActivity();
            }
        }
    }
}
