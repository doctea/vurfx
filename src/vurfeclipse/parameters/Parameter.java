package vurfeclipse.parameters;

import java.io.Serializable;
import java.util.HashMap;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.ControllerGroup;
import controlP5.Knob;
import controlP5.Slider;
import controlP5.Textfield;
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
	public Parameter (Filter filter, String name, Object value) {
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
				if (payload instanceof Float) {
					return ((Float) payload).intValue();
				}
				return Integer.parseInt(payload.toString());
			} else if (this.datatype == Float.class || this.datatype == Double.class) {
				return (Float)Float.parseFloat(payload.toString());
			} else if (this.datatype == Boolean.class) {
				return Boolean.parseBoolean(payload.toString());
			} else if (this.datatype == String.class) {
				return payload.toString();
			} else if (this.datatype == PVector.class) {
				return (PVector)payload;
			} else {
				System.err.println("Don't know how to cast " + payload.getClass() + " '" + payload + "'");
			}
		} catch (NumberFormatException e) {
			System.err.println("got payload type " + payload.getClass() + " but expected " + this.datatype);
			System.err.println(this + this.getName() + " caught " + e.toString() + " trying to decode " + " alleged " + this.datatype + " of '" + payload + "'");
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Object target(String path, Object payload) {
		//filter.println("Parameter " + getName() + " targeted with " + " path " + " and " + payload);
		//this.value = this.datatype.cast(payload);

		//System.out.println("payload is " + payload + ", max is " + getMax() + ", cast payload is " + this.cast(payload));
		if (this.datatype == Integer.class && (Integer)this.getMax()>0) {
			if ((Integer)this.cast(payload)>(Integer)this.getMax()) {
				//System.out.println ("payload is " + (int)this.cast(payload) + " and max is " + getMax() + " - mod should be " + (new Integer(((int)this.cast(payload)) % (int)this.getMax())));
				payload = new Integer((Integer)this.cast(payload) % (Integer)this.getMax());
				//System.out.println("wrapped payload is " + payload);
			}
		}
		
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
		// lerp between new value and last value
		//System.out.println("setValue, value is already " + this.value + ", new value is " + value);

		this.value = value;
		//System.out.println("setting value to " + value);
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


	private void println(String string) {
		System.out.println("Param: " + this + ": " + string);		
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

	synchronized public Controller makeController(ControlP5 cp5, String tabName, ControllerGroup tab, int size) {
		controlP5.Controller o;

		final Parameter self = this;
		
		if (value instanceof Float) {
			if (getName().toLowerCase().contains("rotat") ) { //(Float)getMax()==360.0f) {
				o = cp5.addKnob(tabName).setConstrained(false).setValue((Float)value).setLabel(getName()).setRange((Float)getMin(), (Float)getMax()).setSize(size*2, size*2).setDragDirection(Knob.VERTICAL);
			} else {
				o = cp5.addSlider(tabName).setValue((Float)(Float)value).setLabel(getName())
						.setSliderMode(Slider.FLEXIBLE)
						.setRange(
								new Float((Float)getMin()),
								new Float((Float)getMax())
								)
						.setSize(size*5, size) ;
			}
		} else if (value instanceof Integer) {
			if (getName().toLowerCase().contains("rotat") ) { //(Integer)getMax()==360) {
				o = cp5.addKnob(tabName).setConstrained(false).setValue((Integer)value).setLabel(getName()).setRange((Integer)getMin(), (Integer)getMax()).setSize(size*2, size*2).setDragDirection(Knob.VERTICAL);
			} else if (getName().toLowerCase().endsWith("colour") || getName().toLowerCase().endsWith("colour1") || getName().toLowerCase().endsWith("colour2") ) {
				cp5.setAutoSpacing(size*2, size*2);
				o = cp5.addColorWheel(tabName, 0, 0, size*2).setWidth(size*2).setHeight(size*2).setRGB((Integer)value).setLabel(getName());
			} /*else if (getName().toLowerCase().endsWith("mode")) {
				o = cp5.addKnob(tabName).snapToTickMarks(true).setDragDirection(Knob.VERTICAL)
						.setNumberOfTickMarks((Integer)getMax())
						.setConstrained(true).setValue((Integer)value).setLabel(getName()).setRange((Integer)getMin(), (Integer)getMax()).setSize(size*2, size*2);
			} */else {
				o = cp5.addSlider(tabName).setValue((Integer)value).setLabel(getName()).setRange((Integer)getMin(), (Integer)getMax()).setSize(size*5, size);  //addCallback(this) :
			}
		} else if (value instanceof Boolean ) {
			o = cp5.addToggle(tabName).setState((Boolean)value).setLabel(getName()).setSize(size, size); //.addCallback(this) :
				/*          value instanceof PVector ?
	           cp5.addSlider(tabName + this + me.getKey()).setValue(((PVector)value).x).moveTo(tabName) :*/
		} else if (value instanceof String) {
			o = cp5.addTextfield(tabName).setSize(size*5, size).setText((String) value).setLabel(getName()).setAutoClear(false);
		} else if (value == null) {
			System.err.println("Unhandled null value for Parameter#makeController() for " + getName());
			o = null;
			return null;
		} else {
			System.err.println("Unhandled object type " + (value!=null?value.getClass():"null") + " in Parameter#makeController() for " + getName());
			o = null;
			
			return null;
		}		
		
		if (o instanceof Textfield)  {
			o.addListenerFor(cp5.ACTION_ENTER /*cp5.ACTION_CLICK*/, new CallbackListener() {			
				@Override
				public void controlEvent(CallbackEvent theEvent) {
					((Textfield)theEvent.getController()).setFocus(true);
					self.filter.sc.host.setDisableKeys(true);	// horrible hack to disable keyboard input when a textfield is selected..				
			}	
			});
			
			o.addListenerFor(cp5.ACTION_LEAVE, new CallbackListener() {			
				@Override
				public void controlEvent(CallbackEvent theEvent) {
					((Textfield)theEvent.getController()).setFocus(false);
					filter.sc.host.setDisableKeys(false);	// horrible hack to disable keyboard input when a textfield is selected..   			
			}	
			});
			
		}
		
		
		
		o.addListenerFor(cp5.ACTION_BROADCAST, new CallbackListener() {

			@Override
			public void controlEvent(CallbackEvent theEvent) {
				/*String paramName = (String)controllers.get(ev.getController());
				//println(this+ "#controlEvent(" + ev.getController() + "): paramName is " + paramName + " for " + ev.getController() + " value is " + ev.getController().getValue());
				Object currentValue = getParameterValue(paramName);*/
				String paramName = self.getName();
				Object currentValue = self.filter.getParameterValue(self.getName());
				
				self.filter.changeValueFor(currentValue,paramName,theEvent);
				if (theEvent.getController() instanceof Textfield) { // && !currentValue.equals(((Textfield)ev.getController()).getText())) {
					//sc.host.disableKeys = false;	// horrible hack to disable keyboard input when a textfield is selected..
					((Textfield)theEvent.getController()).setFocus(true);
				}				
			}
			
		});

		return o;
	}

}
