// Client program

import java.io.*;
import java.net.*;

class Client {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public Client() {
        try {
            this.socket = new Socket("localhost", 7777);
            if(this.socket != null){
                this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public Object innout(Object [] userin) throws IOException, ClassNotFoundException {
        this.objectOutputStream.writeObject(userin);
        this.objectOutputStream.flush();
        System.out.println("You have sent your input to the server");
        return (Object) this.objectInputStream.readObject();
    }
}
