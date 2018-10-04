package com.aadharmachine.rssolution.models;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.aadharmachine.rssolutions.Gps.GPSTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/*import com.RealtimeBiometrics.rss.utils.CommonMethod;
import com.RealtimeBiometrics.rss.utils.Constants;*/
/*import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;*/

/**
 * Created by SonuShaikh on 18/07/17.
 */
//import com.google.android.gms.common.GooglePlayServicesUtil;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    //    web services Location upate
    private static final String TAG = "My LocationService";
    private final String NAMESPACE = "http://tempuri.org/";
    private final String SOAP_ACTION = "http://tempuri.org/MarkGpsAttendance";
    private final String METHOD_NAME = "MarkGpsAttendance";
    public String URL = "";


    private Context mContext;
    private boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;

    GPSTracker gps;
    private SQLiteDatabase db;
    public Date date;


    //    web services Location upate
    //private SoapObject response;
    //private SoapObject request;
    private String results;
    public static String Mylan,Mylon,Address;

    @Override
    public void onCreate() {
        super.onCreate();
        //createDatabase();
        mContext = LocationService.this;


        /*
        *Webservces Host URL
        */
        /*URL = "http://"
                + CommonMethod.getPrefsData(mContext, Constants.PREF_USER_IP,
                "") + "/android.asmx?op=MarkGpsAttendance";*/


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // if we are currently trying to get a location and the alarm manager has called this again,
        // no need to start processing a new location.
        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;

            startTracking();


        }

        return START_NOT_STICKY;
    }

    private void startTracking() {
        Log.d(TAG, "startTracking");

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            Log.e(TAG, "unable to connect to google play services.");
        }
    }


    //     Fetch the address of the location by lat and log
    public String getAddress(Context ctx, double latitude, double longitude) {


        StringBuilder result = new StringBuilder();
        String z = null;
        Mylan = Double.toString(latitude);
        Mylon = Double.toString(longitude);
        try {

            if (Mylan.equals("0.0") && Mylon.equals("0.0")) {
//                TODO if error occur
//                Log.i("My Data info11:", Mylan);
//                Log.i("My Data info11:", Mylong);
                z = "Error";
            } else {
                Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
                List<android.location.Address> addresses = geocoder.getFromLocation(latitude,
                        longitude, 1);
                if (addresses.size() > 0) {
                    android.location.Address address = addresses.get(0);

                    String addressline0 = address.getAddressLine(0);
                    String addressline1 = address.getAddressLine(1);
                    String addressline2 = address.getAddressLine(2);
                    String locality = address.getLocality();
                    String city = address.getCountryName();
                    String region_code = address.getCountryCode();
                    String zipcode = address.getPostalCode();

                    result.append(addressline0).append(" ");
                    result.append(addressline1).append(" ");
                    result.append(addressline2).append(" ");
                    result.append(city).append(" ");

//				    result.append(locality + " ");
//				    result.append(city + " " + region_code + " ");
//				    result.append(zipcode);
//				    Log.d("locality", locality);
//				    Log.d("city", city);
//				    Log.d("region_code", region_code);
//				    Log.d("zipcode", zipcode);

                    Log.d("rrrrraaaaaaa", result.toString());
                    z = result.toString();
                }

            }
        } catch (IOException e) {
            Log.e("My Tag", e.getMessage());
            z = "Error";
        }
        return z;

    }

    protected void sendLocationDataToWebsite(Location location) {
        // formatted for mysql datetime format
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());
        Date date = new Date(location.getTime());


        SharedPreferences sharedPreferences = this.getSharedPreferences("com.RealtimeBiometrics.rss.GpsLocTracker.prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

//        calculate distance
        float totalDistanceInMeters = sharedPreferences.getFloat("totalDistanceInMeters", 0f);

        boolean firstTimeGettingPosition = sharedPreferences.getBoolean("firstTimeGettingPosition", true);

        if (firstTimeGettingPosition) {
            editor.putBoolean("firstTimeGettingPosition", false);
        } else {
            Location previousLocation = new Location("");
            previousLocation.setLatitude(sharedPreferences.getFloat("previousLatitude", 0f));
            previousLocation.setLongitude(sharedPreferences.getFloat("previousLongitude", 0f));

            float distance = location.distanceTo(previousLocation);
            totalDistanceInMeters += distance;
            editor.putFloat("totalDistanceInMeters", totalDistanceInMeters);
        }

        editor.putFloat("previousLatitude", (float) location.getLatitude());
        editor.putFloat("previousLongitude", (float) location.getLongitude());
        editor.apply();


        Double speedInMilesPerHour = location.getSpeed() * 2.2369;

// Accuracy And Atitude info
        Double accuracyInFeet = location.getAccuracy() * 3.28;
        Double altitudeInFeet = location.getAltitude() * 3.28;

        Float direction = location.getBearing();


//        Get the ADDRESS

        String Address = getAddress(mContext, location.getLatitude(), location.getLongitude());
//        String Address1 = "IMEI NO: "+ sharedPreferences.getString("appID", "") +  "\nLocation: "+Address;


//        Login Creditails

        /*String loginUser = CommonMethod.getPrefsData(getBaseContext(),
                Constants.PREF_LOGIN_NAME, "");
        String username = CommonMethod.getPrefsData(getBaseContext(),
                Constants.PREF_USER_NAME, "");
        String password = CommonMethod.getPrefsData(mContext,
                Constants.PREF_PASSWORD, "");*/

       /* try {
//        Log info
            Log.e("My INFO TAG  ", "==========================================================================");
            Log.e("My String LoginName", loginUser);
            Log.i("My String latitude: ", Double.toString(location.getLatitude()));
            Log.i("My String longitude: ", Double.toString(location.getLongitude()));
            Log.i("My String speed: ", Integer.toString(speedInMilesPerHour.intValue()));
            Log.i("My String date : ", dateFormat.format(date));
            Log.i("My Str locationmethod :", location.getProvider());
            Log.i("My String username: ", username);

            Log.i("My String appID: ", sharedPreferences.getString("appID", ""));
            Log.i("My String SessionID: ", sharedPreferences.getString("sessionID", ""));
            Log.i("My String accuracy: ", Integer.toString(accuracyInFeet.intValue()));
            Log.i("My String extrainfo: ", Integer.toString(altitudeInFeet.intValue()));
            Log.i("My String eventtype: ", "android");
            Log.i("My String direction: ", Integer.toString(direction.intValue()));
            Log.i("My String Address: ", Address);
            Log.e("My INFO TAG  ", "==========================================================================");
        }catch (Exception bb){
            bb.printStackTrace();
        }




//        Request service for the Location Update
        request = new SoapObject(NAMESPACE, METHOD_NAME);

        //add LoginName
        PropertyInfo addLoginName = new PropertyInfo();
        addLoginName.setName("loginname");
        addLoginName.setValue(loginUser);
        addLoginName.setType(String.class);
        request.addProperty(addLoginName);

        //add Username
        PropertyInfo addUserName = new PropertyInfo();
        addUserName.setName("username");
        addUserName.setValue(username);
        addUserName.setType(String.class);
        request.addProperty(addUserName);

        //add Username
       *//* PropertyInfo addPwd = new PropertyInfo();
        addPwd.setName("pwd");
        addPwd.setValue(password);
        addPwd.setType(String.class);
        request.addProperty(addPwd);
*//*
        //add Date
        PropertyInfo addDate = new PropertyInfo();
        addDate.setName("date");
        addDate.setValue(dateFormat.format(date));
        addDate.setType(String.class);
        request.addProperty(addDate);


        //add latitude
        PropertyInfo addLat = new PropertyInfo();
        addLat.setName("lan");
        addLat.setValue(Double.toString(location.getLatitude()));
        addLat.setType(String.class);
        request.addProperty(addLat);

        //add Longitude
        PropertyInfo addLong = new PropertyInfo();
        addLong.setName("log");
        addLong.setValue(Double.toString(location.getLongitude()));
        addLong.setType(String.class);
        request.addProperty(addLong);

        //add Address
        PropertyInfo addAddress = new PropertyInfo();
        addAddress.setName("Address");
        addAddress.setValue(Address);
        addAddress.setType(String.class);
        request.addProperty(addAddress);


//        //add LocationMethod
//        PropertyInfo addLocMethod = new PropertyInfo();
//        addLocMethod.setName("LocationMethod");
//        addLocMethod.setValue(location.getProvider());
//        addLocMethod.setType(String.class);
//        request.addProperty(addLocMethod);
//
//
//        String Distance = String.format("%.1f", totalDistanceInMeters / 1609);//in miles
//        if (totalDistanceInMeters > 0) {
//            //add Distance
//            PropertyInfo addDistance = new PropertyInfo();
//            addDistance.setName("Distance");
//            addDistance.setValue(Distance);
//            addDistance.setType(String.class);
//            request.addProperty(addDistance);
//        } else {
//
//            PropertyInfo addDistance = new PropertyInfo();
//            addDistance.setName("Distance");
//            addDistance.setValue("0.0");
//            addDistance.setType(String.class);
//            request.addProperty(addDistance);
//
//        }
//
//        String UssrName = sharedPreferences.getString("userName", "");
//        //add Username
//        PropertyInfo addUserName = new PropertyInfo();
//        addUserName.setName("UserName");
//        addUserName.setValue(UssrName);
//        addUserName.setType(String.class);
//        request.addProperty(addUserName);
//
//        String IMEI = sharedPreferences.getString("appID", "");
//        //add AppId (IMEI Number)
//        PropertyInfo addAppId = new PropertyInfo();
//        addAppId.setName("Imei");
//        addAppId.setValue(IMEI);
//        addAppId.setType(String.class);
//        request.addProperty(addAppId);
//
//        String SsId = sharedPreferences.getString("sessionID", "");
//        //add speed SessionId
//        PropertyInfo addSessionId = new PropertyInfo();
//        addSessionId.setName("SessionId");
//        addSessionId.setValue(SsId);
//        addSessionId.setType(String.class);
//        request.addProperty(addSessionId);
//
//        //add accuray
//        PropertyInfo addAccuracy = new PropertyInfo();
//        addAccuracy.setName("Accuracy");
//        addAccuracy.setValue(Integer.toString(accuracyInFeet.intValue()));
//        addAccuracy.setType(String.class);
//        request.addProperty(addAccuracy);
//
//        //add extraInfo (Altitude)
//        PropertyInfo addExtraInfo = new PropertyInfo();
//        addExtraInfo.setName("Altitude");
//        addExtraInfo.setValue(Integer.toString(altitudeInFeet.intValue()));
//        addExtraInfo.setType(String.class);
//        request.addProperty(addExtraInfo);
//
//        //add Eventtype
//        PropertyInfo addEventType = new PropertyInfo();
//        addEventType.setName("EventType");
//        addEventType.setValue("Android");
//        addEventType.setType(String.class);
//        request.addProperty(addEventType);
//
//        //add Direction
//        PropertyInfo addirection = new PropertyInfo();
//        addirection.setName("Direction");
//        addirection.setValue(Integer.toString(direction.intValue()));
//        addirection.setType(String.class);
//        request.addProperty(addirection);


        try {
//        TODO Uplaod the Data in the server using the webservices;
//        Get the response Servies
            if (CommonMethod.isOnline(mContext)) {
                if ((Address.equalsIgnoreCase("Error"))) {
//                Log.e("My Result:: ","Success");
                    Toast.makeText(mContext, "Can't update tracking info to server" +
                            "\nCheck internet connection and \nGPS setting. Try again.", Toast.LENGTH_LONG).show();

                    stopSelf();

                } else {
                    System.out.println("I M UPDATE LOCATION");

                    LocUpdateToServer runner = new LocUpdateToServer();
                    runner.execute("");

                    Log.e("My Result:: ", "Success");
                }
            }else {
                insertIntoDB();

            }

        } catch (Exception ignored) {



        }
*/
    }

    private void enableStrictMode() {
        // TODO Auto-generated method stub
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {

            SharedPreferences sharedPreferences = this.getSharedPreferences("com.RealtimeBiometrics.rss.GpsLocTracker.prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Log.e(TAG, "My position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());
            editor.putString("previousLatitude1", Double.toString(location.getLatitude()));
            editor.putString("previousLongitude1", Double.toString(location.getLongitude()));
            editor.apply();
            // we have our desired accuracy of 500 meters so lets quit this service,
            // onDestroy will be called and stop our location uodates
            if (location.getAccuracy() < 500.0f) {
                stopLocationUpdates();
                sendLocationDataToWebsite(location);
            }


          }
    }

    private void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(30*1000); // milliseconds
        locationRequest.setFastestInterval(5*1000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");

        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "GoogleApiClient connection has been suspend");
    }




}
