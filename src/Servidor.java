import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Servidor {
    public static void main(String[] args) {
        MarcoServidor mimarco = new MarcoServidor();
        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class MarcoServidor extends JFrame implements Runnable{

    private JTextArea areatexto;

    public MarcoServidor() {
        setBounds(1200,300,280,350);
        JPanel milamina = new JPanel();
        milamina.setLayout(new BorderLayout());
        areatexto = new JTextArea();
        milamina.add(areatexto, BorderLayout.CENTER);
        add(milamina);
        setVisible(true);
        Thread mihilo = new Thread(this);
        mihilo.start();
    }

    @Override
    public void run() {

        try {
            ServerSocket servidor = new ServerSocket(9999);

            String nick, ip, mensaje;

            ArrayList<String> listaIP = new ArrayList<String>();

            PaqueteEnvio paquete_recibido;

            while (true) {
                Socket misocket = servidor.accept();
                ObjectInputStream paquete_datos = new ObjectInputStream(misocket.getInputStream());
                paquete_recibido = (PaqueteEnvio) paquete_datos.readObject();
                nick = paquete_recibido.getNick();
                ip = paquete_recibido.getIp();
                mensaje = paquete_recibido.getMensaje();
                if(!mensaje.equals(" Online ")) {
                    areatexto.append("\n" + nick + ": " + mensaje + " para " + ip);
                    Socket enviaDestinatario = new Socket(ip, 9090);
                    ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
                    paqueteReenvio.writeObject(paquete_recibido);
                    enviaDestinatario.close();
                    misocket.close();
                }else{
                    InetAddress localizacion = misocket.getInetAddress();
                    String IpRemota = localizacion.getHostAddress();
                    listaIP.add(IpRemota);
                    paquete_recibido.setIps(listaIP);
                    for (String z:listaIP){
                        System.out.println("Array: " + z);

                        Socket enviaDestinatario = new Socket(z, 9090);

                        ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());

                        paqueteReenvio.writeObject(paquete_recibido);

                        enviaDestinatario.close();

                        misocket.close();
                    }
                }
            }
            } catch(IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
    }
}














































