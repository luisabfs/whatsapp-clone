package br.com.whatsapp.cursoandroid.whatsapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.util.HashMap;

import br.com.whatsapp.cursoandroid.whatsapp.R;
import br.com.whatsapp.cursoandroid.whatsapp.helper.Preferences;

public class ValidatorActivity extends AppCompatActivity {
    //layout
    private EditText edit_code;
    private Button bt_validate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validator);

        edit_code = findViewById(R.id.edit_code);
        bt_validate = findViewById(R.id.bt_validate);

        //defining token mask
        SimpleMaskFormatter simpleMaskCode = new SimpleMaskFormatter("NNNN");
        MaskTextWatcher maskTextCode = new MaskTextWatcher(edit_code, simpleMaskCode);
        edit_code.addTextChangedListener(maskTextCode);

        bt_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retrieving user preferences data
                Preferences preferences = new Preferences(ValidatorActivity.this);
                /*HashMap<String, String> user = preferences.getUserData();

                String generatedToken = user.get("token");
                String typedToken = edit_code.getText().toString();

                if(typedToken.equals(generatedToken)){
                    Toast.makeText(ValidatorActivity.this, "VALID token.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ValidatorActivity.this, "UNVALID token.", Toast.LENGTH_LONG).show();
                }*/
            }
        });
    }
}
