const loginForm = document.getElementById("loginForm");

if (loginForm) {
  loginForm.addEventListener("submit", async function (e) {
    e.preventDefault();

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

      if (response.ok) {
        // saving role and token in localstorage
        localStorage.setItem("token", result.token);
        localStorage.setItem("role", result.role);

        // redirecting to diff pages absed on role
        if (result.role === "ORGANIZER") {
          window.location.href = "dashboard.html";
        } else {
          window.location.href = "home.html";
        }
      } else {
        alert("❌ " + (result.message || "Login failed"));
      }
    } catch (error) {
      console.error(error);
      alert("⚠️ Server error. Try again.");
    }
  });
}
