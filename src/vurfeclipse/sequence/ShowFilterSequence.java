package vurfeclipse.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vurfeclipse.filters.Filter;
import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;

public class ShowFilterSequence extends Sequence {
	String filterPath;
	
	public ShowFilterSequence() {}
	public ShowFilterSequence(Scene outputFX2, int length, String filterPath) {
		super(outputFX2,length);
		this.filterPath = filterPath;
	}
	
	@Override
	public HashMap<String,Object> collectParameters() {
		HashMap<String,Object> params = super.collectParameters();
		params.put("filterPath", filterPath);
		return params;
	}

	@Override
	public void loadParameters(Map<String,Object> params) {
		this.filterPath = (String) params.get("filterPath");
	}
	
	@Override public ArrayList<Mutable> getMutables () {
		if (this.mutables==null) {
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
		}
		return this.mutables;
	}
	
	@Override
	public void __setValuesForNorm(double pc, int iteration) {
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
	@Override
	public boolean notifyRemoval(Filter newf) {
		if (newf.getPath().equals(filterPath)) {
			this.setEnabled(false);
			return true;
		}
		return false;
	}
	@Override
	public void removeSequence(Sequence self) {
		// TODO Auto-generated method stub
		
	}

}
