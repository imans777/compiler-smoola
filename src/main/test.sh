
set CLASSPATH="antlr-4.7.1-complete.jar:$CLASSPATH"

./clean.sh
java -jar antlr-4.7.1-complete.jar Smoola.g4
javac *.java

echo "-------------------- Test -2 -------------------"
java MySmoola in2.sml
echo "-------------------- Test -1 -------------------"
java MySmoola in3.sml
echo "-------------------- Test 0 --------------------"
java MySmoola in_little.sml
echo "-------------------- Test 1 --------------------"
java MySmoola in.sml
echo "-------------------- Test 2 --------------------"
java MySmoola M1_ERR_ClassExtendsTemporary.sml
echo "-------------------- Test 3 --------------------"
java MySmoola M2_ERR_ClassMethodVarSameName.sml
echo "-------------------- Test 4 --------------------"
java MySmoola M3_ERR_varRedefinitionInChildOfChild.sml
echo "-------------------- Test 5 --------------------"
java MySmoola M4_ERR_RepeatedClassAndVariable.sml
echo "-------------------- Test 6 --------------------"
java MySmoola M5_ERR_RepeatedClassAndMethod.sml
echo "------------------- END TESTS ------------------"

./clean.sh