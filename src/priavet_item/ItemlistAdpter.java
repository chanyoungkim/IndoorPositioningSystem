package priavet_item;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidhuman.example.mapsv2example.R;

public class ItemlistAdpter extends BaseAdapter {
	private Context context;
	private ArrayList<Item> mItems;

	public ItemlistAdpter(Context context, ArrayList<Item> Items) {
		this.context = context;
		this.mItems = Items;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.item_list, null);
		}

		ImageView imgIcon = (ImageView) convertView.findViewById(R.id.R_icon);
		TextView floor = (TextView) convertView.findViewById(R.id.R_floor);
		TextView name = (TextView) convertView.findViewById(R.id.R_name);

		if (mItems.get(position).isBluetooth())
			imgIcon.setImageResource(R.drawable.bluetooth);
		else
			imgIcon.setImageResource(R.drawable.object);
		if (mItems.get(position).getFloor() == 0)
			floor.setText("ÁöÇÏ 1Ãþ");
		else
			floor.setText(mItems.get(position).getFloor() + "Ãþ");
	
		name.setText(mItems.get(position).getName());
		name.setTextColor(mItems.get(position).getColor());
		return convertView;
	}
}
