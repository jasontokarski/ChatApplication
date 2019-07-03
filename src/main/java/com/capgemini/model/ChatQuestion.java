package com.capgemini.model;

public class ChatQuestion {
	private Long id;
	private String question;
	private String answer;
	public ChatQuestion() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ChatQuestion(String question, String answer) {
		super();
		this.question = question;
		this.answer = answer;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
