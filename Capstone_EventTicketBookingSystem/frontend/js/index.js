const BASE = "http://localhost:8080/api";
console.log("home page script is ready");

let allEvents = [];
let activeFilter = "ALL";

function toast(msg, type = "success") {
  const t = document.getElementById("toast");
  t.innerHTML = msg;
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
    if (!res.ok) {
        const errMsg = await handleBackendError(res);
        toast(errMsg, "error");
        return;
    }
    allEvents = await res.json();
    console.log("successfully loaded events:", allEvents.length);
    renderEvents(allEvents);
  } catch (e) {
    console.error("failed to load events:", e);
    document.getElementById("eventsGrid").innerHTML =
      `<div style="grid-column:1/-1" class="state-box"><div class="state-icon"><i class="fa-solid fa-triangle-exclamation"></i></div><h3>Could not load events</h3><p>${getFriendlyMessage(e.message) || "Our servers are taking a break. Please try again later."}</p></div>`;
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

function categoryImage(cat) {
  const map = {
    technology: "",
    music:
      "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=500&q=80",
    sports:
      "https://images.unsplash.com/photo-1612872087720-bb876e2e67d1?q=80&w=1007&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    art: "https://images.unsplash.com/photo-1460661419201-fd4cecdf8a8b?w=500&q=80",
    food: "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=500&q=80",
    comedy:
      "https://images.unsplash.com/photo-1527224857830-43a7acc85260?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8Y29tZWR5fGVufDB8fDB8fHww",
    dance:
      "https://images.unsplash.com/photo-1537365587684-f490102e1225?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTR8fGRhbmNlfGVufDB8fDB8fHww",
    business:
      "https://images.unsplash.com/photo-1542744173-8e7e53415bb0?w=500&q=80",
    health:
      "https://images.unsplash.com/photo-1505751172876-fa1923c5c528?w=500&q=80",
    gaming:
      "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=500&q=80",
  };
  const url =
    map[(cat || "").toLowerCase()] ||
    "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=500&q=80";
  return `<img src="${url}" alt="${cat || "event"} image" />`;
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
      seatClass = "seats-full",
      seatLabel = "Sold Out";
    } else if (pct < 0.2) {
      seatClass = "seats-low",
      seatLabel = "Only " + available + " left!";
    }
    const card = document.createElement("div");
    card.className = "event-card";
    card.style.animationDelay = i * 0.06 + "s";
    card.innerHTML = `
        <div class="card-top">
          <div class="card-image-wrapper">${categoryImage(e.category)}</div>
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
    toast("Hey there! You need to log in first to book tickets. ", "error");
    setTimeout(() => (location.href = "login.html"), 1800);
    return;
  }
  location.href = "event-detail.html?id=" + id;
}



loadEvents();
