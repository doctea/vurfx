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

/**
 * Represents a MIDI Controller Change message. The values are parsed into the CC number and the value, which
 * you can access using the methods {@Link Controller-getCC} and {@Link Controller-getValue}.
 * 
 */
public class Controller extends MidiEvent{
	/**
	 * Create a Controller Change message.
	 * @param _channel Controller Change channel
	 * @param _number Controller Change number
	 * @param _value Controller Change value
	 */
	public Controller(final int _channel, final int _number, final int _value){
		super(CONTROL_CHANGE | _channel, _number, _value);
	}

	public Controller(final int _number, final int _value){
		super(CONTROL_CHANGE, _number, _value);
	}

	/**
	 * 
	 * @return the CC number of the message
	 */
	public int getCC(){
		return getData1();
	}

	/**
	 * 
	 * @return the value of the CC message
	 */
	public int getValue(){
		return getData2();
	}

	public String toString() {
		return "rwmidi.Controller cc: " + getCC() + " value: " + getValue();
	}
}
