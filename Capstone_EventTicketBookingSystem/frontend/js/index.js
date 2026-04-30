const BASE = "http://localhost:8080/api";
console.log("home page script is ready");

let allEvents = [];
let activeFilter = "ALL";

function toast(msg, type = "success") {
  const t = document.getElementById("toast");
  t.textContent = msg;
  t.className = "toast " + type + " show";
  setTimeout(() => t.classList.remove("show"), 3000);
}

function logout() {
  console.log("user logged out, clearing local storage");
  localStorage.clear();
  location.href = "login.html";
}

async function loadEvents() {
  console.log("fetching all upcoming events from server...");
  try {
    const res = await fetch(`${BASE}/events`);
    allEvents = await res.json();
    console.log("successfully loaded events:", allEvents.length);
    renderEvents(allEvents);
  } catch (e) {
    console.error("failed to load events:", e);
    document.getElementById("eventsGrid").innerHTML =
      `<div style="grid-column:1/-1" class="state-box"><div class="state-icon"><i class="fa-solid fa-triangle-exclamation"></i></div><h3>Could not load events</h3><p>Make sure the server is running</p></div>`;
  }
}

function setFilter(cat, el) {
  console.log("user clicked on category filter:", cat);
  activeFilter = cat;
  document
    .querySelectorAll(".chip")
    .forEach((c) => c.classList.remove("active"));
  el.classList.add("active");
  filterEvents();
}

function filterEvents() {
  const q = document.getElementById("searchInput").value.toLowerCase();
  let filtered = allEvents;
  
  // filtering by category if they selected one
  if (activeFilter !== "ALL")
    filtered = filtered.filter(
      (e) => (e.category || "").toLowerCase() === activeFilter.toLowerCase(),
    );
    
  // searching by name or venue
  if (q)
    filtered = filtered.filter(
      (e) =>
        e.eventName.toLowerCase().includes(q) ||
        (e.venue || "").toLowerCase().includes(q),
    );
    
  renderEvents(filtered);
}

document.getElementById("searchInput").addEventListener("input", filterEvents);

function categoryEmoji(cat) {
  const map = {
    technology: '<i class="fa-solid fa-laptop-code"></i>',
    music: '<i class="fa-solid fa-music"></i>',
    sports: '<i class="fa-solid fa-futbol"></i>',
    art: '<i class="fa-solid fa-palette"></i>',
    food: '<i class="fa-solid fa-pizza-slice"></i>',
    comedy: '<i class="fa-solid fa-face-laugh-squint"></i>',
    dance: '<i class="fa-solid fa-person-dress"></i>',
    business: '<i class="fa-solid fa-briefcase"></i>',
    health: '<i class="fa-solid fa-hospital"></i>',
    gaming: '<i class="fa-solid fa-gamepad"></i>',
  };
  return map[(cat || "").toLowerCase()] || '<i class="fa-solid fa-tent"></i>';
}

function renderEvents(events) {
  const grid = document.getElementById("eventsGrid");
  document.getElementById("eventsCount").textContent =
    events.length + " event" + (events.length !== 1 ? "s" : "") + " found";
  if (!events.length) {
    grid.innerHTML = `<div style="grid-column:1/-1" class="state-box"><div class="state-icon"><i class="fa-solid fa-ticket"></i></div><h3>No events found</h3><p>Try a different search or filter</p></div>`;
    return;
  }
  grid.innerHTML = "";
  events.forEach((e, i) => {
    const dt = new Date(e.eventDateTime);
    const available = e.availableSeats ?? 0;
    const pct = e.totalSeats > 0 ? available / e.totalSeats : 0;
    let seatClass = "seats-ok",
      seatLabel = available + " seats left";
    if (available === 0) {
      seatClass = "seats-full";
      seatLabel = "Sold Out";
    } else if (pct < 0.2) {
      seatClass = "seats-low";
      seatLabel = "Only " + available + " left!";
    }
    const card = document.createElement("div");
    card.className = "event-card";
    card.style.animationDelay = i * 0.06 + "s";
    card.innerHTML = `
        <div class="card-top">
          <div class="card-emoji">${categoryEmoji(e.category)}</div>
          <div class="card-price-badge">₹${(e.ticketPrice || 0).toLocaleString("en-IN")}</div>
        </div>
        <div class="card-body">
          <div class="card-category">${e.category || "General"}</div>
          <div class="card-name">${e.eventName}</div>
          <div class="card-meta">
            <div class="card-meta-row"><i class="fa-regular fa-calendar"></i> ${dt.toLocaleDateString("en-IN", { weekday: "short", day: "numeric", month: "short", year: "numeric" })}</div>
            <div class="card-meta-row"><i class="fa-regular fa-clock"></i> ${dt.toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit" })}</div>
            <div class="card-meta-row"><i class="fa-solid fa-location-dot"></i> ${e.venue}</div>
          </div>
          <div class="card-seats">
            <span class="seats-text">Availability</span>
            <span class="seats-badge ${seatClass}">${seatLabel}</span>
          </div>
          <button class="card-btn" ${available === 0 ? "disabled" : ""} onclick="goToEvent(${e.id})">
            ${available === 0 ? "Sold Out" : "View & Book →"}
          </button>
        </div>`;
    grid.appendChild(card);
  });
}

function goToEvent(id) {
  console.log("user wants to view event details for id:", id);
  // making sure they are logged in before booking
  if (!localStorage.getItem("token")) {
    console.log("user not logged in, sending to login page");
    toast("Please login to book tickets", "error");
    setTimeout(() => (location.href = "login.html"), 1500);
    return;
  }
  location.href = "event-detail.html?id=" + id;
}

loadEvents();
