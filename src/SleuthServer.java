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
		catch(Exception e) {
			//TODO send the error message to the client and close
		}
	}
	// instance variables
	private ServerSocket serverSocket;
	private BufferedReader input = null;
	private OutputStream output = null;
	// image capturing (can probably be moved into the method)
	private VideoCapture capture;
	private Mat image;
	
	public SleuthServer() throws IOException {
		serverSocket = new ServerSocket(7777);
		
	}
	
	public void takePicture() {
		image = new Mat();
		capture = new VideoCapture(0);
		setResolution();
		capture.read(image);
		
		MatOfByte buffer = new MatOfByte();
		Imgcodecs.imencode(".jpg", image, buffer);
		
		//byte[] imageData = imageData = buffer.toArray();
		//Imgcodecs.imwrite("test.jpg", image);
		
		//TODO send the image back to the client
		
		// only keep the capture open for as long as it's needed
		capture.release();
	}

	private void setResolution() {
		//TODO find some way to dynamically set the camera resolution depending on the device
		capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 720.0);
		capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 1280.0);
	}

	@Override
	public void run() {
		try {server();}
		catch(IOException e) { 
			//TODO notify the client and close
		}
	}
	
	
	private void server() throws IOException {
		boolean isRunning = true;
		// block until a connection is made by the client
		Socket clientSocket = serverSocket.accept();
		// get the input and output streams from the client
		input = new BufferedReader (new InputStreamReader(clientSocket.getInputStream()));
		output = clientSocket.getOutputStream();
		writeOut("Connected to host. Please enter a command:\r\n");
		while(isRunning) {
			executeCommands(input, output);
			
		}
	}

	private void executeCommands(BufferedReader input, OutputStream output) {
		
		
	}
	
	private void writeOut(String out) throws IOException {
		byte[] bytes = out.getBytes();
		output.write(bytes);
	}
}
