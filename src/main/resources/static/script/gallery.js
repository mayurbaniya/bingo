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