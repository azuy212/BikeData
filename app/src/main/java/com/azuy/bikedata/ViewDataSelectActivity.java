package com.azuy.bikedata;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class ViewDataSelectActivity extends AppCompatActivity {

    Button btnViewFuelData, btnViewReserveData, btnViewMaintenanceData, btnViewStatsData;
    DBHelper bikeDataDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data_select);
        initialize();
        attachOnClickListeners();
//        restoreDatabaseBackup();
    }

    private void initialize() {
        btnViewFuelData = (Button) findViewById(R.id.btnViewFuelData);
        btnViewReserveData = (Button) findViewById(R.id.btnViewReserveData);
        btnViewMaintenanceData = (Button) findViewById(R.id.btnViewMaintenanceData);
        btnViewStatsData = (Button) findViewById(R.id.btnViewStatsData);
        bikeDataDB = new DBHelper(this);
    }

    private void attachOnClickListeners() {

        btnViewFuelData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewDataSelectActivity.this, ViewFuelDataActivity.class);
                startActivity(intent);
            }
        });

        btnViewReserveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewDataSelectActivity.this, ViewReserveDataActivity.class);
                startActivity(intent);
            }
        });

        btnViewMaintenanceData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewDataSelectActivity.this, ViewMaintenanceDataActivity.class);
                startActivity(intent);
            }
        });

        btnViewStatsData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewDataSelectActivity.this, ViewStatsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void restoreDatabaseBackup() {

        File sd = Environment.getExternalStorageDirectory();
        String backupDBPath = "//BikeData//Backup//24-Feb-2018//"+Constants.DATABASE_NAME;
        File backupDB = new File(sd, backupDBPath);
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(backupDB, null);

        Cursor fuelTableBackup = db.rawQuery("SELECT * FROM " + Constants.FUEL_TABLE_NAME +
                " ORDER BY DATE(" + Constants.FUEL_FILLING_DATE + ") DESC", null);
        Cursor reserveTableBackup = db.rawQuery("SELECT * FROM " + Constants.RESERVE_TABLE_NAME +
                " ORDER BY DATE(" + Constants.RESERVE_DATE + ") DESC", null);
        Cursor maintenanceTableRecord = db.rawQuery("SELECT * FROM " + Constants.MAINTENANCE_TABLE_NAME +
                " ORDER BY DATE(" + Constants.MAINTENANCE_DATE + ") DESC", null);

        Toast.makeText(this, "Fuel Table Count: " + fuelTableBackup.getCount() + "\n"
                + "Reserve Table Count: " + reserveTableBackup.getCount() + "\n"
                + "Maintenance Table Count: " + maintenanceTableRecord.getCount()
                , Toast.LENGTH_SHORT).show();

        if (fuelTableBackup.getCount() > 0) {
            while (fuelTableBackup.moveToNext()) {
                bikeDataDB.insertFuelFillingRecord(
                        fuelTableBackup.getString(fuelTableBackup.getColumnIndex(Constants.FUEL_FILLING_DATE)),
                        fuelTableBackup.getInt(fuelTableBackup.getColumnIndex(Constants.FUEL_METER_READING)),
                        fuelTableBackup.getInt(fuelTableBackup.getColumnIndex(Constants.FUEL_AMOUNT_RS)),
                        fuelTableBackup.getFloat(fuelTableBackup.getColumnIndex(Constants.FUEL_AMOUNT_LT)),
                        fuelTableBackup.getString(fuelTableBackup.getColumnIndex(Constants.FUEL_PETROL_PUMP))
                );
            }
            Toast.makeText(this, "Fuel Table Backup Successful", Toast.LENGTH_SHORT).show();
        }
        if (reserveTableBackup.getCount() > 0) {
            while (reserveTableBackup.moveToNext()) {
                bikeDataDB.insertFuelReserveRecord(
                        reserveTableBackup.getString(reserveTableBackup.getColumnIndex(Constants.RESERVE_DATE)),
                        reserveTableBackup.getInt(reserveTableBackup.getColumnIndex(Constants.RESERVE_METERING_READING))
                );
            }
            Toast.makeText(this, "Reserve Table Backup Successful", Toast.LENGTH_SHORT).show();
        }
        if (maintenanceTableRecord.getCount() > 0) {
            while (maintenanceTableRecord.moveToNext()) {
                bikeDataDB.insertMaintenanceRecord(
                        maintenanceTableRecord.getInt(maintenanceTableRecord.getColumnIndex(Constants.MAINTENANCE_METER_READING)),
                        maintenanceTableRecord.getString(maintenanceTableRecord.getColumnIndex(Constants.MAINTENANCE_MOBIL_OIL_COMPANY)),
                        maintenanceTableRecord.getInt(maintenanceTableRecord.getColumnIndex(Constants.MAINTENANCE_MOBIL_OIL_PRICE)),
                        maintenanceTableRecord.getString(maintenanceTableRecord.getColumnIndex(Constants.MAINTENANCE_TUNING_LOCATION)),
                        maintenanceTableRecord.getInt(maintenanceTableRecord.getColumnIndex(Constants.MAINTENANCE_TUNING_PRICE)),
                        maintenanceTableRecord.getString(maintenanceTableRecord.getColumnIndex(Constants.MAINTENANCE_ADDITIONAL_PARTS_NAMES)),
                        maintenanceTableRecord.getInt(maintenanceTableRecord.getColumnIndex(Constants.MAINTENANCE_ADDITIONAL_PARTS_PRICE)),
                        maintenanceTableRecord.getString(maintenanceTableRecord.getColumnIndex(Constants.MAINTENANCE_DATE))
                );
            }
            Toast.makeText(this, "Maintenance Table Backup Successful", Toast.LENGTH_SHORT).show();
        }
    }

}
