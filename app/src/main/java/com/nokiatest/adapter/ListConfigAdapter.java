package com.nokiatest.adapter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nokiatest.R;
import com.nokiatest.model.ListConfig;

import java.util.List;


public class ListConfigAdapter extends BaseAdapter {

    private AppCompatActivity activity;
    private List<ListConfig> listConfig;
    private LayoutInflater inflater = null;

    public ListConfigAdapter(AppCompatActivity activity, List<ListConfig> listConfig) {
        this.activity = activity;
        this.listConfig = listConfig;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listConfig.size();
    }

    @Override
    public Object getItem(int position) {
        return listConfig.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_app_list, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtAppName.setText(listConfig.get(position).getAcsName());

        return convertView;
    }


    class ViewHolder {
        TextView txtAppName;
        public ViewHolder(View vi) {
            txtAppName = (TextView)vi.findViewById(R.id.txt_app_name);

        }
    }
}
