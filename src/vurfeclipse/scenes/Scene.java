package vurfeclipse.scenes;

import vurfeclipse.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.Map.Entry;

import processing.core.PGraphics;
import processing.core.PVector;

import vurfeclipse.filters.*;
import vurfeclipse.parameters.Parameter;
import vurfeclipse.projects.Project;
import vurfeclipse.streams.*;
import vurfeclipse.ui.ControlFrame;
import vurfeclipse.sequence.*;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.ControllerGroup;
import controlP5.ScrollableList;
import controlP5.Textlabel;

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
	public transient Filter[] filters;// = new Filter[filterCount];

	transient protected HashMap<String,Sequence> sequences = new HashMap<String,Sequence>();

	public HashMap<String,Sequence> getSequences() {
		if (sequences.size()==0) setupSequences();
		return this.sequences;
	}
	public void setupSequences() {

	}
	public Sequence getSequence(String name) {
		if (!getSequences().containsKey(name)) {
			println(this + " doesn't have a Sequence named '"+name+"'!");
			//System.exit(1);;
			//throw new Exception(this + " doesn't have a Sequence named '"+name+"'!");
		}
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
		if (this.sceneName==null) {
			//return this.getClass().toString();//.toString(;
			this.sceneName = this.getClass().getSimpleName() + ((VurfEclipse)APP.getApp()).pr.getGUID(this.getClass().getSimpleName()); //toString();
			System.err.println("Auto-set Scene name to " + this.sceneName);
		}
		return sceneName;
	}
	public Scene setSceneName(String sn) {
		println("Scene " + this + " setting sceneName to " + sn);
		this.sceneName = sn;
		return this;
	}


	public Object getObjectForPath(String path) {
		//println("Scene#getObjectForPath(" + path + ")");
		try {
			if (!path.contains("/") || path.equals("") || path.equals(this.getSceneName()) || path.equals("mute")) 
				return this;
			String spl[] = path.split("/",2);

			String spl2[] = spl[1].split("/");
			//println ("got spl " + spl.toString() + " and spl2 " + spl2.toString());
			String filterName = spl2[0];
			if (spl2.length==1) {
				return getFilter(filterName);
			} else if (spl2[1].equals("mute")) {
				return getFilter(filterName);
			} else if (spl2[1].equals("pa")) {	// is a Parameter
				if (getFilter(filterName)!=null)
					return getFilter(filterName).getParameter(spl2[2]);
			} 
		} catch (Exception e) {
			System.err.println ("caught " + e);
			System.err.println(e.getStackTrace());
		}

		return null;    		
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
	public HashMap<String,String> getCanvasMappings() {
		return this.buffermap;
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

		if (buffermap.containsKey(canvasName)) {
			// notify all filters that canvas has changed
			
			for (Filter f : this.filters) {
				if (f!=null) {
					f.changeCanvas(buffermap.get(canvasName),canvasPath);
				}
			}
		}
		
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
	public synchronized void setMuted(boolean m) {
		this.muted = m;
		if (null!=muteController) {
			muteController.setBroadcast(false);
			muteController.setValue(isMuted());
			muteController.setBroadcast(true);
		}
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
	private boolean setupFilters = false;

	public boolean initialiseFilters () {
		if (initialisedFilters) return true;
		if (!setupFilters ) setupFilters();
		for (int i = 0 ; i < this.filters.length ; i ++) {
			if (filters[i]!=null) {
				filters[i].initialise();

				/*if (filters[i].out==null) filters[i].out = buffers[BUF_OUT];
        if (filters[i].src==null) filters[i].src = buffers[BUF_SRC];*/

				//if (filters[i].out==null) filters[i].out = getCanvas("out").getSurf();
				//if (filters[i].src==null) filters[i].src = getCanvas("src").getSurf(); //setInputCanvas(getCanvas("src"));

				if (filters[i].out==null) filters[i].setOutputCanvas(getCanvasMapping("out"));
				if (filters[i].src==null) filters[i].setInputCanvas(getCanvasMapping("src"));

				filters[i].start();// 2017-10-29 --- filters were never getting started and so buffers not being initialised..?

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
		return this.registerCallbackPreset(((VurfEclipse)APP.getApp()).pr.getSequencer().getStream(streamName), eventName, callbackName);
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

	public String toString() {
		return super.toString();// + " BUF_OUT:" + getCanvasMapping("out"); //buffers[BUF_OUT];
	}

	transient controlP5.Toggle muteController;
	transient controlP5.Textfield myTextarea;
	transient controlP5.Textfield saveFilenameController;
	transient controlP5.Button saveButton;
	transient controlP5.Button loadButton;

	public synchronized void controlEvent (CallbackEvent ev) {
		if (!ev.getController().isUserInteraction()) return;
		if (ev.getAction()==ControlP5.ACTION_PRESS) {
			//println("controlevent in " + this); 

			//println (ev.getController() + " check if same as " + this.muteController);
			if (ev.getController()==this.muteController) {
				//muteController.setState(!muteController.getState());
				//muteController.setValue(muteController.getValue());
				println("it is, should be toggling state to " + muteController.getValue());
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

	public void savePreset(String filename) {
		((VurfEclipse)APP.getApp()).io.serialize(filename, this.collectParameters());
	}

	public HashMap<String,Object> collectParameters() {
		HashMap<String,Object> params = new HashMap<String,Object>();
		// params for scene here?
		params.put(this.getPath()+"/mute", new Boolean(this.isMuted()));

		for(Filter f : this.getFilters()) {
			//add params for each filter here
			params.put(f.getPath()+"/mute", new Boolean(f.isMuted()));
			for (Parameter p : f.getParameters()) {
				// add params for each parameter here
				//println("collectParameters got " + p.getPath() + " for " + f.getPath());
				params.put(p.getPath(), p.value);
			}
		}
		return params;
	}

	public void loadParameters(HashMap<String,Object> params) {
		for (Entry<String,Object> e : params.entrySet()) {
			println("loadParameters() got " + e.getKey() + " with " + e.getValue().getClass().getName());
			if (e.getKey().endsWith("/mute")) {
				if (host.getObjectForPath(e.getKey())!=null)
					((Mutable) host.getObjectForPath(e.getKey())).setMuted((Boolean)e.getValue());
				continue;
			}
			if (e.getValue() instanceof Parameter) {
				host.target(e.getKey(), ((Parameter)e.getValue()));
			} else {
				host.target(e.getKey(), e.getValue());
			}
			//(((Parameter)e.getValue()).getName(),

			/*Parameter p = (Parameter)e.getValue();
		  Object o = this.host.getObjectForPath(p.getFilterPath());
		  //((Parameter) this.host.getObjectForPath(p.getFilterPath())
		  if (o instanceof Filter) {
			  println(e.getKey() + " is a Filter!");
			  //((Filter)o).target(p.getName(), p.value);	/// prize for king of readable code

			  ((Filter) o).setParameterValue(p.getName(), p.value);
		  } else {
			  println(e.getKey() + " is a " + o.getClass().getName());
		  }*/
			//..setValue( ((Parameter)e.getValue()).value);
			//f.target(e.getKey(), payload)
		}
	}

	/*public void loadPreset2(String filename) {
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

	private Textlabel lblSceneMapping;

	private ControllerGroup tab;
	public void setupControls(ControlFrame cf, ControllerGroup tab) {

		ControlP5 cp5 = cf.control();
		println("Scene#setupControls() in " + this);
		//if (doneControls) return;
		doneControls = true;
		this.cp5 = cp5;
		this.tab = tab;
		this.tabName = tab.getName();

		int margin = 10;
		int lm = 10;
		int topMargin = 40;
		int size = 20;

		int currentY = topMargin;

		this.muteController = cp5.addToggle("mute_"+tabName)
				.setPosition(margin, margin)
				.setSize(size,size)
				.setValue(isMuted())
				//.setPosition(lm, currentY)
				.setLabel("Mute Scene")
				//.plugTo(this, "setMuted")
				//.addCallback(this)
				.moveTo(tab)
				;
		this.muteController.getCaptionLabel().alignY(ControlP5.CENTER);

		/*this.lblSceneMapping = cp5.addLabel(tabName+"_scenemappings")
				.setPosition(this.muteController.getWidth() + margin * 10, margin)
				.setWidth(cf.sketchWidth())
				.setLabel("output mappings")
				.moveTo(tab)
				;*/
		/*String mappingString = "";
		for (Entry<String, String> map : this.getCanvasMappings().entrySet()) {
			mappingString += "{";
			mappingString += map.getKey() + " => " + map.getValue();
			mappingString += "}, ";
		}
		this.lblSceneMapping.setStringValue(mappingString);
		println("got scene mapping " + mappingString);*/

		
		CallbackListener toFront = new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				theEvent.getController().bringToFront();
				((ScrollableList)theEvent.getController()).open();
			}
		};

		CallbackListener close = new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				((ScrollableList)theEvent.getController()).close();
			}
		};
		
		Scene self = this;
		
		int start_x = (int) this.muteController.getWidth() + margin * 8; //(this.lblSceneMapping.getPosition()[0] + margin + this.lblSceneMapping.getWidth());
		int margin_w = 200;
		for (Entry<String, String> map : this.getCanvasMappings().entrySet()) {
			cp5.addLabel(tabName + map.getKey() +  "_canvaspath_label").setText(map.getKey()+":-").setPosition(start_x, margin-12).setWidth(30).setLabel(map.getKey()).moveTo(tab);
			ScrollableList lstTarget = new ScrollableList(cp5,tabName + map.getKey() +  "_canvaspath")
					//.addItem(((FormulaCallback)c).targetPath, ((FormulaCallback)c).targetPath)
					.setLabel(map.getValue()) //((FormulaCallback)c).targetPath)
					//.addItems(APP.getApp().pr.getSceneUrls()) //.toArray(new String[0])) //.getTargetURLs().keySet().toArray(new String[0]))
					.addItems(host.getCanvasPaths())
					.setPosition(start_x, margin)
					.setWidth(margin_w)
					.setBarHeight(15)
					.setItemHeight(15)
					.moveTo(tab)
					.onLeave(close)
					.onEnter(toFront)
					.close()
					.addListenerFor(cp5.ACTION_BROADCAST, new CallbackListener() {
						@Override
						public void controlEvent(CallbackEvent theEvent) {
							int index = (int) theEvent.getController().getValue();
							self.setCanvas(map.getKey(), (String)((ScrollableList)theEvent.getController()).getItem(index).get("text"));
						}
					});
			 start_x += (margin + (margin_w)) + margin/2;
		}
		
	

		currentY += size + margin;

		
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
		int row = 0;
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
				row = filters[i].setupControls(cf,tab,row);

				println("<<<<<<<<<<<<<<<<did setupcontrols for " + filters[i]);

			}
		}
	}

	@Override
	public Object target(String path, Object payload) {
		println("#target('"+path+"', '"+payload+"')");
		if (path.endsWith("/mute")) { //"/mute".equals(path.substring(path.length()-5, path.length()))) {
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
		for (Filter f : s.getFilters()) {
			urls.putAll(f.getTargetURLs());
		}

		urls.putAll(getCustomTargetURLs());

		return urls;
	}
	public double getTimeScale() {
		// TODO Auto-generated method stub
		return host.getSequencer().getTimeScale();
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
	public LinkedHashMap<String, Object> collectSceneSetup() {
		// collect scene setup for saving
		LinkedHashMap<String, Object> output = new LinkedHashMap<String,Object>();
		
		output.put("class", this.getClass().getName());
		output.put("name", this.getSceneName());
		output.put("path", this.getPath());
		
		// save all filters
		LinkedHashMap<String,Object> filters = new LinkedHashMap<String,Object>();
		for (Filter f : this.getFilters()) {
			filters.put(f.getPath(), f.collectFilterSetup());
		}
		output.put(this.getPath()+"/filter_setup", filters);
		
		// save all canvas mappings
		//for (Entry<String, String> cm : this.getCanvasMappings().entrySet()) {			
		//}
		output.put(this.getPath()+"/canvas_setup", this.collectCanvasMappings());
		
		return output;
	}
	private HashMap<String,String> collectCanvasMappings() {
		HashMap<String,String> output = new HashMap<String,String> ();
		for (Entry<String, String> c : this.getCanvasMappings().entrySet()) {
			output.put(c.getKey(), c.getValue());
		}
		return output;
	}
	public void readSnapshot(Map<String, Object> input) {
		for (Entry<String, Object> e : input.entrySet()) {
			if (e.getKey().equals("name")) this.setSceneName((String) e.getValue());	// can also get "name" and "path" here
			if (e.getKey().endsWith("/filter_setup")) {
				// loop over the filters here
				for (Entry<String,Object> fi : ((Map<String, Object>)e.getValue()).entrySet()) {
					// fi.key is filterpath, fi.value is hashmap of parameters to create filter, including classname as 'class'
					Filter new_f = Filter.createFilter((String) ((Map<String, Object>)fi.getValue()).get("class"), this);
					new_f.readSnapshot((Map<String, Object>) fi.getValue());
					this.addFilter(new_f);
				}
				this.setupFilters = true;
			}
			if (e.getKey().endsWith("/canvas_setup")) {
				this.setCanvasMappings((HashMap<String, String>) e.getValue());
			}
			
		}

		
		// create Filters from saved input filter_setup
		//for (Entry<String, Object> i : ((HashMap<String, Object>) input.get(this.getPath()+"/filter_setup")).entrySet()) {
			
		//}
		
	}
	
	private void setCanvasMappings(HashMap<String,String> value) {
		this.buffermap = value;		
	}
	public static Scene createScene(String classname, Project host, int width, int height) {
		Class<?> clazz;
		try {
			clazz = Class.forName(classname);
			System.out.println (clazz.getConstructors());
			//Constructor<?> ctor = clazz.getConstructors()[0]; //[0]; //Scene.class, Integer.class);
			Constructor<?> ctor = clazz.getConstructor(Project.class, Integer.TYPE, Integer.TYPE); //Integer.class, Integer.class); //Scene.class,Integer.TYPE);
			//Object seq = ctor.newInstance(); //(Scene)null, 0);
			return (Scene) ctor.newInstance(host,width,height); //(Scene)null, (int)0);		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public synchronized void moveFilter(Filter f, int direction) {
		for (int i = 0 ; i < this.filterCount; i++) {
			if (i+direction<0 || i+direction>this.filterCount-1) continue;
			if (this.filters[i]==f) {
				Filter t;
				t = filters[i+direction];
				filters[i+direction] = f;
				filters[i] = t;
				break;
			}
		}		
	}
	synchronized public void refreshControls() {
		this.setupControls(host.getApp().getCF(), tab);
		tab.setWidth(host.getApp().getCF().sketchWidth());
	}
	public String getMappingForCanvas(String canvas_in) {
		if (canvas_in==null) return null;
		for (Entry<String, String> c : this.getCanvasMappings().entrySet()) {
			if (canvas_in.equals(c.getValue())) return c.getKey();
		}
		return null;
	}
}
