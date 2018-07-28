package com.azuy.bikedata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    /*
    * DB Helper Constructor
    * */
    DBHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, 2);
    }

    /*
    * onCreate Method to creates all tables and vies required for this project
    * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Constants.FUEL_TABLE_NAME +
                "(" + Constants.FUEL_FILLING_DATE + " TEXT PRIMARY KEY," +
                Constants.FUEL_METER_READING + " INTEGER," +
                Constants.FUEL_AMOUNT_RS + " INTEGER," +
                Constants.FUEL_AMOUNT_LT + " REAL," +
                Constants.FUEL_PETROL_PUMP + " TEXT" +
                ");");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Constants.RESERVE_TABLE_NAME + "(" +
                Constants.RESERVE_DATE + " TEXT PRIMARY KEY," +
                Constants.RESERVE_METERING_READING + " INTEGER" +
                ");");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Constants.MAINTENANCE_TABLE_NAME +
                "(" + Constants.MAINTENANCE_DATE + " TEXT PRIMARY KEY," +
                Constants.MAINTENANCE_METER_READING + " INTEGER," +
                Constants.MAINTENANCE_MOBIL_OIL_COMPANY + " TEXT," +
                Constants.MAINTENANCE_MOBIL_OIL_PRICE + " INTEGER," +
                Constants.MAINTENANCE_TUNING_LOCATION + " TEXT," +
                Constants.MAINTENANCE_TUNING_PRICE + " INTEGER," +
                Constants.MAINTENANCE_ADDITIONAL_PARTS_NAMES + " TEXT," +
                Constants.MAINTENANCE_ADDITIONAL_PARTS_PRICE + " INTEGER" +
                ");");

        db.execSQL("CREATE VIEW IF NOT EXISTS " + Constants.FUEL_TABLE_VIEW + " AS " +
                "SELECT (SELECT Count(*) From " + Constants.FUEL_TABLE_NAME + " B " +
                "Where A." + Constants.FUEL_FILLING_DATE + " <= B." + Constants.FUEL_FILLING_DATE +
                " ) " + Constants.FUEL_VIEW_ID_COLUMN + ", * From " + Constants.FUEL_TABLE_NAME +
                " A Order by " + Constants.FUEL_FILLING_DATE + " DESC");
        db.execSQL("CREATE VIEW IF NOT EXISTS " + Constants.RESERVE_TABLE_VIEW + " AS " +
                "SELECT (SELECT Count(*) From " + Constants.RESERVE_TABLE_NAME + " B " +
                "Where A." + Constants.RESERVE_DATE + " <= B." + Constants.RESERVE_DATE +
                " ) " + Constants.RESERVE_VIEW_ID_COLUMN + ", * From " + Constants.RESERVE_TABLE_NAME +
                " A Order by " + Constants.RESERVE_DATE + " DESC");
        db.execSQL("CREATE VIEW IF NOT EXISTS " + Constants.MAINTENANCE_TABLE_VIEW + " AS " +
                "SELECT (SELECT Count(*) From " + Constants.MAINTENANCE_TABLE_NAME + " B " +
                "Where A." + Constants.MAINTENANCE_DATE + " <= B." + Constants.MAINTENANCE_DATE +
                " ) " + Constants.MAINTENANCE_VIEW_ID_COLUMN + ", * From " + Constants.MAINTENANCE_TABLE_NAME +
                " A Order by " + Constants.MAINTENANCE_DATE + " DESC");
    }

    /*
    * onUpgrade Method to handle Database update
    * */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Constants.FUEL_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.RESERVE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.MAINTENANCE_TABLE_NAME);

        onCreate(db);
    }

    /*
    * Insert Fuel Filling Record into the database
    * */
    boolean insertFuelFillingRecord
            (String fuelDate,
             int meterReading,
             int amountRs,
             float amountLt,
             String petrolPump) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.FUEL_FILLING_DATE, fuelDate);
        contentValues.put(Constants.FUEL_METER_READING, meterReading);
        contentValues.put(Constants.FUEL_AMOUNT_RS, amountRs);
        contentValues.put(Constants.FUEL_AMOUNT_LT, amountLt);
        contentValues.put(Constants.FUEL_PETROL_PUMP, petrolPump);
        return db.insert(Constants.FUEL_TABLE_NAME, null, contentValues) > 0;
    }

    /*
    * Insert Fuel Reserve Record into the database
    * */
    boolean insertFuelReserveRecord(String reserveDate, int meterReading) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.RESERVE_DATE, reserveDate);
        contentValues.put(Constants.RESERVE_METERING_READING, meterReading);
        return db.insert(Constants.RESERVE_TABLE_NAME, null, contentValues) > 0;
    }

    /*
    * Insert Maintenance Record into the database
    * */
    boolean insertMaintenanceRecord
            (int meterReading,
             String mobilOilCompany, int mobilOilPrice,
             String tuningLocation, int tuningPrice,
             String additionalPartsNames, int additionalPartsPrice,
             String maintenanceDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.MAINTENANCE_METER_READING, meterReading);
        contentValues.put(Constants.MAINTENANCE_MOBIL_OIL_COMPANY, mobilOilCompany);
        contentValues.put(Constants.MAINTENANCE_MOBIL_OIL_PRICE, mobilOilPrice);
        contentValues.put(Constants.MAINTENANCE_TUNING_LOCATION, tuningLocation);
        contentValues.put(Constants.MAINTENANCE_TUNING_PRICE, tuningPrice);
        contentValues.put(Constants.MAINTENANCE_ADDITIONAL_PARTS_NAMES, additionalPartsNames);
        contentValues.put(Constants.MAINTENANCE_ADDITIONAL_PARTS_PRICE, additionalPartsPrice);
        contentValues.put(Constants.MAINTENANCE_DATE, maintenanceDate);
        return db.insert(Constants.MAINTENANCE_TABLE_NAME, null, contentValues) > 0;
    }


    /*
    * Get Specific Record from each of three table by giving data
    * */
    public Cursor getFuelFillingRecord(String fillingDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + Constants.FUEL_TABLE_NAME
                + " WHERE " + Constants.FUEL_FILLING_DATE + " = " + fillingDate + "", null);
    }

    public Cursor getFuelReserveRecord(String reserveDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + Constants.RESERVE_TABLE_NAME
                + " WHERE " + Constants.RESERVE_DATE + " = " + reserveDate + "", null);
    }

    public Cursor getMaintenanceRecord(String reserveDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + Constants.MAINTENANCE_TABLE_NAME
                + " WHERE " + Constants.MAINTENANCE_DATE + " = " + reserveDate + "", null);
    }


    /*
    * Check if Specific Record is available on provided data
    * */
    boolean hasFuelFillingRecord(String fillingDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + Constants.FUEL_TABLE_NAME
                + " WHERE DATE(" + Constants.FUEL_FILLING_DATE + ")"
                + " = DATE('" + fillingDate + "')", null).getCount() > 0;
    }

    boolean hasFuelReserveRecord(String reserveDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + Constants.RESERVE_TABLE_NAME
                + " WHERE DATE(" + Constants.RESERVE_DATE + ")"
                + " = DATE('" + reserveDate + "')", null).getCount() > 0;
    }

    boolean hasMaintenanceRecord(String maintenanceDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + Constants.MAINTENANCE_TABLE_NAME
                + " WHERE DATE(" + Constants.MAINTENANCE_DATE + ")"
                + " = DATE('" + maintenanceDate + "')", null).getCount() > 0;
    }


    /*
    * Delete Specific Record on provided data
    * */
    boolean deleteFuelFillingRecord(String fillingDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Constants.FUEL_TABLE_NAME,
                Constants.FUEL_FILLING_DATE + " = ? ",
                new String[]{fillingDate}) > 0;
    }

    boolean deleteFuelReserveRecord(String reserveDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Constants.RESERVE_TABLE_NAME,
                Constants.RESERVE_DATE + " = ? ",
                new String[]{reserveDate}) > 0;
    }

    boolean deleteMaintenanceRecord(String maintenanceDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Constants.MAINTENANCE_TABLE_NAME,
                Constants.MAINTENANCE_DATE + " = ? ",
                new String[]{maintenanceDate}) > 0;
    }


    /*
    * Get all records of each table
    * */
    Cursor getAllFuelFillingRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + Constants.FUEL_TABLE_VIEW +
                " ORDER BY DATE(" + Constants.FUEL_FILLING_DATE + ") DESC", null);
    }

    Cursor getAllFuelReserveRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + Constants.RESERVE_TABLE_VIEW +
                " ORDER BY DATE(" + Constants.RESERVE_DATE + ") DESC", null);
    }

    Cursor getAllMaintenanceRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + Constants.MAINTENANCE_TABLE_VIEW +
                " ORDER BY DATE(" + Constants.MAINTENANCE_DATE + ") DESC", null);
    }


    /*
    * Generic function to get data difference of specified tables on specific ids
    * */
    ArrayList<Float> getBikeDataDifference
            (String firstColumnName,
             String secondColumnName,
             String firstTableName,
             String secondTableName,
             String firstId, String secondId) throws SQLiteException {
        ArrayList<Float> bikeDataDifference = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cBikeDataDifference = db.rawQuery(
                "SELECT CAST((JULIANDAY(A." + firstColumnName + ") - JULIANDAY(B." + secondColumnName +
                        "))  AS FLOAT)\n" +
                        "FROM "+firstTableName+" A\n" +
                        "JOIN "+secondTableName+" B\n" +
                        "On A."+firstId+" = B."+secondId+"-1", null);
        while (cBikeDataDifference.moveToNext()) {
            bikeDataDifference.add(cBikeDataDifference.getFloat(0));
        }
        cBikeDataDifference.close();

        return bikeDataDifference;

    }


    /*
    * Get Mileage Data
    * */
    ArrayList<Float> getBikeMileagePerLt() {
        ArrayList<Float> bikeMileagePerLt = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cBikeMileagePerLt = db.rawQuery(
                "SELECT (\n" +
                        "CAST((JULIANDAY(A." + Constants.RESERVE_METERING_READING + ") - " +
                        "      JULIANDAY(B." + Constants.RESERVE_METERING_READING + ")) AS FLOAT)\n" +
                        " /\n" +
                        "(Select " + Constants.FUEL_AMOUNT_LT + " from " + Constants.FUEL_TABLE_VIEW + " C " +
                        "where C." + Constants.FUEL_VIEW_ID_COLUMN + " = B." + Constants.RESERVE_VIEW_ID_COLUMN + ")) \n" +
                        "FROM " + Constants.RESERVE_TABLE_VIEW + " A\n" +
                        "JOIN " + Constants.RESERVE_TABLE_VIEW + " B\n" +
                        "On A." + Constants.RESERVE_VIEW_ID_COLUMN + " = B." + Constants.RESERVE_VIEW_ID_COLUMN + "-1", null
        );
        while (cBikeMileagePerLt.moveToNext()) {
            bikeMileagePerLt.add(cBikeMileagePerLt.getFloat(0));
        }
        cBikeMileagePerLt.close();

        return bikeMileagePerLt;
    }

    ArrayList<Float> getBikeMileagePerDay() {
        ArrayList<Float> bikeMileagePerDay = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cBikeMileagePerDay = db.rawQuery(
                "SELECT (\n" +
                        "CAST((JULIANDAY(A." + Constants.RESERVE_METERING_READING + ") - " +
                        "      JULIANDAY(B." + Constants.RESERVE_METERING_READING + ")) AS FLOAT)\n" +
                        " /\n" +
                        "CAST((JULIANDAY(A." + Constants.RESERVE_DATE + ") - " +
                        "      JULIANDAY(B." + Constants.RESERVE_DATE + ")) AS FLOAT))\n" +
                        "FROM " + Constants.RESERVE_TABLE_VIEW + " A\n" +
                        "JOIN " + Constants.RESERVE_TABLE_VIEW + " B\n" +
                        "On A." + Constants.RESERVE_VIEW_ID_COLUMN + " = B." + Constants.RESERVE_VIEW_ID_COLUMN + "-1", null
        );
        while (cBikeMileagePerDay.moveToNext()) {
            bikeMileagePerDay.add(cBikeMileagePerDay.getFloat(0));
        }
        cBikeMileagePerDay.close();

        return bikeMileagePerDay;
    }

}