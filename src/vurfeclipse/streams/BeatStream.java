package vurfeclipse.streams;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.Group;
import controlP5.Textfield;
import processing.core.PApplet;
import vurfeclipse.APP;
import vurfeclipse.sequencers.SequenceSequencer;
import vurfeclipse.ui.ControlFrame;

public class BeatStream extends Stream implements Serializable { 
	private float bpm = 125.0f;

	private int tolerance = 50;

	int startTime;

	int beat = 0;

	private float stepDivisions[] = {
			128.0f, 64.0f,32.0f, 16.0f, 8.0f, 4.0f, 3.0f, 2.0f, 1.0f,  //MAKE SURE TO CHANGE stepMarker value below if edit this line!!
			0.5f, 0.25f, 0.125f, 0.0625f, 0.03125f, 0.015625f
	};
	private int stepMarker = 9; // the number of >=1 values in above array
	int lastDealtStepTime[] = new int[stepDivisions.length]; //stepCount];
	float stepLengths[] = new float[stepDivisions.length]; //stepCount];

	int stepCounter[] = new int[stepDivisions.length];

	//int lastDealtBeatTime;

	float beatLength;

	int generatedMessages = 0;

	BeatStream(String streamName, float bpm, int timeMillis) { 
		super(streamName);
		this.bpm = bpm;
		this.setupStepLengths(bpm); 
		this.setBPM(bpm);

		//startTime = timeMillis;//millis();
		//lastDealtBeatTime = startTime;

		//setupStepLengths(bpm);
	

		/*
    quarterBeatLength = beatLength / 4;
    twoBeatLength = beatLength*2;*/

	}
	public BeatStream(String streamName, double bpm, int millis) {
		// TODO Auto-generated constructor stub
		this(streamName,(float)bpm,millis);
	}
	public BeatStream() {
		this.startTime = APP.getApp().millis();		
	}


	int toggleMode = 1;
	public void toggleBPM() {
		toggleMode++;
		if (toggleMode>8) 
			toggleMode = 1;

		float speed = PApplet.map(PApplet.abs(4-toggleMode),1.0f,8.0f,0.5f,2.0f)+0.25f;
		System.out.println("toggling steps for toggleMode " + toggleMode + " speed is " + speed);    
		setSpeed(speed);
	}

	synchronized public void setBPM(float bpm) {
		if (beat==0) {
			this.bpm = bpm;
			this.setupStepLengths(bpm);
		}

		if (beat!=0 && bpm==this.bpm) {
			println("new bpm is same as old, only adjusting time?");
		} else {
			//println("setBPM: current beat position is "  + beat);
			//println("think elapsed beats from current set is " + stepCounter[14] +" ?");
			for (int i = 0/*stepCounter.length-2*/ ; i < stepCounter.length ; i++) {
				println (i + ": " + stepCounter[i] + " | " + stepDivisions[i] + " = " + (Math.ceil(stepCounter[i] / stepDivisions[i])) + "!");
			}
			float elapsedBeats = (float) Math.ceil((float)stepCounter[8] / stepDivisions[8]); //*stepDivisions[14]*2;
			if (elapsedBeats>2) {
				println("song position: " + beat + ", new bpm " + bpm + ", elapsed beats " + elapsedBeats);

				beat += /*stepDivisions[14] * */elapsedBeats; // set the beat offset to the current beat, so the song position progresses
				//println("setBPM got beat " + beat);

				this.bpm = bpm;
				this.setupStepLengths(bpm); 
				stepCounter = new int[stepDivisions.length];

				this.addEvent("set_bpm", this.bpm);
				
				processEventMeat(0);
			}
		}
		startTime = APP.getApp().millis();
		//Arrays.fill(lastDealtStepTime, (int) (startTime - stepLengths[8]));
		if (beat!=0) for (int i = 0 ; i < lastDealtStepTime.length ; i++) {
			lastDealtStepTime[i] = (int) (startTime - stepLengths[i]);
		}
	}    

	float speed = 1.0f;
	public void setSpeed(float speed) {
		this.speed = speed;
		//stepCounter = new int[stepDivisions.length];
		//lastDealtStepTime = new int[stepDivisions.length];    
	}


	void setupStepLengths(float bpm) {
		beatLength = (60 / bpm) * 1000;    
		for (int n = 0 ; n < stepDivisions.length ; n++) {
			stepLengths[n] =  (beatLength / stepDivisions[n]);
			lastDealtStepTime[n] = 0;//startTime; //0;
		}
		stepLengths[stepMarker] = beatLength * 2;
		stepLengths[stepMarker+1] = beatLength * 4;
		stepLengths[stepMarker+2] = beatLength * 8;
		stepLengths[stepMarker+3] = beatLength * 16;
		stepLengths[stepMarker+4] = beatLength * 32;
		stepLengths[stepMarker+5] = beatLength * 64;
	}    

	int skippedCount = 0;

	private Textfield txtBPM;

	synchronized public void processEventMeat(int time) {
		// detect if we're near a beat.
		// time - startTime > (length of beat) +/- ws
		// (length of beat) is 60/120 ? 0.5 second * 1000 = 500 millis
		/*if (time - lastDealtBeatTime > beatLength) {
      lastDealtBeatTime = time;
      addEvent("beat", "BEAT AT " + time);
    }    */
		if (debug) {
			System.out.println("BeatStream speed is " + speed);
			System.out.println("BeatStream time is " + time);
		}

		//time = time - startTime;
		//println("time is " + time);
		// detect if any of the step divisions should trigger
		//System.out.println("FRAME--");
		for (int n = 0 ; n < stepDivisions.length ; n++) {
			//System.out.println("n is " + n + ", stepLength is " + stepLengths[n]);
			double gap = time - lastDealtStepTime[n];	// get the amount of time since this interval was last triggered
			String eventName = //stepDivisions[n]<1 ? 
					n > stepMarker ?
							"bar_" + (n-stepMarker)
							:
								("beat_" + (int)stepDivisions[n]);

							//if (eventName.equals("beat_1")) println("gap is " + gap + " (for stepLength " + stepLengths[n] + ")");

							if ((double)gap*(double)speed > (double)stepLengths[n]/(double)speed) {

								/*if (skippedCount++>toggleMode) {  //////////////////////// really fucking poisonous
          skippedCount = 1;
        } else {
          return;                //////////// early return !!!!!!!!!!!
        }*/


								//stepCounter[n]++;
								//float stepCount = (float) ((float)gap / (float)stepLengths[n]);
								float stepCount = (float) (gap / stepLengths[n])/2;
								//if (n < stepMarker) {
									stepCounter[n] += (int)Math.ceil(stepCount);
								//} else {
									//stepCounter[n] += (int)stepCount;
								//}

								//println("Triggering " + eventName);
								//if (eventName.equals("beat_1"))
								//println("at " + time + " (startTime is " + startTime + ") with gap " + gap + ", triggering for " + eventName + " [" + n + "], stepDivisions[n] beat_" + ((int)stepDivisions[n]) + " with stepcounter " + stepCounter[n]);
								//lastDealtStepTime[n] = time;
								//lastDealtStepTime[n] = (int) ((int) (time/(stepCount * stepLengths[n])) * (stepCount * stepLengths[n])); 
								//lastDealtStepTime[n] = (int)(stepCounter[n]*stepLengths[n]);
								lastDealtStepTime[n] = time;
								//System.out.println("beat_" + stepDivisions[n]);
								//if (gap < tolerance) { // dont trigger too old events if htey're outside tolerance
								//if (usedEvents.contains(eventName)) {
								clearEvents(eventName);  // old beats aren't interesting
								//addEvent(eventName, stepCounter[n] + "th " + eventName + " at " + time);
								//addEvent(eventName, /*beat + */(stepCounter[n]/2));// + "th " + eventName + " at " + time);
								addEvent(eventName, (beat*stepDivisions[n]) + (stepCounter[n]));// + "th " + eventName + " at " + time);
								//addEvent((stepDivisions[n]<1?"bar":"beat"), eventName + "@" + time);
								addEvent((n>stepMarker?"bar":"beat"), stepCounter[n] + "th " + eventName + "@" + time);

								generatedMessages++;
								//}
								//}
							}
		}
		//System.out.println("FRAME END--");

		//int timeTaken = (millis() - startTime) / 1000;
		//System.out.println(generatedMessages + " messages in " + timeTaken + "("+ generatedMessages/timeTaken + "mps)");

	} 



	@Override 
	public HashMap<String,Object> collectParameters() {
		HashMap<String,Object> params = super.collectParameters();
		params.put("tempo", this.bpm);
		return params;
	}

	@Override
	public void readParameters(HashMap<String, Object> input) {
		super.readParameters(input);
		this.setBPM((Float) input.get("tempo"));

		/*HashMap<String, HashMap<String,Object>> callbacks = (HashMap<String, HashMap<String,Object>>) input.get("callbacks");
		for (Entry<String, HashMap<String, Object>> i : callbacks.entrySet()) {
			this.registerEventListener(paramName, ParameterCallback.createParameterCallback(i.getValue().get("class")));
		}*/
		//callbacks = input.
	}

	@Override
	public String[] getEmitterNames() {	
		return new String[] {
				"beat_1","beat_2", /*"beat_3",*/ "beat_4", /*"beat_5", "beat_6", "beat_7",*/ "beat_8", /*"beat_9", "beat_10", "beat_11", "beat_12", "beat_13", "beat_14", "beat_15",*/ "beat_16",
				"beat_32", "beat_64",
				"bar_1","bar_2", "bar_3", "bar_4", "bar_5", "bar_6", "bar_7", "bar_8", "bar_9", "bar_10", "bar_11", "bar_12", "bar_13", "bar_14", "bar_15", "bar_16",

				"set_bpm"
		};
	}

	@Override
	public void setupControls(final ControlFrame cf, Group g) {
		synchronized (cf) {
			super.setupControls(cf, g);
			int margin_y = 20, gap_y = 5, margin_x = 80;
	
			int pos_y = 10;
	
			final BeatStream self = this;
	
			this.txtBPM = cf.control().addTextfield(this.toString() + "_tempo").setLabel("BPM").setText(""+this.bpm).setWidth(margin_x/2)
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
			g.add(this.txtBPM);
	
			g.add(cf.control().addButton(this.toString() + "_resetstart").setLabel("Reset Start")
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
					);
		}
	}

	int bpm_numsamples = 16;
	int[] bpm_counter = new int[bpm_numsamples];
	int bpm_last = APP.getApp().timeMillis;
	int bpm_index = 0;

	DecimalFormat df = new DecimalFormat("#.##");

	@Override
	protected void preCall(ParameterCallback c) {
		if (c instanceof FormulaCallback) {
			((FormulaCallback)c).e.setVariable("length", new BigDecimal(((SequenceSequencer)APP.getApp().pr.getSequencer()).getActiveSequence().getLengthMillis()));
		}
	}
	
	@Override
	public boolean sendKeyPressed(char key) {
		if (key=='\n') {
			println("BPM COUNTER!");
			int gap = APP.getApp().timeMillis - bpm_last;	// time since last press
			if (gap>3000) {		// if over 3 seconds, restart
				println("BPM COUNTER resetting as last press was over 3 seconds ago - discarding press");
				bpm_index = 0;	
				bpm_counter = new int[bpm_numsamples];
				bpm_last = APP.getApp().timeMillis; // set start time
				return true;
			}
			bpm_last = APP.getApp().timeMillis; // set start time

			/*if (bpm_index>=bpm_numsamples || bpm_index == -1) {
				bpm_index = 0;
				bpm_counter = new int[bpm_numsamples];
			}*/
			/*if (
					//bpm_index>=bpm_numsamples || 
				APP.getApp().timeMillis > bpm_last + 3000) {

				println ("3 seconds since last press or looping, resetting");
				// reset array & counter
				//bpm_counter = new int[bpm_numsamples];

				//bpm_counter[bpm_index] = 0;
				//bpm_index++;
				if (bpm_index>=bpm_numsamples) bpm_index = 1;
			}*/
			if (bpm_index>=bpm_numsamples) bpm_index = 0;

			//bpm_counter[bpm_index] = bpm_start - APP.getApp().timeMillis;
			//int distance = (bpm_last - APP.getApp().timeMillis) - bpm_counter[bpm_index-1];
			bpm_counter[bpm_index] = gap;
			println ("collected sample " + gap + " at " + bpm_index);
			bpm_index++;

			//if (bpm_index>bpm_numsamples/2) {

			println ("got " + bpm_numsamples + ", calculate bpm based on averages!");
			//float diff = bpm_counter[bpm_numsamples-1] - bpm_counter[0]; // get distance between first and last beat, in millis
			// TODO: should actually average the difference between each beat
			/*int[] averages = new int[bpm_numsamples];
				for (int i = 1 ; i < bpm_index-1 ; i++) {
					averages[i] = bpm_counter[i] - bpm_counter[i-1];
				}*/
			int total = 0;
			int samples = 0;
			for (int i = 0 ; i < bpm_counter.length ; i++) {
				if (bpm_counter[i]>0) {
					samples ++;
					//println ("av " + i + " = " + bpm_counter[i]);
					total += bpm_counter[i];
				}
			}
			if (samples>2) {
				float average = total / samples;
				//println ("got average " + average);

				//println ("got diff " + diff);

				float diff = average/1000.0f; //bpm_numsamples;
				//println ("got beat diff " + diff);

				// diff 1.500; want to turn that into 120bpm...
				// 60 * diff

				// turn length of 4 beats in to beats per minute..
				// s = d / t
				// divide num_samples by diff ?
				//float bpm = 60.0f * bpm_numsamples * diff;
				float bpm = bpm_numsamples / diff;
				//println ("got pre-bpm " + bpm);
				bpm *= 60.0f / (float)bpm_numsamples;
				//bpm *= 60.0f;
				bpm = Float.valueOf(df.format(bpm));
				println ("got new bpm " + bpm);
				//bpm_index = 0;
				this.setBPM(bpm);
				this.updateGuiBPM(bpm);
			}

		} else {
			//println("collected sample " + (bpm_index-1) + " at "  + bpm_counter[bpm_index-1]);
		}
		//}
		return false;
	}
	private void updateGuiBPM(final float bpm) {
		//APP.getApp().getCF().updateGuiStreamEditor();
		APP.getApp().getCF().queueUpdate(new Runnable() {
			@Override
			public void run() {
				txtBPM.setText("" + bpm);								
			}
		});
	}

}
