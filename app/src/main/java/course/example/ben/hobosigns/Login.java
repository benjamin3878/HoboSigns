package course.example.ben.hobosigns;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import static course.example.ben.hobosigns.R.string.characters_long;
import static course.example.ben.hobosigns.R.string.password_must_be_at_least;
import static course.example.ben.hobosigns.R.string.password_must_be_less_than;
import static course.example.ben.hobosigns.R.string.user_name_must_be_at_least;
import static course.example.ben.hobosigns.R.string.username_must_be_less_than;

/**
 * Created by Ben on 11/17/2015.
 */
public class Login extends AppCompatActivity {

    private final static int MIN_USERNAME_LENGTH = 4;
    private final static int MIN_PASSWORD_LENGTH = 4;
    private final static int MAX_USERNAME_LENGTH = 15;
    private final static int MAX_PASSWORD_LENGTH = 15;
    private EditText userNameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // set up form
        userNameEditText = (EditText) findViewById(R.id.loginUserName);
        passwordEditText = (EditText) findViewById(R.id.loginPassword);

        ParseUser parseUser = new ParseUser();

        Button joinButton = (Button) findViewById(R.id.login);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login(){
        String userName = userNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(!localValidate(userName,password))return;

        ParseUser.logInInBackground(userName, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(Login.this, Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean localValidate(String userName, String password){
        if(userName.length() < MIN_USERNAME_LENGTH){
            Toast.makeText(getApplicationContext(), getString(user_name_must_be_at_least) + " " +
                            " " + MIN_USERNAME_LENGTH + " " + getString(characters_long),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if(password.length() < MIN_PASSWORD_LENGTH){
            Toast.makeText(getApplicationContext(), getString(password_must_be_at_least) + " "
                            + MIN_PASSWORD_LENGTH + " " + getString(characters_long),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if(userName.length() > MAX_USERNAME_LENGTH){
            Toast.makeText(getApplicationContext(), getString(username_must_be_less_than) + " "
                            + MAX_USERNAME_LENGTH + " " + getString(characters_long),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if(password.length() > MAX_PASSWORD_LENGTH){
            Toast.makeText(getApplicationContext(), getString(password_must_be_less_than) + " "
                            + MAX_PASSWORD_LENGTH + " " + getString(characters_long),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}