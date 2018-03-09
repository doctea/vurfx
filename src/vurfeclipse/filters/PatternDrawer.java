package vurfeclipse.filters;


import java.util.*;

import vurfeclipse.APP;
import vurfeclipse.Blob;
import vurfeclipse.VurfEclipse;
import vurfeclipse.scenes.Scene;

class PatternDrawer extends Filter {

  Blob b = new Blob();

  public Filter nextMode () {
    b.nextShape();
    this.setParameterValue("shape", b.getShape());//(Integer)this.getParameterValue("shape")+1);
    return this;
  }

  PatternDrawer(Scene sc) {
    super(sc);
  }

  public boolean initialise () {
    //b.setInput(src.getTexture());
    //b.loadSVG("output/ds2014/dseye.svg");
    super.initialise();
    for (int i = 0 ; i < 10 ; i++) {
      Blob b = new Blob(Blob.SH_CIRCLE,(int)(sc.w*((VurfEclipse)APP.getApp()).abs(((VurfEclipse)APP.getApp()).sin(i))),sc.h/2,(int)((VurfEclipse)APP.getApp()).random(360));
      b.setColour(((VurfEclipse)APP.getApp()).makeColour(randomInt(255),randomInt(255),randomInt(255)));
      b.setEdge(randomInt(1)==0);
      blobs.add(b);
    }

    return true;
  }


  public void setParameterDefaults () {
    //this.setParameterValue("radius", 10.0);
    //this.setParameterValue("rotation", 0.0);
    super.setParameterDefaults();
/*    this.addParameter("edged", new Boolean(true));
    this.addParameter("tint", new Integer(128), 0, 255);//new Integer(128));
    this.addParameter("shape", new Integer(0), 0, b.shapesCount);
    this.addParameter("colour", new Integer(255), new Integer(0), new Integer(2^32)); //APP.color(APP.random(255),APP.random(255),APP.random(255),128)));*/
    //this.addParameter("radius", 0.5, 0.01, 20.0);
  }

  public void updateParameterValue(String paramName, Object value) {
    //System.out.println("setting " + paramName + " to " + value);
    /*if(paramName.equals("radius"))
        b.setRadius((Integer)value);
    else if(paramName.equals("rotation"))
        b.setRotation((Integer)value);
    else*/ if(paramName.equals("edged"))
        b.setEdge((Boolean)value);
    else if(paramName.equals("tint"))
        b.setTint((Integer)value);
    else if(paramName.equals("shape"))    // there goes my hero <3
        b.setShape((Integer)value);
    else if(paramName.equals("colour"))
        b.setColour((Integer)value);
    else
      super.updateParameterValue(paramName, value);
  }

  public void setColour(int r, int g, int b2, int a) {
    b.setColour(r,g,b2,a);
  }

  public boolean applyMeatToBuffers () {
    return drawPattern();
  }


  ArrayList<Blob> blobs = new ArrayList<Blob> ();

  public boolean drawPattern() {
    out().background(((VurfEclipse)APP.getApp()).makeColour(255,255,255,0));
    Iterator<Blob> it = blobs.iterator();
    while (it.hasNext()) {
      Blob b = it.next();

      b.draw(out());
    }
    return true;
  }

  /*public void drawActualObject(GLGraphicsOffScreen out, float currRadius, float currentRadian) {
    //b.setTint(255);
    b.setInput(src.getTexture());
    //b.setColour(255); //new Integer((int)APP.random(255) << (int)APP.random(255) << (int)APP.random(255) << 255));
    b.setColour((int)APP.random(255), (int)APP.random(255), (int)APP.random(255), (int)APP.random(255));
    //b.setColour((Integer)this.getParameterValue("colour"));
    //b.setColour(0);
    b.setRadius((int)currRadius);// * (int)random(5));
    b.setRotation((int)currentRadian);
    //b.setTint(128);
    b.draw(out,src);
    //out.tint(128);
    //out.ellipse(0,0,currRadius/8,currRadius/8);

  }*/


}
