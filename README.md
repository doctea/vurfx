# VurfX docs (?)

by Tristan Rowley (c) 2012-present (except where using code copyright others or public domain!), doctea@gmail.com

a rough and hacky java-based VJ/music video/lightsynth/semi-automated/trippy visuals application thing based on Processing 3.  With glsl shaders, SimpleOpenNI/Kinect rgb and depth support.

uses cp5, many other libraries I'm sure - TODO: ADD LINKS AND STUFF

example output and more info can be found at https://facebook.com/vurfx and https://instagram.com/vurfx

You haven't slept for over 60 hours, you're in no fit state!

## FEATURES
--------

...it does lots of stuff... not always very well or clearly
randomise sequence/playlist mode
ImageListDrawer loads a directory of images to use as sources
BeatStream for locking fx to a beat
OscStream input for OSC
MidiStream input for MIDI
Extend functionality/customise fx through preset callbacks and/or overriding methods
Project snapshots (including Scene & Filter Parameter settings) saved&loaded from xml file(s)


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
|| f,F | save,load Sequence info (debug) | * ||
|| s,S | save,load a Snapshot (stores global Scene&Filter settings and Sequence structure) | * ||
|| B | save Sequence bank to separate .xml files | * ||
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
    * starting to implement this with Snapshots...
  * quick-cut button with no-restart mode for sequences
    * 'o' does this, but think there's a bug
  * stutter time button
  * locking timescale and start of sequence changes to beat tempo/triggers
* UI features
  * map cp5 sliders to Filter Parameters ('proxy' Parameters?)
  * map functions to sliders
  * map game controllers to sliders
* effects/shaders
  * fractal (mandlebrot shader now done)
  * rounded pixel effect
  * vertical stripes effect
  * mask effect to cut out or smooth borders
* streaming/texture sharing
  * turbojpg stream output / stream to&from tcpsyphon/tcpspout/ofxpimapper?
* big bugs
  * ported to Processing 3, can now fullscreen, but upscaling has incorrect matrix?
* improve features
  * better MIDI support
    * tempo/BeatStream sync
  * better REST/web interface

### future plans
  * improve Kinect performance
    * had trouble getting SimpleOpenNI to work on Windows 10, switched to using libFreenect/open-processing-for-kinect library, performance is hugely better but lose rgb/depth registration and potential to use skeleton tracking etc
  * better multi-Kinect support
  * get some more wine
  * better 'live mode'
  
  
-------

Needed for Rainbow Hill Dance Club gig with Mutante:-

  * Mutante set - 1 hour of effects
  * Disco set - 2 hour(?) of dancefloor dancing effects
    * maybe get video playback support working better, and get some videos of dancer silhouettes to use as layers?
  * General
    * save/load projects to multiple xml files
    * start a new project file with bare bones Scenes and Sequences
    * save canvas mappings with Sequence
      * so, there are two different things that need dealing with here
        * Scenes have an expandable list of canvas mappings
          * eg "Scene 'pix0' maps to Project 'temp3', 'Scene 'out' maps to Project 'out'
        * Filter stores an alias for each input & output
          * eg "input maps to pix0", "output maps to out"
      * Scene's canvas mappings should be stored with the scene..?
      * Filter's canvas aliases should be stored with the sequence..?
    
  