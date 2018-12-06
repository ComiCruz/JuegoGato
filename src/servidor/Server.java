package servidor;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {

    private final int port = 2626;
    private final int noJugadoresMax = 2;
    private LinkedList<Socket> users = new LinkedList<Socket>();
    private Boolean turn = true;
    private int Y[][] = new int[3][3];
    private int turnos = 1;

    public void escuchar() {
        try {

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    Y[i][j] = -1;
                }
            }

            ServerSocket servidor = new ServerSocket(port, noJugadoresMax);

            while (true) {

                System.out.println("Esperando jugadores....");
                Socket cliente = servidor.accept();
                System.out.println("Se ha conectado un nuevo usuario");
                users.add(cliente);
                int xo = turnos % 2 == 0 ? 1 : 0;
                turnos++;
                Runnable run = new HiloServer(cliente, users, xo, Y);
                Thread hilo = new Thread(run);
                hilo.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server servidor = new Server();
        servidor.escuchar();
    }
}
