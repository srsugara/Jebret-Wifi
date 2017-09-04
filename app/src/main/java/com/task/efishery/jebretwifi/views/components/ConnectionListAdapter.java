package com.task.efishery.jebretwifi.views.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.task.efishery.jebretwifi.R;
import com.task.efishery.jebretwifi.models.Connection;
import com.task.efishery.jebretwifi.views.activities.MainActivity;

import java.util.List;

/**
 * Created by syauqi on 04/09/17.
 */

public class ConnectionListAdapter extends BaseAdapter{
    Context context;
    private List<Connection> connections;

    private static LayoutInflater inflater=null;
    public ConnectionListAdapter(Context context, List<Connection> connections) {
        // TODO Auto-generated constructor stub
        this.connections=connections;
        this.context=context;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return connections.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return connections.get(position);
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
        TextView connected;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_item, null);

        Connection connection = (Connection) getItem(position);

        holder.signalView = (SignalView) rowView.findViewById(R.id.signal_view);
        holder.signalView.setLevel(connection.getStrength());

        holder.tvLabel=(TextView) rowView.findViewById(R.id.label);
        holder.tvLabel.setText(connection.getName());

        holder.connected = (TextView) rowView.findViewById(R.id.tv_connected);
        if(connection.getIsConnected() == true ) {
            holder.connected.setText("connected");
        }else {
            holder.connected.setText("");
        }

        return rowView;
    }
}
