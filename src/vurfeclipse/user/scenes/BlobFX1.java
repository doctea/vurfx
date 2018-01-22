package vurfeclipse.user.scenes;

import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PApplet;
import vurfeclipse.APP;
import vurfeclipse.Blob;
import vurfeclipse.Canvas;
import vurfeclipse.filters.BlendDrawer;
import vurfeclipse.filters.BlobDrawer;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.*;
import vurfeclipse.sequence.Sequence;

public class BlobFX1 extends SimpleScene {

	abstract class SpinnerSequence extends Sequence {
		int colour1, colour2, colour3, colour4;    	
		public SpinnerSequence() { }
		public SpinnerSequence(BlobFX1 host, int i) {
			super((Scene)host,i);
		}
		/*@Override public ArrayList<Mutable> getMutables() {
			ArrayList<Mutable> muts = new ArrayList<Mutable>();
			muts.add(host);//host.getFilter("BlendDrawer1"));
			return muts;
		}*/
		@Override public void onStop() {	}
		
		@Override public void onStart() {
	
			colour1 = (int) host.getFilter("BlobDrawer").getParameterValue("colour1");
			colour2 = (int) host.getFilter("BlobDrawer").getParameterValue("colour2");
			colour3 = (int) host.getFilter("BlobDrawer2").getParameterValue("colour1");
			colour4 = (int) host.getFilter("BlobDrawer2").getParameterValue("colour2");
			
			host.getFilter("BlobDrawer2").randomiseParameters(this,new String[] {
					"numofCircles", "yRadianMod", "xRadianMod", //"numSections", 
					"endRadius"
			});
			
	
			host.getFilter("BlobDrawer").randomiseParameters(this,new String[] {
					"numofCircles", "yRadianMod", "xRadianMod", //"numSections", 
					"endRadius"
			});		
			
		}
		
		/*@Override public HashMap<String,Object> collectParameters() {
			HashMap<String,Object> params = super.collectParameters();
			params.put("colour1", new Integer(colour1));
			params.put("colour2", new Integer(colour2));
			params.put("colour3", new Integer(colour3));
			params.put("colour4", new Integer(colour4));
			return params;
		}
		
		@Override public void loadParameters(HashMap<String,Object> params) {
			super.loadParameters(params);
			
			if (params.containsKey("colour1")) this.colour1 = (int) params.get("colour1");
			if (params.containsKey("colour2")) this.colour2 = (int) params.get("colour2");
			if (params.containsKey("colour3")) this.colour3 = (int) params.get("colour3");
			if (params.containsKey("colour4")) this.colour4 = (int) params.get("colour4");
			
			if (colour1==0) colour1 = randomColorMinimum(196);
			if (colour2==0) colour2 = randomColorMinimum(196);
			if (colour3==0) colour3 = randomColorMinimum(196);
			if (colour4==0) colour4 = randomColorMinimum(196);
			
		}*/

	}



	public class SpinnerSequence1 extends SpinnerSequence {
		public SpinnerSequence1() { }
	
		public SpinnerSequence1(BlobFX1 blobFX1, int length) {
			// TODO Auto-generated constructor stub
			super(blobFX1, length);
		}
		public void setValuesForNorm(double norm, int iteration) {
			//double inv_norm = (iteration%2==0) ? 1.0f-norm : norm;
			double inv_norm = PApplet.constrain((float)
					((iteration%2==0) ? norm : (float)(1.0f-norm)),
							0.0f,1.0f); //1.0f-norm : norm;// : norm;
			//System.out.println("norm: " + norm + ", inv_norm: " + inv_norm);
	
			//float iteration_warp = (float)(1.0f/(float)iteration)*(float)norm;
			
			colour1 = (int) host.getFilter("BlobDrawer").getParameterValue("colour1");
			colour2 = (int) host.getFilter("BlobDrawer").getParameterValue("colour2");
			colour3 = (int) host.getFilter("BlobDrawer2").getParameterValue("colour1");
			colour4 = (int) host.getFilter("BlobDrawer2").getParameterValue("colour2");

			
			host.getFilter("BlobDrawer").setParameterValue("totalRotate", (float)norm*360.0f); //PApplet.radians((float)norm*360));
			host.getFilter("BlobDrawer").setParameterValue("rotation", (float)+norm*180.0f);
			host.getFilter("BlobDrawer2")
				.setParameterValue("totalRotate", (float)norm*360.0f) // was 720
				.setParameterValueFromSin("radius", PApplet.sin((float)(inv_norm))) ///2f)))
			;
			
			host.getFilter("BlobDrawer2").setParameterValueFromSin("numofCircles", /*APP.getApp().sin(*/(float)inv_norm/*)*/); //0.2f+APP.getApp().sin(iteration_warp/2));
			
	
			int col1 = lerpcolour(colour1, colour2, norm); //0.5); //inv_norm);
			int col2 = lerpcolour(colour3, colour4, norm); //0.5); //norm);
			
			host.getFilter("BlobDrawer").setParameterValue("colour", col1);
			host.getFilter("BlobDrawer2").setParameterValue("colour", col2);
			
			((BlobDrawer)host.getFilter("BlobDrawer")).setColour(
					(int)APP.getApp().red(col1),
					(int)APP.getApp().green(col1),
					(int)APP.getApp().blue(col1),
					255);
	
			((BlobDrawer)host.getFilter("BlobDrawer2")).setColour(
					(int)APP.getApp().red(col2),
					(int)APP.getApp().green(col2),
					(int)APP.getApp().blue(col2),
					255);
	
	
			
		}
		public void onStart() {
			super.onStart();
			println("onStart() " + this);
			/*colour1 = randomColorMinimum(196);// APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
			colour2 = randomColorMinimum(96);//APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
			colour3 = randomColorMinimum(96); //APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
			colour4 = randomColorMinimum(196); //APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
			*/
			((BlobDrawer)host.getFilter("BlobDrawer")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { Blob.SH_CIRCLE, Blob.SH_RECT, Blob.SH_POLY, Blob.SH_FLOWER, /*4, 5, 6, 7 */} ));
			((BlobDrawer)host.getFilter("BlobDrawer2")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { Blob.SH_CIRCLE, Blob.SH_RECT, Blob.SH_POLY, Blob.SH_FLOWER, } )); //Blob.SH_TEXTURE, /*5, 6,*/ 7 } ));
			
			((BlendDrawer)host.getFilter("BlendDrawer2")).changeParameterValue("BlendMode",getRandomArrayElement(new Integer[] { /*2, 4, 8, */4} ));
			//((BlendDrawer)switcher.getScene("blob drawer").getFilter("BlendDrawer")).changeParameterValue("BlendMode",getRandomArrayElement(new Integer[] { 3, 7, 12, 0, 1 } ));
			//((BlendDrawer)switcher.getScene("blob drawer").getFilter("BlendDrawer")).nextMode();
			((BlobDrawer)host.getFilter("BlobDrawer2")).setParameterValueFromSin("xRadianMod",random(0f,1f));
			((BlobDrawer)host.getFilter("BlobDrawer2")).setParameterValueFromSin("yRadianMod",random(0f,1f));
			
			((BlobDrawer)host.getFilter("BlobDrawer2")).setParameterValueFromSin("yRadianMod",random(0f,1f));
			
	
			
			//toggleOutputs();	
		}
	}



	public class SpinnerSequence2 extends SpinnerSequence {
	    	//int colour1, colour2, colour3, colour4;    	
			public SpinnerSequence2() { }
	
	    	public SpinnerSequence2(Scene scene, int i) {
	    		// TODO Auto-generated constructor stub
	    		super((BlobFX1) scene, i);
	    	}
			public void setValuesForNorm(double norm, int iteration) {
				//double inv_norm = (iteration%2==0) ? 1.0f-norm : norm;
				double inv_norm = PApplet.constrain((float)
						((iteration%2==0) ? norm : (float)(1.0f-norm)),
								0.0f,1.0f); //1.0f-norm : norm;// : norm;
	    		//System.out.println("norm: " + norm + ", inv_norm: " + inv_norm);
	
	    		//float iteration_warp = (float)(1.0f/(float)iteration)*(float)norm;
	    		
				colour1 = (int) host.getFilter("BlobDrawer").getParameterValue("colour1");
				colour2 = (int) host.getFilter("BlobDrawer").getParameterValue("colour2");
				colour3 = (int) host.getFilter("BlobDrawer2").getParameterValue("colour1");
				colour4 = (int) host.getFilter("BlobDrawer2").getParameterValue("colour2");

				
	    		host.getFilter("BlobDrawer").setParameterValue("totalRotate", (float)-norm*360.0f); //PApplet.radians((float)norm*360));
	    		host.getFilter("BlobDrawer").setParameterValue("rotation", (float)-norm*360.0f);
	    		//host.getFilter("BlobDrawer2").setParameterValue("rotation", (float)-norm*360.0f);
	    		host.getFilter("BlobDrawer2")
	    			.setParameterValue("totalRotate", (float)norm*180.0f) // was 720
	    			.setParameterValueFromSin("radius", APP.getApp().sin((float)(inv_norm))) ///2f)))
	    		;
	   		
	    		int col1 = lerpcolour(colour1, colour2, inv_norm);
	    		int col2 = lerpcolour(colour3, colour4, inv_norm);
	    		
	    		((BlobDrawer)host.getFilter("BlobDrawer")).setColour(
	    				(int)APP.getApp().red(col1),
	    				(int)APP.getApp().green(col1),
	    				(int)APP.getApp().blue(col1),
	    				255);
	
	    		((BlobDrawer)host.getFilter("BlobDrawer2")).setColour(
	    				(int)APP.getApp().red(col2),
	    				(int)APP.getApp().green(col2),
	    				(int)APP.getApp().blue(col2),
	    				255);
	    	}
	    	public void onStart() {
	    		super.onStart();
	    		println("onStart() " + this);
	    		/*colour1 = randomColorMinimum(196);
	    		colour2 = randomColorMinimum(96);
	    		colour3 = randomColorMinimum(96);
	    		colour4 = randomColorMinimum(196);*/
	   		
	    		//this.setLengthMillis(500 * (int)(APP.getApp().random(1,10)));
	    		
	    		((BlobDrawer)host.getFilter("BlobDrawer")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { 0, 1, 2, 3, 4 }));//, Blob.SH_TEXTURE} ));
	    		((BlobDrawer)host.getFilter("BlobDrawer2")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { 0, 1, 4 })); //, Blob.SH_TEXTURE } ));
	    		
	    		((BlendDrawer)host.getFilter("BlendDrawer2")).changeParameterValue("BlendMode",getRandomArrayElement(new Integer[] { 2,  8, 4} ));
	    		((BlobDrawer)host.getFilter("BlobDrawer2")).setParameterValueFromSin("xRadianMod",random(0f,1f));
	    		
	    		//toggleOutputs();
	    	}    	
	}



	public class SpinnerSequence3 extends SpinnerSequence {
		//int colour1, colour2, colour3, colour4;    	
		public SpinnerSequence3() { }
	
		public SpinnerSequence3(Scene scene, int i) {
			// TODO Auto-generated constructor stub
			super((BlobFX1) scene, i);
		}
		public void setValuesForNorm(double norm, int iteration) {
			//double inv_norm = (iteration%2==0) ? 1.0f-norm : norm;
			double inv_norm = PApplet.constrain((float)
					((iteration%2==0) ? norm : (float)(1.0f-norm)),
							0.0f,1.0f); //1.0f-norm : norm;// : norm;
			//System.out.println("norm: " + norm + ", inv_norm: " + inv_norm);
	
			//float iteration_warp = (float)(1.0f/(float)iteration)*(float)norm;
			
			host.getFilter("BlobDrawer").setParameterValue("totalRotate", (float)-norm*360.0f); //PApplet.radians((float)norm*360));
			host.getFilter("BlobDrawer").setParameterValue("rotation", (float)-norm*180.0f);
			host.getFilter("BlobDrawer2")
				.setParameterValue("totalRotate", (float)norm*360.0f) // was 720
				.setParameterValueFromSin("radius", PApplet.sin((float)(inv_norm))) ///2f)))
			;
			
			host.getFilter("BlobDrawer2").setParameterValueFromSin("numofCircles", /*APP.getApp().sin(*/(float)inv_norm/*)*/); //0.2f+APP.getApp().sin(iteration_warp/2));
	
			colour1 = (int) host.getFilter("BlobDrawer").getParameterValue("colour1");
			colour2 = (int) host.getFilter("BlobDrawer").getParameterValue("colour2");
			colour3 = (int) host.getFilter("BlobDrawer2").getParameterValue("colour1");
			colour4 = (int) host.getFilter("BlobDrawer2").getParameterValue("colour2");
			
			int col1 = lerpcolour(colour1, colour2, inv_norm);
			int col2 = lerpcolour(colour3, colour4, norm);
			
			((BlobDrawer)host.getFilter("BlobDrawer")).setColour(
					(int)APP.getApp().red(col1),
					(int)APP.getApp().green(col1),
					(int)APP.getApp().blue(col1),
					255);
	
			((BlobDrawer)host.getFilter("BlobDrawer2")).setColour(
					(int)APP.getApp().red(col2),
					(int)APP.getApp().green(col2),
					(int)APP.getApp().blue(col2),
					255);
		}
		public void onStart() {
			super.onStart();
			println("onStart() " + this);
			/*colour1 = randomColorMinimum(196);
			colour2 = randomColorMinimum(96) * 2; // brighter;;
			colour3 = randomColorMinimum(96) * 2;
			colour4 = randomColorMinimum(196);*/
			
			((BlobDrawer)host.getFilter("BlobDrawer")).changeParameterValue("edged",random(1.0f)>=0.5f);
			((BlobDrawer)host.getFilter("BlobDrawer2")).changeParameterValue("edged",random(1.0f)>=0.5f);
		
			//this.setLengthMillis(250 * (int)(APP.getApp().random(1,5)));
			
			((BlobDrawer)host.getFilter("BlobDrawer")).setParameterDefaults();
			((BlobDrawer)host.getFilter("BlobDrawer2")).setParameterDefaults();
			
			((BlobDrawer)host.getFilter("BlobDrawer")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { Blob.SH_COMPOUND, Blob.SH_CIRCLE, Blob.SH_FLOWER , Blob.SH_TEXTURE} ));
			((BlobDrawer)host.getFilter("BlobDrawer2")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { Blob.SH_TEXTURE, Blob.SH_RECT, Blob.SH_POLY } ));
			
			((BlendDrawer)host.getFilter("BlendDrawer2")).changeParameterValue("BlendMode",getRandomArrayElement(new Integer[] { 10,  8, 4 } ));
			((BlobDrawer)host.getFilter("BlobDrawer2")).setParameterValueFromSin("xRadianMod",random(0f,1f));
			
			//toggleOutputs();
		
		}    	
	}



	public class SpinnerSequence4 extends SpinnerSequence {
		public SpinnerSequence4() { }
	
		//int colour1, colour2, colour3, colour4;    	
		public SpinnerSequence4(Scene scene, int i) {
			// TODO Auto-generated constructor stub
			super((BlobFX1) scene, i);
		}
		public void setValuesForNorm(double norm, int iteration) {
			//double inv_norm = (iteration%2==0) ? 1.0f-norm : norm;
			double inv_norm = PApplet.constrain((float)
					((iteration%2==0) ? norm : (float)(1.0f-norm)),
							0.0f,1.0f); //1.0f-norm : norm;// : norm;
			//System.out.println("norm: " + norm + ", inv_norm: " + inv_norm);
	
			//float iteration_warp = (float)(1.0f/(float)iteration)*(float)norm;
	
			colour1 = (int) host.getFilter("BlobDrawer").getParameterValue("colour1");
			colour2 = (int) host.getFilter("BlobDrawer").getParameterValue("colour2");
			colour3 = (int) host.getFilter("BlobDrawer2").getParameterValue("colour1");
			colour4 = (int) host.getFilter("BlobDrawer2").getParameterValue("colour2");

			
			host.getFilter("BlobDrawer").setParameterValue("totalRotate", (float)norm*360.0f); //PApplet.radians((float)norm*360));
			host.getFilter("BlobDrawer").setParameterValue("rotation", (float)-norm*180.0f);
			host.getFilter("BlobDrawer2")
				.setParameterValue("totalRotate", PApplet.abs((float)norm*180.0f)) // was 720
				.setParameterValueFromSin("radius", PApplet.sin((float)(inv_norm))) ///2f)))
			;
			
			int col1 = lerpcolour(colour1, colour2, inv_norm);
			int col2 = lerpcolour(colour3, colour4, inv_norm);
			
			((BlobDrawer)host.getFilter("BlobDrawer")).setColour(
					(int)APP.getApp().red(col1),
					(int)APP.getApp().green(col1),
					(int)APP.getApp().blue(col1),
					255
			);
	
			((BlobDrawer)host.getFilter("BlobDrawer2")).setColour(
					(int)APP.getApp().red(col2),
					(int)APP.getApp().green(col2),
					(int)APP.getApp().blue(col2),
					255);
		}
		public void onStart() {
			super.onStart();
			println("onStart() " + this);
			colour1 = randomColorMinimum(196);
			colour2 = randomColorMinimum(96) * 2; // brighter
			colour3 = randomColorMinimum(96) * 2;
			colour4 = randomColorMinimum(196);
			
			//this.setLengthMillis(250 * (int)(APP.getApp().random(1,5)));
			
			((BlobDrawer)host.getFilter("BlobDrawer")).setParameterDefaults();
			((BlobDrawer)host.getFilter("BlobDrawer2")).setParameterDefaults();
			
			((BlobDrawer)host.getFilter("BlobDrawer")).changeParameterValue("edged",random(1)>=0.5f);
			((BlobDrawer)host.getFilter("BlobDrawer2")).changeParameterValue("edged",random(1)>=0.5f);
			
			((BlobDrawer)host.getFilter("BlobDrawer")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { Blob.SH_COMPOUND, Blob.SH_CIRCLE, Blob.SH_FLOWER } ));
			((BlobDrawer)host.getFilter("BlobDrawer2")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { Blob.SH_RECT, Blob.SH_RECT, Blob.SH_POLY } ));
			
			((BlendDrawer)host.getFilter("BlendDrawer2")).changeParameterValue("BlendMode",getRandomArrayElement(new Integer[] { 2,  8, 4 } ));
			//((BlobDrawer)host.getFilter("BlobDrawer2")).setParameterValueFromSin("xRadianMod",APP.getApp().random(0f,1f));
			
			//toggleOutputs();
		
		}    	
	}



	public class TextureSequence extends Sequence {
		int colour1, colour2, colour3, colour4;    	
		public TextureSequence(BlobFX1 host, int i) {
			super((Scene)host,i);
		}
		/*@Override public ArrayList<Mutable> getMutables() {
			ArrayList<Mutable> muts = new ArrayList<Mutable>();
			muts.add(host);//host.getFilter("BlendDrawer1"));
			return muts;
		}*/
		@Override public void onStart () { 
			this.println("texture sequence starting .. ");
			host.getFilter("BlobDrawer").setInputCanvas(host.getCanvasMapping("pix0"));//host.getCanvasMapping("pix0"));
			host.getFilter("BlobDrawer").changeParameterValue("shape", Blob.SH_TEXTURE);
			host.getFilter("BlobDrawer2").setInputCanvas(host.getCanvasMapping("out")); //host.getCanvasMapping("pix1"));
			host.getFilter("BlobDrawer2").changeParameterValue("shape", Blob.SH_TEXTURE);
		}
		@Override public void onStop() {	}
		@Override
		public void setValuesForNorm(double pc, int iteration) {
			// TODO Auto-generated method stub
			
		}	
	}



	public BlobFX1(Project pr, int w, int h) {
		super(pr,w,h); 
		setSceneName("BlobFX1 Instance");
	}
	
	@Override
	public boolean setupFilters() {	
		//SimpleScene this = (SimpleScene) new SimpleScene(this,w,h).setSceneName("BlobScene").setOutputCanvas("/out");
		super.setupFilters();
		//Scene this = this;
		
		// oversize
		/*boolean oversize = false;		
		int ov_w = oversize ? w*2 : w;
		int ov_h = oversize ? h*2 : h;
		if (oversize) {
			Canvas temp = createCanvas("temp2", ov_w, ov_h);
			setCanvas("temp2", getPath()+"/temp2");
			Canvas temp2 = createCanvas("temp3", ov_w, ov_h);
			setCanvas("temp3", getPath()+"/temp3");
			//setCanvas("out", getPath()+"/out");
		}
		
		this.w = ov_w;
		this.h = ov_h;*/
		
		//int ov_w = this.getCanvasMappingW("temp2"), 
		//		ov_h = this.getCanvasMappingH("temp3");
		
		
	  //this.addFilter(new BlobDrawer(this,ov_w,ov_h)/*.setImage("ds2014/dseye.png")*/.setFilterName("BlobDrawer").setCanvases(this.getCanvasMapping("temp2"),this.getCanvasMapping("pix0")));
	  //this.addFilter(new BlobDrawer(this,ov_w,ov_h)/*.setImage("ds2014/dseye.png")*/.setFilterName("BlobDrawer2").changeParameterValue("shape", 2).setCanvases(this.getCanvasMapping("temp3"),this.getCanvasMapping("pix1")));
	  this.addFilter(new BlobDrawer(this,w,h)/*.setImage("ds2014/dseye.png")*/.setFilterName("BlobDrawer").setCanvases(this.getCanvasMapping("temp2"),this.getCanvasMapping("pix0")));
	  this.addFilter(new BlobDrawer(this,w,h)/*.setImage("ds2014/dseye.png")*/.setFilterName("BlobDrawer2").changeParameterValue("shape", 2).setCanvases(this.getCanvasMapping("temp3"),this.getCanvasMapping("pix1")));

	  
    //this.addFilter(new BlendDrawer(this,ov_w/2,ov_h/2,ov_w,ov_h).setFilterName("BlendDrawer").setCanvases(this.getCanvasMapping("out"),this.getCanvasMapping("temp2")));
    //this.addFilter(new BlendDrawer(this,ov_w/2,ov_h/2,ov_w,ov_h).setFilterName("BlendDrawer2").setCanvases(this.getCanvasMapping("out"),this.getCanvasMapping("temp3")));
    this.addFilter(new BlendDrawer(this, host.w, host.h).setFilterName("BlendDrawer").setCanvases(this.getCanvasMapping("out"),this.getCanvasMapping("temp2")));
    this.addFilter(new BlendDrawer(this, host.w, host.h).setFilterName("BlendDrawer2").setCanvases(this.getCanvasMapping("out"),this.getCanvasMapping("temp3")));
    //this.setMuted(true);
    
	    //((BlobDrawer)this.getFilter("BlobDrawer")).loadSVG(APP.getApp().dataPath("image-sources/reindeer.svg"));
	    
	    return true;
	}



	public void setupSequences() {
		//HashMap<String,Sequence> a = super.getSequences();//new HashMap<String,Sequence> ();
		//sequences.put("texture",  new TextureSequence((BlobFX1)this, 2000));
		sequences.put("preset 1", new SpinnerSequence1((BlobFX1)this, 2000));
		sequences.put("preset 2", new SpinnerSequence2((Scene)this, 1000));
		sequences.put("preset 3", new SpinnerSequence3((Scene)this, 1000));
		sequences.put("preset 4", new SpinnerSequence4((Scene)this, 5000));
	}
	
}
