package com.example.iot2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.iot2.dataclasses.Sensor;

import java.util.List;


public class Adapter extends BaseAdapter {
    Context context;
    List<Sensor> sensors;

    public Adapter(List<Sensor> sensors, Context context) {
        this.context = context;
        this.sensors = sensors;
    }

    @Override
    public int getCount() {
        return sensors.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Sensor s = sensors.get(i);

        View view1 = inflater.inflate(R.layout.sensor_without_comparative_value, viewGroup, false);
        if (s.getHasComparative()) {
            view1 = inflater.inflate(R.layout.sensor_with_comparative_value, viewGroup, false);
            TextView comparativeName = view1.findViewById(R.id.comparative_name);
            TextView comparativeValue = view1.findViewById(R.id.comparative_value);

            comparativeValue.setText(String.format(s.getFormat(), s.getComparative()));
            comparativeName.setText(s.getComparativeName());
        }

        TextView value = view1.findViewById(R.id.value);
        TextView unit = view1.findViewById(R.id.unit);
        TextView label = view1.findViewById(R.id.label);

        value.setText(String.format(s.getFormat(), s.getValue()));
        unit.setText(s.getUnit());
        label.setText(s.getName());

        return view1;
    }
}
