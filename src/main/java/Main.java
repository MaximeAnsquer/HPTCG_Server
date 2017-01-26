import hptcg.Player;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {

  static ServerSocket serverSocket;
  static ArrayList<Player> players = new ArrayList<Player>();
  static Socket player1;
  static Socket player2;
  static Player activePlayer;
  static Player inactivePlayer;
  static Object receivedObject;
  static InputStream inputStreamPlayer1;
  static InputStream inputStreamPlayer2;
  static ObjectInputStream objectInputStreamPlayer1;
  static ObjectInputStream objectInputStreamPlayer2;
  static ObjectOutputStream objectOutputStreamPlayer2;
  static int port;

  public static void main(String[] args) {

    if(System.getenv("PORT") == null) {
      port = 2017;
    } else {
      port = Integer.parseInt(System.getenv("PORT"));
    }
    System.out.println("Port: " + port);

    createServerSocket();

    acceptNewPlayer();
    acceptNewPlayer();
    setActivePlayer();

    while(true) {
      waitForActivePlayerToSendObject();
      sendObjectToInactivePlayer();
      changeActivePlayer();
    }
  }

  private static void changeActivePlayer() {
    if (activePlayer == players.get(0)) {
      activePlayer = players.get(1);
      inactivePlayer = players.get(0);
    } else {
      activePlayer = players.get(0);
      inactivePlayer = players.get(1);
    }
  }

  private static void sendObjectToInactivePlayer() {
    try {
      System.out.println("Sending played card to inactive player...");
      inactivePlayer.getObjectOutputStream().writeObject(receivedObject);
      System.out.println("Done.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void setActivePlayer() {
    activePlayer = players.get(0);
    inactivePlayer = players.get(1);
    try {
      activePlayer.getObjectOutputStream().writeObject(true);
      inactivePlayer.getObjectOutputStream().writeObject(false);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void acceptNewPlayer() {
    System.out.println("Waiting for a player to connect...");
    try {
      players.add(new Player(serverSocket.accept()));
      System.out.println("New player connected!");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void waitForActivePlayerToSendObject() {
    try {
      System.out.println("Waiting for " + activePlayer + " to play.");
      receivedObject = activePlayer.getObjectInputStream().readObject();
      System.out.println("Player played " + receivedObject.toString() );
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }


  private static void createServerSocket() {
    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
