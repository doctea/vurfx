package vurfeclipse;

import processing.core.PApplet;

public class APP {
	static public VurfEclipse a;
	
	static public String sketchPath(String s) {
		return getApp().sketchPath(s);
	}
	
	static public VurfEclipse getApp() {
		return a;
	}
	
	static public void setApp(VurfEclipse a) {
		APP.a = a;
	}
}
