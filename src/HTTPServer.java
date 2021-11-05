import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HTTPServer {
    public static void main(String[] args) throws IOException {

        int port = 5050;
        ServerSocket verbindung = new ServerSocket(port);

        boolean walter = true;
        while (walter) {
            Socket clientsocket = null;

            try {
                clientsocket = verbindung.accept();

                System.out.println("Ein neuer Penner hat sich eingeklingt" + clientsocket);

                //Streams erhalten
                DataInputStream eingang = new DataInputStream(clientsocket.getInputStream());
                DataOutputStream ausgang = new DataOutputStream(clientsocket.getOutputStream());

                System.out.println("Neuer Client wird erhaengt");

                //create Faden
                Thread faden = new ClientHandler(clientsocket, eingang, ausgang);

                //start Thread
                faden.start();
            } catch (Exception exception) {
                clientsocket.close();
                exception.printStackTrace();
            }
        }

    }
}


//Client Handler

class ClientHandler extends Thread {
    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
    final DataInputStream eingangthread;
    final DataOutputStream ausgangthread;
    final Socket threadsocket;

    //Constructor
    public ClientHandler(Socket threadsocket, DataInputStream eingangthread, DataOutputStream ausgangthread) {
        this.threadsocket = threadsocket;
        this.eingangthread = eingangthread;
        this.ausgangthread = ausgangthread;
    }

    @Override
    public void run() {
        String received;
        String torerun;
        while (true) {
            try {

                //ask what the fucker needs from you
                ausgangthread.writeUTF("What do you need? \n" + "Type Exit to fuck off");

                //receive answer
                received = eingangthread.readUTF();

                if (received.equals("Exit")) {
                    System.out.println("Client" + this.threadsocket + "send exit..Goodbye");
                    System.out.println("Closing Connection");
                    this.threadsocket.close();
                    System.out.println("Closed");
                    break;
                }

                //datum
                Date datum = new Date();


                switch (received) {

                    case "Date":
                        torerun = fordate.format(datum);
                        ausgangthread.writeUTF(torerun);
                        break;

                    case "Time":
                        torerun = fortime.format(datum);
                        ausgangthread.writeUTF(torerun);
                        break;

                    default:
                        ausgangthread.writeUTF("Falscher Input");
                        break;
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        try {
            //close
            this.ausgangthread.close();
            this.eingangthread.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
