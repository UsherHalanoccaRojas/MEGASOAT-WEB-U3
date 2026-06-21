

document.addEventListener('DOMContentLoaded', () => {
  const AUTH_REDIRECT = '/login';

  function safePageLabel(pathname) {
    const labels = {
      '/': 'Inicio',
      '/index.html': 'Inicio',
      '/ranking.html': 'Ranking',
      '/observatorio.html': 'Observatorio',

      '/admin.html': 'Administracion',
      '/perfil.html': 'Perfil',
      '/login': 'Login',
      '/login.html': 'Login'
    };
    return labels[pathname] || pathname;
  }

  //
  function trackPageNavigation(pathname, origin) {
    const token = localStorage.getItem('megaSoatToken');
    if (!token) return;

    const currentPath = pathname || window.location.pathname;
    if (!currentPath || currentPath.startsWith('/api/')) return;

    const payload = {
      eventType: 'PAGE_NAVIGATION',
      page: safePageLabel(currentPath),
      path: currentPath,
      origin: origin || 'unknown'
    };

    fetch('http://localhost:8081/api/admin/monitoring/ui-event', {
      method: 'POST',
      headers: {
        'Authorization': 'Bearer ' + token,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload),
      keepalive: true
    }).catch(() => {
      // No bloquear la UX por errores de tracking.
    });
  }

  function hasAuthToken() {
    return Boolean(localStorage.getItem('megaSoatToken'));
  }

  function requireAuth(redirectTo = AUTH_REDIRECT) {
    if (hasAuthToken()) return true;
    window.location.replace(redirectTo);
    return false;
  }

  window.requireAuth = requireAuth;
  trackPageNavigation(window.location.pathname, 'page-load');



  // ══════════════════════════════════════
  // NAV DINÁMICA + HAMBURGER MÓVIL
  // ══════════════════════════════════════
  (function initNav() {
    const token = localStorage.getItem('megaSoatToken');
    const user = localStorage.getItem('megaSoatUser');

    if (document.body?.dataset.requiresAuth === 'true' && !token) {
      window.location.replace(AUTH_REDIRECT);
      return;
    }

    // Mostrar/ocultar items según auth (sin interferir con auth-guard para admin.html)
    document.querySelectorAll('.nav-auth').forEach(el => {
      if (el.getAttribute('href') !== '/admin.html') {
        el.style.display = token ? (el.tagName === 'A' ? 'inline-flex' : 'flex') : 'none';
      }
    });
    document.querySelectorAll('.nav-guest').forEach(el => {
      el.style.display = token ? 'none' : '';
    });

    // Nombre de usuario — inyectar avatar + nombre en el chip
    document.querySelectorAll('#userLabel').forEach(el => {
      const chip = el.closest('.user-chip');
      if (!chip) { if (user) el.textContent = user; return; }

      const username = user ? user.split('@')[0] : '?';
      const initials = username.slice(0, 2).toUpperCase();

      // Detectar rol del token JWT (payload base64)
      let rolLabel = '';
      let isSuperadmin = false;
      let isAdmin = false;
      try {
        const tok = localStorage.getItem('megaSoatToken');
        if (tok) {
          const payload = JSON.parse(atob(tok.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
          
          const rawRoles = payload.roles || '';
          const roleArray = Array.isArray(rawRoles) ? rawRoles : String(rawRoles).split(',').map(r => r.trim()).filter(Boolean);
          
          rolLabel = (roleArray[0] || '').replace('ROLE_', '');
          
          isSuperadmin = roleArray.includes('ROLE_SUPERADMIN') || roleArray.includes('SUPERADMIN');
          isAdmin = roleArray.includes('ROLE_ADMIN') || roleArray.includes('ADMIN');
        }
      } catch (e) { }

      chip.innerHTML = `
        <a class="user-chip-avatar" id="chipAvatar" href="/perfil.html" title="Configuración de perfil">${initials}</a>
        <div class="user-chip-info">
          <span class="user-chip-name">${username}</span>
          ${rolLabel ? `<span class="user-chip-role">${rolLabel}</span>` : ''}
        </div>
        <button class="btn-logout" type="button" onclick="window.logout()">Salir</button>`;

      // Cargar avatar desde API si existe
      if (token) {
        fetch('/api/users/me', { headers: { 'Authorization': 'Bearer ' + token } })
          .then(r => r.ok ? r.json() : null)
          .then(data => {
            if (!data || !data.avatarUrl) return;
            const avatarEl = document.getElementById('chipAvatar');
            if (avatarEl) {
              avatarEl.innerHTML = `<img src="${data.avatarUrl}" alt="avatar" style="width:100%;height:100%;border-radius:50%;object-fit:cover;display:block;">`;
            }
          }).catch(() => { });
      }
    });


    // Marcar enlace activo
    const path = window.location.pathname;
    function markActive(a) {
      const href = a.getAttribute('href');
      if (!href) return;
      const isHome = href === '/' && (path === '/' || path === '/index.html');
      const isOther = href !== '/' && path.includes(href.replace('.html', '').replace('/', ''));
      if (isHome || isOther) a.classList.add('active');
    }
    document.querySelectorAll('.nav-links a').forEach(markActive);

    // ── HAMBURGER MENU ──
    const topbar = document.querySelector('.topbar');
    if (!topbar) return;

    // Crear botón hamburger
    const burger = document.createElement('button');
    burger.className = 'nav-hamburger';
    burger.setAttribute('aria-label', 'Menú');
    burger.innerHTML = '<span class="bar"></span><span class="bar"></span><span class="bar"></span>';
    topbar.appendChild(burger);

    // Construir drawer móvil
    const drawer = document.createElement('div');
    drawer.className = 'mobile-drawer';

    // Cabecera del drawer
    const drawerHeader = document.createElement('div');
    drawerHeader.className = 'mobile-drawer-header';
    drawerHeader.innerHTML = `<span class="brand" style="font-size:1.1rem">Mega<span style="color:var(--accent)">SOAT</span></span>`;
    const closeBtn = document.createElement('button');
    closeBtn.className = 'mobile-drawer-close';
    closeBtn.setAttribute('aria-label', 'Cerrar');
    closeBtn.innerHTML = '×';
    drawerHeader.appendChild(closeBtn);
    drawer.appendChild(drawerHeader);

    // Links del drawer (copiados de nav-links)
    const linksContainer = document.createElement('div');
    linksContainer.className = 'mobile-drawer-links';

    // Todos los posibles links de navegación
    const allLinks = [
      { href: '/', label: 'Inicio' },
      { href: '/ranking.html', label: 'Ranking' },
      { href: '/observatorio.html', label: 'Observatorio' },

      { href: '/admin.html', label: 'Administración', auth: true },
      { href: '/perfil.html', label: 'Mi Perfil', auth: true },
    ];

    allLinks.forEach(item => {
      if (item.auth && !token) return;
      
      // Control de acceso para menú móvil
      if (item.href === '/admin.html' && !isSuperadmin) return;
      if (item.href === '/observatorio.html' && !isSuperadmin && !isAdmin) return;

      const a = document.createElement('a');
      a.href = item.href;
      a.textContent = item.label;
      markActive(a);
      linksContainer.appendChild(a);
    });



    // Acción de sesión justo debajo de los links
    if (token) {
      const logoutBtn = document.createElement('button');
      logoutBtn.className = 'btn-danger mobile-drawer-logout';
      logoutBtn.textContent = 'Cerrar sesión';
      logoutBtn.onclick = () => window.logout();
      linksContainer.appendChild(logoutBtn);
    } else {
      const loginLink = document.createElement('a');
      loginLink.href = '/login';
      loginLink.className = 'primary-button mobile-drawer-login';
      loginLink.textContent = 'Ingresar al portal';
      linksContainer.appendChild(loginLink);
    }

    drawer.appendChild(linksContainer);
    // Overlay semitransparente (backdrop)
    const overlay = document.createElement('div');
    overlay.className = 'mobile-overlay';
    document.body.appendChild(overlay);
    document.body.appendChild(drawer);

    // Toggle drawer
    function openDrawer() {
      drawer.classList.add('open');
      overlay.classList.add('open');
      burger.classList.add('open');
      document.body.style.overflow = 'hidden';
    }
    function closeDrawer() {
      drawer.classList.remove('open');
      overlay.classList.remove('open');
      burger.classList.remove('open');
      document.body.style.overflow = '';
    }

    burger.addEventListener('click', () => drawer.classList.contains('open') ? closeDrawer() : openDrawer());
    closeBtn.addEventListener('click', closeDrawer);
    overlay.addEventListener('click', closeDrawer);

    // Cerrar al hacer clic en un link
    linksContainer.querySelectorAll('a').forEach(a => {
      a.addEventListener('click', () => closeDrawer());
    });

    // Cerrar con Escape
    document.addEventListener('keydown', e => { if (e.key === 'Escape') closeDrawer(); });
  })();

  // logout global
  window.logout = function () {
    const token = localStorage.getItem('megaSoatToken');
    if (token) {
      // Usar keepalive para asegurar que el request llegue aunque la página cambie
      fetch('/api/auth/logout', {
        method: 'POST',
        headers: { 'Authorization': 'Bearer ' + token },
        keepalive: true
      }).catch(() => {});
    }
    localStorage.removeItem('megaSoatToken');
    localStorage.removeItem('megaSoatUser');
    window.location.replace(AUTH_REDIRECT);
  };

  window.addEventListener('storage', event => {
    if (event.key === 'megaSoatToken' && !event.newValue && document.body?.dataset.requiresAuth === 'true') {
      window.location.replace(AUTH_REDIRECT);
    }
  });

  window.addEventListener('pageshow', () => {
    if (document.body?.dataset.requiresAuth === 'true') {
      requireAuth();
    }
  });


});
