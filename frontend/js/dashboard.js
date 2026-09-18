const user = Session.requireLogin();

document.getElementById('sidebarName').textContent = user.name;
document.getElementById('sidebarEmail').textContent = user.email;

const statRow = document.getElementById('statRow');
const accountsGrid = document.getElementById('accountsGrid');
const recentLedger = document.getElementById('recentLedger');
const recentSub = document.getElementById('recentSub');

function renderStats(accounts) {
  const totalBalance = accounts.reduce((sum, a) => sum + Number(a.balance), 0);
  const primary = accounts[0];

  statRow.innerHTML = `
    <div class="stat">
      <div class="label">Account holder</div>
      <div class="value" style="font-family:'Fraunces',serif;font-size:20px;">${user.name}</div>
    </div>
    <div class="stat">
      <div class="label">Primary account number</div>
      <div class="value">${primary ? primary.accountNumber : '—'}</div>
    </div>
    <div class="stat">
      <div class="label">Total balance</div>
      <div class="value balance">₹ ${formatMoney(totalBalance)}</div>
    </div>
  `;
}

function renderAccounts(accounts) {
  if (accounts.length === 0) {
    accountsGrid.innerHTML = `
      <div class="empty-state" style="grid-column: 1 / -1;">
        You don't have any accounts yet. <a href="account.html">Open one</a> to get started.
      </div>`;
    return;
  }

  accountsGrid.innerHTML = accounts.map(a => `
    <div class="account-card">
      <div class="acct-number">${a.accountNumber}</div>
      <div class="acct-type">${a.accountType.toLowerCase()} account</div>
      <div class="acct-balance">₹ ${formatMoney(a.balance)}</div>
    </div>
  `).join('');
}

function renderRecent(accountNumber, transactions) {
  recentSub.textContent = accountNumber
    ? `Latest activity on ${accountNumber}.`
    : 'Latest activity on your first account.';

  if (transactions.length === 0) {
    recentLedger.innerHTML = `<div class="empty-state">No transactions yet.</div>`;
    return;
  }

  recentLedger.innerHTML = transactions.map(txnRowHtml).join('');
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
        <div class="meta">${formatDate(t.transactionDate)} · ${t.description || ''}</div>
      </div>
      <div class="amt ${isCredit ? 'credit' : 'debit'}">${isCredit ? '+' : '−'} ₹ ${formatMoney(t.amount)}</div>
    </div>
  `;
}

(async function load() {
  try {
    const accounts = await apiRequest(`/accounts/user/${user.userId}`);
    renderStats(accounts);
    renderAccounts(accounts);

    if (accounts.length > 0) {
      const recent = await apiRequest(`/transactions/account/${accounts[0].accountId}/recent?limit=5`);
      renderRecent(accounts[0].accountNumber, recent);
    } else {
      renderRecent(null, []);
    }
  } catch (err) {
    statRow.innerHTML = `<p class="loading-text">${err.message}</p>`;
    accountsGrid.innerHTML = '';
    recentLedger.innerHTML = '';
  }
})();
