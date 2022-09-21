package com.example.refrigerator_management_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CalendarItemAdapter extends BaseAdapter {
    List<Item> items = null;
    Context context;

    public CalendarItemAdapter(Context context, List<Item> items) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.listview_calendar_item, parent, false);

        TextView name = view.findViewById(R.id.calendarName);
        TextView remainDays = view.findViewById(R.id.calendarRemainDays);

        Item item = items.get(position);
        name.setText(item.getName());
        remainDays.setText(Integer.toString(item.getRemainDays()));

        return view;
    }
}
