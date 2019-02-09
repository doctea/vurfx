package vurfeclipse.streams;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import controlP5.Bang;
import controlP5.Group;
import vurfeclipse.APP;
import vurfeclipse.Targetable;
import vurfeclipse.filters.Filter;
import vurfeclipse.parameters.Parameter;
import vurfeclipse.parameters.ParameterBuffer;
import vurfeclipse.ui.ControlFrame;


/*interface ParameterCallback {
  public void call (Object value);
}*/
public abstract class ParameterCallback implements Serializable {
	/*public void call(String eventName, int time, Object value) {
    call(value);
  }*/
	public ParameterCallback () {
		this(new ParameterBuffer(100,500));
	}
	
	public ParameterCallback (ParameterBuffer paramBuffer) {
		setParamBuffer(paramBuffer);
	}
	
	private ParameterBuffer paramBuffer;

	public ParameterBuffer setParamBuffer(ParameterBuffer paramBuffer) {
		return this.paramBuffer = paramBuffer;
	}

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

	public void __call(Object value) {
		if (isEnabled()) {
			if (this.paramBuffer!=null) {
				value = this.paramBuffer.getValue(value, isLatching());
			}
			
			call(value);
		}
	}
	
	private void println(String string) {
		System.out.println(this + ": " +string);
	}
	
	public HashMap<String,Object> collectParameters() {
		HashMap<String,Object> params = new HashMap<String,Object> ();
		params.put("class", this.getClass().getName());
		params.put("streamSource", streamSource);
		params.put("enabled", isEnabled());
		params.put("latching", isLatching());
		return params;
	}

	public static ParameterCallback makeParameterCallback(Map<String, Object> params) {
		System.out.println ("makeParameterCallback() " + params);
		//HashMap<String,Object> input = (HashMap<String,Object>)payload;

		String classname = (String) params.get("class");
		try {
			Class<?> clazz = Class.forName(classname);
			//System.out.println (clazz.getConstructors());
			//Constructor<?> ctor = clazz.getConstructors()[0]; //[0]; //Scene.class, Integer.class);
			Constructor<ParameterCallback> ctor = (Constructor<ParameterCallback>) clazz.getConstructor(); //Scene.class,Integer.TYPE);
			ParameterCallback callback = (ParameterCallback) ctor.newInstance(); //(Scene)null, 0);
			callback.readParameters(params);
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

	public void readParameters(Map<String, Object> params) {
		if (params.containsKey("streamSource")) 	this.streamSource 	= (String) params.get("streamSource");
		if (params.containsKey("enabled")) 		this.setEnabled((Boolean)params.get("enabled"));
		if (params.containsKey("latching")) 		{
			this.setLatching((Boolean)params.get("latching"));
		}
		return;
	}

	Group g;
	protected boolean latching = false;
	protected BigDecimal latching_value = new BigDecimal(0);
	private Bang bngInputRcvd;
	public Group makeControls(ControlFrame cf, String name) {
		g = new Group(cf.control(), name + "_group");
		
		/*bngInputRcvd = cf.control().addBang(name + "_bang").moveTo(g).setPosition(0,0).setWidth(3).setColorActive(APP.getApp().color(0, 255, 0, 255));
		bngInputRcvd.setValue(0.0);
		g.add(bngInputRcvd);*/
		
		return g;
	}

	public boolean notifyRemoval(Targetable newf) {
		return false;
	}

	public boolean isLatching() {
		return latching;
	}

	public void setLatching(boolean latching) {
		if (latching==true) {
			System.out.println("caught setlatching true!");
		}
		this.latching = latching;
	}

}


