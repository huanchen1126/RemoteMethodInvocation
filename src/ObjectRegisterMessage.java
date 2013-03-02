public class ObjectRegisterMessage implements Message {
  private String id;

  private RemoteReferenceMessage ror = null;

  public ObjectRegisterMessage(String id, RemoteReferenceMessage ror) {
    this.id = id;
    this.ror = ror;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public RemoteReferenceMessage getRor() {
    return ror;
  }

  public void setRor(RemoteReferenceMessage ror) {
    this.ror = ror;
  }

}
