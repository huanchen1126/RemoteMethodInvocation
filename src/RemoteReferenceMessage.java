public class RemoteReferenceMessage implements Message {
  String ip;

  int port;

  Class[] interfaces = null;

  public RemoteReferenceMessage(String ip, int port, Class[] interfaces) {
    this.ip = ip;
    this.port = port;
    this.interfaces = interfaces;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public Class[] getInterfaces() {
    return interfaces;
  }

  public void setInterfaces(Class[] interfaces) {
    this.interfaces = interfaces;
  }
}
