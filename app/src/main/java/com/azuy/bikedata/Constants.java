package com.azuy.bikedata;


public interface Constants {
    String DATABASE_NAME = "LEM-2756_BikeData";

    String FUEL_TABLE_NAME = "bikeFuelActivityTable";
    String FUEL_FILLING_DATE = "FillingDate";
    String FUEL_METER_READING = "FuelMeterReading";
    String FUEL_AMOUNT_RS = "FuelAmountRs";
    String FUEL_AMOUNT_LT = "FuelAmountLt";
    String FUEL_PETROL_PUMP = "FuelPetrolPump";
    String FUEL_TABLE_VIEW = "bikeFuelActivityView";
    String FUEL_VIEW_ID_COLUMN = "FuelFillingId";

    String RESERVE_TABLE_NAME = "bikeReserveActivityTable";
    String RESERVE_DATE = "ReserveDate";
    String RESERVE_METERING_READING = "ReserveMeterReading";
    String RESERVE_TABLE_VIEW = "bikeReserveActivityView";
    String RESERVE_VIEW_ID_COLUMN = "ReserveId";

    String MAINTENANCE_TABLE_NAME = "bikeMaintenanceActivityTable";
    String MAINTENANCE_METER_READING = "MaintenanceMeterReading";
    String MAINTENANCE_MOBIL_OIL_COMPANY = "MaintenanceMobilOilCompany";
    String MAINTENANCE_MOBIL_OIL_PRICE = "MaintenanceMobilOilPrice";
    String MAINTENANCE_TUNING_LOCATION = "MaintenanceTuningLocation";
    String MAINTENANCE_TUNING_PRICE = "MaintenanceTuningPrice";
    String MAINTENANCE_ADDITIONAL_PARTS_NAMES = "MaintenanceAdditionalPartsNames";
    String MAINTENANCE_ADDITIONAL_PARTS_PRICE = "MaintenanceAdditioanlPartsPrice";
    String MAINTENANCE_DATE = "MaintenanceDate";
    String MAINTENANCE_TABLE_VIEW = "bikeMaintenanceActivityView";
    String MAINTENANCE_VIEW_ID_COLUMN = "MaintenanceId";

    String SHARED_PREFERENCE_NAME = "BikeDataSharedPreference";
    String SHARED_PREFERENCE_RESTORE_FILE_PICKER_PATH_KEY = "RestoreFilePickerLocation";
    String SHARED_PREFERENCE_BACKUP_FILE_PICKER_PATH_KEY = "BackupFilePickerLocation";

    int WRITE_EXTERNAL_STORAGE_REQUEST = 112;

    String RESERVE_NOTIFICATION_TITLE = "Your Bike is on Reserve";
    String RESERVE_NOTIFICATION_MESSAGE = "Please refill as soon as possible";
    int RESERVE_NOTIFICATION_ID = 555;

}
