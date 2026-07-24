const API = "http://localhost:8000";

const ITEMS_PER_PAGE = 5;
let activitiesPage = 1;
let activeActivityFilter = "all";
let applicationsPage = 1;
let myActivities = [];
let myRequests = [];
let selectedManageActivityId = null;
let manageActivities = [];
let activeAppFilter = "all";

// ── AUTH ──────────────────────────────────────────────────
function getToken() {
  const token = localStorage.getItem("token");
  if (!token) {
    window.location.href = "../component/index.html";
    return null;
  }
  return token;
}

export function logout() {
  localStorage.removeItem("token");
  window.location.href = "../component/index.html";
}

// ── TOAST ─────────────────────────────────────────────────
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

// ── HELPERS ───────────────────────────────────────────────
function showMsg(id, msg, type) {
  const el = document.getElementById(id);
  if (!el) return;
  el.textContent = msg;
  el.className = "msg " + type;
  el.style.display = "block";
  setTimeout(() => {
    el.style.display = "none";
  }, 5000);
}

function setFieldError(fieldId, errorMsg) {
  const field = document.getElementById(fieldId);
  const errorEl = document.getElementById(`${fieldId}-error`);
  if (!field || !errorEl) return;
  if (errorMsg) {
    field.closest(".field").classList.add("error");
    errorEl.textContent = errorMsg;
  } else {
    field.closest(".field").classList.remove("error");
    errorEl.textContent = "";
  }
}

function clearFieldError(fieldId) {
  setFieldError(fieldId, "");
}

function getInitials(name) {
  return name
    .split(" ")
    .map((w) => w[0])
    .join("")
    .toUpperCase()
    .slice(0, 2);
}

function formatGender(g) {
  return (
    {
      male: "Male",
      female: "Female",
      other: "Other",
      prefer_not_to_say: "Prefer not to say",
    }[g] || g
  );
}

function formatDate(iso) {
  return new Date(iso).toLocaleDateString("en-IN", {
    day: "numeric",
    month: "short",
    year: "numeric",
  });
}

function formatStatus(status) {
  return (
    {
      open: "Open",
      full: "Full",
      cancelled: "Cancelled",
      completed: "Completed",
    }[status] || status
  );
}

function statusClass(status) {
  return (
    {
      open: "status-open",
      full: "status-full",
      cancelled: "status-cancelled",
      completed: "status-completed",
    }[status] || ""
  );
}

function categoryIcon(category) {
  const map = {
    coffee: "ti-coffee",
    food: "ti-tools-kitchen-2",
    hike: "ti-walk",
    sports: "ti-ball-football",
    games: "ti-device-gamepad",
    music: "ti-music",
    art: "ti-palette",
    travel: "ti-plane",
    study: "ti-book",
  };
  const key = (category || "").toLowerCase();
  for (const [k, v] of Object.entries(map)) {
    if (key.includes(k)) return v;
  }
  return "ti-calendar-event";
}

function categoryStyle(category) {
  const styles = [
    { bg: "var(--green-light)", color: "var(--green-dark)" },
    { bg: "var(--peach)", color: "var(--peach-dark)" },
    { bg: "var(--lavender)", color: "var(--lavender-dark)" },
    { bg: "var(--sky)", color: "var(--sky-dark)" },
  ];
  return styles[(category || "").length % styles.length];
}

// Auto-expire: if activity_date is past, treat status as "completed" unless already cancelled
function resolveStatus(activity) {
  if (activity.status === "cancelled") return "cancelled";
  if (new Date(activity.activity_date) < new Date()) return "completed";
  return activity.status;
}

function setText(id, val) {
  const el = document.getElementById(id);
  if (el) el.textContent = val;
}
function setVal(id, val) {
  const el = document.getElementById(id);
  if (el) el.value = val;
}

// ── SIDEBAR SECTION SWITCHING ─────────────────────────────
export function switchSection(name) {
  document
    .querySelectorAll(".profile-section")
    .forEach((s) => s.classList.remove("active"));
  document
    .querySelectorAll(".sidebar-link")
    .forEach((l) => l.classList.remove("active"));

  const section = document.getElementById(`section-${name}`);
  const link = document.getElementById(`nav-${name}`);
  if (section) section.classList.add("active");
  if (link) link.classList.add("active");
}

// ── LOAD PROFILE ──────────────────────────────────────────
export async function loadProfile() {
  const token = getToken();
  if (!token) return;

  try {
    const res = await fetch(`${API}/users/me`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (res.status === 401) {
      logout();
      return;
    }
    const user = await res.json();
    renderProfile(user);
    loadMyActivities(token);
    loadMyRequests(token);
    loadManageActivities(token);
  } catch (e) {
    console.error("Failed to load profile:", e);
  }
}

function renderProfile(user) {
  const initials = getInitials(user.name);
  ["avatar-initials", "sidebar-avatar"].forEach((id) => setText(id, initials));
  setText("sidebar-name", user.name);
  setText("sidebar-email", user.email);
  setText("display-name", user.name);
  setText(
    "display-bio",
    user.bio || "No bio yet — tell people about yourself!",
  );
  setText("display-email", user.email);
  setText("display-city", user.city ? ` ${user.city}` : "—");
  setText("display-city-2", user.city || "—");
  setText("display-phone", user.phone_number || "—");
  setText("display-gender", formatGender(user.gender));
  setText("display-gender-2", formatGender(user.gender));
  setVal("edit-name", user.name);
  setVal("edit-phone", user.phone_number || "");
  setVal("edit-city", user.city || "");
  setVal("edit-bio", user.bio || "");
  setVal("edit-gender", user.gender || "");
  setText("stat-joined", "Jun 2026");
}

// ── MY ACTIVITIES (hosted) ────────────────────────────────
async function loadMyActivities(token) {
  try {
    const actRes = await fetch(`${API}/activities/mine`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    myActivities = await actRes.json();
    setText("stat-hosted", myActivities.length);
    setText(
      "badge-my-activities",
      myActivities.length > 0 ? myActivities.length : "",
    );
    activitiesPage = 1;
    renderActivitiesPage();
  } catch (e) {
    console.error("Failed to load activities:", e);
  }
}

function renderActivitiesPage() {
  const container = document.getElementById("activities-list");
  if (!container) return;

  if (myActivities.length === 0) {
    container.innerHTML = `<div class="empty-state"><i class="ti ti-calendar-off"></i><p>No activities yet — <a href="create-activity.html">create one!</a></p></div>`;
    return;
  }

  // Filter activities based on activeActivityFilter
  const filteredActivities = myActivities.filter((a) => {
    const rs = resolveStatus(a);
    if (activeActivityFilter === "all") return true;
    return rs === activeActivityFilter;
  });

  if (filteredActivities.length === 0) {
    const filterLabel = activeActivityFilter === "all" ? "any" : activeActivityFilter;
    container.innerHTML = `<div class="empty-state"><i class="ti ti-filter-off"></i><p>No ${filterLabel} activities found.</p></div>`;
    return;
  }

  const total = filteredActivities.length;
  const totalPages = Math.ceil(total / ITEMS_PER_PAGE);
  const start = (activitiesPage - 1) * ITEMS_PER_PAGE;
  const pageItems = filteredActivities.slice(start, start + ITEMS_PER_PAGE);

  const cardsHtml = pageItems
    .map((a) => {
      const style = categoryStyle(a.category);
      const icon = categoryIcon(a.category);
      const resolvedStatus = resolveStatus(a);
      const spotsLeft = Math.max(
        0,
        a.max_participants - (a.participants_count || 0),
      );
      const percent =
        ((a.max_participants - spotsLeft) / a.max_participants) * 100;

      const isCancelled = resolvedStatus === "cancelled";
      const isCompleted = resolvedStatus === "completed";

      const cancelBtn = (!isCancelled && !isCompleted) ? `
        <button class="btn-cancel-request" onclick="cancelHostedActivity(${a.id})" style="margin-top:8px;width:100%;justify-content:center;">
          <i class="ti ti-x"></i> Cancel Event
        </button>
      ` : "";

      const editBtn = (!isCancelled && !isCompleted) ? `
        <button class="btn-manage" onclick="window.location.href='./edit-activity.html?id=${a.id}'" style="margin-top:8px;width:100%;justify-content:center;background:var(--sky-light);color:var(--sky-dark);border-color:var(--sky);">
          <i class="ti ti-edit"></i> Edit Event
        </button>
      ` : "";

      const manageBtn = `
        <button class="btn-manage" ${isCancelled ? "disabled style='opacity:0.6;cursor:not-allowed;'" : ""} onclick="window.switchSection('manage-requests');window.selectManageActivity(${a.id})">
          <i class="ti ti-users"></i> Manage
        </button>
      `;

      return `
    <div class="activity-card ${resolvedStatus === 'cancelled' ? 'status-cancelled' : ''}">
      <div class="activity-icon" style="background:${style.bg}">
        <i class="ti ${icon}" style="color:${style.color};font-size:20px"></i>
      </div>
      <div class="activity-info">
        <div class="activity-title">${a.title}</div>
        <div class="activity-meta">
          <i class="ti ti-map-pin"></i> ${a.location}
          &nbsp;·&nbsp;
          <i class="ti ti-calendar"></i> ${formatDate(a.activity_date)}
        </div>
        <div class="capacity-mini">
          <div class="capacity-mini-bar"><div class="capacity-mini-fill" style="width:${percent}%"></div></div>
          <span class="capacity-mini-text">${spotsLeft} spots left</span>
        </div>
      </div>
      <div class="activity-card-actions">
        <span class="status-pill ${statusClass(resolvedStatus)}">${formatStatus(resolvedStatus)}</span>
        ${manageBtn}
        ${editBtn}
        ${cancelBtn}
      </div>
    </div>`;
    })
    .join("");

  const paginationHtml = buildPagination(
    activitiesPage,
    totalPages,
    "changeActivitiesPage",
  );
  container.innerHTML = cardsHtml + paginationHtml;
}

window.changeActivitiesPage = function (page) {
  const totalPages = Math.ceil(myActivities.length / ITEMS_PER_PAGE);
  if (page < 1 || page > totalPages) return;
  activitiesPage = page;
  renderActivitiesPage();
};

window.cancelHostedActivity = async function (activityId) {
  if (!confirm("Are you sure you want to cancel this activity?")) {
    return;
  }

  const token = getToken();
  if (!token) return;

  try {
    const res = await fetch(`${API}/activities/${activityId}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${token}` }
    });

    if (!res.ok) {
      showToast("Could not cancel activity", "error");
      return;
    }

    showToast("Activity cancelled successfully", "success");
    await loadMyActivities(token);
    await loadManageActivities(token);
  } catch (e) {
    showToast("Could not cancel activity", "error");
  }
};

// ── MY APPLICATIONS (requests I sent) ─────────────────────
async function loadMyRequests(token) {
  try {
    const res = await fetch(`${API}/participation/mine`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) return;
    myRequests = await res.json();
    setText("stat-applied", myRequests.length);
    const pendingCount = myRequests.filter(
      (r) => r.status === "pending",
    ).length;
    setText(
      "badge-my-applications",
      myRequests.length > 0 ? myRequests.length : "",
    );
    applicationsPage = 1;
    renderApplicationsPage();
  } catch (e) {
    console.error("Failed to load requests:", e);
  }
}

export function setAppFilter(filter) {
  activeAppFilter = filter;
  applicationsPage = 1;
  document.querySelectorAll(".app-filter-tab").forEach((btn) => btn.classList.toggle("active", btn.dataset.filter === filter));
  renderApplicationsPage();
}

// New activity filter for My Activities
export function setActivityFilter(filter) {
  activeActivityFilter = filter;
  activitiesPage = 1;
  document.querySelectorAll("#my-activities-filter-tabs .app-filter-tab").forEach((btn) => btn.classList.toggle("active", btn.dataset.filter === filter));
  renderActivitiesPage();
}

function renderApplicationsPage() {
  const container = document.getElementById("my-requests-list");
  if (!container) return;

  const filtered =
    activeAppFilter === "all"
      ? myRequests
      : myRequests.filter((r) => r.status === activeAppFilter);

  if (myRequests.length === 0) {
    container.innerHTML = `<div class="empty-state"><i class="ti ti-send"></i><p>No applications yet — <a href="browse.html">browse activities!</a></p></div>`;
    return;
  }

  if (filtered.length === 0) {
    container.innerHTML = `<div class="empty-state"><i class="ti ti-filter-off"></i><p>No ${activeAppFilter} requests found.</p></div>`;
    return;
  }

  const totalPages = Math.ceil(filtered.length / ITEMS_PER_PAGE);
  const start = (applicationsPage - 1) * ITEMS_PER_PAGE;
  const pageItems = filtered.slice(start, start + ITEMS_PER_PAGE);

  const statusColors = {
    pending: {
      bg: "var(--peach)",
      color: "var(--peach-dark)",
      icon: `<i class="ti ti-loader"></i>`,
      pill: "status-full",
    },
    approved: {
      bg: "var(--green-light)",
      color: "var(--green-dark)",
      icon: `<i class="ti ti-check"></i>`,
      pill: "status-open",
    },
    rejected: {
      bg: "#F0EBE1",
      color: "var(--text-muted)",
      icon: `<i class="ti ti-x"></i>`,
      pill: "status-cancelled",
    },
  };

  const cardsHtml = pageItems
    .map((req) => {
      const s = statusColors[req.status] || statusColors.pending;
      const cancelBtn =
        req.status === "pending"
          ? `<button class="btn-cancel-request" onclick="cancelRequest(${req.id})"><i class="ti ti-x"></i> Cancel</button>`
          : "";
      const activityTitle =
        req.activity_title || `Activity #${req.activity_id}`;
      const activityDate = req.activity_date
        ? formatDate(req.activity_date)
        : "";
      const autoStatus =
        req.activity_date &&
        new Date(req.activity_date) < new Date() &&
        req.status === "approved"
          ? "Completed"
          : null;

      const hostContact =
        req.status === "approved" && req.host_name
          ? `<div class="request-host-contact"><i class="ti ti-user"></i> <strong>${req.host_name}</strong>${req.host_phone ? ` &nbsp;·&nbsp; <i class="ti ti-phone"></i> ${req.host_phone}` : ""}</div>`
          : "";

      return `
    <div class="request-row" onclick="window.location.href='activity-detail.html?id=${req.activity_id}'" style="cursor:pointer">
      <div class="request-status-icon" style="background:${s.bg};color:${s.color}">${s.icon}</div>
      <div class="request-info">
        <div class="request-activity-name">${activityTitle}</div>
        <div class="request-date">Applied: ${formatDate(req.requested_at)}${activityDate ? ` &nbsp;·&nbsp; Activity: ${activityDate}` : ""}</div>
        ${hostContact}
      </div>
      <div style="display:flex;flex-direction:column;align-items:flex-end;gap:6px">
        <span class="status-pill ${s.pill}">${autoStatus || req.status}</span>
        ${cancelBtn}
      </div>
    </div>`;
    })
    .join("");

  const paginationHtml = buildPagination(
    applicationsPage,
    totalPages,
    "changeApplicationsPage",
  );
  container.innerHTML = cardsHtml + paginationHtml;
}

window.changeApplicationsPage = function (page) {
  const totalPages = Math.ceil(myRequests.length / ITEMS_PER_PAGE);
  if (page < 1 || page > totalPages) return;
  applicationsPage = page;
  renderApplicationsPage();
};

window.cancelRequest = async function (requestId) {
  if (!confirm("Cancel this join request?")) return;
  const token = getToken();
  if (!token) return;
  try {
    const res = await fetch(`${API}/participation/${requestId}/cancel`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) {
      const d = await res.json();
      showToast(d.detail || "Could not cancel request", "error");
      return;
    }
    showToast("Request cancelled.", "info");
    myRequests = myRequests.filter((r) => r.id !== requestId);
    setText("stat-applied", myRequests.length);
    setText(
      "badge-my-applications",
      myRequests.length > 0 ? myRequests.length : "",
    );
    renderApplicationsPage();
  } catch (e) {
    showToast("Could not reach the server.", "error");
  }
};

// ── MANAGE REQUESTS (Approve/Reject) ─────────────────────
async function loadManageActivities(token) {
  try {
    const [actRes, meRes] = await Promise.all([
      fetch(`${API}/activities`, {
        headers: { Authorization: `Bearer ${token}` },
      }),
      fetch(`${API}/users/me`, {
        headers: { Authorization: `Bearer ${token}` },
      }),
    ]);
    const all = await actRes.json();
    const me = await meRes.json();
    manageActivities = all.filter((a) => a.creator_id === me.id);

    document.getElementById("manage-activities-loading").style.display = "none";

    if (manageActivities.length === 0) {
      document.getElementById("manage-activities-empty").style.display =
        "block";
      return;
    }
    renderManageActivitiesList();

    // auto-select if navigated here with an activity id
    const params = new URLSearchParams(window.location.search);
    const actId = parseInt(params.get("activity"));
    if (actId) {
      selectManageActivity(actId);
    }
  } catch (e) {
    console.error("Failed to load manage activities:", e);
  }
}

function renderManageActivitiesList() {
  const list = document.getElementById("manage-activities-list");
  list.innerHTML = manageActivities
    .map(
      (a) => `
    <div class="activity-item" id="manage-item-${a.id}" onclick="selectManageActivity(${a.id})">
      <div class="activity-item-title">${a.title}</div>
      <div class="activity-item-meta">
        <i class="ti ti-map-pin" style="font-size:12px"></i> ${a.location}
        <span class="pending-count" id="pending-count-${a.id}">…</span>
      </div>
    </div>`,
    )
    .join("");

  manageActivities.forEach((a) => loadPendingCount(a.id));
}

async function loadPendingCount(activityId) {
  const token = getToken();
  if (!token) return;
  try {
    const res = await fetch(`${API}/participation/activity/${activityId}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) return;
    const requests = await res.json();
    const pending = requests.filter((r) => r.status === "pending").length;
    const el = document.getElementById(`pending-count-${activityId}`);
    if (el) el.textContent = pending > 0 ? `${pending} pending` : "0 pending";
    // Update manage badge
    const totalPending = manageActivities.reduce((sum, a) => {
      const countEl = document.getElementById(`pending-count-${a.id}`);
      const txt = countEl?.textContent || "";
      return sum + (parseInt(txt) || 0);
    }, 0);
    setText("badge-manage-requests", totalPending > 0 ? totalPending : "");
  } catch (e) {}
}

window.selectManageActivity = async function (activityId) {
  selectedManageActivityId = activityId;
  document
    .querySelectorAll(".activity-item")
    .forEach((el) => el.classList.remove("selected"));
  document
    .getElementById(`manage-item-${activityId}`)
    ?.classList.add("selected");

  const activity = manageActivities.find((a) => a.id === activityId);
  setText("manage-requests-title", activity ? activity.title : "Requests");

  document.getElementById("manage-requests-placeholder").style.display = "none";
  document.getElementById("manage-requests-loading").style.display = "block";
  document.getElementById("manage-requests-list").innerHTML = "";
  document.getElementById("manage-requests-empty").style.display = "none";

  await loadManageRequests(activityId);
};

async function loadManageRequests(activityId) {
  const token = getToken();
  if (!token) return;
  try {
    const res = await fetch(`${API}/participation/activity/${activityId}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    document.getElementById("manage-requests-loading").style.display = "none";
    if (!res.ok) {
      showToast("Failed to load requests", "error");
      return;
    }
    const requests = await res.json();
    if (requests.length === 0) {
      document.getElementById("manage-requests-empty").style.display = "block";
      return;
    }
    await renderManageRequests(requests);
  } catch (e) {
    showToast("Could not load requests", "error");
  }
}

async function renderManageRequests(requests) {
  const list = document.getElementById("manage-requests-list");
  const pending = requests.filter((r) => r.status === "pending");
  const approved = requests.filter((r) => r.status === "approved");
  const rejected = requests.filter((r) => r.status === "rejected");

  let html = "";
  if (pending.length > 0) {
    html += `<div class="requests-section-label"><i class="ti ti-clock"></i> Pending (${pending.length})</div>`;
    html += pending.map(renderRequestCard).join("");
  }
  if (approved.length > 0) {
    html += `<div class="requests-section-label"><i class="ti ti-circle-check"></i> Approved Participants (${approved.length})</div>`;
    const token = getToken();
    const approvedWithContacts = await Promise.all(
      approved.map(async (r) => {
        try {
          const res = await fetch(`${API}/participation/${r.id}/contact`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          if (res.ok) {
            const contact = await res.json();
            return { ...r, contact };
          }
        } catch (e) {
          console.error(`Failed to load contact for request ${r.id}:`, e);
        }
        return { ...r, contact: { name: r.requester_name || `User #${r.requester_id}`, phone_number: "N/A" } };
      })
    );

    html += `<div class="approved-participants-list" style="display: flex; flex-direction: column; gap: 8px; margin-bottom: 12px;">`;
    approvedWithContacts.forEach(p => {
      html += `
        <div class="participant-item" style="display: flex; align-items: center; justify-content: space-between; padding: 10px 14px; background: var(--green-light); border: 1px solid var(--green); border-radius: 10px;">
          <div style="display: flex; align-items: center; gap: 8px;">
            <i class="ti ti-user" style="font-size: 18px;"></i>
            <span style="font-weight: 700; color: var(--text);">${p.contact.name}</span>
          </div>
          <div style="font-weight: 700; color: var(--green-dark); display: flex; align-items: center; gap: 4px;">
            <i class="ti ti-phone"></i> ${p.contact.phone_number}
          </div>
        </div>
      `;
    });
    html += `</div>`;
  }
  if (rejected.length > 0) {
    html += `<div class="requests-section-label"><i class="ti ti-circle-x"></i> Rejected (${rejected.length})</div>`;
    html += rejected.map(renderRequestCard).join("");
  }
  list.innerHTML = html;
}

function renderRequestCard(req) {
  const date = new Date(req.requested_at).toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
  const statusChip = `<span class="status-chip ${req.status}">${req.status}</span>`;
  const displayName = req.requester_name || `User #${req.requester_id}`;
  const actions =
    req.status === "pending"
      ? `
    <div class="request-actions">
      <button class="btn-approve" onclick="handleApprove(${req.id})"><i class="ti ti-check"></i> Approve</button>
      <button class="btn-reject" onclick="handleReject(${req.id})"><i class="ti ti-x"></i> Reject</button>
    </div>`
      : "";
  const contactSection =
    req.status === "approved"
      ? `
    <div class="contact-reveal" id="contact-${req.id}">
      <i class="ti ti-phone"></i>
      <div>
        <div class="contact-reveal-text">${displayName} — phone hidden</div>
        <div class="contact-reveal-sub">Tap Reveal to see their phone number</div>
      </div>
      <button class="btn-reveal" onclick="revealContact(${req.id})">Reveal Phone</button>
    </div>`
      : "";

  return `
  <div class="request-card ${req.status}" id="request-card-${req.id}">
    <div class="request-card-top">
      <div class="requester-info">
        <i class="ti ti-user" style="font-size: 18px;"></i>
        <div>
          <div class="requester-name">${displayName}</div>
          <div class="requester-date">Requested: ${date}</div>
        </div>
      </div>
      ${statusChip}
    </div>
    ${actions}
    ${contactSection}
  </div>`;
}

window.handleApprove = async function (requestId) {
  const token = getToken();
  if (!token) return;
  try {
    const res = await fetch(`${API}/participation/${requestId}/approve`, {
      method: "PUT",
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) {
      const d = await res.json();
      showToast(d.detail || "Could not approve", "error");
      return;
    }
    showToast("Request approved!", "success");
    await loadManageRequests(selectedManageActivityId);
    loadPendingCount(selectedManageActivityId);
  } catch (e) {
    showToast("Could not approve request", "error");
  }
};

window.handleReject = async function (requestId) {
  const token = getToken();
  if (!token) return;
  try {
    const res = await fetch(`${API}/participation/${requestId}/reject`, {
      method: "PUT",
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) {
      const d = await res.json();
      showToast(d.detail || "Could not reject", "error");
      return;
    }
    showToast("Request rejected.", "info");
    await loadManageRequests(selectedManageActivityId);
    loadPendingCount(selectedManageActivityId);
  } catch (e) {
    showToast("Could not reject request", "error");
  }
};

window.revealContact = async function (requestId) {
  const token = getToken();
  if (!token) return;
  try {
    const res = await fetch(`${API}/participation/${requestId}/contact`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) {
      showToast("Could not load contact", "error");
      return;
    }
    const contact = await res.json();
    const el = document.getElementById(`contact-${requestId}`);
    if (el)
      el.innerHTML = `
      <i class="ti ti-phone" style="color:var(--green-dark)"></i>
      <div>
        <div class="contact-reveal-text">${contact.name}</div>
        <div class="contact-reveal-sub" style="color:var(--green-dark);font-weight:700;display:flex;align-items:center;gap:4px;"><i class="ti ti-phone"></i> ${contact.phone_number}</div>
      </div>
    `;
  } catch (e) {
    showToast("Could not load contact", "error");
  }
};

// ── PAGINATION BUILDER ────────────────────────────────────
function buildPagination(current, total, callbackName) {
  if (total <= 1) return "";
  return `
  <div class="pagination">
    <button class="page-btn" onclick="${callbackName}(${current - 1})" ${current === 1 ? "disabled" : ""}>
      <i class="ti ti-chevron-left"></i>
    </button>
    <span class="page-info">${current} / ${total}</span>
    <button class="page-btn" onclick="${callbackName}(${current + 1})" ${current === total ? "disabled" : ""}>
      <i class="ti ti-chevron-right"></i>
    </button>
  </div>`;
}

// ── TOGGLE EDIT ───────────────────────────────────────────
export function toggleEdit() {
  const panel = document.getElementById("edit-panel");
  if (!panel) return;
  panel.classList.toggle("open");
  if (panel.classList.contains("open"))
    panel.scrollIntoView({ behavior: "smooth", block: "nearest" });
}

// ── SAVE PROFILE ──────────────────────────────────────────
export async function saveProfile() {
  const token = getToken();
  if (!token) return;
  const name = document.getElementById("edit-name")?.value.trim();
  const phone = document.getElementById("edit-phone")?.value.trim();
  const city = document.getElementById("edit-city")?.value;
  const bio = document.getElementById("edit-bio")?.value.trim();
  const gender = document.getElementById("edit-gender")?.value;
  let hasError = false;

  if (!name) {
    setFieldError("edit-name", "Name is required");
    hasError = true;
  } else if (name.length < 2) {
    setFieldError("edit-name", "Name must be at least 2 characters");
    hasError = true;
  } else if (!/^[A-Za-z ]+$/.test(name)) {
    setFieldError("edit-name", "Name can only contain letters and spaces");
    hasError = true;
  } else {
    clearFieldError("edit-name");
  }

  const phoneRegex = /^[6-9]\d{9}$/;
  if (phone && !phoneRegex.test(phone)) {
    setFieldError("edit-phone", "Enter a valid 10-digit Indian mobile number");
    hasError = true;
  } else {
    clearFieldError("edit-phone");
  }

  clearFieldError("edit-city");
  clearFieldError("edit-bio");
  if (hasError) return;

  const btn = document.getElementById("save-btn");
  btn.disabled = true;
  btn.textContent = "Saving...";

  const body = {};
  if (name) body.name = name;
  if (phone) body.phone_number = phone;
  if (city) body.city = city;
  if (bio) body.bio = bio;
  if (gender) body.gender = gender;

  try {
    const res = await fetch(`${API}/users/me`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(body),
    });
    const data = await res.json();
    if (!res.ok) {
      showMsg("edit-error", data.detail || "Update failed.", "error");
      return;
    }
    showMsg("edit-success", "Profile updated! ✓", "success");
    renderProfile(data);
    setTimeout(() => {
      document.getElementById("edit-panel")?.classList.remove("open");
    }, 1500);
  } catch (e) {
    showMsg("edit-error", "Could not reach the server.", "error");
  } finally {
    btn.disabled = false;
    btn.textContent = "Save changes";
  }
}

// ── INIT ──────────────────────────────────────────────────
document.addEventListener("DOMContentLoaded", () => {
  ["edit-name", "edit-phone", "edit-city", "edit-bio", "edit-gender"].forEach(
    (id) => {
      const el = document.getElementById(id);
      if (el) {
        el.addEventListener("input", () => clearFieldError(id));
        el.addEventListener("change", () => clearFieldError(id));
      }
    },
  );
});

window.selectManageActivity = window.selectManageActivity;
window.setAppFilter = setAppFilter;
window.setActivityFilter = setActivityFilter;