const API = "http://localhost:8000";

function checkAuth() {
  const token = localStorage.getItem("token");
  if (!token) window.location.href = "./index.html";
  return token;
}

function logout() {
  localStorage.removeItem("token");
  window.location.href = "./index.html";
}

function setFieldError(fieldId, msg) {
  const field = document.getElementById(fieldId);
  const errorEl = document.getElementById(`${fieldId}-error`);
  if (!field || !errorEl) return;
  if (msg) {
    field.closest(".field").classList.add("error");
    errorEl.textContent = msg;
  } else {
    field.closest(".field").classList.remove("error");
    errorEl.textContent = "";
  }
}

function clearAll() {
  [
    "title",
    "description",
    "category",
    "location",
    "activity-date",
    "max-participants",
    "gender-filter",
  ].forEach((id) => setFieldError(id, ""));
}

function showToast(msg, type = "success") {
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
  toast.innerHTML = `<div class="toast-icon">${icons[type]}</div><span>${msg}</span>`;
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = "0";
    toast.style.transition = "opacity 0.3s";
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

async function createActivity() {
  const token = checkAuth();

  const title = document.getElementById("title").value.trim();
  const description = document.getElementById("description").value.trim();
  const category = document.getElementById("category").value;
  const location = document.getElementById("location").value.trim();
  const activityDate = document.getElementById("activity-date").value;
  const maxParticipants = document.getElementById("max-participants").value;
  const genderFilter = document.getElementById("gender-filter").value;

  clearAll();
  let hasError = false;

  if (!title || title.length < 3) {
    setFieldError("title", "Title must be at least 3 characters");
    hasError = true;
  }
  if (!description || description.length < 10) {
    setFieldError("description", "Description must be at least 10 characters");
    hasError = true;
  }
  if (!category) {
    setFieldError("category", "Please select a category");
    hasError = true;
  }
  if (!location || location.length < 2) {
    setFieldError("location", "Location must be at least 2 characters");
    hasError = true;
  }
  if (!activityDate) {
    setFieldError("activity-date", "Please select a date and time");
    hasError = true;
  } else if (new Date(activityDate) <= new Date()) {
    setFieldError("activity-date", "Activity must be in the future");
    hasError = true;
  }
  if (!maxParticipants || parseInt(maxParticipants) <= 0) {
    setFieldError("max-participants", "Must be at least 1");
    hasError = true;
  }

  if (hasError) return;

  const btn = document.getElementById("create-btn");
  btn.disabled = true;
  btn.innerHTML = `<i class="ti ti-loader"></i> Creating...`;

  try {
    const res = await fetch(`${API}/activities`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        title,
        description,
        category,
        location,
        activity_date: new Date(activityDate).toISOString(),
        max_participants: parseInt(maxParticipants),
        gender_filter: genderFilter,
      }),
    });

    const data = await res.json();

    if (!res.ok) {
      showToast(data.detail || "Could not create activity", "error");
      return;
    }

    showToast("Activity created! 🎉", "success");
    setTimeout(() => {
      window.location.href = `./activity-detail.html?id=${data.id}`;
    }, 1200);
  } catch (e) {
    showToast("Could not reach the server", "error");
  } finally {
    btn.disabled = false;
    btn.innerHTML = `<i class="ti ti-sparkles"></i> Create activity`;
  }
}

document.addEventListener("DOMContentLoaded", () => {
  checkAuth();

  [
    "title",
    "description",
    "category",
    "location",
    "activity-date",
    "max-participants",
  ].forEach((id) => {
    const el = document.getElementById(id);
    if (el) el.addEventListener("input", () => setFieldError(id, ""));
  });
});

window.createActivity = createActivity;
window.logout = logout;
