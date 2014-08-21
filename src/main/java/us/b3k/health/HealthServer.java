package us.b3k.health;

import java.io.PrintStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public class HealthServer implements Container {

	public HealthServer() {}

	public static class QuickResponse {
		private String body;
		private int statusCode;

		public QuickResponse(String body, int statusCode) {
			this.body = body;
			this.statusCode = statusCode;
		}

		public String getBody() {
			return body;
		}

		public int getStatusCode() {
			return statusCode;
		}
	}

	public void handle(Request request, Response response) {
		try {
			Query query = request.getQuery();
			String endpoint = request.getPath().getPath();
			//System.out.println("PATH: " + request.getPath());
			QuickResponse qr = null;

			if (endpoint.equals("/_health")) {
				qr = new QuickResponse("OK", 200);
			}

			if (qr == null) {
				qr = new QuickResponse("Not Found:" + endpoint, 404);
			}

			// prepare response
			PrintStream body = response.getPrintStream();
			long time = System.currentTimeMillis();

			response.setValue("Content-Type", "text/plain");
			response.setValue("Server", "KakfaWebsocketHealthServer/1.0");
			response.setDate("Date", time);
			response.setDate("Last-Modified", time);

			response.setCode(qr.getStatusCode());
			body.println(qr.getBody());

			body.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void run(int port) {
		try {
			Container container = new HealthServer();
			Server server = new ContainerServer(container);
			Connection connection = new SocketConnection(server);
			SocketAddress address = new InetSocketAddress(port);

			connection.connect(address);
		} catch (IOException e) {
			System.out.println("Error starting server");
		}
	}

	public static void main(String[] list) throws Exception {
		run(8787);
   }
}
