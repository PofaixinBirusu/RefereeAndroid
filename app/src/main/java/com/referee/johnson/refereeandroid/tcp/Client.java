package com.referee.johnson.refereeandroid.tcp;

import android.util.Log;
import android.widget.Toast;

import com.referee.johnson.refereeandroid.interaction.ClientInteraction;
import com.referee.johnson.refereeandroid.utils.RefereeApplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private RefereeSocket refereeSocket;

    public static Client getInstance() {
        return ClientInstanceHolder.clientInstance;
    }

    static class ClientInstanceHolder {
        public static final Client clientInstance = new Client();
    }

    public boolean connect(String ip, int port) {
        if (refereeSocket == null) {
            refereeSocket = new RefereeSocket(ip, port);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("TCP_connect", "start");
                refereeSocket.connect();
            }
        }).start();
        int allTime = 3000, sleepTime = 100;
        while (allTime > 0) {
            if(isConnection()) {
                Log.d("TCP_connect", "connect success");
                return true;
            }
            Log.d("TCP_connect", "connecting");
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            allTime -= sleepTime;
        }
        return false;
    }

    public boolean isConnection() {
        if (refereeSocket == null) {
            return false;
        }
        return refereeSocket.isbConnected();
    }

    public void send(String data) {
        refereeSocket.send(data);
    }

    public void setClientInteraction(ClientInteraction clientInteraction) {
        refereeSocket.setClientInteraction(clientInteraction);
    }

    class RefereeSocket {

        private String ipAddress;
        private int port;

        private DataOutputStream dataOutput;
        private DataInputStream dataInput;
        private Socket socket;
        private boolean bConnected = false;

        private ClientInteraction clientInteraction;

        public RefereeSocket (String ip, int port) {
            ipAddress = ip;
            this.port = port;
        }

        public void connect() {
            try {
                socket = new Socket(ipAddress, port);
                dataOutput = new DataOutputStream(socket.getOutputStream());
                dataInput = new DataInputStream(socket.getInputStream());
                bConnected = true;

                while (bConnected) {
                    String data = dataInput.readUTF();
                    Log.d("TCP_connect", data);
                    if (clientInteraction != null) {
                        clientInteraction.toDo(data);
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close ();
            }
        }

        private void close () {
            bConnected = false;
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                    dataOutput.close();
                    dataInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void send(String str) {
            try {
                dataOutput.writeUTF(str);
                dataOutput.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean isbConnected() {
            return bConnected;
        }

        public void setClientInteraction(ClientInteraction clientInteraction) {
            this.clientInteraction = clientInteraction;
        }
    }
}
