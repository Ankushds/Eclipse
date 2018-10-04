package com.mantra.fm220;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.widget.Toast;

public  class CommonMethod {
	private static SharedPreferences.Editor editor;
	//public static String[] NotificationArray=new String[10];

	public static Hashtable hastable = new Hashtable();
	public static String moblileno;
	public static   Vector vectSortedHashtable = new Vector();
	public static int icount=0;
	public static void setPrefsData(Context context, String prefsKey,
			String prefValue) {

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		editor = sharedPreferences.edit();
		editor.putString(prefsKey, prefValue);
		editor.apply();
	}

	public static void setPrefsDataInt(Context context, String prefsKey,
			int prefValue) {

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		editor = sharedPreferences.edit();
		editor.putInt(prefsKey, prefValue);
		editor.apply();
	}
	
	public static boolean saveArray(String[] array, String arrayName, Context mContext) {   
	    SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);  
	    SharedPreferences.Editor editor = prefs.edit();  
	    editor.putInt(arrayName +"_size", array.length);  
	    for(int i=0;i<array.length;i++)  
	       editor.putString(arrayName + "_" + i, array[i]);  
	    
	    return editor.commit();  
	}
	
	
	public static String[] loadArray(String arrayName, Context mContext) {  
	    SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);  
	    int size = prefs.getInt(arrayName + "_size", 0);  
	    String array[] = new String[size];  
	    for(int i=0;i<size;i++)  
	        array[i] = prefs.getString(arrayName + "_" + i, null);  
	    return array;  
	}
	

	public static String getPrefsData(Context context, String prefsKey,
			String defaultValue) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String prefsValue = sharedPreferences.getString(prefsKey, defaultValue);
		return prefsValue;
	}
	public static int getPrefsDataInt(Context context, String prefsKey,
			int defaultValue) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		int prefsValue = sharedPreferences.getInt(prefsKey, defaultValue);
		return prefsValue;
	}
	
	/** Called for checking Internet connection */

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public static boolean isEmailValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	public static void showAlertActivity(String message, final Activity context) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {

								context.finish();
							}
						});
		// AlertDialog alert = builder.create();

		try {
			builder.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressLint("SimpleDateFormat")
	public static String getYears(String str_date) {

		// System.out.println("str_date"+str_date);

		Calendar dob = Calendar.getInstance();
		SimpleDateFormat dateFormat = null;
		Date date = null;
		int age = 0;
		if (str_date == null)
			return "";

		// set the format to use as a constructor argument
		dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		/*
		 * if (str_date.trim().length() != dateFormat.toPattern().length())
		 * return "";
		 */
		dateFormat.setLenient(false);
		try {
			// parse the inDate parameter
			date = dateFormat.parse(str_date.trim());
			dob.setTime(date);

			Calendar today = Calendar.getInstance();
			int curYear = today.get(Calendar.YEAR);
			int curMonth = today.get(Calendar.MONTH);
			int curDay = today.get(Calendar.DAY_OF_MONTH);

			int year = dob.get(Calendar.YEAR);
			int month = dob.get(Calendar.MONTH);
			int day = dob.get(Calendar.DAY_OF_MONTH);

			System.out.println("++++++ " + year);

			age = curYear - year;

			if (curMonth < month || (month == curMonth && curDay < day)) {
				age--;

				System.out
						.println("###################################   age ############"
								+ age);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		// return String.valueOf(age + " years");
		return String.valueOf(age);

	}

	public static void showAlert(String message, Activity context) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {

							}
						});

		try {
			builder.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static byte[] convertBitmapToBytes(Bitmap bitmap) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		bitmap.compress(CompressFormat.JPEG, 80, bos);
		// photo.compress(CompressFormat.JPEG, 80, bos);

		byte[] imageData = bos.toByteArray();
		return imageData;
	}

	public static void showNetworkErrorMessage(String Title, String Message,
			final Activity context) {

		AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);
		alt_bld.setMessage(Message).setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						context.finish();

					}
				});

		AlertDialog alert = alt_bld.create();
		alert.setTitle(Title);

		if (context.isFinishing() == false) {
			alert.show();
		}

	}

	@SuppressLint("SimpleDateFormat")
	public static String getCurrentDateTimeInColonFormat(String date) {

		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");// dd/MM/yyyy
		Date now = new Date();
		String strDate = sdfDate.format(now);

		return strDate;
	}

	public static Bitmap reduceImageSize(File f, Context context) {

		Bitmap m = null;
		try {

			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			final int REQUIRED_SIZE = 150;

			int width_tmp = o.outWidth, height_tmp = o.outHeight;

			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			o2.inPreferredConfig = Bitmap.Config.ARGB_8888;
			m = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			Toast.makeText(
					context,
					"Image file not found in your phone. Please select another image.",
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {

		}
		return m;
	}

}
