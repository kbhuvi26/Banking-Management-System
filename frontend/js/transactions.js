const user = Session.requireLogin();

document.getElementById('sidebarName').textContent = user.name;
document.getElementById('sidebarEmail').textContent = user.email;

const accountSelect = document.getElementById('accountSelect');
const txnAlert = document.getElementById('txnAlert');
const historyLedger = document.getElementById('historyLedger');
const historySub = document.getElementById('historySub');

let accounts = [];

// ---------------- Tabs ----------------

document.querySelectorAll('.tabs button').forEach(btn => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.tabs button').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    document.querySelectorAll('.txn-form').forEach(f => f.style.display = 'none');
    document.getElementById(`${btn.dataset.tab}Form`).style.display = 'block';
    hideAlert(txnAlert);
  });
});

// ---------------- Load accounts into the selector ----------------

async function loadAccounts() {
  const previouslySelected = accountSelect.value;
  try {
    accounts = await apiRequest(`/accounts/user/${user.userId}`);
    if (accounts.length === 0) {
      accountSelect.innerHTML = '<option>No accounts — open one first</option>';
      historyLedger.innerHTML = `<div class="empty-state">Open an account on the Accounts page to get started.</div>`;
      return;
    }
    accountSelect.innerHTML = accounts.map(a =>
      `<option value="${a.accountId}">${a.accountNumber} · ${a.accountType.toLowerCase()} · ₹ ${formatMoney(a.balance)}</option>`
    ).join('');

    const stillExists = accounts.some(a => String(a.accountId) === previouslySelected);
    if (stillExists) accountSelect.value = previouslySelected;

    loadHistory();
  } catch (err) {
    showAlert(txnAlert, err.message);
  }
}

accountSelect.addEventListener('change', loadHistory);

// ---------------- History ----------------

async function loadHistory() {
  const accountId = accountSelect.value;
  if (!accountId) return;

  const account = accounts.find(a => String(a.accountId) === String(accountId));
  historySub.textContent = account ? `Full history for ${account.accountNumber}.` : 'Full history for the selected account.';
  historyLedger.innerHTML = `<p class="loading-text">Loading…</p>`;

  try {
    const txns = await apiRequest(`/transactions/account/${accountId}`);
    if (txns.length === 0) {
      historyLedger.innerHTML = `<div class="empty-state">No transactions on this account yet.</div>`;
      return;
    }
    historyLedger.innerHTML = txns.map(txnRowHtml).join('');
  } catch (err) {
    historyLedger.innerHTML = `<div class="empty-state">${err.message}</div>`;
  }
}

function txnRowHtml(t) {
  const isCredit = t.transactionType === 'DEPOSIT' || t.transactionType === 'TRANSFER_IN';
  const label = {
    DEPOSIT: 'Deposit',
    WITHDRAW: 'Withdrawal',
    TRANSFER_IN: 'Transfer received',
    TRANSFER_OUT: 'Transfer sent',
  }[t.transactionType] || t.transactionType;

  return `
    <div class="ledger-row">
      <div class="desc">
        <div class="type">${label}</div>
        <div class="meta">${formatDate(t.transactionDate)} · ${t.description || ''} · balance after: ₹ ${formatMoney(t.balanceAfter)}</div>
      </div>
      <div class="amt ${isCredit ? 'credit' : 'debit'}">${isCredit ? '+' : '−'} ₹ ${formatMoney(t.amount)}</div>
    </div>
  `;
}

// ---------------- Deposit ----------------

document.getElementById('depositForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  hideAlert(txnAlert);
  try {
    await apiRequest('/transactions/deposit', {
      method: 'POST',
      body: {
        accountId: Number(accountSelect.value),
        amount: Number(document.getElementById('depositAmount').value),
        description: document.getElementById('depositDesc').value.trim() || undefined,
      },
    });
    showAlert(txnAlert, 'Deposit successful.', 'success');
    document.getElementById('depositForm').reset();
    await loadAccounts();
  } catch (err) {
    showAlert(txnAlert, err.message);
  }
});

// ---------------- Withdraw ----------------

document.getElementById('withdrawForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  hideAlert(txnAlert);
  try {
    await apiRequest('/transactions/withdraw', {
      method: 'POST',
      body: {
        accountId: Number(accountSelect.value),
        amount: Number(document.getElementById('withdrawAmount').value),
        description: document.getElementById('withdrawDesc').value.trim() || undefined,
      },
    });
    showAlert(txnAlert, 'Withdrawal successful.', 'success');
    document.getElementById('withdrawForm').reset();
    await loadAccounts();
  } catch (err) {
    showAlert(txnAlert, err.message);
  }
});

// ---------------- Transfer ----------------

document.getElementById('transferForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  hideAlert(txnAlert);
  try {
    await apiRequest('/transactions/transfer', {
      method: 'POST',
      body: {
        fromAccountId: Number(accountSelect.value),
        toAccountNumber: document.getElementById('toAccountNumber').value.trim(),
        amount: Number(document.getElementById('transferAmount').value),
        description: document.getElementById('transferDesc').value.trim() || undefined,
      },
    });
    showAlert(txnAlert, 'Transfer successful.', 'success');
    document.getElementById('transferForm').reset();
    await loadAccounts();
  } catch (err) {
    showAlert(txnAlert, err.message);
  }
});

loadAccounts();
