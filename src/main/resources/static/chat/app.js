let currentUserName = "익명"; // 기본 사용자 이름 설정
let currentWorkSpaceId = "Room1"; // 기본 워크스페이스 ID 설정
let jwtToken = ""; // JWT 토큰 변수
let connectionPath = "/ws-chat"; // 기본 연결 경로
let subscriptionPath = `/topic/message/${currentWorkSpaceId}`; // 기본 구독 경로
let publishPath = `/app/message/${currentWorkSpaceId}`; // 기본 전송 경로

let stompClient = null; // STOMP 클라이언트

// SockJS 및 STOMP 클라이언트 연결
function connect() {
    if (!jwtToken || !connectionPath) {
        alert("JWT 토큰과 연결 경로를 설정해주세요.");
        return;
    }

    const socket = new SockJS(connectionPath); // 연결 경로에 맞게 SockJS 설정
    stompClient = Stomp.over(socket);

    stompClient.connect({ Authorization: `Bearer ${jwtToken}` }, (frame) => {
        console.log('연결됨: ' + frame);
        setConnected(true);

        // 구독 경로에서 메시지 수신
        stompClient.subscribe(subscriptionPath, (message) => {
            const messageObj = JSON.parse(message.body);
            showGreeting(messageObj.content, messageObj.sender.senderName);
        });

        // 사용자 큐에서 채팅 기록을 수신
        stompClient.subscribe('/user/queue/history', (history) => {
            const historyObj = JSON.parse(history.body);
            historyObj.messages.forEach(msg => {
                showGreeting(msg.content, msg.sender.senderName);
            });
        });
    }, (error) => {
        console.error("연결 실패:", error);
    });
}

// 연결 해제 함수
function disconnect() {
    if (stompClient) {
        stompClient.disconnect(() => {
            console.log("연결 해제됨");
            setConnected(false);
        });
    }
}

// 연결 상태를 관리하는 함수
function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    $("#conversation").toggle(connected);
    if (!connected) {
        $("#message").html("");
    }
}

// 메시지 전송 함수
function sendMessage() {
    const messageContent = $("#content").val();
    if (messageContent && currentUserName) {
        stompClient.send(publishPath, {}, JSON.stringify({
            'content': messageContent,
            'senderId': currentUserName,
            'roomId': currentWorkSpaceId,
        }));
        $("#content").val("");
    }
}

// 메시지를 화면에 표시하는 함수
function showGreeting(message, senderName) {
    $("#message").append(`<tr><td><b>${senderName}:</b> ${message}</td></tr>`);
}

// 채팅 기록 불러오기 함수
function loadHistory() {
    stompClient.send("/app/history", {}, JSON.stringify({ 'roomId': currentWorkSpaceId }));
}

// 사용자 이름 설정 함수
function setName() {
    currentUserName = $("#name").val();
}

// JWT 토큰 설정 함수
function setJwtToken() {
    jwtToken = $("#jwt").val();
}

// 워크스페이스 ID 설정 함수
function setWorkSpaceId() {
    currentWorkSpaceId = $("#workspaceId").val();
}

// 연결 경로 설정 함수
function setConnectionPath() {
    connectionPath = $("#connectionPath").val();
}

// 구독 경로 설정 함수
function setSubscriptionPath() {
    subscriptionPath = $("#subscriptionPath").val();
}

// 전송 경로 설정 함수
function setPublishPath() {
    publishPath = $("#publishPath").val();
}

// 페이지 로드 시 이벤트 리스너 설정
$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(connect);
    $("#disconnect").click(disconnect);
    $("#send").click(sendMessage);
    $("#setName").click(setName);
    $("#loadHistory").click(loadHistory);
    $("#setJwt").click(setJwtToken);
    $("#setWorkspaceId").click(setWorkSpaceId);
    $("#setConnectionPath").click(setConnectionPath);
    $("#setSubscriptionPath").click(setSubscriptionPath);
    $("#setPublishPath").click(setPublishPath);
});
