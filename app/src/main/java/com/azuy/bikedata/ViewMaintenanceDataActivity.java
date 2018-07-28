package com.azuy.bikedata;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewMaintenanceDataActivity extends AppCompatActivity {

    DBHelper bikeDataDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_maintenance_data);
        bikeDataDB = new DBHelper(this);
        TableLayout tableLayout = (TableLayout) findViewById(R.id.viewMaintenanceTableLayout);

        try {
            Cursor cursor = bikeDataDB.getAllMaintenanceRecords();
            if (cursor.getCount() > 0) {
                int rowId = 0;
                while (cursor.moveToNext()) {
                    // Read columns data
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date maintenanceDate = sdf1.parse(cursor.getString(cursor.getColumnIndex(Constants.MAINTENANCE_DATE)));
                    final SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                    String maintenanceDateString = sdf2.format(maintenanceDate);

                    int meterReading = cursor.getInt(cursor.getColumnIndex(Constants.MAINTENANCE_METER_READING));
                    String mobilOilCompany = cursor.getString(cursor.getColumnIndex(Constants.MAINTENANCE_MOBIL_OIL_COMPANY));
                    int mobilOilPrice = cursor.getInt(cursor.getColumnIndex(Constants.MAINTENANCE_MOBIL_OIL_PRICE));
                    String tuningLocation = cursor.getString(cursor.getColumnIndex(Constants.MAINTENANCE_TUNING_LOCATION));
                    int tuningPrice = cursor.getInt(cursor.getColumnIndex(Constants.MAINTENANCE_TUNING_PRICE));
                    String additionalPartsNames = cursor.getString(cursor.getColumnIndex(Constants.MAINTENANCE_ADDITIONAL_PARTS_NAMES));
                    int additionalPartsPrice = cursor.getInt(cursor.getColumnIndex(Constants.MAINTENANCE_ADDITIONAL_PARTS_PRICE));

                    // dara rows
                    TableRow row = new TableRow(this);
                    row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));
                    String[] colText = {Integer.toString(++rowId),
                            maintenanceDateString, Integer.toString(meterReading),
                    mobilOilCompany, Integer.toString(mobilOilPrice),
                    tuningLocation, Integer.toString(tuningPrice),
                    additionalPartsNames, Integer.toString(additionalPartsPrice)};
                    for (String text : colText) {
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

                    row.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            return false;
                        }
                    });

                    row.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            TableRow selectedRow = (TableRow) view;
                            final Context currentContext = ViewMaintenanceDataActivity.this;
                            final TextView selectedRowDate = (TextView) selectedRow.getChildAt(1);
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(currentContext,
                                        android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(currentContext);
                            }
                            builder.setTitle("Delete entry")
                                    .setMessage("Are you sure you want to delete Record for "+
                                            selectedRowDate.getText().toString() +" ?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            String stMaintenanceDate = "";
                                            try {
                                                Date dtMaintenanceDate = sdf2.parse(
                                                        selectedRowDate.getText().toString());
                                                SimpleDateFormat sdfDate = new SimpleDateFormat(
                                                        "yyyy-MM-dd hh:mm:ss",
                                                        Locale.getDefault());
                                                stMaintenanceDate = sdfDate.format(dtMaintenanceDate);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            if(bikeDataDB.deleteMaintenanceRecord(stMaintenanceDate)){
                                                Toast.makeText(currentContext,
                                                        "Record for date " +
                                                                selectedRowDate.getText().toString() +
                                                                " deleted Successfully!!!",
                                                        Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(currentContext,
                                                        "Failed to delete Record!!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            return true;
                        }
                    });

                    tableLayout.addView(row);

                }

            }
            cursor.close();

        } catch (SQLiteException | ParseException e) {
            e.printStackTrace();
        }
    }
}
