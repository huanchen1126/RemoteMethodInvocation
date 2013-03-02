public class RemoteReferenceMessage implements Message {
  String id;

  String ip;

  int port;

  Class[] interfaces = null;

  public RemoteReferenceMessage(String rid, String ip, int port, Class[] interfaces) {
    this.id = rid;
    this.ip = ip;
    this.port = port;
    this.interfaces = interfaces;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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
