// fetch-interceptor.js
const originalFetch = window.fetch;

window.fetch = async function () {
    const response = await originalFetch.apply(this, arguments);
    
    // Handle session expiry (401 Unauthorized) globally
    if (response.status === 401) {
        // Clear stored token
        localStorage.removeItem('token');
        
        // Redirect to login page if we are not already on it
        const currentUrl = window.location.href;
        if (!currentUrl.includes('index.html')) {
            if (typeof showToast === 'function') {
                showToast("Session expired. Please log in again.", "error");
                setTimeout(() => {
                    window.location.href = 'index.html';
                }, 1500);
            } else {
                alert("Session expired. Please log in again.");
                window.location.href = 'index.html';
            }
        }
    }
    return response;
};
