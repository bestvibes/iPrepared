package com.blazingphoenix.iprepared;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaibhav on 11/11/13.
 */
public class TaskList extends Fragment {

    private String TABLE;

    public static final String EXTRA_TABLE = "tableExtra";
    public static final String INTENT_UPDATE = "com.blazingPhoenix.preperation.TaskList.updateData";

    private final String TABLE_TASKS = "tasks";
    private final String TABLE_HOME = "home";
    private final String TABLE_CAR = "car";
    private final String TABLE_WORK = "work";

    private final String[] carValues = new String[]{"Nylon tote or day pack", "Nonperishable food", "Manual can opener", "Transistor radio, flashlight and extra batteries", "First aid kit", "Gloves", "Blanket or sleeping bags", "Sealable plastic bags", "Moist towelettes", "Small tool kit", "Matches and lighter", "Walking shoes and extra socks", "Change of clothes", "Cash (small bills and coins)", "Local street map and compass"};
    private final String[] workValues = new String[]{"Dry food, such as candy bars, dried fruit, jerky, and crackers", "Water or orange juice", "Tennis shoes or walking shoes", "First aid kit", "Flashlight and portable radio with extra batteries", "Matches", "Small and large plastic bags", "Toiletries"};
    private final String[] homeValues = new String[]{"Water (One gallon per person per day)", "Nonperishable Food", "Flashlight", "Battery-Powered or handcrank radio", "Extra Batteries", "First Aid Kit", "Medications (7 Day Supply) and medical items", "Multipurpose Tool", "Sanitation and personal hygiene items", "Copies of personal documents", "Cell Phone with Chargers", "Family and emergency contact information", "Extra Cash", "Emergency blanket", "Map(s) of the area"};

    protected SQLiteHelper db;
    List<ListItemClass> list;
    MyAdapter adapt;
    Context context;
    View rootView;
    ListView listView;

    onDataChangeReceiver onDataChangeReceiver;

    public static final TaskList newInstance(String table) {
        TaskList fragment = new TaskList();
        Bundle bundle = new Bundle(2);
        bundle.putString(EXTRA_TABLE, table);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void updateList() {
        list.clear();
        list.addAll(db.getAllItems(TABLE));
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
        context.registerReceiver(onDataChangeReceiver, new IntentFilter(INTENT_UPDATE));
        adapt.notifyDataSetChanged();
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            TABLE = getArguments().getString(EXTRA_TABLE);
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
        context.registerReceiver(onDataChangeReceiver, new IntentFilter(INTENT_UPDATE));
        rootView = inflater.inflate(R.layout.checklist_fragment, container, false);

        db = new SQLiteHelper(context);
        list = db.getAllItems(TABLE);
        adapt = new MyAdapter(context, R.layout.checklist_item, list);
        if (list.isEmpty()) {
            if (TABLE.equals(TABLE_TASKS)) {

                addItem("Build an emergency kit", "", 0);
                addItem("Make a family communications plan", "", 0);
                addItem("Fasten shelves securely to walls", "", 0);
                addItem("Place large or heavy objects on lower shelves", "", 0);
                addItem("Store breakable items such as bottled foods, glass, and china in low, closed cabinets with latches", "", 0);
                addItem("Fasten heavy items such as pictures and mirrors securely to walls and away from beds, couches and anywhere people sit", "", 0);
                addItem("Brace overhead light fixtures and top heavy objects", "", 0);
                addItem("Repair defective electrical wiring and leaky gas connections", "", 0);
                addItem("Install flexible pipe fittings to avoid gas or water leaks", "", 0);
                addItem("Secure your water heater, refrigerator, furnace and gas appliances by strapping them to the wall studs and bolting to the floor", "", 0);
                addItem("Repair any deep cracks in ceilings or foundations", "", 0);
                addItem("Be sure the residence is firmly anchored to its foundation", "", 0);
                addItem("Store weed killers, pesticides, and flammable products securely in closed cabinets with latches and on bottom shelves", "", 0);
                addItem("Locate safe spots in each room under a sturdy table or against an inside wall", "", 0);
                addItem("Hold earthquake drills with your family members: Drop, cover and hold on", "", 0);
            } else if (TABLE.equals(TABLE_WORK)) {
                for (String string : workValues) {
                    addItem(string, "", 0);
                }
            } else if (TABLE.equals(TABLE_CAR)) {
                for (String string : carValues) {
                    addItem(string, "", 0);
                }
            } else if (TABLE.equals(TABLE_HOME)) {
                for (String string : homeValues) {
                    addItem(string, "", 0);
                }
            } else {
                Toast.makeText(context, "Wrong Table Lookup!", Toast.LENGTH_SHORT).show();
            }
        }
        this.listView = (ListView) rootView.findViewById(R.id.checkListView);
        listView.setAdapter(adapt);
        updateList();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceBundle) {
        super.onActivityCreated(savedInstanceBundle);
        listView.setVisibility(View.VISIBLE);
    }

    public void addListItemClass(ListItemClass listItemClass) {
        db.addItem(listItemClass);
        adapt.notifyDataSetChanged();
    }

    public void addItem(String name, String date, int checked) {
        ListItemClass item = new ListItemClass(TABLE, name, date, checked);
        db.addItem(item);
        adapt.notifyDataSetChanged();
    }

    private class MyAdapter extends ArrayAdapter<ListItemClass> implements ExpiryFragmentDialog.OnExpiryDismissedListener {

        Context context;
        List<ListItemClass> taskList = new ArrayList<ListItemClass>();
        int layoutResourceId;
        ListItemClass globalListItemClass;
        CheckBox globalCheckBox;

        public MyAdapter(Context context, int layoutResourceId,
                         List<ListItemClass> objects) {
            super(context, layoutResourceId, objects);
            this.layoutResourceId = layoutResourceId;
            this.taskList = objects;
            this.context = context;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            CheckBox chk;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.checklist_item,
                        parent, false);
                chk = (CheckBox) convertView.findViewById(R.id.taskListCheckBox);
                convertView.setTag(chk);

                chk.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        globalCheckBox = cb;
                        ListItemClass changeTask = (ListItemClass) cb.getTag();
                        globalListItemClass = changeTask;
                        changeTask.setChecked(cb.isChecked() ? 1 : 0);
                        if (cb.isChecked()) {
                            showExpiryDialog();
                        } else if (!cb.isChecked()) {
                            if(changeTask.getNotificationID() != 0) {
                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            notificationManager.cancel(changeTask.getNotificationID());
                            Log.d("com.blazingphoenix.preperation.TaskList", "Deleted Notification: " + changeTask.getNotificationID());
                            am.cancel(PendingIntent.getBroadcast(context, changeTask.getNotificationID(), new Intent(context, NotifyReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT));

                            changeTask.setNotificationID(0);
                            changeTask.setExpiry(null);
                            context.sendBroadcast(new Intent(TaskList.INTENT_UPDATE));
                            Toast.makeText(context, "Expiry Alert Deleted: " + changeTask.getName(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        db.updateItem(changeTask, TABLE);
                        context.sendBroadcast(new Intent(TaskList.INTENT_UPDATE));


                    }

                });
            } else {
                chk = (CheckBox) convertView.getTag();
            }
            ListItemClass current = taskList.get(position);
            chk.setText(current.getName());
            chk.setChecked(current.isChecked() == 1);
            chk.setTag(current);
            //convertView.setBackgroundResource(R.drawable.card_bg_yellow);
            chk.setBackgroundColor(Color.parseColor("#FFCA05"));
            return convertView;
        }

        public void showExpiryDialog() {
            ExpiryFragmentDialog expiryFragmentDialogDialog = new ExpiryFragmentDialog();
            expiryFragmentDialogDialog.setCancelable(false);
            expiryFragmentDialogDialog.setInterface(this);
            expiryFragmentDialogDialog.show(getFragmentManager(), "expiryDialog");
        }

        public Context getExpiryContext() {
            return context;
        }

        public CheckBox getExpiryCheckBox() {
            return globalCheckBox;
        }

        public ListItemClass getExpiryListItemClass() {
            return globalListItemClass;
        }

    }
}