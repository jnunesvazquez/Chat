package chat_cliente_servidor;

import static chat_cliente_servidor.Servidor.sockets;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

public class LeerCliente extends Thread {
    
    private final boolean newUser = true;
    private final boolean notNewUser = false;
    private final Socket socket;
    public static ArrayList<LeerCliente> usuarios = new ArrayList();
    
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private final ObjectOutputStream oos;
    
    private DefaultListModel modelo = new DefaultListModel();
    
    public LeerCliente(String str, Socket socket) throws IOException{
        super(str);
        this.socket = socket;
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        oos = new ObjectOutputStream(socket.getOutputStream());
        usuarios.add(this);
        
        for (int i = 0; i < usuarios.size(); i++){
            usuarios.get(i).enviarMensaje(getName() + " se ha conectado");
            modelo.addElement(usuarios.get(i).getName());
        }
    }
    
    @Override
    public synchronized void run(){
        try {
            System.out.println(getName() + " conectado al puerto " + socket.getLocalPort());
            inicioSesion();
            lectura();
            actualizarLista();
            desconexion();
            
        } catch (IOException ex) {}
    }
    
    public void inicioSesion() throws IOException{
        for (int i = 0; i < usuarios.size(); i++){
            usuarios.get(i).escribirModelo(modelo);
        }
            
        sockets.put(getName(), socket);
        System.out.println("Nuevo usuario conectado: " + getName());
        System.out.println("Hay " + sockets.size() + " usuarios conectados");
    }
    
    public void enviarMensajeChat(String nombre, String mensaje) throws IOException{
        String mensajeChat;
        if (mensaje.equals("/bye")){
            dos.writeBoolean(notNewUser);
            String cierre = nombre + " ha salido de la sala de chat";
            dos.writeUTF(cierre);
        } else {
            dos.writeBoolean(notNewUser);
            mensajeChat = nombre + ": " + mensaje;
            System.out.println(mensajeChat);
            dos.writeUTF(mensajeChat);
        }
    }
    
    public void enviarMensaje(String mensaje) throws IOException{
        dos.writeBoolean(notNewUser);
        dos.writeUTF(mensaje);
    }
    
    public void lectura() throws IOException{
        String mensaje = "";
        while (!mensaje.equalsIgnoreCase("/bye")){
            mensaje = dis.readUTF();
            if (mensaje.equals("/clear")){
                enviarMensajeChat(getName(), mensaje);
            } else {
                for (int i = 0; i < usuarios.size(); i++){
                    usuarios.get(i).enviarMensajeChat(getName(), mensaje);
                }
            }
        }
    }
    
    public void actualizarLista() throws IOException{
        usuarios.remove(this);
        modelo = new DefaultListModel();
        for (int i = 0; i < usuarios.size(); i++){
            modelo.addElement(usuarios.get(i).getName());
        }
            
        for (int i = 0; i < usuarios.size(); i++){
            usuarios.get(i).escribirModelo(modelo);
        }
    }
    
    public void escribirModelo(DefaultListModel modelo) throws IOException{
        dos.writeBoolean(newUser);
        oos.writeObject(modelo);
    }
    
    
    
    public void desconexion(){
        System.out.println(getName() + " se ha desconectado");
        sockets.remove(getName());
        if (sockets.isEmpty()){
            System.out.println("Ningun usuario conectado");
        } else {
            System.out.println("Hay " + sockets.size() + " usuarios conectados");
        }
    }
}