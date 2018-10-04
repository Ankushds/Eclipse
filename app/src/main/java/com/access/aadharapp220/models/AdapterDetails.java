package com.access.aadharapp220.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.access.aadharapp220.R;

import java.util.ArrayList;

/**
 * Created by SonuShaikh on 14/10/2017.
 */

public class AdapterDetails extends ArrayAdapter<Employee> {
    Context context;
    ArrayList<Employee> mcontact;
    SQLiteDatabase mDatabase;

    public AdapterDetails(Context context, ArrayList<Employee> contact){
        super(context, R.layout.report_emp_details, contact);
        this.context=context;
        this.mcontact=contact;
    }

    public  class  Holder{
        TextView nameFV;
        TextView uidFV;
        TextView finger1;
        TextView finger2;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        final Employee data = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        Holder viewHolder; // view lookup cache stored in tag


        if (convertView == null) {


            viewHolder = new Holder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.report_emp_details, parent, false);

            viewHolder.uidFV = (TextView) convertView.findViewById(R.id.txtViewer);
            //viewHolder.finger1 = (ImageView) convertView.findViewById(R.id.imageView2);
            //viewHolder.finger2 = (ImageView) convertView.findViewById(R.id.);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Holder) convertView.getTag();
        }

        //viewHolder.uidFV.setText(""+data.get_uid().toString().trim());
        //viewHolder.nameFV.setText(""+data.get_fname().toString().trim());

      /*  viewHolder.finger1.setImageBitmap(convertToBitmap(data.getFinger1()));
        viewHolder.finger2.setImageBitmap(convertToBitmap(data.getFinger2()));*/

        // Return the completed view to render on screen
        return convertView;

    }


    //get bitmap image from byte array

    private Bitmap convertToBitmap(byte[] b){

        return BitmapFactory.decodeByteArray(b, 0, b.length);

    }

}
