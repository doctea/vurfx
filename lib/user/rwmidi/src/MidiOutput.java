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

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

/**
 * Represents a MIDI output used to send MIDI data. This can be a physical MIDI output or a virtual
 * MIDI receiver like the Java Sound Synthesizer.
 */
public class MidiOutput {
	Receiver receiver;
	javax.sound.midi.MidiDevice device;
	
	MidiOutput(javax.sound.midi.MidiDevice device) throws MidiUnavailableException {
		this.device = device;
		if (!device.isOpen())
			device.open();
		receiver = device.getReceiver();

	}
	
	MidiOutput(Receiver _receiver) {
		receiver = _receiver;
	}

	MidiOutput(MidiOutputDevice _device) throws MidiUnavailableException {
		this(_device.getDevice());
	}

	public String getName() {
		javax.sound.midi.MidiDevice.Info info = device.getDeviceInfo();
		return info.getName() + " " + info.getVendor();
	}
	

	/**
	 * Send a NOTE ON message on this output.
	 * @param channel Channel on which to send the message
	 * @param note Note pitch
	 * @param velocity Note velocity
	 * @return 1 on success, 0 on error
	 */
	public int sendNoteOn(int channel, int note, int velocity) {
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(MidiEvent.NOTE_ON, channel, note, velocity);
			receiver.send(msg, -1);
			return 1;
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Send a NOTE OFF message on this output.
	 * @param channel Channel on which to send the message
	 * @param note Note pitch
	 * @param velocity Note velocity
	 * @return 1 on success, 0 on error
	 */
	public int sendNoteOff(int channel, int note, int velocity) {
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(MidiEvent.NOTE_OFF, channel, note, velocity);
			receiver.send(msg, -1);
			return 1;
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Send a Controller change message on this output.
	 * @param channel Channel on which to send the message
	 * @param cc Controller Change number
	 * @param value Controller Change value
	 * @return 1 on success, 0 on error
	 */
	public int sendController(int channel, int cc, int value) {
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(MidiEvent.CONTROL_CHANGE, channel, cc, value);
			receiver.send(msg, -1);
			return 1;
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Send a Program Change on this output
	 * @param channel Channel on which to send the message
	 * @param value Program Change value
	 * @return 1 on success, 0 on error
	 */
	public int sendProgramChange(int value) {
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(MidiEvent.PROGRAM_CHANGE, value, -1);
			receiver.send(msg, -1);
			return 1;
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Send a SYSEX MIDI message on this output
	 * @param msg Bytes of the sysex message, have to contain 0xF0 at the beginning and 0xF7 at the end
	 * @return 1 on success, 0 on error
	 */
	public int sendSysex(byte [] msg) {
		javax.sound.midi.SysexMessage msg2 = new javax.sound.midi.SysexMessage();
		try {
			msg2.setMessage(msg, msg.length);
			receiver.send(msg2,0);
			return 1;
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	public int sendSysex(SysexMessage msg) {
		return sendSysex(msg.getMessage());
	}


	/**
	 * Close the device associated with this output. This will close other outputs connected to this device as well.
	 */
	public void closeMidi() {
		device.close();
	}
}
