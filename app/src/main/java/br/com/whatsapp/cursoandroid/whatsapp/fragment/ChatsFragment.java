package br.com.whatsapp.cursoandroid.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.whatsapp.cursoandroid.whatsapp.R;
import br.com.whatsapp.cursoandroid.whatsapp.activity.ChatActivity;
import br.com.whatsapp.cursoandroid.whatsapp.adapter.ChatAdapter;
import br.com.whatsapp.cursoandroid.whatsapp.adapter.ContactAdapter;
import br.com.whatsapp.cursoandroid.whatsapp.configuration.FirebaseConfigurations;
import br.com.whatsapp.cursoandroid.whatsapp.helper.Base64Custom;
import br.com.whatsapp.cursoandroid.whatsapp.helper.Preferences;
import br.com.whatsapp.cursoandroid.whatsapp.model.Chat;
import br.com.whatsapp.cursoandroid.whatsapp.model.Contact;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {
    /*
   ------------------------------------- OVERRIDE METHODS -----------------------------------------
   */
    @Override
    public void onStart() {
        super.onStart();
        Preferences preferences = new Preferences(getActivity());
        String loggedUserId = preferences.getUserId();

        chatsReference.child(loggedUserId).addValueEventListener(valueEventListenerChats);
        Log.i(TAG, "ValueEventListener: onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        chatsReference.removeEventListener(valueEventListenerChats);
        Log.i(TAG, "ValueEventListener: onStop");
    }

    //lists
    private ListView listView;
    private ArrayAdapter<Chat> adapter;
    private ArrayList<Chat> chats;

    //firebase
    private DatabaseReference databaseReference = FirebaseConfigurations.getFirebaseDatabase();
    private DatabaseReference chatsReference = databaseReference.child("chats");
    private ValueEventListener valueEventListenerChats;

    //constants
    private static final String TAG = "ChatsFragment";


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        //instanciating objects
        chats = new ArrayList<>();
        listView = view.findViewById(R.id.lv_chats);
        adapter = new ChatAdapter(getActivity(), chats);
        listView.setAdapter(adapter);

        //retrieving contacts from Firebase
        valueEventListenerChats = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //cleaning list
                chats.clear();

                //listing contacts
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Chat chat = data.getValue(Chat.class);

                    chats.add(chat);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);

                //retrieving data
                Chat chat = chats.get(position);

                //sending data to ChatActivity
                intent.putExtra("name", chat.getName());
                String email = Base64Custom.decodeBase64(chat.getIdUser());
                intent.putExtra("email", email);

                startActivity(intent);
            }
        });


        return view;
    }

}
