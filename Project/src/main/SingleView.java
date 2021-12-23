package main;


import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import model.City;
import model.User;

public class SingleView{

	static public JPanel thisPanel = new JPanel();
	
	private Container contentPane = new Container();
	private City[] city = new City[6];
	private JPanel[] spotText = new JPanel[54];
	private JPanel[] cityPanel = new JPanel[6];
	private JLabel[][] spotPanel = new JLabel[6][9];
	private JLabel[] playerLabel = new JLabel[4];
	private JLabel[] Block_Card = new JLabel[4];
	private String[] imageURL =
			{ "../Resource/PlayerRed.png", "../Resource/PlayerBlue.png", "../Resource/PlayerYellow.png", "../Resource/PlayerGreen.png"};
	
	private JPanel mapPanel;
	private JPanel gameLogPanel;
	private JPanel mainBoardPanel;
	private JPanel turnTimePanel;
	private JPanel PlayingUiPanel;
	private JPanel WaitingUiPanel;
	private JPanel BuildCardPanel;
	private JTextArea gameLogTextArea;
	private JScrollPane scrollPane;
	
	private JPanel haveBlockPanel;

	private JLabel TurnLight;
	
	private JLabel[] remainBlockInWaitingPanel = new JLabel[4];
	private JLabel[] remainBlockInPlayingPanel = new JLabel[4];

	private JLabel roundLabel;
	private JLabel[] userScore;
	static public JLabel[] userName;
	
	JLabel[][] eastText = new JLabel[6][9];
	JLabel[][] westText = new JLabel[6][9];
	JLabel[][] southText = new JLabel[6][9];
	JLabel[][] northText = new JLabel[6][9];
	
	private User[] player = new User[4];
	
	private Dealer dealer;
	
	int i;
	int ROUND_CNT;
	int PHASE1_BLOCK_CNT;
	int AITurnRate = 500;

	Integer[][] spotMaxNum = new Integer[6][9];
	Integer[][] spotSumNum = new Integer[6][9];
	Integer[][] spotActive = new Integer[6][9];
	
	MakeBuildingBlock block[] = new MakeBuildingBlock[6];
	MakeBuildingCard card[] = new MakeBuildingCard[4];
	
	Boolean PHASE1_CHECK;
	Boolean PHASE2_CHECK;
	Boolean AI_TURN_CHECK=false;

	Boolean PLAYER_TURN_CHECK=false;
	Boolean AI2_TURN_CHECK=false;
	Boolean AI3_TURN_CHECK=false;
	Boolean AI4_TURN_CHECK=false;
	Boolean PLAYER_FIRST_CHECK = false;
	
	Boolean[] CARD_SELECTED_CHECK = {false, false, false, false, false, false, false, false, false};
	Integer CARD_SELECTED_INDEX = null;
	Boolean[] BLOCK_SELECTED_CHECK = {false, false, false, false};
	Integer BLOCK_SELECTED_NUM = null;
	
	String temptext;
	TurnTimer time;
	
	public SingleView(Container contentPane) {

		this.contentPane = contentPane;

		thisPanel.setVisible(false);
		thisPanel.setLayout(null);
		thisPanel.setBackground(Color.darkGray);
		thisPanel.setSize(1280, 720);
		
		contentPane.add(thisPanel);

		
		player[0] = new User(MHProject.PlayerName);
		player[1] = new User("A.I");
		player[2] = new User("A.I");
		player[3] = new User("A.I");
		
		Initialize();
	}
	
	
	public SingleView() {
		// TODO Auto-generated constructor stub
	}


	public void Phase0() {
		ComputeScore1();
		ComputeScore2();
		ComputeScore3();
		if(ROUND_CNT == 4) {
			GameLog("");
			GameLog("게임 종료!");
			ComputeWinner1();
		}
		else {
			PlayerUiChange();
			Phase1();
		}
	}
	public void Phase1() {
		ROUND_CNT++;
		PHASE1_BLOCK_CNT = 0;
		PHASE1_CHECK = true;
		PHASE2_CHECK = false;
		player[0].getBuildingCard_Num().removeAll(player[0].getBuildingCard_Num());
		
		dealer.DealerCardReset();
		RoundAddCount();
		GameLog("\n　라운드 " + ROUND_CNT + ": (Phase 1) 빌딩 블록 선택 ");
		GameLog("");
		GameLog("[시스템] 빌딩 블록을 6개 선택하세요");
		for(int i=0; i<4; i++) {
			player[0].setBuildingCard_Num(i, dealer.Deal(0));
			player[1].setBuildingCard_Num(i, dealer.Deal(3));
			player[2].setBuildingCard_Num(i, dealer.Deal(2));
			player[3].setBuildingCard_Num(i, dealer.Deal(1));
		}
	}
	public void Phase2() {
		PHASE1_CHECK=false;
		GameLog("\n　라운드 " + ROUND_CNT + ": (Phase 2) 블록 설치 \n");
		GameLog("[시스템] 빌딩 카드 4개가 랜덤으로 지급되었습니다.");

		Music introMusic = new Music("Turn.mp3", false);
		introMusic.start();
		
		for(int j=0; j<6; j++)
			for(int i=0; i<9; i++)
		spotPanel[j][i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		RecyclePlayingPanel();
	}
	public void Phase3() {
		PHASE1_CHECK=false;
		PHASE2_CHECK=false;

		time.threadStop(true);
		
		GameLog("\n　" + ROUND_CNT + " 라운드 종료");
		Phase0();
	}
	public void TurnEnd() {
		// 스팟 이미지 초기화
		RecycleBestSpot();
		RecyclePlayingPanel();
		for(int i=0; i<9; i++) CARD_SELECTED_CHECK[i] = false;
		for(int i=0; i<4; i++) BLOCK_SELECTED_CHECK[i] = false;
		CARD_SELECTED_INDEX = null;
		BLOCK_SELECTED_NUM = null;
		PHASE1_CHECK = false;
		AI_TURN_CHECK = true;
	}
	public void Turn() {
		AI2Thread ai2 = new AI2Thread();
		AI3Thread ai3 = new AI3Thread();
		AI4Thread ai4 = new AI4Thread();
		
		if(ROUND_CNT == 1) {
			
			
			if(PHASE2_CHECK == false ) {
				time = new TurnTimer();
				time.start();
				PlayerUiChange();
				RecyclePlayingPanel();
				Phase2();
				PHASE2_CHECK=true;
			}
			if(PLAYER_TURN_CHECK == true) {
				PLAYER_TURN_CHECK = false;
				time.threadStop(true);
				ai2.start();
			}
			else if(AI2_TURN_CHECK == true) {
				AI2_TURN_CHECK = false;
				ai3.start();
			}
			else if(AI3_TURN_CHECK == true) {
				AI3_TURN_CHECK = false;
				ai4.start();
			}
			else if(AI4_TURN_CHECK == true) {
				AI4_TURN_CHECK = false;
				time = new TurnTimer();
				time.start();
				TurnStart();
			}
		}else if(ROUND_CNT == 2) {
			
			// 처음 턴 시작
			if(PHASE2_CHECK == false && player[0].getHave_blockNum().size() == 6 ) {
				PlayerUiChange();
				RecyclePlayingPanel();
				ai2.start();
				Phase2();
				PHASE2_CHECK=true;
			}
			
			if(AI4_TURN_CHECK == true) {
				AI4_TURN_CHECK = false;
				time = new TurnTimer();
				time.start();
				TurnStart();
			}
			if(PLAYER_TURN_CHECK == true) {
				PLAYER_TURN_CHECK = false;
				if( player[0].getHave_blockNum().size() != 0) {
					time.threadStop(true);
					ai2.start();
				}else {
					Phase3();
				}
			}
			// ai2 턴이 끝났을 때 ai3 실행
			if(AI2_TURN_CHECK == true) {
				AI2_TURN_CHECK = false;
				ai3.start();
			}
			// ai3 턴이 끝났을 때 ai4 실행
			if(AI3_TURN_CHECK == true) {
				AI3_TURN_CHECK = false;
				ai4.start();
			}
			
		}else if(ROUND_CNT == 3) {

			// 처음 턴 시작
				if(PHASE2_CHECK == false && player[0].getHave_blockNum().size() == 6 ) {
					PlayerUiChange();
					RecyclePlayingPanel();
					ai3.start();
					Phase2();
					PHASE2_CHECK=true;
				}
				
				if(PLAYER_TURN_CHECK == true) {
					PLAYER_TURN_CHECK = false;
					time.threadStop(true);
						ai2.start();
				}
				// ai3 턴이 끝났을 때 ai4 실행
				if(AI3_TURN_CHECK == true) {
					AI3_TURN_CHECK = false;
					ai4.start();
				}
				if(AI4_TURN_CHECK == true) {
					AI4_TURN_CHECK = false;
					time = new TurnTimer();
					time.start();
					TurnStart();
				}
				// ai2 턴이 끝났을 때 ai3 실행
				if(AI2_TURN_CHECK == true) {
					AI2_TURN_CHECK = false;
					if( player[0].getHave_blockNum().size() != 0) {
						ai3.start();
					}else {
						Phase3();
					}
				}
		}else if(ROUND_CNT == 4) {

			// 처음 턴 시작
				if(PHASE2_CHECK == false && player[0].getHave_blockNum().size() == 6 ) {
					PlayerUiChange();
					RecyclePlayingPanel();
					ai4.start();
					Phase2();
					PHASE2_CHECK=true;
				}
				
				if(AI4_TURN_CHECK == true) {
					AI4_TURN_CHECK = false;
					time = new TurnTimer();
					time.start();
					TurnStart();
				}
				if(PLAYER_TURN_CHECK == true) {
					PLAYER_TURN_CHECK = false;
					time.threadStop(true);
						ai2.start();
				}
				if(AI2_TURN_CHECK == true) {
					AI2_TURN_CHECK = false;
						ai3.start();
				}
				if(AI3_TURN_CHECK == true) {
					AI3_TURN_CHECK = false;
					if( player[0].getHave_blockNum().size() != 0) {
						ai4.start();
					}else {
						Phase3();
					}
				}
		}
	}
	public void TurnStart() {
		AI_TURN_CHECK = false;
		Music introMusic = new Music("Turn.mp3", false);
		introMusic.start();
		if(player[0].getHave_blockNum().size() != 0) {
			if(player[0].getBuildingCard_Num().size() < 4) {
				player[0].setBuildingCard_Num(3, dealer.Deal(0));
				GameLog("[시스템] 새 빌딩 카드를 받았습니다.");
			}
			RecyclePlayingPanel();
		}
		else {
			Phase3();
		}
		
	}

	public void RoundAddCount() {
		roundLabel.setText("Round "+ROUND_CNT);
	}

	public void RecycleBestSpot() {
		int eastNum, westNum, northNum, southNum;
		int maxNum;
		int flag;

		for(int j=0; j<6; j++) {
			for(int i=0; i<9; i++) {
				if( eastText[j][i].getText() == "")
					eastNum = 0;
				else
					eastNum = Integer.parseInt(eastText[j][i].getText());
				
				if( westText[j][i].getText() == "")
					westNum = 0;
				else
					westNum = Integer.parseInt(westText[j][i].getText());
				
				if( southText[j][i].getText() == "")
					southNum = 0;
				else
					southNum = Integer.parseInt(southText[j][i].getText());
				
				if( northText[j][i].getText() == "")
					northNum = 0;
				else
					northNum = Integer.parseInt(northText[j][i].getText());
				
					maxNum = 0;
					flag = 0;
					
					if(maxNum <= eastNum && eastNum != 0) {
						maxNum = eastNum; 
					}
					if(maxNum <= westNum && westNum != 0) {
						maxNum = westNum; 
					}
					if(maxNum <= southNum && southNum != 0) {
						maxNum = southNum; 
					}
					if(maxNum <= northNum && northNum != 0) {
						maxNum = northNum; 
					}

					if(maxNum == eastNum && eastNum != 0) flag++;
					if(maxNum == westNum && westNum != 0) flag++;
					if(maxNum == southNum && southNum != 0) flag++;
					if(maxNum == northNum && northNum != 0) flag++;
					
					if(maxNum != 0 && flag==1) {
						if(maxNum == eastNum) {
							spotPanel[j][i].setIcon(new ImageIcon(getClass().getResource("/Resource/SpotBlue.png")));
							spotActive[j][i] = 2;
						}
						if(maxNum == westNum) {
							spotPanel[j][i].setIcon(new ImageIcon(getClass().getResource("/Resource/SpotGreen.png")));
							spotActive[j][i] = 4;
						}
						if(maxNum == southNum) {
							spotPanel[j][i].setIcon(new ImageIcon(getClass().getResource("/Resource/SpotYellow.png")));
							spotActive[j][i] = 3;
						}
						if(maxNum == northNum) {
							spotPanel[j][i].setIcon(new ImageIcon(getClass().getResource("/Resource/SpotRed.png")));
							spotActive[j][i] = 1;
						}
					}else {
						spotPanel[j][i].setIcon(new ImageIcon(getClass().getResource("/Resource/Spot.png")));
						spotActive[j][i] = 0;
					}
			}
		}
	}
	public void RecyclePlayingPanel() {

		BuildCardPanel.removeAll();
		haveBlockPanel.removeAll();


		for(int i=0; i<4; i++) {
			card[i] = new MakeBuildingCard();
		}
		for(int i=0; i < player[0].getBuildingCard_Num().size(); i++) {
			BuildCardPanel.add(card[i].getBuildingCard(player[0].getBuildingCard_Num().get(i)));
		}
		for(int i=0; i<6; i++) {
			block[i] = new MakeBuildingBlock();
		}

		for(int i=0; i < player[0].getHave_blockNum().size(); i++)
			haveBlockPanel.add(block[i].getBuildingBlock(player[0].getHave_blockNum().get(i)));

		BuildCardPanel.setVisible(false);
		haveBlockPanel.setVisible(false);
		haveBlockPanel.setVisible(true);
		BuildCardPanel.setVisible(true);
	}
	public void RecycleRemainBlockPanel() {
		for(int i=0; i<4; i++) {
			remainBlockInWaitingPanel[i].setText("x" + player[0].gettotal_blockNum()[i].toString());
			remainBlockInPlayingPanel[i].setText("x" + player[0].gettotal_blockNum()[i].toString());
		}
	}
	public void ResetCityInformation() {
		/* 도시 정보 초기화 */
		for(i=0; i<6; i++) city[i] = new City();
	}
	
	public void Initialize() {
		ROUND_CNT = 0;
		PHASE1_CHECK=false;
		PHASE2_CHECK=false;

		dealer = new Dealer();
		
		ResetCityInformation();
		MakeMainBoardInThisPanel();
		MakeGameLogPanelInThisPanel();
		MakePlayerUIInThisPanel();
		MakeTimerBarInMainBoard();
		MakeMapPanelInMainBoard();
		MakeGameLogInGameLogPanel();
		MakeBuildingCardPanelInPlayerUI();
		MakeHaveBlockPanelInPlayerUI();
		MakePlayerInMainBoard();
		MakeCityInMainBoard();
		MakeSpotInMainBoard();

		Phase1();
	}

	
	public void MakeMainBoardInThisPanel() {

		/* 메인보드 생성 */
		mainBoardPanel = new JPanel(){
			Image background=new ImageIcon(MHProject.class.getResource("../Resource/MainBoardUI.png")).getImage();
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(background, 0, 0, null);
			}
		};
		mainBoardPanel.setLayout(null);
		mainBoardPanel.setBounds(10, 10, 900, 500);
		thisPanel.add(mainBoardPanel);
	}
	public void MakeTimerBarInMainBoard() {

		/* 타이머 바 생성 */
		turnTimePanel = new JPanel();
		turnTimePanel.setLayout(null);
		turnTimePanel.setBackground(Color.green);
		turnTimePanel.setBounds(910, 10, 20, 500);
		thisPanel.add(turnTimePanel);
		
		
	}
	public void MakeMapPanelInMainBoard(){

		/* 지도 생성 */
		mapPanel = new JPanel();
		mapPanel.setLayout(null);
		mapPanel.setBounds(160, 55, 580, 385);
		mapPanel.setBackground(new Color(0, 0, 0, 0));
		mainBoardPanel.add(mapPanel);

	}
	public void MakeGameLogPanelInThisPanel() {
		gameLogPanel = new JPanel(){
			Image background=new ImageIcon(MHProject.class.getResource("../Resource/GameLogUI.png")).getImage();
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(background, 0, 0, null);
			}
		};
		gameLogPanel.setLayout(null);
		gameLogPanel.setBackground(Color.gray);
		gameLogPanel.setBounds(930, 10, 330, 500);
		thisPanel.add(gameLogPanel);
	}
	public void MakeGameLogInGameLogPanel() {

		/* 게임 로그 TextArea */
		gameLogTextArea = new JTextArea();

		gameLogTextArea.setFont(new Font("굴림", Font.PLAIN, 12));
		gameLogTextArea.setForeground(Color.yellow);
		gameLogTextArea.setLineWrap(true);
		gameLogTextArea.setOpaque(false);
		gameLogTextArea.setEditable(false);
		gameLogTextArea.append("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		
		scrollPane = new JScrollPane(gameLogTextArea);
		scrollPane.setBounds(10, 30, 340, 462);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		gameLogPanel.add(scrollPane);
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		

		DefaultCaret caret = (DefaultCaret)gameLogTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}
	public void MakePlayerUIInThisPanel() {

		
		/* Playing UI*/
		PlayingUiPanel = new JPanel(){
			Image background=new ImageIcon(MHProject.class.getResource("../Resource/PlayerUI.png")).getImage();
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(background, 0, 0, null);
			}
		};		
		PlayingUiPanel.setLayout(null);
		PlayingUiPanel.setBounds(10, 525, 1250, 155);
		thisPanel.add(PlayingUiPanel);
		PlayingUiPanel.setVisible(false);

		for(int i=0; i<4; i++) {
			remainBlockInPlayingPanel[i] = new JLabel();
			remainBlockInPlayingPanel[i].setText("x" + player[0].gettotal_blockNum()[i].toString());
			remainBlockInPlayingPanel[i].setFont(new Font("Consola", Font.BOLD, 20));
			remainBlockInPlayingPanel[i].setForeground(Color.white);
			PlayingUiPanel.add(remainBlockInPlayingPanel[i]);
		}
		remainBlockInPlayingPanel[0].setBounds(930, 95, 40, 30);
		remainBlockInPlayingPanel[1].setBounds(1020, 95, 40, 30);
		remainBlockInPlayingPanel[2].setBounds(1105, 95, 40, 30);
		remainBlockInPlayingPanel[3].setBounds(1190, 95, 40, 30);
		
		/* Waiting UI*/
		WaitingUiPanel = new JPanel(){
			Image background=new ImageIcon(MHProject.class.getResource("../Resource/WaitingUI.png")).getImage();
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(background, 0, 0, null);
			}
		};		
		WaitingUiPanel.setLayout(null);
		WaitingUiPanel.setBounds(10, 525, 1250, 155);
		
		JPanel buildingBlockSelectPanel = new JPanel();
		buildingBlockSelectPanel.setBounds(350, 20, 370, 112);
		buildingBlockSelectPanel.setOpaque(false);
		buildingBlockSelectPanel.setLayout(new GridLayout(1, 4, 40, 0));
		WaitingUiPanel.add(buildingBlockSelectPanel);

		Block_Card[0]= new JLabel("0"){
			Image background=new ImageIcon(MHProject.class.getResource("../Resource/BuildingBlock1.png")).getImage();
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(background, 0, 0, null);
			}
		};		
		Block_Card[1] = new JLabel("1"){
			Image background=new ImageIcon(MHProject.class.getResource("../Resource/BuildingBlock2.png")).getImage();
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(background, 0, 0, null);
			}
		};		
		Block_Card[2] = new JLabel("2"){
			Image background=new ImageIcon(MHProject.class.getResource("../Resource/BuildingBlock3.png")).getImage();
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(background, 0, 0, null);
			}
		};		
		Block_Card[3] = new JLabel("3"){
			Image background=new ImageIcon(MHProject.class.getResource("../Resource/BuildingBlock4.png")).getImage();
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(background, 0, 0, null);
			}
		};		

		for(int i=0; i<4; i++) {
		Block_Card[i].setOpaque(false);
		buildingBlockSelectPanel.add(Block_Card[i]);
		Block_Card[i].addMouseListener(new SelectUseBlock());
		Block_Card[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		
		thisPanel.add(WaitingUiPanel);
		
		for(int i=0; i<4; i++) {
			remainBlockInWaitingPanel[i] = new JLabel();
			remainBlockInWaitingPanel[i].setText("x" + player[0].gettotal_blockNum()[i].toString());
			remainBlockInWaitingPanel[i].setFont(new Font("Consola", Font.BOLD, 20));
			remainBlockInWaitingPanel[i].setForeground(Color.white);
			WaitingUiPanel.add(remainBlockInWaitingPanel[i]);
		}
		remainBlockInWaitingPanel[0].setBounds(930, 95, 40, 30);
		remainBlockInWaitingPanel[1].setBounds(1020, 95, 40, 30);
		remainBlockInWaitingPanel[2].setBounds(1105, 95, 40, 30);
		remainBlockInWaitingPanel[3].setBounds(1190, 95, 40, 30);
		
		
		
	}
	public void MakeBuildingCardPanelInPlayerUI() {
		/* 빌딩카드 패널 생성 */		
		BuildCardPanel = new JPanel();
		BuildCardPanel.setLayout(new GridLayout(1, 1, 20, 0));
		BuildCardPanel.setBounds(10, 50, 340, 70);
		BuildCardPanel.setOpaque(false);
		PlayingUiPanel.add(BuildCardPanel);
	}
	public void MakeHaveBlockPanelInPlayerUI() {
		/* 빌딩블락 패널 생성 */		
		haveBlockPanel = new JPanel();
		haveBlockPanel.setBounds(450, 35, 400, 112);
		haveBlockPanel.setLayout(new GridLayout(1, 6, 5, 0));
		haveBlockPanel.setOpaque(false);
		PlayingUiPanel.add(haveBlockPanel);
		
	}
	
	
	public void MakePlayerInMainBoard() {
		/* 플레이어 생성 */
		for(i=0; i<4; i++) {
			playerLabel[i] = new JLabel(){
				Image background=new ImageIcon(MHProject.class.getResource(imageURL[i])).getImage();
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(background, 0, 0, null);
				}
			};
			playerLabel[i].setLayout(null);
			playerLabel[i].setBackground(new Color(0, 0, 0, 0));
			mainBoardPanel.add(playerLabel[i]);
		}
		playerLabel[0].setBounds(440, 10, 50, 50);
		playerLabel[1].setBounds(780, 220, 50, 50);
		playerLabel[2].setBounds(440, 450, 50, 50);
		playerLabel[3].setBounds(100, 220, 50, 50);

		roundLabel  = new JLabel("Round 1");
		userScore = new JLabel[4];
		userName = new JLabel[4];

		roundLabel.setBounds(10, 452, 100, 50);
		roundLabel.setFont(new Font("Consola", Font.BOLD, 20));
		roundLabel.setForeground(Color.white);
		mainBoardPanel.add(roundLabel);

		userName[0] = new JLabel(MHProject.PlayerName);
		userName[1] = new JLabel("A.I2");
		userName[2] = new JLabel("A.I3");
		userName[3] = new JLabel("A.I4");

		for(int i=0; i<4; i++) {
			userScore[i] = new JLabel("0");
			userScore[i].setFont(new Font("Consola", Font.BOLD, 14));
			userScore[i].setForeground(Color.white);
			mainBoardPanel.add(userScore[i]);
		}
		
		for(int i=0; i<4; i++) {
			userName[i].setFont(new Font("Consola", Font.BOLD, 14));
			userName[i].setForeground(Color.yellow);
			mainBoardPanel.add(userName[i]);
		}
		
		userScore[0].setBounds(470,  -28,  200,  100);
		userScore[1].setBounds(810, 180,  300,  100);
		userScore[2].setBounds(470,  410,  200,  100);
		userScore[3].setBounds(130, 180,  200,  100);

		
		userName[0].setBounds(470,  -10,  200,  100);
		userName[1].setBounds(810, 198,  300,  100);
		userName[2].setBounds(470,  428,  200,  100);
		userName[3].setBounds(130, 198,  200,  100);
		
	}
	public void MakeCityInMainBoard() {
		for(int i=0; i<6; i++) {
			cityPanel[i] = new JPanel(){
				Image background=new ImageIcon(MHProject.class.getResource("../Resource/City.png")).getImage();
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(background, 0, 0, null);
				}
			};
			cityPanel[i].setLayout(new GridLayout(3, 3));
			cityPanel[i].setBounds(10, 10, 180, 180);
			mapPanel.add(cityPanel[i]);
		}
		cityPanel[0].setBounds(10, 10, 180, 180);
		cityPanel[1].setBounds(200, 10, 180, 180);
		cityPanel[2].setBounds(390, 10, 180, 180);
		cityPanel[3].setBounds(10, 200, 180, 180);
		cityPanel[4].setBounds(200, 200, 180, 180);
		cityPanel[5].setBounds(390, 200, 180, 180);
	}
	public void MakeSpotInMainBoard() {
		for(Integer j=0; j<6; j++) {
			for(Integer i=0; i<9; i++) {
				spotPanel[j][i] = new JLabel(j*9 + i + ""){
					Image background=new ImageIcon(MHProject.class.getResource("../Resource/Spot.png")).getImage();
					public void paintComponent(Graphics g) {
						super.paintComponent(g);
						g.drawImage(background, 0, 0, null);
					}
				};
				
				
				spotPanel[j][i].setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				spotPanel[j][i].addMouseListener(new SelectSpotInMap());
				spotPanel[j][i].setForeground(new Color(0, 0, 0, 0));
				
				eastText[j][i] = new JLabel(city[j].getSpot()[i].getEast().toString());
				eastText[j][i].setBounds(42, 4, 50, 50);
				eastText[j][i].setFont(new Font("Consola", Font.BOLD, 16));
				eastText[j][i].setForeground(Color.white);
				spotPanel[j][i].add(eastText[j][i]);
				
				westText[j][i] = new JLabel(city[j].getSpot()[i].getWest().toString());
				westText[j][i].setBounds(10, 4, 50, 50);
				westText[j][i].setFont(new Font("Consola", Font.BOLD, 16));
				westText[j][i].setForeground(Color.white);
				spotPanel[j][i].add(westText[j][i]);
				
				southText[j][i] = new JLabel(city[j].getSpot()[i].getSouth().toString());
				southText[j][i].setBounds(26, 20, 50, 50);
				southText[j][i].setFont(new Font("Consola", Font.BOLD, 16));
				southText[j][i].setForeground(Color.white);
				spotPanel[j][i].add(southText[j][i]);
				
				northText[j][i] = new JLabel(city[j].getSpot()[i].getNorth().toString());
				northText[j][i].setBounds(26, -11, 50, 50);
				northText[j][i].setFont(new Font("Consola", Font.BOLD, 16));
				northText[j][i].setForeground(Color.white);
				spotPanel[j][i].add(northText[j][i]);
				
				spotPanel[j][i].setBackground(new Color(0, 0, 0, 0));
				spotPanel[j][i].setLayout(null);
				spotPanel[j][i].setSize(60, 60);
				cityPanel[j].add(spotPanel[j][i]);
			}
		}
	}
	
	public void ComputeScore1() {
		Integer eastNum, westNum, northNum, southNum, maxNum = 0, flag=0, xPos = 0, yPos = 0;
		String userNameString;
		for(int j=0; j<6; j++) {
			for(int i=0; i<9; i++) {

				spotSumNum[j][i] = 0;
				
				if( eastText[j][i].getText() == "")
					eastNum = 0;
				else
					eastNum = Integer.parseInt(eastText[j][i].getText());
				
				if( westText[j][i].getText() == "")
					westNum = 0;
				else
					westNum = Integer.parseInt(westText[j][i].getText());
				
				if( southText[j][i].getText() == "")
					southNum = 0;
				else
					southNum = Integer.parseInt(southText[j][i].getText());
				
				if( northText[j][i].getText() == "")
					northNum = 0;
				else
					northNum = Integer.parseInt(northText[j][i].getText());

				spotSumNum[j][i] += eastNum;
				spotSumNum[j][i] += westNum;
				spotSumNum[j][i] += southNum;
				spotSumNum[j][i] += northNum;
				
				if(spotSumNum[j][i] >= maxNum && spotSumNum[j][i] > 0) {
					maxNum = spotSumNum[j][i];
				}
			}
		}
		
		for(int j=0; j<6; j++) {
			for(int i=0; i<9; i++)
				if(spotSumNum[j][i] == maxNum) { 
					flag++;
					xPos = j;
					yPos = i;
				}
		}
		
		
		if(flag == 1) {
			GameLog("[가장 높은 빌딩] "+ userName[ spotActive[xPos][yPos]-1 ].getText() + ", "+ maxNum +"층, 3점 지급");
			player[spotActive[xPos][yPos]-1].setScore(Integer.parseInt((userScore[spotActive[xPos][yPos]-1].getText())) + 3);
			userScore[spotActive[xPos][yPos]-1].setText(player[spotActive[xPos][yPos]-1].getScore() + "");
		}
		else
			GameLog("[가장 높은 빌딩] 점수 미지급");
		GameLog("");
	}
	public void ComputeScore2() {
		Integer flag=0, cityIndex = 0, userIndex = 0, maxNum =0;
		Integer[] user_CNT = new Integer[4];
		String userNameString;
		for(int j=0; j<6; j++) {

			user_CNT[0] = 0;
			user_CNT[1] = 0;
			user_CNT[2] = 0;
			user_CNT[3] = 0;
			
			maxNum = 0;
			flag = 0;
			
			for(int i=0; i<9; i++) {
				if(spotActive[j][i] != 0)
				user_CNT[ spotActive[j][i] - 1 ]++;
			}

			if(user_CNT[0] >= maxNum) maxNum = user_CNT[0];
			if(user_CNT[1] >= maxNum) maxNum = user_CNT[1];
			if(user_CNT[2] >= maxNum) maxNum = user_CNT[2];
			if(user_CNT[3] >= maxNum) maxNum = user_CNT[3];
			
			for(int i=0; i<4; i++)
				if(user_CNT[i] == maxNum) { 
					flag++;
					cityIndex = j;
					userIndex = i;
				}
			if(flag == 1) {
				GameLog("[가장 많은 빌딩] '도시" + (cityIndex+1) + "' "+ userName[userIndex].getText() + ", "+ maxNum +"개, 2점 지급");
				player[userIndex].setScore( Integer.parseInt((userScore[userIndex].getText())) + 2 );
				userScore[userIndex].setText(player[userIndex].getScore() + "");
			}
			else
				GameLog("[가장 많은 빌딩] '도시" + (cityIndex+1) + "', 점수 미지급");
			}
		GameLog("");
			
	}
	public void ComputeScore3() {
		Integer userIndex = 0;
		Integer[] user_CNT = new Integer[4];

		user_CNT[0] = 0;
		user_CNT[1] = 0;
		user_CNT[2] = 0;
		user_CNT[3] = 0;
		
		for(int j=0; j<6; j++) {
			for(int i=0; i<9; i++) {
				if(spotActive[j][i] != 0)
				user_CNT[ spotActive[j][i] - 1 ]++;
			}
		}
		for(int i=0; i<4; i++) {
			GameLog("[소유 빌딩 갯수] " + userName[i].getText() + " " + user_CNT[i] + " 개 보유");
			player[i].setScore( Integer.parseInt( (userScore[i].getText()) ) + user_CNT[i] );
			userScore[i].setText(player[i].getScore() + "");
		}
		GameLog("");
	}
	public void ComputeWinner1() {
		Integer maxScoreNum = 0, flag=0, highBuildingNum = 0, haveBuildingNum = 0;
		Integer eastNum, westNum, northNum, southNum, maxNum = 0, xPos = 0, yPos = 0;
		Integer[] user_CNT = new Integer[4];
		List<Integer> winHighScoureUser = new ArrayList<Integer>();
		List<Integer> winHighBuildingUser = new ArrayList<Integer>();
		List<Integer> winHaveBuildingUser = new ArrayList<Integer>();
		Integer[] user_SCORE_CNT = new Integer[4];
		Integer[] user_HAVE_CNT = new Integer[4];
		String userNameString;

		for(int i=0; i<4; i++) {
			user_SCORE_CNT[i] = player[i].getScore();
		}
		
		if(user_SCORE_CNT[0] >= maxScoreNum) maxScoreNum = user_SCORE_CNT[0];
		if(user_SCORE_CNT[1] >= maxScoreNum) maxScoreNum = user_SCORE_CNT[1];
		if(user_SCORE_CNT[2] >= maxScoreNum) maxScoreNum = user_SCORE_CNT[2];
		if(user_SCORE_CNT[3] >= maxScoreNum) maxScoreNum = user_SCORE_CNT[3];
		
		for(int i=0; i<4; i++)
			if(user_SCORE_CNT[i] == maxScoreNum) { 
				winHighScoureUser.add(i);
			}
		if(winHighScoureUser.size() == 1) {
			GameLog("[시스템] 승리자는  " + maxScoreNum + "점으로 " + userName[winHighScoureUser.get(0)].getText() + "님이 승리하셨습니다.");
		}
		else {
			GameLog("[시스템] 최고점은 " + maxScoreNum + "점이지만 동점이므로 가장 높은 빌딩소유자가 승리합니다.");

			for(int j=0; j<6; j++) {
				for(int i=0; i<9; i++) {

					spotSumNum[j][i] = 0;
					
					if( eastText[j][i].getText() == "")
						eastNum = 0;
					else
						eastNum = Integer.parseInt(eastText[j][i].getText());
					
					if( westText[j][i].getText() == "")
						westNum = 0;
					else
						westNum = Integer.parseInt(westText[j][i].getText());
					
					if( southText[j][i].getText() == "")
						southNum = 0;
					else
						southNum = Integer.parseInt(southText[j][i].getText());
					
					if( northText[j][i].getText() == "")
						northNum = 0;
					else
						northNum = Integer.parseInt(northText[j][i].getText());

					spotSumNum[j][i] += eastNum;
					spotSumNum[j][i] += westNum;
					spotSumNum[j][i] += southNum;
					spotSumNum[j][i] += northNum;
					
					if(spotSumNum[j][i] >= highBuildingNum) highBuildingNum = spotSumNum[j][i];
					
				}
			}
			for(int j=0; j<6; j++)
				for(int i=0; i<4; i++)
					if(spotSumNum[j][i] == highBuildingNum) { 
						for(int k=0; k<winHighScoureUser.size(); k++) {
							if(spotActive[j][i] == winHighScoureUser.get(k)) {
								winHighBuildingUser.add(k);
							}
						}
					}
			
			if(winHighBuildingUser.size() == 1) {
				GameLog("[시스템] 승리자는  " + highBuildingNum + "층으로 " + userName[winHighBuildingUser.get(0)].getText() + "님이 승리하셨습니다.");
			}
			else {
				GameLog("[시스템] 제일 높은 빌딩은 " + highBuildingNum + "층이지만 동점이므로 가장 많은 빌딩소유자가 승리합니다.");

				for(int i=0; i<winHighBuildingUser.size(); i++) {
					user_HAVE_CNT[i] = 0;
				if(user_HAVE_CNT[i] >= maxScoreNum) maxScoreNum = user_HAVE_CNT[i];
				}
				
				for(int i=0; i<4; i++)
					if(user_HAVE_CNT[i] == maxScoreNum) { 
						flag = i;
					}
				
				GameLog("[시스템] 승리자는  " + haveBuildingNum + "개으므로 " + userName[winHighBuildingUser.get(i)].getText() + "님이 승리하셨습니다.");
			}
			GameLog("");
		}
	}
	


	public void TimeOverAutoSelect() {
		Integer blockNum, cardIndex=0, cityPos, playerCardPositonNum, eastNum, westNum, northNum, southNum, spotBlockNum, blockIndex, ranblock;
		while(true) {
			blockIndex = (int)(Math.random() * player[0].getHave_blockNum().size());
			cityPos = (int)(Math.random() * 6);
			cardIndex = (int)(Math.random() * 4);
			
			blockNum = player[0].getHave_blockNum().get(blockIndex);

			
				playerCardPositonNum = player[0].getBuildingCard_Num().get(cardIndex);
				
				if(northText[cityPos][playerCardPositonNum].getText().equals(""))
					spotBlockNum = 0;
				else
					spotBlockNum = ((Integer.parseInt(northText[cityPos][playerCardPositonNum].getText())) + blockNum);
				
				if(spotMaxNum[cityPos][playerCardPositonNum] == null || spotMaxNum[cityPos][playerCardPositonNum] <= (spotBlockNum + blockNum)){ 
						
					
					player[0].getBuildingCard_Num().remove(cardIndex);
					player[0].setBuildingCard_Num(3, dealer.Deal(3));
	
						if( eastText[cityPos][playerCardPositonNum].getText() == "")
							eastNum = 0;
						else
							eastNum = Integer.parseInt(eastText[cityPos][playerCardPositonNum].getText());
						
						if( westText[cityPos][playerCardPositonNum].getText() == "")
							westNum = 0;
						else
							westNum = Integer.parseInt(westText[cityPos][playerCardPositonNum].getText());
						
						if( southText[cityPos][playerCardPositonNum].getText() == "")
							southNum = 0;
						else
							southNum = Integer.parseInt(southText[cityPos][playerCardPositonNum].getText());
						
						if( northText[cityPos][playerCardPositonNum].getText() == "")
							northNum = 0;
						else
							northNum = Integer.parseInt(northText[cityPos][playerCardPositonNum].getText());
					
					northText[cityPos][playerCardPositonNum].setText((blockNum + 1 + eastNum) + "");
					spotMaxNum[cityPos][playerCardPositonNum] = Integer.parseInt(northText[cityPos][playerCardPositonNum].getText());

					player[0].getHave_blockNum().remove(player[0].getHave_blockNum().indexOf(blockNum));
					
					player[0].getBuildingCard_Num().remove(playerCardPositonNum);
					GameLog("[시스템] "+ (blockNum + 1) + " x 1 빌딩 블록을 쌓았습니다.");
						
					Music introMusic = new Music("tfile.mp3", false);
					introMusic.start();
					
					RecycleBestSpot();
					break;
			}
		}
	}
	public void AI2Turn() {
		Integer blockNum, cardIndex=0, cityPos, playerCardPositonNum, eastNum, westNum, northNum, southNum, spotBlockNum;
		while(true) {
			blockNum = (int)(Math.random() * 24);
			cityPos = (int)(Math.random() * 6);
			cardIndex = (int)(Math.random() * 4);

			if(blockNum < 12) blockNum = 0;
			else if(blockNum < 18) blockNum = 1;
			else if(blockNum < 22) blockNum = 2;
			else if(blockNum < 24) blockNum = 3;
			
			if( player[1].gettotal_blockNum()[blockNum] > 0) {
				playerCardPositonNum = player[1].getBuildingCard_Num().get(cardIndex);

				if(eastText[cityPos][playerCardPositonNum].getText().equals(""))
					spotBlockNum = 0;
				else
					spotBlockNum = ((Integer.parseInt(eastText[cityPos][playerCardPositonNum].getText())) + blockNum);
				
				if(spotMaxNum[cityPos][playerCardPositonNum] == null || spotMaxNum[cityPos][playerCardPositonNum] <= (spotBlockNum + blockNum)){ 
						
					player[1].gettotal_blockNum()[blockNum]--;
					
					player[1].getBuildingCard_Num().remove(0);
					player[1].setBuildingCard_Num(3, dealer.Deal(3));
	
						if( eastText[cityPos][playerCardPositonNum].getText() == "")
							eastNum = 0;
						else
							eastNum = Integer.parseInt(eastText[cityPos][playerCardPositonNum].getText());
						
						if( westText[cityPos][playerCardPositonNum].getText() == "")
							westNum = 0;
						else
							westNum = Integer.parseInt(westText[cityPos][playerCardPositonNum].getText());
						
						if( southText[cityPos][playerCardPositonNum].getText() == "")
							southNum = 0;
						else
							southNum = Integer.parseInt(southText[cityPos][playerCardPositonNum].getText());
						
						if( northText[cityPos][playerCardPositonNum].getText() == "")
							northNum = 0;
						else
							northNum = Integer.parseInt(northText[cityPos][playerCardPositonNum].getText());
					
					eastText[cityPos][playerCardPositonNum].setText((blockNum + 1 + eastNum) + "");
					spotMaxNum[cityPos][playerCardPositonNum] = Integer.parseInt(eastText[cityPos][playerCardPositonNum].getText());

					Music introMusic = new Music("tfile.mp3", false);
					introMusic.start();
					
					RecycleBestSpot();
					break;
				}
			}
		}
	}
	public void AI3Turn() {
		Integer blockNum, cardIndex=0, cityPos, playerCardPositonNum, eastNum, westNum, northNum, southNum, spotBlockNum;
		
		while(true) {
			blockNum = (int)(Math.random() * 4);
			cityPos = (int)(Math.random() * 6);
			cardIndex = (int)(Math.random() * 4);

			
			if( player[2].gettotal_blockNum()[blockNum] > 0) {

				playerCardPositonNum = player[2].getBuildingCard_Num().get(cardIndex);
				
				if(southText[cityPos][playerCardPositonNum].getText().equals(""))
					spotBlockNum = 0;
				else
					spotBlockNum = ((Integer.parseInt(southText[cityPos][playerCardPositonNum].getText())) + blockNum);
				
				if(spotMaxNum[cityPos][playerCardPositonNum] == null || spotMaxNum[cityPos][playerCardPositonNum] <= (spotBlockNum + blockNum)){ 
						
					player[2].gettotal_blockNum()[blockNum]--;
					
					player[2].getBuildingCard_Num().remove(0);
					player[2].setBuildingCard_Num(3, dealer.Deal(2));
					
						if( eastText[cityPos][playerCardPositonNum].getText() == "")
							eastNum = 0;
						else
							eastNum = Integer.parseInt(eastText[cityPos][playerCardPositonNum].getText());
						
						if( westText[cityPos][playerCardPositonNum].getText() == "")
							westNum = 0;
						else
							westNum = Integer.parseInt(westText[cityPos][playerCardPositonNum].getText());
						
						if( southText[cityPos][playerCardPositonNum].getText() == "")
							southNum = 0;
						else
							southNum = Integer.parseInt(southText[cityPos][playerCardPositonNum].getText());
						
						if( northText[cityPos][playerCardPositonNum].getText() == "")
							northNum = 0;
						else
							northNum = Integer.parseInt(northText[cityPos][playerCardPositonNum].getText());
					
					southText[cityPos][playerCardPositonNum].setText((blockNum + 1 + eastNum) + "");
					spotMaxNum[cityPos][playerCardPositonNum] = Integer.parseInt(southText[cityPos][playerCardPositonNum].getText());
					
					Music introMusic = new Music("tfile.mp3", false);
					introMusic.start();
					
					RecycleBestSpot();
					break;
				}
			}
		}
	}
	public void AI4Turn() {
		Integer blockNum, cardIndex=0, cityPos, playerCardPositonNum, eastNum, westNum, northNum, southNum, spotBlockNum;
		while(true) {
			blockNum = (int)(Math.random() * 4);
			cityPos = (int)(Math.random() * 6);
			cardIndex = (int)(Math.random() * 4);

			
			if( player[3].gettotal_blockNum()[blockNum] > 0) {

				playerCardPositonNum = player[3].getBuildingCard_Num().get(cardIndex);
				
				if(westText[cityPos][playerCardPositonNum].getText().equals(""))
					spotBlockNum = 0;
				else
					spotBlockNum = ((Integer.parseInt(westText[cityPos][playerCardPositonNum].getText())) + blockNum);
				
				if(spotMaxNum[cityPos][playerCardPositonNum] == null || spotMaxNum[cityPos][playerCardPositonNum] <= (spotBlockNum + blockNum)){ 
						
					player[3].gettotal_blockNum()[blockNum]--;
					
					player[3].getBuildingCard_Num().remove(0);
					player[3].setBuildingCard_Num(3, dealer.Deal(1));
	
						if( eastText[cityPos][playerCardPositonNum].getText() == "")
							eastNum = 0;
						else
							eastNum = Integer.parseInt(eastText[cityPos][playerCardPositonNum].getText());
						
						if( westText[cityPos][playerCardPositonNum].getText() == "")
							westNum = 0;
						else
							westNum = Integer.parseInt(westText[cityPos][playerCardPositonNum].getText());
						
						if( southText[cityPos][playerCardPositonNum].getText() == "")
							southNum = 0;
						else
							southNum = Integer.parseInt(southText[cityPos][playerCardPositonNum].getText());
						
						if( northText[cityPos][playerCardPositonNum].getText() == "")
							northNum = 0;
						else
							northNum = Integer.parseInt(northText[cityPos][playerCardPositonNum].getText());
					
						westText[cityPos][playerCardPositonNum].setText((blockNum + 1 + eastNum) + "");
					spotMaxNum[cityPos][playerCardPositonNum] = Integer.parseInt(westText[cityPos][playerCardPositonNum].getText());

					
					RecycleBestSpot();
					
					Music introMusic = new Music("tfile.mp3", false);
					introMusic.start();
					break;
				}
			}
		}
	}



	public void GameLog(String text) {
		gameLogTextArea.append("\n" + text);
		gameLogTextArea.setCaretPosition (gameLogTextArea.getDocument (). getLength ());
	}
	public void MessageBox(String text) {
		JOptionPane aa=new JOptionPane();
		aa.showMessageDialog(null, text);
	}
	
	public void PlayerUiChange() {
		if(PlayingUiPanel.isVisible() == true) {
			RecycleRemainBlockPanel();
			PlayingUiPanel.setVisible(false);
			WaitingUiPanel.setVisible(true);
		}
		else {
			RecycleRemainBlockPanel();
			PlayingUiPanel.setVisible(true);
			WaitingUiPanel.setVisible(false);
		}
	}

	class MakeBuildingCard extends JLabel {
		private JLabel[] buildingCard;
		public MakeBuildingCard() {
			// TODO Auto-generated constructor stub
			buildingCard = new JLabel[9];
			buildingCard[0] = new JLabel("0"){
				Image background=new ImageIcon(MHProject.class.getResource("../Resource/BuildingCard1.png")).getImage();
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(background, 0, 0, null);
				}
			};
			buildingCard[1] = new JLabel("1"){
				Image background=new ImageIcon(MHProject.class.getResource("../Resource/buildingCard2.png")).getImage();
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(background, 0, 0, null);
				}
			};
			buildingCard[2] = new JLabel("2"){
				Image background=new ImageIcon(MHProject.class.getResource("../Resource/buildingCard3.png")).getImage();
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(background, 0, 0, null);
				}
			};
			buildingCard[3] = new JLabel("3"){
				Image background=new ImageIcon(MHProject.class.getResource("../Resource/buildingCard4.png")).getImage();
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(background, 0, 0, null);
				}
			};
			buildingCard[4] = new JLabel("4"){
				Image background=new ImageIcon(MHProject.class.getResource("../Resource/buildingCard5.png")).getImage();
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(background, 0, 0, null);
				}
			};
			buildingCard[5] = new JLabel("5"){
				Image background=new ImageIcon(MHProject.class.getResource("../Resource/buildingCard6.png")).getImage();
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(background, 0, 0, null);
				}
			};
			buildingCard[6] = new JLabel("6"){
				Image background=new ImageIcon(MHProject.class.getResource("../Resource/BuildingCard7.png")).getImage();
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(background, 0, 0, null);
				}
			};
			buildingCard[7] = new JLabel("7"){
				Image background=new ImageIcon(MHProject.class.getResource("../Resource/BuildingCard8.png")).getImage();
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(background, 0, 0, null);
				}
			};
			buildingCard[8] = new JLabel("8"){
				Image background=new ImageIcon(MHProject.class.getResource("../Resource/BuildingCard9.png")).getImage();
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(background, 0, 0, null);
				}
			};
			for(i=0; i<9; i++) {
			buildingCard[i].setSize(100, 100);
			buildingCard[i].setAlignmentX(JLabel.CENTER);
			buildingCard[i].addMouseListener(new SelectBuildingCardItem());
			buildingCard[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			
		}
		
		public JLabel getBuildingCard(int index) {
			return buildingCard[index];
		}
	}
	class MakeBuildingBlock extends JLabel {

		
		private JLabel[] buildingBlock;
		
		public MakeBuildingBlock() {
			// TODO Auto-generated constructor stub
			buildingBlock = new JLabel[4];
			
			buildingBlock[0] = new JLabel("0"){
				Image background=new ImageIcon(MHProject.class.getResource("../Resource/BuildingBlock1.png")).getImage();
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(background, 0, 0, null);
				}
			};
			buildingBlock[1] = new JLabel("1"){
				Image background=new ImageIcon(MHProject.class.getResource("../Resource/BuildingBlock2.png")).getImage();
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(background, 0, 0, null);
				}
			};
			buildingBlock[2] = new JLabel("2"){
				Image background=new ImageIcon(MHProject.class.getResource("../Resource/BuildingBlock3.png")).getImage();
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(background, 0, 0, null);
				}
			};
			buildingBlock[3] = new JLabel("3"){
				Image background=new ImageIcon(MHProject.class.getResource("../Resource/BuildingBlock4.png")).getImage();
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(background, 0, 0, null);
				}
			};

			for(i=0; i<4; i++) {
				buildingBlock[i].setSize(70, 70);
				buildingBlock[i].setAlignmentX(JLabel.CENTER);
				buildingBlock[i].addMouseListener(new SelectBuildingBlockItem());
				buildingBlock[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			
		}
		public JLabel getBuildingBlock(int index) {
			return buildingBlock[index];
		}
	}

	class SelectUseBlock extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e) {
			JLabel source = (JLabel) e.getSource();
			Integer BlockNum = Integer.parseInt(source.getText());
			// TODO Auto-generated method stub
			if(player[0].gettotal_blockNum()[BlockNum] > 0) {
				GameLog("[시스템] " + (BlockNum+1) + " x 1 빌딩 블록을(를) 선택, 남은 블록 수:" + (5-PHASE1_BLOCK_CNT++));
				player[0].settotal_blockNum(BlockNum, player[0].gettotal_blockNum()[BlockNum] - 1);
				remainBlockInWaitingPanel[BlockNum].setText("x"+player[0].gettotal_blockNum()[BlockNum].toString());
				player[0].setHave_blockNum(BlockNum);
				
				
			}
			else {
				GameLog("[시스템] " + (BlockNum+1) + " x 1 빌딩 블록이 남아있지 않습니다.");
			}
			if(PHASE1_BLOCK_CNT == 6) {
				AI2_TURN_CHECK = false;
				Turn();
			}
		}
	}
	class SelectSpotInMap extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e) {
			JLabel source = (JLabel) e.getSource();
			Integer Spotindex = Integer.parseInt(source.getText());
			String saveSpotString = northText[Spotindex/9][Spotindex % 9].getText();
			Integer spotBlockNum;
			
			// TODO Auto-generated method stub
			if(PHASE2_CHECK == true) {
				if(BLOCK_SELECTED_NUM != null) {
					if(CARD_SELECTED_INDEX == Spotindex % 9) {

						if(saveSpotString.equals("")) saveSpotString = "0";
						spotBlockNum = (Integer.parseInt(saveSpotString) + BLOCK_SELECTED_NUM);
						
						
						if(spotMaxNum[Spotindex/9][Spotindex%9] == null || spotMaxNum[Spotindex/9][Spotindex%9] <= spotBlockNum){ 
							if(saveSpotString.equals("")) {
								northText[Spotindex/9][Spotindex % 9].setText(BLOCK_SELECTED_NUM + "");
								spotMaxNum[Spotindex/9][Spotindex%9] = BLOCK_SELECTED_NUM;
							}
							else {
								northText[Spotindex/9][Spotindex % 9].setText((BLOCK_SELECTED_NUM + Integer.parseInt(saveSpotString)) + "");
								spotMaxNum[Spotindex/9][Spotindex%9] = BLOCK_SELECTED_NUM + Integer.parseInt(saveSpotString);
							}
							player[0].getHave_blockNum().remove(player[0].getHave_blockNum().indexOf(BLOCK_SELECTED_NUM - 1));
							player[0].getBuildingCard_Num().remove(player[0].getBuildingCard_Num().indexOf(CARD_SELECTED_INDEX));
							GameLog("[시스템] "+BLOCK_SELECTED_NUM + " x 1 빌딩 블록을 쌓았습니다.");
	

							PLAYER_TURN_CHECK=true;
							TurnEnd();
							Turn();
						}else {
							MessageBox("건설 했을때 자신의 건물의 층수가 다른 플레이어보다 같거나 많아야합니다.");
						}
					}
					else
						MessageBox("플레이어1의 빌딩 건설 갯수:" + northText[Spotindex/9][Spotindex % 9].getText() +
								   "\n플레이어2의 빌딩 건설 갯수:" + eastText[Spotindex/9][Spotindex % 9].getText() +
								   "\n플레이어3의 빌딩 건설 갯수:" + southText[Spotindex/9][Spotindex % 9].getText() +
								   "\n플레이어4의 빌딩 건설 갯수:" + westText[Spotindex/9][Spotindex % 9].getText());
				}
				else {
					MessageBox(MHProject.PlayerName+"의 빌딩 건설 갯수:" + northText[Spotindex/9][Spotindex % 9].getText() +
							   "\nA.I2의 빌딩 건설 갯수:" + eastText[Spotindex/9][Spotindex % 9].getText() +
							   "\nA.I3의 빌딩 건설 갯수:" + southText[Spotindex/9][Spotindex % 9].getText() +
							   "\nA.I4의 빌딩 건설 갯수:" + westText[Spotindex/9][Spotindex % 9].getText());
				}
			}
			else {
				MessageBox("PHASE2_CHECK: " + PHASE2_CHECK);
			}
		}
	}	
	class SelectBuildingCardItem extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e) {
			JLabel source = (JLabel) e.getSource();
			Integer index = Integer.parseInt(source.getText());
			// TODO Auto-generated method stub
			if(PHASE2_CHECK == true && AI_TURN_CHECK== false ) {
				if(CARD_SELECTED_CHECK[index] == false && CARD_SELECTED_INDEX == null) {
					for(int j=0; j<6; j++)
						spotPanel[j][index].setIcon(new ImageIcon(getClass().getResource("/Resource/SpotActive.png")));
					CARD_SELECTED_CHECK[index] = true;
					CARD_SELECTED_INDEX = index;
				}else if(CARD_SELECTED_CHECK[index] == true && CARD_SELECTED_INDEX != null) {
					if(BLOCK_SELECTED_NUM != null) {
						MessageBox("선택한 빌딩 블록이 해제되어야 합니다.");
					}
					else {
						RecycleBestSpot();
						CARD_SELECTED_CHECK[index] = false;
						CARD_SELECTED_INDEX = null;
					}
	
				}else if(CARD_SELECTED_CHECK[index] == false && CARD_SELECTED_INDEX != null) {
					MessageBox("두 개 이상의 빌딩카드는 선택할 수 없습니다.");
				}
			} else {
				MessageBox("다른 사람의 턴을 기다리세요.");
			}
		}
	}
	class SelectBuildingBlockItem extends MouseAdapter{
		@Override
		public void mousePressed(MouseEvent e) {
			JLabel source = (JLabel) e.getSource();
			Integer BlockNum = Integer.parseInt(source.getText());
			// TODO Auto-generated method stub
			if(CARD_SELECTED_INDEX == null) {
				MessageBox("빌딩카드를 선택해주세요");
			}else {
				if(BLOCK_SELECTED_NUM != null) {
					GameLog("[시스템] "+BLOCK_SELECTED_NUM + " x 1 의 빌딩 블록이 해제되었습니다");
					BLOCK_SELECTED_NUM = null;
				}
				else {
					BLOCK_SELECTED_NUM = BlockNum + 1;
					GameLog("[시스템] "+BLOCK_SELECTED_NUM + " x 1 의 빌딩 블록이 선택되었습니다");
				}
			}
		}
	}

	class TurnTimer extends Thread {

		int i = 0;
		private boolean stop;
		@Override
		public void run() {
			while(!stop) {
				// TODO Auto-generated method stub
				i += MHProject.timerSpeed;
				turnTimePanel.setVisible(true);
				turnTimePanel.setBounds(910, 510 - i, 20, i);
				if(i > 490) {
					TimeOverAutoSelect();
					PLAYER_TURN_CHECK=true;
					TurnEnd();
					Turn();
					break;
				}
					try {
						sleep(40);
					}
					catch(InterruptedException e) {
						return ;
					}
			}
			turnTimePanel.setVisible(false);
		}	
		public void threadStop(boolean stop){
			this.stop = stop;
		}
	}	
	class AI2Thread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
				try {
					RecycleBestSpot();
					GameLog("[시스템] \"A.I2\"이(가) 빌딩 쌓는 중");
					sleep(AITurnRate);
					AI2Turn();
					sleep(AITurnRate);

					AI2_TURN_CHECK = true;
					Turn();
				}
				catch(InterruptedException e) {
					return ;
				}
			return ;
		}
	}	
	class AI3Thread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
				try {
					RecycleBestSpot();

					GameLog("[시스템] \"A.I3\"이(가) 빌딩 쌓는 중");
					sleep(AITurnRate);
					AI3Turn();
					sleep(AITurnRate);

					AI3_TURN_CHECK = true;
					Turn();
				}
				catch(InterruptedException e) {
					return ;
				}
			return ;
		}
	}	
	class AI4Thread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
				try {

					GameLog("[시스템] \"A.I4\"이(가) 빌딩 쌓는 중");
					sleep(AITurnRate);
					AI4Turn();
					sleep(AITurnRate);

					AI4_TURN_CHECK = true;
					Turn();
				}
				catch(InterruptedException e) {
					return ;
				}
			return ;
		}
	}
}
