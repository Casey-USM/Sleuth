/**
 * Author: Casey Pyburn
 * SeuthServer.java
 * A program that runs on a computer, takes screenshots from the webcam and sends it back to the client 
 */
// Java API imports
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

// opencv imports
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class SleuthServer implements Runnable{
	public static void main(String[] args) {
		// load opencv libraries
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// start the server
		try {
			SleuthServer sleuthServer = new SleuthServer();
			sleuthServer.run();
		}
		catch(Exception e) {System.exit(1);}
	}
	// instance variables
	private ServerSocket serverSocket;
	private BufferedReader input = null;
	private OutputStream output = null;
	private boolean isRunning = true;
	// image capturing (can probably be moved into the method)
	private VideoCapture capture;
	private Mat image;
	
	public SleuthServer() throws IOException {
		serverSocket = new ServerSocket(7777);
		
	}
	
	public void takePicture() throws IOException {
		image = new Mat();
		capture = new VideoCapture(0);
		setResolution();
		// grab an image from the camera
		capture.read(image);
		// only keep the capture open for as long as it's needed
		capture.release();
		MatOfByte buffer = new MatOfByte();
		Imgcodecs.imencode(".jpg", image, buffer);
		
		byte[] imageData = buffer.toArray();
		output.write(imageData);
		output.flush();
	}

	private void setResolution() {
		//TODO find some way to dynamically set the camera resolution depending on the device
		capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 720.0);
		capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 1280.0);
	}

	@Override
	public void run() {
		try {
			while(isRunning)
				server();
		}
		catch(Exception e) {
			if(output!=null) {
				try {
					writeOut("An error occurred");
					writeOut(e.getMessage());
				}
				catch (IOException e1) {System.exit(1);}
			}
			System.exit(1);
		}
	}
	
	
	private void server() throws IOException, InterruptedException {
		// block until a connection is made by the client
		Socket clientSocket = serverSocket.accept();
		// get the input and output streams from the client
		input = new BufferedReader (new InputStreamReader(clientSocket.getInputStream()));
		output = clientSocket.getOutputStream();
		writeOut("Connected to host. Please enter a command:\r\n");
		while(isRunning) {
			executeCommands(input, output);
			
		}
		writeOut("Exiting\r\n");
		clientSocket.close();
	}

	private void executeCommands(BufferedReader input, OutputStream output) throws IOException {
		String command = input.readLine();
		if(command.equalsIgnoreCase("exit") || command.equalsIgnoreCase("quit"))
			isRunning = false;
		else if(command.equalsIgnoreCase("snap")) {
			takePicture();
		}
		else if(command.equalsIgnoreCase("help"))
			printHelp();
		else
			printHelp();
	}
	
	private void printHelp() throws IOException {
		StringBuilder helpMessage = new StringBuilder();
		helpMessage.append("Sleuth:\r\n");
		helpMessage.append("The commands are as follows:\r\n");
		helpMessage.append("\tsnap - takes a pictuire from the remote webcam\r\n");
		helpMessage.append("\texit or quit- stops the program on the remote machine\r\n");
		helpMessage.append("\thelp- displays this message\r\n");
		writeOut(helpMessage.toString());
	}

	private void writeOut(String out) throws IOException {
		byte[] bytes = out.getBytes();
		output.write(bytes);
		output.flush();
	}
}
