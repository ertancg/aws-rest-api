package org.ertancg;
public class Main {

	public static void main(String[] args) {
		Server s = new Server(Integer.parseInt(args[0]));
		s.startServer();
	}

}
