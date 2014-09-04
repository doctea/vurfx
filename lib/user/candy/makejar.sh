javac -source 1.3 -target 1.1 -d . -extdirs ../../lib/ -cp ../proXML/library/proxml.jar *.java
jar -cf candy.jar candy
mv candy.jar library/.

