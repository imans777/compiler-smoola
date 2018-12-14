
set CLASSPATH="antlr-4.7.1-complete.jar:$CLASSPATH"

./clean.sh
java -jar antlr-4.7.1-complete.jar Smoola.g4
javac *.java

echo "-------------------- Test 1 --------------------"
java Smoola in.sml
echo "-------------------- Test 2 --------------------"
java Smoola M1_ERR_ClassExtendsTemporary.sml
echo "-------------------- Test 3 --------------------"
java Smoola M2_ERR_ClassMethodVarSameName.sml
echo "-------------------- Test 4 --------------------"
java Smoola M3_ERR_varRedefinitionInChildOfChild.sml
echo "-------------------- Test 5 --------------------"
java Smoola M4_ERR_RepeatedClassAndVariable.sml
echo "-------------------- Test 6 --------------------"
java Smoola M5_ERR_RepeatedClassAndMethod.sml
echo "------------------- END TESTS ------------------"

./clean.sh