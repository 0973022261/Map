package com.example.inhm.mybehavior;

import android.os.Looper;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by inhm on 2016-05-14.
 */
public class ServerThread extends Thread{

    private static final int REQUEST_NUMBER = 1;
    Socket socket;                              //소켓통신을 위한 소켓 객체 변수 생성
    String host = "54.238.188.178";               //소켓 통신을 위한 서버 host 주소
    int port = 8000;                            //소켓 통신을 위한 서버 port 번호

    int RequestNumber;
    String location;
    ServerThread(int request_number, String line){
        RequestNumber = request_number;
        location = line;
    }
    public synchronized void request1() {
        synchronized (this) {
            try {
                Looper.prepare();

                Log.d("Run", "서버접속");
                socket = new Socket(host, port);

                Log.d("Run", socket.toString());
                String output;
                output = RequestNumber+","+location+"";
                ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
                outstream.writeUTF(output);
                outstream.flush();
                ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
                String input = instream.readUTF();

                instream.close();
                outstream.close();
                socket.close();
                Looper.loop();
            } catch (Exception e) {
                Log.d("Run", e.toString());
            }
        }
    }
    public void run() {
        switch(REQUEST_NUMBER){

            case 1:
                request1();
                break;
            default :
                break;
        }
    }
 }
