const API = "http://localhost:8000";

let allActivities = [];
let currentUserGender = null;

// ── AUTH GUARD ──
function checkAuth() {
  const token = localStorage.getItem("token");
  if (!token) {
    window.location.href = "./index.html";
    return false;
  }
  return token;
}

// ── LOGOUT ──
function logout() {
  localStorage.removeItem("token");
  window.location.href = "./index.html";
}

// ── LOAD USER GENDER ──
async function loadUserGender() {
  const token = checkAuth();
  if (!token) return;

  try {
    const res = await fetch(`${API}/users/me`, {
      headers: { Authorization: `Bearer ${token}` },
    });

    if (res.ok) {
      const user = await res.json();
      currentUserGender = user.gender;
    }
  } catch (e) {
    console.error("Failed to load user:", e);
  }
}

// ── FETCH ACTIVITIES ──
async function fetchActivities() {
  const token = checkAuth();
  if (!token) return;

  document.getElementById("loading").style.display = "block";
  document.getElementById("activities-grid").innerHTML = "";

  try {
    const res = await fetch(`${API}/activities/browse`, {
      headers: { Authorization: `Bearer ${token}` },
    });

    if (!res.ok) throw new Error("Failed to fetch");

    allActivities = await res.json();
    applyFilters();
  } catch (e) {
    console.error("Error fetching activities:", e);
    document.getElementById("loading").innerHTML =
      "Failed to load activities. Try again.";
  }
}

// ── APPLY FILTERS ──
function applyFilters() {
  const category = document.getElementById("filter-category").value;
  const location = document.getElementById("filter-location").value;
  const dateFrom = document.getElementById("filter-date").value;
  const sortBy = document.getElementById("sort-by").value;

  let filtered = allActivities;

  if (category) {
    filtered = filtered.filter(
      (a) => a.category.toLowerCase() === category.toLowerCase(),
    );
  }

  if (location) {
    filtered = filtered.filter((a) =>
      a.location.toLowerCase().includes(location.toLowerCase()),
    );
  }

  if (dateFrom) {
    const fromDate = new Date(dateFrom);
    filtered = filtered.filter((a) => new Date(a.activity_date) >= fromDate);
  }

  if (sortBy === "popular") {
    filtered.sort(
      (a, b) =>
        b.max_participants -
        (b.participants_count || 0) -
        (a.max_participants - (a.participants_count || 0)),
    );
  } else {
    filtered.sort(
      (a, b) => new Date(a.activity_date) - new Date(b.activity_date),
    );
  }
  const girlsOnly = document.getElementById("filter-girls-only").value;

  if (girlsOnly === "female_only") {
    filtered = filtered.filter((a) => a.gender_filter === "female_only");
  } else if (girlsOnly === "all") {
    filtered = filtered.filter((a) => a.gender_filter === "all");
  }
  renderActivities(filtered);
}

// ── RENDER ACTIVITIES ──
function renderActivities(activities) {
  const grid = document.getElementById("activities-grid");
  const loading = document.getElementById("loading");
  const empty = document.getElementById("empty-state");

  loading.style.display = "none";

  if (activities.length === 0) {
    grid.innerHTML = "";
    empty.style.display = "block";
    return;
  }

  empty.style.display = "none";
  grid.innerHTML = activities
    .map((activity) => createActivityCard(activity))
    .join("");
}

// ── CREATE CARD ──
function createActivityCard(activity) {
  const date = new Date(activity.activity_date);
  const dateStr = date.toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });

  const badge =
    activity.gender_filter === "female_only"
      ? "female-only"
      : activity.category.toLowerCase();

  const spotsLeft = Math.max(
    0,
    activity.max_participants - (activity.participants_count || 0),
  );
  const capacityPercent =
    ((activity.max_participants - spotsLeft) / activity.max_participants) * 100;

  return `
    <div class="activity-card">
      <span class="activity-badge ${badge}">${
        activity.gender_filter === "female_only"
          ? "👧 Girls only"
          : activity.category
      }</span>
      
      <h3 class="activity-title">${activity.title}</h3>
      <p class="activity-desc">${activity.description}</p>

      <div class="capacity-bar">
        <div class="capacity-fill" style="width: ${capacityPercent}%"></div>
      </div>
      <div class="capacity-text">${spotsLeft} spots left</div>

      <div class="activity-meta">
        <div class="activity-meta-item">
          <i class="ti ti-calendar-event"></i>
          ${dateStr}
        </div>
        <div class="activity-meta-item">
          <i class="ti ti-map-pin"></i>
          ${activity.location}
        </div>
      </div>

      <div class="activity-action">
        <button class="btn-view" onclick="viewActivity(${activity.id})">
          View
        </button>
        <button class="btn-request" onclick="requestToJoin(${activity.id})">
          Join
        </button>
      </div>
    </div>
  `;
}

// ── VIEW ACTIVITY ──
function viewActivity(activityId) {
  window.location.href = `./activity-detail.html?id=${activityId}`;
}

// ── REQUEST TO JOIN ──
function requestToJoin(activityId) {
  const token = localStorage.getItem("token");
  // TODO: implement participation request (Week 3)
  console.log("Request to join activity:", activityId);
  showToast("Feature coming soon!", "info");
}

// ── RESET FILTERS ──
function resetFilters() {
  document.getElementById("filter-category").value = "";
  document.getElementById("filter-location").value = "";
  document.getElementById("filter-date").value = "";
  document.getElementById("filter-girls-only").value = "";
  document.getElementById("sort-by").value = "date";
  applyFilters();
}

// ── TOAST (same as auth.js) ──
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

// ── INIT ──
document.addEventListener("DOMContentLoaded", async () => {
  if (checkAuth()) {
    await loadUserGender();
    fetchActivities();
  }
});
// ── EXPOSE TO GLOBAL SCOPE ──
window.fetchActivities = fetchActivities;
window.applyFilters = applyFilters;
window.resetFilters = resetFilters;
window.viewActivity = viewActivity;
window.requestToJoin = requestToJoin;
window.logout = logout;
window.showToast = showToast;
