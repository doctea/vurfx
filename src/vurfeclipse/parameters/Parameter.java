package vurfeclipse.parameters;

import java.io.Serializable;

import vurfeclipse.APP;
import vurfeclipse.Targetable;
import vurfeclipse.VurfEclipse;


public class Parameter implements Serializable, Targetable {
  String name;
  public Object value;  
  //private transient controlP5.Controller controller;
  //String controllerName;
  
  String filterPath;
  //transient Scene sc;

  Class datatype;

  public Object min, max;
  
  Parameter () {
    //this.controller = cp5.getController(this.controllerName);
  }
  Parameter (String name, Object value) {
    this.name = name;
    this.value = value;
    this.datatype = value.getClass();
  }
  public Parameter (String name, Object value, Object min, Object max) {
    this(name, value);
    this.min = min;
    this.max = max;
  }
  
  public void setFilterPath(String filterPath) {
    this.filterPath = filterPath;
  }
  public String getFilterPath() {
	  return this.filterPath;
  }
  public String getPath() {
	  return this.getFilterPath();
  }
  public String getName () {
	  return this.name;
  }
  
  public Object cast(Object payload) {
	  if (this.datatype == Integer.class) {
		  return Integer.parseInt(payload.toString());
	  } else if (this.datatype == Double.class) {
		  return Double.parseDouble(payload.toString());
	  } else if (this.datatype == Boolean.class) {
		  return Boolean.parseBoolean(payload.toString());
	  } else if (this.datatype == String.class) {
		  return payload.toString();
	  }
	  return null;
  }
  
  @Override
  public Object target(String path, Object payload) {
	  //this.value = this.datatype.cast(payload);
	  setValue(
			  this.cast(payload)
	  );
	  
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
      float range = (Float)this.max - (Float)this.min;
      this.setValue ((Float)(range*(Float)f) + (Float)this.min);      
    } else if (value instanceof Integer) {
      int range = (Integer)this.max - (Integer)this.min;
      int v = (int)((int)range * f);
      this.setValue (v + (Integer)this.min);
    }
  }

}
