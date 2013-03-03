public class TestClient {
  public static void main(String[] args) {

    TestInterface1 testobj1 = (TestInterface1) StubCompiler.compile("testobj",
            TestInterface1.class, "128.237.125.92", 55555);
    if (testobj1 != null) {
      int sum = testobj1.add(10, 9);
      System.out.println("Sum = " + sum);
    }

    TestInterface2 testobj2 = (TestInterface2) StubCompiler.compile("testobj",
            TestInterface2.class, "128.237.125.92", 55555);
    if (testobj2 != null) {
      Integer[] elements = { 1, 2, 3, 4, 5, 6, 7, 8 };
      int sum = testobj2.addAll(elements);
      System.out.println("Sum all = " + sum);
    }
  }

}
