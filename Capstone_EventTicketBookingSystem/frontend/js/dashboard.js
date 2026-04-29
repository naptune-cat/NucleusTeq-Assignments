document.addEventListener("DOMContentLoaded", () => {
  const BASE_URL = "http://localhost:8080/api/events";

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

  // Switch Sections
  function showSection(id) {
    document.querySelectorAll(".section").forEach((sec) => {
      sec.classList.add("hidden");
    });
    document.getElementById(id).classList.remove("hidden");
  }
  window.showSection = showSection;

  // loading Dashboard Stats
  async function loadStats() {
    try {
      const res = await fetch(`${BASE_URL}/organizer/stats`, {
        headers: getHeaders(),
      });
      const data = await res.json();

      document.getElementById("total").innerText = data.total || 0;
      document.getElementById("active").innerText = data.active || 0;
      document.getElementById("past").innerText = data.past || 0;
      document.getElementById("cancelled").innerText = data.cancelled || 0;
      document.getElementById("totalEarned").textContent =
        "₹" + (data.totalEarned || 0).toLocaleString("en-IN");
    } catch (e) {
      console.error(e);
    }
  }

  // Loading Events
  async function loadEvents() {
    try {
      const res = await fetch(`${BASE_URL}/organizer`, {
        headers: getHeaders(),
      });
      const events = await res.json();
      const container = document.getElementById("eventList");

      container.innerHTML = "";

      events.forEach((e) => {
        const div = document.createElement("div");
        div.className = "event-card";
        div.innerHTML = `
          <h3>${e.eventName}</h3>
          <p>${e.venue}</p>
          <p>${new Date(e.eventDateTime).toLocaleString()}</p>
          <p>Status: ${e.status}</p>
          <button class="cancel-btn" data-id="${e.id}">Cancel</button>
          <button class="export-btn" data-id="${e.id}">Export Attendees</button>
          <button class="edit-btn" data-id="${e.id}"
          ${e.status === "CANCELLED" ? "disabled style='opacity:0.4;cursor:not-allowed;'" : ""}>
          Edit Event
          </button>
        `;
        div
          .querySelector(".cancel-btn")
          .addEventListener("click", () => cancelEvent(e.id, e.status));

        div
          .querySelector(".export-btn")
          .addEventListener("click", () => exportAttendees(e.id));

        div
          .querySelector(".edit-btn")
          .addEventListener("click", () => editEvent(e.id, e.eventDateTime));

        container.appendChild(div);
      });
    } catch (e) {
      console.error(e);
    }
  }

  // Export Attendees
  async function exportAttendees(eventId) {
    try {
      const res = await fetch(
        `http://localhost:8080/api/export/event/${eventId}/attendees`,
        {
          headers: getHeaders(),
        },
      );

      if (!res.ok) {
        toast("Failed to export attendees", "error");
        return;
      }

      const blob = await res.blob();
      const url = window.URL.createObjectURL(blob);

      const a = document.createElement("a");
      a.href = url;
      a.download = `attendees_event_${eventId}.csv`;
      document.body.appendChild(a);
      a.click();
      a.remove();

      window.URL.revokeObjectURL(url);

      toast("CSV downloaded successfully <3", "success");
    } catch (e) {
      console.error(e);
      toast("Something went wrong", "error");
    }
  }
  // Cancel Event button

  async function cancelEvent(id, status) {
    if (status === "CANCELLED") {
      alert("Event is already cancelled ", "error");
      return;
    }

    try {
      await fetch(`${BASE_URL}/${id}/cancel`, {
        method: "PATCH",
        headers: getHeaders(),
      });

      toast("Event cancelled", "success");
      loadEvents();
      loadStats();
    } catch (e) {
      console.error(e);
    }
  }
  window.cancelEvent = cancelEvent;

  // Edit Event button
  function editEvent(id, eventDateTime) {
    localStorage.setItem("editEventId", id);
    const now = new Date();
    const difference = (new Date(eventDateTime) - now) / (1000 * 60 * 60); // hours difference
    if (difference <= 4) {
      alert("Event can only be edited up to 4 hours before start time.");
      return;
    }
    if (difference <= 0) {
      alert("Event has already started. Cannot edit.");
      return;
    }
    localStorage.setItem("editEventId", id);
    window.location.href = "edit-event.html";
  }

  // Create Event button
  const form = document.getElementById("eventForm");
  if (form) {
    form.addEventListener("submit", async (e) => {
      e.preventDefault();

      // checking for valid date as it cannot be in the past
      const selectedDate = new Date(document.getElementById("date").value);
      const now = new Date();
      if (selectedDate < now) {
        alert("Event cannot be created in the past ");
        return;
      }

      // seats must be at least 1
      const seats = parseInt(document.getElementById("seats").value);
      if (isNaN(seats) || seats < 1) {
        toast("Seats must be at least 1", "error");
        return;
      }

      // price must be positive
      const price = parseFloat(document.getElementById("price").value);
      if (isNaN(price) || price < 0) {
        toast("Price cannot be negative", "error");
        return;
      }
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
        await fetch(BASE_URL, {
          method: "POST",
          headers: getHeaders(true),
          body: JSON.stringify(data),
        });

        toast("Event created!", "success");
        form.reset();
        showSection("events");
        loadEvents();
        loadStats();
      } catch (e) {
        console.error(e);
      }
    });
  }

  // logout page
  function logout() {
    localStorage.removeItem("token");
    window.location.href = "login.html";
  }
  window.logout = logout;

  loadStats();
  loadEvents();
});
