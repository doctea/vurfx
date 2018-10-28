package vurfeclipse.parameters;

import java.math.BigDecimal;

import processing.core.PApplet;
import vurfeclipse.APP;

// for buffering values between Streams / Sequences and Parameters

public class ParameterBuffer {

	public ParameterBuffer(int smoothingThresholdMillis, int scalingThresholdMillis) {
		this.smoothingThresholdMillis = smoothingThresholdMillis;
		this.scalingThresholdMillis = scalingThresholdMillis;
	}
	
	public ParameterBuffer() {
		// TODO Auto-generated constructor stub
	}

	Object latching_value;
	Object last_value;
	
	int smoothingThresholdMillis = 100; // higher values == slower blends between parameters
	int scalingThresholdMillis = 100;//(int)delta;
	
	/*public void __call(Object value) {
		if (isEnabled()) {
			if (isLatching()) {*/
	public Object getValue(Object value, boolean latching) {
		if (latching) {
			if (value instanceof Float || value instanceof Double) {
				//latching_value = latching_value.add(BigDecimal.valueOf((Float)value)); //Parameter.classvalue));
				if (latching_value==null || !(latching_value instanceof Float)) latching_value = new Float(0);
				value = (Float)latching_value + (Float)value;
			} else if (value instanceof Integer || value instanceof Long) {
				//latching_value = latching_value.add(BigDecimal.valueOf((Integer)value)); //Parameter.classvalue));
				if (latching_value==null) latching_value = new Integer(0);
				value = (Integer)latching_value + (Integer)value;
			} else if (value instanceof BigDecimal) {
				if (latching_value==null) latching_value = new BigDecimal(0);
				value = ((BigDecimal) latching_value).add((BigDecimal) value); //Parameter.classvalue));
			} else {
				//latching_value = latching_value.add(BigDecimal.valueOf(1));
				println("don't know what to do wtih " + value.getClass() + " in latching addition?");
			}
		} else {
			latching_value = value;
		}
		
		//value = latching_value.floatValue();// new Float(latching_value); //((Float)value) += latching_value;
		//System.out.println(count + " " + this + " latched " + value);
		//value = latching_value.doubleValue(); //((Parameter)getObjectForPath(targetPath)).cast(value);
		//System.out.println ("got latched value " + value);
		
		// disabled lerping 2018-10-14 cos of interference with MIDI controls
		/*if (this.last_value!=null) {
			//if (!value.equals(value))
			value = lerpValue(this.last_value, value);		
			latching_value = value;
		}*/
		
		last_value = value;

		return value;
	}

	int lastLerped;
	private boolean circular;
	public void setCircular(boolean circular) {
		this.circular = circular;
	}

	//int smoothingThresholdMillis = 100;
	private Object lerpValue(Object o, Object n) {
		//smoothingThresholdMillis = 150;
		//scalingThresholdMillis = 50; //1; //100;
		boolean debug = false; //true;
		
		//if (true) return n;
		if (n==last_value) {	// if setting same value as last time, set it absolutely without lerping
			return n;
		}

		float delta = 0.1f + (float)(APP.getApp().millis() - lastLerped);
		if (delta > smoothingThresholdMillis) {
			lastLerped = APP.getApp().millis();
			return n;
		}
		//println("delta is " + ((float)(APP.getApp().millis() - lastLerped)/100));
		if (debug) println("delta is " + delta);
		//APP.getApp();
		//delta *= 10.0f;
		//delta = delta/delta;
		//delta = smoothingThresholdMillis * delta;
				//1.0f/delta * 
		float scale = //0.5f;
			//1.0f/
				/*APP.getApp().constrain (
						0.01f + Math.abs (1.1f - delta), // * 10.0f;
						0.0f, 1.0f
						);*/
				PApplet.map(delta, (float)1f, (float)scalingThresholdMillis, 0.5f, 1.0f); //1.0f);
				///200.0f;
				//	;
		if (debug) 
			println("scale is " + scale);
		
		float distanceModifier = 10.0f; // 10.0f;
		float distanceDivisor = 2.0f;

		float diff = 0.0f;
		Object output = null;
		if (o instanceof Integer) {		
			//if(true) return n;
			diff = (int) (((Integer)n - (((Integer)o)))/distanceDivisor);
			if (debug) println("diff is " + diff);
			
			if (this.isCircular())
				if (Math.abs(diff) > Math.abs((Integer)o)/distanceDivisor) scale = APP.getApp().constrain(scale * distanceModifier,0.1f,1.0f);
			//output = new Integer((Integer)o + diff); //(Integer)o + (((Integer)o - (((Integer)n))/2));
			output = new Integer((Integer)o + (int)(scale * diff)); //(((Integer)n - (Integer)o))/2);
		} else if (o instanceof BigDecimal && (n instanceof Float || n instanceof Double)) {		
			//if(true) return n;
			diff = (Float)n - ((BigDecimal)o).floatValue();
			if (this.isCircular()) 
				if (Math.abs(diff) > Math.abs((Float)o)/distanceDivisor) scale = APP.getApp().constrain(scale * distanceModifier,0.1f,1.0f);
			output = new BigDecimal(((BigDecimal)o).floatValue() + scale * diff);
		} else if (o instanceof BigDecimal && n instanceof BigDecimal) {
			float 	fo = ((BigDecimal)o).floatValue(), 
					fn = ((BigDecimal)n).floatValue();
			//BigDecimal diff = ((BigDecimal)n).subtract(((BigDecimal)o));
			diff = fn - fo;
			if (this.isCircular()) 
				if (Math.abs(diff) > Math.abs((Float)fo)/distanceDivisor) scale = APP.getApp().constrain(scale * distanceModifier,0.1f,1.0f);
			output = new BigDecimal(fo + scale * diff); //((BigDecimal)o).add(diff.multiply(new BigDecimal((float)scale)));//.floatValue() + scale * diff);
		} else if (o instanceof Float) {
			//return point1 + alpha * (point2 - point1);
			if (((Float) o).isInfinite() || (((Float)o).isNaN())) return n;
			diff = ((Float)n - (Float)o);
			if (this.isCircular())
				if (Math.abs(diff) > Math.abs((Float)o)/distanceDivisor) scale = APP.getApp().constrain(scale * distanceModifier,0.1f,1.0f);
			output = (Float)o + scale * diff;//+ (((Float)o) - ((((Float)n))/2.0f));//);
			//output = n;
		} else {
			System.out.println("lerpValue() in " + this + ": unhandled object type " + o.getClass());
		}
		if (debug) {
			println("scale modified by distance to " + scale + ", diff is " + diff);
			println("got output value " + output + " from "+ o);
		}
		if (output!=null) {
			if (debug) println("lerp: " + o + " via " + output + " to " + n);
			if (debug) println("--lerp");
			
			lastLerped = APP.getApp().millis(); 
			return output;
		} else {
			return n;
		}
	}
	private boolean isCircular() {
		return circular;
	}

	private void println(String string) {
		System.out.println(this + string);
	}
	
	
}
