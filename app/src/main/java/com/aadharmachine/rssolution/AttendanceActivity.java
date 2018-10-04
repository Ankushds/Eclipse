package com.aadharmachine.rssolution;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.aadharmachine.rssolution.models.GetAddressTask;
import com.aadharmachine.rssolutions.Gps.GPSTracker;
import com.access.aadharapp220.MainActivity;
import com.access.aadharapp220.R;
import com.access.aadharapp220.models.Contact;
import com.access.aadharapp220.models.DatabaseHandler;
import com.acpl.access_computech_fm220_sdk.FM220_Scanner_Interface;
import com.acpl.access_computech_fm220_sdk.acpl_FM220_SDK;
import com.acpl.access_computech_fm220_sdk.fm220_Capture_Result;
import com.acpl.access_computech_fm220_sdk.fm220_Init_Result;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.mantra.fm220.CommonMethod;
import com.mantra.fm220.Constants;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class AttendanceActivity extends AppCompatActivity implements FM220_Scanner_Interface, GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,ActivityCompat.OnRequestPermissionsResultCallback,
		PermissionUtils.PermissionResultCallback {

	GPSTracker gpsTracker;

	private final AppCompatActivity activity = AttendanceActivity.this;

	protected LocationManager locationManager;
	protected LocationListener locationListener;
	protected boolean gps_enabled, network_enabled;

	private EditText uid, name;
	final int TAKE_PICTURE = 1;
	final int ACTIVITY_SELECT_IMAGE = 2;
	private acpl_FM220_SDK FM220SDK;
	private Button btnSave;
	private ImageView LeftHandTHumb, RightHandThumb, ivMenu, status_img;
	private TextView textMessage, txtUserName, tv_latitude, tv_address;
	private ImageView imageView;
	private Button Capture_match;

	String finger1, finger2;
	private Context mContext;
	private DatabaseHandler db;
	Double latitude, longitude;
	public static String location2;
	private GoogleMap mMap;

	private String thumbImage;

	private GoogleApiClient mGoogleApiClient;
	PermissionUtils permissionUtils;
	ArrayList<String> permissions=new ArrayList<>();

	private final static int PLAY_SERVICES_REQUEST = 1000;
	private final static int REQUEST_CHECK_SETTINGS = 2000;
	private Location mLastLocation;

	/***************************************************
	 * if you are use telecom device enter "Telecom_Device_Key" as your provided key otherwise send "" ;
	 */
	private static final String Telecom_Device_Key = "";
	private byte[] t1, t2, t3, t4;
	private String f1,f2,f3;
	SQLiteDatabase sqLiteDatabase;
	String s1, s2, s3;
	Bitmap bitmap, bitmap1;
	TelephonyManager tel;
    private  Contact contact;



	//region USB intent and functions
	private UsbManager manager;
	private PendingIntent mPermissionIntent;
	private UsbDevice usb_Dev;
	private static final String ACTION_USB_PERMISSION = "com.access.testappfm220.USB_PERMISSION";

	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				int pid, vid;
				pid = device.getProductId();
				vid = device.getVendorId();
				if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
					FM220SDK.stopCaptureFM220();
					FM220SDK.unInitFM220();
					usb_Dev = null;
					textMessage.setText("FM220 disconnected");
					DisableCapture();
				}
			}
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						if (device != null) {
							// call method to set up device communication
							int pid, vid;
							pid = device.getProductId();
							vid = device.getVendorId();
							if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
								fm220_Init_Result res = FM220SDK.InitScannerFM220(manager, device, Telecom_Device_Key);
								if (res.getResult()) {
									textMessage.setText("FM220 ready. " + res.getSerialNo());
									EnableCapture();
								} else {
									textMessage.setText("Error :-" + res.getError());
									DisableCapture();
								}
							}
						}
					} else {
						textMessage.setText("Employee Blocked USB connection");
						textMessage.setText("FM220 ready");
						DisableCapture();
					}
				}
			}
			if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
				synchronized (this) {
					UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (device != null) {
						// call method to set up device communication
						int pid, vid;
						pid = device.getProductId();
						vid = device.getVendorId();
						if ((pid == 0x8225) && (vid == 0x0bca) && !FM220SDK.FM220isTelecom()) {
							Toast.makeText(context, "Wrong device type application restart required!", Toast.LENGTH_LONG).show();
							finish();
						}
						if ((pid == 0x8220) && (vid == 0x0bca) && FM220SDK.FM220isTelecom()) {
							Toast.makeText(context, "Wrong device type application restart required!", Toast.LENGTH_LONG).show();
							finish();
						}

						if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
							if (!manager.hasPermission(device)) {
								textMessage.setText("FM220 requesting permission");
								manager.requestPermission(device, mPermissionIntent);
							} else {
								fm220_Init_Result res = FM220SDK.InitScannerFM220(manager, device, Telecom_Device_Key);
								if (res.getResult()) {
									textMessage.setText("FM220 ready. " + res.getSerialNo());
									EnableCapture();
								} else {
									textMessage.setText("Error :-" + res.getError());
									DisableCapture();
								}
							}
						}
					}
				}
			}
		}
	};
	private String img_str1;
	private String img_str2;

	@Override
	protected void onNewIntent(Intent intent) {
		if (getIntent() != null) {
			return;
		}
		super.onNewIntent(intent);
		setIntent(intent);
		try {
			if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED) && usb_Dev == null) {
				UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				if (device != null) {
					// call method to set up device communication & Check pid
					int pid, vid;
					pid = device.getProductId();
					vid = device.getVendorId();
					if ((pid == 0x8225) && (vid == 0x0bca)) {
						if (manager != null) {
							if (!manager.hasPermission(device)) {
								textMessage.setText("FM220 requesting permission");
								manager.requestPermission(device, mPermissionIntent);
							}
//                            else {
//                                fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
//                                if (res.getResult()) {
//                                    textMessage.setText("FM220 ready. "+res.getSerialNo());
//                                    EnableCapture();
//                                }
//                                else {
//                                    textMessage.setText("Error :-"+res.getError());
//                                    DisableCapture();
//                                }
//                            }
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}

	@Override
	protected void onDestroy() {
		try {
			unregisterReceiver(mUsbReceiver);
			FM220SDK.unInitFM220();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendance);

		//Instantiate database handler


//        FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this);
		textMessage = (TextView) findViewById(R.id.textMessage);
		txtUserName = (TextView) findViewById(R.id.txtUserName);
		LeftHandTHumb = (ImageView) findViewById(R.id.button);
		RightHandThumb = (ImageView) findViewById(R.id.button2);
		btnSave = (Button) findViewById(R.id.btn_save);
		imageView = (ImageView) findViewById(R.id.imageView);
		Capture_match = (Button) findViewById(R.id.button4);
		ivMenu = (ImageView) findViewById(R.id.iv_menu);
		status_img = (ImageView) findViewById(R.id.status_img);
		//markAttendance = (Button) findViewById(R.id.mark_attendance);

		/*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);*/

		permissionUtils=new PermissionUtils(AttendanceActivity.this);

		permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
		permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

		permissionUtils.check_permission(permissions,"Need GPS permission for getting your location",1);

		buildGoogleApiClient();
		tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		tv_address = (TextView) findViewById(R.id.tv_address);
		tv_latitude = (TextView) findViewById(R.id.tv_latitude);
		uid = (EditText) findViewById(R.id.et_name);
		name = (EditText) findViewById(R.id.employee_name);
		db = new DatabaseHandler(this);
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
		tv_address.setText("IMEI Number: "+tel.getDeviceId().toString());
		/*mMap.setMyLocationEnabled(true);

		//set "listener" for changing my location
		mMap.setOnMyLocationChangeListener(myLocationChangeListener());*/

		uid.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(final CharSequence s, int start, int before, int count) {
				if (s.toString().length() > 0) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							final Contact contact = db.getData(s.toString());
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (contact != null && !TextUtils.isEmpty(contact.get_uid())) {
										LeftHandTHumb.setImageBitmap(stringToBitMap(contact.get_fp1()));
										RightHandThumb.setImageBitmap(stringToBitMap1(contact.get_fp2()));
										txtUserName.setText("Employee Name: "+contact.get_fname());
										status_img.setImageDrawable(null);

									} else {
										Log.e("AttendanceActivity -->", "No Record found");
									}
								}
							});
						}
					}).start();
				}
				if(uid.getText().length()>3){
					//callApi();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});


		//Region USB initialisation and Scanning for device
		SharedPreferences sp = getSharedPreferences("last_FM220_type", Activity.MODE_PRIVATE);
		boolean oldDevType = sp.getBoolean("FM220type", true);

		manager = (UsbManager) getSystemService(Context.USB_SERVICE);
		final Intent piIntent = new Intent(ACTION_USB_PERMISSION);
		if (Build.VERSION.SDK_INT >= 16) piIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
		mPermissionIntent = PendingIntent.getBroadcast(getBaseContext(), 1, piIntent, 0);

		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		registerReceiver(mUsbReceiver, filter);
		UsbDevice device = null;
		for (UsbDevice mdevice : manager.getDeviceList().values()) {
			int pid, vid;
			pid = mdevice.getProductId();
			vid = mdevice.getVendorId();
			boolean devType;
			if ((pid == 0x8225) && (vid == 0x0bca)) {
				FM220SDK = new acpl_FM220_SDK(getApplicationContext(), this, true);
				devType = true;
			} else if ((pid == 0x8220) && (vid == 0x0bca)) {
				FM220SDK = new acpl_FM220_SDK(getApplicationContext(), this, false);
				devType = false;
			} else {
				FM220SDK = new acpl_FM220_SDK(getApplicationContext(), this, oldDevType);
				devType = oldDevType;
			}
			if (oldDevType != devType) {
				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean("FM220type", devType);
				editor.apply();
			}
			if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
				device = mdevice;
				if (!manager.hasPermission(device)) {
					textMessage.setText("FM220 requesting permission");
					manager.requestPermission(device, mPermissionIntent);
				} else {
					/*Intent intent = this.getIntent();
					if (intent != null) {
						if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
							finishAffinity();
						}
					}*/
					fm220_Init_Result res = FM220SDK.InitScannerFM220(manager, device, Telecom_Device_Key);
					if (res.getResult()) {
						textMessage.setText("FM220 ready. " + res.getSerialNo());
						EnableCapture();
					} else {
						textMessage.setText("Error :-" + res.getError());
						DisableCapture();

					}
				}
				break;
			}
		}
		if (device == null) {
			textMessage.setText("Pl connect FM220");
			FM220SDK = new acpl_FM220_SDK(getApplicationContext(), this, oldDevType);
		}


		/*LeftHandTHumb.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				DisableCapture();
				FM220SDK.CaptureFM220(2, true, false);
			}
		});

		RightHandThumb.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				DisableCapture();
				FM220SDK.CaptureFM220(2, true, true);
				imageView.setImageBitmap(null);
			}
		});*/

		ivMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setting();
			}
		});

		Capture_match.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				DisableCapture();
				FM220SDK.CaptureFM220(2, true, true);
				imageView.setImageBitmap(null);

//                DisableCapture();
//                FM220SDK.MatchFM220(2, true, true, t1);
                /*if (t1 != null && t4 != null) {
                    if (FM220SDK.MatchFM220(t1, t4)) {
                        textMessage.setText("Finger matched");
						Toast.makeText(activity, "t1 data....."+t1, Toast.LENGTH_SHORT).show();
						Toast.makeText(activity, "t2 data......."+t4, Toast.LENGTH_SHORT).show();
                        t1 = null;
                        t4 = null;
                    } *//*else if (FM220SDK.MatchFM220(t1,t4)){

                        textMessage.setText("Finger matched");

                    }*//*else {
                        textMessage.setText("Finger not matched");
						Toast.makeText(activity, "t1 data....."+t1, Toast.LENGTH_SHORT).show();
						Toast.makeText(activity, "t2 data......."+t4, Toast.LENGTH_SHORT).show();
						//Toast.makeText(activity, "t3 data.........."+t3, Toast.LENGTH_SHORT).show();
                    }
                } else {

                    textMessage.setText("Pl capture first");
					Toast.makeText(activity, "t1 data....."+t1, Toast.LENGTH_SHORT).show();
					Toast.makeText(activity, "t2 data......."+t2, Toast.LENGTH_SHORT).show();
					Toast.makeText(activity, "t3 data.........."+t3, Toast.LENGTH_SHORT).show();
				}
//                String teamplet match example using FunctionBAse64 function .....
                FunctionBase64();*/

				/*if (f1 != null && f2 != null && f3 != null) {
					if (FM220SDK.MatchFM220String(f1, f2)) {
						textMessage.setText("Finger matched");
						t1 = null;
						t2 = null;
					}else
					if (FM220SDK.MatchFM220String(f1, f3)) {
						textMessage.setText("Finger matched");
						t1 = null;
						t2 = null;
					}else{
						Toast.makeText(activity, "Finger string Match Done", Toast.LENGTH_SHORT).show();
					}
				}*/


				/*if (t1 != null && t2 != null && t3 != null) {
					if (FM220SDK.MatchFM220(t1, t2)) {
						textMessage.setText("Finger matched");
						t1 = null;
						t2 = null;
					}
					if (FM220SDK.MatchFM220(t1, t3)) {
						textMessage.setText("Finger matched");
						t1 = null;
						t2 = null;
					} else {
						//textMessage.setText("Finger not matched");
//						System.out.println("Finger enroll data" + t1);
//						System.out.println("Finger enroll data" + t2);
						Toast.makeText(activity, "Finger Match Done", Toast.LENGTH_SHORT).show();

						Log.d("Enroll finger data", "T1" + t1);
						Log.d("Fetch finger value", "T2" + t2);
						Log.d("Fetch Finger Value", "T3" + t3);
					}
				} else {
					textMessage.setText("Pl capture first");
				}
				//                String teamplet match example using FunctionBAse64 function .....
				FunctionBase64();*/
			}
		});

	}

	private Bitmap stringToBitMap(String encodedString) {
		try {
			f3 = encodedString;
			t3 = Base64.decode(encodedString, Base64.NO_WRAP);
			bitmap = BitmapFactory.decodeByteArray(t3, 0, t3.length);
			Log.e("t3 data", "" + bitmap);
			return bitmap;
		} catch (Exception e) {
			e.getMessage();
			return null;
		}
	}

	private Bitmap stringToBitMap1(String encodedString) {
		try {
			f2 = encodedString;
			t2 = Base64.decode(encodedString, Base64.NO_WRAP);
			bitmap1 = BitmapFactory.decodeByteArray(t2, 0, t2.length);
			return bitmap1;
		} catch (Exception e) {
			e.getMessage();
			return null;
		}
	}

	private void FunctionBase64() {
		try {

			String t1base64 = Base64.encodeToString(t1, Base64.DEFAULT);
			String t2base64 = Base64.encodeToString(t2, Base64.DEFAULT);
			String t3Base64 = Base64.encodeToString(t3, Base64.DEFAULT);

			if (Objects.equals(t1base64, t2base64)) {
				if (FM220SDK.MatchFM220String(t1base64, t2base64)) {
					Toast.makeText(getBaseContext(), "Finger matched", Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(getBaseContext(), "Finger not matched", Toast.LENGTH_SHORT).show();

			} else if (Objects.equals(t1base64, t3Base64)) {
				if (FM220SDK.MatchFM220String(t1base64, t2base64)) {
					Toast.makeText(getBaseContext(), "Finger matched", Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(getBaseContext(), "Finger not matched", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, "P1 Capture First", Toast.LENGTH_SHORT).show();
			}

           /* if (t1 != null && t2 != null ) {
                t1base64 = Base64.encodeToString(t1, Base64.NO_WRAP);
                t2base64 = Base64.encodeToString(t2, Base64.NO_WRAP);
                if (FM220SDK.MatchFM220String(t1base64, t2base64)){
                    Toast.makeText(getBaseContext(), "Finger matched", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getBaseContext(), "Finger not matched", Toast.LENGTH_SHORT).show();
            }else if (t1 != null && t3 != null){
                t1base64 = Base64.encodeToString(t1, Base64.NO_WRAP);
                t3Base64 = Base64.encodeToString(t3, Base64.DEFAULT);
                if (FM220SDK.MatchFM220String(t1base64, t3Base64)) {
                    Toast.makeText(getBaseContext(), "Finger Matched", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(getBaseContext(), "Finger not matched", Toast.LENGTH_SHORT).show();
            }else {
				Toast.makeText(mContext, "P1 Capture First", Toast.LENGTH_SHORT).show();
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//COnvert and resize our image to 400dp for faster uploading our images to DB
	protected Bitmap decodeUri(Uri selectedImage, int REQUIRED_SIZE) {

		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

			// Find the correct scale value. It should be the power of 2.
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE) {
					break;
				}
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//Convert bitmap to bytes
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	private byte[] profileImage(Bitmap b) {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		b.compress(Bitmap.CompressFormat.PNG, 0, bos);
		return bos.toByteArray();

	}


	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setTitle("Really Exit?")
				.setMessage("Are you sure you want to exit?")
				.setNegativeButton(android.R.string.no, null)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				}).create().show();
	}

	private void setting() {
		// TODO Auto-generated method stub
		PopupMenu popup = new PopupMenu(AttendanceActivity.this, ivMenu);
		// Inflating the Popup using xml file
		popup.getMenuInflater().inflate(R.menu.main, popup.getMenu());

		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {

				switch (item.getItemId()) {

					case R.id.item1:
						Intent isetting = new Intent(AttendanceActivity.this, MainActivity.class);
						startActivity(isetting);
						return true;

					case R.id.item2:
						Intent ihelp = new Intent(AttendanceActivity.this, DomainActivity.class);
						startActivity(ihelp);
						return true;

					case R.id.item4:

						Intent ihelp1 = new Intent(AttendanceActivity.this, HelpActivity.class);
						startActivity(ihelp1);
						return true;
				}

				return true;

			}
		});
		popup.show();

	}



	private void DisableCapture() {
		//  Capture_BackGround.setEnabled(false);
		imageView.setEnabled(false);
		//Capture_PreView.setEnabled(false);
		Capture_match.setEnabled(false);
		imageView.setImageBitmap(null);
	}

	private void EnableCapture() {
		// Capture_BackGround.setEnabled(true);
		LeftHandTHumb.setEnabled(true);
		RightHandThumb.setEnabled(true);
		Capture_match.setEnabled(true);
	}


	@Override
	public void ScannerProgressFM220(final boolean DisplayImage, final Bitmap ScanImage, final boolean DisplayText, final String statusMessage) {
		AttendanceActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (DisplayText) {
					textMessage.setText(statusMessage);
					textMessage.invalidate();
				}
				if (DisplayImage) {
					imageView.setImageBitmap(ScanImage);
					imageView.invalidate();
				}
			}
		});
	}

	@Override
	public void ScanCompleteFM220(final fm220_Capture_Result result) {
		AttendanceActivity.this.runOnUiThread(new Runnable() {


			@Override
			public void run() {
				if (FM220SDK.FM220Initialized())
					EnableCapture();
				if (result.getResult()) {
					imageView.setImageBitmap(result.getScanImage());
					f1 = BitMapToString(result.getScanImage());
					if (t1 == null) {
						t1 = result.getISO_Template();

					} else {
						t1 = result.getISO_Template();

					}
					textMessage.setText("Success NFIQ:" + Integer.toString(result.getNFIQ()) + "  SrNo:" + result.getSerialNo());
				} else {
					imageView.setImageBitmap(null);
					textMessage.setText(result.getError());
				}
				imageView.invalidate();
				textMessage.invalidate();
				match();
			}
		});
	}

	public void match(){
		if(!TextUtils.isEmpty(CommonMethod.getPrefsData(AttendanceActivity.this, uid.getText().toString() ,""))) {
			Contact contact = new Gson().fromJson(CommonMethod.getPrefsData(AttendanceActivity.this, uid.getText().toString(),""),Contact.class);
			if (t1 != null) {
				t2 = Base64.decode(contact.get_fp1().getBytes(), Base64.DEFAULT);
				t3 = Base64.decode(contact.get_fp2().getBytes(), Base64.DEFAULT);
				if (FM220SDK.MatchFM220(t1, t2)) {
					//textMessage.setText("Finger matched");
					status_img.setImageDrawable(ContextCompat.getDrawable(AttendanceActivity.this, R.drawable.ok));
					callApi();
				} else if (FM220SDK.MatchFM220(t1, t3)) {
					//textMessage.setText("Finger matched");
					status_img.setImageDrawable(ContextCompat.getDrawable(AttendanceActivity.this, R.drawable.ok));
					callApi();
				} else {
					status_img.setImageDrawable(ContextCompat.getDrawable(AttendanceActivity.this, R.drawable.delete));
					Toast.makeText(activity, "Fingerprint not matched ", Toast.LENGTH_SHORT).show();
					Toast.makeText(activity, "Attendance not Marked ", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	public String BitMapToString(Bitmap bitmap){
		ByteArrayOutputStream baos=new  ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
		byte [] b=baos.toByteArray();
		String temp=Base64.encodeToString(b, Base64.DEFAULT);
		return temp;
	}

	public String getAddress(double latitude,double longitude) {
		Address locationAddress;

		locationAddress = getAddress1(latitude, longitude);

		if (locationAddress != null) {

			String address = locationAddress.getAddressLine(0);
			String address1 = locationAddress.getAddressLine(1);
			String city = locationAddress.getLocality();
			String state = locationAddress.getAdminArea();
			String country = locationAddress.getCountryName();
			String postalCode = locationAddress.getPostalCode();


			String currentLocation;

			if (!TextUtils.isEmpty(address)) {
				currentLocation = address;

				if (!TextUtils.isEmpty(address1))
					currentLocation += "\n" + address1;

				if (!TextUtils.isEmpty(city)) {
					currentLocation += "\n" + city;

					if (!TextUtils.isEmpty(postalCode))
						currentLocation += " - " + postalCode;
				} else {
					if (!TextUtils.isEmpty(postalCode))
						currentLocation += "\n" + postalCode;
				}

				if (!TextUtils.isEmpty(state))
					currentLocation += "\n" + state;

				if (!TextUtils.isEmpty(country))
					currentLocation += "\n" + country;
				return currentLocation;

			} else {
				Toast.makeText(AttendanceActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
				return "";
			}
		}
		Toast.makeText(AttendanceActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
		return "";
	}

	public Address getAddress1(double latitude,double longitude)
	{
		Geocoder geocoder;
		List<Address> addresses;
		geocoder = new Geocoder(AttendanceActivity.this, Locale.getDefault());

		try {
			addresses = geocoder.getFromLocation(latitude,longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
			return addresses.get(0);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}






	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();

		mGoogleApiClient.connect();

		LocationRequest mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(10000);
		mLocationRequest.setFastestInterval(5000);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
				.addLocationRequest(mLocationRequest);

		PendingResult<LocationSettingsResult> result =
				LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

		result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
			@Override
			public void onResult(LocationSettingsResult locationSettingsResult) {

				final Status status = locationSettingsResult.getStatus();

				switch (status.getStatusCode()) {
					case LocationSettingsStatusCodes.SUCCESS:
						// All location settings are satisfied. The client can initialize location requests here
						getLocation();

						if (mLastLocation != null) {
							latitude = mLastLocation.getLatitude();
							longitude = mLastLocation.getLongitude();
							tv_latitude.setText("Latitude: "+latitude+"    Longitude: "+longitude);
							//tv_latitude.setText(getAddress( latitude, longitude));

							//Toast.makeText(AttendanceActivity.this,longitude+" "+latitude,Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(AttendanceActivity.this,"Couldn't get the location. Make sure location is enabled on the device",Toast.LENGTH_LONG).show();
						}
						break;
					case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
						try {
							// Show the dialog by calling startResolutionForResult(),
							// and check the result in onActivityResult().
							status.startResolutionForResult(AttendanceActivity.this, REQUEST_CHECK_SETTINGS);

						} catch (IntentSender.SendIntentException e) {
							// Ignore the error.
						}
						break;
					case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
						break;
				}
			}
		});
	}

	public void callApi(){
//        http://www.websitename.com?empid=1001&Imeino=3234565656766556&datetime=19/12/2017 12:41&lat=27.34546&lng=28.55657
		String URL = CommonMethod.getPrefsData(AttendanceActivity.this, Constants.DOMAIN_NAME, "") + "/iclock/CDATA2.ASPX?";

        String url = URL+"empid="+uid.getText().toString()+"&Imeino="+tel.getDeviceId().toString()+"&datetime="+Utility.getCurrentDate()+"&lat="+String.valueOf(latitude)+"&lng="+String.valueOf(longitude);

         System.out.print(url.replace(" ","%20"));
        VolleyRequestClass volleyRequestClass = new VolleyRequestClass(url.replace(" ","%20"),AttendanceActivity.this, new VolleyRequestClass.GetResponse() {
            @Override
            public void getSuccess() {
                Toast.makeText(AttendanceActivity.this, "Attendance Marked", Toast.LENGTH_SHORT).show();
				Toast.makeText(AttendanceActivity.this,"You are At: "+ getAddress(latitude,longitude), Toast.LENGTH_SHORT).show();
				status_img.setImageDrawable(ContextCompat.getDrawable(AttendanceActivity.this, R.drawable.ok));

            }

            @Override
            public void getError() {
                Toast.makeText(AttendanceActivity.this, "URL is wrong please check it again", Toast.LENGTH_LONG).show();
//				status_img.setImageDrawable(ContextCompat.getDrawable(AttendanceActivity.this, R.drawable.delete));
            }
        });
        volleyRequestClass.request();
    }

	private void getLocation() {


			try
			{
				mLastLocation = LocationServices.FusedLocationApi
						.getLastLocation(mGoogleApiClient);
			}
			catch (SecurityException e)
			{
				e.printStackTrace();
			}



	}



	@Override
	public void ScanMatchFM220(final fm220_Capture_Result _result) {
		AttendanceActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (FM220SDK.FM220Initialized()) EnableCapture();
				if (_result.getResult()) {
					imageView.setImageBitmap(_result.getScanImage());
					textMessage.setText("Finger matched\n" + "Success NFIQ:" + Integer.toString(_result.getNFIQ()));
				} else {
					imageView.setImageBitmap(null);
					textMessage.setText("Finger not matched\n" + _result.getError());
				}
				imageView.invalidate();
				textMessage.invalidate();
			}
		});
	}


	/*private GoogleMap.OnMyLocationChangeListener myLocationChangeListener() {
		return new GoogleMap.OnMyLocationChangeListener() {
			@Override
			public void onMyLocationChange(Location location) {
//               clear map marker everytime location update
				mMap.clear();

//                change button status
				tv_latitude.setVisibility(View.VISIBLE);
				tv_address.setVisibility(View.VISIBLE);
				//btnCapturePicture.setEnabled(true);

//                Latlong object
				LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
				longitude = location.getLongitude();
				latitude = location.getLatitude();

				// Instantiating MarkerOptions class
				MarkerOptions options = new MarkerOptions();

// Setting position for the MarkerOptions
				options.position(loc);

				// Setting title for the MarkerOptions
				options.title("Current Position");

				// Setting snippet for the MarkerOptions
				options.snippet("Latitude:" + latitude + ",Longitude:" + longitude);

				// Adding Marker on the Google Map
				mMap.addMarker(options);
//
				// Creating CameraUpdate object for position
				CameraUpdate updatePosition = CameraUpdateFactory.newLatLng(loc);

				// Creating CameraUpdate object for zoom
				CameraUpdate updateZoom = CameraUpdateFactory.zoomBy(4);

				// Updating the camera position to the user input latitude and longitude
				mMap.moveCamera(updatePosition);

				// Applying zoom to the marker position
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 18.0f));
				location2 = "You are at [" + "Lat: " + latitude + " ; " + "Long: " + longitude + " ]";
				tv_latitude.setText(location2);

				//get current address by invoke an AsyncTask object
				new GetAddressTask(AttendanceActivity.this).execute(String.valueOf(latitude), String.valueOf(longitude));
			}
		};
	}*/



	@Override
	public void PermissionGranted(int request_code) {
//		isPermissionGranted=true;

	}

	@Override
	public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {

	}

	@Override
	public void PermissionDenied(int request_code) {

	}

	@Override
	public void NeverAskAgain(int request_code) {

	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {

	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}
	public void clear(){
		uid.setText("");
		txtUserName.setText("");
		status_img.setImageDrawable(null);
	}

	@Override
	protected void onStart() {
		super.onStart();
		clear();
	}

	/*public void callBackDataFromAsyncTask(String address) {
		tv_address.setText(address);

	}*/

}
