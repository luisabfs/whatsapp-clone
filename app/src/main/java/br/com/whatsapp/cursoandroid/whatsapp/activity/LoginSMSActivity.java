package br.com.whatsapp.cursoandroid.whatsapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.util.Random;

import br.com.whatsapp.cursoandroid.whatsapp.R;
import br.com.whatsapp.cursoandroid.whatsapp.helper.Permissions;
import br.com.whatsapp.cursoandroid.whatsapp.helper.Preferences;

public class LoginSMSActivity extends AppCompatActivity {
    //layout
    private EditText edit_name;
    private EditText edit_country;
    private EditText edit_ddd;
    private EditText edit_phone;
    private Button bt_register;

    //variables
    private String[] permissions = new String[]{
            Manifest.permission.SEND_SMS
    };

    //constants
    private static final String TAG = "LoginSMSActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginsms);


        Permissions.validatePermissions(1,this, permissions);

        edit_name = findViewById(R.id.edit_name);
        edit_country = findViewById(R.id.edit_country);
        edit_ddd = findViewById(R.id.edit_ddd);
        edit_phone = findViewById(R.id.edit_phone);
        bt_register = findViewById(R.id.bt_register);

        //defining phone masks
        SimpleMaskFormatter simpleMaskCOUNTRY = new SimpleMaskFormatter("+NN");
        SimpleMaskFormatter simpleMaskDDD = new SimpleMaskFormatter("NN");
        SimpleMaskFormatter simpleMaskPHONE = new SimpleMaskFormatter("NNNNN-NNNN");

        MaskTextWatcher maskTextCOUNTRY = new MaskTextWatcher(edit_country, simpleMaskCOUNTRY);
        MaskTextWatcher maskTextDDD = new MaskTextWatcher(edit_ddd, simpleMaskDDD);
        MaskTextWatcher maskTextPHONE= new MaskTextWatcher(edit_phone, simpleMaskPHONE);

        edit_country.addTextChangedListener(maskTextCOUNTRY);
        edit_ddd.addTextChangedListener(maskTextDDD);
        edit_phone.addTextChangedListener(maskTextPHONE);

        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = edit_name.getText().toString();
                String formattedPhone = edit_country.getText().toString() +
                        edit_ddd.getText().toString() +
                        edit_phone.getText().toString();

                String notFormattedPhone = formattedPhone.replace("+", "");
                notFormattedPhone = notFormattedPhone.replace("-", "");

                //creating token
                Random random = new Random();
                int randomNumber = random.nextInt(9999 - 1000) + 1000;
                String token = String.valueOf(randomNumber);
                String message = "WhatsApp validation code: " + token;

                Preferences preferences = new Preferences(LoginSMSActivity.this);
                //preferences.saveUserPreferences(userName, notFormattedPhone, token);

                //sending SMS
                boolean sentSMS = sendSMS("+" + notFormattedPhone, message);
                Log.i(TAG, "VARIABLE sentSMS: " + sentSMS);

                if(sentSMS){
                    Intent intent = new Intent(LoginSMSActivity.this, ValidatorActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(LoginSMSActivity.this, "Problem sending SMS, please try again!", Toast.LENGTH_LONG).show();
                }

                //HashMap<String, String> user = preferences.getUserData();
                //Log.i(TAG, "user: N:" + user.get("name") + ", P: " + user.get("phone") + ", T: " + user.get("token"));
            }
        });
    }

    //sending SMS method
    private boolean sendSMS (String phone, String message){
        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);
            Log.i(TAG, "METHOD sendSMS: true");

            return true;
        }catch (Exception e){
            return false;
        }
    }

    //checking if edit texts are filled
    public void validateFields(){

    }

    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int result: grantResults){
            if(result == PackageManager.PERMISSION_DENIED){
                alertValidatePermission();
            }
        }
    }

    private void alertValidatePermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission denied!");
        builder.setMessage("In order to use this app, you need to accept the permissions.");

        builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
