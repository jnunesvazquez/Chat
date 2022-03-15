package chat_cliente_servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import javax.swing.JOptionPane;

public class Cliente {
    
    private static String IP;
    private static int PORT;
    private static int newPort;
    private static String nickname;
    public static String textoGlobal = "";
            
    public static void main(String[] args) throws ClassNotFoundException {
        
        try{
            IP = JOptionPane.showInputDialog(null, "Escriba la ip del servidor");
            PORT = Integer.parseInt(JOptionPane.showInputDialog(null, "Escriba el puerto al que quiere conectarse"));

            System.out.println("Estableciendo la conexion");

            InetSocketAddress addr=new InetSocketAddress(IP, PORT);
            Socket clienteSocket = new Socket();
            clienteSocket.connect(addr);
            
            System.out.println("Conexion establecida");
            
            DataInputStream dis = new DataInputStream (clienteSocket.getInputStream());
            DataOutputStream dos = new DataOutputStream (clienteSocket.getOutputStream());
            
            userConfirmation(dis);
            nickName(dos);
            
            Socket cliente = cambiarPuerto(dis);
            DataInputStream dis2 = new DataInputStream (cliente.getInputStream());
            DataOutputStream dos2 = new DataOutputStream (cliente.getOutputStream());
            
            Chat chat = new Chat(dos2, dis2);
            chat.setVisible(true);
            chat.setTitle("Hola " + nickname);
            
            Thread h1 = new EscucharServer(nickname, cliente, chat);
            h1.start();
            
            JOptionPane.showMessageDialog(null, "Conectado a sala de chat");

        }catch (IOException e) {}
    }
    
    public static void userConfirmation(DataInputStream dis) throws IOException{
        String confirmacion = dis.readUTF();
        if (confirmacion.equalsIgnoreCase("/bye")){
            JOptionPane.showMessageDialog(null, "No puedes conectarte a la sala de chat:\nNumero máximo de usuarios alcanzado");
            System.exit(0);
        }
    }
    
    public static void nickName(DataOutputStream dos) throws IOException{
        nickname = JOptionPane.showInputDialog(null, "Escriba su nick");
        dos.writeUTF(nickname);
    }
    
    public static Socket cambiarPuerto(DataInputStream dis) throws IOException{
        System.out.println("Conectandose al nuevo puerto");
        newPort = dis.readInt();
        InetSocketAddress addr = new InetSocketAddress("localhost", newPort);
        Socket clienteSocket = new Socket();
        clienteSocket.connect(addr);
        return clienteSocket;
    }
}
