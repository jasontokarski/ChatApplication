'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#username-form');
var messageForm = document.querySelector('#message-form');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#message-area');
var userListArea = document.querySelector('#user-list-area');
var userList = document.querySelector('#user-list');
var displayUserExistsMessage = document.querySelector('#username-exists');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var clientInfo = null;
var userExistsSubscription = null;
var username = null;
var userExists = 'false';
var hasAttemptedConnection = false;
var colorSwitch = 1;

usernameForm.addEventListener('submit', joinChat, true)
messageForm.addEventListener('submit', sendMessage, true)

window.onload = function() {
	var userConnectedSocket = new SockJS('/users');
	clientInfo = Stomp.over(userConnectedSocket);
	clientInfo.connect({}, initialConnect, onError);
};

function initialConnect() {
	clientInfo.subscribe('/topic/userCount', numberOfUsersConnected);
	userExistsSubscription = clientInfo.subscribe('/topic/userExists', doesUserExist);
	clientInfo.send("/app/chat.getUserCount", {}, "");
}

function numberOfUsersConnected(payload){
	var currentlyConnected = document.querySelector('#numberOfUsers');
	console.log('There are currently: ' + payload.body + ' users connected');
	while(currentlyConnected.firstChild) {
		currentlyConnected.removeChild(currentlyConnected.firstChild);
	}
	currentlyConnected.appendChild(document.createTextNode(payload.body));		
}

function doesUserExist(payload) {
	userExists = payload.body;
}

function joinChat(event) {
	hasAttemptedConnection = true;
	username = document.querySelector('#name').value.trim();
	clientInfo.send("/app/chat.doesUserExist", {}, JSON.stringify(username));	
	
	setTimeout(function() {
		if(username && userExists == 'false') {
			userExistsSubscription.unsubscribe();
			var socket = new SockJS('/ws');
			stompClient = Stomp.over(socket);
			stompClient.connect({}, onConnected, onError);
		}else if (hasAttemptedConnection) {
			var errorMessage = document.createElement('span');
			while (displayUserExistsMessage.firstChild){
				displayUserExistsMessage.removeChild(displayUserExistsMessage.firstChild);
			}
			errorMessage.setAttribute('style', 'color:red;');
			errorMessage.appendChild(document.createTextNode('Username is in use, please try a different username.'));
			displayUserExistsMessage.appendChild(errorMessage);
		}
	}, 100);
	
	event.preventDefault();
}

function onConnected() {
	usernamePage.classList.add('hidden');
	chatPage.classList.remove('hidden');
	stompClient.subscribe('/topic/sendMessage', onMessageReceived);
	stompClient.subscribe('/topic/getUsers', updateUsers);
	stompClient.send("/app/chat.addUser", {}, JSON.stringify({sender: username, type: 'JOIN', content: ''}));
	stompClient.send("/app/chat.getUsers", {}, "");
	
	connectingElement.classList.add('hidden');
}

function onError(error) {
	connectingElement.textContent = 'Could not connect to server';
	connectingElement.style.color = 'red';
}

function sendMessage(event) {
	var messageContent = messageInput.value.trim();
	if(messageContent && stompClient) {
		var chatMessage = {
				sender: username,
				content: messageInput.value,
				type: 'CHAT'
		};
		
		stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
		messageInput.value = '';
	}
	event.preventDefault();
}

function onMessageReceived(payload) {
	var message = JSON.parse(payload.body);
	var messageElement = document.createElement('div');
	
	if(colorSwitch == 1) {
		messageElement.style.backgroundColor = '#FAFAFA';
		colorSwitch--;
	} else {
		messageElement.style.backgroundColor = '#F5F5F5';
		colorSwitch++;
	}
	
	if(message.type === 'JOIN') {
		messageElement.classList.add('event-message');
		messageElement.innerHTML = '<span style="color:red">' + message.sender + '</span>' +':' + ' has joined.';
	} else if (message.type ==='LEAVE') {
        messageElement.classList.add('event-message');
        messageElement.innerHTML = '<span style="color:red">' + message.sender + '</span>' +':' + ' has left.';
        message.content = '';
	}else {
        messageElement.classList.add('chat-message');
        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender + ": ");

        usernameElement.appendChild(usernameText);
        usernameElement.style.color = 'red';
        messageElement.appendChild(usernameElement);
    }


    var messageText = document.createTextNode(" " + message.content);
    messageElement.appendChild(messageText);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function updateUsers(payload) {
	payload.body = payload.body.replace(/[\[\]"]+/g, "");
	var users = payload.body.split(",");
	
 	while(userList.firstChild) {
		userList.removeChild(userList.firstChild);
	}
	
	for(var i = 0; i < users.length; i++) {
		var userItem = document.createElement('li')
		userItem.textContent = users[i];
		userList.appendChild(userItem);
	}
}
