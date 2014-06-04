package com.androidhuman.example.mapsv2example;

import java.util.ArrayList;

import priavet_item.Item;
import priavet_item.ItemlistAdpter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ImageView;


public class MapView extends ImageView {
	private ArrayList<Item> private_Items;
	private ItemlistAdpter R_adapter;
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	private PointF start = new PointF();
	private PointF mid = new PointF();
	float oldDist = 1f;
	float newDist = 1f;


	private MyLocation mlocation;
	private MyLocation itemLoction;
	private int selected_floor = 2;
	private int current_floor = 2;
	private int mcolor = Color.GREEN;

	public MapView(Context context) {
		super(context);
		mlocation = new MyLocation(getContext());
		itemLoction = new MyLocation(getContext());
	}

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mlocation = new MyLocation(getContext());
		itemLoction = new MyLocation(getContext());
	}

	public MapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mlocation = new MyLocation(getContext());
		itemLoction = new MyLocation(getContext());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//super.onTouchEvent(event);
		gestureDetector.onTouchEvent(event);

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY()
						- start.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}
		setImageMatrix(matrix);
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		float[] values = new float[9];
		matrix.getValues(values);

		if (current_floor == selected_floor) {
			mlocation.drawWithTransformations(canvas, values);
			itemLoction.setVisible(true);
		}

		for (int i = 0; i < private_Items.size(); i++) {
			if (selected_floor == private_Items.get(i).getFloor()) {
				itemLoction.setLocation(private_Items.get(i).getPosition());
				itemLoction.drawWithTransformations(canvas, values,
						private_Items.get(i).isBluetooth(), private_Items
								.get(i).getName(), private_Items.get(i)
								.getColor());
			}
		}
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	public void setCurrent_floor(int current_floor) {
		this.current_floor = current_floor;
	}

	public int getCurrent_floor() {
		return current_floor;
	}

	public void adjustWidth(float scale) {
		matrix.postScale(scale, scale);
		setImageMatrix(matrix);
		savedMatrix.set(matrix);
	}

	public void setVisibility(boolean visible) {
		mlocation.setVisible(visible);
	}

	public void setPosition(PointF point) {
		mlocation.setLocation(point);
	}

	public int getselected_floor() {
		return selected_floor;
	}

	public void setselected_floor(int floor_Number) {
		this.selected_floor = floor_Number;
	}

	public void setNavigate(boolean target) {
		mlocation.setTarget(target);
		invalidate();
	}

	public void setprivate_Items(ArrayList<Item> temp) {
		private_Items = temp;
	}

	public ItemlistAdpter getR_adapter() {
		return R_adapter;
	}

	public void setR_adapter(ItemlistAdpter r_adapter) {
		R_adapter = r_adapter;
	}

	public void makeDialog(final MotionEvent e) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle("물건의 이름을 입력해 주세요");
		final EditText input = new EditText(getContext());
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(input);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				float[] values = new float[9];
				matrix.getValues(values);
				float mRelativeX = ((e.getX() - values[2]) / values[0]);
				float mRelativeY = ((e.getY() - values[5]) / values[4]);
				private_Items.add(new Item(input.getText().toString(),
						new PointF(mRelativeX, mRelativeY), selected_floor,
						mcolor));
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		builder.show();
	}
	
	public void addBT(String name, int RSSI)
	{
		int k = 0;
		for(k = 0; k<private_Items.size();k++)
		{
			if(private_Items.get(k).getName() == name)
				private_Items.get(k).setPosition(mlocation.getLocation());
		}
		if(k == private_Items.size())
			private_Items.add(new Item(name,mlocation.getLocation(),selected_floor, Color.BLUE,RSSI));
			
		R_adapter.notifyDataSetChanged();
		invalidate();
	}

	@SuppressWarnings("deprecation")
	final GestureDetector gestureDetector = new GestureDetector(
			new GestureDetector.SimpleOnGestureListener() {
				public void onLongPress(MotionEvent e) {
					makeDialog(e);
					R_adapter.notifyDataSetChanged();
					invalidate();
				};
			});

	public void setMcolor(int mcolor) {
		this.mcolor = mcolor;
	}
}