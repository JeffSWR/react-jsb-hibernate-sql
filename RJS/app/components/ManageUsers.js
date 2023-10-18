import React, { useState, useEffect, useContext } from "react";
import { Link, useNavigate } from "react-router-dom";
import LoginContext from "../../LoginContext";
import axios from "axios";

function ManageUsers() {
  let [allUsers, setAllUsers] = useState([]);
  const { checkGroup, setIsAdmin, checkTokenLogin } = useContext(LoginContext);
  let [showComponent, setShowComponent] = useState(false);

  const navigate = useNavigate();

  async function getAllUsers() {
    const response = await axios.post(
      "/admin/getAllUsers",
      {},
      {
        withCredentials: true,
      }
    );
    setAllUsers(response.data.message);
  }

  async function handleEdit(e) {
    const isLoggedIn = await checkTokenLogin();
    if (isLoggedIn) {
      const isAdmin = await checkGroup("admin");
      setIsAdmin(isAdmin);
      isAdmin
        ? navigate(`/admin/editUser/${e.target.value}`)
        : navigate("/unathourised");
    } else {
      navigate("/login");
    }
  }

  async function handleCreateNewUser() {
    const isLoggedIn = await checkTokenLogin();
    if (isLoggedIn) {
      const isAdmin = await checkGroup("admin");
      setIsAdmin(isAdmin);
      isAdmin ? navigate("/admin/createUser") : navigate("/unathourised");
    } else {
      navigate("/login");
    }
  }

  async function handleCreateNewGroup() {
    const isLoggedIn = await checkTokenLogin();
    if (isLoggedIn) {
      const isAdmin = await checkGroup("admin");
      setIsAdmin(isAdmin);
      isAdmin ? navigate("/admin/createGroup") : navigate("/unathourised");
    } else {
      navigate("/login");
    }
  }

  useEffect(() => {
    async function check() {
      const isLoggedIn = await checkTokenLogin();
      if (isLoggedIn) {
        const isAdmin = await checkGroup("admin");
        setIsAdmin(isAdmin);
        if (isAdmin) {
          getAllUsers();
          setShowComponent(true);
        } else {
          navigate("/unathourised");
        }
      } else {
        navigate("/login");
      }
    }
    check();
  }, []);

  return (
    showComponent && (
      <div className="container">
        <h3>
          Manage Users
          <button onClick={handleCreateNewUser} className="nav-button">
            Create New User
          </button>
          <> </>
          <button onClick={handleCreateNewGroup} className="nav-button">
            Create New Group
          </button>
        </h3>

        <div className="create-group-table">
          <table>
            <thead>
              <tr>
                <th>Username</th>
                <th>Email</th>
                <th>Active?</th>
                <th>Groups</th>
                <th>Edit</th>
              </tr>
            </thead>
            <tbody>
              {allUsers.map((user) => {
                return (
                  <tr key={user.username}>
                    <td>{user.username}</td>
                    <td>{user.email}</td>
                    <td>{user.active === true ? "active" : "disabled"}</td>
                    <td>{user.groups.slice(1, user.groups.length - 1)}</td>
                    <td>
                      <button
                        onClick={handleEdit}
                        id="user-button"
                        // value={user.username}
                        value={user.username}
                      >
                        Edit
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    )
  );
}

export default ManageUsers;
