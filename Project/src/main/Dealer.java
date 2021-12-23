package main;


public class Dealer {
	
	private Integer[] BuildingCard;
	private int cardCheck;
	public Dealer() {
		// TODO Auto-generated constructor stub
		BuildingCard = new Integer[9];
		DealerCardReset();
	}
	
	public void DealerCardReset() {
		BuildingCard[0] = 5;
		BuildingCard[1] = 5;
		BuildingCard[2] = 5;
		BuildingCard[3] = 5;
		BuildingCard[4] = 5;
		BuildingCard[5] = 5;
		BuildingCard[6] = 5;
		BuildingCard[7] = 5;
		BuildingCard[8] = 5;
		cardCheck = 45;
	}
	public Integer Deal(int playerNum) {
		
		Integer card = (int)(Math.random() * 9);
		
		if(cardCheck == 0) {
			BuildingCard[0] = 5;
			BuildingCard[1] = 5;
			BuildingCard[2] = 5;
			BuildingCard[3] = 5;
			BuildingCard[4] = 5;
			BuildingCard[5] = 5;
			BuildingCard[6] = 5;
			BuildingCard[7] = 5;
			BuildingCard[8] = 5;
		}
		
		while(BuildingCard[card] == 0) {
			card = (int)(Math.random() * 9);
		}
		;
		
		if(BuildingCard[card] != 0) {
			BuildingCard[card]--;
		}
		
		cardCheck--;
		return (card + playerNum*2)%9;
	}
}
