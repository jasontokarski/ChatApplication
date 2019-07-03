package com.capgemini.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.capgemini.model.ChatMessage;
import com.capgemini.model.ChatMessage.MessageType;
import com.capgemini.service.BotService;

@Controller
public class ChatController {
	public static int numberOfUsersConnected = 0;
	public static Set<String> userList = new HashSet<>();
	
	@Autowired
	public BotService botService;
	
	@MessageMapping("/chat.sendMessage")
	@SendTo("/topic/sendMessage")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
		if(botService.checkAnswer(chatMessage.getContent())) {
			chatMessage.setType(MessageType.CORRECT);
		}
		return chatMessage;
	}
	
	@MessageMapping("/chat.correctAnswer")
	public void correctAnswer(@Payload String sender) {
		botService.sendBotMessage(sender + " answered correctly! [+10 Points]");
	}
	
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/sendMessage")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
    	userList.add(chatMessage.getSender());
    	numberOfUsersConnected++;
    	headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
    
    @MessageMapping("/chat.getUsers")
    @SendTo("/topic/getUsers")
    public Set<String> getUsers() {
    	return userList;
    }
    
    @MessageMapping("/chat.getUserCount")
    @SendTo("/topic/userCount")
    public int getUserCount() {
    	return numberOfUsersConnected;
    }
    
    @MessageMapping("/chat.doesUserExist")
    @SendTo("/topic/userExists")
	public boolean checkIfUserExists(@Payload String username) {
    	username = username.replaceAll("[\"]+", "");
    	boolean result = userList.contains(username);
    	System.out.println(result);
		return userList.contains(username);
	}
}
