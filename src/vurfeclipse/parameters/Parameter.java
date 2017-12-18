package vurfeclipse.parameters;

import java.io.Serializable;
import java.util.HashMap;

import processing.core.PVector;
import vurfeclipse.APP;
import vurfeclipse.Targetable;
import vurfeclipse.VurfEclipse;
import vurfeclipse.filters.Filter;


public class Parameter implements Serializable, Targetable {
	String name;
	public Object value;  

	private Object defaultValue;
	//private transient controlP5.Controller controller;
	//String controllerName;

	String filterPath;
	transient Filter filter;
	//transient Scene sc;

	Class<? extends Object> datatype;

	private Object min;
	private Object max;

	Parameter () {
		//this.controller = cp5.getController(this.controllerName);
	}
	Parameter (Filter filter, String name, Object value) {
		this.filter = filter;
		this.filterPath = filter.getPath();
		this.name = name;
		this.value = value;
		this.setDefaultValue(value);
		this.datatype = value.getClass();
	}
	public Parameter (Filter filter, String name, Object value, Object min, Object max) {
		this(filter, name, value);
		if (this.filter==null) {
			System.err.println ("wtf null filter?");
		}
		this.setMin(min);
		this.setMax(max);
	}

	public Object reset () {
		setValue(getDefaultValue());
		return getDefaultValue();
	}

	public Object getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public void setFilterPath(String filterPath) {
		if (filterPath==null) {
			System.err.println("no filterpath set!");
			System.exit(0);
		}
		this.filterPath = filterPath;
	}
	public String getFilterPath() {
		return this.filterPath;
	}
	public String getPath() {
		return this.getFilterPath() + "/pa/" + name; /// TODO: HALFWAY THROUGH REJIGGING THIS TO USE BREAKERS LIKE THIS.... 
	}
	public String getName () {
		return this.name;
	}

	public Object cast(Object payload) {
		try {
			if (this.datatype == Integer.class) {
				return Integer.parseInt(payload.toString());
			} else if (this.datatype == Float.class || this.datatype == Double.class) {
				return (Float)Float.parseFloat(payload.toString());
			} else if (this.datatype == Boolean.class) {
				return Boolean.parseBoolean(payload.toString());
			} else if (this.datatype == String.class) {
				return payload.toString();
			} else if (this.datatype == PVector.class) {
				return (PVector)payload;
			}
		} catch (NumberFormatException e) {
			System.err.println(this + this.getName() + " caught " + e.toString() + " trying to decode " + " alleged " + this.datatype + " of '" + payload + "'");
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Object target(String path, Object payload) {
		//filter.println("Parameter " + getName() + " targeted with " + " path " + " and " + payload);
		//this.value = this.datatype.cast(payload);
		setValue(
				this.cast(payload)
		);

		filter.changeParameterValue(name, this.cast(payload));	// was previously updateParameterValue..?!

		return this.value.toString();
	}

	/*public void setController(controlP5.Controller c) {
    //this.controller = c;
    controllerName = c.getControllerName();
  }*/

	/*public controlP5.Controller getController() {
    if (sc==null) sc = pr.getSceneForPath(this.scenePath);
    return sc.getControllerFor(name);
  }*/

	public void setValue(Object value) {
		this.value = value;
		if (filterPath!=null)
			((VurfEclipse)APP.getApp()).pr.updateControl(filterPath, name, value);
		/*controlP5.Controller c = getController();
    if (c!=null) {
      if (value instanceof Float)
        c.setValue((Float)value);
      else if (value instanceof Integer) 
        c.setValue((Integer)value);
      else if (value instanceof Boolean)
        c.setValue((Boolean)value?1.0:0.0);
    }*/
	}


	public void setValueFromSin(float f) {
		if (value instanceof Float) {
			float range = (Float)this.getMax() - (Float)this.getMin();
			this.setValue ((Float)(range*(Float)f) + (Float)this.getMin());      
		} else if (value instanceof Integer) {
			int range = (Integer)this.getMax() - (Integer)this.getMin();
			int v = (int)((int)range * f);
			this.setValue (v + (Integer)this.getMin());
		}
	}
	public Class getDataType() {
		return this.datatype;
	}
	public HashMap<String,Object> getParameterSetup() {
		HashMap<String,Object> output = new HashMap<String,Object> ();
		
		output.put("datatype", 	this.getDataType());
		output.put("name", 		this.getName());
		output.put("default", 	this.getDefaultValue());
		output.put("max", 		this.getMax());
		output.put("min", 		this.getMin());
		
		return output;		
	}
	public Object getMax() {
		return max;
	}
	public void setMax(Object max) {
		this.max = max;
	}
	public Object getMin() {
		return min;
	}
	public void setMin(Object min) {
		this.min = min;
	}

}
