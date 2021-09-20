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






// my 채팅프로그램 실습 최종
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
				//블로킹
				byte[] data = new byte[512];
				
				int size = inputstream.read(data);
				String s = new String(data, 0, size, StandardCharsets.UTF_8);
				System.out.println(s + " 데이터 받음");
				new ChatThread(chat, s, ns).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			server.textArea.appendText(ns + " 님이 나갔습니다.\n");
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
				
				System.out.println(socket2.getInetAddress() + " 접속함");
				OutputStream outputStream = socket2.getOutputStream();
				byte[] data = s.getBytes(StandardCharsets.UTF_8);//한글
				outputStream.write(data);
				System.out.println("데이터 보냄");
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
			System.out.println("메인 서버 소켓 생성");
			//mss.bind(new InetSocketAddress("localhost", 5001));
			mss.bind(new InetSocketAddress(InetAddress.getLocalHost(), 5001));
			System.out.println("바인딩 완료");
			
			while(true) {
				// 블로킹
				Socket ss = mss.accept();
				chat.add(ss);
				//System.out.println(ss.getInetAddress() + " 접속함");
				server.textArea.appendText(ss.getInetAddress() + " 님이 접속했습니다.\n");
				//링크드리스트 던지기
				new ClientThread(chat, ss, server).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
public class Server extends Application{
	Button btn1 = new Button("서버 오픈");
	TextArea textArea = new TextArea();
	@Override
	public void start(Stage arg0) throws Exception {
		VBox root = new VBox();
		root.setPrefSize(400, 300); // 창 크기
		root.setSpacing(15);
		//-------------------------------------
		// 이 영역에 모든 코드가 들어감
		// 방법 2
		
		root.getChildren().addAll(btn1, textArea);
		
		btn1.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				new ConnectThread(Server.this).start();
			}
		});
		
		//-------------------------------------
		Scene scene = new Scene(root);
		arg0.setScene(scene); // 위의 설정값들을 적용
		arg0.setTitle("Server"); // 제목
		arg0.show(); // 창 띄움

	}

	public static void main(String[] args) {
		launch();
	}
}




/*
// ex75 UI 에서 통신
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
				//블로킹
				byte[] data = new byte[512];
				
				int size = inputstream.read(data);
				String s = new String(data, 0, size);
				System.out.println(s + "데이터 받음");
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
			System.out.println("메인 서버 소켓 생성");
			mss.bind(new InetSocketAddress("localhost", 5001));
			//mss.bind(new InetSocketAddress(InetAddress.getLocalHost(), 5001));
			System.out.println("바인딩 완료");
			
			while(true) {
				// 블로킹
				Socket ss = mss.accept();
				System.out.println(ss.getInetAddress() + " 접속함");
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
		root.setPrefSize(400, 300); // 창 크기
		root.setSpacing(15);
		//-------------------------------------
		// 이 영역에 모든 코드가 들어감
		// 방법 2
		Button btn1 = new Button("서버 오픈");
		Button btn2 = new Button("버튼2");
		
		root.getChildren().addAll(btn1, btn2);
		
		btn1.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				new ConnectThread().start();
			}
		});
		
		//-------------------------------------
		Scene scene = new Scene(root);
		arg0.setScene(scene); // 위의 설정값들을 적용
		arg0.setTitle("Server"); // 제목
		arg0.show(); // 창 띄움

	}

	public static void main(String[] args) {
		launch();
	}
}
*/


/*
//ex 74 server - client 통신
public class Server {

	public static void main(String[] args) {
		System.out.println("Server Start");
		try {
			// 서버의 메인 소켓
			ServerSocket mss = new ServerSocket();
			System.out.println("메인 서버 소켓 생성");
			// 소켓 IP와 포트번호 할당
			// 서버측 바인딩 IP주소에는 고정 IP주소를 
			// 사용하면 안됨 서버컴퓨터가 바뀔 수 있기때문
			// mss.bind(new InetSocketAddress("localhost", 5001));
			mss.bind(new InetSocketAddress(InetAddress.getLocalHost(), 5001));
			System.out.println("바인딩 완료");
			// 접속 블로킹
			Socket ss = mss.accept();
			System.out.println(ss.getInetAddress() + " 접속함");
			InputStream inputstream = ss.getInputStream();
			
			//블로킹
			byte[] data = new byte[512];
			
			int size = inputstream.read(data);
			String s = new String(data, 0, size);
			System.out.println(s + "데이터 받음");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		new Scanner(System.in).nextInt();
		System.out.println("Server End");
	}

}
*/