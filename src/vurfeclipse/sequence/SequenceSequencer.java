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
import vurfeclipse.sequence.Sequence;
import codeanticode.glgraphics.GLGraphicsOffScreen;

public class SequenceSequencer extends Sequencer implements Targetable {
	  String activeSequenceName = "";
	  HashMap<String,Sequence> sequences = new HashMap<String,Sequence>();
	
	  HashMap<String,ArrayList<Sequence>> switched_sequences = new HashMap<String,ArrayList<Sequence>>();
	  		// list of Sequences that are applicable for each SequenceName
  
	  public SequenceSequencer (Project host, int w, int h) {
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
		  ArrayList<Sequence> seqs = switched_sequences.get(activeSequenceName);
		  if (seqs!=null) {
			  Iterator<Sequence> it = seqs.iterator();
			  while (it.hasNext()) {
				  //((Sequence)it.next()).setValuesForTime();
				  if (!it.next().readyToChange(max_iterations)) {
					  //println(this+"#readyToChange("+max_iterations+"): not ready to change");
					  ready = false;
					  break;				  
				  }
			  }
		  }
		  return ready;
	  }
	  

	  @Override
	  public void runSequences() {
		  //println(this+"#runSequences");
		  if (readyToChange(2)) {		/////////// THIS MIGHT BE WHAT YOu'RE LOOKING FOR -- number of loop iterations per sequence
			  println(this+"#runSequences(): is readyToChange, calling randomSequence()");
			  randomSequence();
		  }
		  
		  ArrayList<Sequence> seqs = switched_sequences.get(activeSequenceName);
		  if (seqs!=null) {
			  Iterator<Sequence> it = seqs.iterator();
			  while (it.hasNext()) {
				  Sequence sq = it.next();
				  //println(this+"#runSequences(): Setting values on " + sq);
				  sq.setValuesForTime();
			  }
		  }
	  }	
	  		
	  

	  public Sequence getActiveSequence () {
		  return sequences.get(activeSequenceName);
	  }
	  	  
	  
		public Sequence getSequence(String name) {
			// TODO Auto-generated method stub
			return sequences.get(name);
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
				
				// this loop processes each Sequence for the current Sequence; dont think we need to add a URL for each of em!
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
					  println ("Sequencer attempting changeSequence to " + spl[3]);
					  //changeSequence(spl[3]);
				  } else {
					  println ("Sequencer attempting changeSequence to " + payload.toString());
					  //changeSequence(payload.toString());
				  }
				  return "Sequencer active Sequence is currently " + activeSequenceName;
			  } else if (spl[2].equals("toggleLock")) {
				  return "Lock is " + this.toggleLock();
			  }
			  return payload;
		  }		
	  
	  
	  
	  public Sequence addSequence(String SequenceName, Sequence sc) {
		  sequences.put(SequenceName, sc);
		  if (activeSequenceName.equals("")) activeSequenceName = SequenceName;
		  
		  //host.addSequence(sc);
		  this.sequences.put(SequenceName, sc);
		  
		  return sc;
	  }
	  

	  /*public Sequence bindSequence(String switchedSequenceName, String sequenceName, Scene sc) {
		  //addSequence(switchedSequenceName,Sequence);
		  Sequence seq = sc.getSequence(sequenceName);
		  if (seq!=null) 
			  bindSequence(switchedSequenceName, sce.getSequence(sequenceName));
		  else
			  throw new NullPointerException("sequenceName " + sequenceName + " passed a null value for Sequence " + switchedSequenceName + " and " + Sequence);
		  
		  return Sequence;
	  }
	  
		public Sequence bindSequence(String name, Sequence sc) {	// bind it and all sequences
			addSequence(name,sc);
			
			Iterator<Entry<String, Sequence>> it = sc.getSequences().entrySet().iterator();
			while(it.hasNext()) {
				bindSequence(name, it.next().getValue());
			}
			
			return sc;
		}
		*/
		
	  
	  public void randomSequence() {
		  int count = sequences.size();
		  int chosen = (int)APP.getApp().random(0,count);
//			  changeSequence((String)sequences.keySet().toArray()[chosen]);
	  }
	  
	  /*public void changeSequence(String SequenceName) {
		  this.activeSequenceName = SequenceName;
		  
		  muteAllSequences();
		  getSequence(SequenceName).setMuted(false);
		  
		  ArrayList<Sequence> seqs = switched_sequences.get(SequenceName);
		  if (seqs!=null) {
			  Iterator<Sequence> it = seqs.iterator();
			  //((Sequence)it.next()).start();
			  while (it.hasNext()) {
				  Sequence s = ((Sequence)it.next());
				  //try {
					  println(this + "#changeSequence() Changing Sequence to '" + SequenceName + "', starting " + s);
					  //if (s!=null) 
						s.start(); 
					  //else println("Got NullPointerException for a sequence for " + SequenceName + ": ");
			  }
		  }
	  }*/
	  
	  
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
	  
}
