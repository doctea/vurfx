package vurfeclipse.scenes;

import java.util.ArrayList;

import vurfeclipse.filters.BlendDrawer;
import vurfeclipse.filters.Filter;
import vurfeclipse.filters.PhaseRGBFilter;
import vurfeclipse.filters.ShaderFilter;
import vurfeclipse.projects.Project;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.streams.FormulaCallback;

public class PlasmaScene extends Scene {
	//int filterCount = 2;

	//Filter[] filters;// = new Filter[filterCount];

	final int colourModeCount = 8;

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


	public class PlasmaDrawer extends ShaderFilter {
		public PlasmaDrawer() {
			super(null,"Plasma.glsl");
		}
		public PlasmaDrawer(Scene sc) {
			super(sc,"Plasma.glsl");
		}
		@Override
		public void setParameterDefaults() {
			super.setParameterDefaults();
			addParameter("colourMode", new Integer(0), 0, colourModeCount);

			addParameter("width", new Integer(w/16), 0, w*2);
			addParameter("height", new Integer(h/16), 0, h*2);
			addParameter("u_time_2", new Integer(10), 0, 1000000);
		}
		@Override
		public Filter nextMode() {
			changeParameterValue("colourMode", new Integer(((Integer)getParameterValue("colourMode")+1)));
			if ((Integer)getParameterValue("colourMode")>colourModeCount) {
				changeParameterValue("colourMode", new Integer(0));
			}
			return this;
		}
	}
	
	public class MandlebrotDrawer extends ShaderFilter {
		public MandlebrotDrawer() {
			super(null,"mandlebrot.glsl");
		}
		public MandlebrotDrawer(Scene sc) {
			super(sc,"mandlebrot.glsl");
		}
		@Override
		public void setParameterDefaults() {
			super.setParameterDefaults();
			addParameter("x", new Float(0.0f), -1.5f, 1.5f); //-640.0f, 640.0f); //Integer(w/16), 0, w*2);
			addParameter("y", new Float(0.0f), -1.5f, 1.5f); //-480.0f, 480.0f); //Integer(h/16), 0, h*2);
			addParameter("scale", new Float(1.0f), 0.0f, 1.0f); //Integer(h/16), 0, h*2);
			addParameter("rotate", new Float(0.0f), 0.0f, 360.0f); //Integer(h/16), 0, h*2);
			addParameter("iter", new Integer(100), 0, 5000); //Integer(h/16), 0, h*2);
			addParameter("aspect", new Float(host.getApp().getAspectX()));
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
	
	public class MandlebulbDrawer extends ShaderFilter {
		public MandlebulbDrawer() {
			super(null,"mandlebulb.glsl");
		}
		public MandlebulbDrawer(Scene sc) {
			super(sc,"mandlebulb.glsl");
		}
		@Override
		public void setParameterDefaults() {
			super.setParameterDefaults();
			/*
			#define cameraPos vec3(cameraPos_x, cameraPos_y, cameraPos_z)
			#define cameraLookat vec3(cameraLookat_x, cameraLookat_y, cameraLookat_z)
			#define lightDir vec3(lightDir_x, lightDir_y, lightDir_z)
			#define lightColour vec3(lightColor_x, lightColor_y, lightColor_z)
			
			uniform float specular;
			uniform float specularHardness;
			//uniform vec3 diffuse;
			#define diffuse vec3(diffuse_x, diffuse_y, diffuse_z)
			uniform float ambientFactor;
			uniform bool ao;
			uniform bool shadows;
			uniform bool rotateWorld;
			uniform bool antialias;*/
			this.addParameter("cameraPos_x", new Float(0.0f), -5000.0f, 5000.0f);
			this.addParameter("cameraPos_y", new Float(0.0f), -5000.0f, 5000.0f);
			this.addParameter("cameraPos_z", new Float(0.0f), -5000.0f, 5000.0f);
			
			this.addParameter("cameraLookat_x", new Float(0.0f), -5000.0f, 5000.0f);
			this.addParameter("cameraLookat_y", new Float(0.0f), -5000.0f, 5000.0f);
			this.addParameter("cameraLookat_z", new Float(0.0f), -5000.0f, 5000.0f);
			
			this.addParameter("lightDir_x", new Float(0.0f), -500000.0f, 500000.0f);
			this.addParameter("lightDir_y", new Float(0.0f), -500000.0f, 500000.0f);
			this.addParameter("lightDir_z", new Float(0.0f), -500000.0f, 500000.0f);
			
			this.addParameter("lightColour_x", new Float(0.0f), -50.0f, 50.0f);
			this.addParameter("lightColour_y", new Float(0.0f), -50.0f, 50.0f);
			this.addParameter("lightColour_z", new Float(0.0f), -50.0f, 50.0f);
		
			this.addParameter("diffuse_x", new Float(0.0f), -50.0f, 50.0f);
			this.addParameter("diffuse_y", new Float(0.0f), -50.0f, 50.0f);
			this.addParameter("diffuse_z", new Float(0.0f), -50.0f, 50.0f);
			
			this.addParameter("specular", 5.0f);
			this.addParameter("specularHardness", 5.0f);
			
			this.addParameter("ambientFactor", 5.0f);
			
			this.addParameter("rotateWorld", new Boolean(true));
			this.addParameter("ao", new Boolean(true));
			this.addParameter("antialias", new Boolean(true));
			this.addParameter("shadows", new Boolean(true));
			
		}
		@Override
		public Filter nextMode() {
			changeParameterValue("colourMode", new Integer(((Integer)getParameterValue("colourMode")+1)));
			if ((Integer)getParameterValue("colourMode")>colourModeCount) {
				changeParameterValue("colourMode", new Integer(0));
			}
			return this;
		}
	}

	@Override
	public boolean setupFilters () {
		//// START PLASMA SCENE
		//plasmaScene.setCanvas("pix0","/pix0");
		//os2.setCanvas("blendresult", "/blendresult");
		addFilter(new PlasmaDrawer(this).setFilterName("Plasma").setAliases("temp1","out")); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));    //os.addFilter(new ShaderFilter(os,"CrossHatch.xml").setFilterName("CrossHatch").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));

		//addFilter(new PhaseRGBFilter(this).setFilterName("PhaseRGB").setInputCanvas(getCanvasMapping("out")).setOutputCanvas(getCanvasMapping("out")));
		addFilter(new PhaseRGBFilter(this).setFilterName("PhaseRGB").setAlias_in("temp1").setAlias_out("temp1"));

		addFilter(new BlendDrawer(this).setFilterName("BlendDrawer").setAlias_in("temp1").setAlias_out("out"));

		return true;
	}


	@Override
	public void setupSequences() {
		sequences.put("preset 1", new RGBFilterSequence1(this, 2000));
		sequences.put("preset 2", new RGBFilterSequence2(this, 3000));
		sequences.put("preset 3", new RGBFilterSequence3(this, 4000));
	}


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
			host.getFilter("Plasma").changeParameterValue("colourMode",new Integer ((int) random(0,((PlasmaScene)this.host).colourModeCount)));

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
