package main;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ConfigView{

	Container contentPane = new Container();

	@SuppressWarnings("serial")
	static JPanel thisPanel = new JPanel();
	JTextField nameTextField;
	JButton changeNameBtn, changeSpeedBtn1, changeSpeedBtn2;
	JPanel namePane, soundPane;
	JLabel recentName;
	JButton returnToMenuBtn = new JButton("돌아가기");
	
	public ConfigView(Container contentPane) {

		this.contentPane = contentPane;

		thisPanel.setVisible(false);
		thisPanel.setLayout(null);
		
		thisPanel.setSize(1280, 720);

		contentPane.add(thisPanel);

		/* 타이틀 생성 */
		JLabel titleLabel = new JLabel("환 경 설 정");
		titleLabel.setBounds(0, 100, 1280, 50);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setFont(new Font("Consola", Font.BOLD, 30));
		thisPanel.add(titleLabel);

		namePane = new JPanel();
		recentName = new JLabel("현재이름: " + MHProject.PlayerName);
		namePane.add(new JLabel("이 름"));
		nameTextField = new JTextField();
		changeNameBtn = new JButton("변경");
		
		changeNameBtn.addMouseListener(new ChangeNameMouseListener());

		namePane.setLayout(new GridLayout(1, 3));
		namePane.setBounds(200, 200, 1000, 50);
		namePane.add(recentName);
		namePane.add(nameTextField);
		namePane.add(changeNameBtn);
		thisPanel.add(namePane);

		soundPane = new JPanel();
		changeSpeedBtn1 = new JButton("턴 속도 빠름");
		changeSpeedBtn2 = new JButton("턴 속도 느림");
		soundPane.setLayout(new FlowLayout());
		
		soundPane.setBounds(200, 400, 1000, 200);
		soundPane.add(changeSpeedBtn1);
		soundPane.add(changeSpeedBtn2);
		thisPanel.add(soundPane);

		changeSpeedBtn1.addMouseListener(new turnSpeedHigh());
		changeSpeedBtn2.addMouseListener(new turnSpeedLow());
		/* 버튼 생성 */
		returnToMenuBtn.setBounds(200, 600, 200, 50);

		thisPanel.add(returnToMenuBtn);
		returnToMenuBtn.addMouseListener(new returnBtnMouseListener());
	}

	class ChangeNameMouseListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			MHProject.PlayerName = nameTextField.getText();
			recentName.setText(MHProject.PlayerName);
			SingleView singleView = new SingleView();
			singleView.userName[0].setText(MHProject.PlayerName);
		}
		
	}
	class returnBtnMouseListener extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			thisPanel.setVisible(false);
			MenuView.thisPanel.setVisible(true);
		}

	}
	class turnSpeedHigh extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			MHProject.timerSpeed = 3;
		}

	}
	class turnSpeedLow extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			MHProject.timerSpeed = 1;
		}

	}
}
