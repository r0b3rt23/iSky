package com.example.edgepoint.checqr;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingsCustomAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final Integer[] imgid;

    public SettingsCustomAdapter(Activity context, String[] itemname, Integer[] imgid) {
        super(context, R.layout.network_list, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.network_list, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        txtTitle.setText(itemname[position]);
        imageView.setImageResource(imgid[position]);

        DatabaseAccess databaseUserAccess = DatabaseAccess.getInstance(context);
        databaseUserAccess.open();
        String UserAccess = databaseUserAccess.getUserAccess();
        databaseUserAccess.close();

        if (!UserAccess.equals("isky")){
            if (itemname[position].equals("Download Data")) {
                rowView.setEnabled(false);
                rowView.setOnClickListener(null);
                txtTitle.setTextColor((Color.parseColor("#C6C2B6")));
                android.graphics.drawable.Drawable mDrawable = android.support.v4.content.ContextCompat.getDrawable(context, imgid[position]);
                mDrawable.setColorFilter(new android.graphics.PorterDuffColorFilter(0xffC6C2B6, android.graphics.PorterDuff.Mode.SRC_IN));
            }
        }

        return rowView;

    }
}
