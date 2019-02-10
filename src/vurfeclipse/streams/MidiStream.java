package vurfeclipse.streams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.MidiMessage;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.Group;
import controlP5.ScrollableList;
import controlP5.Textfield;
import controlP5.Textlabel;
import controlP5.Toggle;
import themidibus.*;
import vurfeclipse.APP;
import vurfeclipse.ui.ControlFrame;

public class MidiStream extends Stream implements Serializable, MidiListener {

	boolean directMode = false;

	MidiBus myBus;

	float startValue = 0.0f;
	float currentValue = startValue;
	// float endValue = 0.0; //

	float bpm = 125.0f;

	int tolerance = 50;

	int startTime;
	/*
	 * float stepDivisions[] = { 16.0, 8.0, 4.0, 3.0, 2.0, 1.0, //MAKE SURE TO
	 * CHANGE stepMarker value below if edit this line!! 0.5, 0.25, 0.125, 0.0625,
	 * 0.03125, 0.015625 }; int stepMarker = 6; // the number of >=1 values in above
	 * array int lastDealtStepTime[] = new int[stepDivisions.length]; //stepCount];
	 * float stepLengths[] = new float[stepDivisions.length]; //stepCount];
	 * 
	 * int stepCounter[] = new int[stepDivisions.length];
	 */
	int lastDealtBeatTime;

	float beatLength;

	int generatedMessages = 0;

	static int default_device = 0;

	int device = default_device;

	private ScrollableList lstMidiDevice;
	private Toggle tglShowMidi;
	private Textlabel txtLastNote;

	private boolean log_midi;

	public MidiStream() {
		this("midi stream", default_device, true);
	}

	public MidiStream(String streamName) {
		this(streamName, false);
		// output = RWMidi.getOutputDevices()[0].createOutput();
	}

	public MidiStream(String streamName, boolean directMode) {
		this(streamName, default_device, directMode);
	}

	public MidiStream(String streamName, int device, boolean directMode) {
		this.device = device;
		this.directMode = directMode;
		this.streamName = streamName;

		// input = RWMidi.getInputDevices()[this.device].createInput(this);
		this.listDevices();
		// newDevice(device);
	}

	public void setDevice(int device) {
		// if (device!=this.device || this.myBus==null) {
		// set up new device
		device = this.newDevice(device);
		// }
		this.device = device;
	}

	private int newDevice(int device) {
		try {
			//if (myBus != null && myBus.)
				//myBus.close();// .clearInputs();
			myBus = null;
			if (device >= MidiBus.availableInputs().length) {
				println("device number " + device + " is too high, opening device 0 instead");
				device = 0;
			}
			if (device < MidiBus.availableInputs().length) {
				println("Opening MIDI device " + device);
				myBus = new MidiBus(this, device, 0);
			} else {
				throw new IndexOutOfBoundsException("requested device number " + device
						+ " is too high for number of available midi inputs (" + MidiBus.availableInputs() + "!");
			}
		} catch (Exception e) {
			println("!!!!! got exception " + e + " while instantiating MidiBus object?!");
			e.printStackTrace();
		}
		return device;
	}

	public void listDevices() {
		for (String i : MidiBus.availableInputs()) {
			println("got input " + i);
		}
	}

	public void setDirectMode(boolean on) {
		this.directMode = on;
	}
	

	static String[] generate_emitter_names () {
		ArrayList<String> ems = new ArrayList<String>();
		
		ems.add("note");
		
		for (int pit = 0 ; pit < 128 ; pit++) {
			ems.add("note_" 	+ pit);
			ems.add("interval_" + pit % 12);
			ems.add("octave_" 	+ ((int) (pit / 12)));
			
			for (int ch = 0 ; ch < 16 ; ch++) {
				ems.add("tgl_note_"	+ ch + "_" + pit);
				ems.add("cc_" 		+ ch + "_" + pit);
				ems.add("tgl_cc_"	+ ch + "_" + pit);
			}
			
			ems.add("cc_" + pit);
			
		}
		
		Collections.sort(ems);
			
		return ems.toArray(new String[ems.size()]);		
	}
	
	static String[] emitter_names = generate_emitter_names();
	@Override
	public String[] getEmitterNames() {
		return emitter_names;		
	}


	synchronized public void noteOn(int channel, int pit, int vel) {
		if (isEnabled()) {
			if (log_midi)
				println("note\ton\tchannel " + channel + "\t pitch " + pit + " [%=" + pit % 12 + "]\t velocity " + vel);
			txtLastNote.setValue("note_" + channel + "_" + pit);

			addEvent("note", pit);
			addEvent("note_off", pit);
			addEvent("note_" + pit, pit);
			addEvent("note_off_" + pit, pit);
			addEvent("note_" + channel + "_" + pit, pit);
			addEvent("note_off_" + channel + "_" + pit, pit);
			addEvent("interval", pit % 12);
			addEvent("interval_off", pit % 12);
			addEvent("interval_" + pit, pit % 12);
			addEvent("interval_off" + pit, pit % 12);

			addEvent("octave_" + ((int) (pit / 12)), pit % 12);
			
			int tgl = getToggleValueFor("note_" + channel + "_" + pit);
			addEvent("tgl_note_"+ channel + "_" + pit, tgl);

			if (directMode) {
				deliverEvents();
			}
		}
	}
	
	synchronized public void noteOff(int channel, int pit, int vel) {
		if (isEnabled()) {
			if (log_midi)
				println("note\tof\tchannel " + channel + "\t pitch " + pit + " [%=" + pit % 12 + "]\t velocity " + vel);
			txtLastNote.setValue("note_" + channel + "_" + pit);

			addEvent("note_off", pit);
			addEvent("note_off_" + pit, pit);
			addEvent("note_off_" + channel + "_" + pit, pit);
			addEvent("interval_off_", pit % 12);
			addEvent("interval_off_" + pit, pit % 12);

			addEvent("octave_off_" + ((int) (pit / 12)), pit % 12);
			
			if (directMode) {
				deliverEvents();
			}
		}
	}

	synchronized public void controllerChange(int channel, int number, int value) {
		if (isEnabled()) {
			if (log_midi)
				println("CC\tchannel " + channel + "\t number " + number + "\tvalue " + value);
			txtLastNote.setValue("cc_" + channel + "_" + number);
			// if (value>0) {
			addEvent("cc_" + number, value);
			addEvent("cc_" + channel + "_" + number, value);
			
			int tgl = getToggleValueFor("cc_" + channel + "_" + number);
			addEvent("tgl_cc_"+ channel + "_" + number, tgl);
			
			// }

			if (directMode) {
				deliverEvents();
			}
		}
	}

	HashMap<String,Integer> toggle_count = new HashMap<String,Integer>();
	private int getToggleValueFor(String string) {
		if (toggle_count.containsKey(string)) {
			toggle_count.put(string, toggle_count.get(string)+1);
		} else {
			toggle_count.put(string, 1);
		}
		return toggle_count.get(string)%2;
	}

	/*synchronized public void midiMessage(MidiMessage message) { // You can also use midiMessage(MidiMessage message, long timestamp, String bus_name)
		  // Receive a MidiMessage
		  // MidiMessage is an abstract class, the actual passed object will be either javax.sound.midi.MetaMessage, javax.sound.midi.ShortMessage, javax.sound.midi.SysexMessage.
		  // Check it out here http://java.sun.com/j2se/1.5.0/docs/api/javax/sound/midi/package-summary.html
		  //println("");
			//if (!log_midi) return;
		  println("MidiMessage Data:");
		  println("--------");
		  println("Status Byte/MIDI Command:"+message.getStatus());
		  for (int i = 1;i < message.getMessage().length;i++) {
		    println("Param "+(i+1)+": "+(int)(message.getMessage()[i] & 0xFF));
		  }
		}*/
	
		
	int first = 0;
	int count = 0;
	
	synchronized public void rawMidi(byte[] data) { // You can also use rawMidi(byte[] data, String bus_name)
		if (!isEnabled()) return;
		
	  if (first==0) {
	    first = APP.getApp().millis();
	    //count = 1;
	  }
	  // Receive some raw data
	  // data[0] will be the status byte
	  // data[1] and data[2] will contain the parameter of the message (e.g. pitch and volume for noteOn noteOff)
	
	  //println("");
	  
	  if (((int)data[0] & 0xFF)==248)
		    count++;
		  else if (((int)data[0] & 0xFF)==250)
		    count = 0;
		  else {
			  if (log_midi) println("discarding Status Byte/MIDI Command:"+(int)(data[0] & 0xFF));
			return;
		  }
	  
	  //println("discarding Status Byte/MIDI Command:"+(int)(data[0] & 0xFF));
	  // N.B. In some cases (noteOn, noteOff, controllerChange, etc) the first half of the status byte is the command and the second half if the channel
	  // In these cases (data[0] & 0xF0) gives you the command and (data[0] & 0x0F) gives you the channel
	  //println(count + ": Raw Midi Data:");
	  //println("--------");

	  if (log_midi) for (int i = 1;i < data.length;i++) {
	    println("Param "+(i+1)+": "+(int)(data[i] & 0xFF));
	  }
	  //if (millis()-first>0)
	    //println("time is " + (millis()-first) + ": " + 2.5d * ((double)count/((millis()-first)/1000.0d)));
	  // speed = distance/time

	  //saw.amp(0.5f+(float)1.0f/count);
	        //saw.freq(500.0f+(float)1.0f/count);
	
	  // midi timing comes in 24 signals per quarter-note..?
	  if ((count % (32*768)) ==0) {
		addEvent("bar_8", count/(32*768));
	    if (log_midi) println("BAR_8!\t" + count/96);
	    //saw.freq(500.0f+(float)1.0f/count);
	    //saw.play();
	  } 
	  // midi timing comes in 24 signals per quarter-note..?
	  if ((count % (16*384)) ==0) {
		addEvent("bar_4", count/(16*384));
	    if (log_midi) println("BAR_4!\t" + count/96);
	    //saw.freq(500.0f+(float)1.0f/count);
	    //saw.play();
	  } 
	  if (count % 768==0) {
		addEvent("bar_2", count/192);
	    if (log_midi) println("BAR_2!\t" + count/96);
	    //saw.freq(500.0f+(float)1.0f/count);
	    //saw.play();
	  } 
	  if (count % 384==0) {
		addEvent("bar_1", count/96);
	    //if (log_midi) println("BAR_1!\t" + count/96);
	    //saw.freq(500.0f+(float)1.0f/count);
	    //saw.play();
	  } 
	  if (count % 48==0) {
		addEvent("beat_2nd", count/48);
		//if (log_midi) println("BEAT 2nd!\t" + count/48);
	    //saw.freq(500.0f+(float)1.0f/count);
	    //saw.play();
	  } 
	  if (count % 24==0) {
		addEvent("beat_1", count/24);
		//if (log_midi) println("BEAT!\t" + count/24);
	    //saw.freq(500.0f+(float)1.0f/count);
	    //saw.play();
	  } 
	  if (count % 12==0) {
	    //println("half BEAT!\t" + count/12);
		addEvent("beat_2", count/12);
	    //saw.amp(0.2f+(float)1.0f/count);
	    //saw.stop();
	  } 
	  if (count % 6==0) {
	    //println("quarter BEAT!\t" + count/6);
		addEvent("beat_4", count/6);
	    //saw.amp(0.2f+(float)1.0f/count);
	    //saw.stop();
	  }
	  if (count % 3==0) {
		  //  println("eight BEAT!\t" + count/3);
			addEvent("beat_8", count/3);
		    //saw.amp(0.2f+(float)1.0f/count);
		    //saw.stop();
		  }
	  if ((count*2) % 6==0) {
		    //println("sixteenth BEAT!\t" + (2*count)/6);
			addEvent("beat_16", (2*count)/6);
		    //saw.amp(0.2f+(float)1.0f/count);
		    //saw.stop();
		  }
	  if ((count*4) % 12==0) {
		    //println("sixteenth BEAT!\t" + (2*count)/6);
			addEvent("beat_32", (4*count)/12);
		    //saw.amp(0.2f+(float)1.0f/count);
		    //saw.stop();
		  }  
	    
	  if (count>=(24*64)) { 
	    count = 0;
	    first = 0;
	  }
	}
	
	
	/*
	 * void sysexReceived(rwmidi.SysexMessage msg) { println("sysex " + msg); }
	 */

	public void processEventMeat(int time) {
		// detect if we're near a beat.
		// time - startTime > (length of beat) +/- ws
		// (length of beat) is 60/120 ? 0.5 second * 1000 = 500 millis
		/*
		 * if (time - lastDealtBeatTime > beatLength) { //our time has elapsed to
		 * generate a new number updateValue(time, time-lastDealtBeatTime);
		 * lastDealtBeatTime = time; addEvent("value", currentValue); //"BEAT AT " +
		 * time); }
		 */
	}

	@Override
	protected void preCall(Callback c) {
		// TODO Auto-generated method stub

	}

	/*
	 * public void updateValue(int time, int step) {
	 * //System.out.println("updateValue with startTime:" + startTime + ", time:" +
	 * time + " step:" + step + " --- currentValue is " + currentValue);
	 * currentValue += step+1; }
	 */

	@Override
	protected Group makeEmitterSelector(ControlFrame cf, final ParameterCallback callback, String name) {
		Group g = new Group(cf.control(), name + "_select_group").hideBar();

		int margin_x = 10;

		Textfield txtParam = cf.control().addTextfield(name).setLabel("addr").setText(callback.getStreamSource())
				.setAutoClear(false).setWidth(margin_x * 10);

		if (txtLastNote != null && callback.getStreamSource().equals(""))
			txtParam.setValue(txtLastNote.getStringValue());

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
	public String toString() {
		return this.streamName + " [" + device + "]: " + this.getNameForDevice(device);
	}

	private String getNameForDevice(int device) {
		if (device < MidiBus.availableInputs().length) {
			return MidiBus.availableInputs()[device];
		} else {
			return "device number " + device + " out of bounds, no MIDI device";
		}
	}

	@Override
	synchronized public void setupControls(final ControlFrame cf, Group g) {
		super.setupControls(cf, g);
		int margin_y = 20, gap_y = 5, margin_x = 80;

		int pos_y = 10;

		final MidiStream self = this;

		this.lstMidiDevice = cf.control().addScrollableList(this.toString() + "_device").setLabel("MIDI device")
				.setWidth(margin_x * 4).setPosition(margin_x * 3, pos_y).addItems(MidiBus.availableInputs())
				.setHeight(10 * 4).setItemHeight(10).setBarHeight(10).moveTo(g).setValue((int) this.device)
				.addListenerFor(g.ACTION_BROADCAST, new CallbackListener() {
					@Override
					public void controlEvent(CallbackEvent theEvent) {
						// self.setDevice(getDeviceForName(((ScrollableList)theEvent.getController()).getStringValue()));
						int index = (int) ((ScrollableList) theEvent.getController()).getValue();

						self.setDevice(index);// (int) ((ScrollableList)theEvent.getController()).get.getValue());

						// and refresh gui
						// cf.updateGuiStreamEditor();
						lstMidiDevice.close();
					}
				}).onLeave(cf.close).onEnter(cf.toFront).close();
		g.add(this.lstMidiDevice);
		this.lstMidiDevice.bringToFront();

		this.tglShowMidi = cf.control().addToggle(this.toString() + "_showmidi").setLabel("Show in console")
				.setWidth(margin_x / 2).setPosition(margin_x * 8, pos_y).setValue(false).setState(false).moveTo(g)
				.addListenerFor(cf.control().ACTION_BROADCAST, new CallbackListener() {
					@Override
					public void controlEvent(CallbackEvent theEvent) {
						/*
						 * ev.getAction()==ControlP5.ACTION_RELEASED ||
						 * ev.getAction()==ControlP5.ACTION_RELEASEDOUTSIDE ||
						 */
						// ev.getAction()==ControlP5.ACTION_PRESS) {
						println("Setting log_midi state on " + this + " to "
								+ ((Toggle) theEvent.getController()).getState());
						log_midi = ((Toggle) theEvent.getController()).getState();
					}
				});
		g.add(tglShowMidi);

		this.txtLastNote = cf.control().addLabel((this.toString() + "_lastnote")).setWidth(margin_x * 10)
				.setPosition(margin_x * 10, pos_y).setValue("").moveTo(g);
		;
		g.add(txtLastNote);

		/*
		 * g.add(cf.control().addButton(this.toString() +
		 * "_resetstart").setLabel("Reset Start") .setPosition(margin_x * 5, pos_y)
		 * .moveTo(g) .addListenerFor(g.ACTION_BROADCAST, new CallbackListener() {
		 * 
		 * @Override public void controlEvent(CallbackEvent theEvent) { synchronized
		 * (self) { self.startTime = APP.getApp().timeMillis; beat = 0;
		 * self.setBPM(self.bpm); //lastDealtStepTime = new int[stepDivisions.length];
		 * //Arrays.fill(lastDealtStepTime, startTime);
		 * 
		 * // and refresh gui //cf.updateGuiStreamEditor(); } } }) );
		 */
	}

	protected int getDeviceForName(String stringValue) {
		int i = 0;
		for (String s : MidiBus.availableInputs()) {
			if (stringValue.equals(s)) {
				return i;
			}
			i++;
		}
		return 0;
	}

	@Override
	public HashMap<String, Object> collectParameters() {
		HashMap<String, Object> params = super.collectParameters();
		params.put("device_name", this.getNameForDevice(device));
		params.put("device", this.device);
		return params;
	}

	@Override
	public void readParameters(Map<String, Object> input) {
		super.readParameters(input);
		/*
		 * if (input.containsKey("device_name"))
		 * this.setDevice(this.getDeviceForName((String)input.get("device_name")));
		 */
		// else
		if (input.containsKey("device"))
			this.setDevice((int)Float.parseFloat(input.get("device").toString()));
		else
			this.setDevice(default_device);

		/*
		 * HashMap<String, HashMap<String,Object>> callbacks = (HashMap<String,
		 * HashMap<String,Object>>) input.get("callbacks"); for (Entry<String,
		 * HashMap<String, Object>> i : callbacks.entrySet()) {
		 * this.registerEventListener(paramName,
		 * ParameterCallback.createParameterCallback(i.getValue().get("class"))); }
		 */
		// callbacks = input.
	}
	
}
