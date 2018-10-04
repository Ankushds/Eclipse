package com.mantra.fm220;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.access.aadharapp220.R;


public class CamActivity extends Activity {

	private DisplayMetrics metrics;
	private RelativeLayout flayout;
	private Button btn;
	public static Bitmap bitmap;
	private String url;
	private Button button;
	final int TAKE_PICTURE = 1;
	final int ACTIVITY_SELECT_IMAGE = 2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cam_layout);

		button = (Button)findViewById(R.id.button1);

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {


			}
		});

	}

	public void selectImage()
	{
		final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
		AlertDialog.Builder builder = new AlertDialog.Builder(CamActivity.this);
		builder.setTitle("Add Photo!");
		builder.setItems(options,new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(options[which].equals("Take Photo"))
				{
					Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(cameraIntent, TAKE_PICTURE);
				}
				else if(options[which].equals("Choose from Gallery"))
				{
					Intent intent=new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
				}
				else if(options[which].equals("Cancel"))
				{
					dialog.dismiss();
				}

			}
		});
		builder.show();
	}

	private byte[] profileImage(Bitmap b){

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		b.compress(Bitmap.CompressFormat.PNG, 0, bos);
		return bos.toByteArray();

	}

	public void onActivityResult(int requestcode,int resultcode,Intent intent)
	{
		super.onActivityResult(requestcode, resultcode, intent);
		if(resultcode==RESULT_OK)
		{
			/*if(requestcode==TAKE_PICTURE)
			{
				Bitmap photo = (Bitmap)intent.getExtras().get("data");
				//Drawable drawable=new BitmapDrawable(photo);
				flayout.(photo);

			}
			else if(requestcode==ACTIVITY_SELECT_IMAGE)
			{
				Uri selectedImage = intent.getData();
				//String[] filePath = { MediaStore.Images.Media.DATA };
                *//*Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));*//*
				bp=decodeUri(selectedImage, 400);
				flayout.setImageBitmap(bp);


			}*/
		}
	}


}
