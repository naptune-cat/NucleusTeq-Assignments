const API = "http://localhost:8000";

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const phoneRegex = /^[6-9]\d{9}$/;
const passwordRegex =
  /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&^#()_+\-=[\]{};':"\\|,.<>/?]).{8,}$/;

// ── VALIDATION HELPERS ──────────────────────────────────

function setFieldError(fieldId, errorMsg) {
  const field = document.getElementById(fieldId);
  const errorEl = document.getElementById(`${fieldId}-error`);

  if (!field || !errorEl) return;

  if (errorMsg) {
    field.parentElement.classList.add("error");
    errorEl.textContent = errorMsg;
  } else {
    field.parentElement.classList.remove("error");
    errorEl.textContent = "";
  }
}

function clearFieldError(fieldId) {
  setFieldError(fieldId, "");
}

// ── TOAST NOTIFICATIONS ──────────────────────────────────

export function showToast(msg, type = "success") {
  let container = document.getElementById("toast-container");

  if (!container) {
    container = document.createElement("div");
    container.id = "toast-container";
    container.className = "toast-container";
    document.body.appendChild(container);
  }

  const toast = document.createElement("div");
  const icons = { success: "✓", error: "✕", info: "i" };

  toast.className = `toast ${type}`;
  toast.innerHTML = `
    <div class="toast-icon">${icons[type]}</div>
    <span>${msg}</span>
  `;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = "0";
    toast.style.transition = "opacity 0.3s";
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

// ── CONFETTI ────────────────────────────────────────────

export function burstConfetti() {
  const colors = [
    "var(--green)",
    "var(--green-dark)",
    "var(--peach)",
    "var(--lavender)",
    "var(--sky)",
  ];

  for (let i = 0; i < 32; i++) {
    const dot = document.createElement("div");
    dot.className = "confetti-dot";

    const size = 6 + Math.random() * 10;
    const color = colors[Math.floor(Math.random() * colors.length)];
    const tx = (Math.random() - 0.5) * 200;
    const ty = -100 - Math.random() * 120;

    dot.style.cssText = `
      width: ${size}px;
      height: ${size}px;
      background: ${color};
      left: 50vw;
      top: 50vh;
      --tx: ${tx}px;
      --ty: ${ty}px;
      animation-delay: ${Math.random() * 0.15}s;
    `;

    document.body.appendChild(dot);

    setTimeout(() => dot.remove(), 1000);
  }
}

// ── DOODLES ─────────────────────────────────────────────

export function initDoodles() {
  const hero = document.querySelector(".hero");
  if (!hero) return;

  const doodles = ["🌿", "✨", "🎨", "🌸", "🚴", "🧘", "🎭"];
  const container = document.createElement("div");
  container.className = "doodle-container";

  doodles.forEach((emoji, idx) => {
    const doodle = document.createElement("div");
    doodle.className = "doodle";
    doodle.textContent = emoji;

    const duration = 3.5 + Math.random() * 1.5;
    const delay = Math.random() * 1;
    const left = (idx / doodles.length) * 100;
    const rotation = Math.random() * 10 - 5;

    doodle.style.cssText = `
      left: ${left}%;
      top: ${20 + Math.random() * 20}%;
      --duration: ${duration}s;
      --delay: ${delay}s;
      --rotation: ${rotation}deg;
    `;

    container.appendChild(doodle);
  });

  hero.style.position = "relative";
  hero.appendChild(container);
}

// ── TABS ────────────────────────────────────────────────

export function switchTab(tab) {
  ["login", "register"].forEach((t) => {
    const form = document.getElementById(`form-${t}`);
    const tabEl = document.getElementById(`tab-${t}`);

    if (form) form.classList.toggle("active", t === tab);
    if (tabEl) tabEl.classList.toggle("active", t === tab);
  });

  document.querySelectorAll(".field-error").forEach((el) => {
    el.textContent = "";
  });

  document.querySelectorAll(".field").forEach((el) => {
    el.classList.remove("error");
  });
}

export function showAuth(tab) {
  switchTab(tab);

  const section = document.getElementById("auth-section");
  if (section) section.scrollIntoView({ behavior: "smooth" });
}

// ── LOGIN ───────────────────────────────────────────────

export async function handleLogin(event) {
  event.preventDefault();

  const email = document.getElementById("login-email").value.trim();
  const pass = document.getElementById("login-pass").value;

  let hasError = false;

  // Validate email
  if (!email) {
    setFieldError("login-email", "Email is required");
    hasError = true;
  } else if (!emailRegex.test(email)) {
    setFieldError("login-email", "Enter a valid email");
    hasError = true;
  } else {
    clearFieldError("login-email");
  }

  // Validate password
  if (!pass) {
    setFieldError("login-pass", "Password is required");
    hasError = true;
  } else {
    clearFieldError("login-pass");
  }

  if (hasError) return;

  const btn = document.getElementById("login-btn");
  btn.disabled = true;
  btn.textContent = "Logging in...";

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
      showToast(data.detail || "Login failed", "error");
      return;
    }

    localStorage.setItem("token", data.access_token);
    showToast("Logged in! Welcome back ", "success");

    setTimeout(() => {
      window.location.href = "../component/browse.html";
    }, 1200);
  } catch (e) {
    showToast("Could not reach the server.", "error");
  } finally {
    btn.disabled = false;
    btn.textContent = "Log in";
  }
}

// ── REGISTER ────────────────────────────────────────────

export async function handleRegister(event) {
  event.preventDefault();

  const name = document.getElementById("reg-name").value.trim();
  const email = document.getElementById("reg-email").value.trim();
  const password = document.getElementById("reg-pass").value;
  const phone = document.getElementById("reg-phone").value.trim();
  const city = document.getElementById("reg-city").value.trim();
  const bio = document.getElementById("reg-bio").value.trim();
  const gender = document.getElementById("reg-gender").value;

  let hasError = false;

  // Validate name
  if (!name) {
    setFieldError("reg-name", "Name is required");
    hasError = true;
  } else if (name.length < 2) {
    setFieldError("reg-name", "Name must be at least 2 characters long");
    hasError = true;
  } else if (!/^[A-Za-z ]+$/.test(name)) {
    setFieldError("reg-name", "Name can only contain letters and spaces");
    hasError = true;
  } else {
    clearFieldError("reg-name");
  }

  // Validate email
  if (!email) {
    setFieldError("reg-email", "Email is required");
    hasError = true;
  } else if (!emailRegex.test(email)) {
    setFieldError("reg-email", "Enter a valid email");
    hasError = true;
  } else {
    clearFieldError("reg-email");
  }

  // Validate password
  if (!password) {
    setFieldError("reg-pass", "Password is required");
    hasError = true;
  } else if (!passwordRegex.test(password)) {
    setFieldError("reg-pass", "Need 8+ chars, uppercase, number, special char");
    hasError = true;
  } else {
    clearFieldError("reg-pass");
  }

  // Validate phone
  if (!phone) {
    setFieldError("reg-phone", "Phone is required");
    hasError = true;
  } else if (!phoneRegex.test(phone)) {
    setFieldError("reg-phone", "Enter a valid 10-digit number");
    hasError = true;
  } else {
    clearFieldError("reg-phone");
  }

  // Validate gender
  if (!gender) {
    setFieldError("reg-gender", "Please select your gender");
    hasError = true;
  } else {
    clearFieldError("reg-gender");
  }

  clearFieldError("reg-city");
  clearFieldError("reg-bio");

  if (hasError) return;

  const btn = document.getElementById("register-btn");
  btn.disabled = true;
  btn.textContent = "Creating account...";

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
      showToast(data.detail || "Registration failed", "error");
      return;
    }

    showToast("Account created! Log in now.", "success");

    setTimeout(() => {
      switchTab("login");
      document.getElementById("form-login").reset();
    }, 1500);
  } catch (e) {
    showToast("Could not reach the server", "error");
  } finally {
    btn.disabled = false;
    btn.textContent = "Create account";
  }
}

// ── INIT ────────────────────────────────────────────────

document.addEventListener("DOMContentLoaded", () => {
  initDoodles();

  // Clear errors on input
  document.querySelectorAll("input, select").forEach((field) => {
    field.addEventListener("input", () => {
      const fieldId = field.id;
      clearFieldError(fieldId);
    });
  });

  // Confetti on buttons
  document.querySelectorAll(".btn-big-primary").forEach((btn) => {
    btn.addEventListener("click", () => {
      setTimeout(burstConfetti, 200);
    });
  });
});
