import React, { useState, useEffect } from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";

// My Components
import Header from "./components/Header";
import Footer from "./components/Footer";
import Home from "./components/Home";
import LoginPage from "./components/LoginPage";
import CreateUser from "./components/CreateUser";
import ManageUsers from "./components/ManageUsers";
import CreateGroup from "./components/CreateGroup";
import LoginContext from "../LoginContext";
import UnathourisedPage from "./components/UnauthorisedPage";
import axios from "axios";
import EditUser from "./components/EditUser";
import EditOwnProfile from "./components/EditOwnProfile";
import Applications from "./components/Applications";
import Application from "./components/Application";
import CreatePlan from "./components/CreatePlan";

axios.defaults.baseURL = "http://localhost:5000";
axios.defaults.withCredentials = true;

function Main() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const [user, setUser] = useState("");

  const checkTokenLogin = async function () {
    const loginResponse = await axios.post(
      "/admin/checkTokenLogin",
      {},
      {
        withCredentials: true,
      }
    );
    //set login true/false
    setIsLoggedIn(loginResponse.data.isLoggedIn);
    //clear token if false
    !loginResponse.data.isLoggedIn && localStorage.clear();
    //set user if true
    loginResponse.data.username && setUser(loginResponse.data.username);
    //return login value true/false
    return loginResponse.data.isLoggedIn;
  };

  const checkGroup = async function (group) {
    const groupResponse = await axios.post(
      "/admin/checkGroup",
      {
        group: group,
      },
      {
        withCredentials: true,
      }
    );
    //return isGroup value true/false
    return groupResponse.data.isGroup;
  };

  useEffect(() => {
    async function check() {
      //login using token
      await checkTokenLogin();
      //checkAdmin using token
      const isAdmin = await checkGroup("admin");
      //set isGroup (admin) value true/false
      setIsAdmin(isAdmin);
    }
    check();
  }, []);

  return (
    <LoginContext.Provider
      value={{
        setIsAdmin,
        setIsLoggedIn,
        setUser,
        isAdmin,
        isLoggedIn,
        user,
        checkGroup,
        checkTokenLogin,
      }}
    >
      <BrowserRouter>
        <Header />
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/home" element={<Home />} />
          <Route path="/admin/manageUsers" element={<ManageUsers />} />
          <Route path="/admin/createUser" element={<CreateUser />} />
          <Route path="/admin/editUser/:username" element={<EditUser />} />
          <Route
            exact
            path="/editOwnProfile/:username"
            element={<EditOwnProfile />}
          />
          <Route path="/unathourised" element={<UnathourisedPage />} />
          <Route path="/admin/createGroup" element={<CreateGroup />} />
          <Route exact path="/applications" element={<Applications />} />
          <Route
            exact
            path="/applications/:application"
            element={<Application />}
          />
          <Route
            exact
            path="/applications/plans/:application"
            element={<CreatePlan />}
          />
        </Routes>
        <Footer />
      </BrowserRouter>
    </LoginContext.Provider>
  );
}

const root = ReactDOM.createRoot(document.querySelector("#app"));
root.render(<Main />);

if (module.hot) {
  module.hot.accept();
}
