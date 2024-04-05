// 전역 변수 선언
let currentUserName = "익명"; // 기본 사용자 이름 설정

// SockJS를 사용한 STOMP 클라이언트 설정
const stompClient = new StompJs.Client({
    webSocketFactory: () => new SockJS('http://localhost:8080/ws-chat'),
    reconnectDelay: 5000,
    debug: (str) => console.log(str),
});

// WebSocket 연결 이벤트 처리
stompClient.onConnect = (frame) => {
    console.log('Connected: ' + frame);
    setConnected(true);

    // 메시지 구독
    stompClient.subscribe('/topic/message', (message) => {
        const messageObj = JSON.parse(message.body);
        showGreeting(messageObj.content, messageObj.sender);
    });

    // 채팅 기록 구독
    stompClient.subscribe('/topic/history', (history) => {
        const messages = JSON.parse(history.body);
        messages.forEach(msg => {
            showGreeting(msg.content, msg.sender);
        });
    });
};

// STOMP 오류 이벤트 처리
stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

// 연결 및 해제 함수
function connect() {
    stompClient.activate();
}

function disconnect() {
    if (stompClient) {
        stompClient.deactivate();
    }
    setConnected(false);
    console.log("Disconnected");
}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    $("#conversation").toggle(connected);
    $("#message").html("");
}

// 메시지 전송 및 표시 함수
function sendName() {
    const messageContent = $("#content").val();
    if (messageContent && currentUserName) {
        stompClient.publish({
            destination: "/app/message",
            body: JSON.stringify({
                'content': messageContent,
                'roomId': "Room1",
                'sender': currentUserName,
            })
        });
        $("#content").val("");
    }
}

function showGreeting(message, sender) {
    $("#message").append("<tr><td><b>" + sender + ":</b> " + message + "</td></tr>");
}

// 채팅 기록 로드 함수
function loadHistory() {
    stompClient.publish({
        destination: "/app/history",
        body: JSON.stringify({'roomId': "Room1"}),
    });
}

// 사용자 이름 설정 함수
function setName() {
    currentUserName = $("#name").val() || "익명";
    $("#name").val('');
    console.log("User name set to: ", currentUserName);
}

// 이벤트 리스너 설정
$(function () {
    $("#connect").click(connect);
    $("#disconnect").click(disconnect);
    $("#send").click(sendName);
    $("#setName").click(setName);
    $("#loadHistory").click(loadHistory);
});