document.addEventListener('DOMContentLoaded', ()=>{
  // element fade-in with stagger
  const panel = document.querySelector('.panel');
  if(panel){
    panel.classList.add('fade-in-up');
    const features = panel.querySelectorAll('.feature');
    features.forEach((el,i)=>{
      el.style.animation = `fadeUp .6s ${0.12*(i+1)}s backwards`; 
    });
  }
  // create transition overlay on demand (only during nav)
  function createTransitionOverlay(panelEl){
    // if already present, reuse
    let existing = panelEl.querySelector('.dotted-overlay.transition');
    if(existing) return existing;
    const overlay = document.createElement('div');
    overlay.className = 'dotted-overlay transition';
    // create a set of dots with random positions
    for(let i=0;i<18;i++){
      const d = document.createElement('div');
      d.className = 'dot';
      const left = 10 + Math.random()*80; // keep inner margins
      const top = 10 + Math.random()*80;
      const delay = (Math.random()*0.6).toFixed(2);
      d.style.left = left+'%';
      d.style.top = top+'%';
      d.style.animationDelay = delay+'s';
      overlay.appendChild(d);
    }
    panelEl.appendChild(overlay);
    return overlay;
  }

  // create centered loading overlay ("..." suspended dots) appended to body
  function createLoadingOverlay(){
    let existing = document.querySelector('.loading-overlay');
    if(existing) return existing;
    const overlay = document.createElement('div');
    overlay.className = 'loading-overlay';
    const container = document.createElement('div');
    container.className = 'loading-dots';
    for(let i=0;i<3;i++){
      const d = document.createElement('div');
      d.className = 'dot-loading';
      d.style.animationDelay = (i*0.15)+'s';
      container.appendChild(d);
    }
    overlay.appendChild(container);
    document.body.appendChild(overlay);
    return overlay;
  }

  // page transitions for internal nav links
  function setupNavTransitions(){
    const navLinks = document.querySelectorAll('.nav-links a');
    navLinks.forEach(a=>{
      a.addEventListener('click', (e)=>{
        const href = a.getAttribute('href');
        if(!href || href.startsWith('#') || href.startsWith('mailto:') || href.startsWith('http')) return; // skip external
        e.preventDefault();
        // Show centered suspended loading dots before navigating
        const loading = createLoadingOverlay();
        // reveal overlay
        requestAnimationFrame(()=> loading.classList.add('show'));
        // hold long enough so user sees the loading dots (adjustable)
        const HOLD_MS = 900;
        document.body.classList.add('page-exit');
        setTimeout(()=> window.location.href = href, HOLD_MS);
      });
    });
  }
  setupNavTransitions();

  // make sure entrance animation runs
  requestAnimationFrame(()=> document.body.classList.add('page-enter'));
});

