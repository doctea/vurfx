/*

Copyright (c) 2008 Manuel Odendahl

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

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.MidiDevice.Info;

/**
 * Represents a device that can be opened for reading and receiving MIDI messages. An object with callbacks can
 * be given when creating an input from this input device.
 * @author manuel
 *
 */
public class MidiInputDevice extends MidiDevice {

	MidiInputDevice(Info _info) {
		super(_info);
	}

	/**
	 * Create an input object for the device.
	 * @return the created input
	 */
	public MidiInput createInput() {
		try {
			return new MidiInput(this);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Create an input object for the device. See {@Link MidiInput-plug-Object}
	 * 
	 * @param obj Object to be registered as callback
	 * @return the created input
	 */
	public MidiInput createInput(Object obj) {
		return createInput(obj, -1);
	}

	/**
	 * Create an input object for the device and register the object given as argument as a callback for messages on the given channel.
	 * @param obj Object to be registered as callback
	 * @param channel Channel on which the object is to be registered
	 * @return the created input
	 */
	public MidiInput createInput(Object obj, int channel) {
		MidiInput input = createInput();
		input.plug(obj, channel);
		return input;
	}	
}
