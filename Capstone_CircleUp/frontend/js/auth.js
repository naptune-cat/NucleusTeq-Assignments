const API = "http://localhost:8000";

// Validation regex patterns
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const phoneRegex = /^[6-9]\d{9}$/;
const passwordRegex =
  /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&^#()_+\-=[\]{};':"\\|,.<>/?]).{8,}$/;

// ── Helpers ──────────────────────────────────────────────

export function showMsg(id, msg, type) {
  const el = document.getElementById(id);
  if (!el) return;

  el.textContent = msg;
  el.className = "msg " + type;
  el.style.display = "block";

  setTimeout(() => {
    el.style.display = "none";
  }, 5000);
}

export function setLoading(btnId, loading, defaultText) {
  const btn = document.getElementById(btnId);
  if (!btn) return;

  btn.disabled = loading;
  btn.textContent = loading ? defaultText + "..." : defaultText;
}

export function switchTab(tab) {
  ["login", "register"].forEach((t) => {
    const panel = document.getElementById("panel-" + t);
    const tabEl = document.getElementById("tab-" + t);

    if (panel) panel.classList.toggle("active", t === tab);
    if (tabEl) tabEl.classList.toggle("active", t === tab);
  });
}

export function showAuth(tab) {
  switchTab(tab);

  const section = document.getElementById("auth-section");
  if (section) section.scrollIntoView({ behavior: "smooth" });
}

// ── Login ─────────────────────────────────────────────────

export async function handleLogin() {
  const email = document.getElementById("login-email")?.value.trim();
  const pass = document.getElementById("login-pass")?.value;

  if (!email || !pass) {
    showMsg("login-error", "Enter your email and password.", "error");
    return;
  }

  setLoading("login-btn", true, "Log in");

  try {
    const form = new FormData();
    form.append("username", email);
    form.append("password", pass);

    const res = await fetch(`${API}/auth/login`, {
      method: "POST",
      body: form,
    });

    const data = await res.json();

    if (!res.ok) {
      showMsg(
        "login-error",
        data.detail || "Login failed. Check your credentials.",
        "error",
      );
      return;
    }

    localStorage.setItem("token", data.access_token);

    showMsg("login-success", "Logged in! Redirecting...", "success");

    setTimeout(() => {
      window.location.href = "../components/dashboard.html";
    }, 1200);
  } catch (e) {
    showMsg(
      "login-error",
      "Could not reach the server. Is it running?",
      "error",
    );
  } finally {
    setLoading("login-btn", false, "Log in");
  }
}

// ── Register ──────────────────────────────────────────────

export async function handleRegister() {
  const name = document.getElementById("reg-name")?.value.trim();
  const email = document.getElementById("reg-email")?.value.trim();
  const password = document.getElementById("reg-pass")?.value;
  const phone = document.getElementById("reg-phone")?.value.trim();
  const city = document.getElementById("reg-city")?.value.trim();
  const bio = document.getElementById("reg-bio")?.value.trim();
  const gender = document.getElementById("reg-gender")?.value;

  // Validate name
  if (!name) {
    showMsg("reg-error", "Name is required.", "error");
    return;
  }

  // Allow only letters and spaces in the name
  if (!/^[A-Za-z ]+$/.test(name)) {
    showMsg("reg-error", "Name can only contain letters and spaces.", "error");
    return;
  }

  // Validate email
  if (!email) {
    showMsg("reg-error", "Email is required.", "error");
    return;
  }

  // Validate email format
  if (!emailRegex.test(email)) {
    showMsg("reg-error", "Enter a valid email address.", "error");
    return;
  }

  // Validate password
  if (!password) {
    showMsg("reg-error", "Password is required.", "error");
    return;
  }

  // Password must contain:
  // - Minimum 8 characters
  // - At least one uppercase letter
  // - At least one number
  // - At least one special character
  if (!passwordRegex.test(password)) {
    showMsg(
      "reg-error",
      "Password must be at least 8 characters long and include one uppercase letter, one number, and one special character.",
      "error",
    );
    return;
  }

  // Validate phone number
  if (!phone) {
    showMsg("reg-error", "Phone number is required.", "error");
    return;
  }

  // Validate Indian phone number format
  if (!phoneRegex.test(phone)) {
    showMsg(
      "reg-error",
      "Enter a valid 10-digit Indian mobile number.",
      "error",
    );
    return;
  }

  // Ensure gender is selected
  if (!gender) {
    showMsg("reg-error", "Please select your gender.", "error");
    return;
  }

  setLoading("register-btn", true, "Create account");

  const body = {
    name,
    email,
    password,
    phone_number: phone,
    city: city || null,
    bio: bio || null,
    gender,
  };

  try {
    const res = await fetch(`${API}/auth/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    });

    const data = await res.json();

    if (!res.ok) {
      showMsg(
        "reg-error",
        data.detail || "Registration failed. Try again.",
        "error",
      );
      return;
    }

    showMsg("reg-success", "Account created! Log in now.", "success");

    setTimeout(() => {
      switchTab("login");
    }, 1500);
  } catch (e) {
    showMsg("reg-error", "Could not reach the server. Try after sometime ", "error");
  } finally {
    setLoading("register-btn", false, "Create account");
  }
}

// ── Enter key support ─────────────────────────────────────

document.addEventListener("keydown", (e) => {
  if (e.key !== "Enter") return;

  const loginActive = document
    .getElementById("panel-login")
    ?.classList.contains("active");

  if (loginActive) {
    handleLogin();
  } else {
    handleRegister();
  }
});
