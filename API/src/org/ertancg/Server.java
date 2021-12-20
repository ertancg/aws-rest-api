package org.ertancg;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class Server {
	private int serverPort;
	private ServerSocket serverSocket;
	
	private DateTimeFormatter dtf;
	
	/** This constructor starts a server on the desired port.
	 * 
	 * @param port is the port number.
	 */
	public Server(int port){
		this.serverPort = port;
		this.dtf = DateTimeFormatter.ofPattern("HH:mm:ss:ms");
		try {
			this.serverSocket = new ServerSocket(serverPort);
			printLog("Server started on " + InetAddress.getLocalHost().getCanonicalHostName(), 0);
		} catch (IOException e) {
			printLog("Could not create a server.", -1);
			e.printStackTrace();
		}
	}
	
	/** This method listens and accepts clients and assigns them to a thread.
	 * 
	 */
	public void startServer(){
		while(true){
			Socket socketIn = null;
			Socket socketOut = null;
			try{
				socketIn = serverSocket.accept();
				socketOut = serverSocket.accept();
				printLog("Successfully connected with client .", 1);
			}catch(IOException e){
				printLog("Could not connect with the client.", -1);
				e.printStackTrace();
			}
			new Thread( new ServerThread(socketIn, socketOut)).start();
			
		}
		
		
	}
	/** This method closes the socket to disconnect from the client.
	 * 
	 */
	public void disconnect(){
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			printLog("Could not disconnect from the client.", -1);
			e.printStackTrace();
		}
	}
	/** This method prints the desired logging messages.
	 * 
	 * @param msg is the message to be logged.
	 * @param error is the value to determine the type of the log. -1 for error, 1 for normal logs.
	 */
	public void printLog(String msg, int error){
		LocalTime now = LocalTime.now();
		String time = dtf.format(now);
		if(error < 0){
			System.out.println("["+ time +"][Server][ERROR]: " + msg);
		}else{
			System.out.println("["+ time +"][Server][LOG]: " + msg);
		}
		
	}
}
