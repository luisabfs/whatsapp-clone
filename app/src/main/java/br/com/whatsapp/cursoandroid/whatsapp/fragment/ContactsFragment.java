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
import br.com.whatsapp.cursoandroid.whatsapp.adapter.ContactAdapter;
import br.com.whatsapp.cursoandroid.whatsapp.configuration.FirebaseConfigurations;
import br.com.whatsapp.cursoandroid.whatsapp.helper.Preferences;
import br.com.whatsapp.cursoandroid.whatsapp.model.Contact;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {
    /*
    ------------------------------------- OVERRIDE METHODS -----------------------------------------
    */
    @Override
    public void onStart() {
        super.onStart();
        Preferences preferences = new Preferences(getActivity());
        String loggedUserId = preferences.getUserId();

        contactsReference.child(loggedUserId).addValueEventListener(valueEventListenerContacts);
        Log.i(TAG, "ValueEventListener: onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        contactsReference.removeEventListener(valueEventListenerContacts);
        Log.i(TAG, "ValueEventListener: onStop");
    }

    //lists
    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Contact> contacts;

    //firebase
    private DatabaseReference databaseReference = FirebaseConfigurations.getFirebaseDatabase();
    private DatabaseReference  contactsReference = databaseReference.child("contacts");
    private ValueEventListener valueEventListenerContacts;

    //constants
    private static final String TAG = "ContactsFragment";


    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        //instanciating objects
        contacts = new ArrayList<>();
        listView = view.findViewById(R.id.lv_contacts);
        adapter = new ContactAdapter(getActivity(), contacts);
        listView.setAdapter(adapter);

        //retrieving contacts from Firebase
        valueEventListenerContacts = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //cleaning list
                contacts.clear();

                //listing contacts
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Contact contact = data.getValue(Contact.class);

                    contacts.add(contact);
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
                Contact contact = contacts.get(position);

                //sending data to ChatActivity
                intent.putExtra("name", contact.getName());
                intent.putExtra("email", contact.getEmail());

                startActivity(intent);
            }
        });

        return view;
    }

}
