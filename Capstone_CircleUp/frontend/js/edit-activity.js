const API = "";

let activityId = null;
let approvedParticipantsCount = 0;

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

function getActivityId() {
  const params = new URLSearchParams(window.location.search);
  return params.get("id");
}

async function loadActivityData() {
  const token = checkAuth();
  activityId = getActivityId();
  if (!activityId) {
    showToast("Invalid activity ID", "error");
    setTimeout(() => { window.location.href = "./browse.html"; }, 1500);
    return;
  }

  try {
    const res = await fetch(`${API}/activities/${activityId}`, {
      headers: { Authorization: `Bearer ${token}` },
    });

    if (!res.ok) {
      showToast("Failed to fetch activity details", "error");
      setTimeout(() => { window.location.href = "./browse.html"; }, 1500);
      return;
    }

    const activity = await res.json();
    approvedParticipantsCount = activity.participants_count || 0;

    // Prepopulate fields
    document.getElementById("title").value = activity.title;
    document.getElementById("description").value = activity.description;
    document.getElementById("category").value = activity.category;
    document.getElementById("location").value = activity.location;
    document.getElementById("max-participants").value = activity.max_participants;
    document.getElementById("gender-filter").value = activity.gender_filter;

    // Convert date string to local YYYY-MM-DDTHH:MM for datetime-local
    const d = new Date(activity.activity_date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    document.getElementById("activity-date").value = `${year}-${month}-${day}T${hours}:${minutes}`;

  } catch (e) {
    showToast("Failed to load activity details", "error");
  }
}

async function saveActivity() {
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
  } else if (title.length > 200) {
    setFieldError("title", "Title must be at most 200 characters");
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
  } else if (location.length > 200) {
    setFieldError("location", "Location must be at most 200 characters");
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
  } else if (parseInt(maxParticipants) > 1000) {
    setFieldError("max-participants", "Cannot exceed 1000");
    hasError = true;
  } else if (parseInt(maxParticipants) < approvedParticipantsCount) {
    setFieldError("max-participants", `Capacity cannot be lower than currently approved participant count (${approvedParticipantsCount})`);
    hasError = true;
  }

  if (hasError) return;

  const btn = document.getElementById("save-btn");
  btn.disabled = true;
  btn.innerHTML = `<i class="ti ti-loader"></i> Saving...`;

  try {
    const res = await fetch(`${API}/activities/${activityId}`, {
      method: "PUT",
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
      showToast(data.detail || "Could not save activity updates", "error");
      return;
    }

    showToast("Changes saved! 🎉", "success");
    setTimeout(() => {
      window.location.href = `./activity-detail.html?id=${activityId}`;
    }, 1200);
  } catch (e) {
    showToast("Could not reach the server", "error");
  } finally {
    btn.disabled = false;
    btn.innerHTML = `Save changes`;
  }
}

document.addEventListener("DOMContentLoaded", () => {
  checkAuth();
  loadActivityData();

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

window.saveActivity = saveActivity;
window.logout = logout;
