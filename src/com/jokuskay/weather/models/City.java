package com.jokuskay.weather.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.jokuskay.weather.helpers.DbColumn;
import com.jokuskay.weather.helpers.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class City {

    public static final String TABLE = "cities";

    public static enum Column implements DbColumn {
        _id("INTEGER PRIMARY KEY"), country_id("INTEGER"), name("TEXT");

        private String mType;

        private Column(String type) {
            mType = type;
        }

        @Override
        public String getType() {
            return mType;
        }
    }

    private int mId;
    private int mCountryId;
    private String mName;

    public City() {

    }

    public City(int id, int countryId, String name) {
        mId = id;
        mCountryId = countryId;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getCountryId() {
        return mCountryId;
    }

    public void setCountryId(int countryId) {
        mCountryId = countryId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static void removeAll(Context context) {
        DbHelper dbHelper = DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE);
    }

    public static List<City> getByCountryId(Context context, int countryId) {
        Log.d(TABLE, "getByCountryId");

        List<City> result = new ArrayList<>();

        DbHelper dbHelper = DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT * FROM " + TABLE + " WHERE " + Column.country_id.name() + "=? ORDER BY " + Column.name.name() + " ASC",
                new String[]{countryId + ""}
        );
        int iId = c.getColumnIndex(Column._id.name());
        int iCountryId = c.getColumnIndex(Column.country_id.name());
        int iName = c.getColumnIndex(Column.name.name());

        if (c.moveToFirst()) {
            do {
                City city = new City();
                city.setId(c.getInt(iId));
                city.setCountryId(c.getInt(iCountryId));
                city.setName(c.getString(iName));
                result.add(city);
            } while (c.moveToNext());
        }

        c.close();
        return result;
    }

    public static void save(Context context, List<City> cities) {
        DbHelper dbHelper = DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (City city : cities) {
            ContentValues values = new ContentValues();
            values.put(Column._id.name(), city.getId());
            values.put(Column.country_id.name(), city.getCountryId());
            values.put(Column.name.name(), city.getName());
            db.insert(TABLE, null, values);
        }
    }
}
