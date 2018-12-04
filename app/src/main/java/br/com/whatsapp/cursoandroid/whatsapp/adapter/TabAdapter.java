package br.com.whatsapp.cursoandroid.whatsapp.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import br.com.whatsapp.cursoandroid.whatsapp.fragment.ChatsFragment;
import br.com.whatsapp.cursoandroid.whatsapp.fragment.ContactsFragment;

public class TabAdapter extends FragmentStatePagerAdapter {
    private String[] tabTitles = {"CHATS", "CONTACTS"};

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position){
            case 0:
                fragment = new ChatsFragment();
                break;
            case 1:
                fragment = new ContactsFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
