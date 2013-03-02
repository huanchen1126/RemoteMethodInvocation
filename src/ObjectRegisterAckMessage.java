public class ObjectRegisterAckMessage implements Message {
  private boolean success;

  public ObjectRegisterAckMessage(boolean success) {
    this.success = success;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

}
