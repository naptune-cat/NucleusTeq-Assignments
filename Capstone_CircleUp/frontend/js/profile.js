const API = "http://localhost:8000";

// ── Auth guard ────────────────────────────────────────────
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

// ── Helpers ───────────────────────────────────────────────
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

function setLoading(btnId, loading, defaultText) {
  const btn = document.getElementById(btnId);
  if (!btn) return;
  btn.disabled = loading;
  btn.textContent = loading ? defaultText + "..." : defaultText;
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
  const map = {
    male: "Male",
    female: "Female",
    other: "Other",
    prefer_not_to_say: "Prefer not to say",
  };
  return map[g] || g;
}

function formatDate(iso) {
  const d = new Date(iso);
  return d.toLocaleDateString("en-IN", {
    day: "numeric",
    month: "short",
    year: "numeric",
  });
}

function formatStatus(status) {
  const map = { open: "Open", full: "Full", cancelled: "Cancelled" };
  return map[status] || status;
}

function statusClass(status) {
  const map = {
    open: "status-open",
    full: "status-full",
    cancelled: "status-cancelled",
  };
  return map[status] || "";
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
  const idx = (category || "").length % styles.length;
  return styles[idx];
}

// ── Load profile ──────────────────────────────────────────
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
  } catch (e) {
    console.error("Failed to load profile:", e);
  }
}

function renderProfile(user) {
  // avatar initials
  const initials = getInitials(user.name);
  const avatarEl = document.getElementById("avatar-initials");
  if (avatarEl) avatarEl.textContent = initials;

  // header
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

  // pre-fill edit form
  setVal("edit-name", user.name);
  setVal("edit-phone", user.phone_number || "");
  setVal("edit-city", user.city || "");
  setVal("edit-bio", user.bio || "");
  setVal("edit-gender", user.gender || "");

  // member since
  if (user.id) {
    const memberEl = document.getElementById("stat-joined");
    if (memberEl) memberEl.textContent = "Jun 2026";
  }
}

function setText(id, val) {
  const el = document.getElementById(id);
  if (el) el.textContent = val;
}

function setVal(id, val) {
  const el = document.getElementById(id);
  if (el) el.value = val;
}

// ── Load my activities ────────────────────────────────────
async function loadMyActivities(token) {
  try {
    const res = await fetch(`${API}/activities`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    const activities = await res.json();

    // filter to only activities the current user created
    const meRes = await fetch(`${API}/users/me`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    const me = await meRes.json();

    const mine = activities.filter((a) => a.creator_id === me.id);

    const container = document.getElementById("activities-list");
    if (!container) return;

    if (mine.length === 0) {
      container.innerHTML = `
        <div style="text-align:center;padding:2rem;color:var(--text-muted)">
          <i class="ti ti-calendar-off" style="font-size:32px;margin-bottom:0.5rem;display:block" aria-hidden="true"></i>
          No activities yet — <a href="dashboard.html" style="color:var(--green-dark);font-weight:600">create one!</a>
        </div>`;
      return;
    }

    document.getElementById("stat-hosted").textContent = mine.length;

    container.innerHTML = mine
      .map((a) => {
        const style = categoryStyle(a.category);
        const icon = categoryIcon(a.category);
        return `
        <div class="activity-card">
          <div class="activity-icon" style="background:${style.bg}">
            <i class="ti ${icon}" style="color:${style.color};font-size:20px" aria-hidden="true"></i>
          </div>
          <div class="activity-info">
            <div class="activity-title">${a.title}</div>
            <div class="activity-meta">
              <i class="ti ti-map-pin" aria-hidden="true"></i> ${a.location}
              &nbsp;·&nbsp;
              <i class="ti ti-calendar" aria-hidden="true"></i> ${formatDate(a.activity_date)}
            </div>
          </div>
          <span class="status-pill ${statusClass(a.status)}">${formatStatus(a.status)}</span>
        </div>`;
      })
      .join("");
  } catch (e) {
    console.error("Failed to load activities:", e);
  }
}

// ── Toggle edit panel ─────────────────────────────────────
export function toggleEdit() {
  const panel = document.getElementById("edit-panel");
  if (!panel) return;
  panel.classList.toggle("open");
  if (panel.classList.contains("open")) {
    panel.scrollIntoView({ behavior: "smooth", block: "nearest" });
  }
}

// ── Save profile ──────────────────────────────────────────
export async function saveProfile() {
  const token = getToken();
  if (!token) return;

  const name = document.getElementById("edit-name")?.value.trim();
  const phone = document.getElementById("edit-phone")?.value.trim();
  const city = document.getElementById("edit-city")?.value.trim();
  const bio = document.getElementById("edit-bio")?.value.trim();
  const gender = document.getElementById("edit-gender")?.value;

  if (!name) {
    showMsg("edit-error", "Name cannot be empty.", "error");
    return;
  }

  setLoading("save-btn", true, "Save changes");

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

    showMsg("edit-success", "Profile updated!", "success");
    renderProfile(data);
    setTimeout(() => {
      document.getElementById("edit-panel")?.classList.remove("open");
    }, 1500);
  } catch (e) {
    showMsg("edit-error", "Could not reach the server.", "error");
  } finally {
    setLoading("save-btn", false, "Save changes");
  }
}
