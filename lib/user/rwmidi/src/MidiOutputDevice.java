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
 * Represents a device that can be opened for sending MIDI messages.
 * @author manuel
 *
 */
public class MidiOutputDevice extends MidiDevice {

	public MidiOutputDevice(Info _info) {
		super(_info);
	}

	/**
	 * Create an output object for the MIDI device.
	 * @return the created output
	 */
	public MidiOutput createOutput() {
		try {
			return new MidiOutput(this);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
			return null;
		}
	}


}
