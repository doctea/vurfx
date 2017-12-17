package vurfeclipse.sequence;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import vurfeclipse.APP;
import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;

public class ChainSequence extends Sequence {

	ArrayList<Sequence> chain = new ArrayList<Sequence>();
	
	public ChainSequence() { super(); }
	
	public ChainSequence(Scene host, int lengthMillis) {
		super(host,lengthMillis);
	}
	public ChainSequence(int lengthMillis) {
		super(lengthMillis);
	}
	public ChainSequence addSequence(Sequence seq) {
		seq.setLengthMillis(this.getLengthMillis());
		if (this.host==null) this.host = seq.host;
		chain.add(seq);
		return this;
	}
	public ChainSequence addSequence(Scene sc, String sequenceName) {
		return addSequence(sc.getSequence(sequenceName));
	}
	
	@Override public void start() {
		super.start();
		if (this.scene_parameters==null) {
			Iterator<Sequence> it = chain.iterator();
			while(it.hasNext()) {
				it.next().start();
			}
		}
	}
	@Override public void stop() {
		super.stop();
		Iterator<Sequence> it = chain.iterator();
		while(it.hasNext()) {
			it.next().stop();
		}
	}
	
	@Override
	public boolean readyToChange(int max_i) {
		//return iteration>=max_i;
		// assume ready, unless one of the chained items isnt
		if (super.readyToChange(max_i)) return true;
		Iterator<Sequence> it = chain.iterator();
		while(it.hasNext()) {
			if (!it.next().readyToChange(max_i)) return false;
		}
		return true;
	}	
	
	
	@Override
	public ArrayList<Mutable> getMutables() {
		if (this.mutables==null) {
			ArrayList<Mutable> muts = super.getMutables();// new ArrayList<Mutable>();
			//if (host!=null) muts.add(host);
			Iterator<Sequence> it = chain.iterator();
			while(it.hasNext()) {
				muts.addAll(it.next().getMutables());
			}
			this.mutables = muts;
		}
		return this.mutables;
	}	
	
	@Override
	public void setValuesForNorm(double pc, int iteration) {
		// TODO Auto-generated method stub
		Iterator<Sequence> it = chain.iterator();
		while(it.hasNext()) {
			it.next().setValuesForNorm(pc,iteration);
		}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
	  /*Iterator<Sequence> it = chain.iterator();
		while(it.hasNext()) {
			it.next().onStart();
		}*/
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		/*Iterator<Sequence> it = chain.iterator();
		while(it.hasNext()) {
			it.next().onStop();
		}*/
	}

	@Override
	public HashMap<String,Object> collectParameters() {
		HashMap<String,Object> params = super.collectParameters();
		ArrayList<HashMap<String,Object>> chains = new ArrayList<HashMap<String,Object>>();
		for (Sequence cs : chain) {
			HashMap<String,HashMap<String, Object>> temp = cs.getSceneParameters();
			cs.clearSceneParameters();	// remove any scene parameters that are set on the object before saving, since we save our own copy with the chain
			HashMap<String, Object> full_params = cs.collectParameters();
			if (full_params==null) {
				println("wtf, saving null full_params..?");
				cs.collectParameters();
			}
			chains.add(full_params);
		}
		params.put("chain",  chains);
		/*params.put("filterPath", filterPath);
		params.put("parameterName", parameterName);
		params.put("value", "value");*/
		return params;
	}
	
	@Override
	public void loadParameters(HashMap<String,Object> params) {
		super.loadParameters(params);
		ArrayList<HashMap<String,Object>> chains = (ArrayList<HashMap<String,Object>>) params.get("chain");
		for (HashMap<String,Object> cs : chains) {
			// cs contains info to build a new ChainSequence and attach it
			//ChainSequence n = new ChainSequence(this.host, (Integer) cs.get("lengthMillis"));
			if (cs==null) {
				println("skipping null chain sequence from broken save file :(");
				continue;
			}
			if (cs.containsKey("scene_parameters")) cs.remove("scene_parameters");	// don't load scene_parameters for chained sequences, since if there are any they are there from an old version of save format
			Sequence n = Sequence.makeSequence((String) cs.get("class"), (Scene) APP.getApp().pr.getObjectForPath((String) cs.get("hostPath")));
			n.loadParameters(cs);
			this.addSequence(n);
		}		
	}
	
}
