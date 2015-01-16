package com.jokuskay.weather.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.jokuskay.weather.helpers.Constants;
import com.jokuskay.weather.models.City;
import com.jokuskay.weather.models.Country;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class LoadCities extends IntentService {

    private static final String TAG = "LoadCities";

    private Map<String, Integer> mCountries = new HashMap<>();
    private List<City> mCities = new ArrayList<>();

    private int mCountryId;

    public LoadCities() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "start");

        loadXml();

        if (mCountries.size() > 0) {
            Log.d(TAG, mCountries.size() + " countries");
            Country.removeAll(this);
            Country.save(this, mCountries);
        }

        if (mCities.size() > 0) {
            Log.d(TAG, mCities.size() + " cities");
            City.removeAll(this);
            City.save(this, mCities);
        }

        broadcast();
    }

    private void loadXml() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(Constants.URL_CITIES);
            connection = (HttpURLConnection) url.openConnection();

            // todo ETag

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                parseXml(connection.getInputStream());
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void parseXml(InputStream is) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);

        XmlPullParser parser = factory.newPullParser();
        parser.setInput(is, null);

        int eventType;
        do {
            eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG && "city".equals(parser.getName())) {

                String countryName = parser.getAttributeValue(null, "country");

                int countryId;
                if (mCountries.containsKey(countryName)) {
                    countryId = mCountries.get(countryName);
                } else {
                    countryId = ++mCountryId;
                    mCountries.put(countryName, countryId);
                }

                eventType = parser.next();
                if (eventType == XmlPullParser.TEXT) {
                    mCities.add(new City(countryId, parser.getName()));
                }
            }
        } while (eventType != XmlPullParser.END_DOCUMENT);
    }

    private void broadcast() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(getPackageName()));
    }

}
