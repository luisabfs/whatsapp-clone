package br.com.whatsapp.cursoandroid.whatsapp.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.whatsapp.cursoandroid.whatsapp.R;
import br.com.whatsapp.cursoandroid.whatsapp.adapter.TabAdapter;
import br.com.whatsapp.cursoandroid.whatsapp.configuration.FirebaseConfigurations;
import br.com.whatsapp.cursoandroid.whatsapp.helper.Base64Custom;
import br.com.whatsapp.cursoandroid.whatsapp.helper.Preferences;
import br.com.whatsapp.cursoandroid.whatsapp.helper.SlidingTabLayout;
import br.com.whatsapp.cursoandroid.whatsapp.model.Contact;
import br.com.whatsapp.cursoandroid.whatsapp.model.User;

public class MainActivity extends AppCompatActivity {
    //layout
    private Toolbar toolbar;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;

    //variables
    private String contactId;

    //firebase
    private FirebaseAuth firebaseAuth = FirebaseConfigurations.getFirebaseAuth();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private DatabaseReference databaseReference = FirebaseConfigurations.getFirebaseDatabase();
    private DatabaseReference userReference = databaseReference.child("users");
    private DatabaseReference contactsReference = databaseReference.child("contacts");

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);

        slidingTabLayout = findViewById(R.id.stl_tabs);
        viewPager = findViewById(R.id.vp_page);

        //setting sliding tabs
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorAccent));

        //setting adapter
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_exit:
                logoutUser();
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_add:
                openUserRegister();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("ResourceAsColor")
    private void openUserRegister() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        //setting dialog
        alertDialog.setTitle("New Contact");
        alertDialog.setMessage("User e-mail");
        alertDialog.setCancelable(false);

        final EditText editText = new EditText(MainActivity.this);
        alertDialog.setView(editText);

        //setting buttons
        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String contactEmail = editText.getText().toString();

                //validating the e-mail
                if(contactEmail.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please, enter the e-mail.", Toast.LENGTH_LONG).show();
                }else{
                    //verifying if the user is registered in the app
                    contactId = Base64Custom.encodeBase64(contactEmail);

                    //retrieving firebase reference
                    userReference.child(contactId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() != null){
                                //retrieving contact data as User object
                                User userContact = dataSnapshot.getValue(User.class);

                                //retrieving user id (base64)
                                Preferences preferences = new Preferences(MainActivity.this);
                                String loggedUserId = preferences.getUserId();

                                //setting contact data in Contact object
                                Contact contact = new Contact();
                                contact.setUserId(contactId);
                                contact.setEmail(userContact.getEmail());
                                contact.setName(userContact.getName());

                                //saving Contact data
                                contactsReference.child(loggedUserId)
                                        .child(contactId)
                                        .setValue(contact);
                            }else{
                                Toast.makeText(MainActivity.this, "User not registered.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.create().show();
    }

    public void logoutUser(){
        firebaseAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
