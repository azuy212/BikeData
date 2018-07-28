package com.azuy.bikedata;

import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import java.util.Date;
import java.util.Locale;

public class FuelActivity extends AppCompatActivity {
    EditText etFuelMeterReading, etFuelAmountRs, etFuelAmountLt, etPetrolPump, etFuelDate;
    Button btnSubmit;

    DBHelper bikeDataDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel);

        initialize();
        attachListeners();
    }

    private void initialize() {
        etFuelMeterReading = (EditText) findViewById(R.id.etFuelMeterReading);
        etFuelAmountRs = (EditText) findViewById(R.id.etFuelAmountRs);
        etFuelAmountLt = (EditText) findViewById(R.id.etFuelAmountLt);
        etPetrolPump = (EditText) findViewById(R.id.etPetrolPump);
        etFuelDate = (EditText) findViewById(R.id.etFuelDate);
        etFuelDate.setInputType(InputType.TYPE_NULL);
        btnSubmit = (Button) findViewById(R.id.btnFuelSubmit);
        bikeDataDB = new DBHelper(this);
    }

    private void attachListeners() {

        DatePickerDialog.OnDateSetListener dpdl = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String dateSet = String.format(Locale.getDefault(), "%04d", year) + "-" +
                        String.format(Locale.getDefault(), "%02d", month + 1) + "-" +
                        String.format(Locale.getDefault(), "%02d", dayOfMonth);
                etFuelDate.setText(dateSet);
            }
        };

        Date currentDate = new Date();

        SimpleDateFormat sdfDay = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy", Locale.getDefault());

        final DatePickerDialog dpd = new DatePickerDialog(FuelActivity.this, dpdl,
                Integer.parseInt(sdfYear.format(currentDate)),
                Integer.parseInt(sdfMonth.format(currentDate)) - 1,
                Integer.parseInt(sdfDay.format(currentDate)));


        etFuelDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dpd.show();
                }
            }
        });

        etFuelDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    dpd.show();
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
        String stFuelDate = "";
        try {
            Date dtFuelDate = sdf1.parse(etFuelDate.getText().toString());
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            stFuelDate = sdfDate.format(dtFuelDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String stFuelMeterReading = etFuelMeterReading.getText().toString();
        String stFuelAmountRs = etFuelAmountRs.getText().toString();
        String stFuelAmountLt = etFuelAmountLt.getText().toString();
        String stPetrolPump = etPetrolPump.getText().toString();


        if (stFuelDate.isEmpty() || stFuelMeterReading.isEmpty() || stFuelAmountRs.isEmpty() ||
                stFuelAmountLt.isEmpty() || stPetrolPump.isEmpty()) {
            Toast.makeText(this, "Please Fill All Fields!", Toast.LENGTH_SHORT).show();
        } else if (bikeDataDB.hasFuelFillingRecord(stFuelDate)) {
            Toast.makeText(this,
                    "Record for Date "+ stFuelDate +" is already added to database", Toast.LENGTH_SHORT).show();
        } else {

            if(bikeDataDB.insertFuelFillingRecord(
                    stFuelDate,
                    Integer.parseInt(stFuelMeterReading),
                    Integer.parseInt(stFuelAmountRs),
                    Float.parseFloat(stFuelAmountLt),
                    stPetrolPump))
            {
                Toast.makeText(this, "Record Added Successfully!", Toast.LENGTH_SHORT).show();
                removeNotification();
                finish();
            } else
                Toast.makeText(this, "Failed to Add Record", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(Constants.RESERVE_NOTIFICATION_ID);
        }
    }
}
