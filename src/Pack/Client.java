package Pack;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

// my 채팅프로그램 실습 최종
class ChatThread2 extends Thread {
    Socket socket;
    Client client;

    ChatThread2(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            InputStream inputstream = socket.getInputStream();
            while (true) {
                byte[] data = new byte[512];
                int size = inputstream.read(data); // 블로킹
                String s = new String(data, 0, size, StandardCharsets.UTF_8);
                System.out.println(s + " 서버에서 데이터 받음");
                client.textArea.appendText(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
            client.textArea.appendText(e.toString());
        }
    }
}

public class Client extends Application {
    Socket cs;
    VBox root = new VBox();
    HBox hBox1 = new HBox();
    HBox hBox2 = new HBox();
    HBox hBox3 = new HBox();
    HBox hBox4 = new HBox();
    TextArea textArea = new TextArea();
    TextField textField = new TextField();
    TextField textField1 = new TextField();
    TextField textField2 = new TextField();
    TextField textField3 = new TextField();
    Button btn1 = new Button("서버 연결!");
    Button btn2 = new Button("방 나가기");
    Button btn3 = new Button("전송");
    Label label1 = new Label();
    Label label2 = new Label();
    Label label3 = new Label();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage arg0) {
        root.setPrefSize(500, 400); // 창 크기
        root.setSpacing(10);
        root.setPadding(new Insets(15));
        root.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        btn1.setPrefSize(100, 30);
        btn2.setPrefSize(100, 30);
        btn3.setPrefSize(50, 30);

        btn1.setDisable(true);
        btn3.setDisable(true);

        textField.setPrefWidth(400);
        textField.setPrefHeight(30);
        textField1.setPrefWidth(150);
        textField1.setText("115.22.10.42");
        textField2.setPrefWidth(150);
        textField2.setText("5001");

        textArea.setPadding(new Insets(10));

        label1.setText("서버 주소 :");
        label2.setText("포트 번호 :");
        label3.setText("닉네임 입력:");

        hBox1.setAlignment(Pos.CENTER);
        hBox1.setSpacing(10);
        hBox2.setSpacing(15);
        hBox3.setAlignment(Pos.CENTER);
        hBox3.setSpacing(15);
        hBox4.setSpacing(10);
        hBox4.setAlignment(Pos.CENTER);
        //-------------------------------------

        // 이 영역에 모든 코드가 들어감
        // 방법 2
        hBox2.getChildren().addAll(textField, btn3);//입력-전송창
        hBox3.getChildren().addAll(label1, textField1, label2, textField2); //서버주소, 포트번호
        hBox4.getChildren().addAll(label3, textField3, btn1, btn2);// 닉네임창
        root.getChildren().addAll(hBox3, hBox4, textArea, hBox2);

        textField3.setOnKeyTyped(keyEvent -> btn1.setDisable(false)); // 닉네임 입력시 서버 연결 버튼 활성화

        btn1.setOnAction(actionEvent -> { // 서버 연결 버튼
            String nickName = textField3.getText();
            cs = new Socket();
            try {
                String serverAddress = textField1.getText();
                int serverPort = Integer.parseInt(textField2.getText());
                cs.connect(new InetSocketAddress(serverAddress, serverPort));
                // 서버 연결 끝나면 닉네임 서버로 전송
                OutputStream outputStream = cs.getOutputStream();
                byte[] data = nickName.getBytes(StandardCharsets.UTF_8);
                outputStream.write(data);
            } catch (Exception e) {
                textArea.setText(e.toString());
                e.printStackTrace();
            }
            // 서버 연결 끝나면 서버로부터 응답 대기
            new ChatThread2(cs, this).start();
            btn3.setDisable(false);
        });

        btn2.setOnAction(actionEvent -> {
            try {
                OutputStream outputStream = cs.getOutputStream();
                String s = " 님이 나갔습니다.";
                byte[] data = s.getBytes(StandardCharsets.UTF_8);
                outputStream.write(data);
                textField.setText("");
            } catch (Exception e) {
                e.printStackTrace();
                textArea.setText(e.toString());
            }
        	 System.exit(0); 
        	 }); // 나가기 버튼

        
        
        textField.setOnAction(actionEvent -> { // 채팅 입력창
            try {
                OutputStream outputStream = cs.getOutputStream();
                String s = textField.getText() + "\n";
                byte[] data = s.getBytes(StandardCharsets.UTF_8);
                outputStream.write(data);
                textField.setText("");
            } catch (Exception e) {
                e.printStackTrace();
                textArea.setText(e.toString());
            }
        });

        btn3.setOnAction(actionEvent -> { // 전송 버튼
            try {
                OutputStream outputStream = cs.getOutputStream();
                String s = textField.getText() + "\n";
                byte[] data = s.getBytes(StandardCharsets.UTF_8);
                outputStream.write(data);
                textField.setText("");
            } catch (Exception e) {
                e.printStackTrace();
                textArea.setText(e.toString());
            }
        });
        //------------------------------------
        Scene scene = new Scene(root);
        arg0.setScene(scene); // 위의 설정값들을 적용
        arg0.setTitle("Client"); // 제목
        arg0.show(); // 창 띄움

    }
}




/*
class ChatThread2 extends Thread {
	   Socket socket;
	   Client client;
	   String nickName;
	   ChatThread2(Socket socket, Client client, String nickName) {
	       this.socket = socket;
	       this.client = client;
	       this.nickName = nickName;
	   }
	   @Override
	   public void run() {
	       try {
	           InputStream inputstream = socket.getInputStream();
	           while (true) {
	               byte[] data = new byte[512];
	               int size = inputstream.read(data);
	               String s = new String(data, 0, size, StandardCharsets.UTF_8);
//	                System.out.println(s + " 서버에서 데이터 받음");
	               client.textArea.appendText(s);
	           }
	       } catch (Exception e) {
	           e.printStackTrace();
	       }
	   }
	}
	public class Client extends Application {
	   Socket cs;
	   VBox root = new VBox();
	   HBox hBox1 = new HBox();
	   HBox hBox2 = new HBox();
	   HBox hBox3 = new HBox();
	   HBox hBox4 = new HBox();
	   TextArea textArea = new TextArea();
	   TextField textField = new TextField();
	   TextField textField1 = new TextField();
	   TextField textField2 = new TextField();
	   TextField textField3 = new TextField();
	   Button btn1 = new Button("서버 연결!");
	   Button btn2 = new Button("방 나가기");
	   Button btn3 = new Button("전송");
	   Button btn4 = new Button("창닫기");
	   Label label1 = new Label();
	   Label label2 = new Label();
	   Label label3 = new Label();
	   public static void main(String[] args) {
	       launch();
	   }
	   @Override
	   public void start(Stage arg0) throws Exception {
	       root.setPrefSize(500, 400); // 창 크기
	       root.setSpacing(10);
	       root.setPadding(new Insets(15));
//	       root.setBackground(new Background(new Background(Color.LIGHTGRAY, null, null)));
	       btn1.setPrefSize(100, 30);
	       btn2.setPrefSize(100, 30);
	       btn4.setPrefSize(100, 30);
	       btn3.setPrefSize(50, 30);
	       btn1.setDisable(true);
	       btn3.setDisable(true);
	       textField.setPrefWidth(400);
	       textField.setPrefHeight(30);
	       textField1.setPrefWidth(150);
	       textField1.setText("115.22.10.42");
	       textField2.setPrefWidth(150);
	       textField2.setText("5001");
	       textArea.setPadding(new Insets(10));
	       label1.setText("서버 주소 :");
	       label2.setText("포트 번호 :");
	       label3.setText("닉네임 입력:");
	       hBox1.setAlignment(Pos.CENTER);
	       hBox1.setSpacing(10);
	       hBox2.setSpacing(15);
	       hBox3.setAlignment(Pos.CENTER);
	       hBox3.setSpacing(15);
	       hBox4.setSpacing(10);
	       hBox4.setAlignment(Pos.CENTER);
	       //-------------------------------------
	       // 이 영역에 모든 코드가 들어감
	       // 방법 2
	       hBox2.getChildren().addAll(textField, btn3);//입력-전송창
	       hBox3.getChildren().addAll(label1, textField1, label2, textField2); //서버주소, 포트번호
	       hBox4.getChildren().addAll(label3, textField3, btn1, btn2);// 닉네임창
	       root.getChildren().addAll(hBox3, hBox4, textArea, hBox2);
	       textField3.setOnKeyTyped(keyEvent -> btn1.setDisable(false));
	       btn1.setOnAction(actionEvent -> {
	           String nickName = textField3.getText()+"\n";
	           cs = new Socket();
	           try {
	               String serverAddress = textField1.getText();
	               int serverPort = Integer.parseInt(textField2.getText());
	               cs.connect(new InetSocketAddress(serverAddress, serverPort));
	               OutputStream outputStream = cs.getOutputStream();
	               byte[] data = nickName.getBytes(StandardCharsets.UTF_8);
	               outputStream.write(data);
	           } catch (Exception e) {
	               textArea.setText(e.toString());
	               e.printStackTrace();
	           }
	           new ChatThread2(cs, this, nickName).start();
	           btn3.setDisable(false);
	       });
	       btn2.setOnAction(actionEvent -> System.exit(0));
	       textField.setOnAction(actionEvent -> {
	           try {
	               OutputStream outputStream = cs.getOutputStream();
	               String s = textField.getText()+"\n";
	               byte[] data = s.getBytes(StandardCharsets.UTF_8);
	               outputStream.write(data);
	               textField.setText("");
	           } catch (Exception e) {
	               e.printStackTrace();
	           }
	       });
	       btn3.setOnAction(actionEvent -> {
	           try {
	               OutputStream outputStream = cs.getOutputStream();
	               String s = textField.getText()+"\n";
	               byte[] data = s.getBytes(StandardCharsets.UTF_8);
	               outputStream.write(data);
	               textField.setText("");
	           } catch (Exception e) {
	               e.printStackTrace();
	           }
	       });
	       //------------------------------------
	       Scene scene = new Scene(root);
	       arg0.setScene(scene); // 위의 설정값들을 적용
	       arg0.setTitle("Client"); // 제목
	       arg0.show(); // 창 띄움
	   }
	}
*/
	
/*
// my 채팅프로그램 실습)
class ChatThread2 extends Thread{
	Socket socket;
	Client client;
	ChatThread2(Socket socket, Client client){
		this.socket = socket;
		this.client = client;
	}
	@Override
	public void run() {
		try {
			InputStream inputstream = socket.getInputStream();
			while(true) {
				//블로킹
				byte[] data = new byte[512];
				
				int size = inputstream.read(data);
				String s = new String(data, 0, size, StandardCharsets.UTF_8);
				client.textArea.appendText(s + "\n");
				//System.out.println(s + " 서버에서 데이터 받음");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}

public class Client extends Application{
	Socket cs;
	Button btn1 = new Button("서버에 연결");
	Button btn2 = new Button("버튼2");
	TextArea textArea = new TextArea();
	@Override
	public void start(Stage arg0) throws Exception {
		VBox root = new VBox();
		root.setPrefSize(400, 300); // 창 크기
		root.setSpacing(15);
		//-------------------------------------
		// 이 영역에 모든 코드가 들어감
		// 방법 2
		
		
		
		btn1.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				cs = new Socket();
				
				try {
					cs.connect(new InetSocketAddress("localhost", 5001));
					//cs.connect(new InetSocketAddress("220.119.22.165", 5001));
					//cs.connect(new InetSocketAddress("61.83.118.69", 5001));
					//cs.connect(new InetSocketAddress("220.119.26.166", 5001));
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("서버에 접속함");
				new ChatThread2(cs, Client.this).start();
			}
		});
		
		btn2.setOnAction(new EventHandler<ActionEvent>() {
			int count = 0;
			@Override
			public void handle(ActionEvent arg0) {
				try {
					OutputStream outputStream = cs.getOutputStream();
					String s ="apple" + count++;
					byte[] data = s.getBytes(StandardCharsets.UTF_8);
					outputStream.write(data);
					System.out.println("데이터 보냄");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		root.getChildren().addAll(btn1, btn2, textArea);
		//-------------------------------------
		Scene scene = new Scene(root);
		arg0.setScene(scene); // 위의 설정값들을 적용
		arg0.setTitle("Client"); // 제목
		arg0.show(); // 창 띄움

	}
	
	public static void main(String[] args) {
		launch();
	}
}
*/


/*
//ex75 UI 에서 통신
class ConnectThread2 extends Thread{
	@Override
	public void run() {
		try {
			Socket cs = new Socket();
			
			//cs.connect(new InetSocketAddress("localhost", 5001));
			cs.connect(new InetSocketAddress("220.119.22.165", 5001));
			System.out.println("접속 완료");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
public class Client extends Application{
	Socket cs;
	@Override
	public void start(Stage arg0) throws Exception {
		VBox root = new VBox();
		root.setPrefSize(400, 300); // 창 크기
		root.setSpacing(15);
		//-------------------------------------
		// 이 영역에 모든 코드가 들어감
		// 방법 2
		Button btn1 = new Button("서버에 연결");
		Button btn2 = new Button("버튼2");
		
		root.getChildren().addAll(btn1, btn2);
		
		btn1.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				cs = new Socket();
				
				try {
					//cs.connect(new InetSocketAddress("localhost", 5001));
					cs.connect(new InetSocketAddress("220.119.22.165", 5001));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		btn2.setOnAction(new EventHandler<ActionEvent>() {
			int count = 0;
			@Override
			public void handle(ActionEvent arg0) {
				try {
					OutputStream outputStream = cs.getOutputStream();
					String s ="apple" + count++;
					byte[] data = s.getBytes();
					outputStream.write(data);
					System.out.println("데이터 보냄");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		//-------------------------------------
		Scene scene = new Scene(root);
		arg0.setScene(scene); // 위의 설정값들을 적용
		arg0.setTitle("Client"); // 제목
		arg0.show(); // 창 띄움

	}
	
	public static void main(String[] args) {
		launch();
	}
}
*/


/*
//ex 74 server - client 통신
public class Client {

	public static void main(String[] args) {
		System.out.println("Client Start");
		
		
		try {
			Socket cs = new Socket();
			
			System.out.println("숫자를 입력하면 접속을 시도합니다.");
			new Scanner(System.in).nextInt();
			// cs.connect(new InetSocketAddress("localhost", 5001));
			cs.connect(new InetSocketAddress("220.119.22.165", 5001));
			System.out.println("접속 완료");
			System.out.println("숫자를 입력하면 데이터를 전송합니다.");
			new Scanner(System.in).nextInt();
			
			OutputStream outputStream = cs.getOutputStream();
			String s ="apple";
			byte[] data = s.getBytes();
			outputStream.write(data);
			System.out.println("데이터 보냄");
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		new Scanner(System.in).nextInt();
		System.out.println("Client End");
	}

}
*/