public class TestClient {
  public static void main(String[] args) {
    
    if (args.length != 2) {
      System.err.println("Usage: TestClient <registry_ip> <registry_port>");
      return ;
    }
    
    String rip = args[0];
    int rport = Integer.parseInt(args[1]);

    System.out.println("Request a remote object \"testobj\" in terms of interface \"TestInterface1\" ");
    TestInterface1 testobj1 = (TestInterface1) StubCompiler.compile("testobj",
            TestInterface1.class, rip, rport);
    if (testobj1 != null) {
      System.out.println("Call add(10, 9) of object \"testobj\", waiting for result...");
      int sum = testobj1.add(10, 9);
      System.out.println("Receive result: " + sum);
    }

    System.out.println("Request a remote object \"testobj\" in terms of interface \"TestInterface2\" ");
    TestInterface2 testobj2 = (TestInterface2) StubCompiler.compile("testobj",
            TestInterface2.class, rip, rport);
    if (testobj2 != null) {
      Integer[] elements = { 1, 2, 3, 4, 5, 6, 7, 8 };
      System.out.println("Call addAll(int[]{ 1, 2, 3, 4, 5, 6, 7, 8 }) of object \"testobj\", waiting for result...");
      int sum = testobj2.addAll(elements);
      System.out.println("Receive result: " + sum);
    }
  }

}
