package vurfeclipse.streams;

import java.io.Serializable;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.Group;
import controlP5.Textfield;
import themidibus.*;
import vurfeclipse.ui.ControlFrame;

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
  
  static int device = 0;

  public MidiStream() {
  	this("midi stream", device, true);
  }
  
  public MidiStream(String streamName) {
    this(streamName, false);
    //output = RWMidi.getOutputDevices()[0].createOutput();    
  }

  public MidiStream(String streamName, boolean directMode) {
    this(streamName, device, directMode);
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
    addEvent("note_"+pit, pit);
    addEvent("interval", pit%12);
    addEvent("interval_"+pit, pit%12);    
    
    addEvent("octave_"+((int)(pit/12)), pit%12);
    
    if (directMode) {
      deliverEvents();
    }
  }
  
  synchronized public void controllerChange(int channel, int number, int value) {
  	println("got MIDI CC on channel " + channel + ", number " + number + ": " + value);
  	if (value>0)
  		addEvent("cc_"+number, value);
  	
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
	@Override
	protected void preCall(ParameterCallback c) {
		// TODO Auto-generated method stub
		
	}
  
  /*public void updateValue(int time, int step) {
     //System.out.println("updateValue with startTime:" + startTime + ", time:" + time + " step:" + step + " --- currentValue is " + currentValue);
     currentValue += step+1;
  }*/

	@Override
	protected Group makeEmitterSelector(ControlFrame cf, final ParameterCallback callback, String name) {
		Group g = new Group(cf.control(), name + "_select_group").hideBar();
		
		int margin_x = 10;
		
		Textfield txtParam = cf.control().addTextfield(name).setLabel("addr").setText(callback.getStreamSource()).setAutoClear(false).setWidth(margin_x * 10);
				
		txtParam.addListenerFor(g.ACTION_BROADCAST, new CallbackListener() {
			@Override
			public void controlEvent(CallbackEvent theEvent) {
				callback.setStreamSource(theEvent.getController().getStringValue());
			}
		});
				
		g.add(txtParam.moveTo(g));
		return g;
	}
	

	@Override
	synchronized public void setupControls(final ControlFrame cf, Group g) {
		super.setupControls(cf, g);
		int margin_y = 20, gap_y = 5, margin_x = 80;

		int pos_y = 10;

		final MidiStream self = this;

		/*this.txtBPM = cf.control().addTextfield(this.toString() + "_tempo").setLabel("BPM").setText(""+this.bpm).setWidth(margin_x/2)
				.setPosition(margin_x * 3, pos_y)
				.moveTo(g)
				.addListenerFor(g.ACTION_BROADCAST, new CallbackListener() {
					@Override
					public void controlEvent(CallbackEvent theEvent) {
						self.setBPM(Float.parseFloat(((Textfield)theEvent.getController()).getText()));

						// and refresh gui
						cf.updateGuiStreamEditor();
					}
				});
		g.add(this.txtBPM);*/

		/*g.add(cf.control().addButton(this.toString() + "_resetstart").setLabel("Reset Start")
				.setPosition(margin_x * 5, pos_y)
				.moveTo(g)
				.addListenerFor(g.ACTION_BROADCAST, new CallbackListener() {
					@Override
					public void controlEvent(CallbackEvent theEvent) {
						synchronized (self) {
							self.startTime = APP.getApp().timeMillis;
							beat = 0;
							self.setBPM(self.bpm);
							//lastDealtStepTime = new int[stepDivisions.length];
							//Arrays.fill(lastDealtStepTime, startTime);

							// and refresh gui
							//cf.updateGuiStreamEditor();
						}
					}
				})
				);*/
	}
	
}
