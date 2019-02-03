package vurfeclipse.filters;

import vurfeclipse.scenes.Scene;

public class PlasmaDrawer extends ShaderFilter {
	/**
	 * 
	 */

	final int colourModeCount = 8;
	public PlasmaDrawer() {
		super(null,"Plasma.glsl");
	}
	public PlasmaDrawer(Scene sc) {
		super(sc,"Plasma.glsl");
		this.sc = sc;
	}
	@Override
	public void setParameterDefaults() {
		super.setParameterDefaults();
		addParameter("colourMode", new Integer(0), 0, this.colourModeCount);

		addParameter("width", new Integer(this.sc.w/16), 0, 100); //this.sc.w*2);
		addParameter("height", new Integer(this.sc.h/16), 0, 100); //this.sc.h*2);
		addParameter("u_time_2", new Integer(10), 0, 1000000);
	}
	@Override
	public Filter nextMode() {
		changeParameterValue("colourMode", new Integer(((Integer)getParameterValue("colourMode")+1)));
		if ((Integer)getParameterValue("colourMode")>this.colourModeCount) {
			changeParameterValue("colourMode", new Integer(0));
		}
		
		return this;
	}
}