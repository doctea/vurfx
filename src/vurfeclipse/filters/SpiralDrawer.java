package vurfeclipse.filters;


import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;
import vurfeclipse.APP;
import vurfeclipse.Blob;
import vurfeclipse.VurfEclipse;
import vurfeclipse.scenes.Scene;

public class SpiralDrawer extends Filter {
	
	Blob b = new Blob();


  boolean continuousDraw = false;
  int w, h;

  public SpiralDrawer(Scene sc) {
    super(sc);
    this.w = sc.w;
    this.h = sc.h;
  }

  public SpiralDrawer(Scene sc, int w, int h) {
    super(sc);
    this.w = sc.w;
    this.h = sc.h;
  }

  @Override
  public boolean initialise () {
    super.initialise();
    // Extend the start and end of the curve because the first
    // and last point is a control point and is a vertex in the
    // drawn curve when using curveVertex

    //spiralCenter = new PVector(sc.w/2,sc.h/2);

    //setDefaultParameters();

    return true;
  }

  @Override
  public void setParameterDefaults() {
    super.setParameterDefaults();
    addParameter("rotation", 90.0f, 0.0f, 360.0f);
    addParameter("spiralCenter", new PVector(0.5f, 0.5f)); //this.w/2,this.h/2));
    addParameter("numofCircles", 5.0f, 1.0f, 20.0f); //4.5);
    //addParameter("startRadius", 300, 10, 500);
    addParameter("startRadius", this.h, 10, this.h*2);
    addParameter("endRadius", 1.0f, 0.2f, 20.0f); //5 //20
    addParameter("numSections", 60.0f, 1.0f, 120.0f);
    addParameter("totalRotate", 0.0f, 0.0f, 360.0f);
    addParameter("zRotate", 0.0f, 0.0f, 360.0f);

    addParameter("yRadianMod", 1.0f, 0.1f, 10.0f);
    addParameter("xRadianMod", 1.0f, 0.1f, 10.0f);

    addParameter("radius", 1.0f, 0.1f, 5.0f);

    addParameter("outline", new Boolean(false));
    
	this.addParameter("colour", VurfEclipse.makeColour(255, 128, 64, 255));
	this.addParameter("tint",  new Integer(255), 0, 255);//new Integer(128));

    addParameter("mode", new Integer(0), 0, 1);
  }

  @Override
  public Filter nextMode() {
    changeParameterValue("mode",(Integer)getParameterValue("mode")+1);
    if ((Integer)getParameterValue("mode")>1) {
      changeParameterValue("mode",new Integer(0));
    }
    return this;
  }

  /*public void setRotation(float r) {
    this.rotation = r;
  }*/

  /*float rotation = 90.0;

  PVector spiralCenter;
  float numofCircles = 4.5; //3.5;
  float startRadius = 300;
  float endRadius = 5;
  int numSections = 60;*/

  public ArrayList<PShape> collectShapes() {
    PVector pSpiralCenter = (PVector)getParameterValue("spiralCenter");
    if (pSpiralCenter!=null) pSpiralCenter = new PVector(0.0f,0.0f);
    if (pSpiralCenter.x>10.0f) {
    	// must be saved in old format - reset to center
    	pSpiralCenter.x = 0.5f;
    	pSpiralCenter.y = 0.5f;
    }
    float numofCircles = (Float)getParameterValue("numofCircles");
    float startRadius = (Integer)getParameterValue("startRadius");
    float endRadius = (Float)getParameterValue("endRadius");
    //int numSections = (Integer)getParameterValue("numSections");
    float numSections = (Float)getParameterValue("numSections");
    //float deltaMod = (Float)getParameterValue("deltaMod");
    float totalRotate = (Float)getParameterValue("totalRotate");
    float rotation = (Float)getParameterValue("rotation");
    float radius = (Float)getParameterValue("radius");
    float zRotate = (Float)getParameterValue("zRotate");

    float xRadianMod = (Float)getParameterValue("xRadianMod");
    float yRadianMod = (Float)getParameterValue("yRadianMod");
    
    float currRadius = startRadius;

    float totalRadian = numofCircles * PApplet.PI * 2;
    float startRadian = -PApplet.PI;
    float endRadian = startRadian + totalRadian;
    float currentRadian = startRadian;

    //float zRotate = 0.0f;

    // This depends on the current radius
    float deltaAngle = totalRadian / numSections;

    // Spiral starts from outside
    float x;// = cos(startRadian) * startRadius;
    float y;// = sin(startRadian) * startRadius;

    startRadian += deltaAngle;
    endRadian -= deltaAngle;
    currentRadian = endRadian;

    //spiralCenter = new PVector(sc.w/2,sc.h/2);

    PVector spiralCenter = new PVector(pSpiralCenter.x * sc.w, pSpiralCenter.y * sc.h);

    ArrayList<PShape> list = new ArrayList<PShape> ();
    
    //curveVertex(x + spiralCenter.x, y + spiralCenter.y);
    //while (currentRadian < endRadian) {
    if ((Integer)getParameterValue("mode")==0) {
      while (currentRadian >= startRadian) {
        //currentRadian += deltaAngle;
        currentRadian -= (double)deltaAngle + 0.0001d;
        currRadius = PApplet.map(currentRadian, startRadian, endRadian, startRadius, endRadius) * radius;
        x = (float) ((double)xRadianMod*(double)PApplet.cos(currentRadian) * ((double)currRadius / (double)radius));// / radius);
        y = (float) ((double)yRadianMod*(double)PApplet.sin(currentRadian) * (double)((double)currRadius / (double)radius));// / radius);

        //this.drawObject(x,y,spiralCenter,totalRotate,rotation,zRotate,currRadius,currentRadian,deltaAngle);
        list.add(collectObject(x,y,spiralCenter,totalRotate,rotation,zRotate,currRadius,currentRadian,deltaAngle));
      }
    } else {
      int n = 0;
      while (n <= numofCircles*10) {
        //this.drawObject(PApplet.abs(PApplet.sin(n)*this.w), 0, spiralCenter,totalRotate,rotation,zRotate,currRadius,currentRadian,deltaAngle);
    	list.add(this.collectObject(PApplet.abs(PApplet.sin(n)*this.w), 0, spiralCenter,totalRotate,rotation,zRotate,currRadius,currentRadian,deltaAngle));

        n++;
      }
      n--;
      //this.drawObject(PApplet.abs(PApplet.sin(n)*this.w), 0, spiralCenter,totalRotate,rotation,zRotate,currRadius,currentRadian,deltaAngle);
      list.add(this.collectObject(PApplet.abs(PApplet.sin(n)*this.w), 0, spiralCenter,totalRotate,rotation,zRotate,currRadius,currentRadian,deltaAngle));
    }

    return list;
  }


  private PShape collectObject(float x, float y, PVector spiralCenter, float rotation, float totalRotate, // rotation and totalRotate are swapped for some reason?!
		float zRotate, float currRadius, float currentRadian, float deltaAngle) {

	  PShape out = collectActualObject(currRadius, currentRadian);
	  
	  if (out==null) {
		  println("collectObject caught a null object!");
		  return null;
	  }
	  
      //out.translate(spiralCenter.x, spiralCenter.y);    // move to the spiral center

      out.rotate(PApplet.radians(totalRotate));  // rotate around the spiral point by the total rotation amount

      out.translate(x, y);  // move to the plot point

      //float new_w = /*sc.w/*/(sc.w/(currRadius)); //theta*thetaspeed);//theta; ///  radius. width.
      //float new_h = /*sc.h/*/(sc.h/(currRadius)); //theta*thetaspeed);//theta;
      //out.rotate(PApplet.radians(rotation)+currentRadian);//+135);  // rotate around the plot point

      out.rotate(0.0f,0.0f,PApplet.radians(zRotate),0);

      return out;
      //drawActualObject(out, currRadius, radians(rotation)+currentRadian);
      //drawActualObject(out, currRadius, currentRadian);

      //out.popMatrix();
}

  

  public PShape collectActualObject(float currRadius, float currentRadian) {
      float units_w = 1.0f/4, units_h = 0.75f/4;
      float new_w = /*sc.w * (sc.w/*/units_w*currRadius;///4;//);
      float new_h = /*sc.h * (sc.h/*/units_h*currRadius;///4;//);

      //b = new Blob();
      //b.setTint(tint);
      //b.setColour(c);
      
      PShape p = b.getShapePolygon(4);
      p.scale(new_w,new_h);
      p.rotate(currentRadian);
      
      return p;
  }

  public void drawObject(float x, float y, PVector spiralCenter, float totalRotate, float rotation, float zRotate, float currRadius, float currentRadian, float deltaAngle) {
      //float new_w = /*sc.w/*/(sc.w/4*theta*thetaspeed);//theta;
      //float new_h = /*sc.h/*/(sc.h/4*theta*thetaspeed);//theta;

	  PGraphics out = out();
      out.pushMatrix();

      //out.translate(spiralCenter.x, spiralCenter.y);    // move to the spiral center

      //if((Float)getParameterValue("totalRotate")!=0.0) out.rotate(radians((Float)getParameterValue("totalRotate")));
      //println("totalRotate is " + totalRotate);
      out.rotate(PApplet.radians(totalRotate));  // rotate around the spiral point by the total rotation amount


      out.translate(x, y);  // move to the plot point

      //float new_w = /*sc.w/*/(sc.w/(currRadius)); //theta*thetaspeed);//theta; ///  radius. width.
      //float new_h = /*sc.h/*/(sc.h/(currRadius)); //theta*thetaspeed);//theta;
      out.rotate(PApplet.radians(rotation)+currentRadian);//+135);  // rotate around the plot point
      out.rotate(0.0f,0.0f,PApplet.radians(zRotate),0);

      //drawActualObject(out, currRadius, radians(rotation)+currentRadian);
      drawActualObject(out, currRadius, currentRadian);

      out.popMatrix();
  }

  public void drawActualObject(PGraphics out, float currRadius, float currentRadian) {
      //float units_w = sc.w/currRadius;  // get proportion of sc.w appropriate to radius - larger radius, larger part of sc.w.  /currRadius = smaller radius, smaller size.
      //float units_h = sc.h/currRadius;
      float units_w = 1.0f/4, units_h = 0.75f/4;
      float new_w = /*sc.w * (sc.w/*/units_w*currRadius;///4;//);
      float new_h = /*sc.h * (sc.h/*/units_h*currRadius;///4;//);

      out.pushMatrix();
      out.translate(-new_w/2,-new_h/2);
      if((Boolean)getParameterValue("outline")==true) {
        out.stroke(128);
        out.fill(128);
        out.rect(-2,-2,new_w+4,new_h+4);
      }
      //out.noTint();
      out.image(in(),0,0,new_w,new_h);//sc.w/100*theta,sc.h/100*theta);
      //out.image(src.getTexture(),0,0,new_w,new_h);
      out.popMatrix();
      /*out.fill(random(255),random(255),random(255),random(255));
      //out.ellipse(-20,20,20,20);
      out.ellipse(0,0,20,20);*/
  }


  public boolean applyMeatToBuffers() {
	  // new version for single-pass drawing
	  // collect objects to draw
	  // add them to an object group
	  // draw them in one go
	  
	  ArrayList<PShape> list = this.collectShapes();
	  PGraphics out = out();
	  out.background(0,0,0,0);
	  
	  out.pushMatrix();
	  
	  PShape g = APP.getApp().createShape(APP.getApp().GROUP);
	  println("got " + list.size() + " shapes to draw");
	  for (PShape l : list)
		g.addChild(l);
	  
	  //g.setTint((int)this.getParameterValue("tint"));
	  //g.setFill((int) (Math.random()*255));
	  g.setFill((int)this.getParameterValue("colour"));
	  g.setStroke(true);//(boolean)this.getParameterValue("edged"));
	  g.setStrokeWeight(0.0001f);
	  //g.setTexture(in());
	  //g.setFill();
	  
	  out.translate(w/2, h/2);
	  
	  out.rotate((float) Math.toRadians((float)this.getParameterValue("totalRotate")));
	  
	  out.shape(g);
	  
	  out.popMatrix();
	  
	  return true;
  }
  
  //@Override
  @Deprecated
  public boolean old____applyMeatToBuffers() {
	  //if (true) return true;
    //if (src!=out) out.background(0); else out.image(src.getTexture(),0,0,sc.w,sc.h);
    //if (src!=out)
	  
    PGraphics out = out();
    out.background(0,0,0,0);
    //out.background(128,0,0,0);
    //out.setDefaultBlend();
    //out.background(src.getTexture());

    //out.pushMatrix();
    out.translate(w/2, h/2);
    out.scale(0.5f);
    
    //from https://forum.processing.org/topic/drawing-smooth-spiral
    /*PVector spiralCenter = this.spiralCenter;//new PVector(sc.w/2, sc.h/2);
    float numofCircles = this.numofCircles;
    float startRadius = this.startRadius; //300
    float endRadius = this.endRadius;  //20
    int numSections = this.numSections; // 60// bigger the number the smoother the spiral*/
    PVector pSpiralCenter = (PVector)getParameterValue("spiralCenter");
    if (pSpiralCenter.x>10.0f) {
    	// must be saved in old format - reset to center
    	pSpiralCenter.x = 0.5f;
    	pSpiralCenter.y = 0.5f;
    }
    float numofCircles = (Float)getParameterValue("numofCircles");
    float startRadius = (Integer)getParameterValue("startRadius");
    float endRadius = (Float)getParameterValue("endRadius");
    //int numSections = (Integer)getParameterValue("numSections");
    float numSections = (Float)getParameterValue("numSections");
    //float deltaMod = (Float)getParameterValue("deltaMod");
    float totalRotate = (Float)getParameterValue("totalRotate");
    float rotation = (Float)getParameterValue("rotation");
    float radius = (Float)getParameterValue("radius");
    float zRotate = (Float)getParameterValue("zRotate");

    float xRadianMod = (Float)getParameterValue("xRadianMod");
    float yRadianMod = (Float)getParameterValue("yRadianMod");

    float currRadius = startRadius;

    float totalRadian = numofCircles * PApplet.PI * 2;
    float startRadian = -PApplet.PI;
    float endRadian = startRadian + totalRadian;
    float currentRadian = startRadian;

    //float zRotate = 0.0f;

    // This depends on the current radius
    float deltaAngle = totalRadian / numSections;

    // Spiral starts from outside
    float x;// = cos(startRadian) * startRadius;
    float y;// = sin(startRadian) * startRadius;

    startRadian += deltaAngle;
    endRadian -= deltaAngle;
    currentRadian = endRadian;

    //spiralCenter = new PVector(sc.w/2,sc.h/2);

    PVector spiralCenter = new PVector(pSpiralCenter.x * sc.w, pSpiralCenter.y * sc.h);

    //curveVertex(x + spiralCenter.x, y + spiralCenter.y);
    //while (currentRadian < endRadian) {
    if ((Integer)getParameterValue("mode")==0) {
      while (currentRadian >= startRadian) {
        //currentRadian += deltaAngle;
        currentRadian -= (double)deltaAngle + 0.0001d;
        currRadius = PApplet.map(currentRadian, startRadian, endRadian, startRadius, endRadius) * radius;
        x = (float) ((double)xRadianMod*(double)PApplet.cos(currentRadian) * ((double)currRadius / (double)radius));// / radius);
        y = (float) ((double)yRadianMod*(double)PApplet.sin(currentRadian) * (double)((double)currRadius / (double)radius));// / radius);

        this.drawObject(x,y,spiralCenter,totalRotate,rotation,zRotate,currRadius,currentRadian,deltaAngle);
      }
    } else {
      int n = 0;
      while (n <= numofCircles*10) {
        this.drawObject(PApplet.abs(PApplet.sin(n)*this.w), 0, spiralCenter,totalRotate,rotation,zRotate,currRadius,currentRadian,deltaAngle);
        n++;
      }
      n--;
      this.drawObject(PApplet.abs(PApplet.sin(n)*this.w), 0, spiralCenter,totalRotate,rotation,zRotate,currRadius,currentRadian,deltaAngle);
    }
    
    //out.popMatrix();

    return true;
  }


}
