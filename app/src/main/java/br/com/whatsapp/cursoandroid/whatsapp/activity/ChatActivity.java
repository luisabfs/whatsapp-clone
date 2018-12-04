package br.com.whatsapp.cursoandroid.whatsapp.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.whatsapp.cursoandroid.whatsapp.R;
import br.com.whatsapp.cursoandroid.whatsapp.adapter.MessageAdapter;
import br.com.whatsapp.cursoandroid.whatsapp.configuration.FirebaseConfigurations;
import br.com.whatsapp.cursoandroid.whatsapp.helper.Base64Custom;
import br.com.whatsapp.cursoandroid.whatsapp.helper.Preferences;
import br.com.whatsapp.cursoandroid.whatsapp.model.Chat;
import br.com.whatsapp.cursoandroid.whatsapp.model.Message;

public class ChatActivity extends AppCompatActivity {
    /*
    ------------------------------------- OVERRIDE METHODS -----------------------------------------
     */
    @Override
    protected void onStop() {
        super.onStop();
        messageReference.removeEventListener(valueEventListenerMessage);
        Log.i(TAG, "ValueEventListener: onStop");
    }

    //layout
    private Toolbar toolbar;
    private EditText edit_message;
    private ImageButton bt_send;

    //lists
    private ListView listView;
    private ArrayList<Message> messages;
    private ArrayAdapter<Message> adapter;

    //firebase
    private DatabaseReference databaseReference = FirebaseConfigurations.getFirebaseDatabase();
    private DatabaseReference messageReference = databaseReference.child("messages");
    private DatabaseReference chatReference = databaseReference.child("chats");
    private ValueEventListener valueEventListenerMessage;

    //data receiver
    private String nameReceiver;
    private String emailReceiver;
    private String idReceiver;

    //data sender
    private String idSender;
    private String nameSender;

    //constants
    private static final String TAG = "ChatActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.toolbar);
        edit_message = findViewById(R.id.edit_message);
        bt_send = findViewById(R.id.bt_send);
        listView = findViewById(R.id.lv_chats);

        //logged user data
        Preferences preferences = new Preferences(ChatActivity.this);
        idSender = preferences.getUserId();
        nameSender = preferences.getUserName();
        Log.i(TAG, "idSender: "+idSender);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            nameReceiver = bundle.getString("name");
            emailReceiver = bundle.getString("email");
            idReceiver = Base64Custom.encodeBase64(emailReceiver);
            Log.i(TAG, "idReceiver: "+idReceiver);
        }

        toolbar.setTitle(nameReceiver);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        //setting listview and adapter
        messages = new ArrayList<>();
        adapter = new MessageAdapter(ChatActivity.this, messages);
        listView.setAdapter(adapter);


        valueEventListenerMessage = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();

                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Message message = data.getValue(Message.class);
                    messages.add(message);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        //retrieving messages
        messageReference.child(idSender)
                .child(idReceiver)
                .addValueEventListener(valueEventListenerMessage);

        //send message
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = edit_message.getText().toString();

                if(messageText.isEmpty()){

                }else{
                    Message message = new Message();
                    message.setUserId(idSender);
                    message.setMessage(messageText);

                    //saving message for sender
                    Boolean messageSenderReturn = saveMessage(idSender, idReceiver, message);
                    if(!messageSenderReturn){
                        Log.i(TAG, "messageSenderReturn: error saving the message");
                        Toast.makeText(ChatActivity.this, "Problem saving the message, please try again!", Toast.LENGTH_LONG).show();
                    }else{
                        //saving message for receiver
                        Log.i(TAG, "messageReturn: sucess");
                        Boolean messageReceiverReturn = saveMessage(idReceiver, idSender, message);
                        if(!messageReceiverReturn){
                            Log.i(TAG, "messageReceiverReturn: error sending the message");
                            Toast.makeText(ChatActivity.this, "Problem sending the message, please try again!", Toast.LENGTH_LONG).show();
                        }
                    }

                    //saving Chat for sender
                    Chat chat = new Chat();
                    chat.setIdUser(idReceiver);
                    chat.setName(nameReceiver);
                    chat.setMessage(messageText);

                    Boolean chatSenderReturn = saveChat(idSender, idReceiver, chat);
                    if(!chatSenderReturn){
                        Log.i(TAG, "chatSenderReturn: error saving the chat");
                        Toast.makeText(ChatActivity.this, "Problem saving the chat, please try again!", Toast.LENGTH_LONG).show();
                    }else{
                        //saving Chat for receiver
                        Log.i(TAG, "chatReturn: sucess");
                        chat = new Chat();
                        chat.setIdUser(idSender);
                        chat.setName(nameSender);
                        chat.setMessage(messageText);
                        Boolean chatReceiverReturn = saveChat(idReceiver, idSender, chat);

                        if(!chatReceiverReturn){
                            Log.i(TAG, "chatReceiverReturn: error saving the chat for receiver");
                            Toast.makeText(ChatActivity.this, "Problem saving the chat for receiver, please try again!", Toast.LENGTH_LONG).show();
                        }
                    }

                    edit_message.setText("");
                }
            }
        });
    }

    private boolean saveMessage(String idSender, String idReceiver, Message message){
        try{
            messageReference.child(idSender)
                    .child(idReceiver)
                    .push()
                    .setValue(message);


           return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean saveChat(String idSender, String idReceiver, Chat chat){
        try{
            chatReference.child(idSender)
                    .child(idReceiver)
                    .setValue(chat);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
