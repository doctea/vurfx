package vurfeclipse.sequence;

import java.util.ArrayList;
import java.util.HashMap;

import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;

public class ShowSceneSequence extends Sequence {
	String filterPath;
	
	public ShowSceneSequence(Scene outputFX2, int i) {
		super(outputFX2,i);
		//this.filterPath = filterPath;
	}
	

	@Override
	public HashMap<String,Object> collectParameters() {
		HashMap<String,Object> params = super.collectParameters();
		params.put("filterPath", filterPath);
		return params;
	}

	@Override
	public void loadParameters(HashMap<String,Object> params) {
		this.filterPath = (String) params.get("filterPath");
	}

	@Override public ArrayList<Mutable> getMutables () {
		if (this.mutables==null) {
			this.mutables = super.getMutables(); //new ArrayList<Mutable> ();
			this.mutables.add(host);
		}
		/*try {
			System.out.println(host.host.getObjectForPath(this.filterPath).toString());
			this.mutables.add((Mutable)host.host.getObjectForPath(this.filterPath));
			System.out.println(host.host.getObjectForPath(this.filterPath).toString());
		} catch (Exception e) {
			System.out.println("caught " + e + " for " + this.filterPath);
			System.exit(1);
		}*/
					
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
