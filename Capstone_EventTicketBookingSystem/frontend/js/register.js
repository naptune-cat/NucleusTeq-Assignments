function togglePassword(id, btn) {
  const input = document.getElementById(id);
  const icon = btn.querySelector("i");
  if (input.type === "password") {
    input.type = "text";
    icon.classList.replace("fa-eye", "fa-eye-slash");
  } else {
    input.type = "password";
    icon.classList.replace("fa-eye-slash", "fa-eye");
  }
}

const registerForm = document.getElementById("registerForm");
const phoneInput = document.getElementById("phone");
const phoneError = document.getElementById("phoneError");

phoneInput.addEventListener("input", function () {
  this.value = this.value.replace(/[^0-9]/g, "");
});

// Blur pe length check
phoneInput.addEventListener("blur", function () {
  if (this.value.length !== 10) {
    phoneError.textContent = "Phone number must be exactly 10 digits";
    phoneInput.style.border = "1px solid red";
  } else {
    phoneError.textContent = "";
    phoneInput.style.border = "1px solid green";
  }
});

const emailInput = document.getElementById("email");
const emailError = document.getElementById("emailError");

emailInput.addEventListener("blur", function () {
  const gmailRegex = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
  if (!gmailRegex.test(this.value)) {
    emailError.textContent = "Please enter a valid Gmail address (@gmail.com)";
    emailInput.style.border = "1px solid red";
  } else {
    emailError.textContent = "";
    emailInput.style.border = "1px solid green";
  }
});

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

  const userData = {
    name: fullName,
    email: email,
    phone: phone,
    password: password,
    organizerCode: organizerCode,
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
      window.location.href = "./login.html";
    } else {
      alert("❌ " + message);
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Something went wrong. Please try again.");
  }
});
