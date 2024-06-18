let currentUserName = "익명"; // 기본 사용자 이름 설정

const stompClient = new StompJs.Client({
    webSocketFactory: () => new SockJS('/ws-chat'), // 서버 URL에 맞춰 조정하세요.
    reconnectDelay: 5000,
    debug: (str) => console.log(str),
});

stompClient.onConnect = (frame) => {
    console.log('연결됨: ' + frame);
    setConnected(true);

    // 메시지 구독
    stompClient.subscribe('/topic/message', (message) => {
        const messageObj = JSON.parse(message.body);
        showGreeting(messageObj.content, messageObj.sender.senderName);
    });

    // 채팅 기록 구독
    stompClient.subscribe('/topic/history', (history) => {
        const historyObj = JSON.parse(history.body);
        historyObj.messages.forEach(msg => {
            showGreeting(msg.content, msg.sender.senderName);
        });
    });
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
    console.log("연결 해제됨");
}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (!connected) {
        $("#conversation").hide();
    } else {
        $("#conversation").show();
    }
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
                'sender': {
                    'senderName': currentUserName,
                    // 'senderId' 및 기타 필요한 필드 추가
                }
            })
        });
        $("#content").val("");
    }
}

function showGreeting(message, senderName) {
    $("#message").append("<tr><td><b>" + senderName + ":</b> " + message + "</td></tr>");
}

// 채팅 기록 불러오기 함수
function loadHistory() {
    stompClient.publish({
        destination: "/app/history",
        body: JSON.stringify({'roomId': "Room1"}), // 조정 가능
    });
}

// 메시지 전송 함수 이름을 sendName에서 sendMessage로 변경합니다.
// 이는 함수의 실제 역할을 더 명확하게 반영합니다.
function sendMessage() {
    const messageContent = $("#content").val();
    if (messageContent && currentUserName) {
        stompClient.publish({
            destination: "/app/message",
            body: JSON.stringify({
                'content': messageContent,
                'senderId': currentUserName, // 서버의 DTO에 맞춰 senderId를 사용합니다.
                'roomId': 'Room1', // 채팅방 ID
            })
        });
        $("#content").val("");
    }
}

function setName() {
    currentUserName = $("#name").val();
    // $("#name").val("");
}

// 페이지 로드 시 이벤트 리스너 설정
// 기존 sendName 함수 호출 부분을 sendMessage로 수정합니다.
$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(connect);
    $("#disconnect").click(disconnect);
    $("#send").click(sendMessage); // sendName 대신 sendMessage를 호출합니다.
    $("#setName").click(setName);
    $("#loadHistory").click(loadHistory);
});
