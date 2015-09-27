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
import vurfeclipse.scenes.PlasmaScene;
import vurfeclipse.scenes.Scene;
import vurfeclipse.sequence.Sequence;
import codeanticode.glgraphics.GLGraphicsOffScreen;




public class SequenceSequencer extends Sequencer implements Targetable {
	  String activeSequenceName = "";
	  HashMap<String,Sequence> sequences = new HashMap<String,Sequence>();
	  ArrayList<String> randomPool = new ArrayList<String>();

	  //HashMap<String,ArrayList<Sequence>> switched_sequences = new HashMap<String,ArrayList<Sequence>>();
	  		// list of Sequences that are applicable for each SequenceName

	  int seq_pos = 0;
	  ArrayList<String> seqList = new ArrayList<String>();

	  boolean stopSequencesFlag = true;

	  private boolean bindToRandom = true;
	  private boolean randomMode = true;

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
		  return getActiveSequence()==null || getActiveSequence().readyToChange(max_iterations);
	  }

	  /*@Override
	  public boolean readyToChange(int max_iterations) {
		  //if (getActiveSequence()==null || getActiveSequence().getLengthMillis()==0) return true;
		  return super.readyToChange(max_iterations);
	  }*/


	  @Override
	  synchronized public void runSequences() {
		  if (stopSequencesFlag) {
			  Iterator<Sequence> it = sequences.values().iterator();
			  while (it.hasNext()) it.next().stop();
			  stopSequencesFlag = false;
		  }
		  //println(this+"#runSequences");
		  // probably want to move this up to Sequencer and do super.runSequences()
		  if (readyToChange(2)) {		/////////// THIS MIGHT BE WHAT YOu'RE LOOKING FOR -- number of loop iterations per sequence
			  println(this+"#runSequences(): is readyToChange, calling randomSequence()");
			  nextSequence();
		  }

		  getActiveSequence().setValuesForTime();
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

			Iterator<Entry<String, Sequence>> it = sequences.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Sequence> e = it.next();
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
					  changeSequence(spl[3]);
				  } else {
					  println ("Sequencer attempting changeSequence to " + payload.toString());
					  changeSequence(payload.toString());
				  }
				  return "Sequencer active Sequence is currently " + activeSequenceName;
			  } else if (spl[2].equals("toggleLock")) {
				  return "Lock is " + this.toggleLock();
			  }
			  return payload;
		  }

		public Sequence addListSequence(String sequenceName) {
				if (this.sequences.containsKey(sequenceName))
					this.seqList.add(sequenceName);
				return this.sequences.get(sequenceName);
		}


		public Sequence addListSequence(String sequenceName, Sequence sc) {
			this.seqList.add(sequenceName);
			return addSequence(sequenceName, sc);
		}

	  synchronized public Sequence addSequence(String SequenceName, Sequence sc) {
		  sequences.put(SequenceName, sc);
		  if (activeSequenceName.equals("")) activeSequenceName = SequenceName;

		  //host.addSequence(sc);
		  //this.sequences.put(SequenceName, sc);

		  return sc;
	  }

	  public void bindAll(HashMap<String,Sequence> seqs) {
	  	this.bindAll(seqs, 1);
	  }
	  public void bindAll(HashMap<String,Sequence> seqs, int weight) {
	  	for (int i = 0 ; i<weight ; i++) {
	  		sequences.putAll(seqs);
	  		if (isBindToRandom()==true) this.randomPool.addAll(seqs.keySet());
	  	}
	  }

	  public Sequence bindSequence(String nameInSequencer, Sequence seq, int weight) {
		  for (int i = 0 ; i < weight ; i++) {
			  this.sequences.put(nameInSequencer+"_"+i, seq);
			  if (isBindToRandom()==true) this.randomPool.add(nameInSequencer+"_"+i);
		  }
		  return seq;
	  }
	  public Sequence bindSequence(String nameInSequencer, Scene sc, String presetSequenceName, int weight) {
		  return bindSequence(nameInSequencer, sc.getSequence(presetSequenceName), weight);
	  }
	  public Sequence bindSequence(String nameInSequencer, Sequence seq) {
		  return this.bindSequence(nameInSequencer, seq, 1);
	  }


		public Sequence bindSequence(String nameInSequencer, Scene sc, String seqName) {
			// TODO Auto-generated method stub
			return this.bindSequence(nameInSequencer,sc,seqName,1);
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
		  int count = randomPool.size();
		  try {
			  int chosen = (int)APP.getApp().random(0,count);
			  //changeSequence((String)sequences.keySet().toArray()[chosen]);
			  println("Chose random element " + chosen + " of " + count + "('" + (String)randomPool.get(chosen) + "')");
			  changeSequence((String)randomPool.toArray()[chosen]);
		  } catch (Exception e) {
		  	this.println("randomSequence() caught " + e);
		  }
	  }

	  public void nextSequence() {
		if (randomMode) {
	  		randomSequence();
	  	} else {
	  		println("Moving to seqList index " + seq_pos++);
	  		if (seq_pos>seqList.size()) {
	  			seq_pos = 0;
	  			println("Resetting index to 0 because sequence list reached " + seqList.size());
	  		}
	  		changeSequence(seqList.get(seq_pos));
	  	}
	  }

	  public void changeSequence(String SequenceName) {
		  if (this.getActiveSequence()!=null) 		// mute the current sequence
			  this.getActiveSequence().stop();//setMuted(true);

		  this.activeSequenceName = SequenceName;

		  println("Changing to sequence:   " + SequenceName + "  (" + this.getActiveSequence().toString() + ")");

		  //muteAllSequences();
		  this.getActiveSequence().setMuted(false);
		  this.getActiveSequence().start();
	  }


	@Override
	public String getCurrentSequenceName() {
		return activeSequenceName;
	}

	synchronized public void bindAndPermute(String newPrefix, String matchPrefix, Scene sceneForPath, int length) {
		this.bindAndPermute(newPrefix, matchPrefix, sceneForPath, length, 1);
	}

	synchronized public void bindAndPermute(String newPrefix, String matchPrefix, Scene sceneForPath, int length, int weight) {
		HashMap<String,Sequence> toAdd = new HashMap<String,Sequence>();

		Iterator<Entry<String, Sequence>> it = sequences.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String,Sequence> ent = it.next();
			if (ent.getKey().startsWith(matchPrefix)) { //matches
				Iterator<Entry<String, Sequence>> sit = sceneForPath.getSequences().entrySet().iterator();
				while (sit.hasNext()) {
					Entry<String, Sequence> s = sit.next();
					toAdd.put(
							newPrefix + "_" + s.getKey() + "_" + ent.getKey()  + "_PERMUTED",
							new ChainSequence(length).addSequence(s.getValue()).addSequence(ent.getValue())
					);
					println(newPrefix + "_" + s.getKey() + "_" + ent.getKey() + "_PERMUTED");
				}
			}
		}

		//sequences.putAll(toAdd);
		bindAll(toAdd, weight);
	}

	synchronized public void bindAndPermute(String newPrefix, String matchPrefix, Sequence sequence, int length) {
		this.bindAndPermute(newPrefix, matchPrefix, sequence, length, 1);
	}

	synchronized public void bindAndPermute(String newPrefix, String matchPrefix, Sequence sequence, int length, int weight) {
		HashMap<String,Sequence> toAdd = new HashMap<String,Sequence>();

		Iterator<Entry<String, Sequence>> it = sequences.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String,Sequence> ent = it.next();
			if (ent.getKey().startsWith(matchPrefix)) { //matches
				/*Iterator<Entry<String, Sequence>> sit = sceneForPath.getSequences().entrySet().iterator();
				while (sit.hasNext()) {
					Entry<String, Sequence> s = sit.next();*/
					toAdd.put(
							newPrefix + "_" + ent.getKey() + "_" + sequence + "_PERMUTED",
							new ChainSequence(length).addSequence(sequence).addSequence(ent.getValue())
					);
					println(newPrefix + "_" + ent.getKey() + "_" + sequence + "_PERMUTED");
				//}
			}
		}

		//sequences.putAll(toAdd);
		bindAll(toAdd, weight);
	}


	synchronized public void bindAndPermute(String newPrefix, String matchPrefix, String matchPrefix2, int length) {
		HashMap<String,Sequence> toAdd = new HashMap<String,Sequence>();

		Iterator<Entry<String, Sequence>> it = sequences.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String,Sequence> ent = it.next();
			if (ent.getKey().startsWith(matchPrefix)) { //matches
				Iterator<Entry<String, Sequence>> sit = sequences.entrySet().iterator();
				while (sit.hasNext()) {
					Entry<String, Sequence> s = sit.next();
					if (s.getKey().startsWith(matchPrefix2)) {
						toAdd.put(
								newPrefix + "_" + ent.getKey() + "_" + s.getKey() + "_PERMUTED",
								new ChainSequence(length)
									.addSequence(s.getValue())
									.addSequence(ent.getValue())
						);
						println(newPrefix + "_" + ent.getKey() + "_" + s.getKey() + "_PERMUTED");
					}
				}
			}
		}

		//sequences.putAll(toAdd);
		bindAll(toAdd);
	}


	public void bindAndPermute(String newPrefix, Sequence seq, Scene scene, int length) {
		HashMap<String,Sequence> toAdd = new HashMap<String,Sequence>();

		Iterator<Entry<String, Sequence>> sit = scene.getSequences().entrySet().iterator();
		while (sit.hasNext()) {
			Entry<String, Sequence> s = sit.next();
			toAdd.put(
					newPrefix + "_" +  s.getKey() + "_PERMUTED",
					new ChainSequence(length).addSequence(s.getValue()).addSequence(seq)
			);
			println(newPrefix + "_" + s.getKey() + "_PERMUTED");
		}

		//sequences.putAll(toAdd);
		bindAll(toAdd);
	}


	public void bindSequences(String prefix, Scene scene) {
		HashMap<String,Sequence> toAdd = new HashMap<String,Sequence>();

		Iterator<Entry<String, Sequence>> sit = scene.getSequences().entrySet().iterator();
		while (sit.hasNext()) {
			Entry<String, Sequence> s = sit.next();
			toAdd.put(
					prefix + "_" +  s.getKey(),
					s.getValue()
					//new ChainSequence(length).addSequence(s.getValue()).addSequence(seq)
			);
			//println(prefix + "_" + s.getKey() + "_PERMUTED");
		}

		//sequences.putAll(toAdd);
		bindAll(toAdd);
	}


	public int getSequenceCount() {
		return sequences.size();
	}


	public boolean isBindToRandom() {
		return bindToRandom;
	}


	public void setBindToRandom(boolean bindToRandom) {
		this.bindToRandom = bindToRandom;
	}


	public void setRandomMode(boolean b) {
		// TODO Auto-generated method stub
		this.randomMode = b;
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

}
