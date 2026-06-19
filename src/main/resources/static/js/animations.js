

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

    fetch('/api/admin/monitoring/ui-event', {
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

    // Mostrar/ocultar items según auth
    document.querySelectorAll('.nav-auth').forEach(el => {
      el.style.display = token ? (el.tagName === 'A' ? 'inline-flex' : 'flex') : 'none';
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
      try {
        const tok = localStorage.getItem('megaSoatToken');
        if (tok) {
          const payload = JSON.parse(atob(tok.split('.')[1]));
          const roles = (payload.roles || '').replace('ROLE_', '').split(',');
          rolLabel = roles[0] || '';
        }
      } catch (e) { }

      chip.innerHTML = `
        <a class="user-chip-avatar" id="chipAvatar" href="/perfil.html" title="Mi perfil">${initials}</a>
        <div class="user-chip-info">
          <span class="user-chip-name">${username}</span>
          ${rolLabel ? `<span class="user-chip-role">${rolLabel}</span>` : ''}
        </div>
        <a href="/perfil.html" class="btn-profile" title="Mi perfil">⚙</a>
        <button class="btn-logout" onclick="logout()">Salir</button>`;

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



    // ── BÚSQUEDA GLOBAL (DESACTIVADA) ──
    // Desactivada porque la funcionalidad de búsqueda fue retirada del sistema.
    const navRight = document.querySelector('.nav-right');
    if (navRight && false) {
      const searchBtn = document.createElement('button');
      searchBtn.className = 'global-search-btn';
      searchBtn.setAttribute('title', 'Buscar (Ctrl+K)');
      searchBtn.innerHTML = '<svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg><span class="global-search-label">Buscar…</span><kbd>Ctrl K</kbd>';
      navRight.insertBefore(searchBtn, navRight.firstChild);

      // Overlay
      const searchOverlay = document.createElement('div');
      searchOverlay.className = 'search-overlay';
      searchOverlay.innerHTML = `
        <div class="search-modal">
          <div class="search-input-row">
            <svg class="search-icon-modal" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
            <input class="search-input-main" id="globalSearchInput" placeholder="Buscar pólizas, usuarios, puntos de venta…" autocomplete="off" spellcheck="false">
            <button class="search-close-btn" id="searchCloseBtn">Esc</button>
          </div>
          <div class="search-results" id="searchResults">
            <p class="search-hint">Escribe al menos 2 caracteres para buscar</p>
          </div>
        </div>`;
      document.body.appendChild(searchOverlay);

      let searchTimer = null;

      function openSearch() {
        searchOverlay.classList.add('open');
        document.getElementById('globalSearchInput').focus();
        document.body.style.overflow = 'hidden';
      }
      function closeSearch() {
        searchOverlay.classList.remove('open');
        document.body.style.overflow = '';
        document.getElementById('searchResults').innerHTML = '<p class="search-hint">Escribe al menos 2 caracteres para buscar</p>';
        document.getElementById('globalSearchInput').value = '';
      }

      searchBtn.addEventListener('click', openSearch);
      document.getElementById('searchCloseBtn').addEventListener('click', closeSearch);
      searchOverlay.addEventListener('click', e => { if (e.target === searchOverlay) closeSearch(); });
      document.addEventListener('keydown', e => {
        if ((e.ctrlKey || e.metaKey) && e.key === 'k') { e.preventDefault(); openSearch(); }
        if (e.key === 'Escape') closeSearch();
      });

      document.getElementById('globalSearchInput').addEventListener('input', function () {
        clearTimeout(searchTimer);
        const q = this.value.trim();
        const resultsEl = document.getElementById('searchResults');
        if (q.length < 2) {
          resultsEl.innerHTML = '<p class="search-hint">Escribe al menos 2 caracteres para buscar</p>';
          return;
        }
        resultsEl.innerHTML = '<p class="search-hint">Buscando…</p>';
        searchTimer = setTimeout(async () => {
          try {
            const token = localStorage.getItem('megaSoatToken');
            const headers = token ? { 'Authorization': 'Bearer ' + token } : {};
            const res = await fetch(`/api/search?q=${encodeURIComponent(q)}`, { headers });
            if (!res.ok) { resultsEl.innerHTML = '<p class="search-hint">Error al buscar</p>'; return; }
            const data = await res.json();
            renderResults(resultsEl, data, q);
          } catch (e) {
            resultsEl.innerHTML = '<p class="search-hint">Error de conexión</p>';
          }
        }, 320);
      });

      function hl(text, q) {
        if (!text) return '';
        const re = new RegExp(`(${q.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi');
        return String(text).replace(re, '<mark>$1</mark>');
      }

      function renderResults(el, data, q) {
        const total = data.policies.length + data.users.length + data.pos.length;
        if (total === 0) { el.innerHTML = '<p class="search-hint">Sin resultados para «' + q + '»</p>'; return; }
        let html = '';

        if (data.policies.length) {
          html += `<div class="search-group"><div class="search-group-label">Pólizas</div>`;
          data.policies.forEach(p => {
            html += `<a class="search-item" href="/portal.html?poliza=${encodeURIComponent(p.policyNumber)}">
              <span class="search-item-icon">📄</span>
              <span class="search-item-body">
                <span class="search-item-title">${hl(p.policyNumber, q)}</span>
                <span class="search-item-sub">${hl(p.plate, q)} · ${p.insurer} · <span class="status-${(p.status || '').toLowerCase()}">${p.status}</span></span>
              </span>
            </a>`;
          });
          html += '</div>';
        }

        if (data.pos.length) {
          html += `<div class="search-group"><div class="search-group-label">Puntos de venta</div>`;
          data.pos.forEach(p => {
            html += `<a class="search-item" href="/admin.html">
              <span class="search-item-icon">🏪</span>
              <span class="search-item-body">
                <span class="search-item-title">${hl(p.name, q)}</span>
                <span class="search-item-sub">${hl(p.city, q)}</span>
              </span>
            </a>`;
          });
          html += '</div>';
        }

        if (data.users.length) {
          html += `<div class="search-group"><div class="search-group-label">Usuarios</div>`;
          data.users.forEach(u => {
            html += `<a class="search-item" href="/admin.html">
              <span class="search-item-icon">👤</span>
              <span class="search-item-body">
                <span class="search-item-title">${hl(u.fullName, q)}</span>
                <span class="search-item-sub">${hl(u.email, q)} · ${(u.rol || '').replace('ROLE_', '')}</span>
              </span>
            </a>`;
          });
          html += '</div>';
        }

        el.innerHTML = html;
      }
    }

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
      { href: '/', label: '🏠 Inicio' },
      { href: '/ranking.html', label: '🏆 Ranking' },
      { href: '/observatorio.html', label: '🗺️ Observatorio' },

      { href: '/admin.html', label: '⚙️ Administración', auth: true },
      { href: '/perfil.html', label: '👤 Mi Perfil', auth: true },
    ];

    allLinks.forEach(item => {
      if (item.auth && !token) return;
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
      fetch('/api/auth/logout', {
        method: 'POST',
        headers: { 'Authorization': 'Bearer ' + token }
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
