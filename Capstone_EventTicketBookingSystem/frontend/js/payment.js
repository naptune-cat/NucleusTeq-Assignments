const BASE = "http://localhost:8080/api";

const token = localStorage.getItem("token");
const eventId = localStorage.getItem("pendingEventId");
const qty = parseInt(localStorage.getItem("pendingQty") || "1");

let selectedMethod = "UPI";
let eventData = null;

// Redirect if missing data
if (!token || !eventId) {
  console.log("Missing token or eventId → redirecting");
  location.href = "index.html";
}

function hdrs(json = false) {
  const h = { Authorization: "Bearer " + token };
  if (json) h["Content-Type"] = "application/json";
  return h;
}

function toast(msg, type = "success") {
  const t = document.getElementById("toast");
  t.innerHTML = msg;
  t.className = "toast " + type + " show";
  setTimeout(() => t.classList.remove("show"), 3000);
}

function selectMethod(method, el) {
  selectedMethod = method;

  document
    .querySelectorAll(".method-btn")
    .forEach((btn) => btn.classList.remove("selected"));

  el.classList.add("selected");
}

// Icons mapping
function catEmoji(cat) {
  const m = {
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
  return m[(cat || "").toLowerCase()] || '<i class="fa-solid fa-tent"></i>';
}

function cloneTemplate(id) {
  return document.getElementById(id).content.cloneNode(true);
}
// load page function loads event data and renders the payment page
async function loadPage() {
  try {
    const res = await fetch(`${BASE}/events/${eventId}`);
    if (!res.ok) throw new Error("Event load failed");

    eventData = await res.json();

    const dt = new Date(eventData.eventDateTime);
    const total = (eventData.ticketPrice || 0) * qty;
    const totalStr = `₹${total.toLocaleString("en-IN")}`;

    const container = document.getElementById("pageContent");
    container.innerHTML = "";

    // Payment card
    const payFrag = cloneTemplate("tpl-pay-card");

    payFrag.querySelectorAll(".method-btn").forEach((btn) => {
      btn.addEventListener("click", () =>
        selectMethod(btn.dataset.method, btn),
      );
    });

    container.appendChild(payFrag);

    // Summary card
    const sumFrag = cloneTemplate("tpl-summary-card");

    sumFrag.querySelector("[data-slot='emoji']").innerHTML = catEmoji(
      eventData.category,
    );

    sumFrag.querySelector("[data-slot='name']").textContent =
      eventData.eventName;

    sumFrag.querySelector("[data-slot='meta']").innerHTML = `
      <i class="fa-regular fa-calendar"></i> 
      ${dt.toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric" })}<br/>
      <i class="fa-regular fa-clock"></i> 
      ${dt.toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit" })}<br/>
      <i class="fa-solid fa-location-dot"></i> ${eventData.venue}
    `;

    sumFrag.querySelector("[data-slot='price']").textContent =
      `₹${(eventData.ticketPrice || 0).toLocaleString("en-IN")}`;

    sumFrag.querySelector("[data-slot='qty']").textContent =
      `${qty} ticket${qty > 1 ? "s" : ""}`;

    sumFrag.querySelector("[data-slot='total']").textContent = totalStr;
    sumFrag.querySelector("[data-slot='pay-total']").textContent = totalStr;

    container.appendChild(sumFrag);

    document.getElementById("payBtn").addEventListener("click", processPayment);
  } catch (e) {
    toast(e.message || "Failed to load event", "error");
  }
}

// payment processing function that handles booking and payment logic
async function processPayment() {
  const payBtn = document.getElementById("payBtn");
  const overlay = document.getElementById("overlay");

  payBtn.disabled = true;
  overlay.classList.add("show");

  try {
    // firstly creating booking
    const bookRes = await fetch(`${BASE}/bookings`, {
      method: "POST",
      headers: hdrs(true),
      body: JSON.stringify({
        eventId: parseInt(eventId),
        numberOfTickets: qty,
      }),
    });

    if (!bookRes.ok) {
      const err = await bookRes.json();
      throw new Error(err.message || "Booking failed");
    }

    const booking = await bookRes.json();

    // Faking delay (spinner effect)
    await new Promise((r) => setTimeout(r, 2000));

    // then Processing Payment
    const payRes = await fetch(`${BASE}/bookings/payment`, {
      method: "POST",
      headers: hdrs(true),
      body: JSON.stringify({
        bookingId: booking.bookingId,
        paymentMethod: selectedMethod,
      }),
    });

    if (!payRes.ok) {
      const err = await payRes.json();
      throw new Error(err.message || "Payment failed");
    }

    const payment = await payRes.json();

    // if successful payment we will show the success message with details and option to view bookings
    const frag = cloneTemplate("tpl-success");

    frag.querySelector("[data-slot='event-name']").textContent =
      eventData?.eventName || "the event";

    frag.querySelector("[data-slot='txn-id']").textContent =
      `Txn ID: ${payment.transactionId}`;

    frag.querySelector("#viewBookingsBtn").addEventListener("click", () => {
      location.href = "../components/myBooking.html";
    });

    const overlayCard = document.getElementById("overlayCard");
    overlayCard.innerHTML = "";
    overlayCard.appendChild(frag);

    // Cleaning localStorage
    localStorage.removeItem("pendingEventId");
    localStorage.removeItem("pendingQty");
  } catch (e) {
    overlay.classList.remove("show");
    toast(e.message || "Payment failed", "error");
    payBtn.disabled = false;
  }
}

loadPage();
