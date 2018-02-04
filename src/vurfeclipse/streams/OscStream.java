package vurfeclipse.streams;

import java.util.Map;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.Group;
import controlP5.ScrollableList;
import controlP5.Textfield;
import vurfeclipse.APP;
import vurfeclipse.ui.ControlFrame;


import oscP5.*;
import netP5.*;

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
	public void oscEvent (OscMessage theOscMessage) {
		//println("got oscmessage " + theOscMessage);
		println("got addrpattern '" + theOscMessage.addrPattern() + "', floatvalue " + theOscMessage.get(0).floatValue());
		//if (theOscMessage.checkTypetag("f")) {
			this.addEvent(/*this.streamName +*/theOscMessage.addrPattern().replaceFirst("/",""), new Float(theOscMessage.get(0).floatValue()));
		/*} else if (theOscMessage.checkTypetag("il")) {
			this.addEvent(theOscMessage.addrPattern(), theOscMessage.get(0).longValue());
		} else if (theOscMessage.checkTypetag("s")) {
			this.addEvent(theOscMessage.addrPattern(), theOscMessage.get(0).stringValue());
		}*/
	}
	
}
