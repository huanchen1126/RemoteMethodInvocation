public class ObjectRequestMessage implements Message {
  private String id;

  public ObjectRequestMessage(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

}
