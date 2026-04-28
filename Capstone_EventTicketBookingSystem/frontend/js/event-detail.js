const BASE = "http://localhost:8080/api";
const eventId = new URLSearchParams(location.search).get("id");
let ticketPrice = 0,
  maxSeats = 0,
  qty = 1;
if (!eventId) location.href = "index.html";

function toast(msg, type = "success") {
  const t = document.getElementById("toast");
  t.textContent = msg;
  t.className = "toast " + type + " show";
  setTimeout(() => t.classList.remove("show"), 3500);
}

function categoryEmoji(cat) {
  const map = {
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
  return map[(cat || "").toLowerCase()] || '"<i class="fa-solid fa-tent"></i>"';
}

function changeQty(d) {
  qty = Math.max(1, Math.min(maxSeats, qty + d));
  document.getElementById("qtyVal").textContent = qty;
  document.getElementById("totalVal").textContent =
    "₹" + (ticketPrice * qty).toLocaleString("en-IN");
}

async function loadEvent() {
  try {
    const res = await fetch(`${BASE}/events/${eventId}`);
    if (!res.ok) throw new Error();
    const e = await res.json();
    ticketPrice = e.ticketPrice || 0;
    maxSeats = e.availableSeats || 0;
    const dt = new Date(e.eventDateTime);
    const isCancelled = e.status === "CANCELLED";
    const isSoldOut = maxSeats === 0 && !isCancelled;
    const pct =
      e.totalSeats > 0 ? Math.round((e.bookedSeats / e.totalSeats) * 100) : 0;
    document.title = "Evently — " + e.eventName;

    document.getElementById("pageContent").innerHTML = `
        <div class="event-hero">
          <div class="event-hero-top">
            <div>
              <div class="event-big-emoji">${categoryEmoji(e.category)}</div>
              <div class="event-badge ${isCancelled ? "badge-cancelled" : "badge-active"}">
                <span style="width:6px;height:6px;border-radius:50%;background:currentColor;display:inline-block;"></span>
                ${isCancelled ? "Cancelled" : "Active"}
              </div>
              <div class="event-name">${e.eventName}</div>
              <div class="event-category">${e.category || "General Event"}</div>
            </div>
            <div class="price-box">
              <div class="price-label">Per Ticket</div>
              <div class="price-value">₹${ticketPrice.toLocaleString("en-IN")}</div>
              <div class="price-sub">Incl. all taxes</div>
            </div>
          </div>
          <div class="event-hero-meta">
            <div class="meta-chip"><span class="meta-chip-icon"><i class="fa-regular fa-calendar"></i></span><div><div class="meta-chip-label">Date</div><div class="meta-chip-value">${dt.toLocaleDateString("en-IN", { weekday: "short", day: "numeric", month: "long", year: "numeric" })}</div></div></div>
            <div class="meta-chip"><span class="meta-chip-icon"><i class="fa-regular fa-clock"></i></span><div><div class="meta-chip-label">Time</div><div class="meta-chip-value">${dt.toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit" })}</div></div></div>
            <div class="meta-chip"><span class="meta-chip-icon"><i class="fa-solid fa-location-dot"></i></span><div><div class="meta-chip-label">Venue</div><div class="meta-chip-value">${e.venue}</div></div></div>
            <div class="meta-chip"><span class="meta-chip-icon"><i class="fa-solid fa-ticket"></i></span><div><div class="meta-chip-label">Capacity</div><div class="meta-chip-value">${e.totalSeats} seats</div></div></div>
          </div>
        </div>
        <div class="two-col">
          <div class="detail-card">
            <h3>About This Event</h3>
            <div class="description">${e.description}</div>
          </div>
          <div class="seats-card">
            <div class="seats-title">Book Tickets</div>
            <div class="seats-row"><span>Total Seats</span><span>${e.totalSeats}</span></div>
            <div class="seats-row"><span>Booked</span><span>${e.bookedSeats}</span></div>
            <div class="seats-row"><span>Available</span><span style="color:${maxSeats === 0 ? "var(--danger)" : "var(--success)"};">${maxSeats}</span></div>
            <div class="progress-bar"><div class="progress-fill" style="width:${pct}%"></div></div>
            ${
              isCancelled
                ? `<div style="text-align:center;padding:16px;color:var(--danger);font-weight:500;background:rgba(239,68,68,0.08);border-radius:10px;">This event has been cancelled</div>`
                : isSoldOut
                  ? `<div style="text-align:center;padding:16px;color:var(--muted);font-weight:500;background:var(--surface2);border-radius:10px;">This event is sold out</div>`
                  : `<div class="qty-row">
                  <span class="qty-label">Number of Tickets</span>
                  <div class="qty-ctrl">
                    <button class="qty-btn" onclick="changeQty(-1)">−</button>
                    <div class="qty-val" id="qtyVal">1</div>
                    <button class="qty-btn" onclick="changeQty(1)">+</button>
                  </div>
                </div>
                <div class="total-row">
                  <span class="total-label">Total Amount</span>
                  <span class="total-val" id="totalVal">₹${ticketPrice.toLocaleString("en-IN")}</span>
                </div>
                <button class="book-btn" onclick="proceedToPayment()">Proceed to Payment →</button>
                <p style="font-size:11px;color:var(--muted2);text-align:center;margin-top:10px;">Up to ${maxSeats} tickets available</p>`
            }
          </div>
        </div>`;
  } catch (err) {
    document.getElementById("pageContent").innerHTML =
      `<div style="text-align:center;padding:80px;"><div style="font-size:52px;opacity:0.4;"><i class="fa-solid fa-triangle-exclamation"></i></div><h3 style="font-family:'Syne',sans-serif;font-size:20px;margin:16px 0 8px;">Event not found</h3><p style="color:var(--muted);">This event may have been removed</p></div>`;
  }
}

function proceedToPayment() {
  if (!localStorage.getItem("token")) {
    toast("Please login first", "error");
    return;
  }
  localStorage.setItem("pendingEventId", eventId);
  localStorage.setItem("pendingQty", qty);
  location.href = "payment.html";
}

loadEvent();
