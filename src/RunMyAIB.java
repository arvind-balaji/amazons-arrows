import mayflower.Mayflower;

public class RunMyAIB {

  public static void main(String[] args) {
    String ip = Mayflower.ask("IP?");
    if ("".equals(ip.trim()))
      ip = "localhost";

    String sPort = Mayflower.ask("Port?");
    if ("".equals(sPort.trim()))
      sPort = "8083";

    try {
      int port = Integer.parseInt(sPort);
      System.out.println("Connecting to " + ip + " on port " + port);

      AmazonsClient clientA = new AmazonsClient();
      clientA.registerListener(new MiniMaxAIClientListenerC("MiniMax C"));
//      clientA.registerListener(new GUIListener());

      clientA.connect(ip, port);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
