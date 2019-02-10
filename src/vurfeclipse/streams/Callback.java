package vurfeclipse.streams;

import java.util.HashMap;
import java.util.Map;

import vurfeclipse.Targetable;

public interface Callback {

	boolean reactsTo(String streamSource);

	String getStreamSource();

	void __call(Object v);

	boolean isEnabled();

	void setEnabled(boolean booleanValue);

	Callback setStreamSource(String paramName);

	boolean notifyRemoval(Targetable newf);
	
	public Callback setTemporary(boolean temp);
	public boolean isTemporary();

}

/*
Map<String, Object> collectParameters();
	void setStreamSource(String paramName);
*/