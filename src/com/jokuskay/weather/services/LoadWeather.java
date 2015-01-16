package com.jokuskay.weather.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.jokuskay.weather.App;
import com.jokuskay.weather.helpers.Constants;
import com.jokuskay.weather.models.Weather;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadWeather extends IntentService {

    private static final String TAG = "LoadWeather";

    private App mApp;
    private Weather mWeather;

    private int mCityId;

    public LoadWeather() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "start");

        mCityId = intent.getIntExtra("cityId", 0);
        String key = Constants.TIME_WEATHER + mCityId;

        mApp = (App) getApplication();
        mWeather = Weather.getByCityId(this, mCityId);

        if (mWeather == null || System.currentTimeMillis() - mApp.getPrefLong(key) > Constants.CACHE_TIME) {
            mApp.setPref(key, System.currentTimeMillis());

            Weather.remove(this, mCityId);

            mWeather = new Weather();
            mWeather.setId(mCityId);

            loadXml();

            mWeather.save(this);
        }

        broadcast();
    }

    private void loadXml() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(Constants.URL_WEATHER.replace("{id}", mCityId + ""));
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(true);
            connection.setRequestProperty("If-None-Match", mApp.getPrefString(Constants.ETAG_CITIES));

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                mApp.setPref(Constants.ETAG_WEATHER + mCityId, connection.getHeaderField("ETag"));
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
            if (eventType == XmlPullParser.START_TAG) {
                String tagName = parser.getName();
                if ("temperature".equals(tagName)) {
                    eventType = parser.next();
                    if (eventType == XmlPullParser.TEXT) {
                        mWeather.setTemperature(Integer.parseInt(parser.getText()));
                    }
                } else if ("weather_type".equals(tagName)) {
                    eventType = parser.next();
                    if (eventType == XmlPullParser.TEXT) {
                        mWeather.setText(parser.getText());
                    }
                }
            }

        } while (eventType != XmlPullParser.END_TAG || !"fact".equals(parser.getName()));
    }

    private void broadcast() {
        Intent intent = new Intent(getPackageName());
        intent.putExtra("action", Constants.ACTION_WEATHER);
        intent.putExtra("weather", mWeather);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
