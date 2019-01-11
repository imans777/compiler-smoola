package ast.Type;

public class Key { // a Key class for implementing (class, method) key pair
  public String className;
  public String methodName;

  public Key(String className, String methodName) {
    this.className = className;
    this.methodName = methodName;
  }

  @Override
  public boolean equals(Object o) { // this is necessary for fields equalities
    if (this == o)
      return true;
    if (!(o instanceof Key))
      return false;
    Key key = (Key) o;
    return this.className.equals(key.className) && this.methodName.equals(key.methodName);
  }

  @Override
  public int hashCode() { // this is necessary for hash equality and uniqueness only based on fields
    int res = className.hashCode();
    res = 31 * res + methodName.hashCode();
    return res;
  }
}