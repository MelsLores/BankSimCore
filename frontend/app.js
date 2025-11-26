const API_URL = 'http://localhost:8080/api';

// ========== NAVIGATION ==========
function showTab(tabName) {
    // Hide all tabs
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    
    // Remove active class from all buttons
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // Show selected tab
    document.getElementById(tabName).classList.add('active');
    
    // Add active class to clicked button
    event.target.classList.add('active');
    
    // Load data for specific tabs
    if (tabName === 'dashboard') {
        loadDashboard();
    } else if (tabName === 'customers') {
        loadCustomers();
    }
}

// ========== TOAST NOTIFICATIONS ==========
function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type} show`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// ========== DASHBOARD ==========
async function loadDashboard() {
    try {
        // Load summary
        const summaryResponse = await fetch(`${API_URL}/summary`);
        const summary = await summaryResponse.json();
        
        document.getElementById('totalCustomers').textContent = summary.customers;
        document.getElementById('totalAccounts').textContent = summary.accounts;
        document.getElementById('totalBalance').textContent = `$${summary.totalBalance.toFixed(2)}`;
        document.getElementById('totalTransactions').textContent = summary.transactions;
        
        // Load accounts table
        const accountsResponse = await fetch(`${API_URL}/accounts`);
        const accounts = await accountsResponse.json();
        
        const tbody = document.getElementById('accountsTableBody');
        tbody.innerHTML = '';
        
        if (accounts.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3">No hay cuentas registradas</td></tr>';
        } else {
            accounts.forEach(account => {
                const row = tbody.insertRow();
                row.innerHTML = `
                    <td>${account.accountNumber}</td>
                    <td>${account.owner}</td>
                    <td>$${account.balance.toFixed(2)}</td>
                `;
            });
        }
    } catch (error) {
        console.error('Error loading dashboard:', error);
        showToast('Error al cargar el dashboard', 'error');
    }
}

// ========== CUSTOMERS ==========
async function loadCustomers() {
    try {
        const response = await fetch(`${API_URL}/customers`);
        const customers = await response.json();
        
        const container = document.getElementById('customersListContent');
        
        if (customers.length === 0) {
            container.innerHTML = '<p>No hay clientes registrados</p>';
        } else {
            container.innerHTML = customers.map(customer => `
                <div class="customer-item">
                    <strong>${customer.name}</strong>
                    <p>ID: ${customer.customerId}</p>
                </div>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading customers:', error);
        showToast('Error al cargar clientes', 'error');
    }
}

document.getElementById('customerForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const data = {
        customerId: document.getElementById('customerId').value,
        firstName: document.getElementById('firstName').value,
        lastName: document.getElementById('lastName').value,
        personalKey: document.getElementById('personalKey').value,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value
    };
    
    try {
        const response = await fetch(`${API_URL}/customers`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        const result = await response.json();
        
        if (response.ok) {
            showToast('Cliente creado exitosamente!', 'success');
            e.target.reset();
            loadCustomers();
        } else {
            showToast(result.error || 'Error al crear cliente', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error de conexión', 'error');
    }
});

// ========== ACCOUNTS ==========
document.getElementById('accountForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const data = {
        bankCode: document.getElementById('bankCode').value,
        branchCode: document.getElementById('branchCode').value,
        accountNumber: document.getElementById('accountNumber').value,
        customerId: document.getElementById('accountCustomerId').value,
        initialBalance: parseFloat(document.getElementById('initialBalance').value)
    };
    
    try {
        const response = await fetch(`${API_URL}/accounts`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        const result = await response.json();
        
        if (response.ok) {
            showToast('Cuenta creada exitosamente!', 'success');
            e.target.reset();
            loadDashboard();
        } else {
            showToast(result.error || 'Error al crear cuenta', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error de conexión', 'error');
    }
});

// ========== OPERATIONS ==========

// Deposit
document.getElementById('depositForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const data = {
        accountKey: document.getElementById('depositAccount').value,
        amount: parseFloat(document.getElementById('depositAmount').value)
    };
    
    try {
        const response = await fetch(`${API_URL}/deposit`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        const result = await response.json();
        
        if (response.ok) {
            showToast(`Depósito exitoso! Nuevo saldo: $${result.balance.toFixed(2)}`, 'success');
            e.target.reset();
            loadDashboard();
        } else {
            showToast(result.error || 'Error al depositar', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error de conexión', 'error');
    }
});

// Withdraw
document.getElementById('withdrawForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const data = {
        accountKey: document.getElementById('withdrawAccount').value,
        amount: parseFloat(document.getElementById('withdrawAmount').value)
    };
    
    try {
        const response = await fetch(`${API_URL}/withdraw`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        const result = await response.json();
        
        if (response.ok) {
            showToast(`Retiro exitoso! Nuevo saldo: $${result.balance.toFixed(2)}`, 'success');
            e.target.reset();
            loadDashboard();
        } else {
            showToast(result.error || 'Error al retirar', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error de conexión', 'error');
    }
});

// Transfer
document.getElementById('transferForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const data = {
        fromAccount: document.getElementById('transferFrom').value,
        toAccount: document.getElementById('transferTo').value,
        amount: parseFloat(document.getElementById('transferAmount').value)
    };
    
    try {
        const response = await fetch(`${API_URL}/transfer`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        const result = await response.json();
        
        if (response.ok) {
            showToast(`Transferencia exitosa! Nuevo saldo: $${result.balance.toFixed(2)}`, 'success');
            e.target.reset();
            loadDashboard();
        } else {
            showToast(result.error || 'Error al transferir', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error de conexión', 'error');
    }
});

// Balance Query
document.getElementById('balanceForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const accountKey = document.getElementById('balanceAccount').value;
    
    try {
        const response = await fetch(`${API_URL}/balance?accountKey=${encodeURIComponent(accountKey)}`);
        const result = await response.json();
        
        if (response.ok) {
            document.getElementById('balanceResult').innerHTML = `
                <h4>Información de la Cuenta</h4>
                <p><strong>Cuenta:</strong> ${result.accountNumber}</p>
                <p><strong>Titular:</strong> ${result.owner}</p>
                <p><strong>Saldo:</strong> $${result.balance.toFixed(2)}</p>
                <p><strong>Transacciones:</strong> ${result.transactions}</p>
            `;
            showToast('Consulta exitosa', 'success');
        } else {
            showToast(result.error || 'Error al consultar', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error de conexión', 'error');
    }
});

// ========== SERVICES ==========

// Checkbook
document.getElementById('checkbookForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const data = {
        accountKey: document.getElementById('checkbookAccount').value,
        personalKey: document.getElementById('checkbookKey').value
    };
    
    try {
        const response = await fetch(`${API_URL}/checkbook`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        const result = await response.json();
        
        if (response.ok) {
            showToast('Chequera solicitada exitosamente!', 'success');
            e.target.reset();
        } else {
            showToast(result.error || 'Error al solicitar chequera', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error de conexión', 'error');
    }
});

// Statement
document.getElementById('statementForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const data = {
        accountKey: document.getElementById('statementAccount').value,
        personalKey: document.getElementById('statementKey').value
    };
    
    try {
        const response = await fetch(`${API_URL}/statement`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        
        const result = await response.json();
        
        if (response.ok) {
            document.getElementById('statementResult').innerHTML = `
                <pre style="white-space: pre-wrap; font-family: monospace;">${result.statement}</pre>
            `;
            showToast('Estado de cuenta generado!', 'success');
        } else {
            showToast(result.error || 'Error al generar estado de cuenta', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error de conexión', 'error');
    }
});

// ========== TESTS ==========
async function runTests() {
    const container = document.getElementById('testsResults');
    const summary = document.getElementById('testsSummary');
    
    container.innerHTML = '<p>Ejecutando tests...</p>';
    summary.innerHTML = '';
    
    try {
        const response = await fetch(`${API_URL}/tests`);
        const tests = await response.json();
        
        let passCount = 0;
        let failCount = 0;
        let okCount = 0;
        
        container.innerHTML = tests.map(test => {
            const isOk = test.status === 'OK';
            if (isOk) okCount++;
            if (test.result === 'PASS') passCount++;
            if (test.result === 'FAIL') failCount++;
            
            return `
                <div class="test-case ${test.result.toLowerCase()} ${test.status.toLowerCase()}">
                    <div class="test-header">
                        <span class="test-id">${test.id}</span>
                        <span class="test-status ${test.status.toLowerCase()}">${test.status}</span>
                    </div>
                    <div class="test-details">
                        <p><strong>Input:</strong></p>
                        <p>Bank: ${test.bankCode} | Branch: ${test.branchCode} | Account: ${test.accountNumber}</p>
                        <p>Key: ${test.personalKey} | Order: ${test.orderValue}</p>
                        <p><strong>Expected:</strong> ${test.expected} | <strong>Result:</strong> ${test.result}</p>
                    </div>
                </div>
            `;
        }).join('');
        
        const allTestsOk = okCount === 12;
        summary.innerHTML = `
            <h3>Resumen de Tests</h3>
            <p><strong>Total de Tests:</strong> ${tests.length}</p>
            <p><strong>Casos Válidos (PASS):</strong> ${passCount}/4</p>
            <p><strong>Casos Inválidos (FAIL):</strong> ${failCount}/8</p>
            <p><strong>Tests Correctos:</strong> ${okCount}/12</p>
            <h4 style="color: ${allTestsOk ? '#38ef7d' : '#f5576c'}; margin-top: 15px;">
                ${allTestsOk ? '✓ ALL TESTS COMPLETED AS EXPECTED!' : '⚠ Some tests did not behave as expected'}
            </h4>
        `;
        
        showToast('Tests ejecutados exitosamente', 'success');
    } catch (error) {
        console.error('Error:', error);
        container.innerHTML = '<p style="color: red;">Error al ejecutar tests</p>';
        showToast('Error al ejecutar tests', 'error');
    }
}

// ========== INITIALIZE ==========
window.addEventListener('load', () => {
    loadDashboard();
});
