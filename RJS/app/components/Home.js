import React, { useState, useEffect, useContext } from "react";
import { useNavigate } from "react-router-dom";
import LoginContext from "../../LoginContext";
import Cookies from "js-cookie";

function Home() {
  let { user, setUser, checkTokenLogin, setIsAdmin, checkGroup } = useContext(LoginContext);
  let [showComponent, setShowComponent] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    async function check() {
      const isLoggedIn = await checkTokenLogin();
      if (!isLoggedIn) {
        navigate("/login");
        setUser("");
      } else {
        const isAdmin = await checkGroup("admin");
        setIsAdmin(isAdmin);
        setShowComponent(true);
      }
    }
    check();
  }, []);

  return showComponent && <div className="container">Welcome {user}!</div>;
}

export default Home;
