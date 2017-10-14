package vurfeclipse.scenes;

import vurfeclipse.*;

import java.io.*;
import java.util.*;

import processing.core.PGraphics;
import processing.core.PVector;

import vurfeclipse.*;
import vurfeclipse.filters.*;
import vurfeclipse.parameters.Parameter;
import vurfeclipse.projects.Project;
import vurfeclipse.streams.*;
import vurfeclipse.scenes.*;
import vurfeclipse.sequence.*;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Tab;

public abstract class Scene implements CallbackListener, Serializable, Mutable, Targetable {
  // Scene stuff
  public int w,h;

  boolean muted = false;

  public transient Project host;

  int BUF_SRC = 0;
  int BUF_SRC0 = BUF_SRC;
  int BUF_SRC1 = 1;
  int BUF_SRC2 = 2;
  int BUF_SRC3 = 3;

  int BUF_DEPTH = 4;
  int BUF_TEMP = 5;
  int BUF_TEMP2 = 6;
  int BUF_TEMP3 = 7;
  int BUF_TEMP4 = 8;

  int BUF_OUT = 9;
  public int BUF_MAX = 10;

  //PGraphics[] buffers = new PGraphics[BUF_MAX];
  //GLGraphicsOffScreen[] buffers = new GLGraphicsOffScreen[BUF_MAX];

  //Canvas[] canvases = new Canvas[BUF_MAX];

  // localname, projectpath
  HashMap<String,String> buffermap = new HashMap<String,String>();

  //Canvas[] canvases = new Canvas[BUF_MAX];

  protected int filterCount;

  //transient
    public Filter[] filters;// = new Filter[filterCount];

  protected HashMap<String,Sequence> sequences = new HashMap<String,Sequence>();

  public HashMap<String,Sequence> getSequences() {
	  if (sequences.size()==0) setupSequences();
	  return this.sequences;
  }
  public void setupSequences() {

  }
  public Sequence getSequence(String name) {
	  return getSequences().get(name);
  }
  public Sequence makeChainSequenceFrom(String name,Sequence newSeq) {
	  Sequence seq = getSequence(name);
	  return new ChainSequence (newSeq.getLengthMillis()).addSequence(seq).addSequence(newSeq);
  }


  public Scene addSequence(String string, Sequence sequence) {
		getSequences().put(string, sequence);
		return this;
  }

  boolean outputDebug = true;
  public void println(String text) {		// debugPrint, printDebug -- you get the idea
	  if (outputDebug) System.out.println("S " + (text.contains((this.toString()))? text : this+": "+text));
  }


  String sceneName = null;
  public String getSceneName() {
    if (this.sceneName==null)
      //return this.getClass().toString();//.toString(;
      this.sceneName = this.getClass().toString() + ((VurfEclipse)APP.getApp()).pr.getGUID(); //toString();
    return sceneName;
  }
  public Scene setSceneName(String sn) {
    println("Scene " + this + " setting sceneName to " + sn);
    this.sceneName = sn;
    return this;
  }


  public Object getObjectForPath(String path) {
    //println("Scene#getObjectForPath(" + path + ")");
    if (path.equals("") || path.equals(this.getSceneName())) return this;
    String spl[] = path.split("/",2);
    return getFilter(spl[1]);
  }

  public Filter getFilter(String name) {
    for (int i = 0 ; i < filters.length ; i++) {
      if (filters[i]!=null) {
        //println("Scene#getFilter(" + name + ") checking '" + name + "' against '" + filters[i].getFilterName() + "'");
        if (filters[i].getFilterName().equals(name)) {
          return filters[i];
        }
      }
    }
    return null;
  }

  public ArrayList<Filter> getFilters () {
	  ArrayList<Filter> f = new ArrayList<Filter>();
	  for (int i = 0 ; i < filters.length ; i++) {
		  if (filters[i]!=null) {
			  f.add(filters[i]);
		  }
	  }
	  return f;
  }

  public String getPath() {
    return ((VurfEclipse)APP.getApp()).pr.getPath() + "sc/" + this.getSceneName();
  }

  public Canvas createCanvas(String canvasName) {
	return this.createCanvas(canvasName, this.w, this.h);
  }

  public Canvas createCanvas(String canvasName, int width, int height) {
	    String path = getPath()+"/"+canvasName;

	    Canvas c = ((VurfEclipse)APP.getApp()).pr.createCanvas(path, canvasName, width, height);
	    setCanvas(canvasName, path);

	    return c;
  }

  public String getCanvasMapping(String canvasName) {
    String mapTo = (String)buffermap.get(canvasName);
    if (mapTo==null) {
      println ("Scene["+getSceneName()+"]#getCanvasMapping('"+canvasName+"'): mapTo isn't set for '" + canvasName + "'");

      mapTo = getPath()+"/"+canvasName;
      println ("Scene["+getSceneName()+"]#getCanvasMapping('"+canvasName+"'): Creating canvas at path '" + mapTo + "' for '" + canvasName + "' in " + this);
      ((VurfEclipse)APP.getApp()).pr.createCanvas(mapTo, canvasName);
      buffermap.put(canvasName, mapTo); //pr.createCanvas(getPath()+"/"+canvasName, canvasName));//makeCanvas(w,h,gfx_mode,canvasName)));
    }
    return mapTo;
  }

  public PVector getCanvasMappingSize(String canvasMappingName) {
	  Canvas canvas = this.getCanvas(this.getCanvasMapping(canvasMappingName));
	  return canvas.getSize();
  }
  protected int getCanvasMappingH(String canvasMappingName) {
  	return (int)getCanvasMappingSize(canvasMappingName).x;
  }
	protected int getCanvasMappingW(String canvasMappingName) {
		return (int)getCanvasMappingSize(canvasMappingName).y;
	}

  public Canvas getCanvas(String canvasName) {
    String mapTo = getCanvasMapping(canvasName);

    //println("Scene["+getSceneName()+"]#getCanvas('"+canvasName+"'): returning mapped '" + mapTo + "': " + ((VurfEclipse)APP.getApp()).pr.getCanvas(mapTo));
    return ((VurfEclipse)APP.getApp()).pr.getCanvas(mapTo);
  }
  //public Canvas setCanvas(String canvasName, String canvasPath) {
  public Scene setCanvas(String canvasName, String canvasPath) {
    println("setCanvas() setting canvasName '" + canvasName + "' to canvasPath '" + canvasPath + "'");
    buffermap.put(canvasName, canvasPath);
    //return getCanvas(canvasName);
    return this;
  }

/*
  //// new drawing code
  GLTexture outTex;
  GLGraphicsOffScreen off;
  public void generateTexture() {
    if (outTex==null) outTex = new GLTexture(APP,w,h);
    if (off==null) off = createGLBuffer(w,h,gfx_mode);

    applyGL(off);
    outTex.copy(off.getTexture());
  }
  public GLTexture getTexture() {
    return this.outTex;
  }
  /// end new drawing code
*/



  public boolean isMuted() {
    return this.muted;
  }
  public void setMuted() {
	  this.setMuted(true);
  }
  public void setMuted(boolean m) {
    this.muted = m;
    if (null!=muteController) muteController.setValue(isMuted());
    //return this;
  }
  public void toggleMute() {
    this.setMuted(!this.isMuted());
  }


  public Scene addFilter(Filter f) {
    if (this.filters==null) this.filters = new Filter[filterCount];
    int filterNumber = -1;
    for (int i = 0 ; i < filterCount ; i++) {
      if (filters[i]==null) {
        //println(this + " adding " + f + " - Found empty filter at " + i + "!");
        filterNumber = i;
        break;
      }
    }
    if (filterNumber==-1) {
      println("addFilter error - no free filters to add " + f);
    } else {
      filters[filterNumber] = f;

      f.sc = this;  //

      //if (f.src==null) f.src = filters[filterNumber-1].out;  // set the source to the previous
      //if (f.out==null) f.out = filters[filterNumber-1].out;

      if (highestFilter<filterNumber) highestFilter = filterNumber;
    }
    return this;
  }


  int highestFilter = filterCount;
  public int selectedFilter = 0;
  public void selectFilter(int sf) {
    if (sf>=filterCount) sf = 0;
    if (sf<0) sf = filters.length;
    if (this.filters[sf]!=null) {
      this.selectedFilter = sf;
    } else {
      selectFilter(sf+1);
    }
  }

  public Filter getFilter(int index) {
    return filters[index];
  }
  public Filter getSelectedFilter() {
    return filters[selectedFilter];
  }
  public String getSelectedFilterDescription() {
    if(this.filters[selectedFilter]!=null)
      return "["+this.selectedFilter+"]:" + this.filters[this.selectedFilter].getDescription();
    else
      return "no filter selected in " + this;
    //return "["+this.selectedFilter+"]:" + this.filters[this.selectedFilter].toString();
  }
  public void toggleMuteSelected() {
    this.filters[this.selectedFilter].toggleMute();
  }
  public boolean selectedMuteStatus () {
    return this.filters[this.selectedFilter].isMuted();
    //return this.filters[this.selectedFilter].muted;
  }
  public void muteAll() {
    for (int i = 0 ; i < filters.length ; i++) {
      if (filters[i]!=null) {
        //filters[i].muted = true;
        filters[i].setMuted(true);
      }
    }
  }
  public void nextFilterMode() {
    this.filters[this.selectedFilter].nextMode();
  }

  public void selectNext() {
    this.selectFilter(this.selectedFilter+1);
  }
  public void selectPrevious() {
    this.selectFilter(this.selectedFilter-1);
  }

  public void swapFiltersUp() {
    int swap = this.selectedFilter+1;
    if (swap>filters.length) swap = 1;

    Filter temp;
    temp = filters[this.selectedFilter];
    filters[this.selectedFilter] = filters[swap];
    filters[swap] = temp;
    this.selectNext();
  }

  public void swapFiltersDown() {
    int swap = this.selectedFilter-1;
    if (swap<0) swap = 0;

    Filter temp;
    temp = filters[this.selectedFilter];
    filters[this.selectedFilter] = filters[swap];
    filters[swap] = temp;
    //this.selectPrevious();
  }

  /*public Scene setOutputCanvas(Canvas canvas) {
    setOutputBuffer(canvas.surf);
    return this;
  }*/
  public Scene setOutputCanvas(String path) {
    //this.buffermap.put("out", path);
    this.setCanvas("out", path);
    //setOutputBuffer(pr.getCanvas(path).surf);
    //setOutputCanvas(pr.getCanvas(path));
    return this;
  }
  /*public Scene setInputCanvas(Canvas canvas) {
    /// err should ste a hashmap with the
    //setInputBuffer (canvas.surf);
  }*/
  public Scene setInputCanvas(String path) {
    setCanvas("src", path);
    //this.buffermap.put("out", path);
    //setInputCanvas (pr.getCanvas(path));
    return this;
  }
  /*public Scene setOutputBuffer(GLGraphicsOffScreen ob) {
    //println("setOutputBuffer to " + ob + " on " + this);
    //if (buffers[BUF_OUT]!=null) buffers[BUF_OUT].dispose();
    //buffers[BUF_OUT] = ob;
    setBuffer(BUF_OUT, ob);
    return this;
  }
  public Scene setInputBuffer(GLGraphicsOffScreen ib) {
    //println("setInputBuffer to " + ib + " on " + this);
    //if (buffers[BUF_SRC]!=null) buffers[BUF_SRC].dispose();
    //buffers[BUF_SRC] = ib;
    setBuffer(BUF_SRC, ib);
    return this;
  }*/
  /*public Scene setBuffer(int index, GLGraphicsOffScreen b) {
    println("Setting buffer to " + b);
    if (buffers[index]!=null) buffers[index].dispose();
    buffers[index] = b;
    return this;
  }*/

  public boolean initialise () {
    // do some setup, return true/false
    //println("initialise scene " + this);

/*    for (int i=0;i<buffers.length;i++) {
      //buffers[i] = Buffer.createGraphics(sc.w, sc.h, P3D);
      //println("creating buffer " + i);

      //buffers[i] = createBuffer(sc.w,sc.h,gfx_mode);\
      if (buffers[i]==null) {
        buffers[i] = createGLBuffer(w,h,gfx_mode);
        //buffers[i] = pr.createCan
      }
    }*/
/*    for (int i=0;i<filters.length;i++) {
      if (filters[i]!=null) {
        highestFilter = i;
      }
    }*/
    //initialiseFilters();

	/*Canvas blank = createCanvas("blank");
	blank.getSurf().background(0);*/

    return true;
  }
  public abstract boolean setupFilters();
  boolean initialisedFilters = false;
  public boolean initialiseFilters () {
	if (initialisedFilters) return true;
    setupFilters();
    for (int i = 0 ; i < this.filters.length ; i ++) {
      if (filters[i]!=null) {
        filters[i].initialise();

        /*if (filters[i].out==null) filters[i].out = buffers[BUF_OUT];
        if (filters[i].src==null) filters[i].src = buffers[BUF_SRC];*/

        //if (filters[i].out==null) filters[i].out = getCanvas("out").getSurf();
        //if (filters[i].src==null) filters[i].src = getCanvas("src").getSurf(); //setInputCanvas(getCanvas("src"));

        if (filters[i].out==null) filters[i].setOutputCanvas(getCanvasMapping("out"));
        if (filters[i].src==null) filters[i].setInputCanvas(getCanvasMapping("src"));

      }
    }
    initialisedFilters = true;
    return true;
  }

  protected Scene(Project host, int w, int h) {
    // constructor
    this.host = host;
    this.w = w;
    this.h = h;
  }

  public void toggleMute(int filterNumber) {
    //this.filters[filterNumber].muted=!this.fmuted;
    this.filters[filterNumber].toggleMute();
    if (this.filters[filterNumber]!=null) {
      println("Muting [" + filterNumber + "]: " + this.filters[filterNumber] + " now " + this.filters[filterNumber].isMuted());
    }

  }

  // Scene should look after two (or more) buffers.  which buffer the filter draws to/from is set in each filter (in eg DemoScene).
  // two (or more) buffers held - visible buffer to pass to next layer, source data to pass to next layer, depth buffer, overlay buffers..
  // so WebcamFilter would be set to write to the source data layer, with optional write to visible layer
  // MirrorFilter would flip the source data layer AND/OR the visible layer
  // PointDrawer would read from source and write to visible layer
  // multiple outputs per filter - write data to source layer(s) and/or visible layer
  // copy visible layer at end to scene output frame

  int prof_loadbegintimes = 0;
  int prof_filtertimes[] = new int[filterCount];

  public void applyGLtoCanvas(Canvas canvas) {
    applyGL(canvas.getSurf());
  }

  public void applyGL(PGraphics gfx) {
    //int start_mils = millis();
    //gfx.background(0,0,0,255);
    //gfx.background(128,0,0);
    //gfx.background(0,0,0,0);
    
    //gfx.rect(100, 100, 200,200);

    //println(this + " applyGL start loop: ");
    for (int i = 0 ; i < filterCount ; i++) {
      //println("checking " + i);
      if (filters[i]!=null) {
        if (!filters[i].isMuted()) {
          //println("not muted " + i );
          //filters[i].apply(f);
          ////f.setPixelMap(filters[i].apply(f));
          ////f.img = filters[i].getFinalImage();
          //int p_beforeapply = millis();
          filters[i].beginDraw();
          //filters[i].drawLayerText();
          //if (filters[i] instanceof PlainDrawer || filters[i] instanceof KaleidoFilter) println(filters[i] + " has out of " + filters[i].out);
          //println(this + " drawing filter " + i + " " + filters[i]);
          filters[i].applyToBuffers();
          filters[i].endDraw();
          //buffers[BUF_OUT].background(i*(255/8));
          //prof_filtertimes[i] += millis() - p_beforeapply;
        }
      }
    }
    //println(this + " applyGL end loop");
    //println("-------------------------------");
    //println(this + " applyGL(): copying " + buffers[BUF_OUT] + " to " + gfx);// + " in " + this);
/*    gfx.beginDraw();
    //gfx.background(128,0,0,0);
    //gfx.fill(0,0,0,255);
    //gfx.clear(0);
    gfx.image(buffers[BUF_OUT].getTexture(),0,0);
    gfx.endDraw();*/
  }


  public String getProfileDescription () {
    String ret = "";
    //for (int i = 0 ; i<prof_loadbegintimes.length ; i++) {
    for (int i = 0 ; i<prof_filtertimes.length ; i++) {
      if (filters[i]!=null)
        ret += ("["+i+"] " + filters[i].getClass()) + " : " + (prof_filtertimes[i]) + "mils.... ";
    }
    //ret += prof_loadbegintimes[i];
    //ret += (String)prof_loadbegintimes;

    return ret;
  }

  /////////// Callbacks stuff

  public void changeFilterParameterValue(int index, String paramName, Object value) {
    if (null!=filters)
     if (null!=filters[index])
       filters[index].changeParameterValue(paramName,value);
  }
  public void changeFilterParameterValueFromSin(int index, String paramName, float s) {
     if(null!=filters)
      if (null!=filters[index])
        filters[index].changeParameterValueFromSin(paramName,s);
  }


  HashMap<String,ParameterCallback> callbacks;//<String,ParameterCallback>; //<String,ParameterCallback>;
  public void setupCallbackPresets () {
    this.callbacks = new HashMap<String,ParameterCallback> ();
    println("setupCallbackPresets in " + this);
    final Scene self = this;

    this.callbacks.put("toggle", new ParameterCallback() {
      public void call(Object value) {
        int i = Integer.parseInt(value.toString());
        if (i%2==0) {
          //this.toggleMute();
          self.setMuted(false);
        } else {
          self.setMuted(true);
        }
      }
    });

  }
  public ParameterCallback getCallbackPreset(String callbackName) {
    if (this.callbacks==null) this.setupCallbackPresets();
    //println("getting callbackName " + callbackName);
    return (ParameterCallback) this.callbacks.get(callbackName);
  }
  public Scene registerCallbackPreset(Stream s, String eventName, String callbackName) {
    ParameterCallback cb = getCallbackPreset(callbackName);
    s.registerEventListener(eventName, cb);
    return this;
  }
  public Scene registerCallbackPreset(String streamName, String eventName, String callbackName) {
    return this.registerCallbackPreset(((VurfEclipse)APP.getApp()).pr.getStream(streamName), eventName, callbackName);
  }

  ////////// end Callbacks stuff



  public void finish() {
    if (filters!=null)
      for (int i = 0 ; i < filterCount ; i++) {
        if (filters[i]!=null) filters[i].dispose();
      }
  }

  public void sendKeyPressed(char key) {
    Scene sc = this;
    if (key=='\\') {
      sc.toggleMute();
      println((this.isMuted()?"MUTE":"LIVE") + "set on Scene " + this + " toggled to ");
    }
    /*if (key=='p') {
      sc.nextFilterMode();
      println("Switched filter mode on " + sc.getSelectedFilterDescription());
    }
    else if (key=='a') {
      sc.selectNext();
      println("Selected sc " + sc.getSelectedFilterDescription());
    }
    else if (key=='q') {
      sc.selectPrevious();
      println("Selected sc " + sc.getSelectedFilterDescription());
    }
    else if (key=='`') {
      sc.toggleMuteSelected();
      println("Toggled to " + (sc.selectedMuteStatus()?"MUTED":"LIVE") + " for " + sc.getSelectedFilterDescription()); //+ " (is now " + (sc.selectedMuteStatus()?"MUTED":"LIVE") + ")");
    }
    else if (key=='_') {
      sc.muteAll();
      println("Muted all");
    }
    else if (key=='n') {
      sc.swapFiltersUp();
      println("Swapped filters up!");
    }
    else if (key=='m') {
      sc.swapFiltersDown();
      println("Swapped filters down!");
    }
    else if (key>='0' && key<='9') {
      int numkey = int(key+""); //Integer.parseInt(new String(key));
      //sc.filters[numkey].muted = !sc.filters[numkey].muted;
      //sc.toggleMute(numkey);

      sc.selectFilter(numkey);
      println("Selected sc " + sc.getSelectedFilterDescription());
    } else*/
    else if (key=='-') {
      int oldFilter = sc.selectedFilter;
      sc.selectFilter(sc.highestFilter);
      if (sc.filters[sc.selectedFilter] instanceof DebugDrawer) {
        sc.toggleMuteSelected();
      }
      sc.selectFilter(oldFilter);
      println("Selected sc " + sc.getSelectedFilterDescription());
    } else if (key=='=') {
      sc.selectFilter(sc.highestFilter);
      println("Selected sc " + sc.getSelectedFilterDescription());
    }

  }



  public HashMap[] getSerializedMap () {
    HashMap[] m = new HashMap[this.filters.length];
    for (int i = 0 ; i < this.filters.length ; i++) {
      if (filters[i]!=null)
        m[i] = (HashMap) filters[i].getPresetValues();
    }
    return m;
  }
  public void loadPreset(String filename) {
    try {
      FileInputStream f_in = new FileInputStream("D:\\code\\processing\\sketches\\vurf\\output\\" + filename);
      ObjectInputStream obj_in = new ObjectInputStream(f_in);
      HashMap[] values = (HashMap[]) obj_in.readObject();
      //println("got values " + values);
      for (int i = 0 ; i < values.length ; i++) {
        if (values[i]!=null) {
          Iterator it = values[i].entrySet().iterator();
          while (it.hasNext()) {
            Map.Entry e = (Map.Entry)it.next();
            if (filters[i]!=null)
              filters[i].changeParameterValue((String)e.getKey(),e.getValue());
          }
        }
      }
      f_in.close();
      obj_in.close();
    } catch (Exception e) {
      println("error loading preset " + filename + ": " + e);
    }
  }

  public void updateSerializeBox() {
    /*String s = "";
    for (int i = 0 ; i < this.filters.length ; i ++) {
      println("filters.length is " + filters.length);
      if (filters[i]!=null) {
        s += filters[i].serialize();
      }
    }
    myTextarea.setText(s);*/
  }

  public String toString() {
    return super.toString();// + " BUF_OUT:" + getCanvasMapping("out"); //buffers[BUF_OUT];
  }

  /*public ControllerInterface[] getControls() {
    return new ControllerInterface[] {
      new Slider(cp5, null, "slider for " + this.toString(), 0, 10, 5, 10, 10, 100, 20) //, int theX, int theY, int theWidth, int theHeight)
    };

  }*/

  transient controlP5.Toggle muteController;
  transient controlP5.Textfield myTextarea;
  transient controlP5.Textfield saveFilenameController;
  transient controlP5.Button saveButton;
  transient controlP5.Button loadButton;

  public void controlEvent (CallbackEvent ev) {
    //println("controlevent in " + this);
    if (ev.getAction()==ControlP5.ACTION_RELEASED) {
      if (ev.getController()==this.muteController) {
        this.setMuted(muteController.getState());
      }/*
      else if (ev.getController()==this.saveButton) {
        println("save preset " + getSceneName());
        //this.savePreset(saveFilenameController.getText(), getSerializedMap());
        this.savePreset(getSceneName());
      }
      else if (ev.getController()==this.loadButton) {
        println("load preset");
        this.loadPreset2(getSceneName()); //saveFilenameController.getText());
      }*/
    }
  }

  /*public void savePreset(String filename) {
	  ((VurfEclipse)APP.getApp()).io.serialize(filename, this);
  }
  public void loadPreset2(String filename) {
    Scene s = ((VurfEclipse)APP.getApp()).io.deserialize(filename, this.getClass());
    s.setSceneName(this.getSceneName());
    cp5.remove(this.tabName);
    this.setupControls(cp5, tabName);
    ((VurfEclipse)APP.getApp()).pr.replaceScene(cp5, this, s);
  }*/

  transient ControlP5 cp5;
  String tabName;
  boolean doneControls = false;

  private Integer[] palette;
  public void setupControls(ControlP5 cp5, Tab tab) {
    println("Scene#setupControls() in " + this);
    if (doneControls) return;
    doneControls = true;
    this.cp5 = cp5;
    this.tabName = tab.getName();

    int margin = 5;
    int lm = 10;
    int topMargin = 40;
    int size = 20;

    int currentY = topMargin;
    
    this.muteController = cp5.addToggle("mute_"+tabName)
      .setSize(size,size)
      .setValue(isMuted())
      //.setPosition(lm, currentY)
      .setLabel("Mute Scene")
      .plugTo(this, "setMuted")
      //.addCallback(this)
      .moveTo(tabName)
      .linebreak()
      ;
    currentY += size + margin;
    this.muteController.getCaptionLabel().alignY(ControlP5.CENTER);

    /*this.saveButton = cp5.addButton("save_" + tabName)
      .setLabel("save")
        .setSize(size, size)
          .moveTo(tabName)
            //.plugTo(this)
            //.addCallback(this)
            //.addCallback(this)
            .linebreak()
              ;

    this.loadButton = cp5.addButton("load_" + tabName)
      .setLabel("load")
        .setSize(size, size)
          .moveTo(tabName)
            //.plugTo(this)
            ///.setId(1)
            //.addCallback(this)
            //.addCallback(this)
            .linebreak()
              ;
    */
    //cp5.register(this, 0, loadButton);

    //cp5.addCallback(this, new controlP5.Controller[] { saveButton, loadButton });
    cp5.addCallback(this);

    //saveFilenameController = cp5.addTextfield("filename for saving " + tabName + this).setSize(size*10,size*5).setValue("value from ").moveTo(tabName).setLabel("error!").setPosition(size*20,currentY);

    //cp5.addSlider("test " + this.toString()).setPosition(5,20).moveTo(tabName);
 //   for (int i = 0 ; i < filterCount ; i ++) {
   //println("in " + this + " filters length is " + this.filters.length);
    for (int i = 0 ; i < this.filters.length ; i ++) {
      if (filters[i]!=null) {
        println(">>>>>>>>>>>>>>>>>About to setupControls for " + filters[i]);
        //cp5.addToggle("mute_" + tabName + "["+i+"]: " + filters[i])
        /*filters[i].muteController = cp5.addToggle("mute_" + tabName + "["+i+"]: " + filters[i])
           .setLabel("Mute " + filters[i])
           .setSize(size,size)
           .setValue(filters[i].isMuted())
           //.setPosition(lm, currentY+=(size+margin))
           .plugTo(filters[i], "setMuted")
           .moveTo(tabName)
           .addCallback(filters[i])
           .linebreak()
           ;*/
        filters[i].setupControls(cp5,tab);
        
        println("<<<<<<<<<<<<<<<<did setupcontrols for " + filters[i]);

      }
    }



  }

  @Override
  public Object target(String path, Object payload) {
	  println("#target('"+path+"', '"+payload+"')");
	  if ("/mute".equals(path.substring(path.length()-5, path.length()))) {
		  this.toggleMute();
		  return this.isMuted()?"Muted":"Unmuted";
	  }
	  return payload;
  }

  public HashMap<String, Targetable> getCustomTargetURLs() {
	  return new HashMap<String,Targetable>();
  }

  public HashMap<String,Targetable> getTargetURLs() {
	HashMap<String, Targetable> urls = new HashMap<String,Targetable>();
	Scene s = this;
	// add a 'mute' url for the Scene
	if (s instanceof Mutable) {
		urls.put(s.getPath() + "/mute", s);
		println(this + ": added Scene's url '" + s.getPath() + "/mute' mapped to " + s);
	}

	// loop over all the Filters; add the URLs for each Filter
	Iterator fit = s.getFilters().iterator();
	while (fit.hasNext()) {
		Filter f = (Filter) fit.next();
		urls.putAll(f.getTargetURLs());
	}

	urls.putAll(getCustomTargetURLs());

	return urls;
  }
	public double getTimeScale() {
		// TODO Auto-generated method stub
		return host.getTimeScale();
	}
	public boolean hasPalette() {
		// TODO Auto-generated method stub
		return this.palette!=null;
	}
	public Integer[] getPalette() {
		// TODO Auto-generated method stub
		return this.palette;
	}
	public void setPalette(Integer[] intColourArray) {
		this.palette = intColourArray;
	}
}
