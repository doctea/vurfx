package vurfeclipse.projects;

import vurfeclipse.APP;
import vurfeclipse.Canvas;
import vurfeclipse.IOUtils;
import vurfeclipse.Targetable;
import vurfeclipse.VurfEclipse;
import vurfeclipse.connectors.RestConnector;
import vurfeclipse.filters.*;
import vurfeclipse.scenes.*;

import java.io.Serializable;
import java.util.*;

import vurfeclipse.filters.Filter;
import vurfeclipse.scenes.Scene;
import vurfeclipse.sequence.SceneSequencer;
import vurfeclipse.sequence.Sequencer;
import vurfeclipse.streams.*;
import codeanticode.glgraphics.*;
import controlP5.*;
  

public abstract class Project implements Serializable {
  public int w,h;
  public String gfx_mode;
  
  public Project(int w, int h, String gfx_mode) {
    this.w = w;
    this.h = h;
    this.gfx_mode = gfx_mode;
  }
  
  int BUF_OUT = 0;
  int BUF_INP0 = 1;
  int BUF_INP1 = 2;
  int BUF_INP2 = 3;
  
  int BUF_TEMP1 = 4;
  int BUF_TEMP2 = 5;
  
  HashMap<String,Canvas> canvases = new HashMap<String,Canvas>();
  public void addCanvas(String name, Canvas canvas) {
      canvases.put(name,canvas);
      println("Project#addCanvas added " + name);
      //makeBuffersCompatible(name,canvas);
  }
  public Canvas getCanvas(String name) {
    try{
      if (canvases.get(name)==null) {
        println("Project#getCanvas couldn't find '" + name + "'!!!!!!!!!!!!!!! - creating!");
        return createCanvas("/"+name,name);
      }
      //System.out.println("Project#getCanvas('" + name + "') returning " + canvases.get(name) + " with buffer " + canvases.get(name).surf);      
      return canvases.get(name);
    } catch (Exception e) {
      //throw new NullPointerException ("Project#getCanvas Couldn't find a canvas for path 'name'!");
      println("Project#getCanvas threw " + e.toString() + " for path '" + name + "'!");
      //e.printStackTrace();//Couldn't find a canvas for path '" + name + "'!");
      return null;
    }
  }
  /*public void makeBuffersCompatible(String name,Canvas canvas) {
    if (mappings==null) 
      setupBufferMappings();
    buffers[(Integer)mappings.get(name)] = canvas.surf;
  } */  
  HashMap<String, Integer> mappings;
  public void setupBufferMappings() {
    mappings = new HashMap<String, Integer>();
    mappings.put(getPath()+"out", BUF_OUT);
    mappings.put(getPath()+"inp0", BUF_INP0);
    mappings.put(getPath()+"inp1", BUF_INP1);
    mappings.put(getPath()+"inp2", BUF_INP2);    
    mappings.put(getPath()+"temp1", BUF_TEMP1);        
    mappings.put(getPath()+"temp2", BUF_TEMP2);            
  }
  
  public Canvas createCanvas(String path, String canvasName, int width, int height) {
	  //mappings.put(path + "/" + name, makeCanvas(w,h,gfx_mode,name));
		//int w = this.w, h = this.h;
		println("createCanvas for '" + canvasName + "'");
	  Canvas c = Canvas.makeCanvas(width,height,gfx_mode,canvasName);
	  addCanvas(path, c);
	  println("Project#createCanvas('" + path + "','" + canvasName + "') got '" + c.getSurf() + "'");
	  return c;
	}
  
  public Canvas createCanvas(String path, String canvasName) {
	  return this.createCanvas(path, canvasName, this.w, this.h);
  }
  
  //GLGraphicsOffScreen buffers[] = new GLGraphicsOffScreen[8];
  
  //public GLGraphicsOffScreen getOutputBuffer() {
  public Canvas getOutputCanvas() {
    //return buffers[BUF_OUT];
    return getCanvas("out");
  }
  
////// sequencer stuff?
  boolean enableSequencer = true;
  protected Sequencer sequencer;
  
  public boolean processSequencer(int time) {
	  if (enableSequencer) {
		  this.sequencer.runSequences();
	  }
	return enableSequencer;
  }
  
/////////// Event stuff
  HashMap<String, Stream> streams = new HashMap<String, Stream>(); // Stream
  
  //public abstract boolean initialise();
  boolean enableStreams = true;
  
  public void addStream(String streamName, Stream st) {
    //this.streams.put(streamName, st);
    this.streams.put(streamName, st);
  }
  public Stream getStream(String streamName) {
    return (Stream) this.streams.get(streamName); 
  }
  
  public boolean processStreams(int time) {
    if (enableStreams) {
      Iterator<?> i = streams.entrySet().iterator();
      while (i.hasNext()) {
        //println("processStreams in " + this);
        Map.Entry e = (Map.Entry) i.next();
        Stream s = (Stream) e.getValue();
        s.processEvents(time);
        s.deliverEvents();
      }
    }
    
    return true;
  }
  
  public void enableStreams(boolean on) {
    this.enableStreams = true;
  }
  public void disableStreams(boolean off) {
    this.enableStreams = false;
  }
  public void toggleStreams() {
    this.enableStreams = !this.enableStreams;
  }
/////////////// end Event stuff
  
  
  
/////////////// Scene stuff  
  ArrayList<Scene> scenes = new ArrayList<Scene>();
  Scene selectedScene;
  int selectedSceneIndex = 0;
  
  //public abstract boolean initialise ();
  transient GLGraphicsOffScreen off;
  public boolean initialise() {
	println("Project#initialise:");
    
    off = Canvas.createGLBuffer(w,h,gfx_mode);
    initialiseBuffers();
    
    setupStreams();
    
    setupSequencer();
    
    setupScenes();
    
    initialiseScenes();
    
    //if (cp5!=null) { 
        println("Project#initialise about to call setupControls");
    	setupControls();
    //}
    
    setupRest();
    
    setupExposed();
    
    return true;
  }
  
  public Scene getSceneForPath(String path) {
    return (Scene)getObjectForPath(path);
  }
  
  public Object getObjectForPath(String path) {
    // loop over the scenes and check for one with the same name as the first part of path; then pass the rest to getObjectForPath() for the second part..
    String[] spl = path.split("/",5); //, 3);
    //println("spl[1] is " + spl[1]);
    if ("sc".equals(spl[1])) {
    	//println("got sc, looking for " + spl[2]);
	    Iterator<Scene> it = scenes.iterator();
	    while (it.hasNext()) {
	      Scene s = (Scene)it.next();
	      //println("Project#getObjectForPath("+path+") checked '" + s.getSceneName() + "' against '" + spl[1] + "'"); //against stored scene " + s.getSceneName());
	      if (s.getSceneName().equals(spl[2])) { //getSceneName().equals(s)) {
	    	//println("Found " + s.getSceneName());
	        //return s;
	        // ask it to get the rest of the path for us
	        if (spl.length>3) {
	          return s.getObjectForPath(spl[3]+"/"+spl[4]);
	        } else
	          return s;
	      }
	    }
    }
    //println("couldn't find object for path " + path + "!");
    return null;
  }
  
  public ArrayList<Scene> getScenes () {
	  return this.scenes;
  }
  
 /* {
    //this.initialiseScenes();
    //return true; 
  }*/
  
  public abstract boolean setupStreams();
  public abstract boolean setupScenes();
  
  public boolean setupSequencer() {
	  println("#setupSequencer");
	  //System.exit(1);
	  sequencer = new SceneSequencer(this,w,h);	// default to a SceneSequencer type
	  return true;
  };
  
  public abstract boolean initialiseBuffers();
  
  public boolean initialiseScenes() {
    println("== initialiseScenes() in " + this);
    Iterator<Scene> it = scenes.iterator();
    while(it.hasNext()) {
      Scene sc = (Scene) it.next();
      sc.initialise();
      sc.initialiseFilters();
      if (selectedScene==null) selectedScene = sc;
    }     
    return true;
  }
  
  public void replaceScene(ControlP5 cp5, Scene old, Scene nouveau) {
    cp5.addCallback(nouveau);
    this.scenes.set(scenes.indexOf(old), nouveau); 
    //old.destroy();
  }
  
  public Scene addBlankerScene (String canvasName) {
	  return this.addSceneOutputCanvas(
			  new BlankerScene(this,w,h)
			  	.setSceneName("BlankerScene")
			  	.setOutputCanvas(canvasName), canvasName
			  );
	  
	  /*
	  Scene sc = new SimpleScene(this, w, h).setSceneName("BlankerScene").setOutputCanvas(canvasName);
	  BlankFilter bf = (BlankFilter) new BlankFilter(sc).setFilterName("BlankFilter").setOutputCanvas(canvasName);
	  sc.addFilter(bf);
	  return this.addSceneOutputCanvas(sc, canvasName);*/
		//Scene sc = this.addScene();
/*		  blank.beginDraw();	// this is here to blank the buffer before a redraw.  if we disable it, it looks rad as fuck!! and woudl probably where we could put in a motion blur effect too.
		  blank.applyToBuffers();
		  blank.endDraw();*/
		//return sc;
  }
  
  public Scene addScene(Scene sc) {
    this.scenes.add(sc);
    return sc;
  }
  public Scene addSceneOutputCanvas(Scene sc, String oc) {
    sc.setOutputCanvas(oc);
    this.addScene(sc);
    return sc;
  }
  /*public void addSceneOutput(Scene sc, GLGraphicsOffScreen ob) {
    sc.setOutputBuffer(ob);
    this.addScene(sc); 
  }*/
  public Scene addSceneInputCanvas(Scene sc, String ic) {
    sc.setInputCanvas(ic);
    this.addScene(sc);
    return sc;
  }

  public Scene addSceneInputOutputCanvas(Scene sc, String ic, String oc) {
    //or setINputBUffer / setOutputBuffer for compatiblity ..     
    sc.setInputCanvas(ic);
    sc.setOutputCanvas(oc);
    this.addScene(sc);
    return sc;
  }
  
  /*public void processStreams(int time) {
    //Iterator it = Arrays.asList(scenes).iterator();
    Iterator it = scenes.iterator();
    while(it.hasNext()) {
      Scene sc = (Scene) it.next();
      sc.processStreams(time);
    }     
  }*/
  public void selectPreviousScene() {
    selectedSceneIndex--;
    if (selectedSceneIndex<0) selectedSceneIndex = scenes.size();
    selectedScene = scenes.get(selectedSceneIndex);
    selectScene(selectedSceneIndex);
  }
  public void selectNextScene() {
    selectedSceneIndex++;
    if (selectedSceneIndex>=scenes.size()) selectedSceneIndex = 0;
    selectScene(selectedSceneIndex);
  }
  public void selectScene(int index) {
    Scene sc = scenes.get(index);
    println("Selecting " + (sc.isMuted()?"MUTED":"LIVE") + " Scene[" + index + "] " + sc);
    selectedScene = sc;//scenes.get(index);     
  }
  

  public Scene getSelectedScene() {
    if (selectedScene==null) 
      selectedScene = scenes.iterator().next();
    return selectedScene;
    //return scenes.iterator().next();
  }
   
  public String getPath() {
    return "/";
  }
  
//////////// end Scene stuff
  
  /*
    ///// new (not working, abandoned) drawing code
    public void texture_notworking_applyGL(GLGraphicsOffScreen gfx) {
      Iterator it = scenes.iterator();
      while(it.hasNext()) {
        Scene sc = (Scene) it.next();
        if (shouldDrawScene(sc)) {
          sc.generateTexture();
          gfx.image(sc.getTexture(),0,0,w,h);
        }
      }    
  }*/

  public void applyGL(GLGraphicsOffScreen gfx) {
    applyGL(gfx, this.w, this.h);
  }
  public void applyGL(GLGraphicsOffScreen gfx, int w, int h) {
  //public void applyGL(PGraphics gfx) {
    //Iterator it = Arrays.asList(scenes).iterator();
    
    //gfx.clear(0);
    //GLGraphicsOffScreen temp = createGLBuffer(w,h,gfx_mode);
    gfx.background(0,0,0,255);  // added 
    gfx.clear(0);
    
    Canvas out = getCanvas(getPath()+"out");
    
    //out.getSurf().background(0);
    
    gfx.background(APP.getApp().random(255));
    
    Iterator<Scene> it = scenes.iterator();
    while(it.hasNext()) {
      Scene sc = (Scene) it.next();
      //println("Applying to " + sc.toString() + " to " + sc.getSceneName());
      //sc.applyGL(gfx);
      if (shouldDrawScene(sc)) {
    	//println("Should draw " + sc);
        sc.applyGLtoCanvas(out); //getCanvas(getPath()+"out"));
        //sc.applyGL(buffers[BUF_OUT]);
        //sc.applyGL(off);
      }
    }
    ////gfx.image(buffers[BUF_OUT].getTexture(),0,0,w,h);

    gfx.image(out.getSurf().getTexture(),0,0,w,h);//w,h);
    
    ////gfx.image(buffers[BUF_INP1].getTexture(),0,0,w,h);
    ////gfx.image(off.getTexture(),0,0,w,h);
  }


  public boolean shouldDrawScene(Scene sc) {
    if (!sc.isMuted())
      return true;
    return false;
  }
  
  static public Project loadProject() {
    return loadProject("project-test-2");
  }
  static public Project loadProject(String filename) {
    return (Project) ((VurfEclipse)APP.getApp()).io.deserialize(filename, Project.class);
  }
  
  public void saveProject() {
    saveProject("project-test-2");
  }
  public void saveProject(String filename) {
    println("SAVING TO " + filename);
    
    //saveIndividualParts(filename);
    ((VurfEclipse)APP.getApp()).io.serialize(filename, this); //getSelectedScene().getFilter(2)); //getCanvas("/out"));
    //io.serialize("test-serialisation-2", new testsave()); //getCanvas("/out"));
  }    
  public void saveIndividualParts(String filename) {
    Iterator<Scene> it = scenes.iterator();
    while (it.hasNext()) {
      Scene ss = (Scene)it.next();
      println("Serialising " + ss + " " + ss.getSceneName());
      IOUtils.makeProjectFolder(filename);
      IOUtils.serialize(filename + "/scene" + ss + ".sc", ss);
      for (int i = 0 ; i < ss.filters.length ; i++) {
        Filter f = ss.getFilter(i);
        if (f!=null) {
          String filterName = filename + "/scene" + ss;
          IOUtils.makeProjectFolder(filterName);
          String fn = filterName + "/" + "filter-" + i + "-" + ss.getSceneName() + "-" + f.getFilterLabel() + ".fi";
          //String fn = "test";
          println("serialising [" + i + "/" + ss.filters.length + "] " + f + " to " + fn);          

          IOUtils.serialize(fn, f);
        }
      }
    }
  }
  
  public String getSequenceName () {
	  if (this.sequencer!=null) return this.sequencer.getCurrentSequenceName();
	  return "none";
  }
  
  public void sendKeyPressed(char key) {
    //Scene sc = scenes.iterator().next();//.first();
    if (key=='[') {
      selectPreviousScene();
    } else if (key==']') {
      selectNextScene();
    } else  if (key=='s') {
      //println(this.getSelectedScene().getSelectedFilter().serialize());
      //println(this.serialize());
      
      saveProject();
    } else if (key==';') {
    	if (this.sequencer!=null) this.sequencer.setForward();
    } else if (key=='l') {
    	println("toggling sequencer");
    	if (this.sequencer!=null) {
    		println("toggling sequencer " + this.sequencer.toggleLock());
    	}
    } else/* if (key=='\'') {  // SOLO SCENE
      Iterator i = scenes.iterator();
      while (i.hasNext()) {
        Scene sc = (Scene)i.next();
        if (sc!=this.getSelectedScene()) 
          sc.setMuted(true);
        else
          sc.setMuted(false);
      }
    } else*/ if (key=='-') {
      Iterator<Scene> i = scenes.iterator();
      while(i.hasNext())
        ((Scene)i.next()).sendKeyPressed('-');
    } else if (key=='p') {
    	println(rsConn.getURLs().toString());
    	System.exit(0);
    }  else {
      Scene sc = this.getSelectedScene();
  
      if (sc==null) {
        println("No Scene selected in " + this);
        return;
      }

      sc.sendKeyPressed(key);
    }
  }
  
  public void finish() {
    Iterator<Scene> i = scenes.iterator();
    while (i.hasNext()) {
      ((Scene)i.next()).finish();
    }
    /*i = streams.entrySet().iterator();
    while (i.hasNext()) {
      ((Stream)(((Map.Entry)i.next()).getValue())).finish();
    }*/
  }
  
  public void updateControl (String filterPath, String name, Object value) {
    Filter f = (Filter)getObjectForPath(filterPath); 
    //println("Project#updateControl("+filterPath+","+name+","+value + ") for filterPath "+ filterPath + " and param " + name + " got " + f);
    if (null!=f)
      f.updateControl(name, value);
    //else
      //println(">>>Project#updateControl("+filterPath+","+name+","+value + ") couldn't find a filter!");
  }
  
  
  public RestConnector rsConn = new RestConnector(this);
  public void setupRest() {
	  //rsConn = new RestConnector(this);
	  rsConn.start();
	  Thread t = new Thread(rsConn);
	  t.start();
  }
  
  public void setupExposed() {

  }
  
  public void setupControls() {
    if (!((VurfEclipse)APP.getApp()).enablecp5) return;

    ControlP5 cp5 = ((VurfEclipse)APP.getApp()).getCP5();
    
    println("Project#setupControls about to get controlwindow");
    ControlWindow cw = ((VurfEclipse)APP.getApp()).getCW();
    
    
    println("Project#setupControls about to loop over scenes");
    Iterator<Scene> i = scenes.iterator();
    int c = 0;
    Scene n;    
    while(i.hasNext()) {
      n = (Scene)i.next();
      println(c + ": Project#setupControls() got scene " + n.getSceneName());
      String tabName = "["+c+"] " + n.getSceneName(); //getClass();
      //ControlP5 cp5 = ((VurfEclipse)APP.getApp()).getCP5();
      cw.addTab(tabName);
      println("added tab " + tabName);
      //ControllerInterface[] controls = ((Scene)i.next()).getControls();
      cp5.begin(10,40);
      ((Scene)n).setupControls(cp5,tabName);
      println("done setupControls for " + n);
      cp5.end();
      /*for (int n = 0 ; n < controls.length ; n++) {
        cp5.getTab("Scene " + c).add(controls[n]).moveTo("Scene " + c); 
        //cp5.addSlider(controls[n]).moveTo("Scene " + c);
      }*/
      c++;
      //((Scene)i).setupControls(cp5);
    } 
    println("Project#setupControls()----------------------------------------------------------------------------------<END");
  }
  
  

  int guid = 10000;
  public int getGUID() {
    return guid++;
  }  
  
  
  public int lerpcolour (int origin, int dest, double norm) {
	  int or,og,ob,oa;
	  int dr,dg,db,da;
	  or = (int)APP.getApp().red(origin);//(origin>>24) & 0xFF;//(int)APP.getApp().red(origin);
	  og = (int)APP.getApp().green(origin);//(origin>>16) & 0xFF;//(int)APP.getApp().green(origin);
	  ob = (int)APP.getApp().blue(origin);//(origin>>8) & 0xFF;//(int)APP.getApp().blue(origin);
	  
	  dr = (int)APP.getApp().red(dest);
	  dg = (int)APP.getApp().green(dest);
	  db = (int)APP.getApp().blue(dest);
	  

	  int outr, outg, outb;

	  int diff = (int)((Math.max(or,dr)-Math.min(or, dr)) * norm);
	  outr = Math.min(or, dr) + diff;
	  //println("diff r is " + diff);
	  
	  diff = (int)((Math.max(og,dg)-Math.min(og, dg)) * norm);	  
	  outg = Math.min(og, dg) + diff;
	  
	  diff = (int)((Math.max(ob,db)-Math.min(ob, db)) * norm);
	  outb = Math.min(ob, db) + diff;
	  
	  /*println("Blending between (" + or +","+og+","+ob+") and (" + dr + "," + dg + "," + db + ")");
	  println("--got (" + outr + "," + outg + "," + outb + ")");*/
	  
	  return APP.getApp().color(outr,outg,outb);
	  
  }
  
  public int randomColorMinimum(int minimum) {
	  int tot = 0;
	  int r=0,g=0,b=0;
	  while (tot<64) {
		  r = (int)APP.getApp().random(16,255);//, (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);;
		  g = (int)APP.getApp().random(16,255);//APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
		  b = (int)APP.getApp().random(16,255);//APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
		  tot = r+g+b;
	  }
	  
	  return APP.getApp().color(r,g,b);
  }
  public Map<? extends String, ? extends Targetable> getTargetURLs() {
		HashMap<String, Targetable> urls = new HashMap<String, Targetable>();
		
		// get all the Scene urls that are appropriate
		Iterator<Scene> it = this.getScenes().iterator();
		while (it.hasNext()) {
			Scene s = (Scene) it.next();
			urls.putAll(s.getTargetURLs());
		}
		
		// get all the Sequencer urls
		urls.putAll(sequencer.getTargetURLs());
	
		return urls;
	}
  
  
  boolean outputDebug = true;
  public void println(String text) {		// debugPrint, printDebug -- you get the idea
	  if (outputDebug) System.out.println("P " + (text.contains((this.toString()))? text : this+": "+text));
  }

  
  
}
