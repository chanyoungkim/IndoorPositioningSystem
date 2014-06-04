package com.androidhuman.example.mapsv2example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Set;

import priavet_item.HSVColorPickerDialog;
import priavet_item.Item;
import priavet_item.ItemlistAdpter;
import priavet_item.HSVColorPickerDialog.OnColorSelectedListener;

import com.androidhuman.example.mapsv2example.R;
import com.indooratlas.android.IndoorAtlas;
import com.indooratlas.android.IndoorAtlasException;
import com.indooratlas.android.IndoorAtlasFactory;
import com.indooratlas.android.IndoorAtlasListener;
import com.indooratlas.android.ServiceState;

import drawNavigation.NavDrawerItem;
import drawNavigation.NavDrawerListAdapter;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class indoorMain extends FragmentActivity implements
		IndoorAtlasListener, OnTouchListener {

	private MapView floor_Image;
	private BluetoothAdapter btAdapter;
	private BluetoothStateReceiver btReceiver;

	private Handler handler = new Handler();
	private IndoorAtlas indoorAtlas;

	private final String apiKey = "b139be39-fd47-4ceb-b989-6f26657212f9";
	private final String secretKey = "jd7tj6r4pCIjxGeK8xpo9&!hazZOlIJIb1RsgHQAwyn7w3w)gDq%TUyVhBlFtaEMYELPIKUMRMW!A(TpyU1fMd&Z36qqFUQaJ%UWWINPUNspJo9zzKcWwfk2ije4qlSz";

	private final String buildingId = "58ae1d15-c31a-4944-8554-56e573e657e3";
	private final String levelId = "1799e5f1-1767-4ed1-b563-241287810463";
	private boolean calibrated = false;

	private boolean isRightOpen = false;
	private boolean isleftOpen = false;
	private boolean isrcalculate = false;

	private Button bluetooth;
	private Button navigate;
	private Button object;
	private String[] navItems = { "F7", "F6", "F5", "F4", "F3", "F2", "F1",
			"B1" };
	private String[] navInfo = { "전자정보통신대학", "컴퓨터공학부", "전자공학부", "전자정보통신대학",
			"체육대학, 산림과학대학", "체육대학, 산림과학대학", "체육대학, 산림과학대학", "주차장" };
	private View right_View;
	private ListView left_NavList;
	private ListView right_NavList;
	private TextView temp_message;
	private DrawerLayout dlDrawer;
	private LinearLayout temp_view;
	private ActionBarDrawerToggle left_dtToggle;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private ArrayList<Item> private_Items;
	private NavDrawerListAdapter adapter;
	private ItemlistAdpter R_adapter;
	private Switch position_on_off;
	ActionBar actionBar;
	Vibrator vib;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_indoor);
		vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		floor_Image = (MapView) findViewById(R.id.floor_image);

		try {
			indoorAtlas = IndoorAtlasFactory.createIndoorAtlas(
					this.getApplicationContext(), this, apiKey, secretKey);
		} catch (IndoorAtlasException ex) {
			showMessageOnUI("Failed to connect to IndoorAtlas. Check your credentials.");
			this.finish();
		}

		reimage();
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(btReceiver, filter);

		filter = new IntentFilter(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION);
		this.registerReceiver(btReceiver, filter);

		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(btReceiver, filter);

		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!btAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
					Log.d("receive","2222222222222222");
			startActivityForResult(enableBtIntent, 3);
		}

		left_NavList = (ListView) findViewById(R.id.left_nav_list);
		right_NavList = (ListView) findViewById(R.id.right_nav_list);

		try {
			File temp = new File(getFilesDir().getAbsoluteFile() + "/item.dat");
			if (temp != null) {
				private_Items = new ArrayList<Item>();
				FileInputStream fis = new FileInputStream(getFilesDir()
						.getAbsoluteFile() + "/item.dat");
				ObjectInputStream ois = new ObjectInputStream(fis);
				int count = ois.readInt();
				for (int j = 0; j < count; j++)
					private_Items.add((Item) ois.readObject());

				fis.close();
				ois.close();
			} else {
				private_Items = new ArrayList<Item>();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		temp_message = (TextView) findViewById(R.id.message);
		temp_view = (LinearLayout) findViewById(R.id.temp_view);
		R_adapter = new ItemlistAdpter(getApplicationContext(), private_Items);
		right_NavList.setAdapter(R_adapter);

		left_NavList.setOnItemClickListener(new DrawerItemClickListener());
		right_NavList.setOnItemClickListener(new R_ItemClickListener());

		dlDrawer = (DrawerLayout) findViewById(R.id.dl_activity_main_drawer);
		right_View = (View) findViewById(R.id.drawer_right);
		getActionBar().setIcon(new ColorDrawable(Color.TRANSPARENT));
		getActionBar().setTitle("Building NO.7");
		getActionBar().setDisplayShowTitleEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		for (int i = 0; i < 8; i++)
			navDrawerItems.add(new NavDrawerItem(navItems[i],
					R.drawable.building, navInfo[i]));

		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);

		left_NavList.setAdapter(adapter);
		left_NavList.setOnItemClickListener(new DrawerItemClickListener());

		left_dtToggle = new ActionBarDrawerToggle(this, dlDrawer,
				R.drawable.left_menu, R.string.app_name, R.string.app_name) {

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}
		};

		dlDrawer.setDrawerListener(left_dtToggle);
		position_on_off = (Switch) findViewById(R.id.position_switch);
		position_on_off.setChecked(false);
		position_on_off
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							floor_Image.setVisibility(true);
							dlDrawer.closeDrawer(right_View);
							isRightOpen = false;
							floor_Image.postInvalidate();
						} else {
							floor_Image.setVisibility(false);
							dlDrawer.closeDrawer(right_View);
							isRightOpen = false;
							floor_Image.postInvalidate();
						}
					}
				});

		bluetooth = (Button) findViewById(R.id.button_bluetooth);
		bluetooth.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					bluetooth.setBackgroundColor(Color.rgb(244, 244, 236));
					Intent car = new Intent(indoorMain.this, tab4.class);
					startActivity(car);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					bluetooth.setBackgroundColor(Color.TRANSPARENT);
				}
				return false;
			}
		});

		navigate = (Button) findViewById(R.id.button_navigate);
		navigate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					navigate.setBackgroundColor(Color.rgb(244, 244, 236));
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					navigate.setBackgroundColor(Color.TRANSPARENT);
				}
				return false;
			}
		});

		object = (Button) findViewById(R.id.button_object);
		object.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					object.setBackgroundColor(Color.rgb(244, 244, 236));
					HSVColorPickerDialog cpd = new HSVColorPickerDialog(
							indoorMain.this, 0xFF4488CC,
							new OnColorSelectedListener() {
								@Override
								public void colorSelected(Integer color) {
									floor_Image.setMcolor(color);
								}
							});

					cpd.setTitle("색상을 지정하세요");
					cpd.show();
					dlDrawer.closeDrawer(right_View);
					isRightOpen = false;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					object.setBackgroundColor(Color.TRANSPARENT);
				}
				return false;
			}
		});

		floor_Image.setprivate_Items(private_Items);
		floor_Image.setR_adapter(R_adapter);
		actionBar = getActionBar();
		actionBar.hide();
		floor_Image.setCurrent_floor(2);
		showFloorList();
	}

	private void reimage() {
		floor_Image.post(new Runnable() {

			@Override
			public void run() {
				int device_width = getWindow().getWindowManager()
						.getDefaultDisplay().getWidth();
				int imageWidth = ((BitmapDrawable) floor_Image.getDrawable())
						.getBounds().width();
				float scale = device_width / (float) imageWidth;
				floor_Image.adjustWidth(scale);
			}
		});
		floor_Image.postInvalidate();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		btReceiver = new BluetoothStateReceiver();
		IntentFilter filter = new IntentFilter(
				BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(btReceiver, filter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.indoor_actionbar, menu);
		menu.add(0, 0, 0, "현재 층 설정");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		left_dtToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		left_dtToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (left_dtToggle.onOptionsItemSelected(item)) {
			if (isleftOpen) {
				dlDrawer.closeDrawer(left_NavList);
				isleftOpen = false;
			} else {
				if (isRightOpen) {
					dlDrawer.closeDrawer(right_View);
					isRightOpen = false;
				}
				dlDrawer.openDrawer(left_NavList);
				isleftOpen = true;
			}
			return true;
		}

		if (item.getItemId() == R.id.personal_info) {
			if (!isRightOpen) {
				if (isleftOpen) {
					dlDrawer.closeDrawer(left_NavList);
					isleftOpen = false;
				}
				dlDrawer.openDrawer(right_View);
			} else {
				dlDrawer.closeDrawer(right_View);
			}
			isRightOpen = !isRightOpen;
			return true;
		}

		if (item.getItemId() == 0)
			showFloorList();

		return super.onOptionsItemSelected(item);
	}

	private void showFloorList() {
		final CharSequence[] items = { "7호관 7층", "7호관 6층", "7호관 5층", "7호관 4층",
				"7호관 3층", "7호관 2층", "7호관 1층", "7호관 지하 1층" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("현재 층을 선택하세요") // 현재 층을 선택하세요
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int index) {
						change_current_floor(7 - index);
						onResume();
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void showPosition(final PointF point) {
		handler.post(new Runnable() {
			public void run() {
				floor_Image.setPosition(point);
				floor_Image.postInvalidate();
			}
		});
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		v.onTouchEvent(event);
		return false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(btReceiver);
			btReceiver = null;
			FileOutputStream fos = new FileOutputStream(getFilesDir()
					.getAbsoluteFile() + "/item.dat");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeInt(private_Items.size());
			for (int j = 0; j < private_Items.size(); j++)
				oos.writeObject(private_Items.get(j));
			fos.close();
			oos.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onStop() {
		try {
			Log.d("&10", "onStop");
			showMessageOnUI("onStop(): Stopping positioning.");
			indoorAtlas.stopPositioning();
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onStop();
	}

	@Override
	protected void onPause() {
		Log.d("&9", "onPause");
		showMessageOnUI("onPause(): Stopping positioning.");
		indoorAtlas.stopPositioning();
		unregisterReceiver(btReceiver);
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		showMessageOnUI("onRestart().");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("&8", "onResume");
		if (calibrated == false) {
			showMessageOnUI("Calibrating... Waiting for Android's internal calibration, please continue fingure eight motion...");
			if (isrcalculate == false) {
				indoorAtlas.calibrate();
				isrcalculate = true;
			}
		} else {
			Log.d("&15", "onResume : start Positioning");
			showMessageOnUI("onResume(): Starting positioning.");
			indoorAtlas.startPositioning(buildingId, levelId,
					Building_Data.floor[floor_Image.getCurrent_floor()], false);
		}
	}

	// Called on every new location estimate
	public void onServiceUpdate(ServiceState state) {
		// Use location estimate
		Log.d("&1", "onServiceUpdate");
		if (temp_view.getVisibility() == View.VISIBLE)
			invisible();
		showPosition(new PointF(state.getImagePoint().getI()/3, state
				.getImagePoint().getJ()/3));
	}

	// Request failed
	public void onServiceFailure(final String reason) {
		Log.d("&2", "onServiceFailure(): reason : " + reason);
		showMessageOnUI("onServiceFailure(): reason : " + reason);
	}

	// Initializing location service
	public void onServiceInitializing() {
		showMessageOnUI("ServiceInitializing");
		Log.d("&3", "onServiceInitializing");
	}

	// Initialization completed
	public void onServiceInitialized() {
		Log.d("&4", "onServiceInitialized");
		vib.vibrate(100);
		showMessageOnUI("onServiceInitialized(): Walk to get location fix");
	}

	// Location service initialization failed
	public void onInitializationFailed(final String reason) {
		showMessageOnUI("onInitializationFailed(): " + reason);
	}

	// Positioning was stopped
	public void onServiceStopped() {
		Log.d("&6", "onServiceStopped");
		showMessageOnUI("onServiceStopped(): IndoorAtlas Positioning Service is stopped.");
	}

	// Calibration failed
	public void onCalibrationFailed(String reason) {
		Log.d("&7", "onCalibrationFailed(): Calibration failed, reason : "
				+ reason);
		showMessageOnUI("onCalibrationFailed(): Calibration failed, reason : "
				+ reason);
		isrcalculate = false;
	}

	// Calibration successful, positioning can be started
	public void onCalibrationFinished() {
		Log.d("&5", "onCalibrationFinished");
		showMessageOnUI("onCalibrationFinished(): starting positioning.");
		vib.vibrate(100);
		calibrated = true;
		indoorAtlas.startPositioning(buildingId, levelId,
				Building_Data.floor[floor_Image.getCurrent_floor()], false);
	}

	// Helper method
	private void showMessageOnUI(final String message) {
		handler.post(new Runnable() {
			public void run() {
				if (temp_view.getVisibility() != View.INVISIBLE)
					temp_message.setText(message);
			}
		});
	}

	private void invisible() {
		handler.post(new Runnable() {
			public void run() {
				actionBar.show();
				temp_view.setVisibility(View.INVISIBLE);
			}
		});
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long id) {

			set_image(position);
			dlDrawer.closeDrawer(left_NavList);
			isleftOpen = false;
			dlDrawer.closeDrawer(right_View);
			isRightOpen = false;
		}
	}

	private void set_image(int selected_number) {
		switch (selected_number) {
		case 0:
			floor_Image.setImageDrawable(getResources().getDrawable(
					R.drawable.f7));
			floor_Image.setselected_floor(7);
			break;
		case 1:
			floor_Image.setImageDrawable(getResources().getDrawable(
					R.drawable.f6));
			floor_Image.setselected_floor(6);
			break;
		case 2:
			floor_Image.setImageDrawable(getResources().getDrawable(
					R.drawable.f5));
			floor_Image.setselected_floor(5);
			break;
		case 3:
			floor_Image.setImageDrawable(getResources().getDrawable(
					R.drawable.f4));
			floor_Image.setselected_floor(4);
			break;
		case 4:
			floor_Image.setImageDrawable(getResources().getDrawable(
					R.drawable.f3));
			floor_Image.setselected_floor(3);
			break;
		case 5:
			floor_Image.setImageDrawable(getResources().getDrawable(
					R.drawable.f2));
			floor_Image.setselected_floor(2);
			break;
		case 6:
			floor_Image.setImageDrawable(getResources().getDrawable(
					R.drawable.f1));
			floor_Image.setselected_floor(1);
			break;
		case 7:
			floor_Image.setImageDrawable(getResources().getDrawable(
					R.drawable.b1));
			floor_Image.setselected_floor(0);
			break;
		}
	}

	private void change_current_floor(int m) {
		indoorAtlas.stopPositioning();
		temp_view.setVisibility(View.VISIBLE);
	}

	private class R_ItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long id) {
			private_Items.remove(position);
			R_adapter.notifyDataSetChanged();

			dlDrawer.closeDrawer(left_NavList);
			isleftOpen = false;
			dlDrawer.closeDrawer(right_View);
			isRightOpen = false;
			floor_Image.postInvalidate();
		}
	}

	private final class BluetoothStateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("#10", "broadcasted");
			int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
					BluetoothAdapter.STATE_OFF);

			for (BluetoothDevice device : btAdapter.getBondedDevices()) {
				int bt_RSSI = intent.getIntExtra(BluetoothDevice.EXTRA_RSSI,
						Short.MIN_VALUE);
				String bt_name = device.getName();
				floor_Image.addBT(bt_name, bt_RSSI);
				Log.d("#10", "RSSI : " + bt_RSSI);
			}
		}
	}

	
}
