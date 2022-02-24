import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Client {

    public static void main(String[] args) {
        MarcoCliente mimarco = new MarcoCliente();
        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class MarcoCliente extends JFrame {
    public MarcoCliente() {
        setBounds(600,300,280,350);
        LaminaMarcoCliente milamina = new LaminaMarcoCliente();
        add(milamina);
        setVisible(true);
        addWindowListener(new EnvioOnline());
    }
}

//--------------------ENVIO SEÑAL ONLINE ---------------------
class  EnvioOnline extends WindowAdapter{

    public void windowOpened(WindowEvent e){

        try{

            Socket misocket = new Socket("192.168.18.1", 9999);

            PaqueteEnvio datos = new PaqueteEnvio();

            datos.setMensaje(" Online ");

            ObjectOutputStream paquete_datos = new ObjectOutputStream(misocket.getOutputStream());

            paquete_datos.writeObject(datos);

            misocket.close();

        }catch (Exception ex){


        }
    }
}
//-------------------------------------
class LaminaMarcoCliente extends JPanel implements Runnable{

    private JTextField campo1; //nick, ip;
    private  JLabel nick; //A on es posa el nom
    private JComboBox ip; //Part de la ip amb opcions
    private JButton miboton;
    private JTextArea campochat;

    public LaminaMarcoCliente() {
        //Per poder modificar el nom
        //nick= new JTextField(5);
        //Part per fer el nom fix
        String nick_usuario = JOptionPane.showInputDialog("Nick: ");
        JLabel n_nick = new JLabel("Nick: ");
        add(n_nick);
        nick = new JLabel();
        nick.setText(nick_usuario);
        add(nick);
        //Part per fer el nom fix
        //Part per Opcions de la ip i que les guardi
        JLabel texto = new JLabel("Online: ");
        add(texto);
        ip = new JComboBox();
       /* ip.addItem("Usuario 1");
        ip.addItem("Usuario 2");
        ip.addItem("Usuario 3"); */
        //ip.addItem("192.168.210.1");
        //ip.addItem("192.168.210.2");
        add(ip);
        //Part per Opcions de la ip i que les guardi
        //Part a on surten els missatges
        campochat=new JTextArea(12,20);
        add(campochat);
        campo1 = new JTextField(20);
        add(campo1);
        miboton = new JButton("Enviar");
        //Part a on surten els missatges
        EnviarTexto mievento = new EnviarTexto();
        miboton.addActionListener(mievento);

        add(miboton);

        Thread mihilo = new Thread(this);
        mihilo.start();

    }

private class EnviarTexto implements ActionListener{

        //Executa apretant el boto d'enviar
    @Override
    public void actionPerformed(ActionEvent e) {
        //System.out.println(campo1.getText());

        //Escriura en el client el mateix missatge perque sembli un chat
        campochat.append("\n" + campo1.getText());

        try {
            Socket misocket = new Socket("192.168.210.1",9999);

            PaqueteEnvio datos = new PaqueteEnvio();

            datos.setNick(nick.getText());
            //Sense combobox
            //datos.setIp(ip.getText());
            //Per el combobox
            datos.setIp(ip.getSelectedItem().toString());

            datos.setMensaje(campo1.getText());

            ObjectOutputStream paquete_datos = new ObjectOutputStream(misocket.getOutputStream());

            paquete_datos.writeObject(datos);

            misocket.close();
            /*DataOutputStream flujo_salida = new DataOutputStream(misocket.getOutputStream());
            flujo_salida.writeUTF(campo1.getText());
            flujo_salida.close();*/

        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
        //ex.printStackTrace(ex.getMessage()); NO VA
    }
    }
}

    @Override
    public void run() {

        try {

            ServerSocket servidor_cliente = new ServerSocket(9090);

            Socket cliente;

            PaqueteEnvio paqueteRecibido;

            while (true){

                cliente = servidor_cliente.accept();

                ObjectInputStream flujoentrada = new ObjectInputStream(cliente.getInputStream());

                paqueteRecibido = (PaqueteEnvio) flujoentrada.readObject();

                if(!paqueteRecibido.getMensaje().equals(" Online ")){
                    campochat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());
                }else{
                    //Comprovar si funciona:::  campochat.append("\n" + paqueteRecibido.getIps());
                    ArrayList<String> IpsMenu = new ArrayList<String>();

                    IpsMenu=paqueteRecibido.getIps();

                    ip.removeAllItems();

                    for(String z: IpsMenu){
                        ip.addItem(z);

                    }
                }
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}

class PaqueteEnvio implements Serializable {

    private String nick, ip, mensaje;

    private ArrayList<String> Ips;

    public ArrayList<String> getIps() {
        return Ips;
    }

    public void setIps(ArrayList<String> ips) {
        Ips = ips;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}