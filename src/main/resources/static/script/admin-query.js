
        // State variables
        let currentPage = 0;
        let pageSize = 10;
        let paymentFilter = null;
        let searchQuery = '';
        let totalItems = 0;
        let totalPages = 0;
        
        // Base URL for API
        const adminBaseUrl = 'http://localhost:9090/api/admin';
        const userBaseUrl = 'http://localhost:9090/api/user';
        
        // DOM Elements
        const registrationsBody = document.getElementById('registrationsBody');
        const pagination = document.getElementById('pagination');
        const paginationInfo = document.getElementById('paginationInfo');
        const loadingOverlay = document.querySelector('.loading-overlay');
        const searchInput = document.getElementById('searchInput');
        const filterOptions = document.querySelectorAll('.filter-option');
        
        // Stats elements
        const totalRegistrationsEl = document.getElementById('total-registrations');
        const confirmedCountEl = document.getElementById('confirmed-count');
        const pendingCountEl = document.getElementById('pending-count');
        const totalAmountEl = document.getElementById('total-amount');
        
        // Initialize
        document.addEventListener('DOMContentLoaded', function() {
            fetchRegistrations();
            
            // Add event listeners
            searchInput.addEventListener('keyup', function(e) {
                if (e.key === 'Enter') {
                    searchQuery = this.value;
                    currentPage = 0;
                    fetchRegistrations();
                }
            });
            
            filterOptions.forEach(option => {
                option.addEventListener('click', function(e) {
                    e.preventDefault();
                    const filter = this.getAttribute('data-filter');
                    
                    // Update filter
                    if (filter === 'all') {
                        paymentFilter = null;
                    } else if (filter === 'confirmed') {
                        paymentFilter = true;
                    } else if (filter === 'pending') {
                        paymentFilter = false;
                    }
                    
                    currentPage = 0;
                    fetchRegistrations();
                });
            });
        });
        
        // Fetch registrations
        function fetchRegistrations() {
            showLoading(true);
            
            // Build URL with query parameters
            let url = `${adminBaseUrl}/registrations?page=${currentPage}&size=${pageSize}`;
            
            if (paymentFilter !== null) {
                url += `&paymentConfirmed=${paymentFilter}`;
            }
            
            if (searchQuery) {
                url += `&query=${encodeURIComponent(searchQuery)}`;
            }
            
            fetch(url)
                .then(response => response.json())
                .then(data => {
                    if (data.status === 'SUCCESS') {
                        const registrations = data.data.registrations;
                        totalItems = data.data.totalItems;
                        totalPages = data.data.totalPages;
                        
                        // Update stats
                        updateStats(data.data);
                        
                        // Render table
                        renderRegistrations(registrations);
                        
                        // Update pagination
                        updatePagination();
                        
                        // Update pagination info
                        const startItem = currentPage * pageSize + 1;
                        const endItem = Math.min((currentPage + 1) * pageSize, totalItems);
                        paginationInfo.textContent = `Showing ${startItem} to ${endItem} of ${totalItems} entries`;
                    } else {
                        showToast('Error fetching data', 'danger');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showToast('Error fetching data', 'danger');
                })
                .finally(() => {
                    showLoading(false);
                });
        }
        
        // Update stats cards
        function updateStats(data) {
            // In a real app, we would have separate API for stats
            // For now, we'll calculate from the data we have
            
            // Calculate stats
            const total = data.totalItems;
            let confirmed = 0;
            let pending = 0;
            let totalAmount = 0;
            
            data.registrations.forEach(reg => {
                if (reg.paymentConfirmed) {
                    confirmed++;
                    totalAmount += reg.amountPaid;
                } else {
                    pending++;
                }
            });
            
            // Update DOM
            totalRegistrationsEl.textContent = total;
            confirmedCountEl.textContent = confirmed;
            pendingCountEl.textContent = pending;
            totalAmountEl.textContent = `₹${totalAmount}`;
        }
        
        // Render registrations table
        function renderRegistrations(registrations) {
            registrationsBody.innerHTML = '';
            
            if (registrations.length === 0) {
                registrationsBody.innerHTML = `
                    <tr>
                        <td colspan="8" class="text-center py-4">
                            <i class="fas fa-inbox fa-2x mb-2 text-muted"></i>
                            <p class="mb-0">No registrations found</p>
                        </td>
                    </tr>
                `;
                return;
            }
            
            registrations.forEach(reg => {
                const row = document.createElement('tr');
                
                // Format date
                const date = new Date(reg.createdAt);
                const formattedDate = date.toLocaleDateString('en-GB', {
                    day: '2-digit',
                    month: 'short',
                    year: 'numeric'
                });
                
                row.innerHTML = `
                    <td>${reg.id}</td>
                    <td>${reg.name}</td>
                    <td>${reg.email}</td>
                    <td>${reg.phone}</td>
                    <td>${reg.tickets}</td>
                    <td>
                        <span class="status-badge ${reg.paymentConfirmed ? 'status-confirmed' : 'status-pending'}">
                            ${reg.paymentConfirmed ? 'Confirmed' : 'Pending'}
                        </span>
                    </td>
                    <td class="date-cell">${formattedDate}</td>
                    <td>
                        ${reg.imagePath ? 
                            `<button class="btn btn-sm btn-info action-btn" onclick="downloadProof(${reg.id})">
                                <i class="fas fa-download"></i>
                            </button>` : ''
                        }
                        ${!reg.paymentConfirmed ? 
                            `<button class="btn btn-sm btn-success action-btn" onclick="confirmPayment(${reg.id})">
                                <i class="fas fa-check"></i>
                            </button>` : ''
                        }
                        <button class="btn btn-sm btn-danger action-btn" onclick="deleteRegistration(${reg.id})">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                `;
                
                registrationsBody.appendChild(row);
            });
        }
        
        // Update pagination
        function updatePagination() {
            pagination.innerHTML = '';
            
            // Previous button
            const prevDisabled = currentPage === 0 ? 'disabled' : '';
            pagination.innerHTML += `
                <li class="page-item ${prevDisabled}">
                    <a class="page-link" href="#" onclick="changePage(${currentPage - 1})">
                        <i class="fas fa-angle-left"></i>
                    </a>
                </li>
            `;
            
            // Page numbers
            const startPage = Math.max(0, currentPage - 2);
            const endPage = Math.min(totalPages, startPage + 5);
            
            for (let i = startPage; i < endPage; i++) {
                const active = i === currentPage ? 'active' : '';
                pagination.innerHTML += `
                    <li class="page-item ${active}">
                        <a class="page-link" href="#" onclick="changePage(${i})">${i + 1}</a>
                    </li>
                `;
            }
            
            // Next button
            const nextDisabled = currentPage >= totalPages - 1 ? 'disabled' : '';
            pagination.innerHTML += `
                <li class="page-item ${nextDisabled}">
                    <a class="page-link" href="#" onclick="changePage(${currentPage + 1})">
                        <i class="fas fa-angle-right"></i>
                    </a>
                </li>
            `;
        }
        
        // Change page
        function changePage(page) {
            if (page >= 0 && page < totalPages) {
                currentPage = page;
                fetchRegistrations();
            }
        }
        
        // Confirm payment
        function confirmPayment(id) {
            if (!confirm('Are you sure you want to confirm this payment?')) return;
            
            showLoading(true);
            
            fetch(`${adminBaseUrl}/confirm-payment/${id}`, {
                method: 'POST'
            })
            .then(response => response.json())
            .then(data => {
                if (data.status === 'SUCCESS') {
                    showToast('Payment confirmed successfully', 'success');
                    fetchRegistrations();
                } else {
                    showToast('Error confirming payment', 'danger');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showToast('Error confirming payment', 'danger');
            })
            .finally(() => {
                showLoading(false);
            });
        }
        
        // Delete registration
        function deleteRegistration(id) {
            if (!confirm('Are you sure you want to delete this registration?')) return;
            
            showLoading(true);
            
            fetch(`${adminBaseUrl}/delete-registration/${id}`, {
                method: 'DELETE'
            })
            .then(response => response.json())
            .then(data => {
                if (data.status === 'SUCCESS') {
                    showToast('Registration deleted successfully', 'success');
                    fetchRegistrations();
                } else {
                    showToast('Error deleting registration', 'danger');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showToast('Error deleting registration', 'danger');
            })
            .finally(() => {
                showLoading(false);
            });
        }
        
        // Download payment proof
        function downloadProof(id) {
            showLoading(true);
            
            fetch(`${adminBaseUrl}/payment-proof/${id}`)
            .then(response => {
                if (response.ok) {
                    return response.blob();
                } else {
                    throw new Error('File not found');
                }
            })
            .then(blob => {
                // Create a temporary URL for the blob
                const url = URL.createObjectURL(blob);
                
                // Create a link to download the file
                const a = document.createElement('a');
                a.href = url;
                a.download = `payment-proof-${id}.png`;
                document.body.appendChild(a);
                a.click();
                
                // Clean up
                setTimeout(() => {
                    document.body.removeChild(a);
                    URL.revokeObjectURL(url);
                }, 100);
                
                showToast('Payment proof downloaded', 'success');
            })
            .catch(error => {
                console.error('Error:', error);
                showToast('Error downloading payment proof', 'danger');
            })
            .finally(() => {
                showLoading(false);
            });
        }
        
        // Show/hide loading overlay
        function showLoading(show) {
            loadingOverlay.classList.toggle('d-none', !show);
        }
        
        // Show toast notification
        function showToast(message, type = 'info') {
            const toastContainer = document.querySelector('.toast-container');
            const toastId = `toast-${Date.now()}`;
            
            const toast = document.createElement('div');
            toast.className = `toast show bg-${type} text-white`;
            toast.setAttribute('role', 'alert');
            toast.setAttribute('aria-live', 'assertive');
            toast.setAttribute('aria-atomic', 'true');
            
            toast.innerHTML = `
                <div class="toast-body d-flex justify-content-between align-items-center">
                    <div>
                        <i class="fas ${type === 'success' ? 'fa-check-circle' : type === 'danger' ? 'fa-exclamation-circle' : 'fa-info-circle'} me-2"></i>
                        ${message}
                    </div>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
            `;
            
            toastContainer.appendChild(toast);
            
            // Auto remove after 5 seconds
            setTimeout(() => {
                toast.remove();
            }, 5000);
        }
        
        // Mobile sidebar toggle
        document.getElementById('sidebarToggle').addEventListener('click', function() {
            const sidebar = document.querySelector('.sidebar');
            sidebar.classList.toggle('d-none');
            sidebar.classList.toggle('d-block');
        });
