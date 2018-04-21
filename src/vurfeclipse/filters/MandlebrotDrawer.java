package vurfeclipse.filters;

import vurfeclipse.scenes.PlasmaScene;
import vurfeclipse.scenes.Scene;

public class MandlebrotDrawer extends ShaderFilter {
	/**
	 * 
	 */
	public MandlebrotDrawer() {
		super(null,"mandlebrot.glsl");
	}
	public MandlebrotDrawer(Scene sc) {
		super(sc,"mandlebrot.glsl");
		this.sc = sc;
	}
	@Override
	public void setParameterDefaults() {
		super.setParameterDefaults();
		addParameter("x", new Float(0.0f), -1.5f, 1.5f); //-640.0f, 640.0f); //Integer(w/16), 0, w*2);
		addParameter("y", new Float(0.0f), -1.5f, 1.5f); //-480.0f, 480.0f); //Integer(h/16), 0, h*2);
		addParameter("scale", new Float(1.0f), 0.0f, 1.0f); //Integer(h/16), 0, h*2);
		addParameter("rotate", new Float(0.0f), 0.0f, 360.0f); //Integer(h/16), 0, h*2);
		addParameter("iter", new Integer(100), 0, 5000); //Integer(h/16), 0, h*2);
		addParameter("aspect", new Float(this.sc.host.getApp().getAspectX()));
		addParameter("alpha_cutoff", new Float(0.25f), 0.0f, 1.0f);
		addParameter("alpha_cutoff_infinity", new Float(1.0f), 0.0f, 1.0f);
		//println("got aspectx " + host.getApp().getAspectX());
		
		//addParameter("u_time_2", new Integer(10), 0, 1000000);
	}
	/*@Override
	public Filter nextMode() {
		setParameterValue("colourMode", new Integer(((Integer)getParameterValue("colourMode")+1)));
		if ((Integer)getParameterValue("colourMode")>colourModeCount) {
			setParameterValue("colourMode", new Integer(0));
		}
		return this;
	}*/
}