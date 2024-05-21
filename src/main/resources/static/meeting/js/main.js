'use strict';

var stompClient = null;
var JWT = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZXMiOiJVU0VSIiwiZXhwIjoxNzE4ODYxNTQ0fQ.Ast8TkAPyO2lTto253avdOFHhkiC4tjBsF33DPRv4QU";
var WORKSPACE_ID = "f162c91d-ec28-4575-949e-9f1f65f81053";
var roomId;

var localStream;
var pcs = {};
var remoteStreams = {};
var pcConfig = {
    'iceServers': [{
        'urls': 'stun:stun.l.google.com:19302'
    }]
};

var localVideo = document.querySelector('#localVideo');
var remoteVideos = document.querySelector('#remoteVideo');

// Set up audio and video regardless of what devices are present.
var sdpConstraints = {
    offerToReceiveAudio: true,
    offerToReceiveVideo: true
};


function connect() {
    var jwtToken = document.getElementById("jwt").value;
    if (!jwtToken) {
        alert("Please enter a JWT token.");
        return;
    }
    var socket = new WebSocket('ws://localhost:8080/ws-chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({
        "Authorization": jwtToken
    }, function (frame) {
        console.log('Connected: ' + frame);
        navigator.mediaDevices.getUserMedia({
            audio: true,
            video: true
        })
            .then(gotStream)
            .catch(function (e) {
                alert('getUserMedia() error: ' + e.name);
            });
        stompClient.subscribe('/topic/' + WORKSPACE_ID + '/meetingRoomList', function (greeting) {
            updateRoomList(JSON.parse(greeting.body));
        });
        stompClient.subscribe('/user/queue/join', function (message) {
            console.log('Join message received:', message);
            updateJoinMessages(JSON.parse(message.body));
            //조인 되면 offer를 보낸다.
            if (localStream) { //이전에 stream은 얻어왔다고 생각한다.
                handleJoin(JSON.parse(message.body));
            }
        });
        stompClient.subscribe('/topic/messages', function (messageOutput) {
            onMessageReceived(JSON.parse(messageOutput.body));
        });
    });
}

// stream을 얻어오는 함수 (내 화면 나오게 하기)
function gotStream(stream) {
    console.log('Adding local stream.');
    localStream = stream;
    localVideo.srcObject = stream;
}

function handleJoin(message) {
    roomId = message.roomId;
    console.log("HANDLE JOIN!!!");
    console.log(message);
    document.getElementById("roomId").innerText = roomId;
    //여기서 방해 들어왔으니 구독을 2개 해줍니다.
    //TODO: 연결이 끊겼을 때 구독 해제 필요
    stompClient.subscribe("/user/queue/offer/" + roomId, function (messageOutput) {
        console.log('Offer message received:', messageOutput);
        handleOffer(JSON.parse(messageOutput.body));
    });
    stompClient.subscribe("/user/queue/answer/" + roomId, function (messageOutput) {
        console.log('Answer message received:', messageOutput);
        handleAnswer(JSON.parse(messageOutput.body));
    });
    if (message.userCount === 1 || message.userCount === "1") {//내가 방을 만든 경우
        console.log('Created room ' + message.roomId);
    } else {//내가 방에 조인한 경우
        console.log('joined: ' + message["userInfoList"]);

        //TODO: 여기서 몇 명인지 알아 내서 그만큼 화면에 띄워야해요. 이미 있던 유저 수만큼 추가되는 느낌
        message["userInfoList"]?.forEach(function (userInfo) {
            console.log('userInfo.userId:', userInfo.userId);
            console.log(userInfo);
            var MyUserId = document.getElementById("userId").value;
            if (userInfo.userId != MyUserId) {
                createPeerConnection(userInfo.userId);
                pcs[userInfo.userId].createOffer((description) => {
                    setLocalAndSendOffer(description, userInfo.userId)
                }, handleCreateOfferError);
            }
        });
    }
}

//이 함수에서 connection을 만든다.
function createPeerConnection(userId) {
    try {
        var pc = new RTCPeerConnection(pcConfig);
        pc.onicecandidate = function (event) {
            handleIceCandidate(event, userId);
        };
        pc.onaddstream = function (event) {
            handleRemoteStreamAdded(event, userId);
        };
        pc.onremovestream = handleRemoteStreamRemoved;
        pc.addStream(localStream);
        pcs[userId] = pc;
    } catch (e) {
        console.log('Failed to create PeerConnection, exception: ' + e.message);
        alert('Cannot create RTCPeerConnection object.');
        return;
    }
}

function handleOffer(message) {
    console.log("handleOffer");
    console.log(message);
    var userId = message.userId; //보낸사람의 ID
    createPeerConnection(userId);
    var sessionDescription = message.sessionDescription;
    pcs[userId].setRemoteDescription(sessionDescription);
    pcs[userId].createAnswer().then(
        function (description) {
            setLocalAndSendAnswer(description, userId)
        },
        onCreateSessionDescriptionError
    );
}

function handleAnswer(message) {
    console.log("handleAnswer");
    console.log(message);
    var userId = message.userId;
    var sessionDescription = message.sessionDescription;
    pcs[userId].setRemoteDescription(sessionDescription);
}

function setLocalAndSendOffer(sessionDescription, userId) {
    console.log('setLocalAndSendMessage sending message', sessionDescription);
    console.log("userId" + userId);
    pcs[userId].setLocalDescription(sessionDescription);
    console.log('setLocalAndSendMessage sending message', sessionDescription);
    //create Offer를 보낸다.

    stompClient.send("/app/offer/" + roomId, {}, JSON.stringify({
        userId: userId,
        sessionDescription: sessionDescription
    }));
}

var constraints = {
    video: true
};
console.log('Getting user media with constraints', constraints);
if (location.hostname !== 'localhost') {
    requestTurn(
        'https://computeengineondemand.appspot.com/turn?username=41784574&key=4080218913'
    );
}

//TODO: 여기에서 연결이 끊어짐을 알려줘야 합니다. 이벤트 아직 미구현
window.onbeforeunload = function () {
    sendMessage({type: 'bye'});
};


function handleIceCandidate(event, userId) {
    console.log('icecandidate event: ', event);
    if (event.candidate) {
        // sendMessage({
        //     type: 'candidate',
        //     label: event.candidate.sdpMLineIndex,
        //     id: event.candidate.sdpMid,
        //     candidate: event.candidate.candidate,
        //     userId: userId
        // });
    } else {
        console.log('End of candidates.');
    }
}

function handleCreateOfferError(event) {
    console.log('createOffer() error: ', event);
}


function setLocalAndSendAnswer(sessionDescription, userId) {
    console.log('setLocalAndSendMessage sending message', sessionDescription);
    console.log("userId" + userId);
    pcs[userId].setLocalDescription(sessionDescription);
    console.log('setLocalAndSendMessage sending message', sessionDescription);
    //create Offer를 보낸다.

    stompClient.send("/app/answer/" + roomId, {}, JSON.stringify({
        userId: userId,
        sessionDescription: sessionDescription
    }));

}

function onCreateSessionDescriptionError(error) {
    console.log('Failed to create session description: ' + error.toString());
}

function requestTurn(turnURL) {
    var turnExists = false;
    for (var i in pcConfig.iceServers) {
        if (pcConfig.iceServers[i].urls.substr(0, 5) === 'turn:') {
            turnExists = true;
            turnReady = true;
            break;
        }
    }
    if (!turnExists) {
        console.log('Getting TURN server from ', turnURL);
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                var turnServer = JSON.parse(xhr.responseText);
                console.log('Got TURN server: ', turnServer);
                pcConfig.iceServers.push({
                    'urls': 'turn:' + turnServer.username + '@' + turnServer.turn,
                    'credential': turnServer.password
                });
                turnReady = true;
            }
        };
        xhr.open('GET', turnURL, true);
        xhr.send();
    }
}

function handleRemoteStreamAdded(event) {
    console.log('Remote stream added for user:', userId);
    remoteStreams[userId] = event.stream;

    var videoElement = document.createElement('video');
    videoElement.id = 'remoteVideo_' + userId;
    videoElement.autoplay = true;
    videoElement.srcObject = event.stream;
    remoteVideos.appendChild(videoElement);
}

function handleRemoteStreamRemoved(event) {
    console.log('Remote stream removed. Event: ', event);
}


/**
 * 이 아래 함수는 HTML에서 사용하는 함수들입니다.
 */


function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function createRoom() {
    var roomName = prompt("Enter Room Name:");
    stompClient.send("/app/room/create", {}, JSON.stringify({'roomName': roomName}));
}

function joinRoom() {
    var joinId = prompt("Enter Room Id:");
    stompClient.send("/app/room/join/" + joinId, {}, JSON.stringify({}));
}

function leaveRoom() {
    stompClient.send("/app/room/leave", {}, JSON.stringify({}));
}

function getRoomList() {
    stompClient.send("/app/room/list", {}, JSON.stringify({}));
}

function updateRoomList(message) {
    var roomList = document.getElementById("roomList");
    var newMessage = document.createElement("div");
    newMessage.className = 'message';
    newMessage.innerHTML = '<pre>' + JSON.stringify(message, null, 2) + '</pre>';
    roomList.appendChild(newMessage);
    roomList.scrollTop = roomList.scrollHeight;
}

function updateJoinMessages(message) {
    var joinMessages = document.getElementById("joinMessages");
    var newMessage = document.createElement("div");
    newMessage.className = 'message';
    newMessage.innerHTML = '<pre>' + JSON.stringify(message, null, 2) + '</pre>';
    joinMessages.appendChild(newMessage);
    joinMessages.scrollTop = joinMessages.scrollHeight;
}