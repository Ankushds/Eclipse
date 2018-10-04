package com.access.aadharapp220;


/**
 * project FM220_Android_SDK
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aadharmachine.rssolution.AttendanceActivity;
import com.aadharmachine.rssolution.DomainActivity;
import com.aadharmachine.rssolution.HelpActivity;
import com.aadharmachine.rssolution.LoginActivity;
import com.aadharmachine.rssolution.UpdateActivity;
import com.access.aadharapp220.models.Contact;
import com.access.aadharapp220.models.DatabaseHandler;
import com.access.aadharapp220.models.dataAdapter;
import com.access.aadharapp220.utility.RealmHelper;
import com.access.aadharapp220.utility.Utils;
import com.acpl.access_computech_fm220_sdk.FM220_Scanner_Interface;
import com.acpl.access_computech_fm220_sdk.acpl_FM220_SDK;
import com.acpl.access_computech_fm220_sdk.fm220_Capture_Result;
import com.acpl.access_computech_fm220_sdk.fm220_Init_Result;
import com.google.gson.Gson;
import com.mantra.fm220.CommonMethod;
import com.mantra.fm220.Constants;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

import static com.mantra.fm220.CamActivity.bitmap;

public class MainActivity extends AppCompatActivity implements FM220_Scanner_Interface{

    public final static String KEY_EXTRA_CONTACT_ID = "KEY_EXTRA_CONTACT_ID";

    private EditText uid, name;
    private String HolderUid, HolderName;
    private ImageView pic;
    private DatabaseHandler db;


    final int TAKE_PICTURE = 1;
    final int ACTIVITY_SELECT_IMAGE = 2;
    private acpl_FM220_SDK FM220SDK;
    private Button Capture_BackGround,Capture_match,btnSave, btn_edit, btn_delete;
    private ImageView LeftHandTHumb, RightHandThumb,ivMenu;
    private TextView textMessage, textM, btn_find;
    private ImageView imageView;


    public enum Click {
        Right,
        Left
    }

    private Click click = null;

    String finger1, finger2;
    private Context mContext;
    private int count=0;

    /***************************************************
     * if you are use telecom device enter "Telecom_Device_Key" as your provided key otherwise send "" ;
     */
    private static final String Telecom_Device_Key = "";
    private byte[] t1,t2;

    private Bitmap b1,b2;

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
                if ((pid == 0x8225 || pid == 0x8220)  && (vid == 0x0bca)) {
                    FM220SDK.stopCaptureFM220();
                    FM220SDK.unInitFM220();
                    usb_Dev=null;
                    textMessage.setText("FM220 disconnected");
                    DisableCapture();
                    DisableCapture1();
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
                            if ((pid == 0x8225 || pid == 0x8220)  && (vid == 0x0bca)) {
                                fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
                                if (res.getResult()) {
                                    textMessage.setText("FM220 ready. "+res.getSerialNo());
                                    EnableCapture();
                                }
                                else {
                                    textMessage.setText("Error :-"+res.getError());
                                    DisableCapture();
                                    DisableCapture1();
                                }
                            }
                        }
                    } else {
                        textMessage.setText("Employee Blocked USB connection");
                        textMessage.setText("FM220 ready");
                        DisableCapture();
                        DisableCapture1();
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
                        if ((pid == 0x8225)  && (vid == 0x0bca) && !FM220SDK.FM220isTelecom()) {
                            Toast.makeText(context,"Wrong device type application restart required!",Toast.LENGTH_LONG).show();
                            finish();
                        }
                        if ((pid == 0x8220)  && (vid == 0x0bca)&& FM220SDK.FM220isTelecom()) {
                            Toast.makeText(context,"Wrong device type application restart required!",Toast.LENGTH_LONG).show();
                            finish();
                        }

                        if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
                            if (!manager.hasPermission(device)) {
                                textMessage.setText("FM220 requesting permission");
                                manager.requestPermission(device, mPermissionIntent);
                            } else {
                                fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
                                if (res.getResult()) {
                                    textMessage.setText("FM220 ready. "+res.getSerialNo());
                                    EnableCapture();
                                }
                                else {
                                    textMessage.setText("Error :-"+res.getError());
                                    DisableCapture();
                                    DisableCapture1();
                                }
                            }
                        }
                    }
                }
            }
        }
    };



    @Override
    protected void onNewIntent(Intent intent) {
        if (getIntent() != null) {
            return;
        }
        super.onNewIntent(intent);
        setIntent(intent);
        try {
            if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED) && usb_Dev==null) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    // call method to set up device communication & Check pid
                    int pid, vid;
                    pid = device.getProductId();
                    vid = device.getVendorId();
                    if ((pid == 0x8225)  && (vid == 0x0bca)) {
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
//            FM220SDK.unInitFM220();
        }  catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
    //endregion



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this);
        textMessage = (TextView) findViewById(R.id.textMessage);
        textM = (TextView) findViewById(R.id.textM);
        //Capture_PreView = (Button) findViewById(R.id.button2);
        LeftHandTHumb = (ImageView) findViewById(R.id.button);
        RightHandThumb = (ImageView) findViewById(R.id.button2);
        btnSave = (Button) findViewById(R.id.btn_save);
        ivMenu = (ImageView) findViewById(R.id.iv_menu);
        btn_find = (TextView) findViewById(R.id.btn_find);
        btn_edit = (Button)findViewById(R.id.btn_edit);
        btn_edit.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.color_b2b2b2));
        btn_edit.setEnabled(false);
        btn_delete = (Button)findViewById(R.id.btn_delete);
        btn_delete.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.color_b2b2b2));
        btn_delete.setEnabled(false);
        btnSave.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.frame_header));




        //Instantiate database handler
        db=new DatabaseHandler(this);

        //pic= (ImageView) findViewById(R.id.pic);
        uid = (EditText) findViewById(R.id.employee_uid);
        name = (EditText) findViewById(R.id.employee_name);


        //loadDeptSpinnerData();
        //loadDesSpinnerData();

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
        for ( UsbDevice mdevice : manager.getDeviceList().values()) {
            int pid, vid;
            pid = mdevice.getProductId();
            vid = mdevice.getVendorId();
            boolean devType;
            if ((pid == 0x8225) && (vid == 0x0bca)) {
                FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,true);
                devType=true;
            }
            else if ((pid == 0x8220) && (vid == 0x0bca)) {
                FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,false);
                devType=false;
            } else {
                FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,oldDevType);
                devType=oldDevType;
            }
            if (oldDevType != devType) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("FM220type", devType);
                editor.apply();
            }
            if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
                device  = mdevice;
                if (!manager.hasPermission(device)) {
                    textMessage.setText("FM220 requesting permission");
                    manager.requestPermission(device, mPermissionIntent);
                } else {
                    Intent intent = this.getIntent();
                    /*if (intent != null) {
                        if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                            finishAffinity();
                        }
                    }*/
                    fm220_Init_Result res =  FM220SDK.InitScannerFM220(manager,device,Telecom_Device_Key);
                    if (res.getResult()) {
                        textMessage.setText("FM220 ready. "+res.getSerialNo());
                        EnableCapture();
                    }
                    else {
                        textMessage.setText("Error :-"+res.getError());
                        DisableCapture();
                        DisableCapture1();
                    }
                }
                break;
            }
        }
        if (device == null) {
            textMessage.setText("Pl connect FM220");
            FM220SDK = new acpl_FM220_SDK(getApplicationContext(),this,oldDevType);
        }



        LeftHandTHumb.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = Click.Left;
                DisableCapture();
                FM220SDK.CaptureFM220(2,true,false);
            }
        });

        RightHandThumb.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = Click.Right;
                DisableCapture1();
                FM220SDK.CaptureFM220(2,true,true);

            }
        });

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting();
            }
        });

        btn_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(uid.getText().toString())) {
                    if (!TextUtils.isEmpty(CommonMethod.getPrefsData(MainActivity.this, uid.getText().toString(), ""))) {
                        btn_edit.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.frame_header));
                        btn_delete.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.frame_header));
                        btnSave.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.color_b2b2b2));
                        btnSave.setEnabled(false);
                        btn_delete.setEnabled(true);
                        btn_edit.setEnabled(true);
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Contact contact1 = new Gson().fromJson(CommonMethod.getPrefsData(MainActivity.this, uid.getText().toString(),""),Contact.class);
                                t1 = Base64.decode(contact1.get_fp1().getBytes(), Base64.DEFAULT);
                                t2 = Base64.decode(contact1.get_fp2().getBytes(), Base64.DEFAULT);
                                final Contact contact = db.getData(uid.getText().toString());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (contact != null && !TextUtils.isEmpty(contact.get_uid())) {
                                            LeftHandTHumb.setImageBitmap(stringToBitMap(contact.get_fp1()));
                                            RightHandThumb.setImageBitmap(stringToBitMap(contact.get_fp2()));
                                            name.setText( contact.get_fname());

                                        } else {
                                            Log.e("AttendanceActivity -->", "No Record found");
                                        }
                                    }
                                });
                            }
                        }).start();

                    }
                }
            }
        });
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uid.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter uid", Toast.LENGTH_SHORT).show();
                }else if (name.getText().toString().trim().isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter name", Toast.LENGTH_SHORT).show();
                }else if (RightHandThumb.getDrawable() == null) {
                    Toast.makeText(MainActivity.this, "Please Enroll finger", Toast.LENGTH_SHORT).show();
                }else if (LeftHandTHumb.getDrawable() == null) {
                    Toast.makeText(MainActivity.this, "Please Enroll finger", Toast.LENGTH_SHORT).show();
                } else{

                    final String s = name.getText().toString();
                    final String s1 = uid.getText().toString();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Contact contact = new Contact();
                            contact.set_uid(s1);
                            contact.setFName(s);
                            contact.set_fp1(Base64.encodeToString(t1, Base64.DEFAULT));
                            contact.set_fp2(Base64.encodeToString(t2, Base64.DEFAULT));

                            Gson gson = new Gson();
                            String j = gson.toJson(contact);

                            final Bitmap bitmap1 = ((BitmapDrawable)LeftHandTHumb.getDrawable()).getBitmap();
                            final Bitmap bitmap2 = ((BitmapDrawable)RightHandThumb.getDrawable()).getBitmap();

                            if(!TextUtils.isEmpty(CommonMethod.getPrefsData(MainActivity.this, s1 ,""))) {
                                final boolean isInserted = db.insertData(s1, s, BitMapToString(bitmap1), BitMapToString(bitmap2));
                                CommonMethod.setPrefsData(MainActivity.this, s1, j);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "UPDATED", Toast.LENGTH_SHORT).show();
                                        cleardata();
                                    }
                                });

                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "Please enter details", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }).start();
                }
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Alert")
                        .setMessage("Are you sure you want to delete?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                delete();
                            }
                        }).create().show();
            }
        });
    }

    public void delete(){

        if(!TextUtils.isEmpty(CommonMethod.getPrefsData(MainActivity.this, uid.getText().toString() ,""))) {
             db.delete(uid.getText().toString());
            CommonMethod.setPrefsData(MainActivity.this, uid.getText().toString(), "");
            Toast.makeText(MainActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
            cleardata();

        }

    }

    private Bitmap stringToBitMap(String encodedString) {
        try {
            t2 = Base64.decode(encodedString, Base64.NO_WRAP);
            Bitmap bitmap1 = BitmapFactory.decodeByteArray(t2, 0, t2.length);
            return bitmap1;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private void setting() {
        // TODO Auto-generated method stub
        PopupMenu popup = new PopupMenu(MainActivity.this, ivMenu);
        // Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.main1, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.item1:
                        //Intent isetting = new Intent(MainActivity.this, MainActivity.class);
                        //startActivity(isetting);
                        finish();
                        return true;

                    case R.id.item2:
                        Intent ihelp = new Intent(MainActivity.this, DomainActivity.class);
                        startActivity(ihelp);
                        return true;

                    case R.id.item4:

                        Intent ihelp1 = new Intent(MainActivity.this, HelpActivity.class);
                        startActivity(ihelp1);
                        return true;
                }

                return true;

            }
        });
        popup.show();

    }



    public void buttonClicked(View v){
        int id=v.getId();
        HolderUid = uid.getText().toString().trim();
        HolderName = name.getText().toString().trim();

        switch(id){

            case R.id.btn_save:
                if (uid.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter uid", Toast.LENGTH_SHORT).show();
                }if (name.getText().toString().trim().isEmpty()){
                Toast.makeText(MainActivity.this, "Please enter name", Toast.LENGTH_SHORT).show();
            }if (RightHandThumb.getDrawable() == null) {
                Toast.makeText(MainActivity.this, "Please Enroll finger", Toast.LENGTH_SHORT).show();
            } else{

                final String s = name.getText().toString();
                final String s1 = uid.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        /*String img1 =  BitMapToString(b1);
                        String img2 =  BitMapToString(b2);*/
//                        final boolean isInserted = db.insertData(s1, s,img1 ,img2 );

                        Contact contact = new Contact();
                        contact.set_uid(s1);
                        contact.setFName(s);
                        contact.set_fp1(Base64.encodeToString(t1, Base64.DEFAULT));
                        contact.set_fp2(Base64.encodeToString(t2, Base64.DEFAULT));

                        Gson gson = new Gson();
                        String j = gson.toJson(contact);

                        final Bitmap bitmap1 = ((BitmapDrawable)LeftHandTHumb.getDrawable()).getBitmap();
                        final Bitmap bitmap2 = ((BitmapDrawable)RightHandThumb.getDrawable()).getBitmap();

                        if(TextUtils.isEmpty(CommonMethod.getPrefsData(MainActivity.this, s1 ,""))) {
                            final boolean isInserted = db.insertData(s1, s, BitMapToString(bitmap1), BitMapToString(bitmap2));
                            CommonMethod.setPrefsData(MainActivity.this, s1, j);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "INSERTED", Toast.LENGTH_SHORT).show();
                                    cleardata();
                                }
                            });

                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Uid is already registered", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }



                        /*CommonMethod.setPrefsData(MainActivity.this, Constants.PREF_IMAGE_ONE,Base64.encodeToString(t1, 0) );
                        CommonMethod.setPrefsData(MainActivity.this, Constants.PREF_IMAGE_TWO,Base64.encodeToString(t2, 0));*/

                        /*runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isInserted == true) {
                                    Toast.makeText(getBaseContext(), "INSERTED", Toast.LENGTH_SHORT).show();
                                    cleardata();
                                }else
                                    Toast.makeText(getBaseContext(), "not inserted", Toast.LENGTH_SHORT).show();
                            }
                        });*/
                    }
                }).start();




            }
                break;
        }


    }




    //COnvert and resize our image to 400dp for faster uploading our images to DB
    protected Bitmap decodeUri(Uri selectedImage, int REQUIRED_SIZE) {

        try {

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

            // The new size we want to scale to
            // final int REQUIRED_SIZE =  size;

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
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public void cleardata() {
        uid.setText("");
        name.setText("");
        LeftHandTHumb.setImageDrawable(null);
        RightHandThumb.setImageDrawable(null);
        btn_edit.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.color_b2b2b2));
        btn_edit.setEnabled(false);
        btn_delete.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.color_b2b2b2));
        btn_delete.setEnabled(false);
        btnSave.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.frame_header));
        btnSave.setEnabled(true);
    }


    private void DisableCapture() {
        //  Capture_BackGround.setEnabled(false);
        LeftHandTHumb.setEnabled(false);
        //Capture_PreView.setEnabled(false);
        //Capture_match.setEnabled(false);
        LeftHandTHumb.setImageBitmap(null);
    }
    private void DisableCapture1() {
        //  Capture_BackGround.setEnabled(false);
        RightHandThumb.setEnabled(false);
        RightHandThumb.setImageBitmap(null);
    }
    private void EnableCapture() {
        // Capture_BackGround.setEnabled(true);
        LeftHandTHumb.setEnabled(true);
        RightHandThumb.setEnabled(true);
        //Capture_match.setEnabled(true);
    }
    private void FunctionBase64() {
        try {
            String t1base64, t2base64;
            if (t1 != null && t2 != null) {
                t1base64 = Base64.encodeToString(t1, Base64.NO_WRAP);
                t2base64 = Base64.encodeToString(t2, Base64.NO_WRAP);
                if (FM220SDK.MatchFM220String(t1base64, t2base64)) {
                    Toast.makeText(getBaseContext(), "Finger matched", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getBaseContext(), "Finger not matched", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ScannerProgressFM220(final boolean DisplayImage,final Bitmap ScanImage,final boolean DisplayText,final String statusMessage) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (DisplayText) {
                    textMessage.setText(statusMessage);
                    textMessage.invalidate();
                }
                if (DisplayImage) {
                    RightHandThumb.setImageBitmap(ScanImage);
                    RightHandThumb.invalidate();
                }
            }
        });
    }

    /*public void ScanCompFM220(final fm220_Capture_Result results){
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });

    }*/

    @Override
    public void ScanCompleteFM220(final fm220_Capture_Result result) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (FM220SDK.FM220Initialized())  EnableCapture();
                if (result.getResult()) {
                    if(click == Click.Left) {
                        t1 = result.getISO_Template();
                        b1 = result.getScanImage();
                        LeftHandTHumb.setImageBitmap(result.getScanImage());
                    }else {
                        t2 = result.getISO_Template();
                        b2 = result.getScanImage();
                        RightHandThumb.setImageBitmap(result.getScanImage());
                    }
                    /*if (t1 == null) {
                        t1 = result.getISO_Template();
                        b1 = result.getScanImage();
                        //finger1 = new String(t1);
                        //textM.setText("Finger1"+t1);
                    } else {
                        t2 = result.getISO_Template();
                        b2 = result.getScanImage();
                        //finger2 = new String(t2);
                        //textM.setText("Finger2"+t2);
                    }*/
                    textMessage.setText("Success NFIQ:"+Integer.toString(result.getNFIQ())+"  SrNo:"+result.getSerialNo());


                    //Toast.makeText(mContext, "Image Data in ISO: ", Toast.LENGTH_SHORT).show();
                } else {
                    LeftHandTHumb.setImageBitmap(null);
                    textMessage.setText(result.getError());
                }
                LeftHandTHumb.invalidate();
                textMessage.invalidate();
            }
        });
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    @Override
    public void ScanMatchFM220(final fm220_Capture_Result _result) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (FM220SDK.FM220Initialized()) EnableCapture();
                if (_result.getResult()) {
                    LeftHandTHumb.setImageBitmap(_result.getScanImage());
                    textMessage.setText("Finger matched\n" + "Success NFIQ:" + Integer.toString(_result.getNFIQ()));
                } else {
                    LeftHandTHumb.setImageBitmap(null);
                    textMessage.setText("Finger not matched\n" + _result.getError());
                }
                RightHandThumb.invalidate();
                textMessage.invalidate();
            }
        });
    }






}
