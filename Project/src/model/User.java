package model;

import java.util.ArrayList;
import java.util.List;

public class User {

	private Integer[] total_blockNum;
	private List<Integer> have_blockNum;
	private List<Integer> buildingCard_Num;
	private Integer score;
	private String name;
	
	private Integer BLOCK_CNT;
	
	public User(String name) {
		// TODO Auto-generated constructor stub
		total_blockNum = new Integer[4];
		total_blockNum[0] = 12;
		total_blockNum[1] = 6;
		total_blockNum[2] = 4;
		total_blockNum[3] = 2;
		
		have_blockNum = new ArrayList<Integer>();
		buildingCard_Num = new ArrayList<Integer>();
		
		score = 0;
		BLOCK_CNT = 0;
		this.name = name;
	}

	public List<Integer> getHave_blockNum() {
		return have_blockNum;
	}
	public void setHave_blockNum(Integer block_Num) {
		this.have_blockNum.add(block_Num);
	}
	
	public List<Integer> getBuildingCard_Num() {
		return buildingCard_Num;
	}
	public void setBuildingCard_Num(int index, Integer buildingCard_Num) {
		this.buildingCard_Num.add(index, buildingCard_Num);
	}

	public Integer getBLOCK_CNT() {
		return BLOCK_CNT;
	}

	public void setBLOCK_CNT(Integer bLOCK_CNT) {
		BLOCK_CNT = bLOCK_CNT;
	}

	public Integer[] gettotal_blockNum() {
		return total_blockNum;
	}

	public void settotal_blockNum(int index, Integer block_Num) {
		this.total_blockNum[index] = block_Num;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
