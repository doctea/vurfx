package vurfeclipse.sequence;

import java.util.ArrayList;
import java.util.Iterator;

import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;

public class ChainSequence extends Sequence {

	ArrayList<Sequence> chain = new ArrayList<Sequence>();
	
	public ChainSequence(int i) {
		super(i);
	}
	public ChainSequence addSequence(Sequence seq) {
		chain.add(seq);
		return this;
	}
	public ChainSequence addSequence(Scene sc, String sequenceName) {
		return addSequence(sc.getSequence(sequenceName));
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
		Iterator<Sequence> it = chain.iterator();
		while(it.hasNext()) {
			it.next().onStart();
		}

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Iterator<Sequence> it = chain.iterator();
		while(it.hasNext()) {
			it.next().onStop();
		}

	}

}
