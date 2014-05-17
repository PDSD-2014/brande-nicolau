package ro.pub.cs.thinkit.game;

import java.util.ArrayList;

public class Question {

	private String category;
	private int difficulty;
	private String question;
	private String wa1;
	private String wa2;
	private String wa3;
	private String ca;

	public Question(int difficulty, String category, String question, String wa1, String wa2, String wa3, String ca) {
		this.difficulty = difficulty;
		this.category = category;
		this.question = question;
		this.wa1 = wa1;
		this.wa2 = wa2;
		this.wa3 = wa3;
		this.ca = ca;
	}

	public ArrayList<String> getAnswers() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(ca);
		list.add(wa1);
		list.add(wa2);
		list.add(wa3);
		return list;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getWa1() {
		return wa1;
	}

	public void setWa1(String wa1) {
		this.wa1 = wa1;
	}

	public String getWa2() {
		return wa2;
	}

	public void setWa2(String wa2) {
		this.wa2 = wa2;
	}

	public String getWa3() {
		return wa3;
	}

	public void setWa3(String wa3) {
		this.wa3 = wa3;
	}

	public String getCa() {
		return ca;
	}

	public void setCa(String ca) {
		this.ca = ca;
	}

	@Override
	public String toString() {
		StringBuilder repr = new StringBuilder();
		repr.append(difficulty + " ");
		repr.append(category + " ");
		repr.append(question + "->");
		repr.append(wa1 + " ");
		repr.append(wa2 + " ");
		repr.append(wa3 + " ");
		repr.append(ca + " ");
		return repr.toString();
	}
}
