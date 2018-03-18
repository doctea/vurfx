package vurfeclipse.filters;
import controlP5.Button;
import controlP5.CColor;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ColorWheel;
import controlP5.ControlGroup;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.ControllerGroup;
import controlP5.Group;
import controlP5.ScrollableList;
import controlP5.Textfield;

import java.awt.Color;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.Map.Entry;

import vurfeclipse.parameters.Parameter;
import processing.core.PGraphics;
import processing.core.PImage;
import sun.security.provider.MD5;
import vurfeclipse.APP;
import vurfeclipse.Canvas;
import vurfeclipse.Pathable;
import vurfeclipse.Targetable;
import vurfeclipse.VurfEclipse;
import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.ui.ControlFrame;

//import java.util.Iterator;
//import java.util.Map;


public abstract class Filter implements CallbackListener, Pathable, Serializable, Mutable, Targetable {
	boolean muted = false;
	boolean started = false;

	String filterLabel = "";
	String filterName = "";

	boolean drewFrame = false;

	//  PImage buffer;

	//PGraphics out;
	//PGraphics src;
	//@Deprecated String canvas_out, canvas_in;
	//@Deprecated public transient PGraphics out;
	//@Deprecated public transient PGraphics src;
	
	String alias_out, alias_in;


	public String getAlias_in() {
		return alias_in;
	}
	public String getAlias_out() {
		return alias_out;
	}
	public Filter setAlias_in(String alias_in) {
		this.alias_in = alias_in;
		return this;
	}
	public Filter setAlias_out(String alias_out) {
		this.alias_out = alias_out;
		return this;
	}
	public Filter setAliases(String alias_out, String alias_in) {
		this.setAlias_out(alias_out);
		this.setAlias_in(alias_in);
		return this;
	}

	public PGraphics out() {
		/*Canvas canvas = sc.getCanvas(alias_out);
		//println("out() got canvas " + sc.getCanvasMapping(alias_out) + " from '" + alias_out + "'; surface is " + canvas.getSurf());
		return canvas.getSurf();*/
		return sc.getCanvas(alias_out).getSurf();
	}
	public PGraphics in() {
		/*Canvas canvas = sc.getCanvas(alias_in);
		//println("in() got canvas " + sc.getCanvasMapping(alias_in) + " from '" + alias_in + "'; surface is " + canvas.getSurf());
		return canvas.getSurf();*/
		return sc.getCanvas(alias_in).getSurf();
	}


	boolean outputDebug = true;
	public void println(String text) {		// debugPrint, printDebug -- you get the idea
		if (outputDebug) System.out.println("F " + (text.contains((this.toString())) ? text : /*this + " - " +*/ getPath() + ": "+text));
	}



	public Scene sc;

	// helper methods
	int randomInt(int r) {
		return (int)((VurfEclipse)APP.getApp()).random(r);
	}
	float random(int r) {
		return ((VurfEclipse)APP.getApp()).random(r);
	}
	float random(float r) {
		return ((VurfEclipse)APP.getApp()).random(r);
	}
	float random(float l, float h) {
		return ((VurfEclipse)APP.getApp()).random(l, h);
	}

	protected Filter(Scene sc) {
		this.sc = sc;
	}
	/*Filter(Scene sc, GLGraphicsOffScreen out, GLGraphicsOffScreen src) { //  //Filter(Scene sc, PGraphics out, PGraphics src) {
    //this.sc = sc;
    this(sc);
    this.setBuffers(out, src);
  }*/
	/*Filter(Scene sc, GLGraphicsOffScreen out, GLGraphicsOffScreen src, String filterLabel) {
    this(sc, out, src);
    this.filterLabel = filterLabel;
  }*/
	Filter(Scene sc, String filterLabel) {
		this(sc);
		this.filterLabel = filterLabel;
		this.filterName = filterLabel;
	}

	public Filter setFilterLabel (String fl) {
		this.filterLabel = fl;
		return this;
	}
	public Filter setFilterName (String fl) {
		this.filterName = fl;
		this.filterLabel = fl;
		return this;
	}

	public String getFilterName () {
		if(filterName.equals("")) {
			filterName = this.getClass().getSimpleName(); // + ((VurfEclipse)APP.getApp()).pr.getGUID();
			println("setting empty filter name to " + filterName);
		}
		return filterName;
	}

	public Collection<Parameter> getParameters () {
		if (this.parameters==null)
			this.setParameterDefaults();
		return (Collection<Parameter>) this.parameters.values();
	}

	public String getPath () {
		//println("Filter#getPath() for " + toString() + " returning '" + sc.getPath() + "/fl/" + this.getFilterName() +"'");
		return sc.getPath() + "/fl/" + this.getFilterName();
	}


	public String toString() {
		return this.getPath();
	}

	public Object getObjectForPath(String path) {
		if (debug) println("getObjectForPath " + path);
		//System.exit(1);
		String[] spl = path.split("/",2);
		if (spl[0].equals("mute")) return this;
		if (this.parameters.containsKey(spl[0])) {
			return this.getParameter(spl[0]);
		}
		return null;
	}

	/*@Deprecated public Filter setCanvases(String out, String in) {
		setOutputCanvas(out);
		setInputCanvas(in);
		//this.canvas_out = out;
    //this.canvas_in = in;
		return this;
	}
	@Deprecated public Filter setInputCanvas(String in) {
		println("Filter#setInputCanvas('" + src + "') in " + this + " replacing '" + this.canvas_in + "' with '" + in + "'");
		this.canvas_in = in;
		Canvas c = ((VurfEclipse)APP.getApp()).pr.getCanvas(canvas_in);
		if (null!=c)
			this.src = c.getSurf();
		return this;
	}
	@Deprecated public Filter setOutputCanvas(String out) {
		println("Filter#setOutputCanvas('" + out + "') in " + this + " replacing '" + this.canvas_out + "' with '" + out + "'");
		this.canvas_out = out;
		Canvas c = ((VurfEclipse)APP.getApp()).pr.getCanvas(canvas_out);
		if (null!=c)
			this.out = c.getSurf(); //pr.getCanvas(canvas_out).getSurf();
		return this;
	}*/

	/*
	 *   BUFFERS AND DRAWING
	 */

	/*
   setBuffers - sets the buffer objects out and src (you can use more than this in your filters if needed (eg Depth buffer for Kinect camera or multiple in/outs), but most filters will use just this.out and this.src)
	 */
	/*public Filter setBuffers(GLGraphicsOffScreen out, GLGraphicsOffScreen src) {
    setOutputBuffer(out);
    setInputBuffer(src);
    //this.out = out;
    //this.src = src;
    return this;
    //println("Setting out to " + out + " in " + this);
  }
  public Filter setInputBuffer(GLGraphicsOffScreen src) {
    if (src==null) {
      println("Passed null input in " + this);
      exit();
    }
    this.src = src;
    return this;
  }
  public Filter setOutputBuffer(GLGraphicsOffScreen out) {
    if (out==null) {
      println("Passed null output in " + this);
      exit();
    }
    this.out = out;
    return this;
  }*/

	/*
	 * CANVASES
	 */
	/*public Filter setCanvases(Canvas out, Canvas src) {
    setOutputBuffer(out.surf);
    setInputBuffer(src.surf);
    return this;
  }*/

	/*
    applyToBuffers - apply the filter's effects to the buffers that have been set.  actually calls abstract applyMeatToBuffers() function below which should therefore be overridden in all subclasses.
	 */
	public void applyToBuffers () {
		this.drewFrame = false;
		this.drewFrame = applyMeatToBuffers();
	}
	public abstract boolean applyMeatToBuffers(); // returns true/false to indicate if a frame was drawn

	/*
    set the buffers up for drawing and tear them down afterwards - called just before applyToBuffers, by Scene/FilterChain etc
	 */
	public void beginDraw () {
		//out.loadPixels();
		//src.loadPixels();
		//println("beginDraw in " + this + "{");
		/*if (out==null) 
			setOutputCanvas(canvas_out);
		if (src==null) 
			setInputCanvas(canvas_in);*/
		out().beginDraw();
		/*if (!out.displayable()) {
    	println("can't draw to out " + out +"!");
    }*/
		//println("} wrapped beginDraw in " + this);
	}
	public void endDraw () {
		//println("endDraw in " + this + "{");
		out().endDraw();
		//out.updatePixels();
		//println("} endDraw in " + this);
		//out.updatePixels();
	}

	/*
	 *  MODES
	 */
	public Filter nextMode() {
		//
		return this;
	}

	@Override public boolean isMuted() {
		return this.muted;
	}
	@Override public void toggleMute() {
		this.setMuted(!this.isMuted());
		//this.muted = !this.muted;
	}
	@Override public void setMuted(boolean v) {
		if (v==true && this.isMuted()!=v) {
			this.stop();
		}
		else if (v==false && this.isMuted()!=v) {
			this.start();
		}

		if (this.muteController!=null) {
			muteController.setBroadcast(false);
			muteController.setValue(v); //isMuted());
			muteController.setState(v);
			muteController.setBroadcast(true);
			//this.muteController.setValue(v);
			//println("#setMute: muteController (" + this.muteController.getLabel() + ") set to " + v);
			//System.exit(1);
		} else {
			println("#setMute: no muteController set!");
			//System.exit(1);
		}

		this.muted = v;
	}
	@Override public void setMuted() {
		this.setMuted(true);
	}

	/*
	 * old & unused?
	   public String serialize() {
		//return this.muted + ":" + this.parameters.serialize();
		String s = "{";
		if (parameters!=null) {
			Iterator i = parameters.entrySet().iterator();
			while (i.hasNext()) {
				Map.Entry me = (Map.Entry)i.next();
				s += "{ " + me.getKey() + " : " + ((Parameter)me.getValue()).value + "}, ";
			}
		}
		s += "}";
		return s;
		//return "not implemented";
	}*/

	/*
	 *
	 * PARAMETERS
	 *              */
	//HashMap parameters<Parameter> = new HashMap<Parameter> (Parameter.class);// = new HashMap();
	LinkedHashMap<String, Parameter> parameters;// = new HashMap<String,Parameter> ();

	synchronized public Object getParameterValue(String paramName) {
		if (this.parameters==null) this.setParameterDefaults();
		//if (this.parameters.size()==0) setParameterDefaults();
		//println("looking for " + paramName);
		//if (this.parameters.containsKey(paramName))
		return this.parameters.get(paramName).value;
		//else
		//  return "unknown";
	}
	synchronized private Filter setParameterValue(String paramName, Object value) {
		if (this.parameters==null) this.setParameterDefaults();
		//println("setParameterValue ('"+ paramName + "', '" + value + "')");
		//if (this.parameters.size()==0) setParameterDefaults();
		//parameters.put(paramName, value);
		//parameters.put(paramName, new Parameter(paramName, value));//, min, max));
		//if (parameters.containsKey(paramName)) {
		//parameters.get(paramName).value = value;
		if (parameters.containsKey(paramName))
			parameters.get(paramName).setValue(value);
		else
			println("no parameter for " + paramName + "(tried to set value " + value + ")");
		/*} else {
     parameters.put(paramName, new Parameter(paramName, value));
     }*/
		return this;
	}
	synchronized private Filter setParameterValueFromSin(String paramName, Object f) {
		if (this.parameters==null) this.setParameterDefaults();
		try {
			parameters.get(paramName).setValueFromSin((Float)f);
		} catch (NullPointerException e) {
			println("Error - caught null pointer exception trying to set '" + paramName + "' to " + f + " in " + this);
		} catch (ClassCastException e) {
			println("Error - caught trying to set " + paramName + " to a " + f.getClass());
		}
		return this;
	}

	// public void setParameterValue

	synchronized public Filter changeParameterValueFromSin(String paramName, float s) {
		setParameterValueFromSin(paramName, s);
		updateParameterValue(paramName, getParameterValue(paramName));
		return this;
	}
	synchronized public Filter changeParameterValue(String paramName) {
		changeParameterValue(paramName, parameters.get(paramName).value);
		return this;
	}
	synchronized public Filter changeParameterValue(String paramName, Object value) {
		setParameterValue(paramName, value);
		updateParameterValue(paramName, value);
		return this;
	}
	synchronized public void toggleParameterValue(String paramName) {
		// TODO Auto-generated method stub
		if (getParameterValue(paramName) instanceof Boolean) {
			changeParameterValue(paramName, !(Boolean)getParameterValue(paramName));
		}
	}

	synchronized public Filter addParameter(Parameter parameter, String paramName, Object value) {
		if (this.parameters==null) this.setParameterDefaults();
		parameters.put(paramName, parameter);
		updateParameterValue(paramName, value);
		return this;
	}

	synchronized public Filter addParameter(String paramName, Object value, Object min, Object max) {
		println(this + "#addParameter(" + paramName + ", " + value + ", " + min + ", " + max + "): " + this.getFilterLabel());
		return addParameter(new Parameter(this, paramName, value, min, max), paramName, value);
	}
	synchronized public Filter addParameter(String paramName, Object value) {
		if (value instanceof Float) {
			return addParameter(paramName, value, -50.0f, 50.0f);
		} 
		return addParameter(paramName, value, -100, 100);
	}

	synchronized public void updateAllParameterValues() {
		if (this.parameters==null) this.setParameterDefaults();
		for (Entry<String, Parameter> me : parameters.entrySet()) {
			this.updateParameterValue((String)me.getKey(), ((Parameter)me.getValue()).value);
		}
	}

	synchronized public void updateParameterValue(String paramname, Object value) {
		// set parameter Control

		//sc.myTextarea.setText(serialize());
		//sc.updateSerializeBox();
		if(paramname.equals("mute")) {	// WAS "muted" until 2017-11-06 !!! might be wrong/breaking stuff like this
			this.muted = (Boolean) value;
		}
	}

	synchronized public void resetParameters() {
		Iterator<Parameter> it = parameters.values().iterator();
		while (it.hasNext()) {
			Parameter p = it.next();//
			p.reset();
		}
		this.updateAllParameterValues();
	}


	synchronized public void setParameterDefaults () {
		println("setParameterDefaults in " + this);
		if (this.parameters==null) {
			parameters = new LinkedHashMap<String, Parameter>();//String.class,Parameter.class);
		} else {
			resetParameters();
		}
	}

	synchronized public HashMap<String,Object> getPresetValues () {
		HashMap<String,Object> h = new HashMap<String,Object>();
		for (Map.Entry<String,Parameter> me : parameters.entrySet()) {
			h.put(me.getKey(), me.getValue().value);
		}
		h.put("muted", this.muted);
		return h;
	}


	/*
	 * INIT
	 */
	//abstract public boolean initialise ();
	public boolean initialise () {
		//setParameterDefaults();
		this.setMuted(this.muted);	// 2017-10-29
		
		//if (filters[i].out==null) filters[i].setOutputCanvas(getCanvasMapping("out"));
		//if (filters[i].src==null) filters[i].setInputCanvas(getCanvasMapping("src"));
		
		/*if (getOut()==null) {
			println("out is null in " + this + ", canvas_out is " + canvas_out + " setting to default " + sc.getCanvasMapping("src"));
			//setOutputCanvas(sc.getCanvasMapping("out"));
		}
		if (getIn()==null) {
			println("src is null in " + this + ", canvas_in is " + canvas_in + " setting to default " + sc.getCanvasMapping("src"));
			setInputCanvas(sc.getCanvasMapping("src"));
		}*/
		if (this.getAlias_out()==null) {
			this.setAlias_out("out");
			out();
		}
		if (this.getAlias_in()==null) {
			this.setAlias_in("src");
			in();
		}
		
		return true;
	}

	//abstract public void setupFilters ();

	public boolean stop() {    // stop processing when eg muted
		return true;
	}
	public boolean start() {  // stop processing when eg unmuted REMEMBER TO FIGURE OUT WHERE WE HAVE TO CALL THIS IN OUR PROCESSING LOOPS..!
		println("start()");
		//this.setInputCanvas(this.canvas_in);
		//this.setOutputCanvas(this.canvas_out);
		return true;
	}


	// {
	//return true;
	//}

	//abstract public void applyMeat();
	// {
	// do nothing in this superclass
	//}

	public PImage getFinalImage() {
		return this.out().get(0, 0, sc.w, sc.h);
	}

	public void drawLayerText (String label) {
		int lineCount = 2;
		int x = sc.w/2;
		PGraphics out = out();
		out.pushStyle();
		out.fill(128);
		out.text(label, x-150, lineCount*20);
		out.fill(255);
		out.text(label, x-149, (lineCount*20)+1);
		out.fill(0);
		out.text(label, x-148, (lineCount*20)+2);
		out.popStyle();
	}


	public String getFilterLabel() {
		if (this.filterLabel.equals("")) filterLabel = this.getClass().getSimpleName() + ((VurfEclipse)APP.getApp()).pr.getGUID(this.getClass().getSimpleName()); //toString();//.replace("@","-");

		return this.filterLabel.equals("")?this.toString():this.filterLabel;
	}
	public String getDescription () {
		return (this.filterLabel!=""?"'"+filterLabel+"': ":"") +
				this.toString() + " " + (this.isMuted()?"MUTED":"");
				//+ " (out: " + out + ", src: " + src + ")";
	}

	public void dispose() {
		/*this.stop();
		for (Controller c : this.controllers.keySet()) {
			c.remove();
		}
		this.controllers.clear();// = null;
		this.controllerMapping.clear();// = null;
		this.muteController.remove();
		this.nextModeButton.remove();
		this.moveDownButton.remove();
		this.moveUpButton.remove();
		this.lstInputCanvas.remove();
		this.lstOutputCanvas.remove();
		this.cloneButton.remove();
		this.deleteButton.remove();
		this.muteController = null;
		this.nextModeButton = null;
		this.moveDownButton = null;
		this.moveUpButton = null;
		this.lstInputCanvas = null;
		this.lstOutputCanvas = null;
		this.cloneButton = null;
		this.deleteButton = null;
		 */
	}


	//HashMap controllers<Controller,String> = new HashMap<Controller,String> (Controller.class,String.class);
	transient HashMap<controlP5.Controller,String> controllers = new HashMap<controlP5.Controller,String>();
	transient HashMap<String,controlP5.Controller> controllerMapping = new HashMap<String,controlP5.Controller>();

	transient controlP5.Toggle muteController;
	transient controlP5.Button nextModeButton;


	@Override
	@Deprecated
	public void controlEvent (CallbackEvent ev) {
		//println(this + " got event " + ev + " : " + ev.getController() + ev.getController().getValue());
		if (!ev.getController().isUserInteraction()) return;
		//println(this + " got event " + ev + " : " + ev.getController() + ev.getController().getValue());
		/*		if (ev.getController()==this.muteController &&
				//ev.getAction()==ControlP5.ACTION_RELEASED || ev.getAction()==ControlP5.ACTION_RELEASEDOUTSIDE || 
				ev.getAction()==ControlP5.ACTION_PRESS) {
			println("Setting mute state on " + this + " to " + muteController.getState());
			this.setMuted(muteController.getState());
		} else if (ev.getController()==this.nextModeButton && ev.getAction()==ControlP5.ACTION_BROADCAST) {
			this.nextMode();
			
		} else */
			/*if (controllers.containsKey(ev.getController()) &&
				(ev.getController().isUserInteraction() && (ev.getAction()==ControlP5.ACTION_BROADCAST)) //
					//|| ev.getAction()==ControlP5.ACTION_DRAG || ev.getAction()==ControlP5.ACTION_RELEASE || ev.getAction()==ControlP5.ACTION_RELEASE_OUTSIDE || ev.getAction()==ControlP5.ACTION_PRESS)) //|| ev.getAction()==ControlP5.ACTION_BROADCAST)
				) {
			String paramName = (String)controllers.get(ev.getController());
			//println(this+ "#controlEvent(" + ev.getController() + "): paramName is " + paramName + " for " + ev.getController() + " value is " + ev.getController().getValue());
			Object currentValue = getParameterValue(paramName);
			changeValueFor(currentValue,paramName,ev);
			if (ev.getController() instanceof Textfield) { // && !currentValue.equals(((Textfield)ev.getController()).getText())) {
				//sc.host.disableKeys = false;	// horrible hack to disable keyboard input when a textfield is selected..
				((Textfield)ev.getController()).setFocus(true);
			}
		} else*/ /*if (controllers.containsKey(ev.getController()) && ev.getController().isUserInteraction()) {
			if (ev.getController() instanceof Textfield) {
				if (ev.getAction()==ControlP5.ACTION_ENTER || ev.getAction()==ControlP5.ACTION_CLICK) {
					((Textfield)ev.getController()).setFocus(true);
					sc.host.setDisableKeys(true);	// horrible hack to disable keyboard input when a textfield is selected..
				} else if (ev.getAction()==ControlP5.ACTION_LEAVE) {
					((Textfield)ev.getController()).setFocus(false);
					sc.host.setDisableKeys(false);	// horrible hack to disable keyboard input when a textfield is selected..    			
				}
			}
		} else {*/		
		/*f (controllers!=null) {
			String paramName = (String)controllers.get(ev.getController());
		}*/
			//println("UNHANDLED CONTROL EVENT in " + this + "#controlEvent(" + ev.getController() + "): paramName is " + paramName + " for " + ev.getController() + " value is " + ev.getController().getValue() + " action is " + ev.getAction());
		//}
		/*else if (ev.getAction()==ControlP5.ACTION_PRESSED) {
      if (controllers.containsKey(ev.getController())) {
        println("getcontroller is " + ev.getController());
        if (ev.getController() instanceof Slider) {
          println("is slider");
          String paramName = (String)controllers.get(ev.getController());
          Object currentValue = getParameterValue(paramName) ;
          changeValueFor(currentValue,paramName,ev);
        }
      }
    }*/
	}
	public void changeValueFor(Object currentValue, String paramName, CallbackEvent ev) {
		if (currentValue instanceof Boolean) {
			this.changeParameterValue(paramName, ev.getController().getValue()==1.0f);
		}
		else if (currentValue instanceof Float) {
			this.changeParameterValue(paramName, (Float)ev.getController().getValue());
		}
		else if (currentValue instanceof Integer) {
			this.changeParameterValue(paramName, (int)ev.getController().getValue());
		} else if (currentValue instanceof String) {
			this.changeParameterValue(paramName, ((Textfield)ev.getController()).getText());
		}
		else {
			this.changeParameterValue(paramName, ev.getController().getValue());
		}
	}

	int count = 0;
	boolean controlsSetup = false;
	private Button moveUpButton;
	private Button moveDownButton;
	private ScrollableList lstInputCanvas;
	private ScrollableList lstOutputCanvas;
	protected boolean debug = false;
	private Button cloneButton;
	private Button deleteButton;

	public synchronized int setupControls(ControlFrame cf, ControllerGroup tab, int row) {
		final ControlP5 cp5 = cf.control();
		//if (controlsSetup) return 0;
		controlsSetup = true;
		if (debug) println("Filter#setupControls() for "  + this + ": " + tab.getName());
		/*if (count++>20) {
      println("Exiting because setupControls count is " + count + " in " + this);
      System.exit(0);
    }*/

		int size = 20;
		//cp5.addCallback(this);	// don't need this anymore as callbacks are handled by individual controllers
		//cp5.addTextlabel(tabName + this.toString()).setText(getFilterLabel()).linebreak();

		/*Group grp = cp5.addGroup("group_" + tab.getName() + "_" + getFilterName()).moveTo(tab);
    grp.setLabel(getFilterName());
    cp5.addTextlabel("label " + grp.getName(), getFilterName()).setGroup(grp).moveTo(grp).linebreak()
    		.setText("ffs?").setLabel("wtaf?").getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER);

    grp.setTitle("asdfasdf");*/
		ControllerGroup grp = (ControllerGroup) tab;

		int margin_h = 40;
		int margin_w = 5;
		/*int row = 0,*/float col = 0;
		int row_h = 50, col_w = 100;

			
			final Filter self = this;
			
			if (row!=0) {
				this.moveUpButton = cp5.addButton("moveup_" + tab.getName() + getFilterName())
						.setLabel("^")
						.setSize(size, size)
						.setPosition(margin_w + (col*col_w),margin_h + (row*row_h)-3)
						.setHeight(12)
						.moveTo(grp)
						.addListenerFor(cp5.ACTION_BROADCAST, new CallbackListener() {
							@Override
							public void controlEvent(CallbackEvent theEvent) {
								self.sc.moveFilter(self, -1);
								self.sc.refreshControls();
							}					
						})
						;
			}

			this.moveDownButton = cp5.addButton("movedown_" + tab.getName() + getFilterName())
					.setLabel("^")
					.setSize(size, size)
					.setPosition(margin_w + (col*col_w),margin_h + (row*row_h)+13)
					.setHeight(12)
					.moveTo(grp)
					.addListenerFor(cp5.ACTION_BROADCAST, new CallbackListener() {
						@Override
						public void controlEvent(CallbackEvent theEvent) {
							self.sc.moveFilter(self, 1);
							self.sc.refreshControls();
						}					
					})
					;

		
			this.muteController = cp5.addToggle("mute_" + tab.getName() + getFilterName())
					.setPosition(this.moveDownButton.getWidth()+this.moveDownButton.getPosition()[0]+margin_w/*(margin_w*4) + (col*(col_w+margin_w)) + size + 5*/,margin_h + (row*row_h))
					.setLabel("Mute " + this.getFilterLabel())
					.setSize(size*2, size)
					.setValue(this.isMuted())
					.setState(this.isMuted())
					.moveTo(grp)
					.addListenerFor(cp5.ACTION_BROADCAST, new CallbackListener() {
						@Override
						public void controlEvent(CallbackEvent theEvent) {
							/*ev.getAction()==ControlP5.ACTION_RELEASED || ev.getAction()==ControlP5.ACTION_RELEASEDOUTSIDE || */
							//ev.getAction()==ControlP5.ACTION_PRESS) {
							println("Setting mute state on " + this + " to " + muteController.getState());
							self.setMuted(muteController.getState());							
						}
					});

			this.muteController.getValueLabel().align(ControlP5.CENTER, ControlP5.CENTER).setPaddingY(2*cp5.getFont().getHeight());
			this.muteController.getCaptionLabel().align(ControlP5.LEFT, ControlP5.TOP_OUTSIDE).setPaddingY(2*cp5.getFont().getHeight());//.setPaddingY(cp5.getFont().getHeight());

			this.nextModeButton = cp5.addButton("nextmode_" + tab.getName() + getFilterName() + " " + getPath())
					.setLabel(">|")
					.setSize(size, size)
					.setPosition(this.muteController.getWidth()+this.muteController.getPosition()[0]+margin_w/*(margin_w*2) + (col*(col_w+margin_w)) + size + 5*/,margin_h + (row*row_h))
					.moveTo(grp)
					.addListenerFor(cp5.ACTION_BROADCAST, new CallbackListener() {
						@Override
						public void controlEvent(CallbackEvent theEvent) {
							self.nextMode();
						}						
					})
					;
			
			
		/*cp5.addLabel("canvases_" + tab.getName() + getFilterName())
			.setValueLabel("in => " + this.canvas_in + "\nout => " + this.canvas_out)
			.setPosition(this.nextModeButton.getWidth()+this.nextModeButton.getPosition()[0]+margin_w,margin_h + (row*row_h))
			.moveTo(grp);*/
			/*CallbackListener toFront = new CallbackListener() {
				public void controlEvent(CallbackEvent theEvent) {
					theEvent.getController().bringToFront();
					((ScrollableList)theEvent.getController()).open();
				}
			};

			CallbackListener close = new CallbackListener() {
				public void controlEvent(CallbackEvent theEvent) {
					((ScrollableList)theEvent.getController()).close();
				}
			};*/
			
			
		String[] canvases = sc.getCanvasMappings().keySet().toArray(new String[0]);

		/*new ScrollableList(cp5,"test_" + tab.getName())
			.moveTo(grp)
			.addItems(canvases) //new String[] { "test", "another", "value", "blah" })
			.setPosition(0,40)
			.onLeave(close)
			.onEnter(toFront)
			.close()			
			//.setHeight(10)
			.setWidth(size*2)
			//.setBarHeight(10)
			//.setItemHeight(10)	
		; */

		lstInputCanvas = new ScrollableList(cp5,"canvas_input_" + tab.getName() + getFilterName())
			.setLabel(this.getAlias_in()) //sc.getMappingForCanvas(this.canvas_in))
			.addItems(canvases)
			.setPosition(this.nextModeButton.getWidth()+this.nextModeButton.getPosition()[0]+margin_w,margin_h + (row*row_h)-3)
			//.setHeight(10)	// this breaks the dropdown!
			.setWidth(size*2)
			.setBarHeight(10)
			.setHeight(10 * 4)
			.setItemHeight(10)
			.moveTo(grp)
			.addListenerFor(cp5.ACTION_BROADCAST, new CallbackListener() {
				@Override
				public void controlEvent(CallbackEvent theEvent) {
					int index = (int) ((ScrollableList)theEvent.getController()).getValue();
					Map<String,Object> selected = ((ScrollableList)theEvent.getController()).getItem(index);
					String mapName = (String) selected.get("name");
					//String canvas_name = sc.getCanvasMapping(mapName); 
					//self.setInputCanvas(canvas_name);
					self.setAlias_in(mapName);
				}				
			})
			.onLeave(cf.close)
			.onEnter(cf.toFront)
			.close()
			;
		//grp.add(lstInputCanvas);
		
		/*
		Color c = APP.getApp().createDefaultColorFromName(this.filterName);
		grp.setColor(new CColor(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha(), col_w)); //
		*/
		

		lstOutputCanvas = new ScrollableList(cp5,"canvas_out_" + tab.getName() + getFilterName())
			.setLabel(this.getAlias_out()) //sc.getMappingForCanvas(this.canvas_out))
			//.addItems(canvases)
			.addItems(canvases)
			//.setHeight(10)
			.setBarHeight(10)
			.setWidth(size*2)
			.setHeight(10 * 4)
			.setItemHeight(10)
			.setPosition(this.nextModeButton.getWidth()+this.nextModeButton.getPosition()[0]+margin_w,margin_h + (row*row_h)+13)
			.moveTo(grp)
			.addListenerFor(cp5.ACTION_BROADCAST, new CallbackListener() {
				@Override
				public void controlEvent(CallbackEvent theEvent) {
					int index = (int) ((ScrollableList)theEvent.getController()).getValue();
					Map<String,Object> selected = ((ScrollableList)theEvent.getController()).getItem(index);
					String mapName = (String) selected.get("name");
					//String canvas_name = sc.getCanvasMapping(mapName); 
					//self.setOutputCanvas(canvas_name);
					self.setAlias_out(mapName);
				}				
			})
			.onLeave(cf.close)
			.onEnter(cf.toFront)
			.close()
			;

		
		boolean enableClone = true;
		if (enableClone ) {	// 2018-03-02, delete button doesn't work so remove them for now
			cloneButton = cp5.addButton("clone_"+ tab.getName() + getFilterName())
					.setLabel("clone")
					.setSize(size, size)
					.setPosition(lstOutputCanvas.getPosition()[0] + (size*2.5f) + (col*col_w),margin_h + (row*row_h)-5)
					.setHeight(12)
					.moveTo(grp)
					.addListenerFor(cp5.ACTION_BROADCAST, new CallbackListener() {
						@Override
						public void controlEvent(CallbackEvent theEvent) {
							//self.sc.moveFilter(self, -1);
							//self.sc.refreshControls();
	
							final HashMap<String,Object> setup = self.collectFilterSetup();
							final Filter newf = Filter.createFilter(self.getClass().getName(), self.sc);
							final String newName = "copy of " + self.getFilterName();
							
							sc.queueUpdate(new Runnable() {
								@Override
								public void run() {								
									println("CLONING!  new name is " + newName);
									newf.setFilterName(newName).readSnapshot(setup).setFilterName(newName);
									
									//synchronized(self) {
									sc.addFilter(newf);
									newf.initialise();
									newf.start();
									sc.refreshControls();
								}});
							//}
						}					
					});
			
			deleteButton = cp5.addButton("delete_"+ tab.getName() + getFilterName())
					.setLabel("delete")
					.setSize(size, size)
					.setPosition(lstOutputCanvas.getPosition()[0] + (size*2.5f) + (col*col_w),margin_h + (row*row_h)+15)
					.setHeight(12)
					.moveTo(grp)
					.addListenerFor(cp5.ACTION_BROADCAST, new CallbackListener() {
						@Override
						public void controlEvent(CallbackEvent theEvent) {
							sc.queueUpdate(new Runnable() {
								@Override
								public void run() {								
									println("DELETING " + self); //!  new name is " + newName);
									//self.lstOutputCanvas.getParent().remove();
									/*for (Entry<Controller, String> c : self.controllers.entrySet() ) {
										((Controller)c.getKey()).getParent().remove();
									}*/
									//self.controllers.clear();
									//self.controllerMapping.clear();
									sc.removeFilter(self);
									/*APP.getApp().getCF().queueUpdate(new Runnable() {
										@Override
										public void run() {*/
											self.removeControls(cp5);
											sc.refreshControls();											
										/*}
									});*/
								}});
							//}
						}					
					});
		}
		
		
		int param_start_w = margin_w*15;
		
		//cp5.addTextlabel("path_" + tab.getName() + getFilterName(), "Path: " + this.getPath()).setSize(size, size).moveTo(grp);//.linebreak();

		col = 1;

		if (parameters==null)
			setParameterDefaults();

		for (Map.Entry<String,Parameter> me : parameters.entrySet()) {
			//this.updateParameterValue((String)me.getKey(), me.getValue());
			//Object value = me.getValue();
			Parameter param = (Parameter)me.getValue();
			if (debug) println("Filter#setupControls() in " + toString() + " doing control for " + param.getName());
			//Object value = param.value;

			controlP5.Controller o = param.makeController(cp5, tab.getName() + this + me.getKey(), tab, size);

			/*controlP5.Controller o =
        value instanceof Float ?
          cp5.addSlider(tab.getName() + this + me.getKey()).setValue(
        		  (Float)(Float)value).setLabel(me.getKey().toString())
          			.setSliderMode(Slider.FLEXIBLE)
        		  .setRange(
        				  new Float((Float)param.getMin()),
        				  new Float((Float)param.getMax())
        			)
        			.setSize(size*5, size) : //.addCallback(this) :
        value instanceof Integer ? (
        		(Integer)param.getMax()==360 ?
        				cp5.addKnob(tab.getName() + this + me.getKey()).setValue((Integer)value).setLabel(me.getKey().toString()).setRange((Integer)param.getMin(), (Integer)param.getMax()).setSize(size, size)
        				:
        				cp5.addSlider(tab.getName() + this + me.getKey()).setValue((Integer)value).setLabel(me.getKey().toString()).setRange((Integer)param.getMin(), (Integer)param.getMax()).setSize(size*5, size)  //addCallback(this) :
        ) :
        value instanceof Boolean ?
          cp5.addToggle(tab.getName() + this + me.getKey()).setState((Boolean)value).setLabel(me.getKey().toString()).setSize(size, size) : //.addCallback(this) :
          //          value instanceof PVector ?
           //cp5.addSlider(tabName + this + me.getKey()).setValue(((PVector)value).x).moveTo(tabName) :
        value instanceof String ?
        		cp5.addTextfield(tab.getName() + this + me.getKey()).setSize(size*5, size).setText((String) value).setLabel(me.getKey().toString()) :
          null
          //
      ;*/

			param.setFilterPath(this.getPath());
			if (debug) println("Filter: adding control object for filter with path " + this.getPath());


			//col = 2;
			//row--;

			//o.linebreak();
			//param.controller = o;
			//param.setController(o);
			//o.addCallback(this);
			if (o!=null) {
				o.getValueLabel().align(ControlP5.CENTER, ControlP5.CENTER);//.setPaddingY(2*cp5.getFont().getHeight());
				o.getCaptionLabel().align(ControlP5.CENTER, ControlP5.TOP_OUTSIDE);//.setPaddingY(cp5.getFont().getHeight());
				o.setPosition(
						param_start_w + margin_w/2 + (col++*(margin_w+col_w)),
						margin_h + (row*row_h)
						);
				if (o.getWidth() < col_w/2) col -= 0.5f;
				if (col > 11) { //(col_w*margin_w)*col>cf.width) { //(5*(cf.width/col_w))) {
					col = 1;
					row++;
				}
				this.setControllerMapping(param.getName(),o);

				o.moveTo(grp);

				/*if (o.getAbsolutePosition()[0]+(o.getWidth()*2) >= cf.width) {// fuzzy linebreak if gonna go off the edge of the window
        	println ("linebreaking because controller width " + o.getAbsolutePosition()[0]+(o.getWidth()*2) + " is more than frame width" + cf.width + "?");
        	//o.linebreak();
        }*/
				//if (!i.hasNext()) { o.linebreak();}// add a linebreak if its the last one

				//o.linebreak();	// removed 2017-09-22 to make layout loads better !!!
				/*controllers.put(
          o, (String)me.getKey()
        );*/
				if (debug) println(this + ": set up Control for " + me.getKey() + " " + o.getClass()); // + " (which shouldnt differ from " + param.getName() + " if i've understood my own code .. )");
			}
			// }
		}
		row++;

		((ControlGroup<Group>) tab).setBackgroundHeight(margin_h + (row * row_h));
		tab.setColorBackground((int) random(255));
		tab.setSize((int) (margin_w + (col++*col_w)),margin_h + (row * row_h));
		//cp5.linebreak();
		
		tab.bringToFront();

		return row;
	}

	public void removeControls(ControlP5 cp5) {
		synchronized (cp5) {

			APP.getApp().getCF().queueUpdate(new Runnable() {

				@Override
				public void run() {
					for (Controller c : controllers.keySet()) {
						c.remove();
					}
					controllers.clear();// = null;
					controllerMapping.clear();// = null;
					
					if (muteController!=null) muteController.remove();
					if (nextModeButton!=null) nextModeButton.remove();
					if (moveDownButton!=null) moveDownButton.remove();
					if (moveUpButton!=null) moveUpButton.remove();
					if (lstInputCanvas!=null) lstInputCanvas.remove();
					if (lstOutputCanvas!=null) lstOutputCanvas.remove();
					if (cloneButton!=null) cloneButton.remove();
					if (deleteButton!=null) deleteButton.remove();
					if (muteController!=null) muteController = null;
					if (nextModeButton!=null) nextModeButton = null;
					if (moveDownButton!=null) moveDownButton = null;
					if (moveUpButton!=null) moveUpButton = null;
					if (lstInputCanvas!=null) lstInputCanvas = null;
					if (lstOutputCanvas!=null) lstOutputCanvas = null;
					if (cloneButton!=null) cloneButton = null;
					if (deleteButton!=null) deleteButton = null;					
				}				
			});

		}
	}
	synchronized public void setControllerMapping(String paramName, controlP5.Controller o) {
		if (controllerMapping==null) controllerMapping = new HashMap<String,controlP5.Controller> ();
		controllerMapping.put(paramName, o);
		controllers.put(o, paramName);
	}

	public synchronized void updateControl (String name, Object value) {
		//(((controlP5.Controller)controllerMapping.get(name))).setValue(value);
		if (controllerMapping==null) controllerMapping = new HashMap<String,controlP5.Controller> ();
		controlP5.Controller c = (controlP5.Controller)controllerMapping.get(name);

		if (c!=null) {
			c.setBroadcast(false);
			if (value instanceof Float)
				c.setValue((Float)value);
			else if (value instanceof Integer) {
				if (c instanceof ColorWheel) {
					//System.err.println(this + " for " + name + " with value '"+value+"': funky control event loopback bug means that can't manually change the colour...");
					if ((Integer)value!=-1 && (Integer)value!=-16777216) {	// TODO: hacky workaround for bug 'greyscale trap' where you can't move the colour selector UI out of the grey range if certain values get set?
						((ColorWheel)c).setRGB((Integer) value);	// some funky loopback bug
					}
				} else {
					c.setValue((Integer)value);
				}
			}
			else if (value instanceof Boolean)
				c.setValue((Boolean)value?1.0f:0.0f);
			else if (value instanceof String) 
				c.setStringValue((String) value);
			else 
				System.err.println("Caught updating control with unhandled type " + value.getClass() + " in " + this);
			c.setBroadcast(true);
		}

	}


	synchronized public Object target(String path, Object payload) {
		if (debug) println("#target('"+path+"', '"+payload+"'");
		if ("/mute".equals(path.substring(path.length()-5, path.length()))) {
			this.toggleMute();
			return this.isMuted()?"Muted":"Unmuted";
		} else if ("/nextMode".equals(path.substring(path.length()-9, path.length()))) {
			this.nextMode();
			return "nextMode on " + this;
		} else {
			println("got path that i dunno what to do with '"+path+"'");
		}
		return payload;
	}

	public HashMap<String, Targetable> getCustomTargetURLs() {
		return new HashMap<String,Targetable>();
	}

	synchronized public HashMap<String, Targetable> getTargetURLs() {
		HashMap<String, Targetable> urls = new HashMap<String,Targetable> ();
		Filter f = this;

		urls.put(f.getPath() + "/mute", f);
		urls.put(f.getPath() + "/nextMode", f);
		if (debug) println("added Filter's url '" + f.getPath() + "/mute' mapped to " + f);

		Iterator<Parameter> pit = f.getParameters().iterator();
		while (pit.hasNext()) {
			Parameter p = pit.next();
			if (debug) println("added Parameter's url '" + p.getPath() + "' mapped to " + p);
			urls.put(p.getPath(), p);
			/*if (p.getName().equals("text")) {
				if (debug) println("got text!");
				//System.exit(0);
			}*/
		}

		urls.putAll(getCustomTargetURLs());

		return urls;
	}
	synchronized public void randomiseParameters(Sequence seq,String[] parameters) {
		for (String p : parameters ) {
			if (debug) println("Randomising parameter " + p + " in " + this.toString());
			this.setParameterValueFromSin(p, seq.random(0f, 2f)-1f);
		}		
	}
	synchronized public Parameter getParameter(String name) {
		if (this.parameters==null) 
			this.setParameterDefaults();
		return this.parameters.get(name);
		//return null;
	}

	public HashMap<String,Object> collectFilterSetup() {	// for saving snapshots, save setup of filter
		HashMap<String,Object> output = new HashMap<String,Object>();

		output.put("class", this.getClass().getName());
		output.put("name", this.getFilterName());
		output.put("label", this.getFilterLabel());
		output.put("description", this.getDescription());
		output.put("path", this.getPath());
		//output.put("canvas_out", this.getOutputCanvas());
		//output.put("canvas_src", this.getSourceCanvas());
		output.put("alias_out", this.getAlias_out());
		output.put("alias_in", this.getAlias_in());

		output.put("parameter_defaults", this.collectParameterSetup());

		return output;
	}
	protected HashMap<String,Object> collectParameterSetup() {
		HashMap<String,Object> output = new HashMap<String,Object> ();
		for (Parameter p : this.getParameters()) {
			output.put(p.getPath(), p.getParameterSetup());//.getDefaultValue());
		}
		return output;
	}
	
	@SuppressWarnings("unchecked")
	public Filter readSnapshot(Map<String, Object> input) {
		this.setFilterName((String) 	input.get("name"));
		this.setFilterLabel((String) 	input.get("label"));
		//this.setDescription(input.get("description"));
		if (input.containsKey("canvas_out")) {	// backwards compatibility
			//this.setOutputCanvas((String) 	input.get("canvas_out"));
			this.setAlias_out(sc.getMappingForCanvas((String)input.get("canvas_out")));
		}
		if (input.containsKey("canvas_src")) {	// backwards compatibility
			//this.setInputCanvas((String) 	input.get("canvas_src"));	
			this.setAlias_in(sc.getMappingForCanvas((String)input.get("canvas_src")));
		}
		
		if (input.containsKey("alias_out")) this.setAlias_out((String)input.get("alias_out"));
		if (input.containsKey("alias_in")) this.setAlias_in((String)input.get("alias_in"));

		for (Entry<String, Object> p : ((Map<String, Object>) input.get("parameter_defaults")).entrySet()) {
			Map<String,Object> para = (Map<String, Object>) p.getValue();
			this.addParameter((String) para.get("name"), para.get("default"), para.get("min"), para.get("max"));
		}		
		
		return this;
	}

	public static Filter createFilter(String classname, Scene host) {
		try {
			Class clazz = Class.forName(classname);
			//System.out.println (clazz.getConstructors());
			//Constructor<?> ctor = clazz.getConstructors()[0]; //[0]; //Scene.class, Integer.class);
			System.out.println("Filter#createFilter(): about to try and get constructor for classname '" + classname + "'");
			if (classname.contains("$")) {	// its an inner class
				String[] spl = classname.split("\\$");
				clazz = Class.forName(spl[0]); //Class.forName(classname); host.getClass();//
				Class<?> inner = Class.forName(classname);
				//System.out.println (clazz.getConstructors());
				//Constructor<?> ctor = clazz.getConstructors()[0]; //[0]; //Scene.class, Integer.class);
				Constructor<?> ctor = inner.getConstructor(clazz); //Scene.class,Integer.TYPE);
				//Object seq = ctor.newInstance(); //(Scene)null, 0);
				ctor.setAccessible(true);
				Filter filt = (Filter) ctor.newInstance(host); //(Scene)null, (int)0);
				filt.sc = host;
				return filt;
			} else {
				Constructor<?> ctor = clazz.getConstructor(Scene.class); //Scene.class,Integer.TYPE);
				Filter filt = (Filter) ctor.newInstance(host); //(Scene)null, (int)0);
				filt.sc = host;
				return filt;
			}
			//Object seq = ctor.newInstance(); //(Scene)null, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/*	public void changeCanvas(String oldCanvasPath, String canvasPath) {
		if (this.lstInputCanvas!=null) 	this.lstInputCanvas.setItems(sc.getCanvasMappings().keySet().toArray(new String[0]));
		if (this.lstOutputCanvas!=null) this.lstOutputCanvas.setItems(sc.getCanvasMappings().keySet().toArray(new String[0]));
	}*/
	

}

