package com.androidhuman.example.mapsv2example;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;

public class MyLocation extends View {
	private static final int COLOR_POSITION = Color.RED;
	private static final int COLOR_BLUETOOTH = Color.BLUE;

	private Paint mPaint; // draw color
	private PointF mLocation; // location on screen
	private float mRadius; // circle radius
	private float mRelativeX, mRelativeY;

	private boolean mVisible;
	public boolean target_set = false;

	public MyLocation(Context context) {
		super(context);
		mPaint = new Paint();
		mPaint.setColor(COLOR_POSITION);
		mPaint.setTextSize(25);
		mPaint.setAntiAlias(true);

		mVisible = false;
		mRadius = 5f;
		mLocation = new PointF(0, 0);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

	protected void drawWithTransformations(Canvas canvas, float[] matrixValues,
			boolean bluetooth, String name, int color) {
		mRelativeX = (matrixValues[2] + mLocation.x * matrixValues[0]);
		mRelativeY = (matrixValues[5] + mLocation.y * matrixValues[4]);

		if (bluetooth) {
			mPaint.setColor(COLOR_BLUETOOTH);
		} else
			mPaint.setColor(color);

		canvas.drawCircle(mRelativeX, mRelativeY, mRadius, mPaint);
		canvas.drawText(name, mRelativeX, mRelativeY, mPaint);
	}

	protected void drawWithTransformations(Canvas canvas, float[] matrixValues) {
		mRelativeX = (matrixValues[2] + mLocation.x * matrixValues[0]);
		mRelativeY = (matrixValues[5] + mLocation.y * matrixValues[4]);

		if (mVisible == true) {
			mPaint.setColor(COLOR_POSITION);
			canvas.drawCircle(mRelativeX, mRelativeY, mRadius, mPaint);
		}
	}

	public void setLocation(PointF location) {
		mLocation = location;
	}

	public PointF getLocation() {
		return mLocation;
	}

	public void setSize(float radius) {
		mRadius = radius;
	}

	public void setVisible(boolean visible) {
		mVisible = visible;
	}

	public boolean isVisible() {
		return mVisible;
	}

	public void setTarget(boolean target) {
		target_set = target;
	}

	public void setTartPosition(PointF point) {
		target_set = true;
	}
}
