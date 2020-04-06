package com.btitsolutions.staygrateful.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.btitsolutions.staygrateful.Models.GratitudeModel;
import com.btitsolutions.staygrateful.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bereket on 5/12/2017.
 */

public class GratitudeAdapter extends BaseAdapter {

    private Activity context_1;

    private List<GratitudeModel> gratitudeModels;

    public GratitudeAdapter(Activity context,
                               List<GratitudeModel> gratitudeModels) {
        context_1 = context;
        this.gratitudeModels = gratitudeModels;
    }

    @Override
    public int getCount() {
        return gratitudeModels.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView lblContent, lblCreatedDate;

        if (convertView == null) {
            convertView = LayoutInflater.from(context_1).inflate(
                    R.layout.gratitude_list, null);
            lblContent = (TextView) convertView.findViewById(R.id.lblContent);
            lblContent.setText(" " + gratitudeModels.get(position).getContent());

            lblCreatedDate = (TextView) convertView.findViewById(R.id.lblCreatedDate);
            lblCreatedDate.setText(" " + gratitudeModels.get(position).getCreated_date());
        }
        else{
            lblContent = (TextView) convertView.findViewById(R.id.lblContent);
            lblContent.setText(" " + gratitudeModels.get(position).getContent());

            lblCreatedDate = (TextView) convertView.findViewById(R.id.lblCreatedDate);
            lblCreatedDate.setText(" " + gratitudeModels.get(position).getCreated_date());
        }

        return convertView;
    }
}