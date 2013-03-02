
public class InvocationRequestMessage implements Message {
  private String refObjId;
  
  private String methodStr;
  
  private String returnTypeStr;
  
  private Object[] arguments;
  
  // Even though a remove object might implements a couple of
  // interfaces, for a given method, it belongs to only one
  // interface. The Method object has a getDeclaringClass()
  // method which could get the specific interface the method
  // actually belongs to. Therefore, here do not need to use
  // Class[] to store all implemented interfaces.
  private Class interfaceClass;
  
  public InvocationRequestMessage(String id, String m, String r, Class c, Object[] args) {
    this.refObjId = id;
    this.methodStr = m;
    this.returnTypeStr = r;
    this.arguments = args;
    this.interfaceClass = c;
  }
 
  public Class getInterfaceClass() {
    return interfaceClass;
  }

  public String getRefObjId() {
    return refObjId;
  }

  public String getMethodStr() {
    return methodStr;
  }

  public String getReturnTypeStr() {
    return returnTypeStr;
  }

  public Object[] getArguments() {
    return arguments;
  }
  
}
