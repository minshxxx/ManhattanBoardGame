package main;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class MenuView{

	Container contentPane = new Container();

	JButton singleBtn = new JButton("�̱� �÷���");
	JButton createBtn = new JButton("��Ƽ ���� ����");
	JButton joinBtn = new JButton("��Ƽ ���� ����");
	JButton configBtn = new JButton("ȯ�� ����");
	JButton exitBtn = new JButton("�� ��");
	
	@SuppressWarnings("serial")
	static JPanel thisPanel = new JPanel(){
		Image background=new ImageIcon(MHProject.class.getResource("../Resource/menuViewBackground.png")).getImage();
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(background, 0, 0, null);
		}
	};
	
	
	public MenuView(Container contentPane) {

		this.contentPane = contentPane;

		thisPanel.setVisible(true);
		thisPanel.setLayout(null);
		
		thisPanel.setSize(1280, 720);
		
		contentPane.add(thisPanel);
		
		/* ��ư ���� */
		singleBtn.setBounds(540, 300, 200, 50);
		createBtn.setBounds(540, 375, 200, 50);
		joinBtn.setBounds(540, 450, 200, 50);
		configBtn.setBounds(540, 525, 200, 50);
		exitBtn.setBounds(540, 600, 200, 50);

		thisPanel.add(singleBtn);
		thisPanel.add(createBtn);
		thisPanel.add(joinBtn);
		thisPanel.add(configBtn);
		thisPanel.add(exitBtn);

		singleBtn.addMouseListener(new singleMouseListener());
		configBtn.addMouseListener(new configMouseListener());
		exitBtn.addMouseListener(new exitMouseListener());
		
	}

	class singleMouseListener extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			thisPanel.setVisible(false);
			SingleView.thisPanel.setVisible(true);
		}
	}
	class configMouseListener extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			thisPanel.setVisible(false);
			ConfigView.thisPanel.setVisible(true);
		}
	}
	class exitMouseListener extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			System.exit(0);
		}
	}
}
