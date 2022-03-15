package chat_cliente_servidor;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import javax.swing.DefaultListModel;

public class EscucharServer extends Thread{

    private DataInputStream dis;
    private final Socket socket;
    private final Chat chatUsuario;
    private ObjectInputStream ois;
    private DefaultListModel modelo;
    
    public EscucharServer(String str, Socket socket, Chat chat) throws IOException{
        super(str);
        this.socket = socket;
        this.chatUsuario = chat;
        dis = new DataInputStream(socket.getInputStream());
        ois = new ObjectInputStream(socket.getInputStream());
    }
    
    @Override
    public void run(){
        try {
            boolean newUser;
            String mensaje;
            System.out.println("hilo iniciado");
            while(true){
                newUser = dis.readBoolean();
                if (newUser == false){
                    mensaje = dis.readUTF();
                    if (chatUsuario.writeText.getText().equalsIgnoreCase("/bye")){
                        chatUsuario.chatText.append("  Has salido de la sala del chat");
                        socket.close();
                        sleep(1000);
                        System.exit(0);
                    }else if(chatUsuario.writeText.getText().equalsIgnoreCase("/clear")){
                        chatUsuario.chatText.selectAll();
                        chatUsuario.chatText.replaceSelection("");
                    }else {
                        chatUsuario.chatText.append("  " + mensaje + "\n");
                    }
                }else {
                        modelo = (DefaultListModel)ois.readObject();
                        chatUsuario.usersList.setModel(modelo);
                }
                chatUsuario.writeText.setText("");
            }
        } catch (IOException | InterruptedException | ClassNotFoundException ex) {}
    }
}