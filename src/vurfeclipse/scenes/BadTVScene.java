package vurfeclipse.scenes;

import java.util.ArrayList;
import java.util.Arrays;

import vurfeclipse.filters.Filter;
import vurfeclipse.filters.ShaderFilter;
import vurfeclipse.projects.Project;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.streams.FormulaCallback;

public class BadTVScene extends Scene {
	//int filterCount = 2;

	//Filter[] filters;// = new Filter[filterCount];

	final int colourModeCount = 8;

	public BadTVScene(Project host, int w, int h) {
		super(host, w, h);

		this.filterCount = 3;
	}

	public void setupCallbackPresets () {
		super.setupCallbackPresets();
		final Scene self = this;
		//println("adding callback 'spin'");
		//this.callbacks.put("warp", new TimeLink(self));
		this.callbacks.put("warp", new FormulaCallback()
				.setExpression("(input%(100*12.0*pi))") // * 10")
				.setTargetPath(this.getFilter("BadTV").getParameter("iTime").getPath())
		);
	}

	public boolean setupFilters () {
		//// START PLASMA SCENE
		//plasmaScene.setCanvas("pix0","/pix0");
		//os2.setCanvas("blendresult", "/blendresult");
		addFilter(new BadTVFilter(this).setFilterName("BadTV").setCanvases(this.getCanvasMapping("out"), this.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));    //os.addFilter(new ShaderFilter(os,"CrossHatch.xml").setFilterName("CrossHatch").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));

		//addFilter(new BlendDrawer(this).setFilterName("BlendDrawer").setInputCanvas("/temp1").setOutputCanvas(this.getCanvasMapping("out")));

		//addFilter(new PhaseRGBFilter(this).setFilterName("PhaseRGB").setInputCanvas(getCanvasMapping("out")).setOutputCanvas(getCanvasMapping("out")));

		return true;
	}



	public void setupSequences() {
		sequences.put("preset 1", new BadTVSequence1(this, 2000));
		/*sequences.put("preset 2", new RGBFilterSequence2(this, 3000));
		sequences.put("preset 3", new RGBFilterSequence3(this, 4000));*/
	}

	/*public class TimeLink extends ParameterCallback {
		private final Scene self;

		public TimeLink(Scene self) {
			this.self = self;
		}

		@Override
		public void call(Object value) {
			if (value instanceof Integer) {
				//os2.getFilter("BadTV").changeParameterValue("u_time_2", (Integer)((Integer)value%(int)(Math.PI*12)));
				Float limit = (float) (100 * 12.0*Math.PI);
				Integer adjusted = ((Integer)value%(int)(float)limit);

				self.getFilter("BadTV").changeParameterValue("iTime",
						//value
						(float) (Integer)value*10//adjusted
						);
			}
		}
	}*/


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

	class BadTVSequence1 extends Sequence {
		public BadTVSequence1(BadTVScene badtvFX1, int i) {
			// TODO Auto-generated constructor stub
			super(badtvFX1,i);
		}
		@Override public ArrayList<Mutable> getMutables() {
			ArrayList<Mutable> muts = new ArrayList<Mutable>();
			muts.add(host);//.getFilter("BlendDrawer1"));
			host.getFilter("BadTV").setMuted(!host.getFilter("BadTV").isMuted());
			return muts;
		}
		public void setValuesForNorm(double norm, int iteration) {
			//System.out.println(this+"#setValuesForNorm("+norm+","+iteration+"): BlendSequence1 " + norm);
			if (iteration%2==0) norm = 1.0f-norm;	// go up and down again
			//host.getFilter("BlendDrawer1").setParameterValue("Opacity", (float)norm);
			host.getFilter("BadTV").changeParameterValue("iTime", norm * 1000.0f);
			/*host.getFilter("PhaseRGB").changeParameterValueFromSin("rshift", (float)Math.sin(norm));
			host.getFilter("PhaseRGB").changeParameterValueFromSin("gshift", (float)Math.sin(norm*norm));
			host.getFilter("PhaseRGB").changeParameterValueFromSin("bshift", (float)Math.sin(norm*norm*norm));*/
		}
		@Override public void onStart() {
			//this.setLengthMillis((int)random(1,5) * 500);
			/*for (int i = 0 ; i < random(2,10) ; i++)
				host.host.getSceneForPath("/ImageListScene1").getFilter("ImageListDrawer1").nextMode();
			for (int i = 0 ; i < random(2,10) ; i++)
				host.host.getSceneForPath("/ImageListScene2").getFilter("ImageListDrawer2").nextMode();*/
			//host.getFilter("BadTV").changeParameterValue("colourMode",new Integer ((int) random(0,((BadTVScene)this.host).colourModeCount)));

			//host.getFilter("PhaseRGB").setMuted(random(0.0f,1.0f)>=0.33f);

		}
		@Override public void onStop() {	}
	}

}