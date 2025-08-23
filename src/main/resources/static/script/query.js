

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

  resetPaymentChoiceModal();
  const tickets = parseInt(document.getElementById('ticket-count').textContent);
  const amount = tickets * houseEntryFee;
  document.getElementById('paymentAmount').textContent = amount;
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
    // Scroll to payment section
    document.getElementById('payment-section').scrollIntoView({behavior: 'smooth',  block: 'start',  });
  } else {

    registerAndPayLater();

  }
});


$(document).off('click', '#googlePayBtn,#phonePeBtn,#paytmBtn').on('click', '#googlePayBtn,#phonePeBtn,#paytmBtn', function () {
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
    alert('Please select a payment proof file');
    return;
  }
  registerAndUploadPayment(file);


});

function registerAndGetQr() {
  $.ajax({
    url: `/api/users/register`, // Replace with your actual endpoint
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({
      name: $('#name').val(),
      phone: $('#phone').val(),
      email: $('#email').val(),
      payNow: true,
      tickets: $('#ticket-count').text()
    }),
    beforeSend: showPageLoader,
    complete: hidePageLoader,
    success: function (response) {
      if (response.status === 'SUCCESS') {
        $('#qrCodePlaceholder').attr('src', response.data.paymentQrBase64);
        $('.upi-button').attr('href', response.data.upiUri);
        document.getElementById('paymentDetails').scrollIntoView({
          behavior: 'smooth',
          block: 'start',
        });

      }else{
        showBlockingModal(response.msg, function () {
          location.reload(true);
        });
      }
    },
    error: function (xhr, status, error) {
      alert('something went wrong:', error);
    }
  });
}


function registerAndUploadPayment(file) {
  const formData = new FormData();
  formData.append('file', file);
  $.ajax({
    url: `/api/users/add-payment-proof?phone=${$('#phone').val()}&email=${$('#email').val()}`, // Replace with your actual endpoint
    type: 'POST',
    processData: false,
    contentType: false,
    data: formData,
    beforeSend: showPageLoader,
    complete: hidePageLoader,
    success: function (response) {
      if (response.status === 'SUCCESS') {

        const message = `<h2>Registration Complete ✅</h2>
                          <p>
                            Your registration is complete, and your payment is currently under verification.<br>
                            Verification may take up to <strong>2 hours</strong>. You will receive a confirmation email once it’s processed.<br><br>
                            For faster confirmation, you can message us on WhatsApp by clicking the icon below.
                          </p>
                          <a class="whatsapp-btn" href="https://wa.me/8793723317" target="_blank">
                            <img width="24" height="24" class="whatsapp-icon" src="https://upload.wikimedia.org/wikipedia/commons/6/6b/WhatsApp.svg" alt="WhatsApp">
                            Message Us
                          </a>`;

        showBlockingModal(message, function () {
          location.reload(true);
        });
      }else{
         showBlockingModal(response.msg, function () {
          location.reload(true);
        });
      }
    },
    error: function (xhr, status, error) {
      alert('something went wrong:', error);
    }
  });
}


function registerAndPayLater() {
  $.ajax({
    url: `/api/users/register`, // Replace with your actual endpoint
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({
      name: $('#name').val(),
      phone: $('#phone').val(),
      email: $('#email').val(),
      payNow: false,
      tickets: $('#ticket-count').text()
    }),
    beforeSend: showPageLoader,
    complete: hidePageLoader,
    success: function (response) {

      showBlockingModal(response.msg, function () {
        location.reload(true);
      });

    },
    error: function (xhr, status, error) {
      alert('something went wrong:', error);
    }
  });
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




function resetPaymentChoiceModal() {
    document.querySelectorAll('.payment-option').forEach(opt => {
        opt.classList.remove('active');
    });

    document.getElementById('confirmPaymentBtn').style.display = 'none';
    document.getElementById('paymentDetails').style.display = 'none';

    $('#paymentStatusText').text("I've Made Payment");
    document.getElementById('qrCodePlaceholder').src = '';
    document.querySelectorAll('.upi-button').forEach(link => {
        link.removeAttribute('href');
    });

    document.getElementById('fileName').textContent = '';
    document.getElementById('paymentFile').value = ''; 
}

  // document.querySelectorAll('.register-scroll-btn').forEach(button => {
  //   button.addEventListener('click', function() {
  //     // Add smooth scrolling animation
  //     const targetSection = document.getElementById('user-section');
  //     const offset = 100; // Adjust scroll offset if needed
      
  //     window.scrollTo({
  //       top: targetSection.offsetTop - offset,
  //       behavior: 'smooth'
  //     });
  //   });
  // });


			
$(document).ready(function () {
    function setCarouselHeight() {
        var maxHeight = 0
        $('.carousel-inner .game-card').css('height', 'auto');

        $('#gameCarousel .carousel-item').each(function () {
            var itemHeight = $(this).outerHeight(true); // true includes margin
            if (itemHeight > maxHeight) {
                maxHeight = itemHeight;
            }
        });

        $('.carousel-inner .game-card').css('height', maxHeight + 'px');
    }

    $(window).on('load', function () {
        setCarouselHeight();
    });

    $(window).on('resize', function () {
        setCarouselHeight();
    });
});
