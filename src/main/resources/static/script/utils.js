const adminBaseUrl = `${window.AppConfig.baseUrl}/api/admin`;
const userBaseUrl = `${window.AppConfig.baseUrl}/api/users`;
const houseEntryFee = 50;

function showBlockingModal(message, callback) {
    // Create modal elements
    const modalBackdrop = document.createElement('div');
    modalBackdrop.className = 'blocking-modal-backdrop';

    const modalContent = document.createElement('div');
    modalContent.className = 'blocking-modal-content';

    const modalHeader = document.createElement('div');
    modalHeader.className = 'blocking-modal-header';
    modalHeader.innerHTML = '<i class="fas fa-exclamation-circle me-2"></i>Attention Required';

    const modalBody = document.createElement('div');
    modalBody.className = 'blocking-modal-body';
    modalBody.innerHTML = `<p>${message}</p>`;

    const modalFooter = document.createElement('div');
    modalFooter.className = 'blocking-modal-footer';

    const okButton = document.createElement('button');
    okButton.className = 'btn btn-primary btn-lg';
    okButton.textContent = 'OK';

    // Assemble modal
    modalFooter.appendChild(okButton);
    modalContent.appendChild(modalHeader);
    modalContent.appendChild(modalBody);
    modalContent.appendChild(modalFooter);
    modalBackdrop.appendChild(modalContent);

    // Add to document
    document.body.appendChild(modalBackdrop);

    // Add event listener
    okButton.addEventListener('click', function () {
        document.body.removeChild(modalBackdrop);
        if (typeof callback === 'function') {
            callback();
        }
    });

}


function showConfirmBlockingModal(message, successCallback, failedCallback) {
    // Create modal elements
    const modalBackdrop = document.createElement('div');
    modalBackdrop.className = 'blocking-modal-backdrop';

    const modalContent = document.createElement('div');
    modalContent.className = 'blocking-modal-content';

    const modalHeader = document.createElement('div');
    modalHeader.className = 'blocking-modal-header';
    modalHeader.innerHTML = '<i class="fas fa-exclamation-circle me-2"></i>Attention Required';

    const modalBody = document.createElement('div');
    modalBody.className = 'blocking-modal-body';
    modalBody.innerHTML = `<p>${message}</p>`;

    const modalFooter = document.createElement('div');
    modalFooter.className = 'blocking-modal-footer';

    const cancelButton = document.createElement('button');
    cancelButton.className = 'btn btn-secondary btn-lg rounded-5 mx-2';
    cancelButton.textContent = 'Cancel';

    const okButton = document.createElement('button');
    okButton.className = 'btn btn-primary btn-lg mx-2';
    okButton.textContent = 'OK';

    // Assemble modal
    modalFooter.appendChild(cancelButton);
    modalFooter.appendChild(okButton);
    modalContent.appendChild(modalHeader);
    modalContent.appendChild(modalBody);
    modalContent.appendChild(modalFooter);
    modalBackdrop.appendChild(modalContent);

    // Add to document
    document.body.appendChild(modalBackdrop);

    // Add event listener
    okButton.addEventListener('click', function () {
        document.body.removeChild(modalBackdrop);
        if (typeof successCallback === 'function') {
            successCallback();
        }
    });

    cancelButton.addEventListener('click', function () {
        document.body.removeChild(modalBackdrop);
        if (typeof failedCallback === 'function') {
            failedCallback();
        }
    });
}



$(document).on('keypress paste change', '.mob-only', function (e) {
    if (e.type == "paste") {
        if (!e.originalEvent.clipboardData.getData('Text').match(/^[6-9]{1}[0-9]{9}$/)) {
            e.preventDefault();
            return false;
        }
    } else if (e.type == "change") {
        console.log('..')
        if (!this.value.match(/^[6-9]{1}[0-9]{9}$/)) {
            this.value = '';
            return false;
        }
    } else {
        if (this.value.length == 10) {
            e.preventDefault();
            return false;
        }
        else if (this.value.length == 0) {
            var regex = new RegExp("[6-9]");
        }
        else {
            var regex = new RegExp("[0-9]");
        }
        var str = String.fromCharCode(!e.charCode ? e.which : e.charCode);
        if (regex.test(str)) {
            return true;
        }
        e.preventDefault();
        return false;

    }
});

$(document).on('change keypress paste', '.email-only', function (e) {
    if (e.type == 'paste') {
        e.preventDefault();
    }
    else if (e.type == 'keypress') {
        if ($(this).val().length > 100) {
            e.preventDefault();
        }
        var charCode = e.which || e.keyCode;
        if (!/[A-Za-z0-9.%+@-]/.test(String.fromCharCode(charCode))) {

            e.preventDefault();
        }
    } else {
        if (!this.value.trim().match(/^[^\s@]+@[^\s@]+\.[a-zA-Z]+$/)) {
            try { toastInfo("Please Enter A Valid Email Address!!"); } catch (e) {
                alert("Please Enter A Valid Email Address!!")
            }
            $(this).val('');
            return false;
        }
    }
});


window.addEventListener('load', function () {
    setTimeout(function () {
        document.querySelector('.loading-overlay').style.opacity = '0';
        document.querySelector('.loading-overlay').style.visibility = 'hidden';
    }, 500); // slight delay for smoother transition
});

function showPageLoader() {
    document.querySelector('.loading-overlay').style.opacity = '1';
    document.querySelector('.loading-overlay').style.visibility = 'visible';

}

function hidePageLoader() {
    setTimeout(function () {
        document.querySelector('.loading-overlay').style.opacity = '0';
        document.querySelector('.loading-overlay').style.visibility = 'hidden';
    }, 500);
}


function showToast(message, type) {
    // Create toast element
    const toast = $(`
            <div class="toast align-items-center text-white bg-${type} border-0" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="d-flex">
                    <div class="toast-body">
                        ${message}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
            </div>
        `);

    $('.toast-container').append(toast);
    const bsToast = new bootstrap.Toast(toast[0]);
    bsToast.show();

    // Remove toast after it hides
    toast.on('hidden.bs.toast', function () {
        toast.remove();
    });
}

  function handleError(xhr) {
        hidePageLoader();
        const errorMessage = xhr.responseJSON && xhr.responseJSON.msg ? 
            xhr.responseJSON.msg : 'An error occurred';
        showToast(errorMessage, 'danger');
    }

   