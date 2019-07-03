package com.capgemini.service;

import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.capgemini.model.ChatMessage;
import com.capgemini.model.ChatQuestion;
import com.capgemini.repository.BotDAO;

@Service
public class BotService {
	@Autowired
	public SimpMessageSendingOperations messagingTemplate;
	
	@Autowired
	private BotDAO botDao;
	
	public static Map<String, String> questionList = new HashMap<>();
	
    Toolkit toolkit;
    Timer timer;
    
    ChatQuestion chatQuestion;
    ChatMessage chatMessage;
    
    public static int currentQuestionIndex;
    
	public BotService() {
		chatQuestion = new ChatQuestion();
		chatMessage = new ChatMessage();
	}
	
	@PostConstruct
	public void init() {
		questionList = botDao.getAllQuestions();
		currentQuestionIndex = (int)(Math.random() * questionList.size());
		chatQuestion = findByQuestionId(currentQuestionIndex);
		toolkit = Toolkit.getDefaultToolkit();
		timer = new Timer();
		timer.schedule(new AskQuestion(), 2000, 20000);
	}
	
	public ChatQuestion findByQuestionId(int questionIndex) {
		String question = (String)questionList.keySet().toArray()[questionIndex];
		String answer = questionList.get(question);
		chatQuestion.setQuestion(question);
		chatQuestion.setAnswer(answer);
		return chatQuestion;
	}
	
	public boolean checkAnswer(String userAnswer) {
		ChatQuestion currentQuestion = findByQuestionId(currentQuestionIndex);
		if(userAnswer.equals(currentQuestion.getAnswer())) {
			return true;
		} else { 
			return false;
		}
	}
	
	public void sendBotMessage(String message) {
        ChatMessage chatMessage = new ChatMessage();
    	chatMessage.setSender("Bot");
    	chatMessage.setContent(message);
		messagingTemplate.convertAndSend("/topic/sendMessage", chatMessage);
	}
 
	class AskQuestion extends TimerTask {
		public void run() {
        	sendBotMessage(chatQuestion.getQuestion());
		}
	}
}
