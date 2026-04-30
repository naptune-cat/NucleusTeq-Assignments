const BASE_URL = "http://localhost:8080/api/auth";

async function registerUser(userData) {
  console.log("sending registration request to backend for:", userData.email);
  // calling our backend api to register
  const response = await fetch(`${BASE_URL}/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(userData),
  });

  return response.json();
}

async function loginUser(userData) {
  console.log("sending login request to backend for:", userData.email);
  // calling our backend api to login
  const response = await fetch(`${BASE_URL}/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(userData),
  });

  return response.json();
}
