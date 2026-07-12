const API = "http://localhost:8000";

let allActivities = [];
let filteredActivities = [];
let currentUserGender = null;
let currentUserId = null;
let myAppliedActivityIds = new Set(); // tracks activity IDs the current user has already applied to
let myRejectedActivityIds = new Set(); // tracks activity IDs where the user's request was rejected
let browsePage = 1;
const BROWSE_PER_PAGE = 6; // after 6 activities we will see pagination


// ── AUTH GUARD ──
function checkAuth() {
  const token = localStorage.getItem("token");
  if (!token) { window.location.href = "./index.html"; return false; }
  return token;
}

// ── LOGOUT ──
function logout() {
  localStorage.removeItem("token");
  window.location.href = "./index.html";
}

// ── LOAD USER INFO + EXISTING APPLICATIONS ──
async function loadUserData() {
  const token = checkAuth();
  if (!token) return;

  try {
    const [userRes, reqRes] = await Promise.all([
      fetch(`${API}/users/me`, { headers: { Authorization: `Bearer ${token}` } }),
      fetch(`${API}/participation/mine`, { headers: { Authorization: `Bearer ${token}` } }),
    ]);

    if (userRes.ok) {
      const user = await userRes.json();
      currentUserGender = user.gender;
      currentUserId = user.id;
    }

    if (reqRes.ok) {
      const myRequests = await reqRes.json();
      // Track every activity the user has requested (regardless of status)
      myAppliedActivityIds = new Set(myRequests.map(r => r.activity_id));
      // Track activities where the user's request was specifically rejected
      myRejectedActivityIds = new Set(
        myRequests.filter(r => r.status === "rejected").map(r => r.activity_id)
      );
    }
  } catch (e) {
    console.error("Failed to load user data:", e);
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
    document.getElementById("loading").innerHTML = "Failed to load activities. Try again.";
  }
}

// ── APPLY FILTERS ──
function applyFilters() {
  const category = document.getElementById("filter-category").value;
  const location = document.getElementById("filter-location").value;
  const dateFrom = document.getElementById("filter-date").value;
  const sortBy = document.getElementById("sort-by").value;
  const girlsOnly = document.getElementById("filter-girls-only").value;
  const searchEl = document.getElementById("filter-search");
  const search = searchEl ? searchEl.value.trim().toLowerCase() : "";

  let filtered = allActivities;

  // Keyword search across title and description
  if (search) {
    filtered = filtered.filter(a =>
      a.title.toLowerCase().includes(search) ||
      (a.description || "").toLowerCase().includes(search)
    );
  }

  // Keyword search across title and description
  if (search) {
    filtered = filtered.filter(a =>
      a.title.toLowerCase().includes(search) ||
      (a.description || "").toLowerCase().includes(search)
    );
  }

  if (category) {
    filtered = filtered.filter(a => a.category.toLowerCase() === category.toLowerCase());
  }

  if (location) {
    filtered = filtered.filter(a => a.location.toLowerCase().includes(location.toLowerCase()));
  }

  if (dateFrom) {
    const parts = dateFrom.split('-');
    const fromDate = new Date(parts[0], parts[1] - 1, parts[2]); // local midnight
    filtered = filtered.filter(a => {
      const actDate = new Date(a.activity_date);
      return actDate >= fromDate;
    });
  }

  if (girlsOnly === "female_only") {
    filtered = filtered.filter(a => a.gender_filter === "female_only");
  } else if (girlsOnly === "all") {
    filtered = filtered.filter(a => a.gender_filter === "all");
  }

  if (sortBy === "popular") {
    filtered.sort((a, b) =>
      (b.max_participants - (b.participants_count || 0)) -
      (a.max_participants - (a.participants_count || 0))
    );
  } else {
    filtered.sort((a, b) => new Date(a.activity_date) - new Date(b.activity_date));
  }

  filteredActivities = filtered;
  browsePage = 1;
  renderActivities();
}

// ── RENDER ACTIVITIES ──
function renderActivities() {
  const grid = document.getElementById("activities-grid");
  const loading = document.getElementById("loading");
  const empty = document.getElementById("empty-state");
  const paginationEl = document.getElementById("browse-pagination");

  loading.style.display = "none";

  if (filteredActivities.length === 0) {
    grid.innerHTML = "";
    empty.style.display = "block";
    if (paginationEl) paginationEl.innerHTML = "";
    return;
  }

  empty.style.display = "none";
  const totalPages = Math.ceil(filteredActivities.length / BROWSE_PER_PAGE);
  const start = (browsePage - 1) * BROWSE_PER_PAGE;
  const pageItems = filteredActivities.slice(start, start + BROWSE_PER_PAGE);

  grid.innerHTML = pageItems.map(a => createActivityCard(a)).join("");

  if (paginationEl) {
    if (totalPages > 1) {
      let pageButtons = "";
      for (let i = 1; i <= totalPages; i++) {
        pageButtons += `<button class="page-num-btn ${i === browsePage ? 'active' : ''}" onclick="changeBrowsePage(${i})">${i}</button>`;
      }
      paginationEl.innerHTML = `
        <div class="pagination">
          <button class="page-btn" onclick="changeBrowsePage(${browsePage - 1})" ${browsePage === 1 ? "disabled" : ""}>
            <i class="ti ti-chevron-left"></i>
          </button>
          ${pageButtons}
          <button class="page-btn" onclick="changeBrowsePage(${browsePage + 1})" ${browsePage === totalPages ? "disabled" : ""}>
            <i class="ti ti-chevron-right"></i>
          </button>
        </div>`;
    } else {
      paginationEl.innerHTML = "";
    }
  }
}

window.changeBrowsePage = function(page) {
  const totalPages = Math.ceil(filteredActivities.length / BROWSE_PER_PAGE);
  if (page < 1 || page > totalPages) return;
  browsePage = page;
  renderActivities();
  document.querySelector(".activities-section")?.scrollIntoView({ behavior: "smooth", block: "start" });
};

// ── CREATE CARD ──
function createActivityCard(activity) {
  const date = new Date(activity.activity_date);
  const dateStr = date.toLocaleDateString("en-US", { month: "short", day: "numeric", hour: "2-digit", minute: "2-digit" });

  const badge = activity.gender_filter === "female_only" ? "female-only" : activity.category.toLowerCase();

  const spotsLeft = Math.max(0, activity.max_participants - (activity.participants_count || 0));
  const capacityPercent = ((activity.max_participants - spotsLeft) / activity.max_participants) * 100;

  // Check if user is owner, rejected, or already applied
  const isOwner = currentUserId === activity.creator_id;
  const isRejected = myRejectedActivityIds.has(activity.id);
  const hasApplied = myAppliedActivityIds.has(activity.id);

  let joinBtn = "";
  if (isOwner) {
    joinBtn = `<button class="btn-request btn-already-applied" disabled>
                 <i class="ti ti-crown"></i> Your Activity
               </button>`;
  } else if (isRejected) {
    joinBtn = `<button class="btn-request btn-rejected" disabled>
                 <i class="ti ti-x"></i> Rejected
               </button>`;
  } else if (hasApplied) {
    joinBtn = `<button class="btn-request btn-already-applied" disabled>
                 <i class="ti ti-check"></i> Already Applied
               </button>`;
  } else {
    joinBtn = `<button class="btn-request" onclick="requestToJoin(${activity.id}, this)">
                 <i class="ti ti-heart"></i> Join
               </button>`;
  }

  return `
    <div class="activity-card">
      <span class="activity-badge ${badge}">${activity.gender_filter === "female_only" ? "👧 Girls only" : activity.category}</span>

      <h3 class="activity-title">${activity.title}</h3>
      <p class="activity-desc">${activity.description}</p>

      <div class="capacity-bar">
        <div class="capacity-fill" style="width: ${capacityPercent}%"></div>
      </div>
      <div class="capacity-text">${spotsLeft} spots left</div>

      <div class="activity-meta">
        <div class="activity-meta-item"><i class="ti ti-calendar-event"></i> ${dateStr}</div>
        <div class="activity-meta-item"><i class="ti ti-map-pin"></i> ${activity.location}</div>
      </div>

      <div class="activity-action">
        <button class="btn-view" onclick="viewActivity(${activity.id})">View</button>
        ${joinBtn}
      </div>
    </div>`;
}

// ── VIEW ACTIVITY ──
function viewActivity(activityId) {
  window.location.href = `./activity-detail.html?id=${activityId}`;
}

// ── REQUEST TO JOIN ──
async function requestToJoin(activityId, btn) {
  const token = localStorage.getItem("token");
  if (!btn) return;

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
      btn.innerHTML = `<i class="ti ti-heart"></i> Join`;
      return;
    }

    // Mark as applied
    myAppliedActivityIds.add(activityId);
    btn.classList.add("btn-already-applied");
    btn.innerHTML = `<i class="ti ti-check"></i> Already Applied`;
    showToast("Request sent! Waiting for approval. 🎉", "success");
  } catch (e) {
    showToast("Could not reach the server", "error");
    btn.disabled = false;
    btn.innerHTML = `<i class="ti ti-heart"></i> Join`;
  }
}

// ── RESET FILTERS ──
function resetFilters() {
  document.getElementById("filter-category").value = "";
  document.getElementById("filter-location").value = "";
  document.getElementById("filter-date").value = "";
  document.getElementById("filter-girls-only").value = "";
  document.getElementById("sort-by").value = "date";
  const searchEl = document.getElementById("filter-search");
  if (searchEl) searchEl.value = "";
  applyFilters();
}

// ── TOAST ──
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

// ── INIT ──
document.addEventListener("DOMContentLoaded", async () => {
  if (checkAuth()) {
    await loadUserData();
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
