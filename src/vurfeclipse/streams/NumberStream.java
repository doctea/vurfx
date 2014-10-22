package vurfeclipse.streams;

import java.io.Serializable;

public class NumberStream extends Stream implements Serializable { 
  
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

  public NumberStream(String streamName, float bpm, float startValue, int timeMillis) { 
    super(streamName);
    this.bpm = bpm;
    
    this.startValue = startValue;
    this.currentValue = startValue;
    
    startTime = timeMillis;//millis();
    //lastDealtBeatTime = startTime;

    beatLength = (60 / bpm) * 1000;
    lastDealtBeatTime = 0;
    
  }
  
  public void processEventMeat(int time) {
    // detect if we're near a beat.
    // time - startTime > (length of beat) +/- ws
    // (length of beat) is 60/120 ? 0.5 second * 1000 = 500 millis
    if (time - lastDealtBeatTime > beatLength) {
      //our time has elapsed to generate a new number
      updateValue(time, time-lastDealtBeatTime);
      lastDealtBeatTime = time;
      addEvent("value", currentValue); //"BEAT AT " + time);
    }
  }
  
  public void updateValue(int time, int step) {
     //System.out.println("updateValue with startTime:" + startTime + ", time:" + time + " step:" + step + " --- currentValue is " + currentValue);
     currentValue += step+1;
  }

}
