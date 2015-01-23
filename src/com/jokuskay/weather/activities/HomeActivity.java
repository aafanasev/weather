package com.jokuskay.weather.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.jokuskay.weather.App;
import com.jokuskay.weather.R;
import com.jokuskay.weather.fragments.HomeFragment;
import com.jokuskay.weather.helpers.Constants;
import com.jokuskay.weather.models.City;
import com.jokuskay.weather.models.Country;
import com.jokuskay.weather.models.Weather;
import com.jokuskay.weather.services.LoadCities;
import com.jokuskay.weather.services.LoadWeather;
import com.squareup.picasso.Picasso;

public class HomeActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private static final int OPTION_UPDATE = 1;

    private App mApp;
    private HomeFragment mDataFragment;

    private LinearLayout mLayout;
    private ProgressBar mProgress;
    private Spinner mCountries;
    private Spinner mCities;
    private TextView mText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mApp = (App) getApplication();
        mDataFragment = HomeFragment.findOrCreate(getSupportFragmentManager());

        mLayout = (LinearLayout) findViewById(R.id.layout);
        mProgress = (ProgressBar) findViewById(R.id.progress);

        mCountries = (Spinner) findViewById(R.id.spinner_countries);
        mCities = (Spinner) findViewById(R.id.spinner_cities);

        mCountries.setOnItemSelectedListener(this);
        mCities.setOnItemSelectedListener(this);

        mText = (TextView) findViewById(R.id.weather_text);

    }

    private void setUi() {
        mLayout.setVisibility(View.VISIBLE);
        mProgress.setVisibility(View.GONE);

        setCountries();
    }

    private void loadData() {
        Log.d(TAG, "loadData");

        showProgress();
        startService(new Intent(this, LoadCities.class));
    }

    private void showProgress() {
        mLayout.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
    }

    private void setCountries() {
        Log.d(TAG, "setCountries");
        if (mDataFragment.mCountries == null) {
            mDataFragment.mCountries = Country.getAll(HomeActivity.this);
        }

        // re-create or use clear() add()
        ArrayAdapter<Country> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mDataFragment.mCountries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCountries.setAdapter(adapter);
    }

    private void setCities(int countryId) {
        Log.d(TAG, "setCities: " + countryId);
        mDataFragment.mCities = City.getByCountryId(this, countryId);

        Log.d(TAG, "Cities count: " + mDataFragment.mCities.size());

        // re-create or use clear() add()
        ArrayAdapter<City> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mDataFragment.mCities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCities.setAdapter(adapter);
    }

    private void setWeather() {
        ImageView image = (ImageView) findViewById(R.id.weather_image);

        if (mDataFragment.mWeather == null) {
            image.setImageResource(R.drawable.ic_launcher);
            mText.setText("Ошибка, попробуйте еще");
        } else {
            Picasso.with(this).load(mDataFragment.mWeather.getTemperature() < 0 ? Constants.IMG_MINUS : Constants.IMG_PLUS).into(image);
            mText.setText(mDataFragment.mWeather.getTemperature() + ", " + mDataFragment.mWeather.getText());
        }
    }

    @Override
    protected void onResume() {
        super.onPostResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(getPackageName()));

        if (isMyServiceRunning(LoadCities.class)) {
            showProgress();
        } else {
            if (System.currentTimeMillis() - mApp.getPrefLong(Constants.TIME_CITIES) > Constants.CACHE_TIME) {
                loadData();
            } else {
                setUi();

                if (mDataFragment.mWeather != null) {
                    setWeather();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected");

        switch (parent.getId()) {
            case R.id.spinner_countries:
                setCities(mDataFragment.mCountries.get(position).getId());
                break;
            case R.id.spinner_cities:
                mText.setText("Загрузка...");
                Intent intent = new Intent(this, LoadWeather.class);
                intent.putExtra("cityId", mDataFragment.mCities.get(position).getId());
                startService(intent);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(TAG, "onNothingSelected");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, OPTION_UPDATE, 0, "Обновить");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case OPTION_UPDATE:
                Log.d(TAG, "onOptionsItemSelected: UPDATE");
                mApp.clearPrefs();
                loadData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");
            switch (intent.getIntExtra("action", 0)) {
                case Constants.ACTION_CITIES:
                    setUi();
                    break;
                case Constants.ACTION_WEATHER:
                    mDataFragment.mWeather = (Weather) intent.getSerializableExtra("weather");
                    setWeather();
                    break;
            }

        }
    };

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
