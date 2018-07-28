package com.azuy.bikedata;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ViewFuelDataActivity extends AppCompatActivity {

    DBHelper bikeDataDB;
    ListView fuelActivityListView;
    ArrayList<HashMap<String, String>> feedFuelData;
    SimpleAdapter simpleFuelDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_fuel_data);
        initialize();
        fillListViewWithFuelData();

    }

    private void initialize() {
        bikeDataDB = new DBHelper(this);
        fuelActivityListView = (ListView) findViewById(R.id.viewFuelDataListView);
        feedFuelData = new ArrayList<HashMap<String, String>>();
    }

    private void fillListViewWithFuelData(){
        try {
            Cursor cursor = bikeDataDB.getAllFuelFillingRecords();
            if (cursor.getCount() > 0) {

                String fuelDateString[] = new String[]{
                        Constants.FUEL_VIEW_ID_COLUMN, Constants.FUEL_FILLING_DATE,
                        Constants.FUEL_METER_READING, Constants.FUEL_AMOUNT_RS,
                        Constants.FUEL_AMOUNT_LT, Constants.FUEL_PETROL_PUMP
                };

                HashMap<String, String> fuelHeaderMap = new HashMap<String, String>();
                fuelHeaderMap.put(Constants.FUEL_VIEW_ID_COLUMN, "Fuel Id");
                fuelHeaderMap.put(Constants.FUEL_FILLING_DATE, "Filling\nDate");
                fuelHeaderMap.put(Constants.FUEL_METER_READING, "Meter Reading");
                fuelHeaderMap.put(Constants.FUEL_AMOUNT_RS, "Amount (Rs.)");
                fuelHeaderMap.put(Constants.FUEL_AMOUNT_LT, "Amount (Lt.)");
                fuelHeaderMap.put(Constants.FUEL_PETROL_PUMP, "Petrol\nPump");
                feedFuelData.add(fuelHeaderMap);

                while (cursor.moveToNext()) {

                    feedFuelData.add(
                            createHashMapWithFetchedRowData(
                                    fuelDateString, fetchFuelRowData(cursor)
                            )
                    );

                }

                int fuelDataInt[] = new int[]{
                        R.id.textViewFuelId, R.id.textViewFillingDate, R.id.textViewMeterReading,
                        R.id.textViewFuelAmountRs, R.id.textViewFuelAmountLt, R.id.textViewPetrolPump
                };

                simpleFuelDataAdapter = new SimpleAdapter(this, feedFuelData,
                        R.layout.fueldataviewlayout, fuelDateString, fuelDataInt);

                fuelActivityListView.setAdapter(simpleFuelDataAdapter);

                fuelActivityListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                        setOnListViewClickListener(adapterView, view, i, l);
                        return true;
                    }
                });

            }
            cursor.close();

        } catch (SQLiteException | ParseException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String,String> createHashMapWithFetchedRowData
            (String[] fuelDataColumnNames, ArrayList<String> fetchedRowData) {

        HashMap<String, String> hashMapOfFetchedRowData = new HashMap<String, String>();
        for (int i = 0; i < fuelDataColumnNames.length; i++) {
            hashMapOfFetchedRowData.put(fuelDataColumnNames[i], fetchedRowData.get(i));
        }

        return hashMapOfFetchedRowData;
    }

    private void setOnListViewClickListener(AdapterView<?> adapterView, View view, int i, long l){
        HashMap selectedItem = (HashMap) adapterView.getItemAtPosition(i);
        final String selectedRowDate = (String) selectedItem.get(Constants.FUEL_FILLING_DATE);
        final Context currentContext = ViewFuelDataActivity.this;
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(currentContext,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(currentContext);
        }
        builder.setTitle("Delete entry")
                .setMessage("Are you sure you want to delete Record for "+ selectedRowDate +" ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String stFuelFillingDate = "";
                        try {
                            SimpleDateFormat sdf2 = new SimpleDateFormat
                                    ("MMMM dd, yyyy", Locale.getDefault());
                            Date dtFuelDate = sdf2.parse(
                                    selectedRowDate);
                            SimpleDateFormat sdfDate = new SimpleDateFormat(
                                    "yyyy-MM-dd hh:mm:ss",
                                    Locale.getDefault());
                            stFuelFillingDate = sdfDate.format(dtFuelDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if(bikeDataDB.deleteFuelFillingRecord(stFuelFillingDate)){
                            makeToast(
                                    "Record for date " + selectedRowDate + " deleted Successfully!!!",
                                    1000);
                            simpleFuelDataAdapter.notifyDataSetChanged();
//                            finish();
                        } else {
                            makeToast("Failed to delete Record!!!", 1000);
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private ArrayList<String> fetchFuelRowData (Cursor cursor) throws ParseException, SQLiteException {
        ArrayList<String> fetchedRowData = new ArrayList<>();

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());

        fetchedRowData.add(
                Integer.toString(cursor.getInt(cursor.getColumnIndex(Constants.FUEL_VIEW_ID_COLUMN)))
        );
        fetchedRowData.add(
                sdf2.format(sdf1.parse(cursor.getString(cursor.getColumnIndex(Constants.FUEL_FILLING_DATE))))
        );
        fetchedRowData.add(
                Integer.toString(cursor.getInt(cursor.getColumnIndex(Constants.FUEL_METER_READING)))
        );
        fetchedRowData.add(
                Integer.toString(cursor.getInt(cursor.getColumnIndex(Constants.FUEL_AMOUNT_RS)))
        );
        fetchedRowData.add(
                Float.toString(cursor.getFloat(cursor.getColumnIndex(Constants.FUEL_AMOUNT_LT)))
        );
        fetchedRowData.add(
                cursor.getString(cursor.getColumnIndex(Constants.FUEL_PETROL_PUMP))
        );

        return fetchedRowData;
    }

    public void makeToast(String msg, int duration) {
        final Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, duration);

    }
}
