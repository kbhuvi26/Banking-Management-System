// If someone who is already logged in opens the login page, send them
// straight to the dashboard instead of showing the form again.
if (Session.get()) {
  window.location.href = 'dashboard.html';
}

const loginForm = document.getElementById('loginForm');
const alertBox = document.getElementById('alertBox');
const loginBtn = document.getElementById('loginBtn');

loginForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  hideAlert(alertBox);

  const usernameOrEmail = document.getElementById('usernameOrEmail').value.trim();
  const password = document.getElementById('password').value;

  loginBtn.disabled = true;
  loginBtn.textContent = 'Signing in…';

  try {
    const user = await apiRequest('/users/login', {
      method: 'POST',
      body: { usernameOrEmail, password },
    });
    Session.save(user);
    window.location.href = 'dashboard.html';
  } catch (err) {
    showAlert(alertBox, err.message);
    loginBtn.disabled = false;
    loginBtn.textContent = 'Sign in';
  }
});
