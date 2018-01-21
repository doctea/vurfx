package vurfeclipse.projects;

import vurfeclipse.APP;
import vurfeclipse.Canvas;
import vurfeclipse.IOUtils;
import vurfeclipse.Targetable;
import vurfeclipse.VurfEclipse;
import vurfeclipse.connectors.RestConnector;
import vurfeclipse.connectors.XMLSerializer;
import vurfeclipse.filters.*;
import vurfeclipse.scenes.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.Map.Entry;

import vurfeclipse.filters.Filter;
import vurfeclipse.parameters.Parameter;
import vurfeclipse.scenes.Scene;
import vurfeclipse.sequencers.SceneSequencer;
import vurfeclipse.sequencers.SequenceSequencer;
import vurfeclipse.sequencers.Sequencer;
import vurfeclipse.streams.*;
import vurfeclipse.ui.ControlFrame;
import controlP5.*;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import sun.security.jca.GetInstance.Instance;


public abstract class Project implements Serializable {
	public int w,h;
	public String gfx_mode;

	
	public static Project bootProject(int w, int h, String filename) {
		// load a project from XML....!
		//HashMap<String,HashMap<String,Object>> input = pr.readSnapshotFile(filename);
		Project pr = new SavedProject(w, h).setSnapshotFile(filename); //.loadSnapshotsetLoaded(loadSnapshot(filename));
		
		return pr;
	}

	public static Project chooseProject(int desired_width, int desired_height, Object what) {
		// TODO Auto-generated method stub
		if (what instanceof Class) {
			return Project.createProject(desired_width, desired_height, (Class)what);
		} else if (what instanceof String && ((String)what).endsWith(".class")) {
			// instantiate Project class from what
			return Project.createProject(desired_width,desired_height, (String)what);
		} else if (what instanceof String) {
			// assume filename
			return Project.bootProject(desired_width, desired_height, (String) what);
		}
		return null;
	}

	private static Project createProject(int desired_width, int desired_height, String classname) {
		try {
			return createProject(desired_width,desired_height,Class.forName(classname));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static Project createProject(int desired_width, int desired_height, Class clazz) {
		try {
			//clazz = Class.forName(classname);
			//System.out.println (clazz.getConstructors());
			//Constructor<?> ctor = clazz.getConstructors()[0]; //[0]; //Scene.class, Integer.class);
			System.err.println("about to try and get constructor for Project '" + clazz + "'");
			Constructor<?> ctor = clazz.getConstructor(Integer.TYPE,Integer.TYPE);
			return (Project) ctor.newInstance(desired_width, desired_height); //(Scene)null, (int)0);
			//Object seq = ctor.newInstance(); //(Scene)null, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Project(int w, int h) {
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
	synchronized public void addCanvas(String name, Canvas canvas) {
		canvases.put(name,canvas);
		println("Project#addCanvas added " + name);
		//makeBuffersCompatible(name,canvas);
	}
	public Canvas getCanvas(String name) {
		try{
			if (canvases.get(name)==null) {
				println("Project#getCanvas couldn't find '" + name + "'!!!!!!!!!!!!!!! - creating one at " + "/" + name + "!");
				return createCanvas(/*"/"+*/name,name);	// disabled adding leading slash 2017-12-12 as part of loading from xml...
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
	public HashMap<String, Integer> getBufferMappings() {
		return (HashMap<String, Integer>)this.mappings;
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
	synchronized public Canvas getOutputCanvas() {
		//return buffers[BUF_OUT];
		return getCanvas(getPath()+"out");
	}

	protected Sequencer sequencer;

	synchronized public boolean processSequencer(int time) {
		return this.sequencer.runSequences(); //time);// TODO: maybe this is where time should flow in..?
	}

	synchronized public boolean processStreams(int time) {
		return this.sequencer.runStreams(time);
	}



	/////////////// Scene stuff
	ArrayList<Scene> scenes = new ArrayList<Scene>();
	Scene selectedScene;
	int selectedSceneIndex = 0;

	//public abstract boolean initialise ();
	transient PGraphics off;
	public boolean initialise () {
		println("Project#initialise:");

		initialiseBuffers();

		setupSequencer();
		
		setupStreams();

		setupScenes();

		//initialiseScenes();
		//initialiseScenes();

		//if (cp5!=null) {
		/*if (((VurfEclipse)APP.getApp()).enablecp5) {
        println("Project#initialise about to call setupControls");
    	setupControls(((VurfEclipse)APP.getApp()).getCF());
    }*/

		setupRest();
		setupExposed();

		initialised = true;
		return true;
	}

	public Scene getSceneForPath(String path) {
		return (Scene)getObjectForPath(path);
	}

	public Object getObjectForPath(String path) {
		// loop over the scenes and check for one with the same name as the first part of path; then pass the rest to getObjectForPath() for the second part..
		if (path=="/") return this;
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
					if (spl.length>4) {
						//println("#getObjectForPath('"+path+") going to call getObjectForPath on " + s + ", looking for path '"+spl[3]+"/"+spl[4]);
						return s.getObjectForPath(spl[3]+"/"+spl[4]);
					} else if (spl.length>3) {
						//println("#getObjectForPath('"+path+") going to call getObjectForPath on " + s + ", looking for path '"+spl[3]);
						return s.getObjectForPath(spl[3]);	// mute etc
					} else {
						return s;
					}
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

	public boolean initialiseBuffers() {
		println(this.toString() + " initialising buffers");
		return true;
	};

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

	public void applyGL(Canvas offscreen) {
		applyGL(offscreen, this.w, this.h);
	}
	public void applyGL(Canvas offscreen_canvas, int w, int h) {
		//public void applyGL(PGraphics gfx) {
		//Iterator it = Arrays.asList(scenes).iterator();

		PGraphics offscreen = offscreen_canvas.getSurf(); 

		//offscreen.beginDraw();
		//offscreen.beginDraw();
		//gfx.clear(0);
		//GLGraphicsOffScreen temp = createGLBuffer(w,h,gfx_mode);
		//offscreen.background(0,0,0,255);  // added
		//gfx.clear(0);


		Canvas out = getCanvas(getPath()+"out");
		out.getSurf().imageMode(APP.getApp().CENTER);

		//out.getSurf().background(0);

		//offscreen.background(APP.getApp().random(255));

		//offscreen.endDraw();
		Iterator<Scene> it = scenes.iterator();
		while(it.hasNext()) {
			Scene sc = (Scene) it.next();
			//println("Applying to " + sc.toString() + " to " + sc.getSceneName());
			//sc.applyGL(gfx);
			if (shouldDrawScene(sc)) {
				//println("Should draw " + sc + " to " + out.getSurf());
				sc.applyGLtoCanvas(out); //getCanvas(getPath()+"out"));
				//sc.applyGL(buffers[BUF_OUT]);
				//sc.applyGL(off);
			}
		}
		////gfx.image(buffers[BUF_OUT].getTexture(),0,0,w,h);

		//println("Outputting to " + offscreen);
		/*offscreen.beginDraw();
    out.getSurf().imageMode(APP.getApp().CENTER);
    offscreen.image(out.getSurf(),0,0,w,h);//w,h);
    offscreen.endDraw();*/

		//offscreen.endDraw();
		////gfx.image(buffers[BUF_INP1].getTexture(),0,0,w,h);
		////gfx.image(off.getTexture(),0,0,w,h);
	}


	public boolean shouldDrawScene(Scene sc) {
		if (!sc.isMuted())
			return true;
		return false;
	}

	@Deprecated
	public Project loadSnapshot() {
		return loadSnapshot(this.getClass().getSimpleName()+".xml");
	}
	@Deprecated
	public Project loadSnapshot(String filename) {
		println("loadSnapshot from '" + filename + "'");
		//return (Project) ((VurfEclipse)APP.getApp()).io.deserialize(filename+".vj", Project.class);
		//HashMap<String, HashMap<String, Object>> input;
		return loadSnapshot(readSnapshotFile(filename));
	}
	@Deprecated
	public Project loadSnapshot(HashMap<String, HashMap<String, Object>> input) {

		HashMap<String,Object> target_seq = input.get("/seq");

		// get /seq params
		// will also trigger load of a new sequence if it gets one from /seq/changeTo 
		if (target_seq!=null) {
			input.remove("/seq");

			// process sequencer params
			for (Entry<String, Object> e : target_seq.entrySet()) {
				this.sequencer.target(e.getKey(), e.getValue());
			}
		}

		// get /project params
		if (input.containsKey("/project")) {
			HashMap<String,Object> target_pr = input.get("/project");
			input.remove("/project");

			if (target_pr!=null) {
				for (Entry<String, Object> e : target_pr.entrySet()) {
					this.target(e.getKey(), e.getValue());
				}
			}
		}

		// everything that's left in input after being dealt with up above should have a Scene's targetable URL as its key, and a HashMap of targetable URLs of Parameters to apply to that Scene
		// process Parameter params - loop over every scene and call loadParameters
		// DEPRECATED THIS -- FROM NOW ON, SHOULD SAVE THESE UNDER THE /seq/scene_parameters KEY!!
		if (!target_seq.containsKey("scene_parameters")) {
			for (Entry<String, HashMap<String, Object>> e : input.entrySet()) {
				Scene s = (Scene) this.getObjectForPath(e.getKey());
				if(s==null) {
					System.err.println ("Couldn't find a targetable for key '"+e.getKey()+"'!!!");
				} else {
					s.loadParameters(e.getValue());
				}
			}
		}

		return this;
	}

	public HashMap<String, HashMap<String, Object>> readSnapshotFile(String filename) {
		try {
			//input ((VurfEclipse)APP.getApp()).io.deserialize(filename, HashMap.class);
			return (HashMap<String, HashMap<String, Object>>) XMLSerializer.read(filename);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.err.println("Caught " + e1 + " trying to load '" + filename + "'");
			e1.printStackTrace();
			return null;
		}
	}

	public void saveSnapshot() {
		saveSnapshot(
				this.getClass().getSimpleName()
				//+"_"+APP.getApp().millis()
				+((VurfEclipse)APP.getApp()).dateStamp()
				+".xml");
	}
	public void saveSnapshot(String filename) {
		println("SAVING SNAPSHOT TO '" + APP.getApp().sketchPath() + filename + "'");

		//saveIndividualParts(filename);
		//((VurfEclipse)APP.getApp()).io.serialize(filename + ".vj", this); //getSelectedScene().getFilter(2)); //getCanvas("/out"));
		//io.serialize("test-serialisation-2", new testsave()); //getCanvas("/out"));
		try {
			XMLSerializer.write(collectSnapshot(filename), APP.getApp().sketchPath() + filename);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Caught " + e.toString() + " trying to save to '" + filename + "'");
			e.printStackTrace();
		}
	}
	public HashMap<String, HashMap<String, Object>> collectSnapshot(String filename) {
		HashMap<String,HashMap<String,Object>> output = new HashMap<String,HashMap<String,Object>>();

		HashMap<String,Object> projectParams = this.collectParameters();
		output.put("/project",projectParams);

		if (null!=this.sequencer) {
			HashMap<String,Object> sequencerParams = this.sequencer.collectParameters();
			output.put("/seq", sequencerParams); //new HashMap<String,HashMap<String,Object>>().put("current_sequence", this.sequencer.getCurrentSequenceName()));
		}
		
		LinkedHashMap<String, Object> projectSetup = this.collectProjectSetup();
		output.put("/project_setup",projectSetup);

		// collect all the scenes
		//output.putAll(this.collectSceneParameters());

		//((VurfEclipse)APP.getApp()).io.serialize(filename, output);
		return output;
	}
	private LinkedHashMap<String, Object> collectProjectSetup() {
		LinkedHashMap<String, Object> output = new LinkedHashMap<String,Object>();
		// save buffers/canvases
		output.put(getPath()+"project_setup/mappings", this.getBufferMappings());
		// save scene configuration
			// save filter canvas mappings		
	
		for (Scene s : this.getScenes()) {
			output.put(s.getPath(), s.collectSceneSetup());
		}
		
		return output;
	}
	public HashMap<String,HashMap<String,Object>> collectSceneParameters() {
		HashMap<String,HashMap<String,Object>> output = new HashMap<String,HashMap<String,Object>>();
		for (Scene s : this.getScenes()) {
			output.put(s.getPath(), s.collectParameters());
		}
		return output;
	}
	private HashMap<String, Object> collectParameters() {
		HashMap<String,Object> params = new HashMap<String,Object>();
		return params;
	}

	@Deprecated
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

	public Sequencer getSequencer() {
		return this.sequencer;
	}

	public void sendKeyPressed(char key) {
		//Scene sc = scenes.iterator().next();//.first();
		if (isDisableKeys()) {
			println("keys disabled, ignoring " + key);
			return;
		}
		if (key=='[') {
			selectPreviousScene();
		} else if (key==']') {
			selectNextScene();
		} else if (key=='-') {
			Iterator<Scene> i = scenes.iterator();
			while(i.hasNext())
				((Scene)i.next()).sendKeyPressed('-');
			/* if (key=='\'') {  // SOLO SCENE
	      Iterator i = scenes.iterator();
	      while (i.hasNext()) {
	        Scene sc = (Scene)i.next();
	        if (sc!=this.getSelectedScene())
	          sc.setMuted(true);
	        else
	          sc.setMuted(false);
	      }
	    } else*/ 
			/* }else if (key=='p') {
    	println(rsConn.getURLs().toString());
    	System.exit(0);
    } */ 
		} else if (key=='s') {
			//println(this.getSelectedScene().getSelectedFilter().serialize());
			//println(this.serialize());
			((SequenceSequencer) this.sequencer).preserveCurrentSceneParameters();

			saveSnapshot();
		} else if (key=='S') {
			//loadSnapshot();
			//APP.getApp().selectInput("Select a file to load", "loadSnapshot"); //- DOESNT WORK ?
			((SequenceSequencer) this.sequencer).preserveCurrentSceneParameters();
			
			saveSnapshot();
			saveSnapshot("MutanteProject-incremental.xml");
		} else if (this.sequencer.sendKeyPressed(key)) {
			println ("Key " + key + " handled by sequencer!");
		} else {
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


	public transient RestConnector rsConn = new RestConnector(this);
	public void setupRest() {
		//rsConn = new RestConnector(this);
		rsConn.start();
		Thread t = new Thread(rsConn);
		t.start();
	}

	public void setupExposed() {

	}

	public void setupControls(ControlFrame cf) {
		if (!((VurfEclipse)APP.getApp()).enablecp5) return;

		//ui.ControlFrame cf = ((VurfEclipse)APP.getApp()).getCF();
		//ControlP5 cp5 = ((VurfEclipse)APP.getApp()).getCF();

		//println("Project#setupControls about to get controlwindow");
		//ControlFrame cw = ((VurfEclipse)APP.getApp()).getCW();

		println("Project#setupControls about to setupControls() for sequencer " + this.sequencer);
		this.sequencer.setupControls(cf, "Sequencer");

		println("Project#setupControls about to grab cp5 before scene loop..");
		final ControlP5 cp5 = cf.control();

		//this.setupMonitor(cp5);

		/*ListBox lb = cp5.addScrollableList("preset")
    			.setSize(200, 100)
    			.setItemHeight(20)
    			.addItems(this.getAvailableFiles());*/

		println("Project#setupControls about to loop over scenes ("+scenes.size()+" scenes to process)");
		Iterator<Scene> i = scenes.iterator();
		int c = 0;
		
		//cp5.addConsole(cp5.addTextarea("console", "", 0, 20, cf.displayWidth, cf.displayHeight-20));
		
		Tab sceneTab = cp5.addTab("Scenes");

		Accordion accordion = cp5.addAccordion("acc").setWidth(cf.displayWidth);

		Scene n;
		while(i.hasNext()) {
			n = (Scene)i.next();
			println(c + ": Project#setupControls() got scene " + n.getSceneName());
			String tabName = "["+c+"] " + n.getSceneName(); //getClass();
			//ControlP5 cp5 = ((VurfEclipse)APP.getApp()).getCP5();
			//Tab tab = cp5.addTab(tabName);

			Group g = cp5.addGroup(tabName);

			println("added tab " + tabName);
			//ControllerInterface[] controls = ((Scene)i.next()).getControls();
			//cp5.begin(10,40);
			((Scene)n).setupControls(cf,g);//tab);
			println("done setupControls for " + n);
			//cp5.end();

			accordion.addItem(g);

			/*for (int n = 0 ; n < controls.length ; n++) {
        cp5.getTab("Scene " + c).add(controls[n]).moveTo("Scene " + c);
        //cp5.addSlider(controls[n]).moveTo("Scene " + c);
      }*/
			c++;
			//((Scene)i).setupControls(cp5);
		}
		accordion.setPosition(0, 20);
		//accordion.open();
		accordion.setCollapseMode(Accordion.MULTI);

		accordion.moveTo(sceneTab);

		Tab monitorTab = cp5.addTab("Monitor");

		//for (String canvas_name : this.canvases.keySet()) {
		final Project pr = this; //.getCanvas("out");
		controlP5.Canvas cp5canvas = new controlP5.Canvas() {
			@Override
			public synchronized void draw(PGraphics pg) {
				// renders a square with randomly changing colors
				// make changes here.
				//pg.fill(100);
				//pg.rect(APP.getApp().random(255)-20, APP.getApp().random(255)-20, 240, 30);
				//pg.fill(255);
				//pg.beginDraw();

				//cp5.setGraphics(pr.getCanvas(getPath()+"out").getSurf(), 0, 0);

				if (pr.isInitialised()) {
					//pg.beginDraw();

					pg.text("This text is drawn by MyCanvas !!", 0/*APP.getApp().random(255)*/,APP.getApp().random(255));
					pg.beginDraw();
					//pg.image((PImage) pr.getCanvas("/out").getSurf().getCache(pr.getCanvas("/out").getSurf()),0 ,0);
					APP.getApp().spout.receiveTexture(pg); //,0,0);
					pg.endDraw();

					/*pr.getCanvas(getPath()+"out").getSurf().loadPixels();
        			    PImage i = pr.getCanvas(getPath()+"out").getSurf().get(); 
    			    	pg.image(i,0,150,w/8,h/8);
    			    	pg.endDraw();*/
				}
				//pg.endDraw();
				//
			}
		};
		monitorTab.addCanvas(cp5canvas);
		//cp5canvas.moveTo(monitorTab);
		//}


		//this.initialised = true;
		println("Project#setupControls()----------------------------------------------------------------------------------<END");
	}



	private void setupMonitor(ControlP5 cp5) {
		// TODO Auto-generated method stub
		//controlWindow = cp5.addControlWindow("controlP5window",100,100,width/2,height/2,30);
		final Project p = this;
		//cp5.getWindow().setUpdateMode(ControlWindow.NORMAL);
		//((VurfEclipse)APP.getApp()).getCW()
		//ControlWindowCanvas monitor = ((VurfEclipse)APP.getApp()).getCW().control().addCanvas(new ControlWindowCanvas() {
		//		@Override
		//		public void draw(PApplet theApplet) {
		// TODO Auto-generated method stub
		//theApplet.background((int) (Math.random()*255));
		/*if (p.canvases.containsKey(p.getPath()+"out")) {
					theApplet.pushMatrix();
					//theApplet.image(p.getCanvas(p.getPath()+"out").getSurf().get(), 0, theApplet.height - p.h/8, p.w/8, p.h/8);
					theApplet.color((int) (Math.random()*255));
					theApplet.rect(50, 50, 200, 200);
					theApplet.rectMode((int) (Math.random()*10));
					theApplet.popMatrix();
				}*/
		//		}
		// }); //.moveTo(((VurfEclipse)APP.getApp()).getCW().getCurrentTab());

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
	public Map<String, Targetable> getTargetURLs() {
		LinkedHashMap<String, Targetable> urls = new LinkedHashMap<String, Targetable>();

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
	private boolean initialised;
	private HashMap<String,Integer> guids = new HashMap<String,Integer>();
	public boolean disableKeys = false;
	public void println(String text) {		// debugPrint, printDebug -- you get the idea
		if (outputDebug) System.out.println("P " + (text.contains((this.toString()))? text : this+": "+text));
	}

	public boolean isInitialised() {
		// TODO Auto-generated method stub
		return this.initialised;
	}
	public void target(String key, Object value) {
		println("#target("+key+","+value+")");

		Targetable t = (Targetable) this.getObjectForPath(key);
		if (t==null) {
			System.err.println("#target("+key+","+value+") in " + this + " couldn't find the object to target!");
		} else {
			println("#target("+key+","+value + ") got targetable object " + t.getClass() + " " + t);

			if (value instanceof Parameter) {
				key = ((Parameter)value).getName();
				value = ((Parameter)value).value;
			}

			if (t instanceof Filter) {
				t.target(key, value);
			} else if (t instanceof Parameter) {
				t.target(key, value);
			} else {
				println("#target('"+key+"','"+value+"'):  Unhandled Targetable type '"+t.getClass().getName()+"'");
			}
		}	
	}
	public VurfEclipse getApp() {
		// TODO Auto-generated method stub
		return (VurfEclipse) APP.getApp();
	}


	abstract public void initialiseStreams();


	public Integer getGUID(String simpleName) {
		// TODO Auto-generated method stub
		if (!this.guids.containsKey(simpleName)) this.guids.put(simpleName, 0);
		this.guids.put(simpleName, 1 + this.guids.get(simpleName));
		return this.guids.get(simpleName);
	}

	public String[] getSceneUrls() {
		String[] urls = new String[this.getScenes().size()];
		int n = 0;
		for (Scene s : this.getScenes()) {
			urls[n++] = s.getPath();
		}
		return urls;
	}

	public boolean isDisableKeys() {
		return disableKeys;
	}

	public void setDisableKeys(boolean disableKeys) {
		this.disableKeys = disableKeys;
	}



}
