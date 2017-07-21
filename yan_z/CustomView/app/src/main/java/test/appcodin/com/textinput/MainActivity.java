package test.appcodin.com.textinput;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextInput textInput = (TextInput) findViewById(R.id.textInput);
        textInput.setEventListener(new TextInput.IEventListener() {
            @Override
            public void onMaxLength() {
                Log.d(TAG, "onMaxLength");
            }

            @Override
            public void oTextChange(String enteredText) {
                Log.d(TAG, "oTextChange ->"+enteredText);

            }
        });

        Button getText = (Button) findViewById(R.id.getText);
        getText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, textInput.getText(), Toast.LENGTH_LONG).show();
            }
        });
        Button clearText = (Button) findViewById(R.id.clearText);
        clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textInput.clearText();
            }
        });

        Button showError = (Button) findViewById(R.id.showError);
        showError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textInput.showError("ERROR!!!");
            }
        });
        Button clearError = (Button) findViewById(R.id.clearError);
        clearError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textInput.clearError();
            }
        });
        Button focus = (Button) findViewById(R.id.focus);
        focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textInput.focus();
            }
        });


    }
}
