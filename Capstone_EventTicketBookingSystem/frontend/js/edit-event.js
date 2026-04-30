const BASE_URL = "http://localhost:8080/api/events";
const eventId = localStorage.getItem("editEventId");
// sending user back to dashboard if they didn't click edit from there
if (!eventId) {
  console.log("no event id found to edit, going back to dashboard");
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

// to load existing event data so we can prefill the form
async function loadEvent() {
  console.log("fetching event details to edit for id:", eventId);
  try {
    const res = await fetch(`${BASE_URL}/${eventId}`, {
      headers: getHeaders(),
    });

    if (!res.ok) {
      toast("Failed to load event", "error");
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
    toast("Something went wrong", "error");
  }
}

// Submit — update API call
document.getElementById("editForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  console.log("organizer submitted the edit form");

  // packaging the updated data
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
      console.log("event updated successfully!");
      toast("Event updated successfully!", "success");
      setTimeout(() => {
        // cleaning up
        localStorage.removeItem("editEventId");
        window.location.href = "dashboard.html";
      }, 1500);
    } else {
      console.log("failed to update event");
      const err = await res.json();
      toast("❌ " + (err.error || "Update failed"), "error");
    }
  } catch (err) {
    console.error(err);
    toast("Something went wrong", "error");
  }
});

loadEvent();
