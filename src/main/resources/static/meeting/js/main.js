'use strict';

var stompClient = null;
var JWT = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZXMiOiJVU0VSIiwiZXhwIjoxNzE4ODYxNTQ0fQ.Ast8TkAPyO2lTto253avdOFHhkiC4tjBsF33DPRv4QU";
var WORKSPACE_ID = "f162c91d-ec28-4575-949e-9f1f65f81053";
var roomId;
var userId = "1";
var isChannelReady = false;
var isInitiator = false;
var isStarted = false;
var localStream;
var pcs = {};
var remoteStreams = {};
var turnReady;
var amIInRoom = false;
var pcConfig = {
    'iceServers': [{
        'urls': 'stun:stun.l.google.com:19302'
    }]
};

// Set up audio and video regardless of what devices are present.
var sdpConstraints = {
    offerToReceiveAudio: true,
    offerToReceiveVideo: true
};

/////////////////////////////////////////////

var room = 'foo';
// Could prompt for room name:
// room = prompt('Enter room name:');


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
                handleJoin(message.body);
            }
        });
        stompClient.subscribe('/topic/messages', function (messageOutput) {
            onMessageReceived(JSON.parse(messageOutput.body));
        });
        // console.log('Connected: ' + frame);
        // if (room !== '') {
        //     sendMessage({room: room, type: 'create or join'});
        //     console.log('Attempted to create or join room', room);
        // }
    });
}

function handleJoin(message) {
    if (typeof message === 'string') {
        try {
            message = JSON.parse(message);
        } catch (e) {
            console.error('Failed to parse message:', e);
            return;
        }
    }
    roomId = message.roomId;
    console.log("HANDLE JOIN!!!");
    console.log(message);

    document.getElementById("roomId").innerText = message.roomId;
    if (message.userCount === "1") {//내가 방을 만든 경우
        console.log('Created room ' + message.roomId);
    } else {//내가 방에 조인한 경우
        console.log('joined: ' + message["userInfoList"]);
        //TODO: 여기서 몇 명인지 알아 내서 그만큼 화면에 띄워야해요.
        message["userInfoList"].forEach(function (userInfo) {
            console.log('userInfo.userId:', userInfo.userId);
            console.log(userInfo);
            if (userInfo.userId !== userId) {
                createPeerConnection(userInfo.userId);
            }
        });
    }
}

// function sendMessage(message) {
//     console.log('Client sending message: ', message);
//     stompClient.send("/app/message", {}, JSON.stringify(message));
// }

function onMessageReceived(message) {
    console.log('Client received message:', message);
    if (message.type === 'got user media') {
        maybeStart();
    } else if (message.type === 'offer') {
        if (!isInitiator && !isStarted) {
            maybeStart();
        }
        pc.setRemoteDescription(new RTCSessionDescription(message));
        doAnswer();
    } else if (message.type === 'answer' && isStarted) {
        pc.setRemoteDescription(new RTCSessionDescription(message));
    } else if (message.type === 'candidate' && isStarted) {
        var candidate = new RTCIceCandidate({
            sdpMLineIndex: message.label,
            candidate: message.candidate
        });
        pc.addIceCandidate(candidate);
    } else if (message.type === 'bye' && isStarted) {
        handleRemoteHangup();
    }
}

var localVideo = document.querySelector('#localVideo');
var remoteVideo = document.querySelector('#remoteVideo');


//여기서 stream을 등록해서 저장하고 있습니다.
function gotStream(stream) {
    console.log('Adding local stream.');
    localStream = stream;
    localVideo.srcObject = stream;
    // sendMessage({type: 'got user media'});
    // if (isInitiator) {
    //     maybeStart();
    // }
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

function maybeStart() {
    console.log('>>>>>>> maybeStart() ', isStarted, localStream, isChannelReady);
    if (!isStarted && typeof localStream !== 'undefined' && isChannelReady) {
        console.log('>>>>>> creating peer connection');
        createPeerConnection(userId);
        pc.addStream(localStream);
        isStarted = true;
        console.log('isInitiator', isInitiator);
        if (isInitiator) {
            doCall();
        }
    }
}

window.onbeforeunload = function () {
    sendMessage({type: 'bye'});
};

//이 함수에서 connection을 만들고 offer도 보낸다.
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
        pcs[userId] = pc;
        console.log('Created RTCPeerConnnection for user:', userId);
        console.log('Sending offer to peer');
        pc.addStream(localStream);
        pc.createOffer((description) => {
            setLocalAndSendOffer(description, userId)
        }, handleCreateOfferError);
        // pc.createOffer().then(
        //     setLocalAndSendMessage,
        //     onCreateSessionDescriptionError
        // );

    } catch (e) {
        console.log('Failed to create PeerConnection, exception: ' + e.message);
        alert('Cannot create RTCPeerConnection object.');
        return;
    }
}

function handleIceCandidate(event, userId) {
    console.log('icecandidate event: ', event);
    if (event.candidate) {
        sendMessage({
            type: 'candidate',
            label: event.candidate.sdpMLineIndex,
            id: event.candidate.sdpMid,
            candidate: event.candidate.candidate,
            userId: userId
        });
    } else {
        console.log('End of candidates.');
    }
}

function handleCreateOfferError(event) {
    console.log('createOffer() error: ', event);
}

function doCall() {
    console.log('Sending offer to peer');
    // pc.createOffer(setLocalAndSendMessage, handleCreateOfferError);
}

function doAnswer() {
    console.log('Sending answer to peer.');
    pc.createAnswer().then(
        setLocalAndSendMessage,
        onCreateSessionDescriptionError
    );
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

function hangup() {
    console.log('Hanging up.');
    stop();
    sendMessage({type: 'bye'});
}

function handleRemoteHangup() {
    console.log('Session terminated.');
    stop();
    isInitiator = false;
}

function stop() {
    isStarted = false;
    pc.close();
    pc = null;
}

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
    var roomName = prompt("Enter Room Id:");
    stompClient.send("/app/room/join/" + roomId, {}, JSON.stringify({'roomName': roomName}));
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