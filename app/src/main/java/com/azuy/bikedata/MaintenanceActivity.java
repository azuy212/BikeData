package com.azuy.bikedata;

import android.app.DatePickerDialog;
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

public class MaintenanceActivity extends AppCompatActivity {
    EditText etMaintenanceMeterReading,
            etMaintenanceMobilOilCompany,
            etMaintenanceMobilOilPrice,
            etMaintenanceTuningLocation,
            etMaintenanceTuningPrice,
            etMaintenanceAdditionalPartsNames,
            etMaintenanceAdditionalPartsPrice,
            etMaintenanceDate;
    Button btnSubmit;

    DBHelper bikeDataDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        initialize();
        attachListeners();
    }

    private void initialize() {
        etMaintenanceMeterReading = (EditText) findViewById(R.id.etMaintenanceMeterReading);
        etMaintenanceMobilOilCompany = (EditText) findViewById(R.id.etMaintenanceMobilOilCompany);
        etMaintenanceMobilOilPrice = (EditText) findViewById(R.id.etMaintenanceMobilOilPrice);
        etMaintenanceTuningLocation = (EditText) findViewById(R.id.etMaintenanceTuningLocation);
        etMaintenanceTuningPrice = (EditText) findViewById(R.id.etMaintenanceTuningPrice);
        etMaintenanceAdditionalPartsNames = (EditText) findViewById(R.id.etMaintenanceAdditionalPartsNames);
        etMaintenanceAdditionalPartsPrice = (EditText) findViewById(R.id.etMaintenanceAdditionalPartsPrice);
        etMaintenanceDate = (EditText) findViewById(R.id.etMaintenanceDate);
        etMaintenanceDate.setInputType(InputType.TYPE_NULL);
        btnSubmit = (Button) findViewById(R.id.btnMaintenanceSubmit);
        bikeDataDB = new DBHelper(this);
    }

    private void attachListeners() {

        DatePickerDialog.OnDateSetListener dpdl = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String dateSet = String.format(Locale.getDefault(), "%04d", year) + "-" +
                        String.format(Locale.getDefault(), "%02d", month + 1) + "-" +
                        String.format(Locale.getDefault(), "%02d", dayOfMonth);
                etMaintenanceDate.setText(dateSet);
            }
        };

        Date currentDate = new Date();

        SimpleDateFormat sdfDay = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy", Locale.getDefault());

        final DatePickerDialog dpd = new DatePickerDialog(MaintenanceActivity.this, dpdl,
                Integer.parseInt(sdfYear.format(currentDate)),
                Integer.parseInt(sdfMonth.format(currentDate)) - 1,
                Integer.parseInt(sdfDay.format(currentDate)));


        etMaintenanceDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dpd.show();
                }
            }
        });

        etMaintenanceDate.setOnTouchListener(new View.OnTouchListener() {
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
        String stMaintenanceDate = "";
        try {
            Date dtMaintenanceDate = sdf1.parse(etMaintenanceDate.getText().toString());
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            stMaintenanceDate = sdfDate.format(dtMaintenanceDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String stMaintenanceMeterReading = etMaintenanceMeterReading.getText().toString();
        String stMaintenanceMobilOilCompany = etMaintenanceMobilOilCompany.getText().toString();
        String stMaintenanceMobilOilPrice = etMaintenanceMobilOilPrice.getText().toString();
        String stMaintenanceTuningLocation = etMaintenanceTuningLocation.getText().toString();
        String stMaintenanceTuningPrice = etMaintenanceTuningPrice.getText().toString();
        String stMaintenanceAdditionalPartsNames = etMaintenanceAdditionalPartsNames.getText().toString();
        String stMaintenanceAdditionalPartsPrice = etMaintenanceAdditionalPartsPrice.getText().toString();

        if (stMaintenanceDate.isEmpty() || stMaintenanceMeterReading.isEmpty() ||
                stMaintenanceMobilOilCompany.isEmpty() || stMaintenanceMobilOilPrice.isEmpty() ||
                stMaintenanceTuningLocation.isEmpty() || stMaintenanceTuningPrice.isEmpty() ||
                stMaintenanceAdditionalPartsNames.isEmpty() || stMaintenanceAdditionalPartsPrice.isEmpty()) {
            Toast.makeText(this, "Please Fill All Fields!", Toast.LENGTH_SHORT).show();

        } else if (bikeDataDB.hasMaintenanceRecord(stMaintenanceDate)) {
            Toast.makeText(this,
                    "Record for Date " + stMaintenanceDate + " is already added to database", Toast.LENGTH_SHORT).show();
        } else {

            if (bikeDataDB.insertMaintenanceRecord(
                    Integer.parseInt(stMaintenanceMeterReading),
                    stMaintenanceMobilOilCompany, Integer.parseInt(stMaintenanceMobilOilPrice),
                    stMaintenanceTuningLocation, Integer.parseInt(stMaintenanceTuningPrice),
                    stMaintenanceAdditionalPartsNames, Integer.parseInt(stMaintenanceAdditionalPartsPrice),
                    stMaintenanceDate)) {
                Toast.makeText(this, "Record Added Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else
                Toast.makeText(this, "Failed to Add Record", Toast.LENGTH_SHORT).show();
        }
    }
}
