import React, { useContext, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import LoginContext from "../../LoginContext";
import Cookies from "js-cookie";

function Header() {
  const {
    setIsAdmin,
    setIsLoggedIn,
    isLoggedIn,
    isAdmin,
    checkTokenLogin,
    checkGroup,
    setUser,
    user,
  } = useContext(LoginContext);

  const navigate = useNavigate();

  async function handleUsersManagement() {
    const isLoggedIn = await checkTokenLogin();
    if (isLoggedIn) {
      const isAdmin = await checkGroup("admin");
      setIsAdmin(isAdmin);
      isAdmin ? navigate("/admin/manageUsers") : navigate("/unathourised");
    } else {
      navigate("/login");
    }
  }

  async function handleEditOwnProfile(e) {
    const isLoggedIn = await checkTokenLogin();
    isLoggedIn
      ? navigate(`/editOwnProfile/${e.target.name}`)
      : navigate("/login");
  }

  async function handleApplications(e) {
    const isLoggedIn = await checkTokenLogin();
    isLoggedIn ? navigate(`/applications`) : navigate("/login");
  }

  return (
    <header>
      <div className="topnav">
        <Link to="login">
          <div className="tms">Task Management System</div>
        </Link>

        {isLoggedIn && (
          <>
            <button
              onClick={() => {
                localStorage.clear();
                Cookies.remove("jwt");
                setIsAdmin(false);
                setIsLoggedIn(false);
                setUser("");
                navigate("/login");
              }}
              className="button-logout"
            >
              Logout
            </button>
            <button
              name={user}
              onClick={handleEditOwnProfile}
              className="users-management-button"
            >
              {user + "'s Profile"}
            </button>
            <button
              className="users-management-button"
              onClick={handleApplications}
            >
              Applications
            </button>
          </>
        )}

        {isLoggedIn && isAdmin ? (
          <button
            className="users-management-button"
            onClick={handleUsersManagement}
          >
            User Management
          </button>
        ) : (
          <></>
        )}
      </div>
    </header>
  );
}

export default Header;
