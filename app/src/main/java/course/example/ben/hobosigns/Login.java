package course.example.ben.hobosigns;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Ben on 11/17/2015.
 */
public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button joinButton = (Button) findViewById(R.id.login);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO - check form info and query database for login
                //if successful, redirect to home


                //TODO - make this redirect dependent on login db call
                Intent intent = new Intent(v.getContext(), Home.class);
                startActivity(intent);
            }
        });
    }

}