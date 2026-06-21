// Intercepta la navegación y controla el acceso según el rol del usuario (RBAC)
(function() {
    const token = localStorage.getItem('megaSoatToken');
    const path = window.location.pathname;

    // Si la página requiere autenticación y no hay token, redirigir al login
    if (!token) {
        if (document.body && document.body.dataset.requiresAuth === 'true') {
            window.location.replace('/login');
        }
        return;
    }

    // Extraer roles del JWT
    let roles = [];
    try {
        const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
        const rolesVal = payload.roles || '';
        roles = Array.isArray(rolesVal) ? rolesVal : String(rolesVal).split(',').map(r => r.trim()).filter(Boolean);
    } catch(e) {
        console.error("Error al leer el token", e);
    }

    const isSuperadmin = roles.includes('ROLE_SUPERADMIN') || roles.includes('SUPERADMIN');
    const isAdmin = roles.includes('ROLE_ADMIN') || roles.includes('ADMIN');

    // ─── PROTECCIÓN DE RUTAS ───
    if (path.includes('admin.html') && !isSuperadmin) {
        // Solo el SUPERADMIN puede entrar a la administración
        window.location.replace('/ranking.html');
        return;
    }
    
    if (path.includes('observatorio.html') && !isSuperadmin && !isAdmin) {
        // SUPERADMIN y ADMIN pueden entrar. COMERCIAL no puede.
        window.location.replace('/ranking.html');
        return;
    }

    // ─── OCULTAR ENLACES DE NAVEGACIÓN ───
    // Se ejecuta tan pronto el DOM esté listo
    document.addEventListener('DOMContentLoaded', () => {
        const navObservatorio = document.querySelector('.nav-links a[href="/observatorio.html"]');
        const navAdmin = document.querySelector('.nav-links a[href="/admin.html"]');
        
        // Ocultar Observatorio a los Comerciales
        if (navObservatorio && !isSuperadmin && !isAdmin) {
            navObservatorio.style.display = 'none';
        }
        
        // Ocultar Administración a todos menos al Superadmin
        if (navAdmin) {
            navAdmin.style.display = isSuperadmin ? 'inline-block' : 'none';
        }

        // Mostrar elementos de usuario autenticado y ocultar elementos de visitante (invitado)
        document.querySelectorAll('.nav-auth').forEach(el => {
            // El botón de administración se maneja de forma especial arriba
            if (el.getAttribute('href') !== '/admin.html') {
                el.style.display = el.tagName === 'DIV' ? 'flex' : 'inline-block';
            }
        });
        document.querySelectorAll('.nav-guest').forEach(el => el.style.display = 'none');
        
        // Configurar el nombre de usuario si existe el contenedor
        const userLabel = document.getElementById('userLabel');
        const megaUser = localStorage.getItem('megaSoatUser');
        if (userLabel && megaUser) {
            try {
                const u = JSON.parse(megaUser);
                userLabel.textContent = u.nombre || u.email || '—';
            } catch(e) {
                // Si guardaron un string (ej. email) en vez de un JSON
                userLabel.textContent = megaUser;
            }
        }
    });

})();
