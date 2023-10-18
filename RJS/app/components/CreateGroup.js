import React, { useState, useEffect, useContext } from "react";
import { Link, useNavigate } from "react-router-dom";
import LoginContext from "../../LoginContext";
import axios from "axios";

function CreateGroup() {
  let [showComponent, setShowComponent] = useState(false);
  let [allGroups, setAllGroups] = useState([]);
  let [newGroup, setNewGroup] = useState("");
  const { checkGroup, checkTokenLogin, setIsAdmin } = useContext(LoginContext);
  const navigate = useNavigate();

  const getAllGroups = async function () {
    const response = await axios.post(
      "/admin/getAllGroups",
      {},
      {
        withCredentials: true,
      }
    );
    setAllGroups(response.data.message);
  };

  const handleSubmit = async function (e) {
    e.preventDefault();
    const isLoggedIn = await checkTokenLogin();
    if (isLoggedIn) {
      const isAdmin = await checkGroup("admin");
      console.log(isAdmin);
      if (isAdmin) {
        try {
          const response = await axios.post(
            "/admin/createGroup",
            {
              group_name: newGroup,
            },
            {
              withCredentials: true,
            }
          );
          if (!response.data.error) {
            alert(`${newGroup} created!`);
            await getAllGroups();
          } else {
            alert(response.data.message);
          }
        } catch (e) {
          alert(`Exception Error, contact admin`);
        }
      } else {
        navigate("/unathourised");
      }
    } else {
      navigate("/login");
    }
  };

  async function handleBack() {
    const isLoggedIn = await checkTokenLogin();
    if (isLoggedIn) {
      const isAdmin = await checkGroup("admin");
      isAdmin ? navigate("/admin/manageUsers") : navigate("/unathourised");
      setIsAdmin(isAdmin);
    } else {
      navigate("/login");
    }
  }

  useEffect(() => {
    async function check() {
      const isAdmin = await checkGroup("admin");
      if (isAdmin) {
        setShowComponent(true);
        getAllGroups();
      } else {
        navigate("/unathourised");
      }
    }
    check();
  }, []);

  return showComponent ? (
    <div className="container">
      <h3>Create Group</h3>
      <form style={{ border: "none" }} onSubmit={handleSubmit}>
        <input
          type="text"
          style={{ marginRight: "3px" }}
          name="groupname"
          id="groupname"
          value={newGroup}
          onChange={(e) => {
            setNewGroup(e.target.value);
          }}
          placeholder="Enter New Group Name"
          required
        />
        <input type="submit" />
        <Link style={{ float: "right" }} to="/admin/manageUsers">
          <div className="create-forms-back-button" onClick={handleBack}>
            Back
          </div>
        </Link>
      </form>
      <div>
        <h3 className="group-table-header col-12">Current Groups</h3>
      </div>
      <div className="create-group-table">
        <table>
          <tbody>
            {allGroups.map((group) => {
              return (
                <tr key={group}>
                  <td>{group}</td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  ) : (
    <></>
  );
}

export default CreateGroup;
