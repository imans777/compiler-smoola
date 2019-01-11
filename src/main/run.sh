INPUT_NAME=$1

set CLASSPATH="antlr-4.7.1-complete.jar:$CLASSPATH"

./clean.sh
rm output/*.j
java -jar antlr-4.7.1-complete.jar Smoola.g4
javac *.java
java MySmoola $INPUT_NAME
# java -agentlib:jdwp=transport=dt_socket,address=localhost:8888,server=y,suspend=y MySmoola $INPUT_NAME

# AUTOMATE THIS!
# $VAR
# cd output
# java -jar "..\jasmin-2.4\jasmin.jar" -d output *.j
# java Main
# java -agentlib:jdwp=transport=dt_socket,address=localhost:8888,server=y,suspend=y -jar jasmin-2.4\jasmin.jar *.j


# These are for the situation WITHOUT Smoola.java file
  # NORMAL
# java org.antlr.v4.gui.TestRig Smoola program < in.sml
  # WITH PARSER TREE
# java org.antlr.v4.gui.TestRig Smoola program -gui < in.sml
  # DEBUG
# java -agentlib:jdwp=transport=dt_socket,address=localhost:8888,server=y,suspend=y org.antlr.v4.gui.TestRig Smoola program < in.sml

./clean.sh