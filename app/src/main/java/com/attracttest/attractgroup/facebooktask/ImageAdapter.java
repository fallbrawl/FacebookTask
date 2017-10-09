package com.attracttest.attractgroup.facebooktask;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by nexus on 07.10.2017.
 */
public class ImageAdapter extends ArrayAdapter<Uri> {
    private LayoutInflater mInflater;


    public ImageAdapter(Context context, ArrayList<Uri> objects) {
        super(context, 0, objects);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.imag, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.imgitem = (ImageView) convertView.findViewById(R.id.imaq);

            convertView.setTag(holder);
        } else {

            // Get the ViewHolder back to get fast access to the TextView
            holder = (ViewHolder) convertView.getTag();
        }
        Uri uri = getItem(position);

        // Bind the data efficiently with the holder.
        Picasso.with(getContext()).load(uri).fit().into(holder.imgitem);
        return convertView;
    }
    static class ViewHolder {
        ImageView imgitem;
    }
}
