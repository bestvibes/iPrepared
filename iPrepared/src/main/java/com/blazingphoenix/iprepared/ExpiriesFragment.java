package com.blazingphoenix.iprepared;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaibhav on 11/11/13.
 */
public class ExpiriesFragment extends Fragment {

    protected SQLiteHelper db;
    List<ListItemClass> list;
    MyAdapter adapt;
    Context context;
    View rootView;
    ListView listView;

    onDataChangeReceiver onDataChangeReceiver;

    public class ViewHolder {
        TextView title;
        TextView desc;
    }

    public ExpiriesFragment() {
    }

    public void updateList() {
        list.clear();
        list.addAll(db.getExpiringItems());
        adapt.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(onDataChangeReceiver != null) {
            context.unregisterReceiver(onDataChangeReceiver);
            onDataChangeReceiver = null;
        }
        onDataChangeReceiver = new onDataChangeReceiver();
        context.registerReceiver(onDataChangeReceiver, new IntentFilter(TaskList.INTENT_UPDATE));
        updateList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(onDataChangeReceiver != null){
            context.unregisterReceiver(onDataChangeReceiver);
            onDataChangeReceiver = null;
        }
        if(db != null) {
        db.closeDB();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(onDataChangeReceiver != null) {
            context.unregisterReceiver(onDataChangeReceiver);
            onDataChangeReceiver = null;
        }
    }

    private class onDataChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateList();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        context = getActivity();

        onDataChangeReceiver = new onDataChangeReceiver();
        context.registerReceiver(onDataChangeReceiver, new IntentFilter(TaskList.INTENT_UPDATE));
        rootView = inflater.inflate(R.layout.checklist_fragment, container, false);

        db = new SQLiteHelper(context);
        list = db.getExpiringItems();
        adapt = new MyAdapter(context, R.layout.expiries_list_item, list);

        this.listView = (ListView) rootView.findViewById(R.id.checkListView);
        listView.setAdapter(adapt);

        TextView emptyText = (TextView) rootView.findViewById(R.id.emptyExpiries);
        listView.setEmptyView(emptyText);

        return rootView;
    }

    private class MyAdapter extends ArrayAdapter<ListItemClass> {

        Context context;
        List<ListItemClass> taskList = new ArrayList<ListItemClass>();
        int layoutResourceId;

        public MyAdapter(Context context, int layoutResourceId,
                         List<ListItemClass> objects) {
            super(context, layoutResourceId, objects);
            this.layoutResourceId = layoutResourceId;
            this.taskList = objects;
            this.context = context;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.expiries_list_item,
                        parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.expiries_item_title);
                viewHolder.desc = (TextView) convertView.findViewById(R.id.expiries_item_description);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ListItemClass current = taskList.get(position);
            viewHolder.title.setText(current.getName());
            viewHolder.desc.setText(current.getExpiry() + " - " + current.getList());
            return convertView;
        }

    }
}