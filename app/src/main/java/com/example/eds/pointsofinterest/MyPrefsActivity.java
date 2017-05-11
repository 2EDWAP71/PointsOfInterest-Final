package com.example.eds.pointsofinterest;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Eds on 10/05/2017.
 */
public class MyPrefsActivity extends PreferenceActivity{
        public void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }




    }


