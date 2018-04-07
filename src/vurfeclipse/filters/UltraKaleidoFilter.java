package vurfeclipse.filters;

import vurfeclipse.scenes.Scene;

public class UltraKaleidoFilter extends ShaderFilter {

	public UltraKaleidoFilter(Scene sc) {
		super(sc,"UltraKaleido.glsl");
	}

	  /*public void setXYOffset(int x, int y) {
	    this.offsetx = x;
	    this.offsety = y;
	  }*/
	  
	  @Override
	  public void setParameterDefaults () {
		    super.setParameterDefaults();

		    addParameter("iTime", new Float(0.0f), 0f, 10000000f);
		    addParameter("scale", new Float(7.2f), 0f, 10f);

		    addParameter("iterations", new Integer(32), 1, 64);
		    addParameter("centre_x", new Float(0.5f), 0f, 1f);
		    addParameter("centre_y", new Float(0.5f), 0f, 1f);
		    
		    addParameter("adjustor", new Float(0.25f), 0.01f, 3.0f);
		    
		    addParameter("colourPhase", new Float(0.25f), 0f, 1.0f);
		    
		    addParameter("r", new Float(1.0f), 0.01f, 2.0f);
	  }
	
}
