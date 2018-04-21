package vurfeclipse.scenes;

import java.util.ArrayList;

import vurfeclipse.VurfEclipse;
import vurfeclipse.filters.BlendDrawer;
import vurfeclipse.filters.Filter;
import vurfeclipse.filters.PhaseRGBFilter;
import vurfeclipse.filters.PlasmaDrawer;
import vurfeclipse.projects.Project;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.streams.FormulaCallback;

public class PlasmaScene extends Scene {
	//int filterCount = 2;

	//Filter[] filters;// = new Filter[filterCount];


	public PlasmaScene(Project host, int w, int h) {
		super(host, w, h);

	}

	public void setupCallbackPresets () {
		super.setupCallbackPresets();
		final Scene self = this;
		//println("adding callback 'spin'");
		//this.callbacks.put("warp", new TimeLink(self));
		this.callbacks.put("warp", new FormulaCallback()
				.setExpression("input%(100*12.0*pi)")
				.setTargetPath(this.getFilter("Plasma").getParameter("u_time_2").getPath())
		);
	}

	/*public class TimeLink extends ParameterCallback {
	private final Scene self;

	public TimeLink(Scene self) {
		this.self = self;
	}

	@Override
	  public void call(Object value) {
			if (value instanceof Integer) {
				//os2.getFilter("Plasma").changeParameterValue("u_time_2", (Integer)((Integer)value%(int)(Math.PI*12)));
					Float limit = (float) (100 * 12.0*Math.PI);
					Integer adjusted = ((Integer)value%(int)(float)limit);

					self.getFilter("Plasma").changeParameterValue("u_time_2",
	    					//value
	    					adjusted
	    		);
			}
	  }*/


	@Override
	public boolean setupFilters () {
		//// START PLASMA SCENE
		//plasmaScene.setCanvas("pix0","/pix0");
		//os2.setCanvas("blendresult", "/blendresult");
		addFilter(new vurfeclipse.filters.PlasmaDrawer(this).setFilterName("Plasma").setAliases("temp1","out")); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));    //os.addFilter(new ShaderFilter(os,"CrossHatch.xml").setFilterName("CrossHatch").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));

		//addFilter(new PhaseRGBFilter(this).setFilterName("PhaseRGB").setInputCanvas(getCanvasMapping("out")).setOutputCanvas(getCanvasMapping("out")));
		addFilter(new vurfeclipse.filters.PhaseRGBFilter(this).setFilterName("PhaseRGB").setAlias_in("temp1").setAlias_out("temp1"));

		addFilter(new vurfeclipse.filters.BlendDrawer(this).setFilterName("BlendDrawer").setAlias_in("temp1").setAlias_out("out"));

		return true;
	}


	@Override
	public void setupSequences() {
		sequences.put("preset 1", new RGBFilterSequence1(this, 2000));
		sequences.put("preset 2", new RGBFilterSequence2(this, 3000));
		sequences.put("preset 3", new RGBFilterSequence3(this, 4000));
	}


	/*public class PlasmaDrawer extends vurfeclipse.filters.PlasmaDrawer {}
	public class MandlebrotDrawer extends vurfeclipse.filters.MandlebrotDrawer {}*/
	
	public class RGBFilterSequence1 extends Sequence {
		public RGBFilterSequence1() {}
		public RGBFilterSequence1(PlasmaScene plasmaFX1, int i) {
			// TODO Auto-generated constructor stub
			super(plasmaFX1,i);
		}
		@Override public ArrayList<Mutable> getMutables() {
			ArrayList<Mutable> muts = new ArrayList<Mutable>();
			muts.add(host);//.getFilter("BlendDrawer1"));
			return muts;
		}
		@Override public void __setValuesForNorm(double norm, int iteration) {
			//System.out.println(this+"#setValuesForNorm("+norm+","+iteration+"): BlendSequence1 " + norm);
			if (iteration%2==0) norm = 1.0f-norm;	// go up and down again
			//host.getFilter("BlendDrawer1").setParameterValue("Opacity", (float)norm);
			host.getFilter("PhaseRGB").changeParameterValueFromSin("rshift", (float)Math.sin(norm));
			host.getFilter("PhaseRGB").changeParameterValueFromSin("gshift", (float)Math.sin(norm*norm));
			host.getFilter("PhaseRGB").changeParameterValueFromSin("bshift", (float)Math.sin(norm*norm*norm));
		}
		@Override public void onStart() {
			//this.setLengthMillis((int)random(1,5) * 500);
			/*for (int i = 0 ; i < random(2,10) ; i++)
				host.host.getSceneForPath("/ImageListScene1").getFilter("ImageListDrawer1").nextMode();
			for (int i = 0 ; i < random(2,10) ; i++)
				host.host.getSceneForPath("/ImageListScene2").getFilter("ImageListDrawer2").nextMode();*/
			//host.getFilter("Plasma").changeParameterValue("colourMode",new Integer ((int) random(0,((PlasmaScene)this.host).colourModeCount)));

			host.getFilter("PhaseRGB").setMuted(random(0.0f,1.0f)>=0.33f);

		}
		@Override public void onStop() {	}
		@Override
		public boolean notifyRemoval(Filter newf) {
			if (newf == host.getFilter("PhaseRGB")) {
				this.setEnabled(false);
				return true;
			}
			return false;
		}
	}

	public class RGBFilterSequence2 extends RGBFilterSequence1 {
		public RGBFilterSequence2() {}
		public RGBFilterSequence2(PlasmaScene plasmaFX1, int i) {
			// TODO Auto-generated constructor stub
			super(plasmaFX1,i);
		}
		@Override public ArrayList<Mutable> getMutables() {
			ArrayList<Mutable> muts = new ArrayList<Mutable>();
			muts.add(host);//.getFilter("BlendDrawer1"));
			return muts;
		}
		@Override public void __setValuesForNorm(double norm, int iteration) {
			//System.out.println(this+"#setValuesForNorm("+norm+","+iteration+"): BlendSequence1 " + norm);
			if (iteration%2==0) norm = 1.0f-norm;	// go up and down again
			//host.getFilter("BlendDrawer1").setParameterValue("Opacity", (float)norm);
			host.getFilter("PhaseRGB").changeParameterValueFromSin("gshift", (float)Math.sin(norm));
			host.getFilter("PhaseRGB").changeParameterValueFromSin("bshift", (float)Math.sin(norm*norm));
			host.getFilter("PhaseRGB").changeParameterValueFromSin("rshift", (float)Math.sin(norm*norm*norm));
		}

		@Override public void onStop() {	}
	}

	public class RGBFilterSequence3 extends RGBFilterSequence1 {
		public RGBFilterSequence3() {}

		public RGBFilterSequence3(PlasmaScene plasmaFX1, int i) {
			// TODO Auto-generated constructor stub
			super(plasmaFX1,i);
		}
		@Override public ArrayList<Mutable> getMutables() {
			if (this.mutables==null) {
				ArrayList<Mutable> muts = super.getMutables(); //new ArrayList<Mutable>();
				//mutables.add(host);//.getFilter("BlendDrawer1"));
			}
			return this.mutables;
		}
		@Override public void __setValuesForNorm(double norm, int iteration) {
			//System.out.println(this+"#setValuesForNorm("+norm+","+iteration+"): BlendSequence1 " + norm);
			if (iteration%2==0) norm = 1.0f-norm;	// go up and down again
			//host.getFilter("BlendDrawer1").setParameterValue("Opacity", (float)norm);
			host.getFilter("PhaseRGB").changeParameterValueFromSin("bshift", (float)Math.sin(norm));
			host.getFilter("PhaseRGB").changeParameterValueFromSin("gshift", (float)Math.sin(norm*norm));
			host.getFilter("PhaseRGB").changeParameterValueFromSin("rshift", (float)Math.sin(norm*norm*norm));
		}

		@Override public void onStop() {	}
	}


}
