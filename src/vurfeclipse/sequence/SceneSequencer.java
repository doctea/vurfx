package vurfeclipse.sequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import vurfeclipse.APP;
import vurfeclipse.Targetable;
import vurfeclipse.filters.BlankFilter;
import vurfeclipse.filters.Filter;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.Scene;

public class SceneSequencer extends Sequencer implements Targetable {
	  String activeSceneName = "";
	  HashMap<String,Scene> scenes = new HashMap<String,Scene>();
	
	  HashMap<String,ArrayList<Sequence>> switched_sequences = new HashMap<String,ArrayList<Sequence>>();
	  		// list of Sequences that are applicable for each SceneName
  
	  public SceneSequencer (Project host, int w, int h) {
	    //super(host, w,h);
		this.host = host;
		this.w = w;
		this.h = h;
	    
	    //this.filterCount = 16;
	    //this.filters = new Filter[filterCount];
	  }
	  

	  @Override
	  public boolean checkReady(int max_iterations) {
		  boolean ready = true;
		  ArrayList<Sequence> seqs = switched_sequences.get(activeSceneName);
		  if (seqs!=null) {
			  Iterator<Sequence> it = seqs.iterator();
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
	  

	  @Override
	  public boolean runSequences() {
		  //println(this+"#runSequences");
		  if (readyToChange(2)) {		/////////// THIS MIGHT BE WHAT YOu'RE LOOKING FOR -- number of loop iterations per sequence
			  println(this+"#runSequences(): is readyToChange, calling randomScene()");
			  randomScene();
		  }
		  
		  ArrayList<Sequence> seqs = switched_sequences.get(activeSceneName);
		  if (seqs!=null) {
			  Iterator<Sequence> it = seqs.iterator();
			  while (it.hasNext()) {
				  Sequence sq = (Sequence)it.next();
				  //println(this+"#runSequences(): Setting values on " + sq);
				  sq.setValuesForTime();
			  }
		  }
		return true;
	  }	
	  		
		public Scene getScene(String name) {
			// TODO Auto-generated method stub
			return scenes.get(name);
		}

		@Override
		public HashMap<String, Targetable> getTargetURLs() {
			HashMap<String, Targetable> urls = new HashMap<String,Targetable>();
			
			urls.putAll(super.getTargetURLs());
			
			Iterator<Entry<String, ArrayList<Sequence>>> it = switched_sequences.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, ArrayList<Sequence>> e = it.next();
				//int count = 0;
				
				// todo: move the path generation into Sequence
				urls.put("/seq/changeTo/" + e.getKey(), this);
				
				// this loop processes each Sequence for the current Scene; dont think we need to add a URL for each of em!
				/*Iterator<Sequence> sit = e.getValue().iterator();
				   while (sit.hasNext()) {
					Sequence seq = sit.next();
					//urls.put("/seq/" + e.getKey() + "/" + count, seq);
				}*/
			}
			
			return urls;
		}
		

		  @Override
		  public Object target(String path, Object payload) {
			  println(this + "#target('"+path+"', '"+payload+"')");
			  String[] spl = path.split("/",4); // TODO: much better URL and parameter checking.
			  if (spl[2].equals("changeTo")) {
				  if (spl.length>3) {
					  println ("Sequencer attempting changescene to " + spl[3]);
					  changeScene(spl[3]);
				  } else {
					  println ("Sequencer attempting changescene to " + payload.toString());
					  changeScene(payload.toString());
				  }
				  return "Sequencer active scene is currently " + activeSceneName;
			  } else if (spl[2].equals("toggleLock")) {
				  return "Lock is " + this.toggleLock();
			  }
			  return payload;
		  }		
	  
	  
	  
	  
	  public Sequence bindSequence (String sceneName, Sequence seq) {
		  if (!switched_sequences.containsKey(sceneName)) switched_sequences.put(sceneName, new ArrayList<Sequence>());
		  
		  switched_sequences.get(sceneName).add(seq);
		  
		  return seq;
	  }
	  
	  public Scene addScene(String sceneName, Scene sc) {
		  scenes.put(sceneName, sc);
		  if (activeSceneName.equals("")) activeSceneName = sceneName;
		  
		  host.addScene(sc);
		  
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
			
			Iterator<Entry<String, Sequence>> it = sc.getSequences().entrySet().iterator();
			while(it.hasNext()) {
				bindSequence(name, it.next().getValue());
			}
			
			return sc;
		}
		
		
	  
	  public void randomScene() {
		  int count = scenes.size();
		  int chosen = (int)APP.getApp().random(0,count);
	/*	  if (getActiveScene().isMuted()) 
			  randomScene();	// danger - will recurse if there isn't an available unmuted scene to pick
		  else*/
		  if (count > 0)
			  changeScene((String)scenes.keySet().toArray()[chosen]);
	  }
	  
	  public void muteAllScenes() {
		  Iterator<Scene> it = scenes.values().iterator();
		  while (it.hasNext()) {
			  it.next().setMuted(true);
		  }
		  
	  }
	  
	  public void changeScene(String sceneName) {
		  this.activeSceneName = sceneName;
		  
		  muteAllScenes();
		  getScene(sceneName).setMuted(false);
		  
		  ArrayList<Sequence> seqs = switched_sequences.get(sceneName);
		  if (seqs!=null) {
			  Iterator<Sequence> it = seqs.iterator();
			  //((Sequence)it.next()).start();
			  while (it.hasNext()) {
				  Sequence s = ((Sequence)it.next());
				  //try {
					  println(this + "#changeScene() Changing scene to '" + sceneName + "', starting " + s);
					  //if (s!=null) 
						s.start(); 
					  //else println("Got NullPointerException for a sequence for " + sceneName + ": ");
			  }
		  }
	  }
	  
	  
	  /*
	   		// 
	  public ArrayList<Sequence> getAllSequences() {
		  ArrayList<Sequence> seqs = new ArrayList<Sequence> ();
		  Iterator<ArrayList<Sequence>> it = this.switched_sequences.values().iterator();
		  while (it.hasNext()) {
			  seqs.addAll(it.next());
		  }
		  return seqs;
	  }
	   * public void changeSequence(Sequence seq) {
		  
	  }*/
	  
	  public Scene getActiveScene () {
		  return scenes.get(activeSceneName);
	  }


	@Override
	public String getCurrentSequenceName() {
		return activeSceneName;
		// TODO Auto-generated method stub
	}
	  
}
