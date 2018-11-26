set CLASSPATH="antlr-4.7.1-complete.jar:$CLASSPATH"
rm *.class
rm *.tokens
rm Smoola*.java
rm *.interp
java -jar antlr-4.7.1-complete.jar Smoola.g4
javac *.java
java org.antlr.v4.gui.TestRig Smoola program < in.sml
# java org.antlr.v4.gui.TestRig Smoola program -gui < in.sml
