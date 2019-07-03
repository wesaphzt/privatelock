package com.wesaphzt.privatelock.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.Menu;
import android.view.View;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.wesaphzt.privatelock.R;

public class FragmentSettings extends PreferenceFragmentCompat {

    private SharedPreferences prefs;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        setHasOptionsMenu(true);
        Context context = getContext();

        //this static call will reset default values only on the first read
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set title
        getActivity().setTitle("Settings");

        //bg color
        view.setBackgroundColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //hide action bar menu
        menu.setGroupVisible(R.id.menu_main, false);

        super.onPrepareOptionsMenu(menu);
    }
}