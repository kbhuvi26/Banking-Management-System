if (Session.get()) {
  window.location.href = 'dashboard.html';
}

const registerForm = document.getElementById('registerForm');
const alertBox = document.getElementById('alertBox');
const registerBtn = document.getElementById('registerBtn');

registerForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  hideAlert(alertBox);

  const payload = {
    name: document.getElementById('name').value.trim(),
    email: document.getElementById('email').value.trim(),
    phoneNumber: document.getElementById('phoneNumber').value.trim(),
    username: document.getElementById('username').value.trim(),
    password: document.getElementById('password').value,
  };

  registerBtn.disabled = true;
  registerBtn.textContent = 'Creating account…';

  try {
    await apiRequest('/users/register', { method: 'POST', body: payload });
    showAlert(alertBox, 'Account created! Redirecting to sign in…', 'success');
    setTimeout(() => { window.location.href = 'index.html'; }, 1200);
  } catch (err) {
    showAlert(alertBox, err.message);
    registerBtn.disabled = false;
    registerBtn.textContent = 'Create account';
  }
});
