const buttons = document.querySelectorAll('.magic-btn');

buttons.forEach(btn => {
  btn.addEventListener('click', function () {
    const screenWidth = window.innerWidth;
    const screenHeight = window.innerHeight;

    const newLeft = Math.floor(Math.random() * (screenWidth - btn.offsetWidth));
    const newTop = Math.floor(Math.random() * (screenHeight - btn.offsetHeight));

    btn.style.left = newLeft + 'px';
    btn.style.top = newTop + 'px';
  });
});


