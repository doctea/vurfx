package vurfeclipse.projects;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import vurfeclipse.APP;
import vurfeclipse.scenes.Scene;
import vurfeclipse.sequencers.SequenceSequencer;
import vurfeclipse.Targetable;

public class SavedProject extends Project {

	private String filename;
	private HashMap<String, Object> input;
	private HashMap<String, HashMap<String, Object>> inputAll;

	public SavedProject(int w, int h) {
		super(w, h);
		// TODO Auto-generated constructor stub
	}
	
	
	public Object getInput (String key) {
		return getInput().get(key);
	}
	public HashMap<String, Object> getInput () {
		if (this.input==null) {
			inputAll = this.readSnapshotFile(filename);
			input = inputAll.get("/project_setup");
		}
		return this.input;
	}

	@Override
	public boolean setupStreams() {
		getInput();
		HashMap<String, HashMap<String, Object>> stream_input = (HashMap<String, HashMap<String,Object>>) 
				inputAll
				.get("/seq")
				.get("/seq/stream_setup");
		
		this.sequencer.target("/seq/stream_setup", stream_input);
		
		return false;
	}

	@Override
	public boolean setupScenes() {
		// load the /project_setup/scene_parameters hashmap and process it
		for (Entry<String, Object> i : getInput().entrySet()) {
			if (i.getKey().endsWith("/project_setup/mappings")) continue;
				
			HashMap<String, Object> sc_in = (HashMap<String, Object>) i.getValue();
			// need some basic info to create the Scene - ie, classname
			String clazz 	= (String) sc_in.get("class"); //getInput("class");
			String path 	= (String) i.getKey(); //getInput("path");
		
			Scene new_sc = vurfeclipse.scenes.Scene.createScene(clazz, this, this.w, this.h);
			new_sc.readSnapshot(sc_in);
			new_sc.host = this;
			//new_sc.setSceneName((String) getInput("name"));
			this.addScene(new_sc);
		}
		
		this.sequencer.target("/seq/bank/sequences", this.inputAll.get("/seq").get("/seq/bank/sequences"));
		
		this.sequencer.target("/seq/bank/history", this.inputAll.get("/seq").get("/seq/bank/history"));
		
		// add the sequence from the saved load file
		this.sequencer.target("/seq/changeTo", this.inputAll.get("/seq").get("/seq/sequence"));
		
		
		return true;
	}
	
	
	@Override
	public void setupBufferMappings() {
		this.mappings = (HashMap<String, Integer>) input.get("/project_setup/mappings");
	}

	public Project setSnapshotFile(String filename) {
		this.filename = filename;
		return this;
	}

	@Override
	  public boolean setupSequencer() {
		//TODO: replace this with saved version somehow
		  //this.sequencer = new SceneSequencer(this,w,h);
		  this.sequencer = new SequenceSequencer((Project)this,w,h) {
		  	int count = 1;
		  	int seq_count = 1;
		  	@Override
		  	public void nextSequence() {
		  		if (!APP.getApp().isReady()) return;
		  		count++;
		  		//if (count%8==0) this.setRandomMode(!this.randtrue);//count%8==0);
		  		if (count%16==0) {
		  			super.nextRandomSequence();
		  			return;
		  		}
		  		if ((count%2)==0)
		  			this.setTimeScale(
		  					((count%3)==0)?
		  							2.0d:
		  							0.5d
		  		); //getTimeScale()
		  		else
		  			this.setTimeScale(1.0f);
		  		if (count>1000) count = 0;
		  		//this.host.setTimeScale(0.01f);
		  		super.nextSequence();
		  	}
		  	@Override
		  	public boolean runSequences() {
		  		if (!APP.getApp().isReady()) return false;
		  		seq_count++;
		  		/*if (this.getCurrentSequenceName().contains("_next_")) {
		  			println("Fastforwarding sequence " + this.getCurrentSequenceName() + " because it contains '_next_'..");
		  			super.runSequences();
		  			this.nextSequence();
		  		}*/
		  		return super.runSequences();
		  	}
		  };
		  ((SequenceSequencer)this.sequencer).setBindToRandom(true);

		  return true;
	  }


	@Override
	public void initialiseStreams() {
		//HashMap<String, HashMap<String, Object>> stream_input = (HashMap<String, HashMap<String,Object>>) inputAll.get("/seq").get("/seq/stream_setup");
		
	}

	
}
