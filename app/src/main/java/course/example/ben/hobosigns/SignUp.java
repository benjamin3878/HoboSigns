package course.example.ben.hobosigns;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import static course.example.ben.hobosigns.R.string.*;


/**
 * Created by Ben on 11/17/2015.
 */
public class SignUp extends AppCompatActivity {

    private final static int MIN_USERNAME_LENGTH = 4;
    private final static int MIN_PASSWORD_LENGTH = 4;
    private final static int MAX_USERNAME_LENGTH = 15;
    private final static int MAX_PASSWORD_LENGTH = 15;

    private EditText userNameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        //set up signup form
        userNameEditText = (EditText) findViewById(R.id.signupUserName);
        passwordEditText = (EditText) findViewById(R.id.signupPassword);
        confirmPasswordEditText = (EditText) findViewById(R.id.signupConfirmPassword);

        //Button and listener
        Button joinButton = (Button) findViewById(R.id.join);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO - redirect to home if submit completed
                //TODO - make this redirect dependent on signup db call
                signUp();
            }
        });
    }

    /*
     * Register new user
     * return false on fail
     */
    private void signUp(){
        String userName = userNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if(!validate(userName,password,confirmPassword))return;

        // set up a new ParseUser
        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(userName);
        parseUser.setPassword(password);

        parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {

                if(e != null){
                    //error
                    Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent(SignUp.this, Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    /*
     * Validate form fields
     * returns true is valid, otherwise false
     * Todo - add more validation??
     */
    private boolean validate(String userName, String password, String confirmPassword){
        // Validate sign up data
        if(userName.length() < MIN_USERNAME_LENGTH){
            highLightError(userNameEditText);
            Toast.makeText(getApplicationContext(), getString(user_name_must_be_at_least) + " " +
                            " " + MIN_USERNAME_LENGTH + " " + getString(characters_long),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if(password.length() < MIN_PASSWORD_LENGTH){
            highLightError(passwordEditText);
            Toast.makeText(getApplicationContext(), getString(password_must_be_at_least) + " "
                            + MIN_PASSWORD_LENGTH + " " + getString(characters_long),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if(userName.length() > MAX_USERNAME_LENGTH){
            highLightError(userNameEditText);
            Toast.makeText(getApplicationContext(), getString(username_must_be_less_than) + " "
                            + MAX_USERNAME_LENGTH + " " + getString(characters_long),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if(password.length() > MAX_PASSWORD_LENGTH){
            highLightError(passwordEditText);
            Toast.makeText(getApplicationContext(), getString(password_must_be_less_than) + " "
                            + MAX_PASSWORD_LENGTH + " " + getString(characters_long),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if(!password.equals(confirmPassword)){
            highLightError(confirmPasswordEditText);
            Toast.makeText(getApplicationContext(),
                    getString(password_and_confirm_password_must_be_equal),Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    /*
     * Todo - highlight the EditText field editText which is the cause of an error and clear field
     */
    private void highLightError(EditText editText){

    }

}
