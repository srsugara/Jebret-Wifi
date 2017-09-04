package com.task.efishery.jebretwifi.views.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.task.efishery.jebretwifi.R;
import com.task.efishery.jebretwifi.views.activities.MainActivity;

/**
 * Created by syauqi on 04/09/17.
 */

public class CustomListAdapter extends BaseAdapter{
    String [] result;
    int [] strength;
    Context context;

    private static LayoutInflater inflater=null;
    public CustomListAdapter(MainActivity mainActivity, String[] prgmNameList, int[] strengths) {
        // TODO Auto-generated constructor stub
        result=prgmNameList;
        context=mainActivity;
        strength=strengths;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tvLabel;
        SignalView signalView;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_item, null);

        holder.signalView = (SignalView) rowView.findViewById(R.id.signal_view);
        holder.signalView.setLevel(strength[position]);

        holder.tvLabel=(TextView) rowView.findViewById(R.id.label);
        holder.tvLabel.setText(result[position]);

        return rowView;
    }
}
