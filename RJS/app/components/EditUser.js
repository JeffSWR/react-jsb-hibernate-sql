import React, { Fragment, useState, useEffect, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import LoginContext from "../../LoginContext";
import axios from "axios";

function EditUser() {
  const currentUser = useParams().username;
  const { checkGroup, checkTokenLogin, setIsAdmin } = useContext(LoginContext);
  const navigate = useNavigate();

  let [showComponent, setShowComponent] = useState(false);
  let [allGroups, setAllGroups] = useState([]);
  let [outdatedUser, setOutdatedUser] = useState({
    username: "",
    password: "",
    email: "",
    active: 1,
    groups: ",",
  });

  async function getAllGroups() {
    try {
      await axios
        .post(
          "/admin/getAllGroups",
          {},
          {
            withCredentials: true,
          }
        )
        .then((response) =>
          response.data.error === null
            ? setAllGroups(response.data.message)
            : console.log(error)
        );
    } catch (e) {
      console.log(e);
    }
  }

  async function fetchCurrentUser() {
    const response = await axios.post(
      "/admin/getOneUser",
      { username: currentUser },
      {
        withCredentials: true,
      }
    );
    const fetchedUser = response.data.message;

    if (fetchedUser.email === null) {
      setOutdatedUser({ ...fetchedUser, password: "", email: "" });
    } else {
      setOutdatedUser({ ...fetchedUser, password: "" });
    }
  }

  async function handleEdit(e) {
    e.preventDefault();
    if (outdatedUser.password == "") {
      const tempUser = { ...outdatedUser };
      delete tempUser.password;

      try {
        let res = await axios.post(
          "/admin/updateUserWithoutPassword",
          tempUser,
          {
            withCredentials: true,
          }
        );

        if (!res.data.error) {
          alert(res.data.message);
        }
      } catch (e) {
        console.log(e);
      }
    } else {
      try {
        await axios
          .post("/admin/updateUser", outdatedUser, {
            withCredentials: true,
          })
          .then((res) => {
            if (!res.data.error) {
              alert(res.data.message);
            }
          });
      } catch (e) {
        console.log(e);
      }
    }
  }

  async function handleBack() {
    const isAdmin = await checkGroup("admin");
    setIsAdmin(isAdmin);
    isAdmin ? navigate("/admin/manageUsers") : navigate("/unathourised");
  }

  useEffect(() => {
    async function check() {
      const isLoggedIn = await checkTokenLogin();
      if (isLoggedIn) {
        const isAdmin = await checkGroup("admin");
        setIsAdmin(isAdmin);
        if (isAdmin) {
          setShowComponent(true);
          getAllGroups();
          fetchCurrentUser();
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
      <form onSubmit={handleEdit}>
        <div className="container big">
          <h3 style={{ display: "inline" }}>Edit User</h3>
          <div className="create-forms-back-button" onClick={handleBack}>
            Back
          </div>
          <hr />
          <label htmlFor="username">
            <b>Username</b>
          </label>
          <input
            type="text"
            placeholder="Enter Username"
            name="username"
            id="username"
            value={outdatedUser.username}
            onChange={(e) => {
              setOutdatedUser({ ...outdatedUser, username: e.target.value });
            }}
            readOnly
          />
          <label htmlFor="psw">
            <b>Password </b>
          </label>
          <input
            type="password"
            placeholder="Enter New Password (Leave it empty if no changes)"
            pattern="((^$)|(^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,10}$))"
            name="psw"
            value={outdatedUser.password}
            onChange={(e) => {
              setOutdatedUser({ ...outdatedUser, password: e.target.value });
            }}
            id="psw"
            title="Passwords need to be 8-10 alphanumerical with special characters"
          />
          <label htmlFor="email">
            <b>Email</b>
          </label>
          <input
            type="email"
            placeholder="Enter Email"
            name="email"
            value={outdatedUser.email}
            onChange={(e) => {
              setOutdatedUser({ ...outdatedUser, email: e.target.value });
            }}
            id="email"
          />
          <label htmlFor="isActive">
            <b>Active?</b>
          </label>
          <br />
          <input
            type="radio"
            id="Active"
            name="isActive"
            value={1}
            checked={outdatedUser.active ? true : false}
            onChange={(e) => setOutdatedUser({ ...outdatedUser, active: 1 })}
          />
          <label htmlFor="Active">Active</label>{" "}
          {currentUser === "admin" ? (
            <></>
          ) : (
            <input
              type="radio"
              id="disabled"
              name="isActive"
              checked={outdatedUser.active ? false : true}
              value={0}
              onChange={(e) => setOutdatedUser({ ...outdatedUser, active: 0 })}
            />
          )}
          {currentUser === "admin" ? (
            <></>
          ) : (
            <label htmlFor="disabled">Disabled</label>
          )}
          <br />
          <label htmlFor="groups">
            <b>Groups</b>
          </label>
          <br />
          <>
            {allGroups.map((group) => {
              return (
                <Fragment key={group}>
                  <input
                    type="checkbox"
                    checked={
                      outdatedUser.groups.includes(`,${group},`) ? true : false
                    }
                    onChange={(e) => {
                      if (e.target.checked) {
                        setOutdatedUser({
                          ...outdatedUser,
                          groups: outdatedUser.groups + e.target.value + ",",
                        });
                      } else {
                        setOutdatedUser({
                          ...outdatedUser,
                          groups: outdatedUser.groups.replace(
                            `${e.target.value + ","}`,
                            ""
                          ),
                        });
                      }
                    }}
                    id={group}
                    name="groups"
                    value={group}
                  />
                  <label className="checkbox" htmlFor={group}>
                    {group}
                  </label>{" "}
                </Fragment>
              );
            })}
          </>
          <button type="submit" className="registerbtn">
            Edit
          </button>
        </div>
      </form>
    )
  );
}

export default EditUser;
