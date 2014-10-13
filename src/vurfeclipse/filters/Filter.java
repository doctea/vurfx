package vurfeclipse.filters;
import vurfeclipse.*;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import codeanticode.glgraphics.*;

import java.io.Serializable;
import java.util.*;

import processing.core.PImage;
import vurfeclipse.APP;
import vurfeclipse.Canvas;
import vurfeclipse.Pathable;
import vurfeclipse.VurfEclipse;
import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;

//import java.util.Iterator;
//import java.util.Map;

class Parameter implements Serializable {

  
  String name;
  public Object value;  
  //private transient controlP5.Controller controller;
  //String controllerName;
  
  String filterPath;
  //transient Scene sc;

  Class datatype;

  Object min, max;
  
  Parameter () {
    //this.controller = cp5.getController(this.controllerName);
  }
  Parameter (String name, Object value) {
    this.name = name;
    this.value = value;
    this.datatype = value.getClass();
  }
  Parameter (String name, Object value, Object min, Object max) {
    this(name, value);
    this.min = min;
    this.max = max;
  }
  
  public void setFilterPath(String filterPath) {
    this.filterPath = filterPath;
  }
  
  /*public void setController(controlP5.Controller c) {
    //this.controller = c;
    controllerName = c.getControllerName();
  }*/
  
  /*public controlP5.Controller getController() {
    if (sc==null) sc = pr.getSceneForPath(this.scenePath);
    return sc.getControllerFor(name);
  }*/

  void setValue(Object value) {
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
  

  void setValueFromSin(float f) {
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


public abstract class Filter implements CallbackListener, Pathable, Serializable, Mutable {
  boolean muted = false;
  boolean started = false;

  String filterLabel = "";
  String filterName = "";

  boolean drewFrame = false;

  //  PImage buffer;

  //PGraphics out;
  //PGraphics src;
  public transient GLGraphicsOffScreen out;
  public transient GLGraphicsOffScreen src;

  
  boolean outputDebug = true;
  public void println(String text) {		// debugPrint, printDebug -- you get the idea
	  if (outputDebug) System.out.println("F " + (text.contains((this.toString()))? text : this+": "+text));
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

  Filter(Scene sc) {
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
      filterName = this.getClass().toString() + ((VurfEclipse)APP.getApp()).pr.getGUID();
      println("setting empty filter name to " + filterName);
    }
    return filterName;
  }

  public String getPath () {
    println("Filter#getPath() for " + toString() + " returning '" + sc.getPath() + "/" + this.getFilterName() +"'");
    return sc.getPath() + "/" + this.getFilterName();
  }


  public Object getObjectForPath(String path) {
    String[] spl = path.split("/",2);
    if (this.parameters.containsKey(spl[0])) {
      return this.parameters.get(spl[0]);
    }
    return null;
  }

  String canvas_out;
  String canvas_in;
  public Filter setCanvases(String out, String in) {
    setOutputCanvas(out);
    setInputCanvas(in);
    /*this.canvas_out = out;
    this.canvas_in = in;*/
    return this;
  }  
  public Filter setInputCanvas(String in) {
    this.canvas_in = in;
    Canvas c = ((VurfEclipse)APP.getApp()).pr.getCanvas(canvas_in);
    if (null!=c)    
      this.src = c.getSurf();
    return this;    
  }
  public Filter setOutputCanvas(String out) {
    println("Filter#setOutputCanvas('" + out + "') in " + this + " replacing '" + this.canvas_out + "'");
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
  public void nextMode() {
    //
  }


  public boolean isMuted() {
    return this.muted;
  }
  public void toggleMute() {
    this.setMute(!this.isMuted());
    //this.muted = !this.muted;
  }
  public void setMute(boolean v) {
    if (v==true && this.isMuted()!=v) {
      this.stop();
    } 
    else if (v==false && this.isMuted()!=v) {
      this.start();
    }

    if (this.muteController!=null) this.muteController.setState(v);

    this.muted = v;
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

  public Object getParameterValue(String paramName) {
    if (this.parameters==null) this.setParameterDefaults();
    //if (this.parameters.size()==0) setParameterDefaults();
    //println("looking for " + paramName);
    //if (this.parameters.containsKey(paramName)) 
    return this.parameters.get(paramName).value;
    //else
    //  return "unknown";
  }
  public Filter setParameterValue(String paramName, Object value) {
    if (this.parameters==null) this.setParameterDefaults();
    //println("in " + this + ": setParameterValue ('"+ paramName + "', '" + value + ")");
    //if (this.parameters.size()==0) setParameterDefaults();
    //parameters.put(paramName, value);
    //parameters.put(paramName, new Parameter(paramName, value));//, min, max));
    //if (parameters.containsKey(paramName)) {
    //parameters.get(paramName).value = value;
    parameters.get(paramName).setValue(value);
    /*} else {
     parameters.put(paramName, new Parameter(paramName, value));
     }*/
    return this;
  }
  public Filter setParameterValueFromSin(String paramName, Object f) {
    if (this.parameters==null) this.setParameterDefaults();
    try {
      parameters.get(paramName).setValueFromSin((Float)f);
    } catch (ClassCastException e) {
      println("Error - caught trying to set " + paramName + " to a " + f.getClass());
    }
    return this;
  }

  // public void setParameterValue  

  public Filter changeParameterValueFromSin(String paramName, float s) {
    setParameterValueFromSin(paramName, s);
    updateParameterValue(paramName, getParameterValue(paramName));
    return this;
  }
  public Filter changeParameterValue(String paramName) {
    changeParameterValue(paramName, parameters.get(paramName).value);
    return this;
  }  
  public Filter changeParameterValue(String paramName, Object value) {
    setParameterValue(paramName, value);
    updateParameterValue(paramName, value);    
    return this;
  }
  public void toggleParameterValue(String paramName) {
		// TODO Auto-generated method stub
    if (getParameterValue(paramName) instanceof Boolean) {
    	changeParameterValue(paramName, !(Boolean)getParameterValue(paramName));
    }
  }  

  public Filter addParameter(String paramName, Object value, Object min, Object max) {
    if (this.parameters==null) this.setParameterDefaults();
    println(this + "#addParameter(" + paramName + ", " + value + ", " + min + ", " + max + "): " + this.getFilterLabel());
    parameters.put(paramName, new Parameter(paramName, value, min, max)); 
    updateParameterValue(paramName, value);
    return this;
  }
  public Filter addParameter(String paramName, Object value) {
    return addParameter(paramName, value, -100, 100);
  }

  public void updateAllParameterValues() {
    Iterator i = parameters.entrySet().iterator();
    while (i.hasNext ()) {
      Map.Entry me = (Map.Entry)i.next();
      this.updateParameterValue((String)me.getKey(), ((Parameter)me.getValue()).value);
    }
  }

  public void updateParameterValue(String paramname, Object value) {
    // set parameter Control
    

    //sc.myTextarea.setText(serialize());
    //sc.updateSerializeBox();
    if(paramname=="muted") {
      this.muted = (Boolean) value;
    }
  }

  public void setParameterDefaults () {
    println("setParameterDefaults in " + this);
    parameters = new HashMap<String, Parameter>();//String.class,Parameter.class);
  }

  public HashMap<String,Object> getPresetValues () {
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
    return true;
  }

  //abstract public void setupFilters ();

  public boolean stop() {    // stop processing when eg muted
    return true;
  }
  public boolean start() {  // stop processing when eg unmuted REMEMBER TO FIGURE OUT WHERE WE HAVE TO CALL THIS IN OUR PROCESSING LOOPS..!
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
    if (this.filterLabel=="") filterLabel = this.getClass().toString() + ((VurfEclipse)APP.getApp()).pr.getGUID(); //toString();//.replace("@","-");
    
    return this.filterLabel==""?this.toString():this.filterLabel;
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


  public void controlEvent (CallbackEvent ev) {
    //println(this + " got event " + ev + " : " + ev.getController());
    if (ev.getAction()==ControlP5.ACTION_RELEASED) {
      if (ev.getController()==this.muteController) {
        println("Setting mute state on " + this + " to " + muteController.getState());
        this.setMute(muteController.getState());
      } else if (ev.getController()==this.nextModeButton) {
        this.nextMode();
      } 
      else if (controllers.containsKey(ev.getController())) {
        String paramName = (String)controllers.get(ev.getController());                
        println(this+ "#controlEvent(" + ev.getController() + "): paramName is " + paramName + " for " + ev.getController() + " value is " + ev.getController().getValue());
        Object currentValue = getParameterValue(paramName) ;
        changeValueFor(currentValue,paramName,ev);
      }
    } /*else if (ev.getAction()==ControlP5.ACTION_PRESSED) {
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
        } 
        else {
          this.changeParameterValue(paramName, ev.getController().getValue());
        }
  }    
 
  int count = 0;
  boolean controlsSetup = false;
  public synchronized void setupControls(ControlP5 cp5, String tabName) {
	if (controlsSetup) return;
	controlsSetup = true;
    println("Filter#setupControls() for "  + this + ": " + tabName);
    /*if (count++>20) {
      println("Exiting because setupControls count is " + count + " in " + this);
      System.exit(0);
    }*/
    int size = 20;
    cp5.addCallback(this);
    //cp5.addTextlabel(tabName + this.toString()).setText(getFilterLabel()).linebreak();

    this.muteController = cp5.addToggle("mute_" + tabName + getFilterName())
      .setLabel("Mute " + this.getFilterLabel())
        .setSize(size*2, size)
          .setValue(this.isMuted())
            //.setPosition(lm, currentY+=(size+margin))
            //.plugTo(this, "setMuted")
            //.plugTo(this)
            .moveTo(tabName)
              //.addCallback(this)
              ;

    this.nextModeButton = cp5.addButton("nextmode_" + tabName + getFilterName())
      .setLabel(">|")
        .setSize(size, size)
          .moveTo(tabName)
            //.plugTo(this)
            //.addCallback(this)
            .linebreak()
              ;

    cp5.addTextlabel("path_" + tabName + getFilterName(), "Path: " + this.getPath()).setSize(size, size).moveTo(tabName).linebreak();
    


    if (parameters==null) 
      setParameterDefaults();

    Iterator i = parameters.entrySet().iterator();
    //controlP5.Controller c = cp5.addSlider("die");
    while (i.hasNext ()) {
      Map.Entry me = (Map.Entry)i.next();
      //this.updateParameterValue((String)me.getKey(), me.getValue());
      //Object value = me.getValue();
      Parameter param = (Parameter)me.getValue();
      println("Filter#setupControls() in " + toString() + " doing control for " + param.name);
      Object value = param.value;
      controlP5.Controller o = 
        value instanceof Float ?
          cp5.addSlider(tabName + this + me.getKey()).setValue(
        		  (Float)(Float)value).setLabel(me.getKey().toString())
        		  .setRange(
        				  new Float((Float)param.min), 
        				  new Float((Float)param.max)
        				  )
        				  .moveTo(tabName).setSize(size*5, size) : //.addCallback(this) :
        value instanceof Integer ?
          cp5.addSlider(tabName + this + me.getKey()).setValue((Integer)value).setLabel(me.getKey().toString()).setRange((Integer)param.min, (Integer)param.max).moveTo(tabName).setSize(size*5, size) : //addCallback(this) :
        value instanceof Boolean ?
          cp5.addToggle(tabName + this + me.getKey()).setState((Boolean)value).setLabel(me.getKey().toString()).moveTo(tabName).setSize(size, size) : //.addCallback(this) :
          /*          value instanceof PVector ?
           cp5.addSlider(tabName + this + me.getKey()).setValue(((PVector)value).x).moveTo(tabName) :*/
          null
          //cp5.addTextfield(tabName + this + me.getKey()).setValue("value from " + me.getKey() + " " + (String)value.toString()).moveTo(tabName).setLabel("error!")
      ;



      //o.linebreak();
      //param.controller = o;
      //param.setController(o);
      //o.addCallback(this);
      if (o!=null) {
        o.getValueLabel().align(ControlP5.LEFT, ControlP5.RIGHT).setPaddingY(0);      
        o.getCaptionLabel().align(ControlP5.RIGHT, ControlP5.RIGHT).setPaddingY(0);        
        param.setFilterPath(this.getPath());
        println("Filter: adding control object for filter with path " + this.getPath());
        this.setControllerMapping(param.name,o);
        
        if (!i.hasNext()) o.linebreak();
        o.linebreak();
        /*controllers.put(
          o, (String)me.getKey()
        );*/
        println(this + ": set up Control for " + me.getKey() + " (which shouldnt differ from " + param.name + " if iv understood my own code .. )");
      }
      // }
    }
    //cp5.linebreak();
  }
  
  public void setControllerMapping(String paramName, controlP5.Controller o) {
    if (controllerMapping==null) controllerMapping = new HashMap<String,controlP5.Controller> ();
    controllerMapping.put(paramName, o);
    controllers.put(o, paramName);
  } 
  
  public void updateControl (String name, Object value) {
    //(((controlP5.Controller)controllerMapping.get(name))).setValue(value);
    if (controllerMapping==null) controllerMapping = new HashMap<String,controlP5.Controller> ();    
    controlP5.Controller c = (controlP5.Controller)controllerMapping.get(name);
    if (c!=null) {
        if (value instanceof Float)
          c.setValue((Float)value);
        else if (value instanceof Integer) 
          c.setValue((Integer)value);
        else if (value instanceof Boolean)
          c.setValue((Boolean)value?1.0f:0.0f);    
    }
  }

}

