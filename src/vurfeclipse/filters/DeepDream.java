package vurfeclipse.filters;

import vurfeclipse.filters.Filter;
import vurfeclipse.scenes.Scene;

//import org.bytedeco.javacpp.*;
//import org.bytedeco.javacpp.presets.caffe;

public class DeepDream extends Filter {

	DeepDream(Scene sc) {
		super(sc);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean initialise() {
		
		return true;		
	}

	@Override
	public boolean applyMeatToBuffers() {
		// TODO Auto-generated method stub
		return false;
	}

}
