package priavet_item;

import java.io.Serializable;
import android.graphics.PointF;

public class Item implements Serializable{
	private String name;
	private float x, y;
	private double strong_Signal;
	private int floor;
	private int color;

	public Item(String name, PointF position,int floor,int color) {
		this.name = name;
		this.x = position.x;
		this.y = position.y;
		this.strong_Signal = -1000.0;
		this.floor = floor;
		this.color = color;
	}

	public Item(String name, PointF position, int floor,int color, double signal) {
		this.name = name;
		this.x = position.x;
		this.y = position.y;
		this.strong_Signal = signal;
		this.floor = floor;
		this.color = color;
	}

	public void setPosition(PointF position) {
		this.x = position.x;
		this.y = position.y;
	}

	public String getName() {
		return name;
	}

	public PointF getPosition() {
		return new PointF(x,y);
	}

	public double getStrong_Signal() {
		return strong_Signal;
	}
	
	public void setStrong_Signal(double strong_Signal) {
		this.strong_Signal = strong_Signal;
	}

	public void bluetooth_item(PointF temp_position, double signal) {
		if (strong_Signal < signal) {
			strong_Signal = signal;
			this.x = temp_position.x;
			this.y = temp_position.y;
		}
	}

	public boolean isBluetooth() {
		return strong_Signal < -1000.0;
	}
	
	public void setFloor(int floor) {
		this.floor = floor;
	}
	
	public int getFloor() {
		return floor;
	}
	
	public int getColor() {
		return color;
	}
	
	public void setColor(int color) {
		this.color = color;
	}
}
