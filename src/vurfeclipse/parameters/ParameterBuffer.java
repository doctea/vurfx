package vurfeclipse.parameters;

import java.math.BigDecimal;

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
				if (latching_value==null) latching_value = new Float(0);
				value = new Float ((Float)latching_value + (Float)value);
			} else if (value instanceof Integer || value instanceof Long) {
				//latching_value = latching_value.add(BigDecimal.valueOf((Integer)value)); //Parameter.classvalue));
				if (latching_value==null) latching_value = new Integer(0);
				value = new Integer((Integer)latching_value + (Integer)value);
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
		
		if (this.last_value!=null) {
			//if (!value.equals(value))
			value = lerpValue(this.last_value, value);		
			latching_value = value;
		}
		
		last_value = value;

		return value;
	}

	int lastLerped;
	//int smoothingThresholdMillis = 100;
	private Object lerpValue(Object o, Object n) {
		smoothingThresholdMillis = 200;
		scalingThresholdMillis = 100;
		boolean debug = false; //true;

		float delta = 0.1f + (float)(APP.getApp().millis() - lastLerped);
		if (delta>smoothingThresholdMillis) {
			lastLerped = APP.getApp().millis();
			return n;
		}
		//println("delta is " + ((float)(APP.getApp().millis() - lastLerped)/100));
		if (debug) println("delta is " + delta);
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
				APP.getApp().map(delta, (float)1f, (float)scalingThresholdMillis, 0.01f, 1.0f);
				///200.0f;
				//	;
		if (debug) println("scale is " + scale);
		Object output = null;
		if (o instanceof Integer) {
			int diff = (int) (((Integer)n - (((Integer)o)))/2);
			if (debug) println("diff is " + diff);
			//output = new Integer((Integer)o + diff); //(Integer)o + (((Integer)o - (((Integer)n))/2));
			output = new Integer((Integer)o + (int)(scale * diff)); //(((Integer)n - (Integer)o))/2);
		} else if (o instanceof BigDecimal && (n instanceof Float || n instanceof Double)) {
			float diff = (Float)n - ((BigDecimal)o).floatValue();
		    output = new BigDecimal(((BigDecimal)o).floatValue() + scale * diff);
		} else if (o instanceof BigDecimal && n instanceof BigDecimal) {
			BigDecimal diff = ((BigDecimal)n).subtract(((BigDecimal)o));
		    output = ((BigDecimal)o).add(diff.multiply(new BigDecimal((float)scale)));//.floatValue() + scale * diff);
		} else if (o instanceof Float) {
			//return point1 + alpha * (point2 - point1);
			if (((Float) o).isInfinite() || (((Float)o).isNaN())) return n;
			output = new Float((Float)o + scale * ((Float)n - (Float)o));//+ (((Float)o) - ((((Float)n))/2.0f));//);
			//output = n;
		} else {
			System.out.println("lerpValue() in " + this + ": unhandled object type " + o.getClass());
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
	private void println(String string) {
		System.out.println(this + string);
	}
	
	
}
