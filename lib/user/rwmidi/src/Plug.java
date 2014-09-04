/*

Copyright (c) 2005 Christian Riekoff
			  2008 Manuel Odendahl

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General
Public License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330,
Boston, MA  02111-1307  USA
*/

package rwmidi;

import java.lang.reflect.Method;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

/**
 * Wrapper class for callback plugs. You don't usually need to access this class.
 * @author manuel
 *
 */

class Plug{
	private final Method method;
	private final String methodName;
	private final Object object;
	private final Class objectClass;
	private Class paramClass;

	private int status = -1;
	private int channel = -1;

	Plug(
			final Object _object,
			final String _methodName,
			final int _channel,
			final int _status
	){
		object = _object;
		objectClass = getObject().getClass();
		methodName = _methodName;
		method = initPlug();
		setStatus(_status);
		channel = _channel; 
	}
	
	static boolean objectHasMethod(Object obj, String methodName) {
		Class objectClass = obj.getClass();
		for (Method method : objectClass.getMethods()) {
			if (method.getName().equals(methodName))
				return true;
		}
		return false;
	}

	private Method initPlug(){		
		if (methodName != null && methodName.length() > 0){
			final Method[] objectMethods = objectClass.getMethods();

			for (int i = 0; i < objectMethods.length; i++){
				objectMethods[i].setAccessible(true);

				if (objectMethods[i].getName().equals(methodName)){
					final Class[] objectMethodParams = objectMethods[i].getParameterTypes();
					if (objectMethodParams.length == 1) {
						paramClass = objectMethodParams[0];
						try {
							return objectClass.getMethod(methodName, objectMethodParams);
						} catch (Exception e) {
							break;
						}
					} else {
						break;
					}
				}
			}
		}
		throw new RuntimeException("Error on plug: >" +methodName + "< Invalid argument class");
	}

	void callPlug(MidiInput _input, final MidiMessage msg){
		try{
			if ((msg.getStatus() & 0xF0) != getStatus() && getStatus() != -1)
				return;
			if (msg instanceof ShortMessage) {
				ShortMessage smsg = (ShortMessage)msg;
				if (smsg.getChannel() != channel && channel != -1)
					return;
			}
			MidiEvent event = null;
			if (msg instanceof MidiEvent) {
				event = (MidiEvent)msg;
			} else {
				event = MidiEvent.create(msg);
			}

			if (event != null) {
				event.setInput(_input);
				if (paramClass.isInstance(event))
					method.invoke(getObject(),new Object[]{event});
			}
		}catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("Error on calling plug: " +methodName);
		}
	}

	protected Object getObject() {
		return object;
	}

	protected String getMethodName() {
		return methodName;
	}

	protected void setChannel(int channel) {
		this.channel = channel;
	}

	protected int getChannel() {
		return channel;
	}

	protected void setStatus(int status) {
		this.status = status;
	}

	protected int getStatus() {
		return status;
	}
}
