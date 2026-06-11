const BASE_URL = "http://localhost:8080/api/auth";

const errorMessageMap = {
  // Auth Errors
  "Invalid password":
    "Oops! That password doesn't look right. Please try again.",
  "User not found":
    "We couldn't find an account with that email. Maybe sign up?",
  "Email already exists":
    "This email is already registered. Try logging in instead!",
  "Unauthorized": "Your session has expired. Please log in again to continue.",
  "Invalid email format": "Please enter a valid email address.",
  "Bad credentials": "Invalid email or password. Please check and try again.",

  // Event Errors
  "Event not found": "Sorry, we couldn't find that event.",
  "Event already cancelled": "This event has already been cancelled.",
  "Event cannot be created in the past":
    "You can't host an event in the past! Please pick a future date.",
  "Event can only be edited up to 4 hours before start time.":
    "Too late! Events can only be edited at least 4 hours before they start.",

  // Booking Errors
  "Not enough tickets":
    "Sorry, there aren't enough tickets left for this event.",
  "Booking failed": "Something went wrong while booking. Please try again.",
  "Payment failed":
    "Payment didn't go through. Please check your details and try again.",
  "Invalid booking": "There was an issue with your booking request.",

  // Generic
  "Internal Server Error":
    "Something went wrong on our end. We're working on it!",
  "Server error. Try again.":
    "Our servers are acting up. Please try again in a moment.",
};

function getFriendlyMessage(msg) {
  if (!msg) return "An unexpected error occurred. Please try again.";

  // Checking for exact matches
  if (errorMessageMap[msg]) {
    return errorMessageMap[msg];
  }

  // Checking for partial matches or common backend strings
  const lowerMsg = msg.toLowerCase();
  if (
    lowerMsg.includes("invalid password") ||
    lowerMsg.includes("bad credentials")
  ) {
    return errorMessageMap["Invalid password"];
  }
  if (lowerMsg.includes("user not found")) {
    return errorMessageMap["User not found"];
  }
  if (lowerMsg.includes("email already exists")) {
    return errorMessageMap["Email already exists"];
  }
  if (lowerMsg.includes("not enough") || lowerMsg.includes("insufficient")) {
    return errorMessageMap["Not enough tickets"];
  }

  return msg; // Returning original if no better version found
}

// for handling session expiration and other auth related errors globally across the app
function checkSession() {
  const token = localStorage.getItem("token");

  if (!token) return true; // no token → already logged out

  try {
    const payload = JSON.parse(atob(token.split(".")[1]));

    const currentTime = Math.floor(Date.now() / 1000);

    if (payload.exp <= currentTime) {
      localStorage.clear();

      toast("Your session has expired. Please login again.", "error");

      setTimeout(() => {
        location.href = "login.html";
      }, 1800);

      return false;
    }

    return true;
  } catch (err) {
    localStorage.clear();
    return false;
  }
}

async function handleBackendError(response) {
  try {
    const contentType = response.headers.get("content-type");
    if (contentType && contentType.indexOf("application/json") !== -1) {
      const data = await response.json();
      return getFriendlyMessage(data.message || data.error || "Action failed");
    } else {
      const text = await response.text();
      return getFriendlyMessage(text || "Action failed");
    }
  } catch (e) {
    return "Something went wrong. Please try again later.";
  }
}

// function for registering user by sending data to backend
async function registerUser(userData) {
  console.log("sending registration request to backend for:", userData.email);
  const response = await fetch(`${BASE_URL}/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(userData),
  });

  return response;
}
// function for logging in user by sending data to backend
async function loginUser(userData) {
  console.log("sending login request to backend for:", userData.email);
  const response = await fetch(`${BASE_URL}/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(userData),
  });

  return response;
}
// this makes the methods as globally accessible so that they can be used in other js files
window.getFriendlyMessage = getFriendlyMessage;
window.handleBackendError = handleBackendError;
window.checkSession = checkSession;