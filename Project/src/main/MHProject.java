package main;


import javax.swing.JFrame;

public class MHProject extends JFrame{

	public static String PlayerName = "UnknownPlayer";
	public static int timerSpeed = 2;
	
	private MenuView menuView;
	private SingleView singleView;
	private ConfigView configView;
	
	public MHProject() { 
		menuView = new MenuView(getContentPane());
		singleView = new SingleView(getContentPane());
		configView = new ConfigView(getContentPane());
		
		/* JFrame ���� */
		setTitle("Manhattan Board Game - ������");//Ÿ��Ʋ
		setSize(1280,720);//�������� ũ��
		setResizable(false);//â�� ũ�⸦ �������� ���ϰ�
		setLocationRelativeTo(null);//â�� ��� ������
		setLayout(null);//���̾ƿ� ����
		setVisible(true);//â�� ���̰�	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//JFrame�� ���������� ����ǰ�
		
		Music introMusic = new Music("BGM.mp3", true);
		introMusic.start();
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MHProject();
	}

}
