package com.azuy.bikedata;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    Button btnFuel, btnReserve, btnMaintenance, btnViewData;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.action_restore:
                getDatabaseRestoreChooserDialog().show();
                return true;

            case R.id.action_about:
                makeToast("Bike Data!!!", 2000);
                return true;

            case R.id.action_backupPath:
                getDatabaseBackupChooserDialog().show();
                return true;

            case R.id.action_exit:
                makeToast("Closing...", 1000);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public ChooserDialog getDatabaseRestoreChooserDialog() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
        final SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
        String defaultFilePickerPath = sharedPreferences.getString(
                Constants.SHARED_PREFERENCE_RESTORE_FILE_PICKER_PATH_KEY,
                Environment.getExternalStorageDirectory().getAbsolutePath()
        );
        return getFileChooserDialog(
                defaultFilePickerPath,
                new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        sharedPrefEditor.putString(Constants.SHARED_PREFERENCE_RESTORE_FILE_PICKER_PATH_KEY, path);
                        sharedPrefEditor.apply();
                        restoreBikeDate(pathFile);
                    }
                },
                false);
    }

    public ChooserDialog getDatabaseBackupChooserDialog() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
        final SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
        String defaultFilePickerPath = sharedPreferences.getString(
                Constants.SHARED_PREFERENCE_BACKUP_FILE_PICKER_PATH_KEY,
                Environment.getExternalStorageDirectory().getAbsolutePath()
        );
        return getFileChooserDialog(
                defaultFilePickerPath,
                new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        sharedPrefEditor.putString(Constants.SHARED_PREFERENCE_BACKUP_FILE_PICKER_PATH_KEY, path);
                        sharedPrefEditor.apply();
                    }
                },
                true);
    }

    public ChooserDialog getFileChooserDialog(
            String defaultFilePickerPath,
            ChooserDialog.Result resultCallback
            , boolean folderChooser
    ) {
        ChooserDialog chooserDialog = new ChooserDialog(this);
        if (folderChooser) {
            chooserDialog.withFilter(true, false);
            chooserDialog.enableOptions(true);
        }
        chooserDialog.withStartFile(defaultFilePickerPath);
        chooserDialog.withChosenListener(resultCallback);
        chooserDialog.build();
        return chooserDialog;
    }

    public void restoreBikeDate(final File backupDatabaseFile) {
        grantAccessForExternalStorage();
        try {
            final File databasePath = getApplicationContext().getDatabasePath(Constants.DATABASE_NAME);

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(MainActivity.this);
            }
            builder.setTitle("Replace Database")
                    .setMessage("Are you sure you want to replace your database?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                FileChannel src = new FileInputStream(backupDatabaseFile).getChannel();
                                FileChannel dst = new FileOutputStream(databasePath).getChannel();
                                dst.transferFrom(src, 0, src.size());
                                src.close();
                                dst.close();
                                Toast.makeText(getBaseContext(), "Backup Restored Successfully!", Toast.LENGTH_LONG).show();
                            } catch (IOException ioe) {
                                makeToast("Transfer Error Occurred: " + ioe.getMessage(), 2000);
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void backupDatabase() {
        grantAccessForExternalStorage();
        try {
            File internalStorage = Environment.getExternalStorageDirectory();

            if (internalStorage.canWrite()) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
                String backupDBPath = sharedPreferences.getString(Constants.SHARED_PREFERENCE_BACKUP_FILE_PICKER_PATH_KEY,
                        Constants.DATABASE_NAME);
                File databasePath = getApplicationContext().getDatabasePath(Constants.DATABASE_NAME);
                File backupDB = new File(backupDBPath, Constants.DATABASE_NAME);

                if (databasePath.exists()) {
                    FileChannel src = new FileInputStream(databasePath).getChannel();
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
            Toast.makeText(this, "Backup Creation Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void grantAccessForExternalStorage() {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, Constants.WRITE_EXTERNAL_STORAGE_REQUEST);
            }
        }
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
