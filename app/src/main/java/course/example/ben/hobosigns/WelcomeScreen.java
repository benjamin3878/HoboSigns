package course.example.ben.hobosigns;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;

/**
 * Created by Ben on 11/18/2015.
 */
public class WelcomeScreen extends AppCompatActivity {
    //hello
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in
        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
            startActivity(new Intent(this, Home.class));
        }else{
            setContentView(R.layout.welcome_screen);

            Button loginButton = (Button) findViewById(R.id.login);
            Button signUpButton = (Button) findViewById(R.id.sign_up);

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Login.class);
                    startActivity(intent);
                }
            });

            signUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), SignUp.class);
                    startActivity(intent);
                }
            });
        }
    }
}
