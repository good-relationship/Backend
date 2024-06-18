'use strict';

var isInitiator;
var room = prompt("방 이름을 입력하세요:");


const wsUrl = "ws://localhost:8080/ws-chat";
const client = new StompJs.Client({
    brokerURL: wsUrl,
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000
});
client.onConnect = function (frame) {
    console.log('클라이언트 메시지: STOMP 연결 완료');

    if (room !== "") {
        console.log('클라이언트 메시지: 방 ' + room + '에 참가 요청');
        client.subscribe('/topic/' + room, function (message) {
            const payload = JSON.parse(message.body);

            if (payload.type === 'created') {
                isInitiator = true;
                console.log('방 ' + payload.room + '이 생성되었습니다. 클라이언트 ID: ' + payload.id);
            } else if (payload.type === 'full') {
                console.log('클라이언트 메시지: 방 ' + payload.room + '이 가득 찼습니다 :^(');
            } else if (payload.type === 'joined') {
                isInitiator = false;
                console.log('방 ' + payload.room + '에 참가했습니다. 클라이언트 ID: ' + payload.id);
            } else if (payload.type === 'log') {
                console.log.apply(console, payload.data);
            }
        });

        sendMessage({
            type: 'create or join',
            data: room
        });
    }
};

// STOMP 연결 종료 시 수행할 작업
client.onDisconnect = function (frame) {
    console.log('클라이언트 메시지: STOMP 연결이 종료되었습니다.');
};

// 메시지 전송을 위한 함수
function sendMessage(message) {
    client.publish({
        destination: '/app/message',
        body: JSON.stringify(message)
    });
}

// STOMP 클라이언트 연결 시작
client.activate();
