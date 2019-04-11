import mayflower.Mayflower;

public class RunTest {

  public static void main(String[] args) {

    int port = 8082;
    int count = Integer.parseInt(Mayflower.ask("# of games?"));

      new AmazonsServer(port, count);
      AmazonsClient clientA = new AmazonsClient();
      AmazonsClient clientB = new AmazonsClient();
    clientA.registerListener(new MiniMaxAIClientListenerB("AI B"));
    clientA.registerListener(new GUIListener());
    clientB.registerListener(new MiniMaxAIClientListener("AI A"));
      clientA.connect(port);
      clientB.connect(port);

  }
}
