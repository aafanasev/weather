package com.jokuskay.weather.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.jokuskay.weather.models.City;
import com.jokuskay.weather.models.Country;

import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private List<Country> mCountries;
    private List<City> mCities;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public List<Country> getCountries() {
        return mCountries;
    }

    public void setCountries(List<Country> countries) {
        mCountries = countries;
    }

    public List<City> getCities() {
        return mCities;
    }

    public void setCities(List<City> cities) {
        mCities = cities;
    }

}
