package vurfeclipse.user.scenes;

import java.util.HashMap;

import vurfeclipse.APP;
import vurfeclipse.Blob;
import vurfeclipse.filters.BlendDrawer;
import vurfeclipse.filters.BlobDrawer;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.*;
import vurfeclipse.sequence.Sequence;

public class BlobFX1 extends SimpleScene {

	public BlobFX1(Project pr, int w, int h) { 
		super(pr,w,h); 
		setSceneName("BlobFX1 Instance");
	}
	
	public boolean setupFilters() {	
		//SimpleScene this = (SimpleScene) new SimpleScene(this,w,h).setSceneName("BlobScene").setOutputCanvas("/out");
		super.setupFilters();
		//Scene this = this;
	    this.addFilter(new BlobDrawer(this)/*.setImage("ds2014/dseye.png")*/.setFilterName("BlobDrawer").setCanvases(this.getCanvasMapping("temp2"),this.getCanvasMapping("inp0")));
	    this.addFilter(new BlobDrawer(this)/*.setImage("ds2014/dseye.png")*/.setFilterName("BlobDrawer2").changeParameterValue("shape", 2).setCanvases(this.getCanvasMapping("temp3"),this.getCanvasMapping("temp2")));
	    this.addFilter(new BlendDrawer(this).setFilterName("BlendDrawer").setCanvases(this.getCanvasMapping("out"),this.getCanvasMapping("temp2")));
	    this.addFilter(new BlendDrawer(this).setFilterName("BlendDrawer2").setCanvases(this.getCanvasMapping("out"),this.getCanvasMapping("temp3")));
	    //this.setMuted(true);
	    
	    return true;
	}

	public void setupSequences() {
		//HashMap<String,Sequence> a = super.getSequences();//new HashMap<String,Sequence> ();
		sequences.put("preset 1", new SpinnerSequence1((BlobFX1)this, 2000));
		sequences.put("preset 2", new SpinnerSequence2((Scene)this, 1000));
		sequences.put("preset 3", new SpinnerSequence3((Scene)this, 1000));
		sequences.put("preset 4", new SpinnerSequence4((Scene)this, 5000));
	}
	
}

abstract class SpinnerSequence extends Sequence {
	int colour1, colour2, colour3, colour4;    	
	public SpinnerSequence(BlobFX1 host, int i) {
		super((Scene)host,i);
	}
	
	public void toggleOutputs() {
		// this bit below shouldnt be here.
		if (APP.getApp().random(0f,1.0f)>=0.5f) host.host.getSceneForPath("/sc/OutputShader").getFilter("Toon").toggleMute();
		//switcher.host.getSceneForPath("/sc/OutputShader").getFilter("pulsatingEmboss").setMute((APP.getApp().random(0f,1.0f)>=0.2f));
		//switcher.host.getSceneForPath("/sc/OutputShader").getFilter("CrossHatch").setMute((APP.getApp().random(0f,1.0f)>=0.2f));
		if (APP.getApp().random(0f,1.0f)>=0.5f) host.host.getSceneForPath("/sc/OutputShader").getFilter("Edges").toggleMute();
		if (APP.getApp().random(0f,1.0f)>=0.5f) host.host.getSceneForPath("/sc/OutputShader2").getFilter("Feedback").toggleMute();
		if (APP.getApp().random(0f,1.0f)>=0.5f) host.host.getSceneForPath("/sc/OutputShader2").getFilter("Kaleido").toggleMute();
		
		//if (APP.getApp().random(0f,1.0f)>=0.5f) host.host.getSceneForPath("/sc/OutputShader2").getFilter("BlendDrawer pix0 to out").toggleMute();
		host.host.getSceneForPath("/sc/OutputShader2").getFilter("BlendDrawer pix0 to out").setMute((APP.getApp().random(0f,1.0f)>=0.25f));
		
		//if (APP.getApp().random(0f,1.0f)>=0.5f) host.host.getSceneForPath("/sc/OutputShader2").getFilter("BlendDrawer pix0 to out").changeParameterValue("BlendMode", getRandomArrayElement(new Integer[] { 3, 4, 8, 8, 8, 9, 12 } ));
		if (APP.getApp().random(0f,1.0f)>=0.5f) ((BlendDrawer)host.host.getSceneForPath("/sc/OutputShader2").getFilter("BlendDrawer pix0 to out")).setBlendMode((Integer)getRandomArrayElement(new Integer[] { 3, 4, 8, 8, 8, 9, 12 } ));
		
		if (APP.getApp().random(0f,1.0f)>=0.5f) ((BlendDrawer)host.host.getSceneForPath("/sc/TextFlash").getFilter("BlendDrawer")).setBlendMode((Integer)getRandomArrayElement(new Integer[] { 3, 4, 8, 8, 8, 9, 12 }));
		((BlendDrawer)host.host.getSceneForPath("/sc/TextFlash").getFilter("BlendDrawer")).setMute((APP.getApp().random(0f,1.0f)>=0.25f));
		
		if (APP.getApp().random(0f,1.0f)>=0.5f) ((BlendDrawer)host.host.getSceneForPath("/sc/PlasmaScene").getFilter("BlendDrawer")).setBlendMode((Integer)getRandomArrayElement(new Integer[] { 3, 4, 8, 8, 8, 9, 12 }));
		((BlendDrawer)host.host.getSceneForPath("/sc/PlasmaScene").getFilter("BlendDrawer")).setMute((APP.getApp().random(0f,1.0f)>=0.25f));
		
		
		host.host.getSceneForPath("/sc/OutputShader2").getFilter("Kaleido").nextMode();    			
	}
	
}

class SpinnerSequence1 extends SpinnerSequence {
	public SpinnerSequence1(BlobFX1 blobFX1, int i) {
		// TODO Auto-generated constructor stub
		super(blobFX1, i);
	}
	public void setValuesForNorm(double norm, int iteration) {
		//double inv_norm = (iteration%2==0) ? 1.0f-norm : norm;
		double inv_norm = APP.getApp().constrain((float)
				((iteration%2==0) ? norm : (float)(1.0f-norm)),
						0.0f,1.0f); //1.0f-norm : norm;// : norm;
		//System.out.println("norm: " + norm + ", inv_norm: " + inv_norm);

		//float iteration_warp = (float)(1.0f/(float)iteration)*(float)norm;
		
		host.getFilter("BlobDrawer").setParameterValue("totalRotate", (float)norm*360.0f); //PApplet.radians((float)norm*360));
		host.getFilter("BlobDrawer").setParameterValue("rotation", (float)-norm*180.0f);
		host.getFilter("BlobDrawer2")
			.setParameterValue("totalRotate", (float)norm*360.0f) // was 720
			.setParameterValueFromSin("radius", APP.getApp().sin((float)(inv_norm))) ///2f)))
		;
		
		host.getFilter("BlobDrawer2").setParameterValueFromSin("numofCircles", /*APP.getApp().sin(*/(float)inv_norm/*)*/); //0.2f+APP.getApp().sin(iteration_warp/2));

		int col1 = lerpcolour(colour1, colour2, inv_norm);
		int col2 = lerpcolour(colour3, colour4, inv_norm);
		
		((BlobDrawer)host.getFilter("BlobDrawer")).setColour(
				(int)APP.getApp().red(col1),
				(int)APP.getApp().green(col1),
				(int)APP.getApp().blue(col1));

		((BlobDrawer)host.getFilter("BlobDrawer2")).setColour(
				(int)APP.getApp().red(col2),
				(int)APP.getApp().green(col2),
				(int)APP.getApp().blue(col2));


		
	}
	public void onStart() {
		System.out.println("onStart() " + this);
		colour1 = randomColorMinimum(196);// APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
		colour2 = randomColorMinimum(96);//APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
		colour3 = randomColorMinimum(96); //APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
		colour4 = randomColorMinimum(196); //APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
		((BlobDrawer)host.getFilter("BlobDrawer")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { Blob.SH_CIRCLE, Blob.SH_RECT, Blob.SH_POLY, Blob.SH_FLOWER, /*4, 5, 6, 7 */} ));
		((BlobDrawer)host.getFilter("BlobDrawer2")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { Blob.SH_CIRCLE, Blob.SH_RECT, Blob.SH_POLY, Blob.SH_FLOWER, Blob.SH_TEXTURE, /*5, 6,*/ 7 } ));
		
		((BlendDrawer)host.getFilter("BlendDrawer2")).changeParameterValue("BlendMode",getRandomArrayElement(new Integer[] { /*2, 4, 8, */4} ));
		//((BlendDrawer)switcher.getScene("blob drawer").getFilter("BlendDrawer")).changeParameterValue("BlendMode",getRandomArrayElement(new Integer[] { 3, 7, 12, 0, 1 } ));
		//((BlendDrawer)switcher.getScene("blob drawer").getFilter("BlendDrawer")).nextMode();
		((BlobDrawer)host.getFilter("BlobDrawer2")).setParameterValueFromSin("xRadianMod",APP.getApp().random(0f,1f));
		((BlobDrawer)host.getFilter("BlobDrawer2")).setParameterValueFromSin("yRadianMod",APP.getApp().random(0f,1f));
		
		toggleOutputs();	
	}
}

class SpinnerSequence2 extends SpinnerSequence {
    	int colour1, colour2, colour3, colour4;    	
    	public SpinnerSequence2(Scene scene, int i) {
    		// TODO Auto-generated constructor stub
    		super((BlobFX1) scene, i);
    	}
		public void setValuesForNorm(double norm, int iteration) {
    		//double inv_norm = (iteration%2==0) ? 1.0f-norm : norm;
			double inv_norm = APP.getApp().constrain((float)
					((iteration%2==0) ? norm : (float)(1.0f-norm)),
							0.0f,1.0f); //1.0f-norm : norm;// : norm;
    		//System.out.println("norm: " + norm + ", inv_norm: " + inv_norm);

    		//float iteration_warp = (float)(1.0f/(float)iteration)*(float)norm;
    		
    		host.getFilter("BlobDrawer").setParameterValue("totalRotate", (float)-norm*360.0f); //PApplet.radians((float)norm*360));
    		host.getFilter("BlobDrawer").setParameterValue("rotation", (float)-norm*180.0f);
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
    				/*(col1 >> 24) & 0xFF,
    				(col1 >> 16) & 0xFF,
    				(col1 >> 8) & 0xFF,*/
    				255);

    		((BlobDrawer)host.getFilter("BlobDrawer2")).setColour(
    				(int)APP.getApp().red(col2),
    				(int)APP.getApp().green(col2),
    				(int)APP.getApp().blue(col2));
    	}
    	public void onStart() {
    		System.out.println("onStart() " + this);
    		colour1 = randomColorMinimum(196);
    		colour2 = randomColorMinimum(96);
    		colour3 = randomColorMinimum(96);
    		colour4 = randomColorMinimum(196);
   		
    		//this.setLengthMillis(500 * (int)(APP.getApp().random(1,10)));
    		
    		((BlobDrawer)host.getFilter("BlobDrawer")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { 0, 1, 2, 3, 4 }));//, Blob.SH_TEXTURE} ));
    		((BlobDrawer)host.getFilter("BlobDrawer2")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { 0, 1, 4 })); //, Blob.SH_TEXTURE } ));
    		
    		((BlendDrawer)host.getFilter("BlendDrawer2")).changeParameterValue("BlendMode",getRandomArrayElement(new Integer[] { 2,  8, 4} ));
    		((BlobDrawer)host.getFilter("BlobDrawer2")).setParameterValueFromSin("xRadianMod",APP.getApp().random(0f,1f));
    		
    		toggleOutputs();
    	}    	
}

class SpinnerSequence3 extends SpinnerSequence {
	int colour1, colour2, colour3, colour4;    	
	public SpinnerSequence3(Scene scene, int i) {
		// TODO Auto-generated constructor stub
		super((BlobFX1) scene, i);
	}
	public void setValuesForNorm(double norm, int iteration) {
		//double inv_norm = (iteration%2==0) ? 1.0f-norm : norm;
		double inv_norm = APP.getApp().constrain((float)
				((iteration%2==0) ? norm : (float)(1.0f-norm)),
						0.0f,1.0f); //1.0f-norm : norm;// : norm;
		//System.out.println("norm: " + norm + ", inv_norm: " + inv_norm);

		//float iteration_warp = (float)(1.0f/(float)iteration)*(float)norm;
		
		host.getFilter("BlobDrawer").setParameterValue("totalRotate", (float)-norm*360.0f); //PApplet.radians((float)norm*360));
		host.getFilter("BlobDrawer").setParameterValue("rotation", (float)-norm*180.0f);
		host.getFilter("BlobDrawer2")
			.setParameterValue("totalRotate", (float)norm*180.0f) // was 720
			.setParameterValueFromSin("radius", APP.getApp().sin((float)(inv_norm))) ///2f)))
		;
		
		int col1 = lerpcolour(colour1, colour2, inv_norm);
		int col2 = lerpcolour(colour3, colour4, norm);
		
		((BlobDrawer)host.getFilter("BlobDrawer")).setColour(
				(int)APP.getApp().red(col1),
				(int)APP.getApp().green(col1),
				(int)APP.getApp().blue(col1));

		((BlobDrawer)host.getFilter("BlobDrawer2")).setColour(
				(int)APP.getApp().red(col2),
				(int)APP.getApp().green(col2),
				(int)APP.getApp().blue(col2));
	}
	public void onStart() {
		System.out.println("onStart() " + this);
		colour1 = randomColorMinimum(196);
		colour2 = randomColorMinimum(96) * 2; // brighter
		colour3 = randomColorMinimum(96) * 2;
		colour4 = randomColorMinimum(196);
		
		((BlobDrawer)host.getFilter("BlobDrawer")).changeParameterValue("edged",APP.getApp().random(1)>=0.5f);
		((BlobDrawer)host.getFilter("BlobDrawer2")).changeParameterValue("edged",APP.getApp().random(1)>=0.5f);

		
		
		this.setLengthMillis(250 * (int)(APP.getApp().random(1,5)));
		
		((BlobDrawer)host.getFilter("BlobDrawer")).setParameterDefaults();
		((BlobDrawer)host.getFilter("BlobDrawer2")).setParameterDefaults();
		
		((BlobDrawer)host.getFilter("BlobDrawer")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { Blob.SH_COMPOUND, Blob.SH_CIRCLE, Blob.SH_FLOWER } ));
		((BlobDrawer)host.getFilter("BlobDrawer2")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { Blob.SH_TEXTURE, Blob.SH_RECT, Blob.SH_POLY } ));
		
		((BlendDrawer)host.getFilter("BlendDrawer2")).changeParameterValue("BlendMode",getRandomArrayElement(new Integer[] { 2,  8, 4 } ));
		((BlobDrawer)host.getFilter("BlobDrawer2")).setParameterValueFromSin("xRadianMod",APP.getApp().random(0f,1f));
		
		toggleOutputs();
	
	}    	
}


class SpinnerSequence4 extends SpinnerSequence {
	int colour1, colour2, colour3, colour4;    	
	public SpinnerSequence4(Scene scene, int i) {
		// TODO Auto-generated constructor stub
		super((BlobFX1) scene, i);
	}
	public void setValuesForNorm(double norm, int iteration) {
		//double inv_norm = (iteration%2==0) ? 1.0f-norm : norm;
		double inv_norm = APP.getApp().constrain((float)
				((iteration%2==0) ? norm : (float)(1.0f-norm)),
						0.0f,1.0f); //1.0f-norm : norm;// : norm;
		//System.out.println("norm: " + norm + ", inv_norm: " + inv_norm);

		//float iteration_warp = (float)(1.0f/(float)iteration)*(float)norm;

		
		host.getFilter("BlobDrawer").setParameterValue("totalRotate", (float)norm*360.0f); //PApplet.radians((float)norm*360));
		host.getFilter("BlobDrawer").setParameterValue("rotation", (float)norm*180.0f);
		host.getFilter("BlobDrawer2")
			.setParameterValue("totalRotate", (float)norm*180.0f) // was 720
			.setParameterValueFromSin("radius", APP.getApp().sin((float)(inv_norm))) ///2f)))
		;
		
		int col1 = lerpcolour(colour1, colour2, inv_norm);
		int col2 = lerpcolour(colour3, colour4, inv_norm);
		
		((BlobDrawer)host.getFilter("BlobDrawer")).setColour(
				(int)APP.getApp().red(col1),
				(int)APP.getApp().green(col1),
				(int)APP.getApp().blue(col1));

		((BlobDrawer)host.getFilter("BlobDrawer2")).setColour(
				(int)APP.getApp().red(col2),
				(int)APP.getApp().green(col2),
				(int)APP.getApp().blue(col2));
	}
	public void onStart() {
		System.out.println("onStart() " + this);
		colour1 = randomColorMinimum(196);
		colour2 = randomColorMinimum(96) * 2; // brighter
		colour3 = randomColorMinimum(96) * 2;
		colour4 = randomColorMinimum(196);
		
		//this.setLengthMillis(250 * (int)(APP.getApp().random(1,5)));
		
		((BlobDrawer)host.getFilter("BlobDrawer")).setParameterDefaults();
		((BlobDrawer)host.getFilter("BlobDrawer2")).setParameterDefaults();
		
		((BlobDrawer)host.getFilter("BlobDrawer")).changeParameterValue("edged",APP.getApp().random(1)>=0.5f);
		((BlobDrawer)host.getFilter("BlobDrawer2")).changeParameterValue("edged",APP.getApp().random(1)>=0.5f);
		
		((BlobDrawer)host.getFilter("BlobDrawer")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { Blob.SH_COMPOUND, Blob.SH_CIRCLE, Blob.SH_FLOWER } ));
		((BlobDrawer)host.getFilter("BlobDrawer2")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { Blob.SH_RECT, Blob.SH_RECT, Blob.SH_POLY } ));
		
		((BlendDrawer)host.getFilter("BlendDrawer2")).changeParameterValue("BlendMode",getRandomArrayElement(new Integer[] { 2,  8, 4 } ));
		//((BlobDrawer)host.getFilter("BlobDrawer2")).setParameterValueFromSin("xRadianMod",APP.getApp().random(0f,1f));
		
		toggleOutputs();
	
	}    	
}