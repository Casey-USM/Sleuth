import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SleuthClient {
	private final int defaultPort = 7777;
	private InputStream serverInput = null;
	private BufferedWriter serverOutput = null;
	
	public static void main (String[] args) throws UnknownHostException, IOException {
		SleuthClient sc = new SleuthClient();
		sc.runClient();
	}
	
	public SleuthClient() {}
	
	public void runClient() throws UnknownHostException, IOException {
		Scanner input = new Scanner(System.in);
		System.out.println("Enter an IP to connect to:");
		
		String address = input.nextLine();
		while(!address.matches("\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b")) {
			System.out.println("Please input a valid IPv4 address");
			address = input.nextLine();
		}
		Socket socket = new Socket(address, defaultPort);
		serverOutput = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		serverInput = socket.getInputStream();
		String command = "";
		System.out.println("Connected to host");
		while(!socket.isClosed()) {
			System.out.println("reading from server");
			System.out.println(readFromServer());
			System.out.println("reading done");
			command = input.nextLine();
			System.out.println("command was "+command);
			writeToServer(command);
			if(command.equalsIgnoreCase("snap")){
				getImage();
			}
		}
		
		socket.close();
		input.close();
	}
	
	private void getImage() throws IOException {
		String fileName = Long.toString(System.currentTimeMillis()) +".jpg";
		byte[] imageData = serverInput.readAllBytes();
		OutputStream fileOut = new FileOutputStream(new File(fileName));
		fileOut.write(imageData);
		fileOut.close();
	}

	private String readFromServer() throws IOException {
		byte[] data = serverInput.readAllBytes();
		return new String(data);
	}
	
	private void writeToServer(String output) throws IOException {
		serverOutput.write(output, 0, output.length());
		serverOutput.flush();
	}
}
