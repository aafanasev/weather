package com.jokuskay.weather.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.jokuskay.weather.models.City;
import com.jokuskay.weather.models.Country;

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = "DbHelper";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "weather.db";

    private static DbHelper instance;

    public static DbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbHelper(context);
        }
        return instance;
    }

    private DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(TAG, "create DbHelper");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");

        String sql = getTableCreateSql(Country.TABLE, Country.Column.values());
        db.execSQL(sql);

        sql = getTableCreateSql(City.TABLE, City.Column.values());
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade from " + oldVersion + " to" + newVersion + " version");

        db.execSQL("DROP TABLE IF EXISTS " + Country.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + City.TABLE);

        onCreate(db);
    }

    private String getTableCreateSql(String tableName, DbColumn[] cols) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder
                .append("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append("(");

        String dot = "";
        for (DbColumn col : cols) {
            sqlBuilder.append(dot);
            sqlBuilder.append(col.name());
            sqlBuilder.append(" ");
            sqlBuilder.append(col.getType());
            dot = ", ";
        }

        sqlBuilder.append(");");
        return sqlBuilder.toString();
    }

}