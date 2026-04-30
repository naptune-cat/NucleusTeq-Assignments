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

function toast(msg, type = "success") {
  const t = document.getElementById("toast");
  t.textContent = msg;
  t.className = "toast " + type + " show";
  setTimeout(() => t.classList.remove("show"), 3000);
}

const registerForm = document.getElementById("registerForm");
const phoneInput = document.getElementById("phone");
const phoneError = document.getElementById("phoneError");

const nameInput = document.getElementById("fullName");
const nameError = document.getElementById("nameError");

// checking if the user typed a valid name when they click away
nameInput.addEventListener("blur", function () {
  const pattern = new RegExp(this.pattern);
  if (this.value && !pattern.test(this.value)) {
    nameError.textContent = this.title;
    nameInput.style.border = "1px solid red";
  } else if (this.value) {
    nameError.textContent = "";
    nameInput.style.border = "1px solid green";
  }
});

phoneInput.addEventListener("input", function () {
  this.value = this.value.replace(/[^0-9]/g, "");
});

// making sure phone number is exactly 10 digits when they leave the field
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

// validating the email format
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

const passwordInput = document.getElementById("password");
const passwordError = document.getElementById("passwordError");

passwordInput.addEventListener("blur", function () {
  const pattern = new RegExp(this.pattern);
  if (this.value && !pattern.test(this.value)) {
    passwordError.textContent = this.title;
    passwordInput.style.border = "1px solid red";
  } else if (this.value) {
    passwordError.textContent = "";
    passwordInput.style.border = "1px solid green";
  }
});

const confirmPasswordInput = document.getElementById("confirmPassword");
const confirmPasswordError = document.getElementById("confirmPasswordError");

confirmPasswordInput.addEventListener("blur", function () {
  if (this.value && this.value !== passwordInput.value) {
    confirmPasswordError.textContent = "Passwords do not match";
    confirmPasswordInput.style.border = "1px solid red";
  } else if (this.value) {
    confirmPasswordError.textContent = "";
    confirmPasswordInput.style.border = "1px solid green";
  }
});

registerForm.addEventListener("submit", async function (e) {
  e.preventDefault();
  console.log("someone is trying to register");

  const fullName = document.getElementById("fullName").value.trim();
  const email = document.getElementById("email").value.trim();
  const phone = document.getElementById("phone").value.trim();
  const password = document.getElementById("password").value;
  const confirmPassword = document.getElementById("confirmPassword").value;
  const organizerCode = document.getElementById("organizerCode").value.trim();

  // double checking passwords match
  console.log("checking if passwords match...");
  if (password !== confirmPassword) {
    toast("Passwords do not match", "error");
    return;
  }

  const userData = {
    name: fullName,
    email: email,
    phone: phone,
    password: password,
    organizerCode: organizerCode,
  };

  console.log("sending registration data to the backend for:", email);

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
      console.log("registration successful!", message);
      toast(message, "success");

      registerForm.reset();
      console.log("sending user to login page now");
      setTimeout(() => {
        window.location.href = "./login.html";
      }, 1500);
    } else {
      toast(message, "error");
    }
  } catch (error) {
    console.error("something went wrong during registration:", error);
    toast("Something went wrong. Please try again.", "error");
  }
});
