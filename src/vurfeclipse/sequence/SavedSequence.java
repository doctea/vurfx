package vurfeclipse.sequence;

import java.util.HashMap;
import java.util.Map;

import vurfeclipse.APP;
import vurfeclipse.filters.Filter;

public class SavedSequence extends Sequence {

	private String filename;

	public SavedSequence(String filename, int sequenceLengthMillis) {
		this.filename = filename;
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public Map<String,Object> collectParameters() {
		Map<String, Object> params = super.collectParameters();
		params.put("filename", filename);
		return params;
	}

	@Override
	public void loadParameters(Map<String,Object> params) {
		this.filename = (String) params.get("filename");
	}

	@Override
	public void __setValuesForNorm(double pc, int iteration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		APP.getApp().loadSnapshot(this.filename);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub

	}


	@Override
	public boolean notifyRemoval(Filter newf) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void removeSequence(Sequence self) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void __setValuesAbsolute(double pc, int iteration) {
		// TODO Auto-generated method stub
		
	}

}
