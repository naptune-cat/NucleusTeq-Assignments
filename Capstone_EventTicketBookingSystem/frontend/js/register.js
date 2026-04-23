const registerForm = document.getElementById("registerForm");

registerForm.addEventListener("submit", async function (e) {
  e.preventDefault();

  const fullName = document.getElementById("fullName").value.trim();
  const email = document.getElementById("email").value.trim();
  const phone = document.getElementById("phone").value.trim();
  const password = document.getElementById("password").value;
  const confirmPassword = document.getElementById("confirmPassword").value;
  const organizerCode = document.getElementById("organizerCode").value.trim();

  console.log(password, confirmPassword);
  if (password !== confirmPassword) {
    alert("Passwords do not match");
    return;
  }

  let role = "CUSTOMER";

  if (organizerCode === "PartyPalooza9988") {
    role = "ORGANIZER";
  }

  const userData = {
    name: fullName,
    email: email,
    phone: phone,
    password: password,
    role: role,
  };

  try {
    const response = await fetch(`${BASE_URL}/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(userData),
    });

    const message = await response.text();

    if (response.ok) {
      alert("✔️ " + message);

      registerForm.reset();

      window.location.href = "../login.html";
    } else {
      alert("❌ " + message);
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Something went wrong. Please try again.");
  }
});
