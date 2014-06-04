package drawNavigation;

public class NavDrawerItem {
	private String title;
	private int icon;
	private String info;

	public NavDrawerItem() {
		this.title = null;
		this.icon = 0;
		this.info = null;
	}

	public NavDrawerItem(String title, int icon, String info) {
		this.title = title;
		this.icon = icon;
		this.info = info;
	}

	public String getTitle() {
		return this.title;
	}

	public int getIcon() {
		return this.icon;
	}

	public String getInfo() {
		return info;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}