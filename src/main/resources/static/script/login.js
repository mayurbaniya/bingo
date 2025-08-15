//  document.getElementById('loginForm').addEventListener('submit', function(e) {
//             e.preventDefault();
            
//             // Get form values
//             const username = this.querySelector('input[type="text"]').value;
//             const password = this.querySelector('input[type="password"]').value;
            
//             // Simple validation
//             if(username === '' || password === '') {
//                 alert('Please enter both username and password');
//                 return;
//             }
            
//             // For demo purposes, just show an alert
//             alert(`Login attempt with:\nUsername: ${username}\nPassword: ${password}\n\nThis is a demo - no actual authentication is performed.`);
            
//             // In a real application, you would send this to a server
//             // window.location.href = '/admin-dashboard.html';
//         });