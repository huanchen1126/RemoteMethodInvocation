
public class Main {
  
  public static final boolean DEBUG = true;

  public static void main(String[] args) {
    Class[] inters = {TestInterface.class};
    RemoteReferenceMessage ror = new RemoteReferenceMessage("refa","", 0, inters);
    TestInterface a = (TestInterface) StubCompiler.createStub(ror, TestInterface.class);
    
    a.functionA(1, 2);
  }

}
