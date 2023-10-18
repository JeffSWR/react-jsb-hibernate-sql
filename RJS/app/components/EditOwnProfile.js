import React, { useState, useEffect, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import LoginContext from "../../LoginContext";
import axios from "axios";

function EditOwnProfile() {
  const currentUser = useParams().username;
  let [outdatedUser, setOutdatedUser] = useState({
    username: "",
    password: "",
    email: "",
    groups: "",
  });

  const { checkGroup, checkTokenLogin, setIsAdmin } = useContext(LoginContext);

  const navigate = useNavigate();

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
    const isLoggedIn = await checkTokenLogin();
    if (isLoggedIn) {
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
        console.log(outdatedUser);
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
    } else {
      navigate("/login");
    }
  }

  async function handleBack() {
    const isLoggedIn = await checkTokenLogin();
    isLoggedIn ? navigate("/home") : navigate("/unathourised");
  }

  useEffect(() => {
    const check = async function () {
      const isLoggedIn = await checkTokenLogin();
      if (!isLoggedIn) {
        navigate("/login");
      } else {
        fetchCurrentUser();
        const isAdmin = await checkGroup("admin");
        setIsAdmin(isAdmin);
      }
    };
    check();
  }, []);

  return (
    <form onSubmit={handleEdit}>
      <div className="container big">
        <h3 style={{ display: "inline" }}>Edit Profile</h3>
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
          disabled
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
        <br />
        <label htmlFor="groups">
          <b>Groups</b>
        </label>
        <input
          type="text"
          value={outdatedUser.groups.slice(1, outdatedUser.groups.length - 1)}
          disabled
        />

        <br />
        <button type="submit" className="registerbtn">
          Edit
        </button>
      </div>
    </form>
  );
}

export default EditOwnProfile;
