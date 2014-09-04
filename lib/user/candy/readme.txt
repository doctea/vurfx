  Candy - SVG Importer for Processing

  Copyright (c) 2006-08 Michael Chang (Flux)
  http://www.ghost-hack.com/

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  ----------------------------------------------------====

  With that out of the way...

  SVG Candy is a minimal SVG import library for Processing.
  The last build for SVG Candy was for Processing-0115.

  SVG stands for Scalar Vector Graphics, a portable graphics
  format. It is a vector format so it allows for infinite resolution
  and relatively minute file sizes. Most modern media software
  can view SVG files, including Firefox, Adobe products, etc. 
  You can use something like Illustrator to edit SVG files.

  This library was specifically tested under SVG files created from
  Adobe Illustrator. I can't guarantee that it'll work for any
  SVG's created from anything else. In the future I will also
  test with open source graphics editing software so we'll reach
  maximal compatibility. In the mean time, you're on your own.

  ----------------------------------------------------====

  Installation:
  SVG Candy uses Christian Riekoff's proXML to parse SVGs. You
  will first need to install that package. Get it at:

  http://www.texone/proxml

  Once you have that, extract Candy to processing-xxxx/libraries
  so that the contents is located in processing-xxxx/libraries/candy

  You should replace the proxml.jar for the proxml lib with the 
  proxml.jar included in this archive. This is only temporary,
  but it allows svg loading for online apps.

  Restart Processing if it has already started.

  ----------------------------------------------------====

  Usage:
  In Processing under sketch->import library, candy should appear
  as a library. Import Candy.

  An SVG created under Illustrator must be created in the
  following manner:
  
  File->Save for Web (or control-alt-shift-s on a PC)
  Under settings, make sure the CSS properties is set to
  "PRESENTATION ATTRIBUTES"

  Saving it any other way will most likely break Candy

  ----------------------------------------------------====

  A minimal example program using Candy:
  (assuming a working moo.svg is in your data folder)  

  import proxml.*;
  import candy.*;
  
  SVG moo;
  void setup(){
    size(400,400);
    moo = new SVG("moo.svg",this);
  } 
  void draw(){
    moo.draw();
  } 

  Note that proxml is imported as well. This is not needed when
  running the app directly from Processing, as Candy will know
  where it is. However when you export as an applet you will
  also need to export proxml along with it to have working Candy.

  ----------------------------------------------------====

  Methods:
  (there aren't many)
  
  SVG(String filename)
    constructor
    filename - string path to your svg file

  draw()
    draws the svg to screen

  printSVG()
    prints the svg data to console

  ----------------------------------------------------====

  Known issues:
  Some SVGs created from Illustrator will fail to load.
  An example of an illegal SVG tag:

  <polygon opacity="0.42" fill="#9FB214" stroke="#6161E5" stroke-width="2" 
  enable-background="new    " points="1.31,295.314 145.402,37.375 379.762,
  204.393 235.67,462.331 "/>

  Note the 
    enable-background="new     "

  This will break the XML parser. 
  Candy has a temporary fix to work around this, and will remove 
  the entire enable-background attribute if you are running your
  processing app locally. However if you have exported your app
  to be viewable on a web page, this svg will break your applet.
  However, SVGs without that attribute will work just fine.

  Note that you can also go in and fix the SVG by hand by removing
  the line noted above. This is what happens when you can't 
  outsource the work to machines.

  This issue will probably be resolved by the next release.

  ====-------------------

  Some SVG objects and features may not yet be supported.
  Here is a partial list of non-included features
  -Rounded rectangles
  -Things with direct horizontal lines or direct vertical lines
  -Drop shadow objects
  -Typography
  -Layers
  -Patterns
  -Embedded images

  I'll probably be getting Candy to support the first two fairly soon.

  ====-------------------

  Filled beziers will look messed up under OPENGL and P3D.
  This is not Cadny Lib's fault. I'm pointing my finger at Fry for this. :)
  File a petition, or something, at http://www.processing.org/discourse
  to get 3D/GL bezier fills fixed.

  ====-------------------

  If you experience any other weirdness or bugs, please file them to
  flux.blackcat@gmail.com with subject: Delicious Candy