package vurfeclipse.sequence;

import vurfeclipse.APP;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.Scene;

public class SavedSequence extends Sequence {

	private String filename;

	public SavedSequence(String filename, int sequenceLengthMillis) {
		this.filename = filename;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setValuesForNorm(double pc, int iteration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		APP.getApp().loadProject(this.filename);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub

	}

}
