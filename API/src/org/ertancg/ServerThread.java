package org.ertancg;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ServerThread implements Runnable{
	private Socket clientIn;
	private Socket clientOut;
	
	private DateTimeFormatter dtf;  
	
	private InputStream input;
	private OutputStream output;
	
	
	/** This constructor initializes the client Socket.
	 * 
	 * @param clientIn is the client Socket for its InputStream.
	 * @param clientOut is the client Socket for its OutputStream.
	 */
	public ServerThread(Socket clientIn, Socket clientOut){
		this.clientIn = clientIn;
		this.clientOut = clientOut;
		this.dtf = DateTimeFormatter.ofPattern("HH:mm:ss:ms");
	}
	
	/** This method is the @Overridden method of the Runnable Interface. It initializes Streams
	 * 	and then 
	 * 
	 */
	public void run() {
		
		try {
			input = this.clientIn.getInputStream();
			output = this.clientOut.getOutputStream();
			request(new BufferedReader(new InputStreamReader(input)), new PrintWriter(this.output, true));
		} catch (IOException e) {
			printLog("Could not open I/O Streams.", -1);
			e.printStackTrace();
		}finally{
			disconnect();
		}
		
	}
	/** This method recieves HTTP Request from the client and process' it and then sends a HTTP Response to the client.
	 * 
	 * @param input is the InputStream from the client.
	 * @param output is the OutputStream from the client.
	 */
	private void request(BufferedReader br, PrintWriter os){
		String line;
		String request = "";
		String header = "";
		String body = "";
		String response = "";
		
		
		
		try {
			while((line = br.readLine()) != null){
				if(line.length()<= 0) break;
				request += line;
			}
			
			if(request.length() > 0){
				header = request.substring(0, (request.indexOf("{") < 0) ? request.length() : request.indexOf("{"));
				body = request.substring((request.indexOf("{") < 0) ? 0 : request.indexOf("{"));
				
				printLog("Header of the request is: " + header, 1);
				printLog("Body of the request is: " + body, 1);
				
				RequestHandler r = new RequestHandler(header, body, getThreadID());
				response = r.Handle();
				r.finalize();
			}else{
				response = "HTTP/1.1 404 NOT FOUND\r\n";
			}
			
			
			
			os.write(response);
			os.flush();
			os.close();
			
			
		} catch (IOException e) {
			printLog("Could not read from the stream. " + this.clientIn.getInetAddress(),-1);
			e.printStackTrace();
		}
	}
	/** This method disconnects from the client.
	 * 
	 */
	public void disconnect(){
		try {
			this.input.close();
			this.output.close();
			this.clientIn.close();
			this.clientOut.close();
			printLog("Disconnected from the client.", 1);
		} catch (IOException e) {
			printLog("Could not disconnect from the client: " + this.clientIn.getInetAddress(), -1);
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
		String time = this.dtf.format(now);
		if(error < 0){
			System.out.println("["+ time +"][Server-Thread" + getThreadID() + "][ERROR]: " + msg);
		}else{
			System.out.println("["+ time +"][Server-Thread" + getThreadID() + "][LOG]: " + msg);
		}
		
	}
	/** This method @return's the ObjectID of this Object.
	 * 
	 * @return The ObjectID of the Object.
	 */
	public int getThreadID(){
		return System.identityHashCode(this) ;
	}
}
