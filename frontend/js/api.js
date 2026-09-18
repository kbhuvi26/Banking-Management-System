/**
 * Shared helpers used by every page.
 *   - API_BASE:  where the Spring Boot backend is running
 *   - apiRequest(): a thin wrapper around fetch() that always sends/reads
 *     JSON and unwraps our backend's { success, message, data } shape
 *   - Session:   a tiny localStorage-based "who is logged in" store.
 *     There is no Spring Security / JWT in this project - after a
 *     successful login we simply remember the returned user object in
 *     the browser's localStorage, and every other page reads it from
 *     there. This keeps the frontend 100% plain HTML/CSS/JS as required.
 */

const API_BASE = 'http://localhost:8080/api';

async function apiRequest(path, { method = 'GET', body } = {}) {
  const options = {
    method,
    headers: { 'Content-Type': 'application/json' },
  };
  if (body !== undefined) {
    options.body = JSON.stringify(body);
  }

  let response;
  try {
    response = await fetch(`${API_BASE}${path}`, options);
  } catch (networkErr) {
    throw new Error('Could not reach the server. Is the Spring Boot backend running on port 8080?');
  }

  let payload;
  try {
    payload = await response.json();
  } catch (parseErr) {
    throw new Error('Unexpected response from server');
  }

  if (!response.ok || payload.success === false) {
    throw new Error(payload.message || `Request failed (${response.status})`);
  }
  return payload.data;
}

const Session = {
  KEY: 'bms_current_user',

  save(user) {
    localStorage.setItem(this.KEY, JSON.stringify(user));
  },

  get() {
    const raw = localStorage.getItem(this.KEY);
    return raw ? JSON.parse(raw) : null;
  },

  clear() {
    localStorage.removeItem(this.KEY);
  },

  // Call at the top of every protected page - bounces back to login if
  // nobody is signed in.
  requireLogin() {
    const user = this.get();
    if (!user) {
      window.location.href = 'index.html';
      return null;
    }
    return user;
  },
};

function logout() {
  Session.clear();
  window.location.href = 'index.html';
}

function formatMoney(value) {
  const n = Number(value);
  return n.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function formatDate(isoString) {
  const d = new Date(isoString);
  return d.toLocaleString('en-IN', {
    day: '2-digit', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  });
}

function showAlert(el, message, type = 'error') {
  el.textContent = message;
  el.className = `alert show alert-${type}`;
}

function hideAlert(el) {
  el.className = 'alert';
}
