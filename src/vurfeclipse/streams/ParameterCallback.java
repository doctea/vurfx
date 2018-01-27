package vurfeclipse.streams;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashMap;


/*interface ParameterCallback {
  public void call (Object value);
}*/
public abstract class ParameterCallback implements Serializable {
	/*public void call(String eventName, int time, Object value) {
    call(value);
  }*/


	boolean shouldDie = false;


	abstract public void call(Object value);


	public HashMap<String,Object> collectParameters() {
		HashMap<String,Object> params = new HashMap<String,Object> ();
		params.put("class", this.getClass().getName());
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
		return;
	}
}


