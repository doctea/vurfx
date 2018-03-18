package vurfeclipse.streams;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.HashMap;
import controlP5.Group;
import vurfeclipse.APP;
import vurfeclipse.filters.Filter;
import vurfeclipse.parameters.Parameter;
import vurfeclipse.ui.ControlFrame;


/*interface ParameterCallback {
  public void call (Object value);
}*/
public abstract class ParameterCallback implements Serializable {
	/*public void call(String eventName, int time, Object value) {
    call(value);
  }*/

	String streamSource;
	boolean enabled = true;
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	
	public boolean reactsTo(String streamSource) {
		return (streamSource.equals(this.streamSource));
	}

	public String getStreamSource() {
		return streamSource;
	}

	public ParameterCallback setStreamSource(String streamSource) {
		this.streamSource = streamSource;
		return this;
	}

	boolean shouldDie = false;

	abstract public void call(Object value);


	int lastLerped;
	//int smoothingThresholdMillis = 100;
	private Object lerpValue(Object o, Object n) {
		boolean debug = false;
		int smoothingThresholdMillis = 100; // higher values == slower blends between parameters
		int scalingThresholdMillis = 100;//(int)delta;
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
				APP.getApp().map(delta, (float)0.5f, (float)scalingThresholdMillis, 0.1f, 1.0f);
				///200.0f;
				//	;
		if (debug) println("scale is " + scale);
		Object output = null;
		if (o instanceof Integer) {
			int diff = (int) (((Integer)n - (((Integer)o)))/2);
			if (debug) println("diff is " + diff);
			//output = new Integer((Integer)o + diff); //(Integer)o + (((Integer)o - (((Integer)n))/2));
			output = new Integer((Integer)o + (int)(scale * diff)); //(((Integer)n - (Integer)o))/2);
		} else if (o instanceof BigDecimal) {
			float diff = (Float)n - ((BigDecimal)o).floatValue();
		  output = new BigDecimal(((BigDecimal)o).floatValue() + scale * diff);
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
		System.out.println(this + ": " +string);
	}

	Object last_value;
	public void __call(Object value) {
		if (isEnabled()) {
			if (isLatching()) {
				if (value instanceof Float || value instanceof Double) {
					latching_value = latching_value.add(BigDecimal.valueOf((Float)value)); //Parameter.classvalue));	
				} else if (value instanceof Integer || value instanceof Long) {
					latching_value = latching_value.add(BigDecimal.valueOf((Integer)value)); //Parameter.classvalue));
				} else {
					latching_value = latching_value.add(BigDecimal.valueOf(1));
				}
				//value = latching_value.floatValue();// new Float(latching_value); //((Float)value) += latching_value;
				//System.out.println(count + " " + this + " latched " + value);
				value = latching_value.doubleValue(); //((Parameter)getObjectForPath(targetPath)).cast(value);
				//System.out.println ("got latched value " + value);
			}
			
			if (this.last_value!=null) {
				//if (!value.equals(value))
					value = lerpValue(this.last_value, value);
			}
			
			last_value = value;

			call(value);
		}
	}

	
	public HashMap<String,Object> collectParameters() {
		HashMap<String,Object> params = new HashMap<String,Object> ();
		params.put("class", this.getClass().getName());
		params.put("streamSource", streamSource);
		params.put("enabled", isEnabled());
		params.put("latching", isLatching());
		return params;
	}


	public static ParameterCallback makeParameterCallback(HashMap<String, Object> input) {
		// TODO Auto-generated method stub		
		System.out.println ("makeParameterCallback() " + input);
		//HashMap<String,Object> input = (HashMap<String,Object>)payload;

		String classname = (String) input.get("class");
		try {
			Class<?> clazz = Class.forName(classname);
			//System.out.println (clazz.getConstructors());
			//Constructor<?> ctor = clazz.getConstructors()[0]; //[0]; //Scene.class, Integer.class);
			Constructor<?> ctor = clazz.getConstructor(); //Scene.class,Integer.TYPE);
			ParameterCallback callback = (ParameterCallback) ctor.newInstance(); //(Scene)null, 0);
			callback.readParameters(input);
			return callback;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Caught " + e + ": Didn't manage to instantiate " + classname + " might be missing constructor?");
			e.printStackTrace();
		}

		//streamName = (String) input.get("name");
		//String paramName = (String) input.get("paramName");

		return null;
	}

	public void readParameters(HashMap<String, Object> input) {
		if (input.containsKey("streamSource")) 	this.streamSource 	= (String) input.get("streamSource");
		if (input.containsKey("enabled")) 		this.setEnabled((Boolean)input.get("enabled"));
		if (input.containsKey("latching")) 		{
			this.setLatching((Boolean)input.get("latching"));
		}
		return;
	}

	Group g;
	protected boolean latching = false;
	protected BigDecimal latching_value = new BigDecimal(0);
	public Group makeControls(ControlFrame cf, String name) {
		g = new Group(cf.control(), name + "_group");
		
		return g;
	}

	public boolean notifyRemoval(Filter newf) {
		return false;
	}

	public boolean isLatching() {
		return latching;
	}

	public void setLatching(boolean latching) {
		if (latching==true) {
			System.out.println("caught setlatchinging true!");
		}
		this.latching = latching;
	}

}


