'use strict';

var videoElement = document.querySelector('video');
var audioSelect = document.querySelector('select#audioSource');
var videoSelect = document.querySelector('select#videoSource');
var vgaButton = document.querySelector('button#vga');
var qvgaButton = document.querySelector('button#qvga');
var hdButton = document.querySelector('button#hd');
var stopButton = document.querySelector('button#stop');
var dimensions = document.querySelector('p#dimensions');

audioSelect.onchange = updateStream;
videoSelect.onchange = updateStream;

vgaButton.onclick = () => updateStream(vgaConstraints);
qvgaButton.onclick = () => updateStream(qvgaConstraints);
hdButton.onclick = () => updateStream(hdConstraints);
stopButton.onclick = stopStream;
// 기본 비디오 해상도 제약 조건
var defaultConstraints = {
    video: true,
    audio: true
};

var qvgaConstraints = {
    video: {
        width: {max: 320},
        height: {max: 180}
    }
};

var vgaConstraints = {
    video: {
        width: {max: 640},
        height: {max: 360}
    }
};

var hdConstraints = {
    video: {
        width: {min: 1280},
        height: {min: 720}
    }
};

function getDevices() {
    // Safari에서는 gUM이 호출될 때까지 기본 장치만 가져옵니다.
    return navigator.mediaDevices.enumerateDevices();
}

function gotDevices(deviceInfos) {
    window.deviceInfos = deviceInfos; // make available to console
    console.log('Available input and output devices:', deviceInfos);
    for (const deviceInfo of deviceInfos) {
        const option = document.createElement('option');
        option.value = deviceInfo.deviceId;
        if (deviceInfo.kind === 'audioinput') {
            option.text = deviceInfo.label || `Microphone ${audioSelect.length + 1}`;
            audioSelect.appendChild(option);
        } else if (deviceInfo.kind === 'videoinput') {
            option.text = deviceInfo.label || `Camera ${videoSelect.length + 1}`;
            videoSelect.appendChild(option);
        }
    }
}

function updateStream(constraints = defaultConstraints) {
    if (window.stream) {
        window.stream.getTracks().forEach(track => track.stop());
    }
    const audioSource = audioSelect.value;
    const videoSource = videoSelect.value;

    constraints = {
        ...constraints,
        audio: {deviceId: audioSource ? {exact: audioSource} : undefined},
        video: {...(constraints.video || {}), deviceId: videoSource ? {exact: videoSource} : undefined}
    };

    navigator.mediaDevices.getUserMedia(constraints)
        .then(gotStream)
        .catch(handleError);
}

function stopStream() {
    if (window.stream) {
        window.stream.getTracks().forEach(track => track.stop());
        videoElement.srcObject = null;
    }
}

function gotStream(stream) {
    window.stream = stream; // make stream available to console
    audioSelect.selectedIndex = [...audioSelect.options].findIndex(option => option.text === stream.getAudioTracks()[0]?.label);
    videoSelect.selectedIndex = [...videoSelect.options].findIndex(option => option.text === stream.getVideoTracks()[0]?.label);
    videoElement.srcObject = stream;
    videoElement.addEventListener('play', function () {
        setTimeout(displayVideoDimensions, 500);
    });
}

function handleError(error) {
    console.error('Error: ', error);
}

function displayVideoDimensions() {
    dimensions.textContent = 'Actual video dimensions: ' + videoElement.videoWidth +
        'x' + videoElement.videoHeight + 'px.';
}

// 초기화 함수 호출
updateStream();
getDevices().then(gotDevices);
