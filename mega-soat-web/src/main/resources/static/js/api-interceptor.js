// Intercepta todas las respuestas de fetch para manejar sesión desplazada
(function () {
    const _fetch = window.fetch;

    window.fetch = async function (...args) {
        const response = await _fetch(...args);

        if (response.status === 401) {
            const clone = response.clone();
            try {
                const body = await clone.json();
                if (body.sessionExpired) {
                    showSessionExpiredBanner();
                    return response;
                }
            } catch (_) {}
        }

        return response;
    };

    function showSessionExpiredBanner() {
        localStorage.removeItem('megaSoatToken');
        localStorage.removeItem('megaSoatUser');

        // Evitar mostrar múltiples banners
        if (document.getElementById('sessionExpiredBanner')) return;

        const banner = document.createElement('div');
        banner.id = 'sessionExpiredBanner';
        banner.style.cssText = `
            position:fixed;top:0;left:0;right:0;z-index:9999;
            background:#ef4444;color:#fff;
            text-align:center;padding:14px 20px;
            font-size:.93rem;font-weight:600;
            box-shadow:0 4px 16px rgba(0,0,0,.3);
        `;
        banner.innerHTML = `
            ⚠ Tu sesión fue cerrada porque ingresaste desde otro dispositivo.
            <a href="/login" style="color:#fff;text-decoration:underline;margin-left:12px">Iniciar sesión</a>
        `;
        document.body.prepend(banner);

        setTimeout(() => { window.location.href = '/login'; }, 4000);
    }
})();
