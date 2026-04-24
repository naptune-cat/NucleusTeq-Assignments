document.addEventListener("DOMContentLoaded", () => {
  const BASE_URL = "http://localhost:8080/api/events";
  
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
          <button data-id="${e.id}">Cancel</button>
        `;
        div
          .querySelector("button")
          .addEventListener("click", () => cancelEvent(e.id));
        container.appendChild(div);
      });
    } catch (e) {
      console.error(e);
    }
  }

  // Cancel Event button
  async function cancelEvent(id) {
    try {
      await fetch(`${BASE_URL}/${id}/cancel`, {
        method: "PATCH",
        headers: getHeaders(),
      });

      alert("Event cancelled");
      loadEvents();
      loadStats();
    } catch (e) {
      console.error(e);
    }
  }
  window.cancelEvent = cancelEvent;

  // Create Event button
  const form = document.getElementById("eventForm");
  if (form) {
    form.addEventListener("submit", async (e) => {
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
        await fetch(BASE_URL, {
          method: "POST",
          headers: getHeaders(true),
          body: JSON.stringify(data),
        });

        alert("Event created!");
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
