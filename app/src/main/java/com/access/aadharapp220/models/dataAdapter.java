package com.access.aadharapp220.models;

import android.content.Context;
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
 * Visit website http://www.whats-online.info
 * **/

public class dataAdapter extends ArrayAdapter<Contact> {

    Context context;
    ArrayList<Contact> mcontact;


    public dataAdapter(Context context, ArrayList<Contact> contact){
        super(context, R.layout.report_list_item, contact);
        this.context=context;
        this.mcontact=contact;
    }

    public  class  Holder{
        TextView nameFV;
        TextView uidFV;
        TextView deptFV;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        Contact data = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        Holder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {


            viewHolder = new Holder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.report_list_item, parent, false);

            viewHolder.uidFV = (TextView) convertView.findViewById(R.id.txtViewer);
            viewHolder.nameFV = (TextView) convertView.findViewById(R.id.txtViewer2);
            viewHolder.deptFV = (TextView) convertView.findViewById(R.id.txtViewer3);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Holder) convertView.getTag();
        }


        viewHolder.uidFV.setText(""+data.get_uid().toString().trim());
        viewHolder.nameFV.setText(""+data.get_fname().toString().trim());

        // Return the completed view to render on screen
        return convertView;
    }

    //get bitmap image from byte array

    private Bitmap convertToBitmap(byte[] b){

        return BitmapFactory.decodeByteArray(b, 0, b.length);

    }

}

