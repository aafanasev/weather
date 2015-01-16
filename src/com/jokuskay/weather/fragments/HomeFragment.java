package com.jokuskay.weather.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.jokuskay.weather.models.City;
import com.jokuskay.weather.models.Country;
import com.jokuskay.weather.models.Weather;

import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    public List<Country> mCountries;
    public List<City> mCities;
    public Weather mWeather;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public static HomeFragment findOrCreate(FragmentManager fm) {
        HomeFragment fragment = (HomeFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new HomeFragment();
            fm.beginTransaction().add(fragment, TAG).commit();
        }
        return fragment;
    }

}