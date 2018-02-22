package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.StringBuilder;
import java.util.Random;

public class Server implements Runnable {

	private int port;
	private volatile boolean running;

	public Server(int port) {
		this.port = port;
		this.running = false;
	}

	public void startServer() {
		Socket request;
		PrintWriter response;
		ServerSocket server;
		try {
			server = new ServerSocket(this.port);
		} catch (Exception e) {
			ServerConsole.getInstance().print("Failed to start the server on port " + new Integer(this.port).toString());
			ServerConsole.getInstance().print(e.toString());
			return;
		}
		this.running = true;
		ServerConsole.getInstance().print("Server is started on port " + new Integer(this.port).toString());
		while (this.running) {
			try {
				request = server.accept();
				response = new PrintWriter(request.getOutputStream(), true); // true for autoflush on println
			} catch (Exception e) {
				ServerConsole.getInstance().print("Failed to accept connection.");
				continue;
			}
			try {
				this.serviceClient(request, response);
			} catch (Exception e) {
				ServerConsole.getInstance().print("Failed to service client.");
				ServerConsole.getInstance().print(e.toString());
				try {
					response.println("ERROR: " + e.toString());
				} catch (Exception err) {
					// give up trying to get the client feedback.
				}
			} finally {
				try {
					request.close();
				} catch (Exception e) {
					ServerConsole.getInstance().print("Failed to close client connection.");
				}
			}
		}
		ServerConsole.getInstance().print("Server is Stopped");
	}

	private void serviceClient(Socket request, PrintWriter response) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
		int channel = Integer.parseInt(in.readLine());
		Random rand = new Random();
		while (this.running) {
			if (in.ready() && "stop".equals(in.readLine())) {
				break;
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < channel; i++) {
				sb.append(new Integer(rand.nextInt(ServerConstants.DEFAULT_MAX - ServerConstants.DEFAULT_MIN + 1) + ServerConstants.DEFAULT_MIN).toString());
				if (i < channel - 1) {
					// Don't append a trailing space on the last entry.
					sb.append(" ");
				}
			}
			String resp = sb.toString();
			response.println(resp);
			ServerConsole.getInstance().print("Sent response: " + resp);
			Thread.sleep(ServerConstants.DEFAULT_FREQ * 1000);
		}
		ServerConsole.getInstance().print("Stopping number server");
	}

	@Override
	public void run() {
		startServer();
	}

	public synchronized void stop() {
		this.running = false;
	}

	public static Server createServerThread() {
		Server server = new Server(ServerConstants.PORT_NUMBER);
		new Thread(server).start();
		return server;

	}

}
