console.log("JS loaded");
const loginForm = document.getElementById("loginForm");

if (!loginForm) {
  console.log(" loginForm not found");
} else {
  loginForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    alert("Form submitted ✅");

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const userData = { email, password };

    try {
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(userData),
      });

      const result = await response.json();

      console.log("FULL RESPONSE  " + JSON.stringify(result));

      if (response.ok) {
        // Save token & role
        localStorage.setItem("token", result.token);
        localStorage.setItem("role", result.role);

        // Normalize role safely
        let role = "";

        if (result.role) {
          role = result.role.toString().toUpperCase().trim();
        } else if (result.roles && result.roles.length > 0) {
          role = result.roles[0].toString().toUpperCase().trim();
        }

        //  Role-based redirect
        if (role.includes("ORGANIZER")) {
          console.log("Redirecting to dashboard ");
          window.location.href = "dashboard.html";
        } else {
          console.log("Redirecting to index");
          window.location.href = "index.html";
        }
      } else {
        alert("❌ " + (result.message || "Login failed"));
      }
    } catch (error) {
      alert("⚠️ Server error. Try again.");
    }
  });
}
