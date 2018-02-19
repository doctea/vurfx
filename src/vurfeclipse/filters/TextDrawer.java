package vurfeclipse.filters;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import vurfeclipse.APP;
import vurfeclipse.VurfEclipse;
import vurfeclipse.scenes.Scene;

public class TextDrawer extends Filter {
  
  String t;
  
  String fontName = "LCDSolid-128.vlw";
  
  int x = 0, y = 0;
  int w, h;
  
  int colour = 255;
  int rotation = 0;
  int zrotation = 0;
  
  int fontSize = 128;
  
  
  boolean motionBlur = false;
  boolean continuousDraw = false;
  
  public TextDrawer(Scene sc) {
    super(sc);
    this.w = sc.w;
    this.h = sc.h;
  }
  public TextDrawer(Scene sc, String t) {
    this(sc);
    this.t = t;
  }
  public TextDrawer(Scene sc, int x, int y, int w, int h) {
    this(sc);
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }
  @Override
  public void setParameterDefaults() {
    super.setParameterDefaults();
    addParameter("text", ":)");    
    addParameter("colour", 255, 0, 255);
    addParameter("rotation", 0, 0, 360);
    addParameter("zrotation", 0, 0, 360);
    addParameter("scale", new Float(1.0f), 0.01f, 4.0f);
    addParameter("translate_x", new Float(0.0f), -1.0f, 1.0f);
    addParameter("translate_y", new Float(0.0f), -1.0f, 1.0f);
    addParameter("continuousDraw", new Boolean(true));
    addParameter("motionBlur", new Boolean(true));
    addParameter("happyNewYear", new Boolean(false));
  }
  @Override
  public void updateParameterValue(String paramName, Object value) {
    if (paramName.equals("text")) {
      //this.t = (String)value;
      this.setText((String)value);
      println("Changed text to " + value);
    } else if (paramName.equals("colour")) {
      //this.colour = (Integer)value;
      this.setColour((Integer)value);
    } else if (paramName.equals("rotation")) {
      //this.rotation = (Float)value;
      this.setRotation((Integer)value);
    } else if (paramName.equals("continuousDraw")) {
      this.setContinuousDraw((Boolean)value);
    } else if (paramName.equals("zrotation")) {
      this.setZRotation((Integer)value);
    } else if (paramName.equals("motionBlur")) {
    	  this.motionBlur = ((Boolean)value); 
    } else {
      super.updateParameterValue(paramName,value);
    }
  }
  
  /*public void setXYOffset(int x, int y) {
    this.offsetx = x;
    this.offsety = y;
  }*/
   
  public void setFont(PFont font) {
	  this.font = font;
	  this.fontSize = font.getSize();
  }
  
  public PFont getFont() {
    if (this.font==null)
      font = ((VurfEclipse)APP.getApp()).loadFont(fontName);
    //System.out.println("returning font " + font);
    return font;
  }
  
  transient PFont font;
  public boolean initialise() {
    // set up inital variables or whatevs 
    
    font = getFont();
    
    return true;
  }
  public void setColour(int c) {
    this.colour = c;
  }
  public void setText(String t) {
    this.t = t;
  }  
  public void setRotation(int r) {
    this.rotation = r;
  }
  public void setZRotation(int r) {
    this.zrotation = r;
  }
  
  public TextDrawer setContinuousDraw() {
    return this.setContinuousDraw(true);
  }
  public TextDrawer setContinuousDraw(boolean t) {
    this.continuousDraw = t;
    return this;
  }
  
  public String currentCache = "";
  public void drawText() {
    if (t==null) return;
    String currentTag = this.t + ":" + getFont() + ":" + t.length() + ":" + this.rotation + ":" + this.zrotation + ":" + this.motionBlur;
    if (continuousDraw || !currentTag.equals(currentCache)) {
      //System.out.println("currentTag drawing " + currentTag + "(cached is " + currentCache + ")");
      int fontHeight = 
    		t.length()==1 ?
    		  fontSize 
    		: 
		    	  /*PApplet.constrain(
		        h - (((t.length()*t.length())/t.length()/2)*fontSize)
		        , h/5, h
		       )*/
    			96
		    ;
      //int fontHeight = fontSize;
      //System.out.println("fontHeight for '" + t + "' (length " + t.length() + ") is " + fontHeight);
      
      //if (!motionBlur) {
    	//out.background(0,0,0,0);
      /*} else {
    	out.fill(0,128);
      	out.rect(0,0,w,h);
      }*/
      
      PGraphics out = out();
      
      out.beginDraw();
      //out.clear(0);
      out.pushMatrix();
      out.background(0,0,0,0);
      out.fill(colour);
      out.textFont(getFont(), fontHeight); //256);
      //out.textSize(256);
      out.textAlign(PApplet.CENTER);
      //out.text(t, w/2, (h/2)+128);
      //out.translate(w/2,(h/2)+(fontHeight/2));///2);
      out.translate(w/2,h/2);
      if (rotation!=0) {
        out.rotate(PApplet.radians(rotation));
      }
      out.translate(0, fontHeight/2);
      if (zrotation!=0) {
        out.rotateY(PApplet.radians(zrotation));
      }
      out.translate(
    		  w*(Float)this.getParameterValue("translate_x"), 
    		  h*(Float)this.getParameterValue("translate_y")
      ); 
      out.scale((float) this.getParameterValue("scale"));
      out.text(getText(), 0, 0);
      out.popMatrix();
      out.endDraw();
      currentCache = currentTag;
    } else {
      //System.out.println("Using cached " + currentTag);
    }
  }
  
  private String getText() {
	  if ((Boolean)this.getParameter("happyNewYear").value==true) {
		  DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		  Date date = new Date();
		  System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
		  
		  String hour = new SimpleDateFormat("HH").format(date);
		  if (hour.equals("23") || hour.equals("17")) {
			  if (new SimpleDateFormat("mm").format(date).equals("59") || new SimpleDateFormat("mm").format(date).equals("12")) {
				  return ".." + (60 - Integer.parseInt(new SimpleDateFormat("ss").format(date))) +"..";
			  }
			  return dateFormat.format(date);
		  } else if (hour.equals("00")) {
			  return "HAPPY NEW YEAR!";
		  }
	  }

	  return t;
}
  
public boolean applyMeatToBuffers() {
    //System.out.println("in applymeattobuffers in pointdrawer (" + this + "), src is " + src);
    
    // image draw mode
    //out.getTexture().blend();
    //out.setBlendMode(REPLACE);
    //out.image(src.getTexture(),x,y,w,h);
    
    drawText();

    
    // pixel copy mode
      //arrayCopy(src.pixels, out.pixels);
      
    return true;
  }
  
  public void beginDraw() {
    //src.loadPixels();
    //out.loadPixels();
    //out.beginDraw();
    //if (out==null) setOutputCanvas(canvas_out);
    //if (src==null) setInputCanvas(canvas_in);
    //if (font==null) font = APP.loadFont("LCDSolid-128.vlw");
  }
  
  public void endDraw() {
    //out.updatePixels();
    //out.endDraw();
  }
  
}
