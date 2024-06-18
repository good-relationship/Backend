document.getElementById('loginKakao').addEventListener('click', function () {
    const clientIdKakao = '3ddf7722d197e0209fbdd5ac713d2521';
    const redirectUriKakao = 'http://localhost:8080/login/kakao/index.html';
    window.location.href = `https://kauth.kakao.com/oauth/authorize?client_id=${clientIdKakao}&redirect_uri=${redirectUriKakao}&response_type=code`;
});

document.getElementById('loginNaver').addEventListener('click', function () {
    const clientIdNaver = 'ZFs6GCqTERTuPLYJ5_Ax';
    const redirectUriNaver = 'http://localhost:8080/login/naver/index.html';
    window.location.href = `https://nid.naver.com/oauth2.0/authorize?client_id=${clientIdNaver}&response_type=code&redirect_uri=${redirectUriNaver}&state=false`;
});
