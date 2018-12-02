set CLASSPATH="antlr-4.7.1-complete.jar:$CLASSPATH"
./clean.sh
java -jar antlr-4.7.1-complete.jar Smoola.g4
javac *.java
# NORMAL
java org.antlr.v4.gui.TestRig Smoola program < in.sml
# WITH PARSER TREE
# java org.antlr.v4.gui.TestRig Smoola program -gui < in.sml
# DEBUG
# java -agentlib:jdwp=transport=dt_socket,address=localhost:8888,server=y,suspend=y org.antlr.v4.gui.TestRig Smoola program < in.sml
./clean.sh