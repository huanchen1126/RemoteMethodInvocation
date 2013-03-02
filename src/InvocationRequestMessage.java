
public class InvocationRequestMessage implements Message {
  private String refObjId;
  
  private String methodStr;
  
  private String returnTypeStr;
  
  private Object[] arguments;
  
  private String interfaceStr;
  
  public InvocationRequestMessage(String id, String i, String m, String r, Object[] args) {
    this.refObjId = id;
    this.methodStr = m;
    this.returnTypeStr = r;
    this.arguments = args;
    this.interfaceStr = i;
  }
 
  public String getInterfaceStr() {
    return interfaceStr;
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
