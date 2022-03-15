package chat_cliente_servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.IOException;
import java.util.HashMap;

public class Servidor {
    
    private static final String IP = "localhost";
    private static final int PORT = 5555;
    private static int newPort = 5555;
    static ArrayList<String> datos = new ArrayList();
    static HashMap<String, Socket> sockets = new HashMap();
    ServerSocket server = null;
    
    public static void main(String[] args) {
        try {
            
            System.out.println("Creando socket servidor");

            ServerSocket serverSocket = new ServerSocket();
            
            InetSocketAddress addr = new InetSocketAddress(IP, PORT);
            serverSocket.bind(addr);

            System.out.println("Realizando el bind");
                
            while(true){
                Socket newSocket = serverSocket.accept();
                System.out.println("Conexion recibida");
                
                DataInputStream dis = new DataInputStream(newSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(newSocket.getOutputStream());
                
                if (sockets.size() >= 10){
                    rejectAccess(dos, newSocket);
                } else {
                    String nickname = grantAccess(dos, dis);
                    Thread h1 = new LeerCliente(nickname, cambiarPuerto(dos));
                    h1.start();
                }
            }
        } catch (IOException e){}
     }
    
    public static String grantAccess(DataOutputStream dos, DataInputStream dis) throws IOException{
        dos.writeUTF("");
        String mensaje = dis.readUTF();
        return mensaje;
    }
    
    public static void rejectAccess(DataOutputStream dos, Socket socket) throws IOException{
        System.out.println("No pueden conectarse mas usuarios");
        dos.writeUTF("/bye");
        socket.close();
    }
    
    public static Socket cambiarPuerto(DataOutputStream dos) throws IOException{
        newPort++;
        dos.writeInt(newPort);
        InetSocketAddress addr = new InetSocketAddress(IP, newPort);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(addr);
        Socket userSocket = serverSocket.accept();
        System.out.println("Segunda conexion recibida");
        return userSocket;
    }
}
