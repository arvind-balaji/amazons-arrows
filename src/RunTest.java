import mayflower.Mayflower;

public class RunTest {

  public static void main(String[] args) {

    int port = 8082;
    int count = Integer.parseInt(Mayflower.ask("# of games?"));

      new AmazonsServer(port, count);
      AmazonsClient clientA = new AmazonsClient();
      AmazonsClient clientB = new AmazonsClient();
    clientA.registerListener(new MiniMaxAIClientListenerD("AI D"));
    clientA.registerListener(new GUIListener(10));
    clientB.registerListener(new MiniMaxAIClientListenerC("AI C"));
      clientA.connect(port);
      clientB.connect(port);

  }
}
