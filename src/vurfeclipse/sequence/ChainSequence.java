package vurfeclipse.sequence;

import java.util.ArrayList;
import java.util.Iterator;

import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;

public class ChainSequence extends Sequence {

	ArrayList<Sequence> chain = new ArrayList<Sequence>();
	
	public ChainSequence(Scene host, int i) {
		super(host,i);
	}
	public ChainSequence(int i) {
		super(i);
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
		Iterator<Sequence> it = chain.iterator();
		while(it.hasNext()) {
			it.next().start();
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
		ArrayList<Mutable> muts = new ArrayList<Mutable>();
		//if (host!=null) muts.add(host);
		Iterator<Sequence> it = chain.iterator();
		while(it.hasNext()) {
			muts.addAll(it.next().getMutables());
		}
		return muts;
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

}
