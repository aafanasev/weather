package com.jokuskay.weather.helpers;

public class Constants {

    public static final int ACTION_CITIES = 1;
    public static final int ACTION_WEATHER = 2;

    public static final String ETAG_CITIES = "etag_cities";
    public static final String TIME_CITIES = "time_cities";
    public static final String ETAG_WEATHER = "etag_weather_";
    public static final String TIME_WEATHER = "time_weather_";

    public static final String URL_CITIES = "https://pogoda.yandex.ru/static/cities.xml";
    public static final String URL_WEATHER = "http://export.yandex.ru/weather-ng/forecasts/{id}.xml";

    public static final String IMG_MINUS = "http://hikingartist.files.wordpress.com/2012/05/1-christmas-tree.jpg";
    public static final String IMG_PLUS = "http://www.rewalls.com/images/201201/reWalls.com_59293.jpg";

    public static final int CACHE_TIME = 3600000; // 1 hour

}
