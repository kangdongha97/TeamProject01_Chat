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

// my ä�����α׷� �ǽ� ����
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
                int size = inputstream.read(data); // ���ŷ
                String s = new String(data, 0, size, StandardCharsets.UTF_8);
                System.out.println(s + " �������� ������ ����");
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
    Button btn1 = new Button("���� ����!");
    Button btn2 = new Button("�� ������");
    Button btn3 = new Button("����");
    Label label1 = new Label();
    Label label2 = new Label();
    Label label3 = new Label();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage arg0) {
        root.setPrefSize(500, 400); // â ũ��
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

        label1.setText("���� �ּ� :");
        label2.setText("��Ʈ ��ȣ :");
        label3.setText("�г��� �Է�:");

        hBox1.setAlignment(Pos.CENTER);
        hBox1.setSpacing(10);
        hBox2.setSpacing(15);
        hBox3.setAlignment(Pos.CENTER);
        hBox3.setSpacing(15);
        hBox4.setSpacing(10);
        hBox4.setAlignment(Pos.CENTER);
        //-------------------------------------

        // �� ������ ��� �ڵ尡 ��
        // ��� 2
        hBox2.getChildren().addAll(textField, btn3);//�Է�-����â
        hBox3.getChildren().addAll(label1, textField1, label2, textField2); //�����ּ�, ��Ʈ��ȣ
        hBox4.getChildren().addAll(label3, textField3, btn1, btn2);// �г���â
        root.getChildren().addAll(hBox3, hBox4, textArea, hBox2);

        textField3.setOnKeyTyped(keyEvent -> btn1.setDisable(false)); // �г��� �Է½� ���� ���� ��ư Ȱ��ȭ

        btn1.setOnAction(actionEvent -> { // ���� ���� ��ư
            String nickName = textField3.getText();
            cs = new Socket();
            try {
                String serverAddress = textField1.getText();
                int serverPort = Integer.parseInt(textField2.getText());
                cs.connect(new InetSocketAddress(serverAddress, serverPort));
                // ���� ���� ������ �г��� ������ ����
                OutputStream outputStream = cs.getOutputStream();
                byte[] data = nickName.getBytes(StandardCharsets.UTF_8);
                outputStream.write(data);
            } catch (Exception e) {
                textArea.setText(e.toString());
                e.printStackTrace();
            }
            // ���� ���� ������ �����κ��� ���� ���
            new ChatThread2(cs, this).start();
            btn3.setDisable(false);
        });

        btn2.setOnAction(actionEvent -> {
            try {
                OutputStream outputStream = cs.getOutputStream();
                String s = " ���� �������ϴ�.";
                byte[] data = s.getBytes(StandardCharsets.UTF_8);
                outputStream.write(data);
                textField.setText("");
            } catch (Exception e) {
                e.printStackTrace();
                textArea.setText(e.toString());
            }
        	 System.exit(0); 
        	 }); // ������ ��ư

        
        
        textField.setOnAction(actionEvent -> { // ä�� �Է�â
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

        btn3.setOnAction(actionEvent -> { // ���� ��ư
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
        arg0.setScene(scene); // ���� ���������� ����
        arg0.setTitle("Client"); // ����
        arg0.show(); // â ���

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
//	                System.out.println(s + " �������� ������ ����");
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
	   Button btn1 = new Button("���� ����!");
	   Button btn2 = new Button("�� ������");
	   Button btn3 = new Button("����");
	   Button btn4 = new Button("â�ݱ�");
	   Label label1 = new Label();
	   Label label2 = new Label();
	   Label label3 = new Label();
	   public static void main(String[] args) {
	       launch();
	   }
	   @Override
	   public void start(Stage arg0) throws Exception {
	       root.setPrefSize(500, 400); // â ũ��
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
	       label1.setText("���� �ּ� :");
	       label2.setText("��Ʈ ��ȣ :");
	       label3.setText("�г��� �Է�:");
	       hBox1.setAlignment(Pos.CENTER);
	       hBox1.setSpacing(10);
	       hBox2.setSpacing(15);
	       hBox3.setAlignment(Pos.CENTER);
	       hBox3.setSpacing(15);
	       hBox4.setSpacing(10);
	       hBox4.setAlignment(Pos.CENTER);
	       //-------------------------------------
	       // �� ������ ��� �ڵ尡 ��
	       // ��� 2
	       hBox2.getChildren().addAll(textField, btn3);//�Է�-����â
	       hBox3.getChildren().addAll(label1, textField1, label2, textField2); //�����ּ�, ��Ʈ��ȣ
	       hBox4.getChildren().addAll(label3, textField3, btn1, btn2);// �г���â
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
	       arg0.setScene(scene); // ���� ���������� ����
	       arg0.setTitle("Client"); // ����
	       arg0.show(); // â ���
	   }
	}
*/
	
/*
// my ä�����α׷� �ǽ�)
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
				//���ŷ
				byte[] data = new byte[512];
				
				int size = inputstream.read(data);
				String s = new String(data, 0, size, StandardCharsets.UTF_8);
				client.textArea.appendText(s + "\n");
				//System.out.println(s + " �������� ������ ����");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}

public class Client extends Application{
	Socket cs;
	Button btn1 = new Button("������ ����");
	Button btn2 = new Button("��ư2");
	TextArea textArea = new TextArea();
	@Override
	public void start(Stage arg0) throws Exception {
		VBox root = new VBox();
		root.setPrefSize(400, 300); // â ũ��
		root.setSpacing(15);
		//-------------------------------------
		// �� ������ ��� �ڵ尡 ��
		// ��� 2
		
		
		
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
				System.out.println("������ ������");
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
					System.out.println("������ ����");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		root.getChildren().addAll(btn1, btn2, textArea);
		//-------------------------------------
		Scene scene = new Scene(root);
		arg0.setScene(scene); // ���� ���������� ����
		arg0.setTitle("Client"); // ����
		arg0.show(); // â ���

	}
	
	public static void main(String[] args) {
		launch();
	}
}
*/


/*
//ex75 UI ���� ���
class ConnectThread2 extends Thread{
	@Override
	public void run() {
		try {
			Socket cs = new Socket();
			
			//cs.connect(new InetSocketAddress("localhost", 5001));
			cs.connect(new InetSocketAddress("220.119.22.165", 5001));
			System.out.println("���� �Ϸ�");
			
			
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
		root.setPrefSize(400, 300); // â ũ��
		root.setSpacing(15);
		//-------------------------------------
		// �� ������ ��� �ڵ尡 ��
		// ��� 2
		Button btn1 = new Button("������ ����");
		Button btn2 = new Button("��ư2");
		
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
					System.out.println("������ ����");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		//-------------------------------------
		Scene scene = new Scene(root);
		arg0.setScene(scene); // ���� ���������� ����
		arg0.setTitle("Client"); // ����
		arg0.show(); // â ���

	}
	
	public static void main(String[] args) {
		launch();
	}
}
*/


/*
//ex 74 server - client ���
public class Client {

	public static void main(String[] args) {
		System.out.println("Client Start");
		
		
		try {
			Socket cs = new Socket();
			
			System.out.println("���ڸ� �Է��ϸ� ������ �õ��մϴ�.");
			new Scanner(System.in).nextInt();
			// cs.connect(new InetSocketAddress("localhost", 5001));
			cs.connect(new InetSocketAddress("220.119.22.165", 5001));
			System.out.println("���� �Ϸ�");
			System.out.println("���ڸ� �Է��ϸ� �����͸� �����մϴ�.");
			new Scanner(System.in).nextInt();
			
			OutputStream outputStream = cs.getOutputStream();
			String s ="apple";
			byte[] data = s.getBytes();
			outputStream.write(data);
			System.out.println("������ ����");
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		new Scanner(System.in).nextInt();
		System.out.println("Client End");
	}

}
*/