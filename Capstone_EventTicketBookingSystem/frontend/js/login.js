const loginForm = document.getElementById("loginForm");

loginForm.addEventListener("submit", async function (e) {
  e.preventDefault();

  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  const userData = {
    email,
    password,
  };

  try {
    const response = await fetch("http://localhost:8080/api/auth/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    });
    const result = await response.json();

    if (response.ok) {
      localStorage.setItem("token", result.token);
      localStorage.setItem("userEmail", result.email);
      alert("✔️ Login successful");
      window.location.href = "index.html";
    } else {
      alert("❌ " + result.message);
    }
  } catch (error) {
    alert("Invalid email or password");
  }
});
