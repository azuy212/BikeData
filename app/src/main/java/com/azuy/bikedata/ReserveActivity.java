package com.azuy.bikedata;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReserveActivity extends AppCompatActivity {
    EditText etReserveMeterReading, etReserveDate;
    Button btnSubmit;

    DBHelper bikeDataDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        initialize();
        attachListeners();
    }

    private void initialize() {
        etReserveMeterReading = (EditText) findViewById(R.id.etReserveMeterReading);
        etReserveDate = (EditText) findViewById(R.id.etReserveDate);
        etReserveDate.setInputType(InputType.TYPE_NULL);
        btnSubmit = (Button) findViewById(R.id.btnReserveSubmit);
        bikeDataDB = new DBHelper(this);
    }

    private void attachListeners() {

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String dateSet = String.format(Locale.getDefault(), "%04d", year) + "-" +
                        String.format(Locale.getDefault(), "%02d", month + 1) + "-" +
                        String.format(Locale.getDefault(), "%02d", dayOfMonth);
                etReserveDate.setText(dateSet);
            }
        };

        Date currentDate = new Date();

        SimpleDateFormat sdfDay = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy", Locale.getDefault());

        final DatePickerDialog datePickerDialog = new DatePickerDialog(ReserveActivity.this, dateSetListener,
                Integer.parseInt(sdfYear.format(currentDate)),
                Integer.parseInt(sdfMonth.format(currentDate)) - 1,
                Integer.parseInt(sdfDay.format(currentDate)));


        etReserveDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    datePickerDialog.show();
                }
            }
        });

        etReserveDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    datePickerDialog.show();
                return false;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addValuesToDatabase();
            }
        });
    }

    private void addValuesToDatabase() {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String stReserveDate = "";
        try {
            Date dtReserveDate = sdf1.parse(etReserveDate.getText().toString());
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            stReserveDate = sdfDate.format(dtReserveDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String stReserveMeterReading = etReserveMeterReading.getText().toString();

        if (stReserveDate.isEmpty() || stReserveMeterReading.isEmpty()) {
            Toast.makeText(this, "Please Fill All Fields!", Toast.LENGTH_SHORT).show();
        } else if (bikeDataDB.hasFuelReserveRecord(stReserveDate)) {
            Toast.makeText(this,
                    "Record for Date " + stReserveDate + " is already added to database", Toast.LENGTH_SHORT).show();
        } else {

            if (bikeDataDB.insertFuelReserveRecord
                    (stReserveDate, Integer.parseInt(stReserveMeterReading))) {
                Toast.makeText(this, "Record Added Successfully!", Toast.LENGTH_SHORT).show();
                addNotification();
                finish();
            } else
                Toast.makeText(this, "Failed to Add Record", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNotification() {
        ArrayList<Float> noOfKMsBetweenReserves = bikeDataDB.getBikeDataDifference(
                Constants.RESERVE_METERING_READING, Constants.RESERVE_METERING_READING,
                Constants.RESERVE_TABLE_VIEW, Constants.RESERVE_TABLE_VIEW,
                Constants.RESERVE_VIEW_ID_COLUMN, Constants.RESERVE_VIEW_ID_COLUMN
        );
        ArrayList<Float> bikeMileagePerLt = bikeDataDB.getBikeMileagePerLt();
        ArrayList<Float> bikeMileagePerDay = bikeDataDB.getBikeMileagePerDay();

        String notificationMessage = "Distance (R): " + Math.round(noOfKMsBetweenReserves.get(0)) + " km" + "\n" +
                "Mileage (km/lt): " + bikeMileagePerLt.get(0) + "\n" +
                "Mileage (km/day): " + bikeMileagePerDay.get(0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentTitle(Constants.RESERVE_NOTIFICATION_TITLE)
                        .setContentText(Constants.RESERVE_NOTIFICATION_MESSAGE)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMessage))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setOngoing(true);
        builder.setDefaults(Notification.DEFAULT_SOUND);

        Intent notificationIntent = new Intent(this, FuelActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(Constants.RESERVE_NOTIFICATION_ID, builder.build());
        }
    }
}
