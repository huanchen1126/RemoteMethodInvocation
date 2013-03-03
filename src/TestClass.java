public class TestClass implements TestInterface1, TestInterface2 {

  @Override
  public Integer add(Integer a, Integer b) {
    return new Integer(a + b);
  }

  @Override
  public Integer addAll(Integer[] args) {
    Integer sum = 0;
    for (Integer a : args) {
      sum += a;
    }
    return sum;
  }

}
