#!/bin/bash
FILES="*.glc"
for f in $FILES
do
 echo "Processing $f file..."
 # take action on each file. $f store current file name
 #glc-play $f -y 1 -o - | mencoder -demuxer y4m - -ovc lavc -lavcopts vcodec=mpeg4 -o ${f%.*}.avi
 #glc-play $f -y 1 -o - | ffmpeg -i - -q:v 12 ${f%.*}.avi
 ionice glc-play $f -y 1 -o - | ionice nice ffmpeg -i - -c:v libx264 -preset medium -crf 22 -c:a copy ${f%.*}.avi
 #mencoder -demuxer y4m - -ovc lavc -lavcopts vcodec=mpeg4 -o ${f%.*}.avi
 # This previous line takes in the glc file at $f and changes the extension to .avi for saving it as mpeg4
done
