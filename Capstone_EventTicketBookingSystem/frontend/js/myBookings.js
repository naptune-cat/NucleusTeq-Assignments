// myBookings.js
const BASE = "http://localhost:8080/api";
const token = localStorage.getItem("token");

// redirecting back to login if they aren't signed in
if (!token) {
  console.log("no token found, sending to login page");
  location.href = "login.html";
}

let allBookings = [];
let activeTab = "ALL";

function hdrs(json = false) {
  const h = { Authorization: "Bearer " + token };
  if (json) h["Content-Type"] = "application/json";
  return h;
}

function toast(msg, type = "success") {
  const t = document.getElementById("toast");
  t.innerHTML = msg;
  t.className = "toast " + type + " show";
  setTimeout(() => t.classList.remove("show"), 3500);
}

function logout() {
  console.log("user logged out");
  localStorage.clear();
  location.href = "login.html";
}

function catEmoji(cat) {
  const m = {
    technology: '"<i class="fa-solid fa-laptop-code"></i>"',
    music: '"<i class="fa-solid fa-music"></i>"',
    sports: '"<i class="fa-solid fa-futbol"></i>"',
    art: '"<i class="fa-solid fa-palette"></i>"',
    food: '"<i class="fa-solid fa-pizza-slice"></i>"',
    comedy: '"<i class="fa-solid fa-face-laugh-squint"></i>"',
    dance: '"<i class="fa-solid fa-person-dress"></i>"',
    business: '"<i class="fa-solid fa-briefcase"></i>"',
    health: '"<i class="fa-solid fa-hospital"></i>"',
    gaming: '"<i class="fa-solid fa-gamepad"></i>"',
  };
  return m[(cat || "").toLowerCase()] || '"<i class="fa-solid fa-tent"></i>"';
}

function setTab(tab, el) {
  console.log("switching tab to:", tab);
  activeTab = tab;
  document
    .querySelectorAll(".tab")
    .forEach((t) => t.classList.remove("active"));
  el.classList.add("active");
  renderBookings();
}

async function loadBookings() {
  console.log("fetching my bookings from the server...");
  try {
    const res = await fetch(`${BASE}/bookings/my`, { headers: hdrs() });
    if (!res.ok) {
      const errMsg = await handleBackendError(res);
      toast(errMsg, "error");
      throw new Error(errMsg);
    }
    allBookings = await res.json();
    console.log("loaded bookings:", allBookings.length);
    updateCounts();
    renderBookings();
  } catch (e) {
    console.error("failed to load bookings:", e);
    document.getElementById("bookingsList").innerHTML =
      `<div class="state-box"><div class="state-icon"><i class="fa-solid fa-triangle-exclamation"></i></div><h3>Could not load bookings</h3><p>${getFriendlyMessage(e.message) || "Make sure you are logged in"}</p></div>`;
  }
}

function updateCounts() {
  document.getElementById("countAll").textContent = allBookings.length;
  document.getElementById("countConfirmed").textContent = allBookings.filter(
    (b) => b.bookingStatus === "CONFIRMED",
  ).length;
  document.getElementById("countCancelled").textContent = allBookings.filter(
    (b) =>
      b.bookingStatus === "CANCELLED" ||
      b.bookingStatus === "CANCELLED_BY_ORGANIZER",
  ).length;
}

function renderBookings() {
  const list = document.getElementById("bookingsList");
  let filtered = allBookings;
  if (activeTab !== "ALL") {
    if (activeTab === "CANCELLED") {
      filtered = allBookings.filter(
        (b) =>
          b.bookingStatus === "CANCELLED" ||
          b.bookingStatus === "CANCELLED_BY_ORGANIZER",
      );
    } else {
      filtered = allBookings.filter((b) => b.bookingStatus === activeTab);
    }
  }

  if (!filtered.length) {
    console.log("no bookings to show for this tab");
    list.innerHTML = `<div class="state-box"><div class="state-icon"><i class="fa-solid fa-ticket"></i></div><h3>No bookings here</h3><p>${activeTab === "ALL" ? "You haven't booked any events yet" : "No " + activeTab.toLowerCase() + " bookings"}</p><button class="browse-btn" onclick="location.href='index.html'">Browse Events</button></div>`;
    return;
  }

  // Sort: newest first
  filtered = [...filtered].sort(
    (a, b) => new Date(b.bookingTime) - new Date(a.bookingTime),
  );
  list.innerHTML = "";

  filtered.forEach((b, i) => {
    const eventDt = new Date(b.eventDateTime);
    const bookingDt = new Date(b.bookingTime);
    const isUpcoming = eventDt > new Date();
    const status = b.bookingStatus;
    const stripeClass =
      status === "CANCELLED" || status === "CANCELLED_BY_ORGANIZER"
        ? "cancelled"
        : status === "PENDING"
          ? "pending"
          : "";

    const statusBadge =
      status === "CONFIRMED"
        ? `<span class="status-badge status-confirmed"><span class="status-dot"></span>Confirmed</span>`
        : status === "CANCELLED"
          ? `<span class="status-badge status-cancelled"><span class="status-dot"></span>Cancelled</span>`
          : status === "CANCELLED_BY_ORGANIZER"
            ? `<span class="status-badge status-cancelled"><span class="status-dot"></span>Cancelled by Organizer</span>`
            : `<span class="status-badge status-pending"><span class="status-dot"></span>Pending</span>`;

    const canCancel = status === "CONFIRMED" && isUpcoming;

    const wrap = document.createElement("div");
    wrap.className = "ticket-wrap";
    wrap.style.animationDelay = i * 0.07 + "s";
    wrap.innerHTML = `
        <div class="ticket">
          <div class="ticket-stripe ${stripeClass}"></div>
          <div class="ticket-inner">
            <div class="ticket-left">
              <div class="ticket-status-row">
                ${statusBadge}
                ${isUpcoming && status === "CONFIRMED" ? '<span style="font-size:11px;color:var(--success);">Upcoming ✓</span>' : ""}
                ${!isUpcoming && status !== "CANCELLED" && status !== "CANCELLED_BY_ORGANIZER" ? '<span style="font-size:11px;color:var(--muted);">Past event</span>' : ""}
              </div>
              <div class="ticket-event-name">${b.eventName}</div>
              <div class="ticket-meta-grid">
                <div class="ticket-meta-item"><span class="ticket-meta-icon"><i class="fa-regular fa-calendar"></i></span>${eventDt.toLocaleDateString("en-IN", { weekday: "short", day: "numeric", month: "short", year: "numeric" })}</div>
                <div class="ticket-meta-item"><span class="ticket-meta-icon"><i class="fa-regular fa-clock"></i></span>${eventDt.toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit" })}</div>
                <div class="ticket-meta-item"><span class="ticket-meta-icon"><i class="fa-solid fa-location-dot"></i></span>${b.venue}</div>
                <div class="ticket-meta-item"><span class="ticket-meta-icon"><i class="fa-solid fa-ticket"></i></span>${b.numberOfTickets} ticket${b.numberOfTickets > 1 ? "s" : ""}</div>
              </div>
              <hr class="ticket-divider"/>
              <div class="ticket-footer">
                <div class="ticket-booking-id">Booking #${b.bookingId} · Booked ${bookingDt.toLocaleDateString("en-IN")}</div>
                ${canCancel ? `<button class="cancel-btn" onclick="cancelBooking(${b.bookingId})">Cancel Booking</button>` : ""}
                ${(status === "CANCELLED" || status === "CANCELLED_BY_ORGANIZER") && b.cancellationTime ? `<span style="font-size:11px;color:var(--muted2);">Cancelled on ${new Date(b.cancellationTime).toLocaleDateString("en-IN")}</span>` : ""}
              </div>
            </div>
            <div class="ticket-right">
              <div class="ticket-amount">₹${(b.totalAmount || 0).toLocaleString("en-IN")}</div>
              <div class="ticket-qty">${b.numberOfTickets} × ticket</div>
            </div>
          </div>
        </div>`;
    list.appendChild(wrap);
  });
}

async function cancelBooking(id) {
  console.log("user requested to cancel booking:", id);
  if (
    !confirm(
      "Are you sure you want to cancel this booking?\nCancellations are allowed only up to 3 hours before the event.",
    )
  ) {
    console.log("user backed out of cancelling");
    return;
  }

  try {
    const res = await fetch(`${BASE}/bookings/${id}/cancel`, {
      method: "PATCH",
      headers: hdrs(),
    });
    if (res.ok) {
      console.log("booking cancelled successfully!");
      toast(
        "Booking has been cancelled successfully. Refund will be processed soon.",
        "success",
      );
      await loadBookings();
    } else {
      console.log("failed to cancel booking");
      const errMsg = await handleBackendError(res);
      toast(errMsg, "error");
    }
  } catch (e) {
    console.error("error while cancelling booking:", e);
    toast(
      getFriendlyMessage(e.message) || "Server error. Please try again.",
      "error",
    );
  }
}

loadBookings();
