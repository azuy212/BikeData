package com.azuy.bikedata;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    Button btnFuel, btnReserve, btnMaintenance, btnViewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        attachOnClickListeners();
    }

    private void initialize() {
        btnFuel = (Button) findViewById(R.id.btnFuel);
        btnReserve = (Button) findViewById(R.id.btnReserve);
        btnMaintenance = (Button) findViewById(R.id.btnMaintenance);
        btnViewData = (Button) findViewById(R.id.btnViewData);
    }

    private void attachOnClickListeners() {

        btnFuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FuelActivity.class);
                startActivity(intent);
            }
        });

        btnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReserveActivity.class);
                startActivity(intent);
            }
        });

        btnMaintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MaintenanceActivity.class);
                startActivity(intent);
            }
        });

        btnViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewDataSelectActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        backupDatabase();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.WRITE_EXTERNAL_STORAGE_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            "Thanks to allow Please save your Chart/Data once again", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,
                            "You need to allow it inorder to save your data", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void backupDatabase() {
        grantAccessForExternalStorage();
        try{
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//com.azuy.bikedata//databases//"+Constants.DATABASE_NAME;
                String backupDBPath = "//BikeData//"+Constants.DATABASE_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getBaseContext(), "Backup Created Successfully!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "current DB doesn't exists", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Unable to write on SD Card", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void grantAccessForExternalStorage(){
        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) MainActivity.this, PERMISSIONS, Constants.WRITE_EXTERNAL_STORAGE_REQUEST );
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
