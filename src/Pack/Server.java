package Pack;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;






// my ä�����α׷� �ǽ� ����
class ClientThread extends Thread{
	Socket socket;
	Server server;
	static String ns;
	LinkedList<Socket> chat = new LinkedList<Socket>();
	ClientThread(LinkedList<Socket> chat, Socket socket, Server server){
		this.socket = socket;
		this.chat = chat;
		this.server = server;
	}
	@Override
	public void run() {
		try {
			InputStream inputstream = socket.getInputStream();
			byte[] name = new byte[32];
			int nsize = inputstream.read(name);
			ns = new String(name, 0, nsize, StandardCharsets.UTF_8);

			while(true) {
				//���ŷ
				byte[] data = new byte[512];
				
				int size = inputstream.read(data);
				String s = new String(data, 0, size, StandardCharsets.UTF_8);
				System.out.println(s + " ������ ����");
				new ChatThread(chat, s, ns).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			server.textArea.appendText(ns + " ���� �������ϴ�.\n");
		}
	}
}
class ChatThread extends Thread{
	String s;
	LinkedList<Socket> chat = new LinkedList<Socket>();
	ChatThread(LinkedList<Socket> chat, String s, String ns){
		this.chat = chat;
		this.s = ns+" : "+s;
	}
	@Override
	public void run() {
		
		for (Socket socket2 : chat) {
			//System.out.println(chat2);
			try {
				
				System.out.println(socket2.getInetAddress() + " ������");
				OutputStream outputStream = socket2.getOutputStream();
				byte[] data = s.getBytes(StandardCharsets.UTF_8);//�ѱ�
				outputStream.write(data);
				System.out.println("������ ����");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
class ConnectThread extends Thread{
	Server server;
	ConnectThread(Server server){
		this.server = server;
	}
	@Override
	public void run() {
		LinkedList<Socket> chat = new LinkedList<Socket>();
		try {
			ServerSocket mss = new ServerSocket();
			System.out.println("���� ���� ���� ����");
			//mss.bind(new InetSocketAddress("localhost", 5001));
			mss.bind(new InetSocketAddress(InetAddress.getLocalHost(), 5001));
			System.out.println("���ε� �Ϸ�");
			
			while(true) {
				// ���ŷ
				Socket ss = mss.accept();
				chat.add(ss);
				//System.out.println(ss.getInetAddress() + " ������");
				server.textArea.appendText(ss.getInetAddress() + " ���� �����߽��ϴ�.\n");
				//��ũ�帮��Ʈ ������
				new ClientThread(chat, ss, server).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
public class Server extends Application{
	Button btn1 = new Button("���� ����");
	TextArea textArea = new TextArea();
	@Override
	public void start(Stage arg0) throws Exception {
		VBox root = new VBox();
		root.setPrefSize(400, 300); // â ũ��
		root.setSpacing(15);
		//-------------------------------------
		// �� ������ ��� �ڵ尡 ��
		// ��� 2
		
		root.getChildren().addAll(btn1, textArea);
		
		btn1.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				new ConnectThread(Server.this).start();
			}
		});
		
		//-------------------------------------
		Scene scene = new Scene(root);
		arg0.setScene(scene); // ���� ���������� ����
		arg0.setTitle("Server"); // ����
		arg0.show(); // â ���

	}

	public static void main(String[] args) {
		launch();
	}
}




/*
// ex75 UI ���� ���
class ClientThread extends Thread{
	Socket socket;
	ClientThread(Socket socket){
		this.socket = socket;			

	}
	@Override
	public void run() {
		try {
			InputStream inputstream = socket.getInputStream();
			while(true) {
				//���ŷ
				byte[] data = new byte[512];
				
				int size = inputstream.read(data);
				String s = new String(data, 0, size);
				System.out.println(s + "������ ����");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
class ConnectThread extends Thread{
	@Override
	public void run() {
		try {
			ServerSocket mss = new ServerSocket();
			System.out.println("���� ���� ���� ����");
			mss.bind(new InetSocketAddress("localhost", 5001));
			//mss.bind(new InetSocketAddress(InetAddress.getLocalHost(), 5001));
			System.out.println("���ε� �Ϸ�");
			
			while(true) {
				// ���ŷ
				Socket ss = mss.accept();
				System.out.println(ss.getInetAddress() + " ������");
				new ClientThread(ss).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
public class Server extends Application{
	@Override
	public void start(Stage arg0) throws Exception {
		VBox root = new VBox();
		root.setPrefSize(400, 300); // â ũ��
		root.setSpacing(15);
		//-------------------------------------
		// �� ������ ��� �ڵ尡 ��
		// ��� 2
		Button btn1 = new Button("���� ����");
		Button btn2 = new Button("��ư2");
		
		root.getChildren().addAll(btn1, btn2);
		
		btn1.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				new ConnectThread().start();
			}
		});
		
		//-------------------------------------
		Scene scene = new Scene(root);
		arg0.setScene(scene); // ���� ���������� ����
		arg0.setTitle("Server"); // ����
		arg0.show(); // â ���

	}

	public static void main(String[] args) {
		launch();
	}
}
*/


/*
//ex 74 server - client ���
public class Server {

	public static void main(String[] args) {
		System.out.println("Server Start");
		try {
			// ������ ���� ����
			ServerSocket mss = new ServerSocket();
			System.out.println("���� ���� ���� ����");
			// ���� IP�� ��Ʈ��ȣ �Ҵ�
			// ������ ���ε� IP�ּҿ��� ���� IP�ּҸ� 
			// ����ϸ� �ȵ� ������ǻ�Ͱ� �ٲ� �� �ֱ⶧��
			// mss.bind(new InetSocketAddress("localhost", 5001));
			mss.bind(new InetSocketAddress(InetAddress.getLocalHost(), 5001));
			System.out.println("���ε� �Ϸ�");
			// ���� ���ŷ
			Socket ss = mss.accept();
			System.out.println(ss.getInetAddress() + " ������");
			InputStream inputstream = ss.getInputStream();
			
			//���ŷ
			byte[] data = new byte[512];
			
			int size = inputstream.read(data);
			String s = new String(data, 0, size);
			System.out.println(s + "������ ����");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		new Scanner(System.in).nextInt();
		System.out.println("Server End");
	}

}
*/