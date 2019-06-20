package com.capgemini.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.capgemini.model.ChatMessage;

@Controller
public class ChatController {
	
	public static List<String> userList = new ArrayList<>();
	
	@MessageMapping("/chat.sendMessage")
	@SendTo("/topic/public")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
		return chatMessage;
	}
	
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        userList.add(chatMessage.getSender());
        return chatMessage;
    }
    
    @MessageMapping("/chat.getUsers")
    @SendTo("/topic/users")
    public List<String> getUsers() {
    	return userList;
    }
    
    @MessageMapping("/chat.getUserCount")
    @SendTo("/topic/userCount")
    public int getUserCount() {
    	return userList.size();
    }
}
