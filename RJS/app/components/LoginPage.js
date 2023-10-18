import axios from "axios";
import React, { useState, useEffect, useContext } from "react";
import { useNavigate } from "react-router-dom";
import LoginContext from "../../LoginContext";
import Cookies from "js-cookie";

function LoginPage({}) {
  let [username, setUsername] = useState("");
  let [password, setPassword] = useState("");
  let [showComponent, setShowComponent] = useState(false);
  let [isWrongPassword, setIsWrongPassword] = useState(false);

  const navigate = useNavigate();

  const { setIsAdmin, setIsLoggedIn, setUser, checkTokenLogin } =
    useContext(LoginContext);

  async function checkLogin(e) {
    e.preventDefault();
    try {
      //check login with password
      const loginResponse = await axios.post(
        "/login",
        {
          username,
          password,
        },
        {
          withCredentials: true,
        }
      );

      if (loginResponse.data.isLoggedIn) {
        //active user
        //check admin
        const adminResponse = await axios.post(
          "/admin/checkGroup",
          {
            group_name: "admin",
          },
          {
            withCredentials: true,
          }
        );
        setUser(username);
        setIsAdmin(adminResponse.data.isGroup);
        setIsLoggedIn(loginResponse.data.isLoggedIn);

        //clear form
        setPassword("");
        setUsername("");

        //navigate to home
        navigate("/home");
      } else {
        //not active user
        if (!loginResponse.data.isLoggedIn) {
          setIsWrongPassword(true);
          Cookies.remove("jwt");
        }
      }
    } catch (e) {
      console.log(e);
    }
  }

  useEffect(() => {
    async function check() {
      const isLoggedIn = await checkTokenLogin();
      if (isLoggedIn) {
        navigate("/home");
      } else {
        localStorage.clear();
        Cookies.remove("jwt");
        setIsLoggedIn(isLoggedIn);
        setIsAdmin(false);
        setShowComponent(true);
      }
    }
    check();
  }, []);

  return showComponent ? (
    <div className="container">
      <form className="big" onSubmit={checkLogin}>
        <>
          <input
            onChange={(e) => setUsername(e.target.value)}
            value={username}
            type="text"
            placeholder="Username"
            name="username"
            required
          />{" "}
          <input
            onChange={(e) => setPassword(e.target.value)}
            value={password}
            type="password"
            placeholder="Password"
            name="psw"
            required
          />{" "}
          {isWrongPassword ? (
            <div style={{ color: "red" }}>Incorrect Username or Password</div>
          ) : (
            <div></div>
          )}
          <button className="button-login" type="submit">
            Authenticate
          </button>
        </>
      </form>
    </div>
  ) : (
    <></>
  );
}

export default LoginPage;
