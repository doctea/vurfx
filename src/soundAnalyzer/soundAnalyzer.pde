/**
https://raw.githubusercontent.com/kokashking/Re-SpaceTools/master/soundAnalyzer/soundAnalyzer.pde
 */

import oscP5.*;
import netP5.*;
import ddf.minim.*;
import controlP5.*;
import de.looksgood.ani.*;

ControlP5 cp5;

Textfield oscClipNr;
Textfield oscLayerNr;

Minim minim;
AudioInput in;

int levelCoeff = 150;
int levelThreashold = 30;
int timeBeatThreashold = 30;
int lastTimeBeat = 0;
boolean debug = false;
boolean sendToResolume = true;

int layerNr = 3;
int clipNr = 1;

int beatEllipseSize = 30;

OscP5 oscP5;
NetAddress myRemoteLocation;
NetAddress ResolumeLocation;

void setup() {
  size(500, 400);
  frameRate(25);
  /* start oscP5, listening for incoming messages at port 12000 */

  Ani.init(this);

  //GUI
  cp5 = new ControlP5(this);
  cp5.addSlider("levelThreashold")
    .setPosition(20, height/2 + 100)
      .setRange(0, 100)
        ;


  cp5.addSlider("timeBeatThreashold")
    .setPosition(20, height/2 + 130)
      .setRange(0, 1500)
        ;

  cp5.addSlider("levelCoeff")
    .setPosition(20, height/2 + 150)
      .setRange(0, 1500)
        ;

  cp5.addToggle("sendToResolume")
    .setPosition(300, height/2 + 100)
      .setSize(20, 20)
        ;


  oscLayerNr = cp5.addTextfield("oscLayerNr")
    .setPosition(300, height/2 + 150)
      .setSize(60, 20)
        .setAutoClear(false)
          .setLabel("Layer Nr")
            .setText(Integer.toString(layerNr))
              .setMoveable(true);

  oscClipNr = cp5.addTextfield("oscClipNr")
    .setPosition(370, height/2 + 150)
      .setSize(60, 20)
        .setAutoClear(false)
          .setLabel("Clip Nr")
            .setText(Integer.toString(clipNr))
              .setMoveable(true);


  //SOUND
  minim = new Minim(this);
  in = minim.getLineIn();


  //OSC
  oscP5 = new OscP5(this, 12000);

  /* myRemoteLocation is a NetAddress. a NetAddress takes 2 parameters,
   * an ip address and a port number. myRemoteLocation is used as parameter in
   * oscP5.send() when sending osc packets to another computer, device, 
   * application. usage see below. for testing purposes the listening port
   * and the port of the remote location address are the same, hence you will
   * send messages back to this sketch.
   */
  myRemoteLocation = new NetAddress("255.255.255.255" /*127.0.0.1"*/, 12001);
  ResolumeLocation = new NetAddress("255.255.255.255" /*127.0.0.1"*/, 12000);//7000);
}


void draw() {
  background(0);

  stroke(255);

  //float avc = 0;
  float[] buff_left = in.left.toArray(); //new float[in.left.size()];
  float[] buff_right = in.right.toArray(); //new float[in.left.size()];
  for (int i = 0; i < in.bufferSize () - 1; i++)
  {
    line( i, 50 + buff_left[i]*50, i+1, 50 + buff_left[i+1]*50 );
    line( i, 150 + buff_right[i]*50, i+1, 150 + buff_right[i+1]*50 );
    //avc += in.left.level();
  }
  //avc = avc/(float)in.bufferSize();

  if (debug) {
    println(" ");
  }


  int level = (int)(in.left.level() * levelCoeff);
  if (level > levelThreashold && millis() > lastTimeBeat + timeBeatThreashold) {


    if (debug) {
      println(millis());
      println(level);
      println("trigger");
    }
    beatEllipseSize = 30;
    animateBeatCircle();
    sendTrigger();
    if (sendToResolume) {
      sendOSCToResolume();
    }
    lastTimeBeat = millis();
  }

  //sendTimeSin();
  sendVolumes(in.left.level(), in.right.level());


  fill(255);
  ellipse(width/2, height/2, beatEllipseSize, beatEllipseSize);
}

public void controlEvent(ControlEvent theEvent) {

  if (theEvent.getController().getName().equals("oscClipNr")) {
    clipNr = Integer.parseInt(oscClipNr.getText());
  } else if (theEvent.getController().getName().equals("oscLayerNr")) {
    layerNr = Integer.parseInt(oscLayerNr.getText());
  }
}

void animateBeatCircle() {
  Ani.to(this, 0.2, "beatEllipseSize", 60, Ani.EXPO_OUT);
}

void mouseMoved() {
  /* in the following different ways of creating osc messages are shown by example */
  if (mousePressed) {
    OscMessage myMessage = new OscMessage("/mouseY");
    myMessage.add(mouseY); /* add an int to the osc message */
  
    OscMessage myMessage2 = new OscMessage("/mouseX");
    myMessage2.add(mouseX); /* add an int to the osc message */
  
    /* send the message */
    oscP5.send(myMessage, ResolumeLocation);
    oscP5.send(myMessage2, ResolumeLocation);
  }
}

void sendTrigger() {
  OscMessage myMessage = new OscMessage("/trigger");
  oscP5.send(myMessage, myRemoteLocation);
}

int count = 0;

void sendOSCToResolume() {
  count++;
  String target = "/layer"+ layerNr + "/clip" + clipNr + "/connect";
  println(count + " sending '1' to target " + target);
  OscMessage myMessage = new OscMessage(target);
  myMessage.add(1); //count); //1);
  oscP5.send(myMessage, ResolumeLocation);
}

void sendTimeSin() {
  count++;
  String target = "/timeSin"; //layer"+ layerNr + "/clip" + clipNr + "/connect";
  println(count + " sending '" + sin(millis()) + "' to target " + target);
  OscMessage myMessage = new OscMessage(target);
  myMessage.add(sin(millis())); //count); //1);
  oscP5.send(myMessage, ResolumeLocation);
}

void sendVolumes(float left, float right) {
  count++;
  String target = "/volumes"; //layer"+ layerNr + "/clip" + clipNr + "/connect";
  
  // just sending the straight value here and fiddling with it in processing gives the non-linear response expected?
  //float v = left;
  
  //float v = (float)(Math.log(2.0d)/Math.log((double)left)); //left; //(float)Math.pow((double)left*10, 3.0d)/100;
  println("left is " +left);
  
  /*float v = left * 10;
    v = pow(v, 4);
    v = abs(log(v));
    v = map(v, 0.0f, 6f, 0.0f, 1.0f);
    v = 6.0f - v;*/
    
  // convert to linear... sort of not what we need though really?
  float v = //20 *
    4.0f + 
    (float)Math.log10(left); 
  v = v / 4.0f;
  
  println(count + " sending '" + v + "' to target " + target);
  OscMessage myMessage = new OscMessage(target);
  myMessage.add(v); //count); //1);
  oscP5.send(myMessage, ResolumeLocation);
}

  /*v = (float)Math.pow((double)v,3);
    
  //v = //6.0 * 
    //(float)Math.log(left); //) * 2.0f;
  println("logged v is " + v);
  
  v = abs(v);
  
  //v /= 10.0;
  v = 6.0f - v;
  
  //v = abs (1.0/ (100.0 / v) - 1.0f);
  
  v = (float)Math.log((float)v);
  
  //v *= 10.0;*/

/* incoming osc message are forwarded to the oscEvent method. */
void oscEvent(OscMessage theOscMessage) {
  /* print the address pattern and the typetag of the received OscMessage */
  print("### received an osc message.");
  print(" addrpattern: "+theOscMessage.addrPattern());
  println(" typetag: "+theOscMessage.typetag());
}