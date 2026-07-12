const API = "http://localhost:8000";

let currentUser = null;
let activity = null;

function checkAuth() {
  const token = localStorage.getItem("token");
  if (!token) window.location.href = "./index.html";
  return token;
}

function logout() {
  localStorage.removeItem("token");
  window.location.href = "./index.html";
}

function getActivityId() {
  const params = new URLSearchParams(window.location.search);
  return params.get("id");
}

async function loadCurrentUser() {
  const token = checkAuth();
  if (!token) return null;
  try {
    const res = await fetch(`${API}/users/me`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (res.ok) return await res.json();
  } catch (e) {
    console.error("Failed to load user:", e);
  }
  return null;
}

async function loadActivity(activityId) {
  const token = checkAuth();
  if (!token) return null;
  try {
    const res = await fetch(`${API}/activities/${activityId}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (res.ok) return await res.json();
  } catch (e) {
    console.error("Failed to load activity:", e);
  }
  return null;
}

function renderActivity(activity, user) {
  const isOwner = user && user.id === activity.creator_id;
  const isFemaleOnly = activity.gender_filter === "female_only";
  const isFemale = user && user.gender === "female";

  // Badges
  document.getElementById("detail-badges").innerHTML = `
    <span class="detail-badge category">${activity.category}</span>
    ${isFemaleOnly ? '<span class="detail-badge girls-only">👧 Girls only</span>' : ""}
    <span class="detail-badge status-${activity.status}">${activity.status}</span>
  `;

  // Title
  document.getElementById("detail-title").textContent = activity.title;

  // Meta row
  const date = new Date(activity.activity_date);
  const dateStr = date.toLocaleDateString("en-US", {
    weekday: "long",
    month: "long",
    day: "numeric",
    year: "numeric",
  });
  const timeStr = date.toLocaleTimeString("en-US", {
    hour: "2-digit",
    minute: "2-digit",
  });

  document.getElementById("detail-meta-row").innerHTML = `
    <div class="detail-meta-item">
      <i class="ti ti-calendar-event"></i> ${dateStr}
    </div>
    <div class="detail-meta-item">
      <i class="ti ti-clock"></i> ${timeStr}
    </div>
    <div class="detail-meta-item">
      <i class="ti ti-map-pin"></i> ${activity.location}
    </div>
  `;

  // Description
  document.getElementById("detail-desc").textContent = activity.description;

  // Info list
  const spotsLeft = Math.max(
    0,
    activity.max_participants - (activity.participants_count || 0),
  );
  document.getElementById("detail-info-list").innerHTML = `
    <div class="detail-info-item">
      <i class="ti ti-users"></i>
      <span>${activity.max_participants} max participants</span>
    </div>
    <div class="detail-info-item">
      <i class="ti ti-map-pin"></i>
      <span>${activity.location}</span>
    </div>
    <div class="detail-info-item">
      <i class="ti ti-calendar"></i>
      <span>${dateStr} at ${timeStr}</span>
    </div>
    <div class="detail-info-item">
      <i class="ti ti-tag"></i>
      <span>${activity.category}</span>
    </div>
    ${
      isFemaleOnly
        ? `
    <div class="detail-info-item">
      <i class="ti ti-gender-female"></i>
      <span>Female participants only</span>
    </div>`
        : ""
    }
  `;

  // Capacity bar
  const filled = activity.max_participants - spotsLeft;
  const percent = (filled / activity.max_participants) * 100;
  document.getElementById("capacity-count").textContent =
    `${spotsLeft} of ${activity.max_participants} left`;
  setTimeout(() => {
    document.getElementById("capacity-fill").style.width = `${percent}%`;
  }, 300);

  // Action area
  const actionArea = document.getElementById("action-area");
  if (activity.status === "cancelled") {
    actionArea.innerHTML = `<div class="cancelled-notice">❌ This activity has been cancelled</div>`;
  } else if (activity.status === "completed") {
    actionArea.innerHTML = `<div class="completed-notice">✅ This activity has already taken place</div>`;
  } else if (isOwner) {
    actionArea.innerHTML = `
      <div class="own-activity-notice">✅ You are hosting this activity</div>
      <a href="./approve-reject.html" class="join-btn" style="margin-top:10px;text-decoration:none;display:flex;align-items:center;justify-content:center;gap:8px;">
        <i class="ti ti-users"></i> Manage requests
      </a>
    `;
  } else if (activity.status === "full") {
    actionArea.innerHTML = `<div class="full-notice">😔 This activity is full</div>`;
  } else if (isFemaleOnly && !isFemale) {
    actionArea.innerHTML = `<div class="girls-only-notice">👧 This activity is for female participants only</div>`;
  } else {
    actionArea.innerHTML = `
      ${isFemaleOnly ? '<div class="girls-only-notice" style="margin-bottom:12px">👧 Girls only hangout</div>' : ""}
      <button class="join-btn" id="join-btn" onclick="requestToJoin(${activity.id})">
        <i class="ti ti-heart"></i> Request to join
      </button>
    `;
  }

  // Host
  document.getElementById("host-avatar").textContent = "👤";
  document.getElementById("host-name").textContent =
    `Host #${activity.creator_id}`;

  // Show content
  document.getElementById("loading").style.display = "none";
  document.getElementById("detail-content").style.display = "block";
}

async function requestToJoin(activityId) {
  const token = localStorage.getItem("token");
  const btn = document.getElementById("join-btn");

  btn.disabled = true;
  btn.innerHTML = `<i class="ti ti-loader"></i> Sending...`;

  try {
    const res = await fetch(`${API}/participation/${activityId}/request`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
    });

    const data = await res.json();

    if (!res.ok) {
      showToast(data.detail || "Could not send request", "error");
      btn.disabled = false;
      btn.innerHTML = `<i class="ti ti-heart"></i> Request to join`;
      return;
    }

    burstConfetti();
    btn.classList.add("requested");
    btn.innerHTML = `<i class="ti ti-check"></i> Request sent!`;
    showToast("Request sent! Waiting for approval.", "success");
  } catch (e) {
    showToast("Could not reach the server", "error");
    btn.disabled = false;
    btn.innerHTML = `<i class="ti ti-heart"></i> Request to join`;
  }
}

function burstConfetti() {
  const colors = [
    "var(--green)",
    "var(--green-dark)",
    "var(--peach)",
    "var(--lavender)",
    "var(--sky)",
  ];
  for (let i = 0; i < 40; i++) {
    const dot = document.createElement("div");
    dot.className = "confetti-dot";
    const size = 6 + Math.random() * 10;
    const tx = (Math.random() - 0.5) * 260;
    const ty = -80 - Math.random() * 140;
    dot.style.cssText = `
      width:${size}px;height:${size}px;
      background:${colors[Math.floor(Math.random() * colors.length)]};
      left:50vw;top:50vh;
      --tx:${tx}px;--ty:${ty}px;
      animation-delay:${Math.random() * 0.15}s;
    `;
    document.body.appendChild(dot);
    setTimeout(() => dot.remove(), 1000);
  }
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

document.addEventListener("DOMContentLoaded", async () => {
  const activityId = getActivityId();
  if (!activityId) {
    document.getElementById("loading").style.display = "none";
    document.getElementById("not-found").style.display = "block";
    return;
  }
  currentUser = await loadCurrentUser();
  activity = await loadActivity(activityId);
  if (!activity) {
    document.getElementById("loading").style.display = "none";
    document.getElementById("not-found").style.display = "block";
    return;
  }
  renderActivity(activity, currentUser);
});

window.logout = logout;
window.requestToJoin = requestToJoin;
