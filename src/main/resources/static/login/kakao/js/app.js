document.addEventListener('DOMContentLoaded', function () {
    const contentDiv = document.getElementById('content');
    const params = new URLSearchParams(window.location.search);
    const code = params.get('code');

    if (code) {
        console.log('Authorization code:', code);
        fetch(`http://localhost:8080/login/oauth2/kakao?code=${code}`, {
            method: 'POST'
        }).then(response => response.json())
            .then(data => {
                console.log(data);
                contentDiv.innerHTML = `Login Success: <pre>${JSON.stringify(data, null, 2)}</pre>`;
            })
            .catch(error => {
                console.error('Error:', error);
                contentDiv.innerHTML = `Error: ${error}`;
            });
    } else {
        contentDiv.innerHTML = "No authorization code found.";
    }
});
