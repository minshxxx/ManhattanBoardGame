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
		
		/* JFrame 설정 */
		setTitle("Manhattan Board Game - 개발중");//타이틀
		setSize(1280,720);//프레임의 크기
		setResizable(false);//창의 크기를 변경하지 못하게
		setLocationRelativeTo(null);//창이 가운데 나오게
		setLayout(null);//레이아웃 설정
		setVisible(true);//창이 보이게	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//JFrame이 정상적으로 종료되게
		
		Music introMusic = new Music("BGM.mp3", true);
		introMusic.start();
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MHProject();
	}

}
