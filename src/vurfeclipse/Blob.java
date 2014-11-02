package vurfeclipse;

import processing.core.PApplet;
import processing.core.PShape;
import codeanticode.glgraphics.*;

public class Blob {
  public int x = 0;

  public int y = 0;

  int r=10;
  
  int c;
  int tint = 255;
  
  private int shape = 1;
  public static int SH_CIRCLE = 0;
  public static int SH_RECT = 1;
  public static int SH_POLY = 2;
  public static int SH_FLOWER = 3;
  public static int SH_COMPOUND = 4;	// compound and texture were previously flipped
  public static int SH_TEXTURE = 5;
  public static int SH_SVG = 6;
  public static int SH_IMAGE = 7;  
  public int shapesCount = 8;
  public int SH_RANDOM = shapesCount;
  
  int rot = 0;
  
  private int strokeSize = 3;
  
  int numSides = 6;
  
  private boolean scaleRelative = false;
  
  boolean edge = false;
  
  transient GLTexture src;
  String imageName;
  
  public void setInput(GLTexture t) {
    this.src = t;
  }
  
  
  public void setImage(String imageName) {
    this.imageName = imageName;
  }
  
  public Blob() {

  }
  
  public Blob(int shape, int x, int y, int rot) {
    //this.shape = shape;
    this();
    this.setShape(shape);
    this.setXY(x,y);
    this.setRotation(rot);
  }
  
  PShape svg;
  public void loadSVG(String fn) {
    svg = APP.getApp().loadShape(fn); 
  }
  
  public void setShape (int s) {
    this.shape = PApplet.constrain(s,0,shapesCount);
    if (this.shape==SH_TEXTURE && src==null) {
      setShape(s+1);
    }
  }
  
  public int nextShape() {
    setShape(getShape() + 1);
    if (getShape()>=shapesCount) setShape(0);
    return getShape();
  }
  
  public void setXY(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  public void setRadius(int r) {
    this.r = r;
  }
  
  public void setColour(int c) {
    this.c = c;
  }
  public void setColour(int r , int g , int b, int a) {
    this.c = a | r<<24 | g << 16 | b << 8;// | a;
  }
  public void setColour(int r, int g, int b) {
	  this.c = APP.getApp().color(r,g,b);
  }
  
  
  public void setTint(int c) {
    this.tint = c;
  }
  
  public void setRotation(int rot) {
    this.rot = rot;
  }
  
  public void setEdge (boolean edge) {
    this.edge = edge;
  }
  
  public void setSides(int sides) {
    this.numSides = sides;
  }
  public int getSides() {
    return numSides;
  }
 
  
  //void draw(PGraphics out) {
  public void draw(GLGraphicsOffScreen out, GLGraphicsOffScreen src) {
    this.src = src.getTexture();
    draw(out);
  }
  public void draw(GLGraphicsOffScreen out) {
    out.pushStyle();
    if (tint!=255) 
      out.fill(c, tint);
    else {
      out.noTint();
      out.fill(c);
    }
    if (!edge) {
      out.noStroke();
    } else {
      out.stroke(0);
      out.strokeWeight(this.getStrokeSize());
    }
    //out.noTint();
    //out.ellipse(x-r,y-r,r/2,r/2); 
    
    out.pushMatrix();
    out.translate(x,y);
    
    if (getShape()!=0 && rot>0) {
      //out.rotate(c<<24);
      //out.rotate(random(rot));
      out.rotate(rot%(((VurfEclipse)APP.getApp()).millis()/1000)%60);
    }
    
    int R = isScaleRelative()?(int)((VurfEclipse)APP.getApp()).brightness(c)/8 : r;
/*          if (doRelative) {
        blobs[b].setRadius((int)hue(pix)/8+((int)random(r)));//(int)random(r));
      }*/
      
//System.out.println("draw shape radius " + R + " shape " + shape);
    int oldShape = getShape();
    if (oldShape==SH_RANDOM) setShape((int)((VurfEclipse)APP.getApp()).random(SH_RANDOM-3));
    if (getShape()==SH_CIRCLE) {
      //out.ellipse(x,y,r,r);
      out.ellipse(0,0,R,R);
    } else if (getShape()==SH_RECT) {
      //out.rect(x-r,y-r,r/2,r/2);
      out.rectMode(PApplet.RADIUS);
      //out.rect(-R,-R,R,R);
      out.rect(0,0,R/2,R/2);
      //out.rect(0,0,R,R);
      //polygon(out, /*doPolyRand?(int)random(3,8):*/4, 0, 0, R, R, 0);
    } else if (getShape()==SH_POLY) {
      //System.out.println("doing poly?");
      polygon(out, /*doPolyRand?(int)random(3,8):*/numSides, 0, 0, R, R, 0);
    } else if (getShape()==SH_FLOWER) {
      polygon(out, /*doPolyRand?(int)random(3,8):*/numSides, 0, 0, R, R, 0);
      out.pushMatrix();
      out.rotate(rot*2);
      for (int i = 0; i < 360; i+=20) {
        float x = PApplet.sin(PApplet.radians(i)) * R/2;
        float y = PApplet.cos(PApplet.radians(i)) * R/2;
        out.pushMatrix();
        out.translate(x,y);
        out.rotate(rot*4);
        polygon(out, numSides, 0, 0, R/4, R/4, 0);
        out.popMatrix();
        //polygon(out, numSides, x, y, R/4, R/4, 0);
      }
      out.popMatrix();
    } else if (getShape()==SH_TEXTURE) {
      float units_w = 1.0f, units_h = 0.75f;
      float new_w = units_w * R/2;///*sc.w * (sc.w/*/units_w*R;///4;//);  
      float new_h = units_h * R/2;///*sc.h * (sc.h/*/units_h*R;///4;//);
  
      out.pushMatrix();
      out.translate(-new_w/2,-new_h/2);
      out.image(src,0,0,new_w,new_h);//sc.w/100*theta,sc.h/100*theta);
      //out.image(src.getTexture(),0,0,new_w,new_h);
      out.popMatrix();
    } else if (getShape()==SH_COMPOUND) {
      drawCompoundBlob(out, x, y, R, R, 0);
    } else if (getShape()==SH_SVG && svg!=null) {
    	System.out.println("SVG Draw!");
      drawSVG(out, x, y, R, R, 0);
    } else if (getShape()==SH_IMAGE && imageName!=null && !imageName.equals("")) {
      drawImage(out, x, y, R, R, 0);
    }
    setShape(oldShape);
      
    
    out.popMatrix();
    
    out.popStyle();
 
  }
  
  
  //void polygon(PGraphics out, int n, float cx, float cy, float r)
  void polygon(GLGraphicsOffScreen out, int n, float cx, float cy, float r)
  {
    polygon(out, n, cx, cy, r * 2.0f, r * 2.0f, 0.0f);
  }
  
  float TWO_PI = PApplet.TWO_PI;
  float[] twopi_lookup = new float[] { 3, TWO_PI/1, TWO_PI/2, TWO_PI/3, TWO_PI/4, TWO_PI/5, TWO_PI/6, TWO_PI/7, TWO_PI/8, TWO_PI/9, TWO_PI/10, TWO_PI/11, TWO_PI/12, TWO_PI/13, TWO_PI/14, TWO_PI/15, TWO_PI/16 };
  
  //void polygon(PGraphics out, int n, float cx, float cy, float w, float h, float startAngle)
  void polygon(GLGraphicsOffScreen out, int n, float cx, float cy, float w, float h, float startAngle)
  {
    if (n > 2)
    {
      //float angle = TWO_PI/n;
      float angle = twopi_lookup[n];
  
      /* The horizontal "radius" is one half the width;
       the vertical "radius" is one half the height */
      w = w / 2.0f;
      h = h / 2.0f;
  
      out.beginShape();
      for (int i = 0; i < n; i++)
      {
        float calc = startAngle + angle * i;
        out.vertex(cx + w * ((VurfEclipse)APP.getApp()).cos(calc),
        cy + h * ((VurfEclipse)APP.getApp()).sin(calc));
      }
      out.endShape(PApplet.CLOSE);
    }
  }

  void drawImage(GLGraphicsOffScreen out, float cx, float cy, float w, float h, float startAngle) {
    if (imageName!=null) {
      out.pushMatrix();
      out.rotate(startAngle);
      out.image(ImageRepository.IR.getImageForFilename(imageName), cx, cy, w, h);
      out.popMatrix(); 
    }
  }

  void drawSVG(GLGraphicsOffScreen out, float cx, float cy, float w, float h, float startAngle) {
    if (svg!=null) {
      out.pushMatrix();
      out.rotate(startAngle);
      out.shape(svg, cx, cy, w, h);
      out.popMatrix();
    }
  }

  Blob compoundBlob[];
  void drawCompoundBlob(GLGraphicsOffScreen out, float cx, float cy, float w, float h, float startAngle) {
    
    //if(this.compoundBlob==null) {
      float step = (float)h/8;
       this.compoundBlob = new Blob[] {
         new Blob(SH_RECT,x,y,(int)startAngle),
         new Blob(SH_POLY,x,y+(int)step,(int)startAngle),
         new Blob(SH_RECT,x,y+(int)step+10,(int)startAngle),
         new Blob(SH_POLY,x,y+(int)step*2,(int)startAngle),
         new Blob(SH_POLY,x,y+(int)step*3,(int)startAngle),
         //new Blob(SH_TEXTURE,x,y+(int)step*4,(int)startAngle),
         new Blob(SH_POLY,x-15,y+(int)step*5,(int)startAngle),
         new Blob(SH_POLY,x+15,y+(int)step*5,(int)startAngle),
       };
    //};
    
    int numSpokes = 5;
    for (int r = 0 ; r < 360 ; r+=(360/numSpokes)) {
      out.pushMatrix();
      out.rotate(PApplet.radians(r));
      for (int i = 0 ; i < compoundBlob.length ; i++) {
        compoundBlob[i].setEdge(edge);
        //compoundBlob[i].setColour(color(random((i/numSpokes)*255.0),random(255.0),random(255.0),255));
        compoundBlob[i].setColour(this.c); ///1 * color(random(255.0),random(255.0),random(255.0),255));
        compoundBlob[i].setTint(255);
        compoundBlob[i].setRadius(i*(int)w/12);
        compoundBlob[i].setSides(((VurfEclipse)APP.getApp()).constrain(8+i/3,3,12));
        compoundBlob[i].setInput(src);//out.getTexture());//src
        if (compoundBlob[i].getShape() == SH_COMPOUND) compoundBlob[i].setShape(SH_POLY);
        //compoundBlob[i].setShape(i%shapesCount-2);
        compoundBlob[i].draw(out);
      }
      out.popMatrix();
    }
  }


public boolean isScaleRelative() {
	return scaleRelative;
}


public void setScaleRelative(boolean scaleRelative) {
	this.scaleRelative = scaleRelative;
}


public int getStrokeSize() {
	return strokeSize;
}


public void setStrokeSize(int strokeSize) {
	this.strokeSize = strokeSize;
}


public int getShape() {
	return shape;
}
  
  
}
