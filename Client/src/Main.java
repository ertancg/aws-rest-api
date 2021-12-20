import java.util.Random;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {
		Client c = new Client(args[0], Integer.parseInt(args[1]),Integer.parseInt(args[2]));
		System.out.println("Client started.");
		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter a mode: M/A (Manual/Auto)");
		String mode = sc.nextLine();
		if(mode.equals("M")){
			System.out.println("Please enter a name: ");
			String name = sc.nextLine();
			System.out.println("Please enter a country code: ");
			String country = sc.nextLine();
			System.out.println("Please enter points: ");
			String points = sc.nextLine();
			User u = new User(name, 0, points, country);
			c.setUser(u);
			System.out.println("Please enter the operation: 1-GET /leaderboard, 2-GET /leaderboard/{ISO_CODE},"
					+ "3-GET /user/profile/{GUID}, 4-POST /user/create, 5- POST /score/submit");
			int operation = sc.nextInt();
			switch(operation){
				case 1:
					c.startConnection();
					c.getLeaderboard();
					break;
				case 2:
					c.startConnection();
					c.getCountryLeaderboard(country);
					break;
				case 3:
					c.startConnection();
					c.getProfile();
					break;
				case 4:
					c.startConnection();
					c.createUser();
					break;
				case 5:
					System.out.println("Please enter new score: ");
					int change = sc.nextInt();
					c.startConnection();
					c.updateScore(change);
					break;
				default:
					break;
			};
		}else if(mode.equals("A")){
			System.out.println("Please enter the number of users you want to create: ");
			int limit = sc.nextInt();
			
			Random rd = new Random();
			for(int i = 0; i < limit; i++){
				User random = new User(generateRandomText(), rd.nextInt(1000), getRandomCountry(), "");
				c.setUser(random);
				c.startConnection();
				c.createUser();
			}
		}
	}
	public static String generateRandomText(){
		Random rd = new Random();
		StringBuilder st = new StringBuilder();
		String charSet = "ABCDEFGHIJKLMNOPRSTUVXY0123456789";
		for(int i=0; i<rd.nextInt(10); i++){
			st.append(charSet.charAt(rd.nextInt(charSet.length())));
		}
		return st.toString();
	}
	public static String getRandomCountry(){
		Random rd = new Random();
		String[] country = {"TR", "US", "GB", "AU", "FR"};
		return country[rd.nextInt(country.length)];
	}

}
