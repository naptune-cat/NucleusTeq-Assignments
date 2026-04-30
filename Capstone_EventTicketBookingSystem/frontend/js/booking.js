const BASE = "http://localhost:8080/api";
const token = localStorage.getItem("token");
const eventId = localStorage.getItem("pendingEventId");
const qty = parseInt(localStorage.getItem("pendingQty") || "1");
let selectedMethod = "UPI";
let eventData = null;

if (!token || !eventId) {
  console.log("missing token or event id, sending back to home page");
  location.href = "index.html";
}

function hdrs(json = false) {
  const h = { Authorization: "Bearer " + token };
  if (json) h["Content-Type"] = "application/json";
  return h;
}

function toast(msg, type = "error") {
  const t = document.getElementById("toast");
  t.textContent = msg;
  t.className = "toast " + type + " show";
  setTimeout(() => t.classList.remove("show"), 3500);
}

function selectMethod(method, el) {
  selectedMethod = method;
  document
    .querySelectorAll(".method-btn")
    .forEach((b) => b.classList.remove("selected"));
  el.classList.add("selected");
  document.getElementById("upiFields").className =
    "extra-fields" + (method === "UPI" ? " show" : "");
  document.getElementById("cardFields").className =
    "extra-fields" +
    (method === "CREDIT_CARD" || method === "DEBIT_CARD" ? " show" : "");
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

async function loadPage() {
  console.log("loading payment page for event:", eventId);
  try {
    const res = await fetch(`${BASE}/events/${eventId}`);
    eventData = await res.json();
    console.log("loaded event details for payment:", eventData.eventName);
    
    const dt = new Date(eventData.eventDateTime);
    const total = (eventData.ticketPrice || 0) * qty;

    document.getElementById("pageContent").innerHTML = `
        <div class="pay-card">
          <div class="pay-title">Complete Payment</div>
          <div class="pay-sub">Choose your preferred payment method</div>
          <div class="method-label">Payment Method</div>
          <div class="methods-grid">
            <button class="method-btn selected" onclick="selectMethod('UPI',this)">📱 UPI</button>
            <button class="method-btn" onclick="selectMethod('NET_BANKING',this)">🏦 Net Banking</button>
            <button class="method-btn" onclick="selectMethod('CREDIT_CARD',this)">💳 Credit Card</button>
            <button class="method-btn" onclick="selectMethod('DEBIT_CARD',this)">🏧 Debit Card</button>
          </div>
          <div class="extra-fields show" id="upiFields">
            <div class="field"><label>UPI ID</label><input type="text" placeholder="yourname@upi"/></div>
          </div>
          <div class="extra-fields" id="cardFields">
            <div class="field"><label>Card Number</label><input type="text" placeholder="1234 5678 9012 3456" maxlength="19"/></div>
            <div class="field-row">
              <div class="field"><label>Expiry</label><input type="text" placeholder="MM/YY" maxlength="5"/></div>
              <div class="field"><label>CVV</label><input type="text" placeholder="•••" maxlength="3"/></div>
            </div>
          </div>
          <div class="divider"></div>
          <p style="font-size:13px;color:var(--muted);line-height:1.7;">This is a <strong style="color:var(--text);">mock payment</strong> — no real transaction will occur. Clicking Pay will simulate a successful payment confirmation.</p>
        </div>
        <div class="summary-card">
          <div class="summary-title">Order Summary</div>
          <div class="summary-event">
            <div class="summary-event-emoji">${catEmoji(eventData.category)}</div>
            <div class="summary-event-name">${eventData.eventName}</div>
            <div class="summary-event-meta">
              <i class="fa-regular fa-calendar"></i> ${dt.toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric" })}<br/>
              <i class="fa-regular fa-clock"></i> ${dt.toLocaleTimeString("en-IN", { hour: "2-digit", minute: "2-digit" })}<br/>
              <i class="fa-solid fa-location-dot"></i> ${eventData.venue}
            </div>
          </div>
          <div class="summary-row"><span>Ticket Price</span><span>₹${(eventData.ticketPrice || 0).toLocaleString("en-IN")}</span></div>
          <div class="summary-row"><span>Quantity</span><span>${qty} ticket${qty > 1 ? "s" : ""}</span></div>
          <div class="summary-row"><span>Convenience Fee</span><span style="color:var(--success);">FREE</span></div>
          <div class="summary-divider"></div>
          <div class="summary-total">
            <span style="font-size:14px;color:var(--muted);">Total</span>
            <span class="summary-total-val">₹${total.toLocaleString("en-IN")}</span>
          </div>
          <button class="pay-btn" id="payBtn" onclick="processPayment()">🔒 Pay ₹${total.toLocaleString("en-IN")}</button>
          <div class="secure-note">🔒 256-bit encrypted · Powered by Evently</div>
        </div>`;
  } catch (e) {
    console.error("could not load event details:", e);
    toast("Failed to load event");
  }
}

async function processPayment() {
  console.log("user clicked pay button, starting payment process...");
  document.getElementById("payBtn").disabled = true;
  document.getElementById("overlay").classList.add("show");
  try {
    // Create PENDING booking
    console.log("creating a pending booking first...");
    const bookRes = await fetch(`${BASE}/bookings`, {
      method: "POST",
      headers: hdrs(true),
      body: JSON.stringify({
        eventId: parseInt(eventId),
        numberOfTickets: qty,
      }),
    });
    if (!bookRes.ok) {
      console.log("failed to create pending booking");
      const err = await bookRes.json();
      document.getElementById("overlay").classList.remove("show");
      toast(err.message || "Booking failed");
      document.getElementById("payBtn").disabled = false;
      return;
    }
    const booking = await bookRes.json();
    console.log("pending booking created successfully:", booking.bookingId);

    // Simulate processing delay
    console.log("simulating payment delay...");
    await new Promise((r) => setTimeout(r, 2200));

    // Confirm payment
    console.log("confirming the payment now...");
    const payRes = await fetch(`${BASE}/bookings/payment`, {
      method: "POST",
      headers: hdrs(true),
      body: JSON.stringify({
        bookingId: booking.bookingId,
        paymentMethod: selectedMethod,
      }),
    });
    if (!payRes.ok) {
      console.log("payment confirmation failed");
      const err = await payRes.json();
      document.getElementById("overlay").classList.remove("show");
      toast(err.message || "Payment failed");
      document.getElementById("payBtn").disabled = false;
      return;
    }
    const payment = await payRes.json();
    console.log("payment successful! transaction id:", payment.transactionId);

    // Step 4 — Show success
    document.getElementById("overlayCard").innerHTML = `
        <div class="success-ring">✓</div>
        <div class="success-title">Payment Successful!</div>
        <div class="success-sub">Your booking is confirmed. Enjoy <strong>${eventData?.eventName || "the event"}</strong>!</div>
        <div class="txn-id">Txn ID: ${payment.transactionId}</div>
        <button class="goto-btn" onclick="location.href='bookings.html'">View My Bookings →</button>`;

    // clearing local storage so they don't accidentally book again
    localStorage.removeItem("pendingEventId");
    localStorage.removeItem("pendingQty");
  } catch (e) {
    console.error("server error during payment:", e);
    document.getElementById("overlay").classList.remove("show");
    toast("Server error. Please try again.");
    document.getElementById("payBtn").disabled = false;
  }
}

loadPage();
