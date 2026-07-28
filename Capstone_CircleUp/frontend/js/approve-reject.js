const API = "";

let selectedActivityId = null;
let myActivities = [];

// ── AUTH ──
function checkAuth() {
  const token = localStorage.getItem("token");
  if (!token) window.location.href = "./index.html";
  return token;
}

function logout() {
  localStorage.removeItem("token");
  window.location.href = "./index.html";
}

// ── TOAST ──
function showToast(msg, type = "success") {
  const container = document.getElementById("toast-container");
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

// ── LOAD MY ACTIVITIES ──
async function loadMyActivities() {
  const token = checkAuth();

  try {
    const res = await fetch(`${API}/activities`, {
      headers: { Authorization: `Bearer ${token}` },
    });

    if (!res.ok) throw new Error("Failed to load");

    const all = await res.json();

    // get current user
    const meRes = await fetch(`${API}/users/me`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    const me = await meRes.json();

    // filter activities I created
    myActivities = all.filter((a) => a.creator_id === me.id);

    document.getElementById("my-activities-loading").style.display = "none";

    if (myActivities.length === 0) {
      document.getElementById("my-activities-empty").style.display = "block";
      return;
    }

    renderMyActivities(myActivities);
  } catch (e) {
    document.getElementById("my-activities-loading").textContent =
      "Failed to load.";
  }
}

// ── RENDER MY ACTIVITIES ──
function renderMyActivities(activities) {
  const list = document.getElementById("my-activities-list");
  list.innerHTML = activities
    .map(
      (a) => `
    <div class="activity-item" id="activity-item-${a.id}" onclick="selectActivity(${a.id})">
      <div class="activity-item-title">${a.title}</div>
      <div class="activity-item-meta">
        <i class="ti ti-map-pin" style="font-size:12px"></i>
        ${a.location}
        <span class="pending-count" id="pending-count-${a.id}">…</span>
      </div>
    </div>
  `,
    )
    .join("");

  // load pending counts for each activity
  activities.forEach((a) => loadPendingCount(a.id));
}

// ── LOAD PENDING COUNT ──
async function loadPendingCount(activityId) {
  const token = checkAuth();
  try {
    const res = await fetch(`${API}/participation/activity/${activityId}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) return;
    const requests = await res.json();
    const pending = requests.filter((r) => r.status === "pending").length;
    const el = document.getElementById(`pending-count-${activityId}`);
    if (el) el.textContent = pending > 0 ? pending : "0";
  } catch (e) {
    console.error(e);
  }
}

// ── SELECT ACTIVITY ──
async function selectActivity(activityId) {
  selectedActivityId = activityId;

  // highlight selected
  document
    .querySelectorAll(".activity-item")
    .forEach((el) => el.classList.remove("selected"));
  document
    .getElementById(`activity-item-${activityId}`)
    ?.classList.add("selected");

  const activity = myActivities.find((a) => a.id === activityId);
  document.getElementById("requests-panel-title").textContent = activity
    ? activity.title
    : "Requests";

  document.getElementById("requests-placeholder").style.display = "none";
  document.getElementById("requests-loading").style.display = "block";
  document.getElementById("requests-list").innerHTML = "";
  document.getElementById("requests-empty").style.display = "none";

  await loadRequests(activityId);
}

// ── LOAD REQUESTS ──
async function loadRequests(activityId) {
  const token = checkAuth();
  try {
    const res = await fetch(`${API}/participation/activity/${activityId}`, {
      headers: { Authorization: `Bearer ${token}` },
    });

    document.getElementById("requests-loading").style.display = "none";

    if (!res.ok) {
      showToast("Failed to load requests", "error");
      return;
    }

    const requests = await res.json();

    if (requests.length === 0) {
      document.getElementById("requests-empty").style.display = "block";
      return;
    }

    await renderRequests(requests);
  } catch (e) {
    showToast("Could not load requests", "error");
  }
}

// ── RENDER REQUESTS ──
async function renderRequests(requests) {
  const list = document.getElementById("requests-list");

  const pending = requests.filter((r) => r.status === "pending");
  const approved = requests.filter((r) => r.status === "approved");
  const rejected = requests.filter((r) => r.status === "rejected");

  let html = "";

  if (pending.length > 0) {
    html += `<div class="requests-section-label"><i class="ti ti-clock"></i> Pending (${pending.length})</div>`;
    html += pending.map((r) => renderRequestCard(r)).join("");
  }

  if (approved.length > 0) {
    html += `<div class="requests-section-label"><i class="ti ti-circle-check"></i> Approved Participants (${approved.length})</div>`;
    const token = checkAuth();
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
    html += rejected.map((r) => renderRequestCard(r)).join("");
  }

  list.innerHTML = html;
}

// ── RENDER REQUEST CARD ──
function renderRequestCard(req) {
  const date = new Date(req.requested_at).toLocaleDateString("en-US", { month: "short", day: "numeric", hour: "2-digit", minute: "2-digit" });
  const statusChip = `<span class="status-chip ${req.status}">${req.status}</span>`;
  const displayName = req.requester_name || `User #${req.requester_id}`;
  const actions = req.status === "pending" ? `
    <div class="request-actions">
      <button class="btn-approve" onclick="handleApprove(${req.id})"><i class="ti ti-check"></i> Approve</button>
      <button class="btn-reject" onclick="handleReject(${req.id})"><i class="ti ti-x"></i> Reject</button>
    </div>` : "";
  // Name is always visible; phone is only revealed after approval
  const contactSection = req.status === "approved" ? `
    <div class="contact-reveal" id="contact-${req.id}">
      <i class="ti ti-phone"></i>
      <div>
        <div class="contact-reveal-text">${displayName} — phone hidden</div>
        <div class="contact-reveal-sub">Tap Reveal to see their phone number</div>
      </div>
      <button class="btn-reveal" onclick="revealContact(${req.id})">Reveal Phone</button>
    </div>` : "";

  return `
  <div class="request-card ${req.status}" id="request-card-${req.id}">
    <div class="request-card-top">
      <div class="requester-info">
        <div class="requester-avatar"><i class="ti ti-user" style="font-size: 18px;"></i></div>
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

// ── APPROVE ──
async function handleApprove(requestId) {
  const token = checkAuth();
  try {
    const res = await fetch(`${API}/participation/${requestId}/approve`, {
      method: "PUT",
      headers: { Authorization: `Bearer ${token}` },
    });

    if (!res.ok) {
      const data = await res.json();
      showToast(data.detail || "Could not approve", "error");
      return;
    }

    showToast("Request approved! 🎉", "success");
    await loadRequests(selectedActivityId);
    loadPendingCount(selectedActivityId);
  } catch (e) {
    showToast("Could not approve request", "error");
  }
}

// ── REJECT ──
async function handleReject(requestId) {
  const token = checkAuth();
  try {
    const res = await fetch(`${API}/participation/${requestId}/reject`, {
      method: "PUT",
      headers: { Authorization: `Bearer ${token}` },
    });

    if (!res.ok) {
      const data = await res.json();
      showToast(data.detail || "Could not reject", "error");
      return;
    }

    showToast("Request rejected.", "info");
    await loadRequests(selectedActivityId);
    loadPendingCount(selectedActivityId);
  } catch (e) {
    showToast("Could not reject request", "error");
  }
}

// ── REVEAL CONTACT ──
async function revealContact(requestId) {
  const token = checkAuth();
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
    if (el) {
      el.innerHTML = `
        <i class="ti ti-phone" style="color:var(--green-dark)"></i>
        <div>
          <div class="contact-reveal-text">${contact.name}</div>
          <div class="contact-reveal-sub" style="color:var(--green-dark);font-weight:700;display:flex;align-items:center;gap:4px;"><i class="ti ti-phone"></i> ${contact.phone_number}</div>
        </div>
      `;
    }
  } catch (e) {
    showToast("Could not load contact", "error");
  }
}

// ── INIT ──
document.addEventListener("DOMContentLoaded", async () => {
  if (checkAuth()) {
    await loadMyActivities();
    const params = new URLSearchParams(window.location.search);
    const actId = params.get("id");
    if (actId) {
      selectActivity(parseInt(actId));
    }
  }
});

window.selectActivity = selectActivity;
window.handleApprove = handleApprove;
window.handleReject = handleReject;
window.revealContact = revealContact;
window.logout = logout;
