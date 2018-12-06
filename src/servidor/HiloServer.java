package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.LinkedList;


public class HiloServer implements Runnable {

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private int figura;
    private int Y[][];
    private boolean turn;
    private LinkedList<Socket> usuarios = new LinkedList<Socket>();

    
    public HiloServer(Socket soc, LinkedList users, int xo, int[][] Gato) {
        socket = soc;
        usuarios = users;
        figura = xo;
        Y = Gato;
    }

    @Override
    public void run() {
        try {

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            turn = figura == 1;
            String msg = "";
            msg += "JUEGAS: " + (turn ? "X;" : "O;");
            msg += turn;
            out.writeUTF(msg);

            while (true) {

                String recibidos = in.readUTF();
                String recibido[] = recibidos.split(";");

                int f = Integer.parseInt(recibido[0]);
                int c = Integer.parseInt(recibido[1]);

                Y[f][c] = figura;

                String cad = "";
                cad += figura + ";";
                cad += f + ";";
                cad += c + ";";

                boolean ganador = gano(figura);
                boolean completo = full();

                if (!ganador && !completo) {
                    cad += "NADIE";
                } else if (!ganador && completo) {
                    cad += "EMPATE";
                } else if (ganador) {
                    vaciar();
                    cad += figura == 1 ? "X" : "O";
                }

                for (Socket usuario : usuarios) {
                    out = new DataOutputStream(usuario.getOutputStream());
                    out.writeUTF(cad);
                }
            }
        } catch (Exception e) {

            for (int i = 0; i < usuarios.size(); i++) {
                if (usuarios.get(i) == socket) {
                    usuarios.remove(i);
                    break;
                }
            }
            vaciar();
        }
    }

    public boolean gano(int n) {
        for (int i = 0; i < 3; i++) {
            boolean gano = true;
            for (int j = 0; j < 3; j++) {
                gano = gano && (Y[i][j] == n);
            }
            if (gano) {
                return true;
            }
        }

        for (int i = 0; i < 3; i++) {
            boolean gano = true;
            for (int j = 0; j < 3; j++) {
                gano = gano && (Y[j][i] == n);
            }
            if (gano) {
                return true;
            }
        }

        if (Y[0][0] == n && Y[1][1] == n && Y[2][2] == n) {
            return true;
        }

        return false;
    }

    public boolean full() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (Y[i][j] == -1) {
                    return false;
                }
            }
        }

        vaciar();
        return true;
    }

    public void vaciar() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Y[i][j] = -1;
            }
        }
    }
}
