package vurfeclipse.streams;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.Group;
import controlP5.Textfield;
import vurfeclipse.ui.ControlFrame;


import oscP5.*;

public class OscStream extends Stream implements OscEventListener {

	OscP5 oscP5;
	
	public OscStream() {
		  oscP5 = new OscP5(this,12000);
		  oscP5.addListener(this);
	}

	public OscStream(String streamName) {
		super(streamName);

		  oscP5 = new OscP5(this,12000);
		  oscP5.addListener(this);
	}
	
	@Override
	public synchronized void registerEventListener(String paramName, ParameterCallback callback) {
		// TODO Auto-generated method stub
		super.registerEventListener(paramName, callback);
		if(callback instanceof FormulaCallback) ((FormulaCallback) callback).latching = true;
	}
	
	@Override
	protected Group makeEmitterSelector(ControlFrame cf, ParameterCallback callback, String name) {
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

	@Override
	public void oscEvent (OscMessage theOscMessage) {
		//println("got oscmessage " + theOscMessage);
		//
		//if (theOscMessage.checkTypetag("f")) {
		//println("got type tag: " + theOscMessage.typetag());
		//println("got addrpattern '" + theOscMessage.addrPattern() + "', floatvalue " + theOscMessage.get(0).floatValue());
		if (theOscMessage.checkTypetag("f")) {
			this.addEvent(/*this.streamName +*/theOscMessage.addrPattern()/*.replaceFirst("/","")*/, Float.parseFloat(""+theOscMessage.get(0).floatValue()));
		} else if (theOscMessage.checkTypetag("i")) {
			this.addEvent(theOscMessage.addrPattern(), theOscMessage.get(0).intValue());
			println("added event for " + theOscMessage.addrPattern().replaceFirst("/","") + " '" + theOscMessage.get(0).intValue() + "'");
		} else if (theOscMessage.checkTypetag("l")) {
			this.addEvent(theOscMessage.addrPattern(), theOscMessage.get(0).longValue());
		} else if (theOscMessage.checkTypetag("s")) {
			this.addEvent(theOscMessage.addrPattern(), theOscMessage.get(0).stringValue());
		} else {
			println("unhandled typetag " + theOscMessage.get(0).toString());
		}
	}
	
}
