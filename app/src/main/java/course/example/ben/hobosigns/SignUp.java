package course.example.ben.hobosigns;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Ben on 11/17/2015.
 */
public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        Button joinButton = (Button) findViewById(R.id.join);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO - check user input
                //make sure user name is great than x characters
                //password matches confirmPassword
                //maybe add email?
                //If not good, highlight box with error
                //else submit to database

                //TODO - redirect to home if submit completed
                //TODO - make this redirect dependent on signup db call
                Intent intent = new Intent(v.getContext(), Home.class);
                startActivity(intent);
            }
        });
    }
}
