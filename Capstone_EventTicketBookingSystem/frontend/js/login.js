console.log("login page script loaded successfully");

function toast(msg, type = "success") {
  const t = document.getElementById("toast");
  t.innerHTML = msg;
  t.className = "toast " + type + " show";
  setTimeout(() => t.classList.remove("show"), 3000);
}

const loginForm = document.getElementById("loginForm");

if (!loginForm) {
  console.log("loginForm not found");
} else {
  loginForm.addEventListener("submit", async function (e) {
    e.preventDefault();
    
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");

    // Client-side validations with toasts
    if (!email) {
      toast("Please enter your email address", "error");
      emailInput.focus();
      return;
    }
    
    if (!password) {
      toast("Please enter your password", "error");
      passwordInput.focus();
      return;
    }

    if (!emailInput.checkValidity()) {
      toast(emailInput.title || "Please enter a valid Gmail address", "error");
      return;
    }

    if (!passwordInput.checkValidity()) {
      toast(passwordInput.title || "Invalid password format", "error");
      return;
    }

    // packing up user credentials to send to backend
    const userData = { email, password };

    try {
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(userData),
      });

      if (response.ok) {
        const result = await response.json();
        console.log("FULL RESPONSE ", result);
        
        // saving tokens so user stays logged in
        localStorage.setItem("token", result.token);
        localStorage.setItem("role", result.role);

        let role = "";

        if (result.role) {
          role = result.role.toString().toUpperCase().trim();
        } else if (result.roles && result.roles.length > 0) {
          role = result.roles[0].toString().toUpperCase().trim();
        }

        toast("Welcome back! Login successful ✅", "success");

        if (role.includes("ORGANIZER")) {
          console.log("Redirecting to dashboard");
          setTimeout(() => {
            window.location.href = "dashboard.html";
          }, 1500);
        } else {
          console.log("Redirecting to index");
          setTimeout(() => {
            window.location.href = "index.html";
          }, 1500);
        }
      } else {
        const errorMessage = await handleBackendError(response);
        console.log("login failed:", errorMessage);
        toast(errorMessage, "error");
      }
    } catch (error) {
      console.error("something went wrong while logging in:", error);
      toast(
        '<i class="fa-solid fa-triangle-exclamation"></i> ' + getFriendlyMessage(error.message || "Server error. Try again."),
        "error",
      );
    }
  });
}
