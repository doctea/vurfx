package vurfeclipse.filters;

import java.util.HashMap;

import processing.core.PGraphics;
import processing.core.PVector;
import vurfeclipse.filters.*;
import vurfeclipse.scenes.*;
import vurfeclipse.streams.*;
import vurfeclipse.*;
import vurfeclipse.scenes.Scene;

public class BlobDrawer extends SpiralDrawer {

  Blob b = new Blob();

  public Filter nextMode () {
    b.nextShape();
    this.setParameterValue("shape", b.getShape());//(Integer)this.getParameterValue("shape")+1);
    return this;
  }

  public BlobDrawer(Scene sc) {
    super(sc);
  }

  public BlobDrawer(Scene sc, int ov_w, int ov_h) {
	super(sc, ov_w, ov_h);
  }

  public boolean initialise () {
    //b.setInput(src.getTexture());
    //b.loadSVG("output/ds2014/dseye.svg");
    return super.initialise();
  }

  public BlobDrawer setImage(String fn) {
    this.b.setImage(fn);
    ImageRepository.IR.cacheLoad(fn);
    return this;
  }

  @Override
  synchronized public void setParameterDefaults () {
    //this.setParameterValue("radius", 10.0);
    //this.setParameterValue("rotation", 0.0);
    super.setParameterDefaults();
    this.addParameter("edged", new Boolean(true));
    this.addParameter("tint",  new Integer(255), 0, 255);//new Integer(128));
    this.addParameter("shape", new Integer(0), 0, b.shapesCount);
    //this.addParameter("colour",new Integer(255), new Integer(0), new Integer(2^32)); //APP.color(APP.random(255),APP.random(255),APP.random(255),128)));

    //this.addParameter("spiralCenter", new PVector(this.w/2, this.h/2));		// duplicate, already specified in superclass?

    //this.addParameter("startRadius")

    //this.addParameter("radius", 0.5, 0.01, 20.0);
  }

  @Override
  synchronized public void updateParameterValue(String paramName, Object value) {
    //System.out.println("setting " + paramName + " to " + value);
    /*if(paramName.equals("radius"))
        b.setRadius((Integer)value);
    else if(paramName.equals("rotation"))
        b.setRotation((Integer)value);
    else*/ if(paramName.equals("edged"))
        b.setEdge((Boolean)value);
    else if(paramName.equals("tint"))
        b.setTint((Integer)value);
    else if(paramName.equals("shape")) {   // there goes my hero <3
    	if (src==null) src = sc.getCanvas("pix0").getSurf();
    	if (src!=null) b.setInput(src);
        b.setShape((Integer)value);
    }
    else if(paramName.equals("colour"))
        b.setColour((Integer)value);
    else
      super.updateParameterValue(paramName, value);
  }

  public void setColour(int r, int g, int b2, int a) {
    b.setColour(r,g,b2,a);
  }
  public void setColour(int r, int g, int b2) {
	    b.setColour(r,g,b2);
  }

  public void loadSVG(String filename) {
	  b.loadSVG(filename);
  }

  int colourSwitchCount = 0;
  int colourSwitchEvery = 50; // frames

  synchronized public void drawActualObject(PGraphics out, float currRadius, float currentRadian) {

	  	//if (true) return;
	  
	/*colourSwitchCount++;
	if (colourSwitchEvery<=colourSwitchCount) {*/
			//b.setColour((int)((VurfEclipse)APP.getApp()).random(255), (int)((VurfEclipse)APP.getApp()).random(255), (int)((VurfEclipse)APP.getApp()).random(255), (int)((VurfEclipse)APP.getApp()).random(255));
		/*	colourSwitchEvery = (int) ((VurfEclipse)APP.getApp()).frameRate;
			colourSwitchCount = 0;
	}*/

  	b.setShape((Integer)getParameterValue("shape"));
    //b.setTint(255);
	//if ((int)((VurfEclipse)APP.getApp()).random(100)==0) println("BlobDrawer is setting blob texture to " + src.getTexture());
    //b.setInput(sc.host.getCanvas("/out").getSurf().getTexture()); //src.getTexture());
  	b.setInput(sc.getCanvas("temp3").getSurf());//.getTexture());
    //b.setColour(255); //new Integer((int)APP.random(255) << (int)APP.random(255) << (int)APP.random(255) << 255));
    //b.setColour((int)((VurfEclipse)APP.getApp()).random(255), (int)((VurfEclipse)APP.getApp()).random(255), (int)((VurfEclipse)APP.getApp()).random(255), (int)((VurfEclipse)APP.getApp()).random(255));
    //b.setColour((Integer)this.getParameterValue("colour"));
    //b.setColour(0);
    b.setRadius(currRadius);// * (int)random(5));
    b.setRotation((int)Math.toRadians((currentRadian)));
    //System.out.println("currentRadian is " + currentRadian);
    //b.setTint(128);
    b.draw(out,src);
    //out.tint(128);
    //out.ellipse(0,0,currRadius/8,currRadius/8);

  }

  

	@Override
	public HashMap<String,Object> collectFilterSetup() {	// for saving snapshots, save setup of filter
		HashMap<String,Object> output = super.collectFilterSetup();
		output.put("canvas_pix0", sc.getCanvas("pix0"));
		return output;
	}

}
