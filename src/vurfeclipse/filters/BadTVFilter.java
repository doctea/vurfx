package vurfeclipse.filters;

import java.util.Arrays;

import vurfeclipse.scenes.Scene;

public class BadTVFilter extends ShaderFilter {
	public BadTVFilter() {
		super(null, "BadTV.glsl");
	}
	public BadTVFilter(Scene sc) {
		super(sc, "BadTV.glsl");
	}

	@Override
	public void setParameterDefaults() {
		super.setParameterDefaults();
		/*addParameter("colourMode", new Integer(0), 0, colourModeCount);

	addParameter("width", new Integer(w/16), 0, w*2);
	addParameter("height", new Integer(h/16), 0, h*2);*/
		addParameter("iTime", new Float(10), new Float(0), new Float(1000000));
		addParameter("iResolutionX", 1.0f);
		addParameter("iResolutionY", 1.0f);
		for (String param : Arrays.asList("vertJerkOpt", "vertMovementOpt", "bottomStaticOpt", "scalinesOpt", "rgbOffsetOpt", "horzFuzzOpt")) {
			addParameter(param, 1.0f, new Float(0.0), new Float(1.0));
		}
		//addParameter("vertJerkOpt", 1.0f, new Float(0.0), new Float(1.0));
		//addParameter("vertMovementOpt", 1.0f, new Float(0.0), new Float(1.0));")
		/*float vertJerkOpt = 1.0;
	float vertMovementOpt = 1.0;
	float bottomStaticOpt = 1.0;
	float scalinesOpt = 1.0;
	float rgbOffsetOpt = 1.0;
	float horzFuzzOpt = 1.0;*/
		//addParameter("iResolutionX", new Float((float)host.w), new Float(0), new Float(1900));
		//addParameter("iResolutionY", new Float((float)host.h), new Float(0), new Float(1080));
	}

	@Override
	public Filter nextMode() {
		/*changeParameterValue("colourMode", new Integer(((Integer)getParameterValue("colourMode")+1)));
	if ((Integer)getParameterValue("colourMode")>colourModeCount) {
		changeParameterValue("colourMode", new Integer(0));
	}*/
		return this;
	}
}