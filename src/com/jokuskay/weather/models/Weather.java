package com.jokuskay.weather.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.jokuskay.weather.helpers.DbColumn;
import com.jokuskay.weather.helpers.DbHelper;

import java.io.Serializable;

public class Weather implements Serializable {

    public static final String TABLE = "weather";

    public static enum Column implements DbColumn {
        _id("INTEGER PRIMARY KEY"), temperature("INTEGER"), text("TEXT");

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
    private int mTemperature;
    private String mText;

    public Weather() {

    }

    public Weather(int id, int temp, String text) {
        mId = id;
        mTemperature = temp;
        mText = text;
    }


    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public int getTemperature() {
        return mTemperature;
    }

    public void setTemperature(int temperature) {
        this.mTemperature = temperature;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public static void remove(Context context, int id) {
        DbHelper dbHelper = DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE + " WHERE " + Column._id.name() + "=" + id);
    }

    public void save(Context context) {
        DbHelper dbHelper = DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Column._id.name(), mId);
        values.put(Column.temperature.name(), mTemperature);
        values.put(Column.text.name(), mText);

        db.insert(TABLE, null, values);

    }

    public static Weather getByCityId(Context context, int cityId) {
        Weather result = null;

        DbHelper dbHelper = DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT * FROM " + TABLE + " WHERE " + Column._id.name() + "=?",
                new String[]{cityId + ""}
        );
        int iId = c.getColumnIndex(Column._id.name());
        int iTemp = c.getColumnIndex(Column.temperature.name());
        int iText = c.getColumnIndex(Column.text.name());

        c.moveToFirst();
        try {
            result = new Weather(c.getInt(iId), c.getInt(iTemp), c.getString(iText));
        } catch (IndexOutOfBoundsException e) {

        }
        c.close();

        return result;
    }

}
