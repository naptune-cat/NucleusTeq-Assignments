const BASE_URL = "http://localhost:8080/api/events";
const eventId = localStorage.getItem("editEventId");
// sending user back to dashboard if they didn't click edit from there
if (!eventId) {
  console.log("no event id found to edit, going back to dashboard");
  window.location.href = "dashboard.html";
}
function toast(msg, type = "success") {
  const t = document.getElementById("toast");
  t.innerHTML = msg;
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

// to load existing event data so we can prefill the form
async function loadEvent() {
  console.log("fetching event details to edit for id:", eventId);
  try {
    const res = await fetch(`${BASE_URL}/${eventId}`, {
      headers: getHeaders(),
    });

    if (!res.ok) {
      const errMsg = await handleBackendError(res);
      toast(errMsg, "error");
      setTimeout(() => location.href = "dashboard.html", 2000);
      return;
    }

    const e = await res.json();
    console.log("loaded event data to edit:", e.eventName);

    // filling up the inputs with what we got from the server
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
    toast(getFriendlyMessage(err.message) || "Failed to load event details. Please try again.", "error");
  }
}

// Submit — update API call
document.getElementById("editForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  console.log("organizer submitted the edit form");

  const name = document.getElementById("name").value.trim();
  const description = document.getElementById("description").value.trim();
  const venue = document.getElementById("venue").value.trim();
  const dateVal = document.getElementById("date").value;
  const seatsVal = document.getElementById("seats").value;
  const priceVal = document.getElementById("price").value;

  // Frontend validations with toasts
  if (!name || !description || !venue || !dateVal || !seatsVal || !priceVal) {
    toast("Please fill in all event details", "error");
    return;
  }

  const selectedDate = new Date(dateVal);
  const now = new Date();
  if (selectedDate < now) {
    toast("Event date cannot be in the past.", "error");
    return;
  }

  const seats = parseInt(seatsVal);
  if (isNaN(seats) || seats < 1) {
    toast("Total seats must be at least 1", "error");
    return;
  }

  const price = parseFloat(priceVal);
  if (isNaN(price) || price < 0) {
    toast("Ticket price cannot be negative", "error");
    return;
  }

  // packaging the updated data
  const data = {
    eventName: name,
    description: description,
    venue: venue,
    eventDateTime: dateVal,
    totalSeats: seats,
    ticketPrice: price,
    category: document.getElementById("category").value,
  };

  try {
    const res = await fetch(`${BASE_URL}/${eventId}`, {
      method: "PUT",
      headers: getHeaders(true),
      body: JSON.stringify(data),
    });

    if (res.ok) {
      console.log("event updated successfully!");
      toast("Event updated successfully! ✨", "success");
      setTimeout(() => {
        // cleaning up
        localStorage.removeItem("editEventId");
        window.location.href = "dashboard.html";
      }, 1500);
    } else {
      console.log("failed to update event");
      const errMsg = await handleBackendError(res);
      toast(errMsg, "error");
    }
  } catch (err) {
    console.error(err);
    toast(getFriendlyMessage(err.message) || "Failed to update event. Please try again.", "error");
  }
});

loadEvent();
