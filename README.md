# VurfX docs (?)

by Tristan Rowley (c) 2012-present (except where using code copyright others or public domain!), doctea@gmail.com

a rough and hacky java-based VJ/music video/trippy visuals application thing using Processing 1.5.1 and GLGraphics library.  With glsl shaders, SimpleOpenNI/Kinect rgb and depth support.

uses cp5, many other libraries I'm sure - TODO: ADD LINKS AND STUFF

example output and more info can be found at https://facebook.com/vurfx and https://instagram.com/vurfx

You haven't slept for over 60 hours, you're in no fit state!

## FEATURES
--------

...it does lots of stuff... not always very well or clearly
randomise sequence/playlist mode
ImageListDrawer loads a directory of images to use as sources
BeatStream for locking fx to a beat
extend functionality/customise fx through preset callbacks and/or overriding methods 

### HAVE YOU BEEN AT THE CONTROLS?

#### Keyboard controls

|| key | action | likely to change in future? ||
|| - | show/hide debug layer ||
|| l | lock sequencer (don't change when sequence is ready) ||
|| ' | stop sequencer (sequence values don't update, sequencer doesn't change when ready) ||
|| ; | next sequence (ie next in sequencer playlist or random etc depending on behaviour of the sequencer) ||
|| j,J | previous sequence in playlist history (J does not restart the sequence) ||
|| k,K | next sequence in playlist history (K does not restart the sequence) ||
|| o | cut between current and next playlist history without restarting sequence ||
|| O | restart currently playing sequence | * ||
|| q | increase sequence timescale by 0.1 | * ||
|| a | decrease sequence timescale by 0.1 | * ||
|| space | take screenshot ||
|| m | pause/stop streams ||

sequence playlist history / backwards buttons (j/k to go back/fore, J/K to go back/fore without restarting the Sequence. o)

#### CODE/CLASS/OBJECT STRUCTURE 

* VurfEclipse** is the main(), setup, and render loop
  * has one instantiated **Project**
    * has many **Streams**
      * callbacks interact with **Filter**s and **Scene**s
    * has one **Sequencer**
      * has many **Sequences**
        * interacts with **Filter**s and **Scene**s via overriding methods
    * has many **Scenes**
      * has many **Sequences**
      * has many **Filter**s
        * has many **Presets**
        * has many **Parameters**
    * **Scenes** and **Filters** each have many **Canvas**es

## TODO

* proper docs/instructions
* sequencer features
  * delete items from history
  * save/load history to file
  * set presets for favourites
  * quick-cut button with no-restart mode for sequences
  * stutter time button
  * locking timescale and start of sequence changes to beat tempo/triggers
* UI features
  * map cp5 sliders to Filter Parameters ('proxy' Parameters?)
  * map MIDI controls to sliders
  * map functions to sliders
  * map game controllers to sliders
* effects/shaders
  * fractal
  * rounded pixel effect
  * vertical stripes effect
  * mask effect to cut out borders
* streaming/texture sharing
  * turbojpg stream output / stream to&from tcpsyphon/tcpspout/ofxpimapper?
* big bugs
  * fix problems and figure out how to make a true fullscreen version
  * make run as an Application reliably rather than an Applet, to help remove some window border
* improve features
  * better MIDI support
  * better REST/web interface

### future plans
	port to Processing 3 or to some other platform or package
	learn OSC and interface with that
	improve Kinect performance
	better multi-Kinect support
	get some more wine



