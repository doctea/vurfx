/*
  
  flob part sys example
  a typical particle system influenced by blobs positions
  each particle has its own force towards all blobs
  André Sier 2010
  
*/

import processing.opengl.*;
import processing.video.*;
import s373.flob.*;


Capture video;   
Flob flob;       
ArrayList blobs; 

PSys psys;

int tresh = 20;   // adjust treshold value here or keys t/T
int fade = 25;
int om = 1;
int videores=256;
String info="";
PFont font;
float fps = 60;
int videotex = 0; //case 0: videotex = videoimg;//case 1: videotex = videotexbin; 
//case 2: videotex = videotexmotion//case 3: videotex = videoteximgmotion;



void setup() {
  // osx quicktime bug 882 processing 1.0.1
  try { 
    quicktime.QTSession.open();
  } 
  catch (quicktime.QTException qte) { 
    qte.printStackTrace();
  }

  size(1024,768,OPENGL);
  frameRate(fps);
  rectMode(CENTER);
  // init video data and stream
  video = new Capture(this, videores, videores, (int)fps);  
  flob = new Flob(videores, videores, width, height);

  flob.setThresh(tresh).setSrcImage(videotex)
  .setBackground(video).setBlur(0).setOm(1).
  setFade(fade).setMirror(true,false);

  font = createFont("monaco",10);
  textFont(font);
  
  smooth();

  psys = new PSys(2500);
  //stroke(255,200);
  //stroke(random(255),200);
  strokeWeight(4);
}

float a = 0.1;

void draw() {

  if(video.available()) {
     video.read();
     blobs = flob.calc(flob.binarize(video));
  }

  image(flob.getSrcImage(), 0, 0, width, height);

  rectMode(CENTER);

  int numblobs = blobs.size();
  for(int i = 0; i < numblobs; i++) {
    ABlob ab = (ABlob)flob.getABlob(i); 
    psys.touch(ab);

    //box
    fill(0,0,255,100);
    //rect(ab.cx,ab.cy,ab.dimx,ab.dimy);
    //centroid

    rect(ab.cx,ab.cy, 5, 5);
    info = ""+ab.id+" "+ab.cx+" "+ab.cy;
    //text(info,ab.cx,ab.cy+20);
  }

  psys.go();
  psys.draw();

  //report presence graphically
//  fill(255,152,255);
  //stroke(sin(millis()), cos(millis()), tan(millis()));//,map(i,0,numblobs,0,128),map(i,0,numblobs,0,64),200);

  stroke(255);//color(255*sin(a*a),64,64));

  rectMode(CORNER);
  rect(5,5,flob.getPresencef()*width,10);
  String stats = ""+frameRate+"\nflob.numblobs: "+numblobs+"\nflob.thresh:"+tresh+
    " <t/T>"+"\nflob.fade:"+fade+"   <f/F>"+"\nflob.om:"+flob.getOm()+
    "\nflob.image:"+videotex+"\nflob.presence:"+flob.getPresencef();
  fill(0,255,0);
  text(stats,5,25);
  
  a += 0.01;
}


void keyPressed() {

  if (key=='S')
    video.settings();
  if (key=='i') {  
    videotex = (videotex+1)%4;
    flob.setImage(videotex);
  }
  if(key=='t') {
    flob.setTresh(tresh--);
  }
  if(key=='T') {
    flob.setTresh(tresh++);
  }   
  if(key=='f') {
    flob.setFade(fade--);
  }
  if(key=='F') {
    flob.setFade(fade++);
  }   
  if(key=='o') {
    om=(om +1) % 3;
    flob.setOm(om);
  }   
  if(key==' ') //space clear flob.background
    flob.setBackground(video);
}


