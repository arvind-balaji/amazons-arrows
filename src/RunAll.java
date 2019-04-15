public class RunAll {

  public static void main(String[] args) {
    int port = 8082;

    AmazonsServer server =  new AmazonsServer(port);
    AmazonsClient clientA = new AmazonsClient();
    AmazonsClient clientB = new AmazonsClient();

    clientA.registerListener(new MiniMaxAIClientListenerB("MiniMax C"));
    clientA.registerListener(new GUIListener());

    clientB.registerListener(new MyAIClientListener("AI A"));

    clientA.connect(port);
    clientB.connect(port);

  }
}
