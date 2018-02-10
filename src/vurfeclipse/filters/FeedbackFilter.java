package vurfeclipse.filters;

import vurfeclipse.scenes.Scene;

public class FeedbackFilter extends ShaderFilter {

	public FeedbackFilter(Scene sc) {
		super(sc,"Feedback.glsl");
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void setParameterDefaults() {
		super.setParameterDefaults();
		/*ShaderFilter(os2,"Feedback.glsl")
		.setFilterName("Feedback")*/
		addParameter("dirs", new Integer(9), 1, 16);
		addParameter("amp", new Float(4.1f), 0.5f, 10f);
		addParameter("radio", new Float(7.0f), 3.0f, 15f);
		addParameter("distance_r", new Integer(2), -8, 8);
		addParameter("distance_g", new Integer(4), -8, 8);
		addParameter("distance_b", new Integer(6), -8, 8);
		addParameter("distance_a", new Integer(8), -8, 8);
	}
}
