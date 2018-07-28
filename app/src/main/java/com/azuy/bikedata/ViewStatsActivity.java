package com.azuy.bikedata;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ViewStatsActivity extends AppCompatActivity {

    DBHelper bikeDataDB;
    TableLayout viewStatsTableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stats);

        initialize();
        generateBikeStats();

    }

    private void initialize() {
        bikeDataDB = new DBHelper(this);
        viewStatsTableLayout = (TableLayout) findViewById(R.id.viewStatsTableLayout);
    }

    private void generateBikeStats () {
        ArrayList<Float> noOfDaysBetweenFuelFillings = bikeDataDB.getBikeDataDifference(
                Constants.FUEL_FILLING_DATE, Constants.FUEL_FILLING_DATE,
                Constants.FUEL_TABLE_VIEW, Constants.FUEL_TABLE_VIEW,
                Constants.FUEL_VIEW_ID_COLUMN, Constants.FUEL_VIEW_ID_COLUMN
        );
        printStatsOnTable(findLatestAvgMinMax("Days(F)", noOfDaysBetweenFuelFillings));

        ArrayList<Float> noOfDaysBetweenReserves = bikeDataDB.getBikeDataDifference(
                Constants.RESERVE_DATE, Constants.RESERVE_DATE,
                Constants.RESERVE_TABLE_VIEW, Constants.RESERVE_TABLE_VIEW,
                Constants.RESERVE_VIEW_ID_COLUMN, Constants.RESERVE_VIEW_ID_COLUMN
        );
        printStatsOnTable(findLatestAvgMinMax("Days(R)", noOfDaysBetweenReserves));

        ArrayList<Float> noOfDaysBetweenFillingAndReserve = bikeDataDB.getBikeDataDifference(
                Constants.RESERVE_DATE, Constants.FUEL_FILLING_DATE,
                Constants.RESERVE_TABLE_VIEW, Constants.FUEL_TABLE_VIEW,
                Constants.RESERVE_VIEW_ID_COLUMN, Constants.FUEL_VIEW_ID_COLUMN
        );
        printStatsOnTable(findLatestAvgMinMax("Days(FR)", noOfDaysBetweenFillingAndReserve));



        ArrayList<Float> noOfKMsBetweenFuelFillings = bikeDataDB.getBikeDataDifference(
                Constants.FUEL_METER_READING, Constants.FUEL_METER_READING,
                Constants.FUEL_TABLE_VIEW, Constants.FUEL_TABLE_VIEW,
                Constants.FUEL_VIEW_ID_COLUMN, Constants.FUEL_VIEW_ID_COLUMN
        );
        printStatsOnTable(findLatestAvgMinMax("KMs(F)", noOfKMsBetweenFuelFillings));

        ArrayList<Float> noOfKMsBetweenReserves = bikeDataDB.getBikeDataDifference(
                Constants.RESERVE_METERING_READING, Constants.RESERVE_METERING_READING,
                Constants.RESERVE_TABLE_VIEW, Constants.RESERVE_TABLE_VIEW,
                Constants.RESERVE_VIEW_ID_COLUMN, Constants.RESERVE_VIEW_ID_COLUMN
        );
        printStatsOnTable(findLatestAvgMinMax("KMs(R)", noOfKMsBetweenReserves));

        ArrayList<Float> noOfKMsBetweenFillingAndReserve = bikeDataDB.getBikeDataDifference(
                Constants.RESERVE_METERING_READING, Constants.FUEL_METER_READING,
                Constants.RESERVE_TABLE_VIEW, Constants.FUEL_TABLE_VIEW,
                Constants.RESERVE_VIEW_ID_COLUMN, Constants.FUEL_VIEW_ID_COLUMN
        );
        printStatsOnTable(findLatestAvgMinMax("KMs(FR)", noOfKMsBetweenFillingAndReserve));

        ArrayList<Float> bikeMileagePerLt = bikeDataDB.getBikeMileagePerLt();
        printStatsOnTable(findLatestAvgMinMax("Mileage (km/lt)", bikeMileagePerLt));

        ArrayList<Float> bikeMileagePerDay = bikeDataDB.getBikeMileagePerDay();
        printStatsOnTable(findLatestAvgMinMax("Mileage (km/day)", bikeMileagePerDay));

    }


    private ArrayList<String> findLatestAvgMinMax(String statsTitle ,List<Float> arrayListOfStats) {
        ArrayList<String> arrayListToReturn = new ArrayList<>();

        arrayListToReturn.add(statsTitle);
        arrayListToReturn.add(String.format(Locale.getDefault(), "%.2f",
                arrayListOfStats.get(0)));

        int sumOfArrayListOfStats = 0;
        for (float value : arrayListOfStats) {
            sumOfArrayListOfStats += value;
        }
        arrayListToReturn.add(String.format(Locale.getDefault(), "%.2f",
                (float)sumOfArrayListOfStats / arrayListOfStats.size()));

        arrayListToReturn.add(String.format(Locale.getDefault(), "%.2f",
                Collections.min(arrayListOfStats)));
        arrayListToReturn.add(String.format(Locale.getDefault(), "%.2f",
                Collections.max(arrayListOfStats)));

        return arrayListToReturn;
    }

    private void printStatsOnTable (ArrayList<String> rowData) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        for (String text : rowData) {
            TextView tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.CENTER);
            tv.setBackground(ContextCompat.getDrawable(this, R.drawable.cellshape));
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(16);
            tv.setPadding(10, 5, 10, 5);
            tv.setText(text);
            row.addView(tv);
        }
        viewStatsTableLayout.addView(row);
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
