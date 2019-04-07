import mayflower.Mayflower;

public class RunTest {

  public static void main(String[] args) {

    int port = 8082;
    int count = Integer.parseInt(Mayflower.ask("# of games?"));

      new AmazonsServer(port, count);
      AmazonsClient clientA = new AmazonsClient();
      AmazonsClient clientB = new AmazonsClient();
      clientA.registerListener(new MyAIClientListener("AI A"));
//      clientA.registerListener(new GUIListener());
      clientB.registerListener(new MyAIClientListenerB("AI B"));
      clientA.connect(port);
      clientB.connect(port);

  }
}
