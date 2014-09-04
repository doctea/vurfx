package vurfeclipse;

import processing.core.PApplet;

public class APP {
	static public PApplet a;
	
	static public String sketchPath(String s) {
		return getApp().sketchPath(s);
	}
	
	static public PApplet getApp() {
		return a;
	}
	
	static public void setApp(PApplet a) {
		APP.a = a;
	}
}
