package candy;
import java.io.*;
import java.lang.*;
import java.util.*;
import processing.core.*;
import proxml.*;

//CANDY
//Processing SVG import for the common man
//author:  Flux
//email:   flux.blackcat@gmail.com
//  http://www.ghost-hack.com

//Using Christian Riekoff's handy proXML parser
//  http://www.texone.org/proxml
//  Hawt.

//Last 100% working for Processing 0115
//Please send bugs and annoyances to above email
//Refer to readme.txt that was part of this package for legal jargon

public class SVG{
    public String filename;
    protected static PApplet p;
    private static XMLInOut xmlio;
    
    private int svgWidth = 0;
    private int svgHeight = 0;
    private Vector draw = new Vector();
    
    private XMLElement svgData;
    public SVG(String filename, PApplet parent){
	//Hook into PApplet
	this.p = parent;
	parent.registerDispose(this);
	
	//Hacky way to remove any enable-background bugs written by Illustrator
	//Fuck Adobe
	//ATTENTION: This method will not work for applets.
	this.filename = filename;
	
	String[] stringBuffer = p.loadStrings(filename);
	if(!p.online){
	    for(int i=0;i<stringBuffer.length;i++){
		stringBuffer[i] = stringBuffer[i].replaceAll("enable-background.\"","");
		stringBuffer[i] = stringBuffer[i].replaceAll("new.[ ]{0,10}\"","");	    
	    }	
	    p.saveStrings("data/temp.xml",stringBuffer);
	}

	//Load into XML parser
	XMLElement svgDocument;
	try{
	    xmlio = new XMLInOut(p);
	    if(p.online)
		svgDocument = xmlio.loadElementFrom(filename);
	    else
		svgDocument = xmlio.loadElementFrom("temp.xml");
	}catch(InvalidDocumentException ide){
	    p.println("XML: File does not exist");
	    return;
	}

	//Get the xml child node we need
	XMLElement doc = svgDocument.getChild(0);
	XMLElement entSVG = doc.getChild(0);
	XMLElement svg = entSVG.getChild(1);
	//While we're doing that, save the width and height too
	svgWidth = svg.getIntAttribute("width");
	svgHeight = svg.getIntAttribute("height");
	XMLElement graphics = svg.getChild(1);
	this.svgData = svg;

	//Print SVG on construction
	//Use this for debugging
	//	svg.printElementTree(" .");

	//Store vector graphics into our draw-routine
	XMLElement elements[];
	elements = graphics.getChildren();	
	for(int i=0;i<elements.length;i++){
	    String name = elements[i].getElement();
	    XMLElement element = elements[i];
	    if(name.equals("line"))
		draw.add(new Line(element));
	    else
		if(name.equals("circle"))
		    draw.add(new Circle(element));
		else
		    if(name.equals("ellipse"))
			draw.add(new Ellipse(element));
		    else
			if(name.equals("rect"))
			    draw.add(new Rectangle(element));
			else
			    if(name.equals("polygon"))
				draw.add(new Polygon(element));
			    else
				if(name.equals("path"))
				    draw.add(new Path(element));
	}
    }

    //Taste the rainbow
    public void draw(){
	for(int i=0;i<draw.size();i++){
	    VectorObject vo = (VectorObject)draw.get(i);
	    vo.draw();
	}
    }

    //If you need to debug your SVG, use this method
    public void printSVG(){
	svgData.printElementTree(" .");
    }

    //Converts a string to a float
    private float valueOf(String s){
	return Float.valueOf(s).floatValue();
    }

    //The following will be called when app shuts down
    public void dispose(){
    }

    //Default vector graphics class from which all others will polymorph
    protected class VectorObject{
	int strokeColor;
	int fillColor;
	float strokeWeight = 1;
	public VectorObject(XMLElement properties){
	    getColors(properties);
	}
	protected void draw(){
	}
	//We'll need color information like stroke, fill, opacity, strokew-weight
	protected void getColors(XMLElement properties){
	    //Yes. This is hacky.
	    String strokeColor = "none";
	    String fillColor = "none";
	    String opacity = "FF";
	    String strokeWeight = "1";
	    if(properties.hasAttribute("opacity")){
		int o = (int) (Float.valueOf(properties.getAttribute("opacity")).floatValue() * 255);
		opacity = p.hex(o);
		opacity = opacity.substring(6,8);
	    }	    
	    //A value of -1 = noStroke() or noFill()
	    if(properties.hasAttribute("stroke")){
		strokeColor = properties.getAttribute("stroke");
		if(!strokeColor.equals("none")){
		    strokeColor = strokeColor.substring(1,7);
		    strokeColor = opacity + strokeColor;
		    this.strokeColor = p.unhex(strokeColor);
		}
		else
		    this.strokeColor = -1;
	    }
	    else
		this.strokeColor = -1;
	    if(properties.hasAttribute("fill")){
		fillColor = properties.getAttribute("fill");	
		if(!fillColor.equals("none")){
		    fillColor = fillColor.substring(1,7);
		    fillColor = opacity + fillColor;
		    this.fillColor = p.unhex(fillColor);
		}
		else
		    this.fillColor = -1;
	    }
	    else
		this.fillColor = -1;
	    if(properties.hasAttribute("stroke-width")){
		strokeWeight = properties.getAttribute("stroke-width");
		this.strokeWeight = valueOf(strokeWeight);
	    }
	}
	protected void setColors(){
	    p.colorMode(p.RGB,255,255,255,255);
	    if(strokeColor!=-1)
		p.stroke(strokeColor);
	    else
		p.noStroke();
	    if(fillColor!=-1)
		p.fill(fillColor);
	    else
		p.noFill();
	    p.strokeWeight(strokeWeight);
	}
    }
    
    private class Line extends VectorObject{
	float x1;
	float y1;
	float x2;
	float y2;
	public Line(XMLElement properties){
	    super(properties);
	    this.x1 = properties.getFloatAttribute("x1");
	    this.y1 = properties.getFloatAttribute("y1");
	    this.x2 = properties.getFloatAttribute("x2");
	    this.y2 = properties.getFloatAttribute("y2");	    
	}
	protected void draw(){
	    setColors();
	    p.line(x1,y1,x2,y2);
	}
    }
    
    private class Circle extends VectorObject{
	float x;
	float y;
	float radius;
	public Circle(XMLElement properties){
	    super(properties);
	    this.x = properties.getFloatAttribute("cx");
	    this.y = properties.getFloatAttribute("cy");
	    this.radius = properties.getFloatAttribute("r") * 2;
	}
	protected void draw(){
	    setColors();
	    p.ellipseMode(p.CENTER);
	    p.ellipse(x,y,radius,radius);
	}
    }
    
    private class Ellipse extends VectorObject{
	float x;
	float y;
	float rx;
	float ry;
	boolean hasTransform = false;
	float transformation[] = null;
	//Should we keep these here even when we don't have transforms?
	float rotation = 0;
	float translateX = 0;
	float translateY = 0;
	public Ellipse(XMLElement properties){	    
	    super(properties);
	    this.x = properties.getFloatAttribute("cx");
	    this.y = properties.getFloatAttribute("cy");
	    this.rx = properties.getFloatAttribute("rx") * 2;
	    this.ry = properties.getFloatAttribute("ry") * 2;

	    String transform = "";	    
	    if(properties.hasAttribute("transform")){
		this.hasTransform = true;
		transform = properties.getAttribute("transform");
		transform = transform.substring(7,transform.length()-2);
		String tf[] = p.split(transform);
		this.transformation = new float[tf.length];
		for(int i=0;i<transformation.length;i++)
		    this.transformation[i] = Float.valueOf(tf[i]).floatValue();		
	    }
	    //Hacky code to get rotation working
	    //Done through the powers of trial and error
	    if(this.hasTransform){
		float t[] = this.transformation;
		if(t[0]<0&&t[1]<0&&t[2]>0&&t[3]<0)
		    this.rotation = -p.acos(this.transformation[3]);
		if(t[0]>0&&t[1]<0&&t[2]>0&&t[3]>0)
		    this.rotation = p.asin(this.transformation[1]);
		if(t[0]<0&&t[1]>0&&t[2]<0&&t[3]<0)
		    this.rotation = p.acos(this.transformation[0]);
		if(t[0]>0&&t[1]>0&&t[2]<0&&t[3]>0)
		    this.rotation = p.acos(this.transformation[0]);
		this.translateX = this.transformation[4];
		this.translateY = this.transformation[5];
	    }
	}
	protected void draw(){
	    setColors();
	    p.ellipseMode(p.CENTER);
	    if(hasTransform){
		p.pushMatrix();
		p.translate(translateX,translateY);
		p.rotate(rotation);
	    }
	    p.ellipse(x,y,rx,ry);
	    if(hasTransform){
		p.popMatrix();
		
	    }
	}	
    }
    
    private class Rectangle extends VectorObject{
	float x;
	float y;
	float w;
	float h;
	boolean hasTransform = false;
	float transformation[] = null;
	float rotation = 0;
	float translateX = 0;
	float translateY = 0;
	public Rectangle(XMLElement properties){
	    super(properties);
	    this.x = properties.getFloatAttribute("x");
	    this.y = properties.getFloatAttribute("y");
	    this.w = properties.getFloatAttribute("width");
	    this.h = properties.getFloatAttribute("height");

	    String transform = "";
	    if(properties.hasAttribute("transform")){
		this.hasTransform = true;
		transform = properties.getAttribute("transform");
		transform = transform.substring(7,transform.length()-2);
		String tf[] = p.split(transform);
		transformation = new float[tf.length];
		for(int i=0;i<transformation.length;i++)
		    transformation[i] = Float.valueOf(tf[i]).floatValue();		
	    }

	    //Hacky code to get rotation working
	    //Done through the powers of trial and error
	    //This might be wrong....
	    if(this.hasTransform){
		float t[] = this.transformation;
		if(t[0]<0&&t[1]<0&&t[2]>0&&t[3]<0)
		    this.rotation = -p.acos(this.transformation[3]);
		if(t[0]>0&&t[1]<0&&t[2]>0&&t[3]>0)
		    this.rotation = p.asin(this.transformation[1]);
		if(t[0]<0&&t[1]>0&&t[2]<0&&t[3]<0)
		    this.rotation = p.acos(this.transformation[0]);
		if(t[0]>0&&t[1]>0&&t[2]<0&&t[3]>0)
		    this.rotation = p.acos(this.transformation[0]);
		//rotation = p.asin(transformation[1]);
		translateX = this.transformation[4];
		translateY = this.transformation[5];
	    }
	}
	protected void draw(){
	    p.rectMode(p.CORNER);
	    setColors();			      
	    if(hasTransform){
		p.pushMatrix();
		p.translate(translateX,translateY);
		p.rotate(rotation);
	    }
	    p.rect(x,y,w,h);
	    if(hasTransform){
		p.popMatrix();
	    }	
	}
    }

    private class Polygon extends VectorObject{
	float points[][] = null;
	public Polygon(XMLElement properties){
	    super(properties);
	    String pointsBuffer[] = null;
	    if(properties.hasAttribute("points"))
		pointsBuffer = p.split(properties.getAttribute("points"),' ');      
	    points = new float[pointsBuffer.length-1][2];	    
	    for(int i=0;i<points.length;i++){
		String pb[] = p.split(pointsBuffer[i],',');
		points[i][0] = Float.valueOf(pb[0]).floatValue();
		points[i][1] = Float.valueOf(pb[1]).floatValue();
	    }
	}
	protected void draw(){
	    setColors();
	    if(points!=null)
		if(points.length>0){
		    p.beginShape(p.POLYGON);
		    for(int i=0;i<points.length;i++){
			p.vertex(points[i][0],points[i][1]);
		    }
		    p.endShape();
		}
	}
    }
    
    //Hang on! This is going to be meaty.    
    //Big and nasty constructor coming up....
    private class Path extends VectorObject{
	Vector points = new Vector();
	boolean closed = false;
	public Path(XMLElement properties){
	    super(properties);
	    String pathDataBuffer = "";
	    if(!properties.hasAttribute("d"))
		return;
	    pathDataBuffer = properties.getAttribute("d");
	    Vector pathChars = new Vector();
	    boolean lastSeperate = false;
	    for(int i=0;i<pathDataBuffer.length();i++){
		char c = pathDataBuffer.charAt(i);
		boolean seperate = false;
		if(c == 'M' || c == 'm' ||
		   c == 'L' || c == 'l' ||
		   c == 'H' || c == 'h' ||
		   c == 'V' || c == 'v' ||
		   c == 'C' || c == 'c' ||
		   c == 'S' || c == 's' ||
		   c == ',' ||
		   c == 'Z' || c == 'z'){	
		    seperate = true;	
		    if(i!=0)
			pathChars.add("|");		
		}		
		if(c == 'Z' || c == 'z')
		    seperate = false;
		if(c == '-' && !lastSeperate){
		    pathChars.add("|");
		}
		if(c!=',')
		    pathChars.add(""+pathDataBuffer.charAt(i));	    
		if(seperate && 
		   c!=',' && c!='-')
		    pathChars.add("|");
		lastSeperate = seperate;
		
	    }
	    pathDataBuffer = "";      
	    for(int i=0;i<pathChars.size();i++){
		pathDataBuffer = pathDataBuffer + (String) pathChars.get(i);
	    }
	    String pathDataKeys[] = p.split(pathDataBuffer,'|');
	    float cp[] = {0,0};
	    for(int i=0;i<pathDataKeys.length;i++){
		char c = pathDataKeys[i].charAt(0);
		switch(c){
		    //M - move to (absolute)
		case 'M':
		    {
			cp[0] = valueOf(pathDataKeys[i+1]);
			cp[1] = valueOf(pathDataKeys[i+2]);
			float s[] = {cp[0],cp[1]};
			i+=2;
			points.add(s);
		    }
		    break;
		    //m - move to (relative)
		case 'm':
		    {
			cp[0] = cp[0] + valueOf(pathDataKeys[i+1]);
			cp[1] = cp[1] + valueOf(pathDataKeys[i+2]);
			float s[] = {cp[0],cp[1]};
			i+=2;
			points.add(s);
		    }
		    //C - curve to (absolute)
		case 'C':
		    {
			float curvePA[]={
			    valueOf(pathDataKeys[i+1]),
			    valueOf(pathDataKeys[i+2]) 		    			    
			};
			float curvePB[]={
			    valueOf(pathDataKeys[i+3]),
			    valueOf(pathDataKeys[i+4])
			};
			float endP[]={
			    valueOf(pathDataKeys[i+5]),
			    valueOf(pathDataKeys[i+6])
			};
			cp[0] = endP[0];
			cp[1] = endP[1];
			i+=6;
			points.add(curvePA);
			points.add(curvePB);
			points.add(endP);		    
		    }
		    break;
		    //c - curve to (relative)
		case 'c': 
		    {
			float curvePA[]={
			    cp[0] + valueOf(pathDataKeys[i+1]),
			    cp[1] + valueOf(pathDataKeys[i+2]) 		    			    
			};
			float curvePB[]={
			    cp[0] + valueOf(pathDataKeys[i+3]),
			    cp[1] + valueOf(pathDataKeys[i+4])
			};
			float endP[]={
			    cp[0] + valueOf(pathDataKeys[i+5]),
			    cp[1] + valueOf(pathDataKeys[i+6])
			};
			cp[0] = endP[0];
			cp[1] = endP[1];
			i+=6;
			points.add(curvePA);
			points.add(curvePB);
			points.add(endP);
		    }		
		    break;
		    //S - curve to shorthand (absolute)
		case 'S': 
		    {
			float lastPoint[] = (float[])points.get(points.size()-1);
			float lastLastPoint[] = (float[])points.get(points.size()-2);
			float s[]={
			    lastPoint[0],
			    lastPoint[1]
			};
			float curvePA[]={
			    cp[0] + (lastPoint[0] - lastLastPoint[0]),
			    cp[1] + (lastPoint[1] - lastLastPoint[1])
			};
			float curvePB[] = {
			    valueOf(pathDataKeys[i+1]),
			    valueOf(pathDataKeys[i+2])
			};	
			float e[]={
			    valueOf(pathDataKeys[i+3]),
			    valueOf(pathDataKeys[i+4])
			};
			cp[0] = e[0];
			cp[1] = e[1];
			points.add(curvePA);
			points.add(curvePB);
			points.add(e);
			i+=4;
		    }
		    break;
		    //s - curve to shorthand (relative)
		case 's': 
		    {
			float lastPoint[] = (float[])points.get(points.size()-1);
			float lastLastPoint[] = (float[])points.get(points.size()-2);
			float s[]={
			    lastPoint[0],
			    lastPoint[1]
			};
			float curvePA[]={
			    cp[0] + (lastPoint[0] - lastLastPoint[0]),
			    cp[1] + (lastPoint[1] - lastLastPoint[1])
			};
			float curvePB[] = {
			    cp[0] + valueOf(pathDataKeys[i+1]),
			    cp[1] + valueOf(pathDataKeys[i+2])
			};	
			float e[]={
			    cp[0] + valueOf(pathDataKeys[i+3]),
			    cp[1] + valueOf(pathDataKeys[i+4])
			};
			cp[0] = e[0];
			cp[1] = e[1];
			points.add(curvePA);
			points.add(curvePB);
			points.add(e);
			i+=4;
		    }
		    break;
		case 'Z':
		    closed = true;
		    break;
		case 'z':
		    closed = true;
		    break;
		}	    
	    }
	}
	
	protected void draw(){
	    setColors();
	    if(closed)
		p.beginShape(p.POLYGON);
	    else
		p.beginShape(p.LINE_STRIP);
	    float start[] = (float[])points.get(0);
	    p.vertex(start[0],start[1]);
	    for(int i=1;i<points.size();i+=3){
		float a[] = (float[])points.get(i);
		float b[] = (float[])points.get(i+1);
		float e[] = (float[])points.get(i+2);	    	    
		p.bezierVertex(a[0],a[1],b[0],b[1],e[0],e[1]);	   
	    }
	    p.endShape();
	}


    }

    //--------------------end of class
}
