package com.example.edgepoint.checqr;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Act_Scan_Adapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] activityname;
    private final String[] scan;
    private final Integer[] imgidAS;

    public Act_Scan_Adapter(Activity context, String[] activityname, String[] scan,  Integer[] imgidAS) {
        super(context, R.layout.actscan_list, activityname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.activityname=activityname;
        this.scan=scan;
        this.imgidAS=imgidAS;
    }
    
    //add comment

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.actscan_list, null,true);

        TextView activityTitle = (TextView) rowView.findViewById(R.id.activity);
        TextView scanTitle = (TextView) rowView.findViewById(R.id.active);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        activityTitle.setText(activityname[position]);
        imageView.setImageResource(imgidAS[position]);
        if (position < 2){
            scanTitle.setText(scan[position]);
        }else {
            scanTitle.setText("List of all finished activity");
        }
        return rowView;

    }
}
