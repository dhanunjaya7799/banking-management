// Global variables
const API_BASE = '/api';
let currentUser = null;
let currentUserRole = null;

// DOM Elements
const loginModal = document.getElementById('loginModal');
const registerModal = document.getElementById('registerModal');
const dashboardContainer = document.getElementById('dashboardContainer');
const mainContent = document.querySelector('.main-content');
const notificationContainer = document.getElementById('notificationContainer');

// Initialize app
document.addEventListener('DOMContentLoaded', function() {
    initializeEventListeners();
    checkAuthStatus();
});

// Event Listeners
function initializeEventListeners() {
    // Navigation
    const hamburger = document.getElementById('hamburger');
    const navMenu = document.getElementById('nav-menu');
    
    if (hamburger) {
        hamburger.addEventListener('click', () => {
            hamburger.classList.toggle('active');
            navMenu.classList.toggle('active');
        });
    }

    // Form submissions
    document.getElementById('loginForm').addEventListener('submit', handleLogin);
    document.getElementById('registerForm').addEventListener('submit', handleRegister);

    // Close modals when clicking outside
    window.addEventListener('click', (e) => {
        if (e.target.classList.contains('modal')) {
            e.target.style.display = 'none';
        }
    });
}

// Authentication functions
function checkAuthStatus() {
    const userData = localStorage.getItem('currentUser');
    const userRole = localStorage.getItem('userRole');
    
    if (userData && userRole) {
        currentUser = JSON.parse(userData);
        currentUserRole = userRole;
        showDashboard();
    }
}

async function handleCreateStaff(e) {
    e.preventDefault();

    const firstName = document.getElementById('staffFirstName').value;
    const lastName = document.getElementById('staffLastName').value;
    const email = document.getElementById('staffEmail').value;
    const phoneNumber = document.getElementById('staffPhone').value;
    const aadharNumber = document.getElementById('staffAadhar').value;
    const dateOfBirth = document.getElementById('staffDob').value;
    const address = document.getElementById('staffAddress').value;
    const password = document.getElementById('staffPassword').value;
    const confirmPassword = document.getElementById('staffConfirmPassword').value;

    if (password !== confirmPassword) {
        showNotification('Passwords do not match', 'error');
        return;
    }

    const staffData = {
        firstName,
        lastName,
        email,
        phoneNumber,
        aadharNumber,
        dateOfBirth,
        address,
        password
    };

    try {
        showLoading();
        const response = await fetch(`${API_BASE}/users/staff`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(staffData)
        });

        if (response.ok) {
            showNotification('Staff user created successfully!', 'success');
            document.getElementById('createStaffForm').reset();
            // Refresh users list
            loadAllUsers();
        } else {
            const error = await response.text();
            showNotification('Failed to create staff: ' + error, 'error');
        }
    } catch (error) {
        showNotification('Failed to create staff: ' + error.message, 'error');
    } finally {
        hideLoading();
    }
}

async function handleLogin(e) {
    e.preventDefault();
    
    const phoneNumber = document.getElementById('loginPhone').value;
    const role = document.getElementById('userRole').value;
    const password = document.getElementById('loginPassword').value;
    
    try {
        showLoading();
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                phoneNumber: phoneNumber,
                role: role,
                password: password
            })
        });
        
        if (response.ok) {
            const result = await response.json();

            currentUser = result.user;
            currentUserRole = role;

            localStorage.setItem('currentUser', JSON.stringify(result.user));
            localStorage.setItem('userRole', role);

            closeModal('loginModal');
            showDashboard();
            showNotification('Login successful!', 'success');
        } else {
            const error = await response.text();
            showNotification('Login failed: ' + error, 'error');
        }
    } catch (error) {
        showNotification('Login failed: ' + error.message, 'error');
    } finally {
        hideLoading();
    }
}

async function handleRegister(e) {
    e.preventDefault();
    
    const password = document.getElementById('registerPassword').value;
    const confirmPassword = document.getElementById('registerConfirmPassword').value;

    if (password !== confirmPassword) {
        showNotification('Passwords do not match', 'error');
        return;
    }

    const formData = {
        firstName: document.getElementById('firstName').value,
        lastName: document.getElementById('lastName').value,
        email: document.getElementById('email').value,
        phoneNumber: document.getElementById('phoneNumber').value,
        aadharNumber: document.getElementById('aadharNumber').value,
        dateOfBirth: document.getElementById('dateOfBirth').value,
        address: document.getElementById('address').value,
        password: password
    };
    
    try {
        showLoading();
        const response = await fetch(`${API_BASE}/users/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        });
        
        if (response.ok) {
            const user = await response.json();
            showNotification('Registration successful! You can now login.', 'success');
            closeModal('registerModal');
            showLogin();
        } else {
            const error = await response.text();
            showNotification('Registration failed: ' + error, 'error');
        }
    } catch (error) {
        showNotification('Registration failed: ' + error.message, 'error');
    } finally {
        hideLoading();
    }
}

function logout() {
    currentUser = null;
    currentUserRole = null;
    localStorage.removeItem('currentUser');
    localStorage.removeItem('userRole');
    
    dashboardContainer.style.display = 'none';
    mainContent.style.display = 'block';
    showNotification('Logged out successfully', 'success');
}

// Dashboard functions
function showDashboard() {
    mainContent.style.display = 'none';
    dashboardContainer.style.display = 'block';
    
    if (currentUserRole === 'CUSTOMER') {
        loadCustomerDashboard();
    } else if (currentUserRole === 'STAFF' || currentUserRole === 'ADMIN') {
        loadStaffDashboard();
    }
}

async function loadCustomerDashboard() {
    const dashboardHTML = `
        <div class="dashboard-header">
            <div class="container">
                <h1>Welcome, ${currentUser.firstName} ${currentUser.lastName}</h1>
                <p>Customer Dashboard</p>
                <button class="btn btn-secondary" onclick="logout()">
                    <i class="fas fa-sign-out-alt"></i> Logout
                </button>
            </div>
        </div>
        
        <nav class="dashboard-nav">
            <div class="container">
                <ul>
                    <li><a href="#" class="nav-tab active" data-tab="accounts">My Accounts</a></li>
                    <li><a href="#" class="nav-tab" data-tab="transfer">Transfer Money</a></li>
                    <li><a href="#" class="nav-tab" data-tab="history">Transaction History</a></li>
                    <li><a href="#" class="nav-tab" data-tab="request">Request Account</a></li>
                </ul>
            </div>
        </nav>
        
        <div class="dashboard-content">
            <div class="container">
                <div id="accounts" class="dashboard-section active">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">My Accounts</h3>
                        </div>
                        <div id="accountsList">Loading...</div>
                    </div>
                    
                    <div class="card" style="margin-top: 2rem;">
                        <div class="card-header">
                            <h3 class="card-title">Transfer PIN</h3>
                        </div>
                        <div id="pinSection">
                            <div id="pinStatus">Loading PIN status...</div>
                            <div id="createPinForm" style="display: none;">
                                <p>Create a 6-digit PIN for secure money transfers. This PIN can only be created once.</p>
                                <div class="form-group">
                                    <label for="newPin">Enter 6-digit PIN:</label>
                                    <input type="password" id="newPin" maxlength="6" placeholder="Enter 6 digits" style="text-align: center; letter-spacing: 0.2rem;">
                                </div>
                                <div class="form-group">
                                    <label for="confirmPin">Confirm PIN:</label>
                                    <input type="password" id="confirmPin" maxlength="6" placeholder="Confirm 6 digits" style="text-align: center; letter-spacing: 0.2rem;">
                                </div>
                                <button type="button" class="btn btn-primary" onclick="createTransferPin()">
                                    <i class="fas fa-lock"></i> Create PIN
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div id="transfer" class="dashboard-section">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">Transfer Money</h3>
                        </div>
                        <form id="transferForm">
                            <div class="form-group">
                                <label for="fromAccount">From Account</label>
                                <select id="fromAccount" required>
                                    <option value="">Select Account</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="transferType">Transfer To</label>
                                <select id="transferType" required>
                                    <option value="">Select Transfer Type</option>
                                    <option value="phone">Phone Number</option>
                                    <option value="account">Account Number</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="transferTo">Recipient</label>
                                <input type="text" id="transferTo" placeholder="Enter phone number or account number" required>
                            </div>
                            <div class="form-group">
                                <label for="transferAmount">Amount</label>
                                <input type="number" id="transferAmount" min="1" step="0.01" required>
                            </div>
                            <div class="form-group">
                                <label for="transferDescription">Description</label>
                                <input type="text" id="transferDescription" placeholder="Optional description">
                            </div>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-paper-plane"></i> Transfer Money
                            </button>
                        </form>
                    </div>
                </div>
                
                <div id="history" class="dashboard-section">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">Transaction History</h3>
                        </div>
                        <div id="transactionHistory">Loading...</div>
                    </div>
                </div>
                
                <div id="request" class="dashboard-section">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">Request New Account</h3>
                        </div>
                        <form id="accountRequestForm">
                            <div class="form-group">
                                <label for="accountType">Account Type</label>
                                <select id="accountType" required>
                                    <option value="">Select Account Type</option>
                                    <option value="SAVINGS">Savings Account</option>
                                    <option value="CURRENT">Current Account</option>
                                    <option value="FIXED_DEPOSIT">Fixed Deposit</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="initialDeposit">Initial Deposit</label>
                                <input type="number" id="initialDeposit" min="0" step="0.01" value="0">
                            </div>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-file-alt"></i> Submit Request
                            </button>
                        </form>
                        <div id="accountRequests" style="margin-top: 2rem;">
                            <h4>My Account Requests</h4>
                            <div id="requestsList">Loading...</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    dashboardContainer.innerHTML = dashboardHTML;
    
    // Initialize dashboard event listeners
    initializeDashboardEvents();
    
    // Load initial data
    loadCustomerAccounts();
    loadCustomerRequests();
    checkPinStatus();
}

async function loadStaffDashboard() {
    const dashboardHTML = `
        <div class="dashboard-header">
            <div class="container">
                <h1>Welcome, ${currentUser.firstName} ${currentUser.lastName}</h1>
                <p>${currentUserRole} Dashboard</p>
                <button class="btn btn-secondary" onclick="logout()">
                    <i class="fas fa-sign-out-alt"></i> Logout
                </button>
            </div>
        </div>
        
        <nav class="dashboard-nav">
            <div class="container">
                <ul>
                    <li><a href="#" class="nav-tab active" data-tab="pending">Pending Requests</a></li>
                    <li><a href="#" class="nav-tab" data-tab="users">All Users</a></li>
                    <li><a href="#" class="nav-tab" data-tab="accounts">All Accounts</a></li>
                    <li><a href="#" class="nav-tab" data-tab="transactions">All Transactions</a></li>
                </ul>
            </div>
        </nav>
        
        <div class="dashboard-content">
            <div class="container">
                <div id="pending" class="dashboard-section active">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">Pending Account Requests</h3>
                        </div>
                        <div id="pendingRequests">Loading...</div>
                    </div>
                </div>
                
                <div id="users" class="dashboard-section">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">All Users</h3>
                        </div>
                        <div id="usersList">Loading...</div>
                    </div>
                    ${currentUserRole === 'ADMIN' ? `
                    <div class="card" style="margin-top: 2rem;">
                        <div class="card-header">
                            <h3 class="card-title">Create Staff User</h3>
                        </div>
                        <div class="card-body">
                            <form id="createStaffForm">
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="staffFirstName">First Name</label>
                                        <input type="text" id="staffFirstName" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="staffLastName">Last Name</label>
                                        <input type="text" id="staffLastName" required>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label for="staffEmail">Email</label>
                                    <input type="email" id="staffEmail" required>
                                </div>
                                <div class="form-group">
                                    <label for="staffPhone">Phone Number</label>
                                    <input type="text" id="staffPhone" placeholder="10-digit phone number" required>
                                </div>
                                <div class="form-group">
                                    <label for="staffAadhar">Aadhar Number</label>
                                    <input type="text" id="staffAadhar" placeholder="12-digit Aadhar number" required>
                                </div>
                                <div class="form-group">
                                    <label for="staffDob">Date of Birth</label>
                                    <input type="date" id="staffDob" required>
                                </div>
                                <div class="form-group">
                                    <label for="staffAddress">Address</label>
                                    <textarea id="staffAddress" rows="2" required></textarea>
                                </div>
                                <div class="form-group">
                                    <label for="staffPassword">Password</label>
                                    <input type="password" id="staffPassword" required>
                                </div>
                                <div class="form-group">
                                    <label for="staffConfirmPassword">Confirm Password</label>
                                    <input type="password" id="staffConfirmPassword" required>
                                </div>
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-user-plus"></i> Create Staff
                                </button>
                            </form>
                        </div>
                    </div>
                    ` : ''}
                </div>
                
                <div id="accounts" class="dashboard-section">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">All Accounts</h3>
                        </div>
                        <div id="allAccounts">Loading...</div>
                    </div>
                </div>
                
                <div id="transactions" class="dashboard-section">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title">All Transactions</h3>
                        </div>
                        <div id="allTransactions">Loading...</div>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    dashboardContainer.innerHTML = dashboardHTML;
    
    // Initialize dashboard event listeners
    initializeDashboardEvents();
    
    // Load initial data
    loadPendingRequests();
}

function initializeDashboardEvents() {
    // Tab navigation
    document.querySelectorAll('.nav-tab').forEach(tab => {
        tab.addEventListener('click', (e) => {
            e.preventDefault();
            
            // Remove active class from all tabs and sections
            document.querySelectorAll('.nav-tab').forEach(t => t.classList.remove('active'));
            document.querySelectorAll('.dashboard-section').forEach(s => s.classList.remove('active'));
            
            // Add active class to clicked tab and corresponding section
            tab.classList.add('active');
            document.getElementById(tab.dataset.tab).classList.add('active');
            
            // Load data for the selected tab
            loadTabData(tab.dataset.tab);
        });
    });
    
    // Forms
    const transferForm = document.getElementById('transferForm');
    if (transferForm) {
        transferForm.addEventListener('submit', handleTransfer);
    }
    
    const accountRequestForm = document.getElementById('accountRequestForm');
    if (accountRequestForm) {
        accountRequestForm.addEventListener('submit', handleAccountRequest);
    }

    const createStaffForm = document.getElementById('createStaffForm');
    if (createStaffForm) {
        createStaffForm.addEventListener('submit', handleCreateStaff);
    }
}

function loadTabData(tabName) {
    switch(tabName) {
        case 'accounts':
            if (currentUserRole === 'CUSTOMER') {
                loadCustomerAccounts();
                checkPinStatus();
            } else {
                loadAllAccounts();
            }
            break;
        case 'history':
            loadTransactionHistory();
            break;
        case 'request':
            loadCustomerRequests();
            break;
        case 'pending':
            loadPendingRequests();
            break;
        case 'users':
            loadAllUsers();
            break;
        case 'transactions':
            loadAllTransactions();
            break;
    }
}

// Customer functions
async function loadCustomerAccounts() {
    try {
        const response = await fetch(`${API_BASE}/accounts/user/${currentUser.id}`);
        const accounts = await response.json();
        
        const accountsList = document.getElementById('accountsList');
        const fromAccountSelect = document.getElementById('fromAccount');
        
        if (accounts.length === 0) {
            accountsList.innerHTML = '<p>No accounts found. Please request an account first.</p>';
            return;
        }
        
        // Display accounts
        let accountsHTML = '<div class="table-container"><table class="table"><thead><tr><th>Account Number</th><th>Type</th><th>Balance</th><th>Status</th></tr></thead><tbody>';
        
        accounts.forEach(account => {
            accountsHTML += `
                <tr>
                    <td>${account.accountNumber}</td>
                    <td>${account.accountType}</td>
                    <td>₹${account.balance.toLocaleString()}</td>
                    <td><span class="status-badge status-${account.status.toLowerCase()}">${account.status}</span></td>
                </tr>
            `;
        });
        
        accountsHTML += '</tbody></table></div>';
        accountsList.innerHTML = accountsHTML;
        
        // Populate transfer form
        if (fromAccountSelect) {
            fromAccountSelect.innerHTML = '<option value="">Select Account</option>';
            accounts.filter(acc => acc.status === 'ACTIVE').forEach(account => {
                fromAccountSelect.innerHTML += `<option value="${account.accountNumber}">${account.accountNumber} (₹${account.balance.toLocaleString()})</option>`;
            });
        }
        
    } catch (error) {
        showNotification('Failed to load accounts: ' + error.message, 'error');
    }
}

async function checkPinStatus() {
    try {
        const response = await fetch(`${API_BASE}/users/${currentUser.id}/has-pin`);
        const hasPin = await response.json();
        
        const pinStatus = document.getElementById('pinStatus');
        const createPinForm = document.getElementById('createPinForm');
        
        if (hasPin) {
            pinStatus.innerHTML = '<p style="color: green;"><i class="fas fa-check-circle"></i> Transfer PIN is already created and active.</p>';
            createPinForm.style.display = 'none';
        } else {
            pinStatus.innerHTML = '<p style="color: orange;"><i class="fas fa-exclamation-triangle"></i> No transfer PIN found. Create one to enable money transfers.</p>';
            createPinForm.style.display = 'block';
        }
    } catch (error) {
        document.getElementById('pinStatus').innerHTML = '<p style="color: red;">Failed to check PIN status.</p>';
    }
}

async function createTransferPin() {
    const newPin = document.getElementById('newPin').value;
    const confirmPin = document.getElementById('confirmPin').value;
    
    if (!newPin || newPin.length !== 6 || !/^\d{6}$/.test(newPin)) {
        showNotification('PIN must be exactly 6 digits', 'error');
        return;
    }
    
    if (newPin !== confirmPin) {
        showNotification('PINs do not match', 'error');
        return;
    }
    
    try {
        showLoading();
        const response = await fetch(`${API_BASE}/users/${currentUser.id}/create-pin`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({ pin: newPin })
        });
        
        if (response.ok) {
            showNotification('Transfer PIN created successfully!', 'success');
            document.getElementById('newPin').value = '';
            document.getElementById('confirmPin').value = '';
            checkPinStatus(); // Refresh PIN status
        } else {
            const error = await response.text();
            showNotification('Failed to create PIN: ' + error, 'error');
        }
    } catch (error) {
        showNotification('Failed to create PIN: ' + error.message, 'error');
    } finally {
        hideLoading();
    }
}

let currentTransferSession = null;
let transferPinViewed = false;

async function handleTransfer(e) {
    e.preventDefault();
    
    const fromAccount = document.getElementById('fromAccount').value;
    const transferType = document.getElementById('transferType').value;
    const transferTo = document.getElementById('transferTo').value;
    const amount = document.getElementById('transferAmount').value;
    const description = document.getElementById('transferDescription').value || 'Money transfer';
    
    try {
        showLoading();
        
        // Check if user has a transfer PIN
        const pinCheckResponse = await fetch(`${API_BASE}/users/${currentUser.id}/has-pin`);
        const hasPin = await pinCheckResponse.json();
        
        if (!hasPin) {
            hideLoading();
            showNotification('Please create a transfer PIN first in My Accounts section', 'error');
            return;
        }
        
        hideLoading();
        
        // Show PIN input dialog
        showTransferPinDialog(fromAccount, transferType, transferTo, amount, description);
        
    } catch (error) {
        showNotification('Transfer initiation failed: ' + error.message, 'error');
        hideLoading();
    }
}

function showTransferPinDialog(fromAccount, transferType, transferTo, amount, description) {
    const modal = document.createElement('div');
    modal.className = 'modal';
    modal.style.display = 'block';
    modal.innerHTML = `
        <div class="modal-content">
            <div class="modal-header">
                <h2>Enter Transfer PIN</h2>
            </div>
            <div class="modal-body">
                <div class="transfer-summary">
                    <h3>Transfer Details:</h3>
                    <p><strong>From:</strong> ${fromAccount}</p>
                    <p><strong>To:</strong> ${transferTo} (${transferType})</p>
                    <p><strong>Amount:</strong> ₹${parseFloat(amount).toLocaleString()}</p>
                    <p><strong>Description:</strong> ${description}</p>
                </div>
                <div class="pin-section" style="margin-top: 1rem;">
                    <label for="transferPinInput">Enter your 6-digit transfer PIN:</label>
                    <input type="password" id="transferPinInput" maxlength="6" placeholder="Enter PIN" style="margin: 0.5rem 0; padding: 0.5rem; font-size: 1.2rem; text-align: center; letter-spacing: 0.2rem; width: 100%;">
                    <div class="modal-actions" style="margin-top: 1rem;">
                        <button class="btn btn-success" onclick="confirmCustomPinTransfer('${fromAccount}', '${transferType}', '${transferTo}', '${amount}', '${description}')">
                            <i class="fas fa-check"></i> Confirm Transfer
                        </button>
                        <button class="btn btn-secondary" onclick="cancelTransfer()">
                            <i class="fas fa-times"></i> Cancel
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    // Focus on PIN input
    setTimeout(() => {
        document.getElementById('transferPinInput').focus();
    }, 100);
}

async function viewTransferPin(sessionId) {
    try {
        const response = await fetch(`${API_BASE}/pin/view/${sessionId}`);
        const data = await response.json();
        
        if (response.ok) {
            document.getElementById('pinNumber').textContent = data.pin;
            document.getElementById('pinDisplay').style.display = 'block';
            document.getElementById('viewPinBtn').style.display = 'none';
            document.getElementById('pinInputSection').style.display = 'block';
            transferPinViewed = true;
            
            showNotification(data.message, 'info');
        } else {
            showNotification('Failed to retrieve PIN: ' + data.message, 'error');
        }
    } catch (error) {
        showNotification('Failed to retrieve PIN: ' + error.message, 'error');
    }
}

async function confirmCustomPinTransfer(fromAccount, transferType, transferTo, amount, description) {
    const pinInput = document.getElementById('transferPinInput').value;
    
    if (!pinInput || pinInput.length !== 6) {
        showNotification('Please enter a valid 6-digit PIN', 'error');
        return;
    }
    
    try {
        showLoading();
        
        const endpoint = transferType === 'phone' ? 'transfer/by-phone' : 'transfer/by-account';
        const paramName = transferType === 'phone' ? 'toPhoneNumber' : 'toAccountNumber';
        
        const formData = new URLSearchParams({
            fromAccountNumber: fromAccount,
            [paramName]: transferTo,
            amount: amount,
            description: description,
            userId: currentUser.id,
            pin: pinInput
        });
        
        const response = await fetch(`${API_BASE}/transactions/${endpoint}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData
        });
        
        if (response.ok) {
            const transaction = await response.json();
            showNotification(`Transfer successful! Transaction ID: ${transaction.transactionId}`, 'success');
            document.getElementById('transferForm').reset();
            loadCustomerAccounts(); // Refresh balances
            cancelTransfer(); // Close modal
        } else {
            const error = await response.text();
            showNotification('Transfer failed: ' + error, 'error');
        }
    } catch (error) {
        showNotification('Transfer failed: ' + error.message, 'error');
    } finally {
        hideLoading();
    }
}

function cancelTransfer() {
    const modal = document.querySelector('.modal');
    if (modal) {
        document.body.removeChild(modal);
    }
    currentTransferSession = null;
    transferPinViewed = false;
}

async function handleAccountRequest(e) {
    e.preventDefault();
    
    const requestData = {
        userId: currentUser.id,
        accountType: document.getElementById('accountType').value,
        initialDeposit: parseFloat(document.getElementById('initialDeposit').value) || 0
    };
    
    try {
        showLoading();
        const response = await fetch(`${API_BASE}/account-requests`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestData)
        });
        
        if (response.ok) {
            showNotification('Account request submitted successfully!', 'success');
            document.getElementById('accountRequestForm').reset();
            loadCustomerRequests();
        } else {
            const error = await response.text();
            showNotification('Request failed: ' + error, 'error');
        }
    } catch (error) {
        showNotification('Request failed: ' + error.message, 'error');
    } finally {
        hideLoading();
    }
}

async function loadCustomerRequests() {
    try {
        const response = await fetch(`${API_BASE}/account-requests/user/${currentUser.id}`);
        const requests = await response.json();
        
        const requestsList = document.getElementById('requestsList');
        
        if (requests.length === 0) {
            requestsList.innerHTML = '<p>No account requests found.</p>';
            return;
        }
        
        let requestsHTML = '<div class="table-container"><table class="table"><thead><tr><th>Request ID</th><th>Account Type</th><th>Initial Deposit</th><th>Status</th><th>Created</th></tr></thead><tbody>';
        
        requests.forEach(request => {
            requestsHTML += `
                <tr>
                    <td>${request.requestId}</td>
                    <td>${request.accountType}</td>
                    <td>₹${request.initialDeposit.toLocaleString()}</td>
                    <td><span class="status-badge status-${request.status.toLowerCase()}">${request.status}</span></td>
                    <td>${new Date(request.createdAt).toLocaleDateString()}</td>
                </tr>
            `;
        });
        
        requestsHTML += '</tbody></table></div>';
        requestsList.innerHTML = requestsHTML;
        
    } catch (error) {
        showNotification('Failed to load requests: ' + error.message, 'error');
    }
}

async function loadTransactionHistory() {
    try {
        // First, get user's accounts to determine transaction direction
        const accountsResponse = await fetch(`${API_BASE}/accounts/user/${currentUser.id}`);
        const userAccounts = await accountsResponse.json();
        const userAccountNumbers = userAccounts.map(account => account.accountNumber);
        
        const response = await fetch(`${API_BASE}/transactions/history/phone/${currentUser.phoneNumber}`);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const transactions = await response.json();
        const historyContainer = document.getElementById('transactionHistory');
        
        // Check if transactions is actually an array
        if (!Array.isArray(transactions)) {
            console.error('Expected array but got:', transactions);
            historyContainer.innerHTML = '<p>Error: Invalid response format from server.</p>';
            return;
        }
        
        if (transactions.length === 0) {
            historyContainer.innerHTML = '<p>No transactions found.</p>';
            return;
        }
        
        let historyHTML = '<div class="table-container"><table class="table"><thead><tr><th>Transaction ID</th><th>Type</th><th>Amount</th><th>Direction</th><th>Description</th><th>Status</th><th>Date</th></tr></thead><tbody>';
        
        transactions.forEach(transaction => {
            // Determine transaction direction and color
            let amountColor = '#333'; // default color
            let direction = 'Unknown';
            let amountPrefix = '₹';
            
            if (transaction.transactionType === 'DEPOSIT') {
                amountColor = 'green';
                direction = 'Credit';
                amountPrefix = '+₹';
            } else if (transaction.transactionType === 'WITHDRAWAL') {
                amountColor = 'red';
                direction = 'Debit';
                amountPrefix = '-₹';
            } else if (transaction.transactionType === 'TRANSFER') {
                // Check if it's outgoing or incoming transfer
                const fromAccountNumber = transaction.fromAccount ? transaction.fromAccount.accountNumber : null;
                const toAccountNumber = transaction.toAccount ? transaction.toAccount.accountNumber : null;
                
                if (fromAccountNumber && userAccountNumbers.includes(fromAccountNumber)) {
                    // Outgoing transfer (user sent money)
                    amountColor = 'red';
                    direction = 'Sent';
                    amountPrefix = '-₹';
                } else if (toAccountNumber && userAccountNumbers.includes(toAccountNumber)) {
                    // Incoming transfer (user received money)
                    amountColor = 'green';
                    direction = 'Received';
                    amountPrefix = '+₹';
                }
            }
            
            historyHTML += `
                <tr>
                    <td>${transaction.transactionId}</td>
                    <td>${transaction.transactionType}</td>
                    <td style="color: ${amountColor}; font-weight: bold;">${amountPrefix}${transaction.amount.toLocaleString()}</td>
                    <td><span style="color: ${amountColor}; font-weight: bold;">${direction}</span></td>
                    <td>${transaction.description || '-'}</td>
                    <td><span class="status-badge status-${transaction.status.toLowerCase()}">${transaction.status}</span></td>
                    <td>${new Date(transaction.transactionDate).toLocaleString()}</td>
                </tr>
            `;
        });
        
        historyHTML += '</tbody></table></div>';
        historyContainer.innerHTML = historyHTML;
        
    } catch (error) {
        console.error('Transaction history error:', error);
        showNotification('Failed to load transaction history: ' + error.message, 'error');
    }
}

// Staff functions
async function loadPendingRequests() {
    try {
        const response = await fetch(`${API_BASE}/account-requests/pending`);
        const requests = await response.json();
        
        const pendingContainer = document.getElementById('pendingRequests');
        
        if (requests.length === 0) {
            pendingContainer.innerHTML = '<p>No pending requests found.</p>';
            return;
        }
        
        let requestsHTML = '<div class="table-container"><table class="table"><thead><tr><th>Request ID</th><th>Customer</th><th>Account Type</th><th>Initial Deposit</th><th>Created</th><th>Actions</th></tr></thead><tbody>';
        
        requests.forEach(request => {
            requestsHTML += `
                <tr>
                    <td>${request.requestId}</td>
                    <td>${request.user.firstName} ${request.user.lastName}<br><small>${request.user.phoneNumber}</small></td>
                    <td>${request.accountType}</td>
                    <td>₹${request.initialDeposit.toLocaleString()}</td>
                    <td>${new Date(request.createdAt).toLocaleDateString()}</td>
                    <td>
                        <button class="btn btn-success" onclick="approveRequest(${request.id})">
                            <i class="fas fa-check"></i> Approve
                        </button>
                        <button class="btn btn-danger" onclick="rejectRequest(${request.id})">
                            <i class="fas fa-times"></i> Reject
                        </button>
                    </td>
                </tr>
            `;
        });
        
        requestsHTML += '</tbody></table></div>';
        pendingContainer.innerHTML = requestsHTML;
        
    } catch (error) {
        showNotification('Failed to load pending requests: ' + error.message, 'error');
    }
}

async function approveRequest(requestId) {
    const comments = prompt('Enter approval comments (optional):') || 'Approved after verification';
    
    try {
        showLoading();
        const formData = new URLSearchParams({
            staffId: currentUser.id,
            comments: comments
        });
        
        const response = await fetch(`${API_BASE}/account-requests/${requestId}/approve`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData
        });
        
        if (response.ok) {
            showNotification('Account request approved successfully!', 'success');
            loadPendingRequests();
        } else {
            const error = await response.text();
            showNotification('Approval failed: ' + error, 'error');
        }
    } catch (error) {
        showNotification('Approval failed: ' + error.message, 'error');
    } finally {
        hideLoading();
    }
}

async function rejectRequest(requestId) {
    const reason = prompt('Enter rejection reason:');
    if (!reason) return;
    
    const comments = prompt('Enter additional comments (optional):') || 'Request rejected';
    
    try {
        showLoading();
        const formData = new URLSearchParams({
            staffId: currentUser.id,
            rejectionReason: reason,
            comments: comments
        });
        
        const response = await fetch(`${API_BASE}/account-requests/${requestId}/reject`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData
        });
        
        if (response.ok) {
            showNotification('Account request rejected successfully!', 'success');
            loadPendingRequests();
        } else {
            const error = await response.text();
            showNotification('Rejection failed: ' + error, 'error');
        }
    } catch (error) {
        showNotification('Rejection failed: ' + error.message, 'error');
    } finally {
        hideLoading();
    }
}

async function loadAllUsers() {
    try {
        const response = await fetch(`${API_BASE}/users`);
        const users = await response.json();
        
        const usersContainer = document.getElementById('usersList');
        
        let usersHTML = '<div class="table-container"><table class="table"><thead><tr><th>Name</th><th>Email</th><th>Phone</th><th>Role</th><th>Created</th></tr></thead><tbody>';
        
        users.forEach(user => {
            usersHTML += `
                <tr>
                    <td>${user.firstName} ${user.lastName}</td>
                    <td>${user.email}</td>
                    <td>${user.phoneNumber}</td>
                    <td><span class="status-badge status-${user.role.toLowerCase()}">${user.role}</span></td>
                    <td>${new Date(user.createdAt).toLocaleDateString()}</td>
                </tr>
            `;
        });
        
        usersHTML += '</tbody></table></div>';
        usersContainer.innerHTML = usersHTML;
        
    } catch (error) {
        showNotification('Failed to load users: ' + error.message, 'error');
    }
}

async function loadAllAccounts() {
    try {
        const response = await fetch(`${API_BASE}/accounts`);
        const accounts = await response.json();
        
        const accountsContainer = document.getElementById('allAccounts');
        
        let accountsHTML = '<div class="table-container"><table class="table"><thead><tr><th>Account Number</th><th>Customer</th><th>Type</th><th>Balance</th><th>Status</th></tr></thead><tbody>';
        
        accounts.forEach(account => {
            accountsHTML += `
                <tr>
                    <td>${account.accountNumber}</td>
                    <td>${account.user.firstName} ${account.user.lastName}</td>
                    <td>${account.accountType}</td>
                    <td>₹${account.balance.toLocaleString()}</td>
                    <td><span class="status-badge status-${account.status.toLowerCase()}">${account.status}</span></td>
                </tr>
            `;
        });
        
        accountsHTML += '</tbody></table></div>';
        accountsContainer.innerHTML = accountsHTML;
        
    } catch (error) {
        showNotification('Failed to load accounts: ' + error.message, 'error');
    }
}

async function loadAllTransactions() {
    try {
        const response = await fetch(`${API_BASE}/transactions`);
        const transactions = await response.json();
        
        const transactionsContainer = document.getElementById('allTransactions');
        
        let transactionsHTML = '<div class="table-container"><table class="table"><thead><tr><th>Transaction ID</th><th>Type</th><th>Amount</th><th>From</th><th>To</th><th>Status</th><th>Date</th></tr></thead><tbody>';
        
        transactions.forEach(transaction => {
            const fromAccount = transaction.fromAccount ? transaction.fromAccount.accountNumber : '-';
            const toAccount = transaction.toAccount ? transaction.toAccount.accountNumber : '-';
            
            transactionsHTML += `
                <tr>
                    <td>${transaction.transactionId}</td>
                    <td>${transaction.transactionType}</td>
                    <td>₹${transaction.amount.toLocaleString()}</td>
                    <td>${fromAccount}</td>
                    <td>${toAccount}</td>
                    <td><span class="status-badge status-${transaction.status.toLowerCase()}">${transaction.status}</span></td>
                    <td>${new Date(transaction.transactionDate).toLocaleString()}</td>
                </tr>
            `;
        });
        
        transactionsHTML += '</tbody></table></div>';
        transactionsContainer.innerHTML = transactionsHTML;
        
    } catch (error) {
        showNotification('Failed to load transactions: ' + error.message, 'error');
    }
}

// Utility functions
function showLogin() {
    closeModal('registerModal');
    loginModal.style.display = 'block';
}

function showRegister() {
    closeModal('loginModal');
    registerModal.style.display = 'block';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <div style="display: flex; justify-content: space-between; align-items: center;">
            <span>${message}</span>
            <button onclick="this.parentElement.parentElement.remove()" style="background: none; border: none; font-size: 18px; cursor: pointer;">&times;</button>
        </div>
    `;
    
    notificationContainer.appendChild(notification);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (notification.parentElement) {
            notification.remove();
        }
    }, 5000);
}

function showLoading() {
    // You can implement a loading spinner here
    console.log('Loading...');
}

function hideLoading() {
    // Hide loading spinner
    console.log('Loading complete');
}
