package com.example.matth.project4;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.LinkedList;

/**
 * Activity used for registering an new account
 * @author Matthias Falk
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText nameInput;
    private EditText passInput;
    private Button btnOk;
    private Controller controller;

    /**
     * Basic onCreate method that calls initialize
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialize();
    }

    /**
     * initializes all of the components in the activity
     */
    private void initialize() {
        controller = new Controller(this);
        nameInput = findViewById(R.id.regAc_nameInput);
        passInput = findViewById(R.id.regAc_passInput);
        btnOk = findViewById(R.id.regAc_buttonReg);
        btnOk.setOnClickListener(new ButtonListener(this));
    }

    /**
     * Inner class that handles the button btnOk
     * Checks if the username exists before registering
     */
    private class ButtonListener implements View.OnClickListener{
        Context context;
        private ButtonListener(Context context){
            this.context = context;
        }
        @Override
        public void onClick(View v) {
            String userName = String.valueOf(nameInput.getText());
            String password = String.valueOf(passInput.getText());
            String[] userNames = controller.getUsernames();
            if (userNames.length >= 1) {
                for (int i = 0; i < userNames.length; i++) {
                    if (userName.equals(userNames[i])) {
                        Toast.makeText(context, "That username already exists", Toast.LENGTH_LONG).show();
                        break;
                    } else {
                        controller.registerNewUser(userName, password);
                        Intent intent = new Intent(context, UIActivity.class);
                        intent.putExtra("Username", userName);
                        startActivity(intent);
                    }
                }
            }else{
                controller.registerNewUser(userName, password);
                Intent intent = new Intent(context, UIActivity.class);
                intent.putExtra("Username", userName);
                startActivity(intent);
            }
        }
    }
}
