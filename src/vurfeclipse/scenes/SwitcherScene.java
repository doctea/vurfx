package vurfeclipse.scenes;

import java.util.*;
import java.util.Map.Entry;

import codeanticode.glgraphics.GLGraphicsOffScreen;
import vurfeclipse.APP;
import vurfeclipse.filters.BlankFilter;
import vurfeclipse.filters.Filter;
import vurfeclipse.projects.Project;
import vurfeclipse.sequence.Sequence;

public class SwitcherScene extends Scene {
  //Filter[] filters;// = new Filter[filterCount];
  //BUF_OUT = 0;
  //BUF_MAX = 1;
	
  String activeSceneName = "";
  HashMap<String,Scene> scenes = new HashMap<String,Scene>();

  HashMap<String,ArrayList<Sequence>> switched_sequences = new HashMap<String,ArrayList<Sequence>>();
  
  Filter blank;
  
  public SwitcherScene (Project host, int w, int h) {
    super(host, w,h);
    
    this.filterCount = 16;
    this.filters = new Filter[filterCount];
  }
  
  public Sequence bindSequence (String sceneName, Sequence seq) {
	  if (!switched_sequences.containsKey(sceneName)) switched_sequences.put(sceneName, new ArrayList<Sequence>());
	  
	  switched_sequences.get(sceneName).add(seq);
	  
	  return seq;
  }
  
  public Scene addScene(String sceneName, Scene sc) {
	  scenes.put(sceneName, sc);
	  if (activeSceneName.equals("")) activeSceneName = sceneName;
	  
	  return sc;
  }
  

  public Scene bindScene(String switchedSceneName, String sequenceName, Scene scene) {
	  addScene(switchedSceneName,scene);
	  Sequence seq = scene.getSequence(sequenceName);
	  if (seq!=null) 
		  bindSequence(switchedSceneName, scene.getSequence(sequenceName));
	  else
		  throw new NullPointerException("sequenceName " + sequenceName + " passed a null value for scene " + switchedSceneName + " and " + scene);
	  
	  return scene;
  }
  
	public Scene bindScene(String name, Scene sc) {	// bind it and all sequences
		addScene(name,sc);
		
		Iterator it = sc.getSequences().entrySet().iterator();
		while(it.hasNext()) {
			bindSequence(name, (Sequence)((Entry)it.next()).getValue());
		}
		
		return sc;
	}  
  
  public void randomScene() {
	  int count = scenes.size();
	  int chosen = (int)APP.getApp().random(0,count);
/*	  if (getActiveScene().isMuted()) 
		  randomScene();	// danger - will recurse if there isn't an available unmuted scene to pick
	  else*/
		  changeScene((String)scenes.keySet().toArray()[chosen]);
  }
  
  public void changeScene(String sceneName) {
	  this.activeSceneName = sceneName;
	  
	  ArrayList seqs = switched_sequences.get(sceneName);
	  if (seqs!=null) {
		  Iterator it = seqs.iterator();
		  //((Sequence)it.next()).start();
		  while (it.hasNext()) {
			  Sequence s = ((Sequence)it.next());
			  //try {
				  println(this + "#changeScene() Changing scene");
				  //if (s!=null) 
					s.start(); 
				  //else println("Got NullPointerException for a sequence for " + sceneName + ": ");
			  /*} catch (NullPointerException e) {
				  System.out.println(this + "#changeScene: Got NullPointerException for a sequence for " + sceneName + ": " + s + ", " + e);
				  System.exit(1);
			  }*/
		  }
	  }
  }
  
  public Scene getActiveScene () {
	  return scenes.get(activeSceneName);
  }
  
  public boolean readyToChange(int max_iterations) {
	  boolean ready = true;
	  ArrayList seqs = switched_sequences.get(activeSceneName);
	  if (seqs!=null) {
		  Iterator it = seqs.iterator();
		  while (it.hasNext()) {
			  //((Sequence)it.next()).setValuesForTime();
			  if (!((Sequence)it.next()).readyToChange(max_iterations)) {
				  //println(this+"#readyToChange("+max_iterations+"): not ready to change");
				  ready = false;
				  break;				  
			  }
		  }
	  }
	  return ready;
  }
  
  public void runSequences() {
	  //println(this+"#runSequences");
	  if (readyToChange(2)) {		/////////// THIS MIGHT BE WHAT YOu'RE LOKOING FOR -- number of loop iterations per sequence
		  println(this+"#runSequences(): is readyToChange, calling randomScene()");
		  randomScene();
	  }
	  
	  ArrayList seqs = switched_sequences.get(activeSceneName);
	  if (seqs!=null) {
		  Iterator it = seqs.iterator();
		  while (it.hasNext()) {
			  Sequence sq = (Sequence)it.next();
			  //println(this+"#runSequences(): Setting values on " + sq);
			  sq.setValuesForTime();
		  }
	  }
  }
  
  public boolean setupFilters () {
	//super.setupFilters();
	blank = new BlankFilter(this).setOutputCanvas(this.getCanvasMapping("out"));
	  
	Collection<Scene> col = scenes.values();
	Iterator it = col.iterator();
	while (it.hasNext()) {
		((Scene)it.next()).setupFilters();
	}
	  
    return true;
  }
  
  int frameCount = 0;
  int frameSkip = 1;
  public void applyGL (GLGraphicsOffScreen gfx) {
	  blank.beginDraw();	// this is here to blank the buffer before a redraw.  if we disable it, it looks rad as fuck!! and woudl probably where we could put in a motion blur effect too.
	  blank.applyToBuffers();
	  blank.endDraw();
	  
	  frameCount++;
	  if (frameCount>=frameSkip) {
		  frameCount = 0;
		  runSequences();
	  }
	  getActiveScene().applyGL(gfx);
  }
	
	public Scene getScene(String name) {
		// TODO Auto-generated method stub
		return scenes.get(name);
	}

  
}
