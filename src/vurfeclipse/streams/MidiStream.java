package vurfeclipse.streams;

import java.io.Serializable;
import java.util.*;

import themidibus.*;

public class MidiStream extends Stream implements Serializable { 
  
  boolean directMode = false;
  
  MidiBus myBus;
  
  float startValue = 0.0f;
  float currentValue = startValue;
  //float endValue = 0.0; //
  
  
  float bpm = 125.0f;
  
  int tolerance = 50;
  
  int startTime;
/*
  float stepDivisions[] = {
    16.0, 8.0, 4.0, 3.0, 2.0, 1.0,  //MAKE SURE TO CHANGE stepMarker value below if edit this line!!
    0.5, 0.25, 0.125, 0.0625, 0.03125, 0.015625
  };
  int stepMarker = 6; // the number of >=1 values in above array
  int lastDealtStepTime[] = new int[stepDivisions.length]; //stepCount];
  float stepLengths[] = new float[stepDivisions.length]; //stepCount];
  
  int stepCounter[] = new int[stepDivisions.length];
  */
  int lastDealtBeatTime;

  float beatLength;
  
  int generatedMessages = 0;
  
  int device = 1;

  MidiStream(String streamName) {
    this(streamName, false);

    //output = RWMidi.getOutputDevices()[0].createOutput();    
  }
  MidiStream(String streamName, boolean directMode) {
    this(streamName, 1, directMode);
  }
  public MidiStream(String streamName, int device, boolean directMode) {
    this.device = device;
    this.directMode = directMode;
    this.streamName = streamName;

    //input = RWMidi.getInputDevices()[this.device].createInput(this);
    try {
    	myBus = new MidiBus (this,this.device,0);
    } catch (Exception e) {
    	System.out.println("!!!!! got exception " + e + " while instantiating MidiBus object?!");
    }

  }  


  public void setDirectMode(boolean on) {
    this.directMode = on;
  }
  
  synchronized public void noteOn(int channel, int pit, int vel) {
    System.out.println("note on " + channel + " " + pit + " " + vel);
    addEvent("note", pit);    
    addEvent("note_"+channel, pit);
    addEvent("interval", pit%12);
    addEvent("interval_"+channel, pit%12);    
    if (directMode) {
      deliverEvents();
    }
  }
  
  /*void sysexReceived(rwmidi.SysexMessage msg) {
    println("sysex " + msg);
  } */ 
  
  public void processEventMeat(int time) {
    // detect if we're near a beat.
    // time - startTime > (length of beat) +/- ws
    // (length of beat) is 60/120 ? 0.5 second * 1000 = 500 millis
    /*if (time - lastDealtBeatTime > beatLength) {
      //our time has elapsed to generate a new number
      updateValue(time, time-lastDealtBeatTime);
      lastDealtBeatTime = time;
      addEvent("value", currentValue); //"BEAT AT " + time);
    }*/
  }
  
  /*public void updateValue(int time, int step) {
     //System.out.println("updateValue with startTime:" + startTime + ", time:" + time + " step:" + step + " --- currentValue is " + currentValue);
     currentValue += step+1;
  }*/

}
