document.addEventListener("DOMContentLoaded", () => {
  console.log("dashboard script is ready");
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
    console.log("fetching organizer stats for the dashboard...");
    try {
      const res = await fetch(`${BASE_URL}/organizer/stats`, {
        headers: getHeaders(),
      });
      const data = await res.json();
      console.log("got dashboard stats:", data);

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
    console.log("grabbing all events created by this organizer...");
    try {
      const res = await fetch(`${BASE_URL}/organizer`, {
        headers: getHeaders(),
      });
      const events = await res.json();
      console.log("found this many events:", events.length);
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
    console.log("trying to download attendees list for event:", eventId);
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
      toast(e.message || "Failed to export attendees. Please try again.", "error");
    }
  }
  // Cancel Event button

  async function cancelEvent(id, status) {
    console.log("organizer requested to cancel event:", id);
    if (status === "CANCELLED") {
      console.log("event is already cancelled, ignoring click");
      toast("Event is already cancelled", "error");
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
      toast(e.message || "Failed to cancel event", "error");
    }
  }
  window.cancelEvent = cancelEvent;

  // Edit Event button
  function editEvent(id, eventDateTime) {
    console.log("organizer wants to edit event:", id);
    localStorage.setItem("editEventId", id);
    const now = new Date();
    // calculating difference in hours
    const difference = (new Date(eventDateTime) - now) / (1000 * 60 * 60); 
    if (difference <= 4) {
      console.log("too close to start time to edit");
      toast("Event can only be edited up to 4 hours before start time.", "error");
      return;
    }
    if (difference <= 0) {
      toast("Event has already started. Cannot edit.", "error");
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
      console.log("creating a new event from the form data");

      // checking for valid date as it cannot be in the past
      const selectedDate = new Date(document.getElementById("date").value);
      const now = new Date();
      if (selectedDate < now) {
        console.log("selected date is in the past, rejecting");
        toast("Event cannot be created in the past", "error");
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

      console.log("sending new event data to backend...");

      try {
        await fetch(BASE_URL, {
          method: "POST",
          headers: getHeaders(true),
          body: JSON.stringify(data),
        });

        console.log("event successfully created!");
        toast("Event created!", "success");
        form.reset();
        showSection("events");
        loadEvents();
        loadStats();
      } catch (e) {
        console.error(e);
        toast(e.message || "Failed to create event", "error");
      }
    });
  }

  // logout page
  function logout() {
    console.log("organizer logging out...");
    localStorage.removeItem("token");
    window.location.href = "login.html";
  }
  window.logout = logout;

  loadStats();
  loadEvents();
});
