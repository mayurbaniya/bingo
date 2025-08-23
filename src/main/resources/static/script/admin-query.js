$(document).ready(function() {
    // Global variables
    let currentPage = 0;
    const pageSize = 10;
    let currentFilter = 'all';
    let searchQuery = '';
    let totalItems = 0;
    let totalConfirmed = 0;
    let totalPending = 0;

    // Initialize the dashboard
    loadStatistics();
    

    // Filter change handler
    $('.filter-option').click(function(e) {
        e.preventDefault();
        currentFilter = $(this).data('filter');
        currentPage = 0;
        loadRegistrations();
    });

    // Search input handler with debounce
    let searchTimer;
    $('#searchInput').on('keyup', function() {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(function() {
            searchQuery = $('#searchInput').val().trim();
            if (searchQuery.length === 0 || searchQuery.length >= 3) {
                currentPage = 0;
                loadRegistrations();
            }
        }, 500);
    });

    // Pagination handler
    $(document).on('click', '.page-link', function(e) {
        e.preventDefault();
        const page = $(this).data('page');
        if (page !== undefined) {
            currentPage = page;
            loadRegistrations();
        }
    });

    // Confirm payment handler
    $(document).on('click', '.btn-confirm', function() {
        const id = $(this).data('id');
        showConfirmBlockingModal('Are you sure you want to confirm this payment?', function() {
            confirmPayment(id);
        });

    });

    // Delete registration handler
    $(document).on('click', '.btn-delete', function() {
        const id = $(this).data('id');
        showConfirmBlockingModal('Are you sure you want to delete this registration?', function() {
            deleteRegistration(id);
        });
    });

    // View payment proof handler
    $(document).on('click', '.btn-view-proof', function() {
       showReceipt($(this).attr('imagePath'));
    });

    // Function to load statistics
    function loadStatistics() {
        showPageLoader();
        // Fetch all registrations to calculate stats
        $.get('/api/admin/registrations?page=0&size=1000')
            .done(function(response) {
                loadRegistrations();
                const totalRegistrations = response.data.totalRegistrations;
                const paymentConfirmedCount = response.data.paymentConfirmedCount;
                const paymentPendingCount = response.data.paymentPendingCount;
                const totalConfirmedAmount = response.data.totalConfirmedAmount;

                const registrations = response.data.registrations;
                totalItems = registrations.length;
                totalConfirmed = registrations.filter(r => r.paymentConfirmed).length;
                totalPending = registrations.filter(r => !r.paymentConfirmed).length;
                
                // Calculate total amount
                const totalAmount = registrations
                    .filter(r => r.paymentConfirmed)
                    .reduce((sum, reg) => sum + (reg.amountPaid || 0), 0);
                
                // Update UI
                $('#total-registrations').text(totalRegistrations);
                $('#confirmed-count').text(paymentConfirmedCount);
                $('#pending-count').text(paymentPendingCount);
                $('#total-amount').text('₹' + totalConfirmedAmount);
            })
            .fail(handleError);
    }

    // Function to load registrations
    function loadRegistrations() {
        showPageLoader();
        let url;
        let params = {
            page: currentPage,
            size: pageSize
        };

        if (searchQuery) {
            url = '/api/admin/search';
            params.query = searchQuery;
        } else {
            url = '/api/admin/registrations';
            if (currentFilter === 'confirmed') {
                params.paymentConfirmed = true;
            } else if (currentFilter === 'pending') {
                params.paymentConfirmed = false;
            }
        }

        $.get(url, params)
            .done(function(response) {
                renderTable(response.data.registrations);
                renderPagination(response.data.totalPages, currentPage);
                updatePaginationInfo(response.data.totalItems, response.data.registrations.length);
                hidePageLoader();
            })
            .fail(handleError);
    }
         

    function renderTable(registrations) {
        const $tbody = $('#registrationsBody');
        $tbody.empty();

        if (registrations.length === 0) {
            $tbody.append('<tr><td colspan="8" class="text-center">No registrations found</td></tr>');
            return;
        }

        registrations.forEach((reg,index) => {
            const date = new Date(reg.createdAt);
            const formattedDate = date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
            const statusText = reg.paymentConfirmed ? 'Confirmed' : 'Pending';
            const statusClass = reg.paymentConfirmed ? 'status-confirmed' : 'status-pending';

            const row = `
                <tr>
                    <td>${index+1}</td>
                    <td>${reg.name}</td>
                    <td>${reg.email}</td>
                    <td>${reg.phone}</td>
                    <td>${reg.tickets}</td>
                    <td><span class="status-badge ${statusClass}">${statusText}</span></td>
                    <td class="date-cell">${formattedDate}</td>
                    <td>
                        <div class="d-flex">
                            ${!reg.paymentConfirmed ? 
                                `<button class="btn btn-sm btn-success btn-confirm me-1" data-id="${reg.id}" title="Confirm Payment">
                                    <i class="fas fa-check"></i>
                                </button>` : ''}
                            <button class="btn btn-sm btn-danger btn-delete me-1" data-id="${reg.id}" title="Delete">
                                <i class="fas fa-trash"></i>
                            </button>
                            ${reg.imagePath ? 
                                `<button class="btn btn-sm btn-info btn-view-proof" imagePath="${reg.imagePath}" data-id="${reg.id}" title="View Payment Proof">
                                    <i class="fas fa-receipt"></i>
                                </button>` : ''}
                        </div>
                    </td>
                </tr>
            `;
            $tbody.append(row);
        }); 
    }

    // Function to render pagination
    function renderPagination(totalPages, currentPage) {
        const $pagination = $('#pagination');
        $pagination.empty();

        if (totalPages === 0) {
            return;
        }

        // Previous button
        const prevDisabled = currentPage <= 0 ? 'disabled' : '';
        $pagination.append(`
            <li class="page-item ${prevDisabled}">
                <a class="page-link" href="#" data-page="${currentPage - 1}">Previous</a>
            </li>
        `);

        // Page numbers
        for (let i = 0; i < totalPages; i++) {
            const active = i === currentPage ? 'active' : '';
            $pagination.append(`
                <li class="page-item ${active}">
                    <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
                </li>
            `);
        }

        // Next button
        const nextDisabled = currentPage >= totalPages - 1 ? 'disabled' : '';
        $pagination.append(`
            <li class="page-item ${nextDisabled}">
                <a class="page-link" href="#" data-page="${currentPage + 1}">Next</a>
            </li>
        `);
    }

    // Function to update pagination info
    function updatePaginationInfo(totalItems, currentCount) {
        const start = currentPage * pageSize + 1;
        const end = start + currentCount - 1;
        const text = `Showing ${start} to ${end} of ${totalItems} entries`;
        $('#paginationInfo').text(text);
    }

    // Function to confirm payment
    function confirmPayment(id) {
        showPageLoader();
        $.ajax({
            url: `/api/admin/confirm-payment/${id}`,
            type: 'POST',
            success: function(response) {
                showToast('Payment confirmed successfully', 'success');
                loadRegistrations();
                loadStatistics();
            },
            error: handleError
        });
    }

    // Function to delete registration
    function deleteRegistration(id) {
        showPageLoader();
        $.ajax({
            url: `/api/admin/delete-registration/${id}`,
            type: 'DELETE',
            success: function(response) {
                showToast('Registration deleted successfully', 'success');
                loadRegistrations();
                loadStatistics();
            },
            error: handleError
        });
    }
  
});

function showReceipt(imageUrl) {
    document.getElementById("receiptImage").src = imageUrl;
    document.getElementById("receiptOverlay").style.display = "flex";
}

function hideReceipt() {
    document.getElementById("receiptOverlay").style.display = "none";
}

async function generateConfirmedListPDF() {
    showPageLoader();
    try {
        // --- 1. Fetch Data ---
        const response = await fetch("/api/admin/get-confirmed-list");
        if (!response.ok) {
            throw new Error(`API request failed with status ${response.status}`);
        }
        const json = await response.json();
        const data = json.data || [];

      

        // --- 3. Prepare Table Data ---
        const tableBody = [
            ["S.No", "Name", "Phone", "Email", "Registration ID", "Tickets", "Amount Paid", "Payment Receipt"].map(h => ({ text: h, style: "tableHeader" }))
        ];
        data.forEach((user, index) => {
            tableBody.push([
                { text: (index + 1).toString(), style: 'cellCenter' },
                { text: user.name, style: 'cellText' },
                { text: user.phone, style: 'cellText' },
                { text: user.email, style: 'cellText' },
                { text: user.registrationId, style: 'cellText' },
                { text: user.tickets.toString(), style: 'cellCenter' },
                { text: `₹${parseFloat(user.amountPaid || 0).toFixed(2)}`, style: 'cellRight' },
                { text: "View Receipt", link: user.imagePath, style: 'cellLink' }
            ]);
        });
        
        // --- 4. Calculate Totals ---
        const totalTickets = data.reduce((sum, user) => sum + (user.tickets || 0), 0);
        const totalAmount = data.reduce((sum, user) => sum + parseFloat(user.amountPaid || 0), 0);
        if (data.length > 0) {
            tableBody.push([
                { text: "TOTAL", colSpan: 5, style: "totalLabel", alignment: "left" }, {}, {},
                { text: `Registration  ${data.length}`, style: "totalValue", alignment: "right" },{},
                { text: totalTickets.toString(), style: "totalValue", alignment: "center" },
                { text: `₹${totalAmount.toFixed(2)}`, style: "totalValue", alignment: "right" },
                { text: "", style: "totalValue" }
            ]);
        }

        // --- 5. Define PDF Document ---
        const docDefinition = {
            pageOrientation: 'landscape',
            pageMargins: [40, 80, 40, 60],

            // MODIFIED: Simplified header with only text
            header: {
                stack: [
                    { text: "Confirmed Users Report", style: "header" },
                    { text: "Ganpati Festival Registration", style: "subheader" }
                ],
                margin: [40, 25, 40, 0] // Aligns the text with page margins
            },

            footer: function(currentPage, pageCount) {
                return {
                    columns: [
                        { text: `Report generated on: ${new Date().toLocaleString('en-IN')}`, alignment: 'left', style: 'footer' },
                        { text: `Page ${currentPage.toString()} of ${pageCount}`, alignment: 'right', style: 'footer' }
                    ],
                    margin: [40, 0, 40, 0]
                };
            },

            content: [
                {
                    table: {
                        headerRows: 1,
                        widths: ['auto', '*', 'auto', '*', 'auto', 'auto', 'auto', 'auto'],
                        body: tableBody
                    },
                    layout: {
                        fillColor: function (rowIndex) {
                            if (rowIndex === 0 || rowIndex > data.length) return null;
                            return (rowIndex % 2 === 0) ? '#FFFFFF' : '#f5f5f5';
                        }
                    }
                },
                {
                    text: "* This is an auto-generated report.",
                    style: "footerNote",
                    margin: [0, 20, 0, 0]
                }
            ],
            
            styles: {
                header: { fontSize: 20, bold: true, color: '#ff6600' },
                subheader: { fontSize: 14, color: '#2c3e50' },
                tableHeader: { bold: true, fontSize: 11, color: 'white', fillColor: '#2c3e50', alignment: 'center', margin: [0, 5, 0, 5] },
                cellText: { margin: [0, 4, 0, 4] },
                cellCenter: { margin: [0, 4, 0, 4], alignment: 'center' },
                cellRight: { margin: [0, 4, 0, 4], alignment: 'right' },
                cellLink: { margin: [0, 4, 0, 4], alignment: 'center', color: '#1a73e8', decoration: 'underline' },
                totalLabel: { bold: true, fontSize: 11, fillColor: '#2c3e50', color: 'white', margin: [0, 5, 0, 5] },
                totalValue: { bold: true, fontSize: 11, fillColor: '#2c3e50', color: 'white', margin: [0, 5, 0, 5] },
                footer: { fontSize: 9, color: '#666666', italics: true },
                footerNote: { fontSize: 9, color: '#666666', italics: true }
            },
            defaultStyle: {
                font: 'Roboto',
                fontSize: 10,
                columnGap: 20
            }
        };

        // --- 6. Generate and Download PDF ---
        const fileName = `Confirmed_Users_Report_${new Date().toISOString().slice(0, 10)}.pdf`;
        pdfMake.createPdf(docDefinition).download(fileName);
        
    } catch (err) {
        console.error("Error generating PDF:", err);
        alert("Failed to generate PDF. Check the console for more details.");
    } finally {
        hidePageLoader();
    }
}