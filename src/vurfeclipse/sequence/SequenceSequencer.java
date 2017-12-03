package vurfeclipse.sequence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import vurfeclipse.APP;
import vurfeclipse.Targetable;
import vurfeclipse.VurfEclipse;
import vurfeclipse.connectors.XMLSerializer;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.Scene;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.sequence.*;
import vurfeclipse.ui.ControlFrame;
import controlP5.Bang;
import controlP5.CallbackEvent;
import controlP5.ControlP5;
import controlP5.Controller;




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
		private boolean historyMode = false;

		private ArrayList<String> historySequenceNames = new ArrayList<String>();
		private int historyCursor;

	  public SequenceSequencer (Project host, int w, int h) {
	    //super(host, w,h);
			this.host = host;
			this.w = w;
			this.h = h;

	    //this.filterCount = 16;
	    //this.filters = new Filter[filterCount];
	  }


	  public void saveHistory() throws IOException {
	  	this.saveHistory("history.txt");
	  }
		public void saveHistory(String fileName) throws IOException {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this.historySequenceNames);
			/*for (String seqname : this.historySequenceNames) {
				oos.writeObject(seqname);
				oos.writeChars("\r\n");
			}*/
			oos.close();
			println("Saved sequencer history to " + fileName + "!");
		}
		
		public void loadHistory()throws IOException {
			// TODO Auto-generated method stub
			loadHistory("history.txt");
		}
		public void loadHistory(String fileName) throws IOException {
			FileInputStream fis = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			try {
				this.historySequenceNames = (ArrayList<String>) ois.readObject();
				println("Loaded sequencer history from " + fileName + "!");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ois.close();
		}

	  
	  @Override
	  public boolean checkReady(int max_iterations) {
	  	if (getActiveSequence()!=null && getActiveSequence().getLengthMillis()<100) {
	  		host.println("SequenceSequencer is " + this.toString() + ": " + getActiveSequence() + " has " + getActiveSequence().getLengthMillis() + " length..?");
	  	}
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
			  while (it.hasNext()) {
				  Sequence next = it.next();
				  if (next!=null) next.stop();
			  }
			  stopSequencesFlag = false;
		  }
		  //println(this+"#runSequences");
		  // probably want to move this up to Sequencer and do super.runSequences()
		  if (readyToChange(2)) {		/////////// THIS MIGHT BE WHAT YOu'RE LOOKING FOR -- number of loop iterations per sequence
			  println(this+"#runSequences(): is readyToChange from " + this.activeSequenceName + ", calling randomSequence()");
			  nextSequence();
		  }
		  if (getActiveSequence()==null) nextSequence();

		  //host.setTimeScale(0.1f);
		  
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
			HashMap<String, Targetable> urls = super.getTargetURLs(); //new HashMap<String,Targetable>();
			//urls.putAll(super.getTargetURLs());
			
			urls.put("/seq/seed", this);

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
					  changeSequence(spl[3],false, true);	// 2017-11-16, quick hack to prevent targeted changes from saving in history..
				  } else {
					  if (payload instanceof String) {
						  println ("Sequencer attempting changeSequence to " + payload.toString());
						  changeSequence(payload.toString(),false, true); // 2017-11-16, quick hack to prevent targeted changes from saving in history..
					  } else if (payload instanceof HashMap<?,?>) {
						  println ("Sequencer attempting changeSequence to passed-in definition of a sequence!");
						  Sequence newSeq = this.loadSequence((HashMap<String,Object>)payload);
						  String seqName = (String) ((HashMap) payload).get("current_sequence_name");
						  seqName = seqName==null?"loaded":seqName;
						  this.addSequence(seqName, newSeq);
						  changeSequence(seqName);
					  }
				  }
				  return "Sequencer active Sequence is currently " + activeSequenceName;
			  } else if (spl[2].equals("toggleLock")) {
				  return "Lock is " + this.toggleLock();
			  } else if (spl[2].equals("seed")) {
				  this.getActiveSequence().setSeed((Long)payload);
				  return "Set seed to "+payload;
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
	  public synchronized void bindAll(String prefix, HashMap<String,Sequence> seqs, int weight) {
		  Iterator<String> it = seqs.keySet().iterator();
		  HashMap<String,Sequence> newSeqs = new HashMap<String,Sequence> ();
		  while (it.hasNext()) {
			  String c = it.next();
			  newSeqs.put(prefix+"_"+c, seqs.get(c));
			  //seqs.remove(c);
		  }
		  bindAll(newSeqs, weight);
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
		  int chosen = 0;
		  //try {
			  chosen = (int)APP.getApp().random(0,count);
			  if (chosen<seqList.size()) seq_pos = chosen; // set list index
			  if (chosen<0) chosen = 0;
			  //changeSequence((String)sequences.keySet().toArray()[chosen]);
			  println("Chose random element " + chosen + " of " + count + "('" + (String)randomPool.get(chosen) + "')");
			  changeSequence((String)randomPool.toArray()[chosen]);
		  /*} catch (Exception e) {
		  	this.println("randomSequence() with chosen " + chosen + " (of count " + count + ") caught " + e);
		  }*/
		}

	  public void nextSequence() {
	  	if (historyMode) {
	  		this.histNextSequence(1, true);
	  	} else if (randomMode) {
	  		randomSequence();
	  	} else {
	  		println("Moving to seqList index " + seq_pos++);
	  		if (seq_pos>=seqList.size()) {
	  			seq_pos = 0;
	  			println("Resetting index to 0 because sequence list reached " + seqList.size());
	  		}
	  		String newSequenceName = seqList.get(seq_pos); 
	  		changeSequence(newSequenceName);

	  	}
	  }
	  
	  /*public void histPreviousSequence(int distance) {
	  	this.histPreviousSequence(distance, true);
	  }*/
	  
	  public void histMoveCursor(int distance, boolean restart) {
	  	int size = this.historySequenceNames.size();
	  	historyCursor+=distance;
	  	if (historyCursor<0) {
	  		historyCursor = 0;
	  		host.println("SequenceSequencer already at start of history");
	  	} else if (historyCursor>size-1) {
	  		historyCursor = size - 1;
	  		
	  		host.println("SequenceSequencer already at end of history");
	  	} else {
	  		if (this.historySequenceNames.get(historyCursor)!=null) {
	  			host.println("SequenceSequencer moving history cursor to " + historyCursor + " with restart of " + restart);
	  			String previousSequenceName = this.historySequenceNames.get(historyCursor);
	  		
	  			changeSequence(previousSequenceName, false, restart);
	  			//if (size>1) this.historySequenceNames.remove(size-1);
	  		}
	  	}	  
	  }
	  
	  public void histPreviousSequence(int distance, boolean restart) {
	  	this.histMoveCursor(-distance, restart);
	  }
	  public void histNextSequence(int distance, boolean restart) {
	  	this.histMoveCursor(distance, restart);
	  }
	  
	  public void changeSequence(String sequenceName) {
	  	this.changeSequence(sequenceName,true,true);
	  }

	  public void changeSequence(String SequenceName, boolean remember, boolean restart) {
		  if (this.getActiveSequence()!=null) {		// mute the current sequence 
			  if (restart) this.getActiveSequence().stop();//setMuted(true);
			  // check if this is already the top of the sequence history, if so don't add it again 
		  }

		  this.activeSequenceName = SequenceName;
		  
		  if (null==this.getActiveSequence()) {
		  	println("Got NULL for " + this.activeSequenceName + "!");
		  	return;
		  }

		  println("Changing to sequence: " + SequenceName + "  (" + this.getActiveSequence().toString() + ")");
		  if (remember && this.shouldRemember(SequenceName)) {
		  	this.addHistorySequenceName(SequenceName);
		  	//if (historyCursor==this.historySequenceNames.size()-1)	// if the cursor is already tracking the history then set cursor to most recent item so that 'j' does jump to the most recent sequence 
	  		historyCursor = this.historySequenceNames.size()-1;
		  }
		  //muteAllSequences();
		  this.getActiveSequence().setMuted(false);
		  if (restart) this.getActiveSequence().start();
	  }


	private boolean shouldRemember(String sequenceName) {
			// TODO Auto-generated method stub
		  if (sequenceName.contains("_next_")) {
		  	return false;
		  }
		  if (getSequence(sequenceName).readyToChange(0)) {
		  	host.println(this.toString() + ": " + sequenceName +" is ready to change for 0, not remembering!");;
		  }
			return true;
		}


	private void addHistorySequenceName(String activeSequenceName2) {
			println("Added " + activeSequenceName2 + " as the " + this.historySequenceNames.size() + "th history item");
			historySequenceNames.add(activeSequenceName2);
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
	
	public void bindSavedSequencer(String prefix, int sequenceLength, int weight) {
		  List<String> textFiles = new ArrayList<String>();
		  String directory = System.getProperty("user.dir") + "/saves"; //APP.getApp().sketchPath("");
		  File dir = new File(directory);
		  
		  HashMap<String, Sequence> sequences = new HashMap<String, Sequence>();
		  
		  for (File file : dir.listFiles()) {
		    if (file.getName().startsWith(host.getClass().getSimpleName()) && file.getName().endsWith((".xml"))) {
		      //textFiles.add(file.getName());
		      ((HashMap<String, Sequence>) sequences).put("_saved " + file.getName(), new SavedSequence("saves/"+file.getName(),sequenceLength));
		    }
		  }
		  //return textFiles;
		  
		  //for (String filename : textFiles) {
		  	//for (int i = 0 ; i < weight ; i++) {
		  		this.bindAll(sequences, weight); 
		  	//}
		  //}
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
		this.seqList = this.randomPool;
	}


	public void restartSequence() {
		println("restarting" + activeSequenceName);
		this.getActiveSequence().iteration = 0;
		this.getActiveSequence().start();
	}


	int sequenceDistance = 1;
	public void cutSequence() {
		// go up and down cursor alternately; if nothing to switch to, do a random mahfucker?
		if (sequenceDistance>0) {
			this.histNextSequence(sequenceDistance,false);
		} else {
			this.histPreviousSequence(-sequenceDistance,false);
		}
			
		if (sequenceDistance == 1) sequenceDistance = -1; else if (sequenceDistance == -1) sequenceDistance = 1; 
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
	
	@Override 
	public boolean sendKeyPressed(char key) {
		/*if (key=='w') {
    	try {
    			saveHistory();
    	} catch (IOException e) {
    		System.out.println("Couldn't save history! " + e);
    	}
    } else */if (key=='e') {
    	try {
    			loadHistory();
    	} catch (IOException e) {
    		System.out.println("Couldn't load history! " + e);
    	}
    } else if (key=='j' || key=='J') {	// HISTORY BACK
      histPreviousSequence(1,key=='j'?true:false);
    } else if (key=='k' || key=='K') { // HISTORY FORWARD
      histNextSequence(1,key=='k'?true:false);
    } else if (key=='O' ) { // RESTART CURRENT SEQUENCE (stutter effect)
      restartSequence();
    } else if (key=='o') { // HISTORY 'cut' between cursor/next (or do random if at end?)
      cutSequence();
    } else if (key=='p') {
    	this.historyMode = !this.historyMode;
    	println ("historyMode set to " + historyMode);
    } else if (key=='f') {
    	saveSequence(this.getCurrentSequenceName() + ((VurfEclipse)APP.getApp()).dateStamp());
    } else if (key=='F') {
    	//loadSequence(host.getApp().select.selectInput("Load a sequence", activeSequenceName));
    	loadSequence("test.xml");
    } else if (super.sendKeyPressed(key)) {
    } else {
    	return false;
    }
		return true;
	}
	
	private Sequence loadSequence(String filename) {
	  	HashMap<String, Object> input;
	  	try {
	  		//input ((VurfEclipse)APP.getApp()).io.deserialize(filename, HashMap.class);
	  		input = (HashMap<String, Object>) XMLSerializer.read(filename);
	  		
	  		Sequence newSeq = this.loadSequence(input);
	  		this.addSequence(filename, newSeq); //Sequence.createSequence((String) input.get("class")));
	  		this.changeSequence(filename);
	  		
			return newSeq;	
	  	} catch (Exception e1) {
	  		// TODO Auto-generated catch block
	  		System.err.println("Caught " + e1 + " trying to load sequence '" + filename + "'");
	  		e1.printStackTrace();
	  		//return this;
	  	}
	  	return null;
	}


	public Sequence loadSequence(HashMap<String, Object> input) {
  		try {
			Scene host = this.host.getSceneForPath((String)input.get("hostPath"));
			Sequence newSeq = Sequence.createSequence((String) input.get("class"), host);
			newSeq.loadParameters(input);
			
			return newSeq;
		} catch (Exception e) {
			// TODO Auto-generated catch block
	  		System.err.println("Caught " + e + " trying to load sequence from input hash");
		}
		return null;
	}


	private void saveSequence(String filename) {
		if (!filename.endsWith(".xml")) filename += ".xml";
		filename = filename.replace(':', '_');
		
		Sequence toSave = this.getActiveSequence();
		HashMap<String,Object> output; //= new HashMap<String,Object>();
		output = toSave.collectParameters();
		try {
			XMLSerializer.write(output, filename);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Caught " + e.toString() + " trying to save sequence of class " + toSave.getClass().getSimpleName() + " to '" + filename + "'");
			e.printStackTrace();
		}
	}


	protected Bang saveHistoryButton;
	protected Bang loadHistoryButton;

	@Override public void setupControls (ControlFrame cf, String tabName) {
		this.saveHistoryButton = cf.control().addBang("SAVE sequencer history");		//.moveTo(((VurfEclipse)APP.getApp()).getCW()/*.getCurrentTab()*/).linebreak();
		this.loadHistoryButton = cf.control().addBang("LOAD sequencer history");		//.moveTo(((VurfEclipse)APP.getApp()).getCW()/*.getCurrentTab()*/).linebreak();
		cf.control().addCallback(this);
	}
	
  @Override public void controlEvent (CallbackEvent ev) {
    //println("controlevent in " + this);
    if (ev.getAction()==ControlP5.ACTION_RELEASED) {
    	Controller c = ev.getController();
      if (ev.getController()==this.saveHistoryButton) {
        try {
					this.saveHistory();
				} catch (IOException e) {
					println("Problem saving history!");
					e.printStackTrace();
				}
      } else if (ev.getController()==this.loadHistoryButton) {
        try {
					this.loadHistory();
				} catch (IOException e) {
					println("Problem load history!");
					e.printStackTrace();
				}
      }
      /*else if (ev.getController()==this.saveButton) {
        println("save preset " + getSceneName());
        //this.savePreset(saveFilenameController.getText(), getSerializedMap());
        this.savePreset(getSceneName());
      }
      else if (ev.getController()==this.loadButton) {
        println("load preset");
        this.loadPreset2(getSceneName()); //saveFilenameController.getText());
      }*/
    } else {
    	super.controlEvent(ev);
    }
  }

  @Override public HashMap<String,Object> collectParameters() {
	  HashMap<String,Object> params = super.collectParameters();  
	  params.put("/seq/seed", this.getActiveSequence().getSeed());
	  params.put("/seq/changeTo",this.getActiveSequence().collectParameters());
	  params.put("/seq/current_sequence_name", this.getCurrentSequenceName());	// just save the name, used when re-loading from xml or hashmap
	  return params;
  }
  

}
