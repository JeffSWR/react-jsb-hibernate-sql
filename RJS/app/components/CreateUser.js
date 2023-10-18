import React, { Fragment, useState, useEffect, useContext } from "react";
import { useNavigate } from "react-router-dom";
import LoginContext from "../../LoginContext";
import axios from "axios";

function CreateUser() {
  let [showComponent, setShowComponent] = useState(false);
  const navigate = useNavigate();

  let [allGroups, setAllGroups] = useState([]);
  let [newUser, setNewUser] = useState({
    username: "",
    password: "",
    email: "",
    active: 1,
    groups: ",",
  });

  const { checkGroup, checkTokenLogin, setIsAdmin } = useContext(LoginContext);

  const getAllGroups = async function () {
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
  };

  useEffect(() => {
    async function check() {
      const isLoggedIn = await checkTokenLogin();
      if (isLoggedIn) {
        const isAdmin = await checkGroup("admin");
        if (isAdmin) {
          setShowComponent(true);
          getAllGroups();
        } else {
          navigate("/unathourised");
        }
      } else {
        navigate("/login");
      }
    }
    check();
  }, []);

  async function handleSubmit(e) {
    e.preventDefault();
    const isLoggedIn = await checkTokenLogin();
    if (isLoggedIn) {
      const isAdmin = await checkGroup("admin");
      setIsAdmin(isAdmin);
      if (isAdmin) {
        try {
          await axios
            .post("/admin/createUser", newUser, {
              withCredentials: true,
            })
            .then((response) => {
              if (!response.data.error) {
                alert(response.data.message);
                setNewUser({
                  username: "",
                  password: "",
                  email: "",
                  active: 1,
                  groups: ",",
                });
              } else {
                if (1062) {
                  alert("Duplicated entry for email or password!");
                }
                // console.log(response.data.error.errno);
              }
            });
        } catch (e) {
          console.log(e);
        }
      } else {
        navigate("/unathourised");
      }
    } else {
      navigate("/login");
    }
  }

  async function handleBack() {
    const isAdmin = await checkGroup("admin");
    setIsAdmin(isAdmin);
    isAdmin ? navigate("/admin/manageUsers") : navigate("/unathourised");
  }

  return showComponent ? (
    <div>
      <form onSubmit={handleSubmit}>
        <div className="container big">
          <h3 style={{ display: "inline" }}>Create User</h3>
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
            value={newUser.username}
            onChange={(e) => {
              setNewUser({ ...newUser, username: e.target.value });
            }}
            required
          />
          <label htmlFor="psw">
            <b>Password</b>
          </label>
          <input
            type="password"
            placeholder="Enter Password"
            name="psw"
            pattern="^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,10}$"
            title="Passwords need to be 8-10 alphanumerical with special characters"
            value={newUser.password}
            onChange={(e) => {
              setNewUser({ ...newUser, password: e.target.value });
            }}
            id="psw"
            required
          />
          <label htmlFor="email">
            <b>Email</b>
          </label>
          <input
            type="email"
            placeholder="Enter Email"
            name="email"
            value={newUser.email}
            onChange={(e) => {
              setNewUser({ ...newUser, email: e.target.value });
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
            defaultChecked="checked"
            onClick={(e) => setNewUser({ ...newUser, active: 1 })}
          />
          <label htmlFor="Active">Active</label>{" "}
          <input
            type="radio"
            id="disabled"
            name="isActive"
            value={0}
            onClick={(e) => setNewUser({ ...newUser, active: 0 })}
          />
          <label htmlFor="disabled">Disabled</label>
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
                    onClick={(e) => {
                      if (e.target.checked) {
                        setNewUser({
                          ...newUser,
                          groups: newUser.groups + e.target.value + ",",
                        });
                      } else {
                        setNewUser({
                          ...newUser,
                          groups: newUser.groups.replace(
                            `${e.target.value + ","}`,
                            ""
                          ),
                        });
                      }
                      // console.log(newUser);
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
            Create
          </button>
        </div>
      </form>
    </div>
  ) : (
    <></>
  );
}

export default CreateUser;
