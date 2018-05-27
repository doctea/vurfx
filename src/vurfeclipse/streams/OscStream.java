package vurfeclipse.streams;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.Group;
import controlP5.Textfield;
import vurfeclipse.APP;
import vurfeclipse.ui.ControlFrame;


import oscP5.*;

public class OscStream extends Stream implements OscEventListener {

	OscP5 oscP5;
	
	public OscStream() {
		if (oscP5==null) {
		  oscP5 = new OscP5(this,12000);
		  //oscP5.addListener(this);
		}
	}

	public OscStream(String streamName) {
		super(streamName);
	    oscP5 = new OscP5(this,12000);
		//oscP5.addListener(this);
	}
	
	@Override
	public synchronized void registerEventListener(String paramName, ParameterCallback callback) {
		// TODO Auto-generated method stub
		super.registerEventListener(paramName, callback);
		//if(callback instanceof ParameterCallback) ((ParameterCallback) callback).latching = true;
	}
	
	@Override
	synchronized public void setupControls(final ControlFrame cf, Group g) {
		super.setupControls(cf, g);
		int margin_y = 20, gap_y = 5, margin_x = 80;

		int pos_y = 10;

		final OscStream self = this;

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
	public void processEventMeat(int time) {
		// TODO Auto-generated method stub
		int i = 0;
		
		//oscP5.send("/testvurf", new Object[] { 1.0f });
		//oscP5.send("/testvurf3", new Object[] { 1.0f }, "127.0.0.1", 12000);
	}

	@Override
	public void oscStatus(OscStatus arg0) {
		// TODO Auto-generated method stub
		println("oscStatus " + arg0);
	}
	
	@Override
	protected String getMessageNameForStreamSource(String streamSource) {
		return streamSource;		
	}

	int messageCount = 0;
	@Override
	synchronized public void oscEvent (OscMessage theOscMessage) {
		if (debug) println("got oscmessage #" + messageCount++ + " " + theOscMessage.hashCode() + " @ " + theOscMessage.addrPattern() + " value '" + theOscMessage.get(0).floatValue() +"'");
		//
		//if (theOscMessage.checkTypetag("f")) {
		//println("got type tag: " + theOscMessage.typetag());
		//println("got addrpattern '" + theOscMessage.addrPattern() + "', floatvalue " + theOscMessage.get(0).floatValue());
		if (theOscMessage.checkTypetag("f")) {
			this.addEvent(theOscMessage.addrPattern().replaceFirst("/",""), Float.parseFloat(""+theOscMessage.get(0).floatValue()));
		} else if (theOscMessage.checkTypetag("i")) {
			this.addEvent(theOscMessage.addrPattern().replaceFirst("/",""), theOscMessage.get(0).intValue());
			//println("added event for " + theOscMessage.addrPattern().replaceFirst("/","") + " '" + theOscMessage.get(0).intValue() + "'");
		} else if (theOscMessage.checkTypetag("l")) {
			this.addEvent(theOscMessage.addrPattern().replaceFirst("/",""), theOscMessage.get(0).longValue());
		} else if (theOscMessage.checkTypetag("s")) {
			this.addEvent(theOscMessage.addrPattern().replaceFirst("/",""), theOscMessage.get(0).stringValue());
		} else {
			println("unhandled typetag " + theOscMessage.get(0).toString());
		}
	}

	@Override
	protected void preCall(ParameterCallback c) {
		// TODO Auto-generated method stub
		
	}
	
}
