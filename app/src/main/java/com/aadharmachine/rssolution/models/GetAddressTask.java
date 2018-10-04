package com.aadharmachine.rssolution.models;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.aadharmachine.rssolution.AttendanceActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by apple on 11/06/16.
 */
public class GetAddressTask extends AsyncTask<String, Void, String> {

    private AttendanceActivity activity;
    private String address;
    private String province;
    private String state;
    private String country;


    public GetAddressTask(AttendanceActivity activity) {
        super();
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(activity, Locale.getDefault());

//
//
            try {
                addresses = geocoder.getFromLocation(Double.parseDouble(params[0]), Double.parseDouble(params[1]), 1);

                if (addresses != null && addresses.size() > 0) {
                    //get current Street name
                    address = addresses.get(0).getAddressLine(0);

                    //get current province/City
                    province = addresses.get(0).getAddressLine(1);

                    //get state
                    state = addresses.get(0).getAddressLine(2);

                    //get country
                    country = addresses.get(0).getCountryName();
                }
                //get postal code
//            String postalCode = addresses.get(0).getPostalCode();

                //get place Name
//            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL


                return address;


            } catch (IOException ex) {
                ex.printStackTrace();
                return "Internet Connection is slow...";

            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                return "IllegalArgument Exception.";
            }



    }

    /**
     * When the task finishes, onPostExecute() call back data to Activity UI and displays the address.
     *
     * @param address
     */
    /*@Override
    protected void onPostExecute(String address) {
        // Call back Data and Display the current address in the UI
        activity.callBackDataFromAsyncTask(address);


    }*/
}