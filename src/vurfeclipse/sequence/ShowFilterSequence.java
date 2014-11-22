package vurfeclipse.sequence;

import java.util.ArrayList;

import vurfeclipse.filters.Filter;
import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;

public class ShowFilterSequence extends Sequence {
	String filterPath;
	
	public ShowFilterSequence(Scene outputFX2, int i, String filterPath) {
		super(outputFX2,i);
		this.filterPath = filterPath;
	}

	@Override public ArrayList<Mutable> getMutables () {
		this.mutables = new ArrayList<Mutable> ();
		try {
			//System.out.println(host.host.getObjectForPath(this.filterPath).toString());
			this.mutables.add((Mutable)host.host.getObjectForPath(this.filterPath));
			//System.out.println(host.host.getObjectForPath(this.filterPath).toString());
		} catch (Exception e) {
			System.out.println("caught " + e + " for " + this.filterPath);
			System.exit(1);
		}
		this.mutables.add(host);			
		return this.mutables;
	}
	
	@Override
	public void setValuesForNorm(double pc, int iteration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub

	}

}
