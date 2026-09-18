const user = Session.requireLogin();

document.getElementById('sidebarName').textContent = user.name;
document.getElementById('sidebarEmail').textContent = user.email;

const pageAlert = document.getElementById('pageAlert');
const accountsGrid = document.getElementById('accountsGrid');

// ---------------- Profile form ----------------

document.getElementById('profName').value = user.name;
document.getElementById('profEmail').value = user.email;
document.getElementById('profPhone').value = user.phoneNumber;
document.getElementById('profUsername').value = user.username;

document.getElementById('profileForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  hideAlert(pageAlert);

  const payload = {
    name: document.getElementById('profName').value.trim(),
    email: document.getElementById('profEmail').value.trim(),
    phoneNumber: document.getElementById('profPhone').value.trim(),
  };

  try {
    const updated = await apiRequest(`/users/${user.userId}`, { method: 'PUT', body: payload });
    Session.save(updated);
    showAlert(pageAlert, 'Profile updated successfully.', 'success');
  } catch (err) {
    showAlert(pageAlert, err.message);
  }
});

document.getElementById('deleteAccountBtn').addEventListener('click', async () => {
  const sure = confirm('This will permanently delete your profile and every linked bank account. Continue?');
  if (!sure) return;

  try {
    await apiRequest(`/users/${user.userId}`, { method: 'DELETE' });
    Session.clear();
    window.location.href = 'index.html';
  } catch (err) {
    showAlert(pageAlert, err.message);
  }
});

// ---------------- Accounts list ----------------

let currentAccounts = [];

async function loadAccounts() {
  try {
    currentAccounts = await apiRequest(`/accounts/user/${user.userId}`);
    renderAccounts();
  } catch (err) {
    accountsGrid.innerHTML = `<p class="loading-text">${err.message}</p>`;
  }
}

function renderAccounts() {
  if (currentAccounts.length === 0) {
    accountsGrid.innerHTML = `<div class="empty-state" style="grid-column:1/-1;">No accounts yet. Click "New account" to open one.</div>`;
    return;
  }

  accountsGrid.innerHTML = currentAccounts.map(a => `
    <div class="account-card">
      <div class="acct-number">${a.accountNumber}</div>
      <div class="acct-type">${a.accountType.toLowerCase()} account</div>
      <div class="acct-balance">₹ ${formatMoney(a.balance)}</div>
      <div class="table-actions" style="margin-top:14px;">
        <button class="btn btn-outline" style="width:auto;padding:7px 12px;font-size:13px;" onclick="openEditModal(${a.accountId}, '${a.accountType}')">Edit type</button>
        <button class="btn btn-danger" style="width:auto;padding:7px 12px;font-size:13px;" onclick="deleteAccount(${a.accountId})">Close account</button>
      </div>
    </div>
  `).join('');
}

async function deleteAccount(accountId) {
  const sure = confirm('Close this account? This cannot be undone.');
  if (!sure) return;
  try {
    await apiRequest(`/accounts/${accountId}`, { method: 'DELETE' });
    loadAccounts();
  } catch (err) {
    showAlert(pageAlert, err.message);
  }
}

loadAccounts();

// ---------------- Create account modal ----------------

const createModalBackdrop = document.getElementById('createModalBackdrop');
const createAlert = document.getElementById('createAlert');

document.getElementById('openCreateModal').addEventListener('click', () => {
  hideAlert(createAlert);
  document.getElementById('createAccountForm').reset();
  createModalBackdrop.classList.add('show');
});
document.getElementById('cancelCreate').addEventListener('click', () => createModalBackdrop.classList.remove('show'));

document.getElementById('createAccountForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  hideAlert(createAlert);

  const payload = {
    userId: user.userId,
    accountType: document.getElementById('newAccountType').value,
    initialDeposit: Number(document.getElementById('initialDeposit').value || 0),
  };

  try {
    await apiRequest('/accounts', { method: 'POST', body: payload });
    createModalBackdrop.classList.remove('show');
    loadAccounts();
  } catch (err) {
    showAlert(createAlert, err.message);
  }
});

// ---------------- Edit account type modal ----------------

const editModalBackdrop = document.getElementById('editModalBackdrop');
const editAlert = document.getElementById('editAlert');

function openEditModal(accountId, accountType) {
  hideAlert(editAlert);
  document.getElementById('editAccountId').value = accountId;
  document.getElementById('editAccountType').value = accountType;
  editModalBackdrop.classList.add('show');
}
document.getElementById('cancelEdit').addEventListener('click', () => editModalBackdrop.classList.remove('show'));

document.getElementById('editAccountForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  hideAlert(editAlert);

  const accountId = document.getElementById('editAccountId').value;
  const accountType = document.getElementById('editAccountType').value;

  try {
    await apiRequest(`/accounts/${accountId}`, { method: 'PUT', body: { accountType } });
    editModalBackdrop.classList.remove('show');
    loadAccounts();
  } catch (err) {
    showAlert(editAlert, err.message);
  }
});
