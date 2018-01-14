package vurfeclipse.filters;
import vurfeclipse.*;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ColorWheel;
import controlP5.ControlGroup;
import controlP5.ControlP5;
import controlP5.ControllerGroup;
import controlP5.Group;
import controlP5.Slider;
import controlP5.Tab;
import controlP5.Textfield;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.Map.Entry;

import vurfeclipse.parameters.Parameter;
import processing.core.PGraphics;
import processing.core.PImage;
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
  String canvas_out, canvas_in;
  public transient PGraphics out;
  public transient PGraphics src;


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
  Filter(Scene sc, String out, String src) {
    this(sc);
    this.setCanvases(out,src);
  }
  Filter(Scene sc, String out, String src, String filterLabel) {
    this(sc, out, src);
    this.filterLabel = filterLabel;
    this.filterName = filterLabel;
  }
  /*Filter(Scene sc, GLGraphicsOffScreen out, GLGraphicsOffScreen src, String filterLabel) {
    this(sc, out, src);
    this.filterLabel = filterLabel;
  }*/
  Filter(Scene sc, String filterLabel) {
    this(sc);
    this.filterLabel = filterLabel;
    this.filterName = filterLabel;
  }

  public void setFilterLabel (String fl) {
    this.filterLabel = fl;
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
	  return this.parameters.values();
  }

  public String getPath () {
    //println("Filter#getPath() for " + toString() + " returning '" + sc.getPath() + "/fl/" + this.getFilterName() +"'");
    return sc.getPath() + "/fl/" + this.getFilterName();
  }


  public String toString() {
	  return this.getPath();
  }

  public Object getObjectForPath(String path) {
	  System.out.println("getObjectForPath " + path);
	  //System.exit(1);
    String[] spl = path.split("/",2);
    if (spl[0].equals("mute")) return this;
    if (this.parameters.containsKey(spl[0])) {
      return this.getParameter(spl[0]);
    }
    return null;
  }

  public Filter setCanvases(String out, String in) {
    setOutputCanvas(out);
    setInputCanvas(in);
    /*this.canvas_out = out;
    this.canvas_in = in;*/
    return this;
  }
  public Filter setInputCanvas(String in) {
    println("Filter#setInputCanvas('" + src + "') in " + this + " replacing '" + this.canvas_in + "' with '" + in + "'");
    this.canvas_in = in;
    Canvas c = ((VurfEclipse)APP.getApp()).pr.getCanvas(canvas_in);
    if (null!=c)
      this.src = c.getSurf();
    return this;
  }
  public Filter setOutputCanvas(String out) {
    println("Filter#setOutputCanvas('" + out + "') in " + this + " replacing '" + this.canvas_out + "' with '" + out + "'");
    this.canvas_out = out;
    Canvas c = ((VurfEclipse)APP.getApp()).pr.getCanvas(canvas_out);
    if (null!=c)
      this.out = c.getSurf(); //pr.getCanvas(canvas_out).getSurf();
    return this;
  }

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
    if (out==null) setOutputCanvas(canvas_out);
    if (src==null) setInputCanvas(canvas_in);
    out.beginDraw();
    /*if (!out.displayable()) {
    	println("can't draw to out " + out +"!");
    }*/
    //println("} wrapped beginDraw in " + this);
  }
  public void endDraw () {
    //println("endDraw in " + this + "{");
    out.endDraw();
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
  }

  /*
  *
   * PARAMETERS
   *              */
  //HashMap parameters<Parameter> = new HashMap<Parameter> (Parameter.class);// = new HashMap();
  HashMap<String, Parameter> parameters;// = new HashMap<String,Parameter> ();

  synchronized public Object getParameterValue(String paramName) {
    if (this.parameters==null) this.setParameterDefaults();
    //if (this.parameters.size()==0) setParameterDefaults();
    //println("looking for " + paramName);
    //if (this.parameters.containsKey(paramName))
    return this.parameters.get(paramName).value;
    //else
    //  return "unknown";
  }
  synchronized public Filter setParameterValue(String paramName, Object value) {
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
  synchronized public Filter setParameterValueFromSin(String paramName, Object f) {
    if (this.parameters==null) this.setParameterDefaults();
    try {
      parameters.get(paramName).setValueFromSin((Float)f);
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
    Iterator i = parameters.entrySet().iterator();
    while (i.hasNext ()) {
      Map.Entry me = (Map.Entry)i.next();
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
    	parameters = new HashMap<String, Parameter>();//String.class,Parameter.class);
    } else {
    	resetParameters();
    }
  }

  synchronized public HashMap<String,Object> getPresetValues () {
    HashMap<String,Object> h = new HashMap<String,Object>();
    Iterator i = parameters.entrySet().iterator();
    while(i.hasNext()) {
      Map.Entry me = (Map.Entry)i.next();
      h.put((String)me.getKey(), ((Parameter)me.getValue()).value);
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
    return true;
  }

  //abstract public void setupFilters ();

  public boolean stop() {    // stop processing when eg muted
    return true;
  }
  public boolean start() {  // stop processing when eg unmuted REMEMBER TO FIGURE OUT WHERE WE HAVE TO CALL THIS IN OUR PROCESSING LOOPS..!
	println("start()");
	this.setInputCanvas(this.canvas_in);
	this.setOutputCanvas(this.canvas_out);
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
    return this.out.get(0, 0, sc.w, sc.h);
  }

  public void drawLayerText (String label) {
    int lineCount = 2;
    int x = sc.w/2;
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
      this.toString() + " " + (this.isMuted()?"MUTED":"") +
      " (out: " + out + ", src: " + src + ")";
  }

  public void dispose() {
    this.stop();
  }


  //HashMap controllers<Controller,String> = new HashMap<Controller,String> (Controller.class,String.class);
  transient HashMap<controlP5.Controller,String> controllers = new HashMap<controlP5.Controller,String>();
  transient HashMap<String,controlP5.Controller> controllerMapping = new HashMap<String,controlP5.Controller>();

  transient controlP5.Toggle muteController;
  transient controlP5.Button nextModeButton;


  @Override
  public void controlEvent (CallbackEvent ev) {
	//println(this + " got event " + ev + " : " + ev.getController() + ev.getController().getValue());
	if (!ev.getController().isUserInteraction()) return;
	//println(this + " got event " + ev + " : " + ev.getController() + ev.getController().getValue());
    if (ev.getController()==this.muteController &&
    		/*ev.getAction()==ControlP5.ACTION_RELEASED || ev.getAction()==ControlP5.ACTION_RELEASEDOUTSIDE || */
    		ev.getAction()==ControlP5.ACTION_PRESS) {
        println("Setting mute state on " + this + " to " + muteController.getState());
        this.setMuted(muteController.getState());
    } else if (ev.getController()==this.nextModeButton && ev.getAction()==ControlP5.ACTION_PRESS) {
        this.nextMode();
    } else if (controllers.containsKey(ev.getController()) &&
    		(ev.getController().isUserInteraction() && (ev.getAction()==ControlP5.ACTION_BROADCAST || ev.getAction()==ControlP5.ACTION_DRAG || ev.getAction()==ControlP5.ACTION_RELEASE || ev.getAction()==ControlP5.ACTION_RELEASE_OUTSIDE || ev.getAction()==ControlP5.ACTION_PRESS)) //|| ev.getAction()==ControlP5.ACTION_BROADCAST)
    	) {
        String paramName = (String)controllers.get(ev.getController());
        //println(this+ "#controlEvent(" + ev.getController() + "): paramName is " + paramName + " for " + ev.getController() + " value is " + ev.getController().getValue());
        Object currentValue = getParameterValue(paramName);
        changeValueFor(currentValue,paramName,ev);
    	if (ev.getController() instanceof Textfield) { // && !currentValue.equals(((Textfield)ev.getController()).getText())) {
    		//sc.host.disableKeys = false;	// horrible hack to disable keyboard input when a textfield is selected..
    		((Textfield)ev.getController()).setFocus(true);
    	}
    } else if (controllers.containsKey(ev.getController()) && ev.getController().isUserInteraction()) {
    	if (ev.getController() instanceof Textfield) {
    		if (ev.getAction()==ControlP5.ACTION_ENTER || ev.getAction()==ControlP5.ACTION_CLICK) {
    			((Textfield)ev.getController()).setFocus(true);
    			sc.host.disableKeys = true;	// horrible hack to disable keyboard input when a textfield is selected..
    		} else if (ev.getAction()==ControlP5.ACTION_LEAVE) {
    			((Textfield)ev.getController()).setFocus(false);
    			sc.host.disableKeys = false;	// horrible hack to disable keyboard input when a textfield is selected..    			
    		}
    	}
    } else {		
      String paramName = (String)controllers.get(ev.getController());
      //println("UNHANDLED CONTROL EVENT in " + this + "#controlEvent(" + ev.getController() + "): paramName is " + paramName + " for " + ev.getController() + " value is " + ev.getController().getValue() + " action is " + ev.getAction());
    }
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
  public synchronized int setupControls(ControlFrame cf, ControllerGroup tab, int row) {
	  ControlP5 cp5 = cf.control();
  	if (controlsSetup) return 0;
  	controlsSetup = true;
    println("Filter#setupControls() for "  + this + ": " + tab.getName());
    /*if (count++>20) {
      println("Exiting because setupControls count is " + count + " in " + this);
      System.exit(0);
    }*/
       
    int size = 20;
    cp5.addCallback(this);
    //cp5.addTextlabel(tabName + this.toString()).setText(getFilterLabel()).linebreak();
       
    /*Group grp = cp5.addGroup("group_" + tab.getName() + "_" + getFilterName()).moveTo(tab);
    grp.setLabel(getFilterName());
    cp5.addTextlabel("label " + grp.getName(), getFilterName()).setGroup(grp).moveTo(grp).linebreak()
    		.setText("ffs?").setLabel("wtaf?").getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER);
    
    grp.setTitle("asdfasdf");*/
    Group grp = (Group) tab;
    
    int margin_h = 40;
    int margin_w = 5;
    /*int row = 0,*/float col = 0;
    int row_h = 50, col_w = 100;
        
    this.muteController = cp5.addToggle("mute_" + tab.getName() + getFilterName())
      .setPosition(margin_w + (col*col_w),margin_h + (row*row_h))
      .setLabel("Mute " + this.getFilterLabel())
      .setSize(size*2, size)
      .setValue(this.isMuted())
      .setState(this.isMuted())
      //.setPosition(lm, currentY+=(size+margin))
      //.plugTo(this, "setMuted")
      //.plugTo(this)
      .moveTo(grp)
      //.addCallback(this)
      ;
    
    this.muteController.getValueLabel().align(ControlP5.CENTER, ControlP5.CENTER).setPaddingY(2*cp5.getFont().getHeight());
    this.muteController.getCaptionLabel().align(ControlP5.LEFT, ControlP5.TOP_OUTSIDE).setPaddingY(2*cp5.getFont().getHeight());//.setPaddingY(cp5.getFont().getHeight());

    this.nextModeButton = cp5.addButton("nextmode_" + tab.getName() + getFilterName())
      .setLabel(">|")
      .setSize(size, size)
      .setPosition(margin_w + (col*(col_w+margin_w)) + size + 5,margin_h + (row*row_h))
      .moveTo(grp)
            //.plugTo(this)
            //.addCallback(this)
            //.linebreak()
              ;

    //cp5.addTextlabel("path_" + tab.getName() + getFilterName(), "Path: " + this.getPath()).setSize(size, size).moveTo(grp);//.linebreak();

    col = 1;

    if (parameters==null)
      setParameterDefaults();

    Iterator i = parameters.entrySet().iterator();
    //controlP5.Controller c = cp5.addSlider("die");
    while (i.hasNext ()) {
      Map.Entry me = (Map.Entry)i.next();
      //this.updateParameterValue((String)me.getKey(), me.getValue());
      //Object value = me.getValue();
      Parameter param = (Parameter)me.getValue();
      println("Filter#setupControls() in " + toString() + " doing control for " + param.getName());
      Object value = param.value;
      
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
      println("Filter: adding control object for filter with path " + this.getPath());


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
        		margin_w/2 + (col++*(margin_w+col_w)),
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
        println(this + ": set up Control for " + me.getKey() + " " + o.getClass()); // + " (which shouldnt differ from " + param.getName() + " if i've understood my own code .. )");
      }
      // }
    }
    row++;

    ((ControlGroup<Group>) tab).setBackgroundHeight(margin_h + (row * row_h));
    tab.setColorBackground((int) random(255));
    tab.setSize((int) (margin_w + (col++*col_w)),margin_h + (row * row_h));
    //cp5.linebreak();

    return row;
  }

  public void setControllerMapping(String paramName, controlP5.Controller o) {
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
        	  if ((int)value!=-1 && (int)value!=-16777216) {	// TODO: hacky workaround for bug 'greyscale trap' where you can't move the colour selector UI out of the grey range if certain values get set?
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


  public Object target(String path, Object payload) {
	  println("#target('"+path+"', '"+payload+"'");
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

  public HashMap<String, Targetable> getTargetURLs() {
	HashMap<String, Targetable> urls = new HashMap<String,Targetable> ();
	Filter f = this;

	urls.put(f.getPath() + "/mute", f);
	urls.put(f.getPath() + "/nextMode", f);
	println("added Filter's url '" + f.getPath() + "/mute' mapped to " + f);

	Iterator<Parameter> pit = f.getParameters().iterator();
	while (pit.hasNext()) {
		Parameter p = pit.next();
		println("added Parameter's url '" + p.getPath() + "' mapped to " + p);
		urls.put(p.getPath(), p);
		if (p.getName().equals("text")) {
			println("got text!");
			//System.exit(0);
		}
	}

	urls.putAll(getCustomTargetURLs());

	return urls;
  }
	public void randomiseParameters(Sequence seq,String[] parameters) {
		for (String p : parameters ) {
			println("Randomising parameter " + p + " in " + this.toString());
			this.setParameterValueFromSin(p, seq.random(0f, 2f)-1f);
		}		
	}
	public Parameter getParameter(String name) {
		if (this.parameters==null) 
			this.setParameterDefaults();
		return this.parameters.get(name);
		//return null;
	}
	
	public void readSnapshot(Map<String,Object> input) {
		this.setFilterName((String) input.get("name"));
		this.setFilterLabel((String) input.get("label"));
		//this.setDescription(input.get("description"));
		this.setOutputCanvas((String) input.get("canvas_out"));
		this.setInputCanvas((String) input.get("canvas_src"));	
	
		for (Entry<String, Object> p : ((Map<String, Object>) input.get("parameter_defaults")).entrySet()) {
			Map<String,Object> para = (Map<String, Object>) p.getValue();
			this.addParameter((String) para.get("name"), para.get("default"), para.get("min"), para.get("max"));
		}		
	}
	
	public HashMap<String,Object> collectFilterSetup() {	// for saving snapshots, save setup of filter
		HashMap<String,Object> output = new HashMap<String,Object>();
		
		output.put("class", this.getClass().getName());
		output.put("name", this.getFilterName());
		output.put("label", this.getFilterLabel());
		output.put("description", this.getDescription());
		output.put("path", this.getPath());
		output.put("canvas_out", this.getOutputCanvas());
		output.put("canvas_src", this.getSourceCanvas());
		
		output.put("parameter_defaults", this.collectParameterSetup());
		
		return output;
	}
	private HashMap<String,Object> collectParameterSetup() {
		HashMap<String,Object> output = new HashMap<String,Object> ();
		for (Parameter p : this.getParameters()) {
			output.put(p.getPath(), p.getParameterSetup());//.getDefaultValue());
		}
		return output;
	}
	private String getSourceCanvas() {
		// TODO Auto-generated method stub
		return this.canvas_in;
	}
	private String getOutputCanvas() {
		// TODO Auto-generated method stub
		return this.canvas_out;
	}
	public static Filter createFilter(String classname, Scene host) {
		try {
			Class clazz = Class.forName(classname);
			//System.out.println (clazz.getConstructors());
			//Constructor<?> ctor = clazz.getConstructors()[0]; //[0]; //Scene.class, Integer.class);
			System.err.println("about to try and get constructor for Filter '" + classname + "'");
			if (classname.contains("$")) {
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
				return (Filter) ctor.newInstance(host); //(Scene)null, (int)0);
			}
			//Object seq = ctor.newInstance(); //(Scene)null, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}

