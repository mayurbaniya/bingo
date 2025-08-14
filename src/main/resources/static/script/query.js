
const adminBaseUrl = 'http://localhost:9090/api/admin';
const userBaseUrl = 'http://localhost:9090/api/users';
document.addEventListener('DOMContentLoaded', function () {
  // Get all gallery items
  // Gallery nav link click handler
  $('.gallery-link').on('click', function (e) {
    e.preventDefault();
    $('#gallery').show();
    $('html, body').animate({
      scrollTop: $('#gallery').offset().top - 70
    }, 800);
  });

  // Close gallery button
  $('#closeGallery').on('click', function () {
    $('#gallery').hide();
  });

  // Gallery lightbox functionality
  const galleryItems = document.querySelectorAll('.gallery-item');
  const lightbox = document.getElementById('lightbox');
  const lightboxImg = document.getElementById('lightbox-img');
  const lightboxCaption = document.getElementById('lightbox-caption');
  const closeBtn = document.querySelector('.lightbox-close');
  const prevBtn = document.querySelector('.lightbox-prev');
  const nextBtn = document.querySelector('.lightbox-next');

  let currentIndex = 0;
  const images = [];
  const captions = [];

  // Prepare image data
  galleryItems.forEach((item, index) => {
    const img = item.querySelector('img');
    const caption = item.querySelector('.gallery-caption');

    images.push(img.src);
    captions.push(caption.innerHTML);

    // Add click event to open lightbox
    item.addEventListener('click', () => {
      currentIndex = index;
      updateLightbox();
      lightbox.classList.add('active');
      document.body.style.overflow = 'hidden'; // Prevent scrolling
    });
  });

  // Update lightbox content
  function updateLightbox() {
    lightboxImg.src = images[currentIndex];
    lightboxCaption.innerHTML = captions[currentIndex];
  }

  // Close lightbox
  closeBtn.addEventListener('click', () => {
    lightbox.classList.remove('active');
    document.body.style.overflow = '';
  });

  // Click on background to close
  lightbox.addEventListener('click', (e) => {
    if (e.target === lightbox) {
      lightbox.classList.remove('active');
      document.body.style.overflow = '';
    }
  });

  // Previous image
  prevBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    currentIndex = (currentIndex - 1 + images.length) % images.length;
    updateLightbox();
  });

  // Next image
  nextBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    currentIndex = (currentIndex + 1) % images.length;
    updateLightbox();
  });

  // Keyboard navigation
  document.addEventListener('keydown', (e) => {
    if (lightbox.classList.contains('active')) {
      if (e.key === 'Escape') {
        lightbox.classList.remove('active');
        document.body.style.overflow = '';
      } else if (e.key === 'ArrowLeft') {
        currentIndex = (currentIndex - 1 + images.length) % images.length;
        updateLightbox();
      } else if (e.key === 'ArrowRight') {
        currentIndex = (currentIndex + 1) % images.length;
        updateLightbox();
      }
    }
  });
});

// Initialize Bootstrap components
const paymentChoiceModal = new bootstrap.Modal(document.getElementById('paymentChoiceModal'));

// Ticket counter functionality
document.getElementById('increment-ticket').addEventListener('click', function () {
  const countElement = document.getElementById('ticket-count');
  let count = parseInt(countElement.textContent);
  if (count < 10) {
    countElement.textContent = count + 1;
  }
});

document.getElementById('decrement-ticket').addEventListener('click', function () {
  const countElement = document.getElementById('ticket-count');
  let count = parseInt(countElement.textContent);
  if (count > 1) {
    countElement.textContent = count - 1;
  }
});

// Form submission
document.getElementById('registrationForm').addEventListener('submit', function (e) {
  e.preventDefault();

  // Get form values
  const name = document.getElementById('name').value;
  const email = document.getElementById('email').value;
  const phone = document.getElementById('phone').value;
  const tickets = document.getElementById('ticket-count').textContent;

  // Store user data in session for payment proof
  sessionStorage.setItem('userEmail', email);
  sessionStorage.setItem('userPhone', phone);

  // Show payment choice modal
  paymentChoiceModal.show();
});

// Payment choice handlers
document.getElementById('payNowOption').addEventListener('click', function () {
  document.querySelectorAll('.payment-option').forEach(opt => {
    opt.classList.remove('active');
  });
  this.classList.add('active');

  $('#paymentStatusText').text("I've Made Payment");
  registerAndGetQr();

  // Show payment details
  document.getElementById('paymentDetails').style.display = 'block';
  document.getElementById('confirmPaymentBtn').style.display = 'inline-block';

  // Set payment amount (600 per ticket)
  const tickets = parseInt(document.getElementById('ticket-count').textContent);
  const amount = tickets * 600;
  document.getElementById('paymentAmount').textContent = amount;
});

document.getElementById('payLaterOption').addEventListener('click', function () {
  document.querySelectorAll('.payment-option').forEach(opt => {
    opt.classList.remove('active');
  });
  this.classList.add('active');
  // Hide payment details
  document.getElementById('paymentDetails').style.display = 'none';
  document.getElementById('confirmPaymentBtn').style.display = 'inline-block';
  $('#paymentStatusText').text("Complete Registration!");
});

// Confirm payment button
document.getElementById('confirmPaymentBtn').addEventListener('click', function () {
  paymentChoiceModal.hide();

  // Check if Pay Now was selected
  const payNowSelected = document.getElementById('payNowOption').classList.contains('active');

  if (payNowSelected) {
    // Show payment proof section
    document.getElementById('payment-section').style.display = 'block';
    document.getElementById('user-section').style.display = 'none';

    // Set user email in the message
    const userEmail = sessionStorage.getItem('userEmail');
    document.getElementById('user-email').textContent = userEmail;
    document.getElementById('proof-email').value = userEmail;
    document.getElementById('proof-phone').value = sessionStorage.getItem('userPhone');

    // Scroll to payment section
    document.getElementById('payment-section').scrollIntoView({ behavior: 'smooth' });
  } else {

    registerAndPayLater();
   


  }
});



$(document).off('click', '#googlePayBtn,#phonePeBtn,#paytmBtn').on('click', '#googlePayBtn,#phonePeBtn,#paytmBtn', function () {
  showNotification('Redirecting to payment url...');
  window.open(this.href, '_blank');
});

// File input handler
document.getElementById('paymentFile').addEventListener('change', function (e) {
  if (this.files.length > 0) {
    document.getElementById('fileName').textContent = 'Selected: ' + this.files[0].name;
  }
});

// Payment proof form submission
document.getElementById('paymentProofForm').addEventListener('submit', function (e) {
  e.preventDefault();

  // Get the file
  const file = document.getElementById('paymentFile').files[0];
  if (!file) {
    showNotification('Please select a payment proof file');
    return;
  }



  const message = `<h2>Registration Complete ✅</h2>
    <p>
      Your registration is complete, and your payment is currently under verification.<br>
      Verification may take up to <strong>2 hours</strong>. You will receive a confirmation email once it’s processed.<br><br>
      For faster confirmation, you can message us on WhatsApp by clicking the icon below.
    </p>
    <a class="whatsapp-btn" href="https://wa.me/918123456789" target="_blank">
      <img width="24" height="24" class="whatsapp-icon" src="https://upload.wikimedia.org/wikipedia/commons/6/6b/WhatsApp.svg" alt="WhatsApp">
      Message Us
    </a>`;

  showBlockingModal(message, function () {
    location.reload(true);
  });
});

function registerAndGetQr() {
  $.ajax({
    url: `${userBaseUrl}/register`, // Replace with your actual endpoint
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({
      name: $('#name').val(),
      phone: $('#phone').val(),
      email: $('#email').val(),
      payNow: true,
      tickets: $('#ticket-count').text()
    }),
    success: function (response) {
      $('#qrCodePlaceholder').attr('src', response.data.paymentQrBase64);
      $('.upi-button').attr('href', response.data.upiUri);
    },
    error: function (xhr, status, error) {
      alert('something went wrong:', error);
    }
  });
}

function registerAndPayLater() {
  $.ajax({
    url: `${userBaseUrl}/register`, // Replace with your actual endpoint
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({
      name: $('#name').val(),
      phone: $('#phone').val(),
      email: $('#email').val(),
      payNow: false,
      tickets: $('#ticket-count').text()
    }),
    success: function (response) {
       showBlockingModal("Registration completed successfully! You can pay at the venue.", function () {
      location.reload(true);
    });
    },
    error: function (xhr, status, error) {
      alert('something went wrong:', error);
    }
  });
}

// Show notification function
function showNotification(message) {
  // Create notification element
  const notification = document.createElement('div');
  notification.className = 'alert alert-info alert-dismissible fade show';
  notification.role = 'alert';
  notification.innerHTML = `
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            `;

  // Add to the top of the page
  const container = document.querySelector('.container');
  container.prepend(notification);

  // Auto remove after 5 seconds
  setTimeout(() => {
    const alert = bootstrap.Alert.getOrCreateInstance(notification);
    alert.close();
  }, 5000);
}

// Animation on scroll
function checkScroll() {
  const elements = document.querySelectorAll('.animate-on-scroll');
  elements.forEach(element => {
    const position = element.getBoundingClientRect();
    if (position.top < window.innerHeight * 0.9) {
      element.classList.add('visible');
    }
  });
}

// Initial check
window.addEventListener('load', checkScroll);
window.addEventListener('scroll', checkScroll);

// Navbar scroll effect
window.addEventListener('scroll', function () {
  const navbar = document.querySelector('.navbar');
  if (window.scrollY > 50) {
    navbar.classList.add('shadow-sm');
  } else {
    navbar.classList.remove('shadow-sm');
  }
});


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
