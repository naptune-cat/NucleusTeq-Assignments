const BASE_URL = "http://localhost:8080/api/events";
const eventId = localStorage.getItem("editEventId");
if (!eventId) {
  window.location.href = "dashboard.html";
}
function toast(msg, type = "success") {
  const t = document.getElementById("toast");
  t.textContent = msg;
  t.className = "toast " + type + " show";
  setTimeout(() => t.classList.remove("show"), 3000);
}

function getHeaders(isJson = false) {
  const headers = {
    Authorization: "Bearer " + localStorage.getItem("token"),
  };
  if (isJson) headers["Content-Type"] = "application/json";
  return headers;
}

// Redirect if event id is not therein localStorage (means user came to this page without clicking edit from dashboard)
if (!eventId) {
  window.location.href = "dashboard.html";
}

// to load xisting event data
async function loadEvent() {
  try {
    const res = await fetch(`${BASE_URL}/${eventId}`, {
      headers: getHeaders(),
    });

    if (!res.ok) {
      toast("Failed to load event", "error");
      return;
    }

    const e = await res.json();

    document.getElementById("name").value = e.eventName;
    document.getElementById("venue").value = e.venue;
    document.getElementById("seats").value = e.totalSeats;
    document.getElementById("price").value = e.ticketPrice;
    document.getElementById("description").value = e.description;
    document.getElementById("category").value = e.category || "";

    const dt = new Date(e.eventDateTime);
    const formatted = dt.toISOString().slice(0, 16);
    document.getElementById("date").value = formatted;
  } catch (err) {
    console.error(err);
    toast("Something went wrong", "error");
  }
}

// Submit — update API call
document.getElementById("editForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const data = {
    eventName: document.getElementById("name").value,
    description: document.getElementById("description").value,
    venue: document.getElementById("venue").value,
    eventDateTime: document.getElementById("date").value,
    totalSeats: parseInt(document.getElementById("seats").value),
    ticketPrice: parseFloat(document.getElementById("price").value),
    category: document.getElementById("category").value,
  };

  try {
    const res = await fetch(`${BASE_URL}/${eventId}`, {
      method: "PUT",
      headers: getHeaders(true),
      body: JSON.stringify(data),
    });

    if (res.ok) {
      toast("Event updated successfully!", "success");
      setTimeout(() => {
        localStorage.removeItem("editEventId");
        window.location.href = "dashboard.html";
      }, 1500);
    } else {
      const err = await res.json();
      toast("❌ " + (err.error || "Update failed"), "error");
    }
  } catch (err) {
    console.error(err);
    toast("Something went wrong", "error");
  }
});

loadEvent();
