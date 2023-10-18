import React, { useState, useEffect, useContext } from "react";
import { useNavigate } from "react-router-dom";
import LoginContext from "../../LoginContext";
import axios from "axios";

function Applications() {
  //----Authorisation
  const { checkTokenLogin, checkGroup } = useContext(LoginContext);
  let [showComponent, setShowComponent] = useState(false);

  let [isProjectLead, setIsProjectLead] = useState(false);

  //----Retrieve all Apps
  let [allApplications, setAllApplications] = useState([]);

  async function getAllApplications() {
    const response = await axios.post(
      "/getAllApplications",
      {},
      { withCredentials: true }
    );

    const allApplications = response.data.applications;

    allApplications.forEach((app) => {
      Object.keys(app).forEach((key) => {
        if (app[key] === null) {
          app[key] = "";
        }
      });
    });

    setAllApplications(response.data.applications);
  }

  //----Retrieve all Groups
  let [allGroups, setAllGroups] = useState([]);

  const getAllGroups = async function () {
    try {
      await axios
        .post("/admin/getAllGroups", {}, { withCredentials: true })
        .then((response) =>
          response.data.error === null
            ? setAllGroups(response.data.message)
            : console.log(error)
        );
    } catch (e) {
      console.log(e);
    }
  };

  //----Create new App

  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newApplication, setNewApplication] = useState({});

  const emptyApplicationState = {
    app_Acronym: "",
    app_Description: "",
    app_Rnumber: "",
    app_startDate: "",
    app_endDate: "",
    app_permit_Open: "",
    app_permit_toDoList: "",
    app_permit_Doing: "",
    app_permit_Done: "",
    app_permit_create: "",
  };

  const toggleCreateModal = () => {
    setShowCreateModal(!showCreateModal);
  };

  const handleNewChange = (e) => {
    const { name, value } = e.target;
    setNewApplication((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  async function handleSubmitCreate(e) {
    e.preventDefault();
    try {
      console.log(newApplication);
      const response = await axios.post("/createApplication", newApplication, {
        withCredentials: true,
      });

      if (response.data.e) {
        if (response.data.e.errno == 1062) alert("Duplicate entry!");
      } else {
        alert(`${response.data.message}`);
        setNewApplication(emptyApplicationState);
        getAllApplications();
      }
    } catch (e) {
      console.log(e);
    }
  }

  async function handleCreateNewApplication(e) {
    const isProjectLead = await checkGroup("project lead");
    if (isProjectLead) {
      setNewApplication(emptyApplicationState);
      await getAllGroups();
      toggleCreateModal();
    } else {
      console.log("/unauthorized");
    }
  }

  //----Edit App
  const [showEditModal, setShowEditModal] = useState(false);
  const [editedApplication, setEditedApplication] = useState({});

  const toggleEditModal = () => {
    setShowEditModal(!showEditModal);
  };

  const handleEditedChange = (e) => {
    const { name, value } = e.target;
    setEditedApplication((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  async function handleSubmitEdit(e) {
    e.preventDefault();
    try {
      console.log(editedApplication);
      const response = await axios.post(
        "/updateApplication",
        editedApplication
      );
      alert(`${response.data.message}`);
    } catch (e) {
      console.log(e);
    }
  }

  const navigate = useNavigate();

  async function handleEditModal(e) {
    const isProjectLead = await checkGroup("project lead");
    if (isProjectLead) {
      const response = await axios.post("/getApplication", {
        appAcronym: e.target.value,
      });

      const outdatedApp = response.data.application;
      console.log(outdatedApp);

      Object.keys(outdatedApp).forEach((key) => {
        if (outdatedApp[key] === null) {
          outdatedApp[key] = "";
        }
      });

      await getAllGroups();
      setEditedApplication(outdatedApp);
      console.log(e.target.value);
      console.log(response);
      toggleEditModal();
    } else {
      console.log("/unauthorized");
    }
  }

  //----View App
  let [showViewModal, setShowViewModal] = useState(false);

  useEffect(() => {
    async function check() {
      const isLoggedIn = await checkTokenLogin();
      if (isLoggedIn) {
        getAllApplications();
        setShowComponent(true);
        const isProjectLead = await checkGroup("project lead");
        console.log(isProjectLead);
        setIsProjectLead(isProjectLead);
      } else {
        navigate("/login");
      }
    }
    check();
  }, []);

  return showComponent ? (
    <div className="container">
      <h3>
        Applications
        {isProjectLead && (
          <button onClick={handleCreateNewApplication} className="nav-button">
            Create New Application
          </button>
        )}
      </h3>

      <div className="create-app-table">
        <table>
          <thead>
            <tr>
              <th>Acronym</th>
              <th>Description</th>
              <th>View Tasks</th>
              <th>{isProjectLead ? "Edit Application" : "View Application"}</th>
            </tr>
          </thead>
          <tbody>
            {allApplications.map((application) => {
              return (
                <tr key={application.app_Acronym}>
                  <td>{application.app_Acronym}</td>
                  <td>
                    {application.app_Description.length >= 30
                      ? application.app_Description.slice(0, 30) + "..."
                      : application.app_Description}
                  </td>
                  <td>
                    <button
                      onClick={(e) => {
                        navigate(`/applications/${e.target.value}`);
                      }}
                      id="user-button"
                      // value={user.username}
                      value={application.app_Acronym}
                    >
                      View Tasks
                    </button>
                  </td>
                  <td>
                    <button
                      onClick={
                        isProjectLead
                          ? handleEditModal
                          : async (e) => {
                              const response = await axios.post(
                                "/getApplication",
                                {
                                  appAcronym: e.target.value,
                                }
                              );

                              const outdatedApp = response.data.application;

                              Object.keys(outdatedApp).forEach((key) => {
                                if (outdatedApp[key] === null) {
                                  outdatedApp[key] = "";
                                }
                              });

                              await getAllGroups();
                              setEditedApplication(outdatedApp);
                              setShowViewModal(true);
                            }
                      }
                      id="user-button"
                      // value={user.username}
                      value={application.app_Acronym}
                    >
                      {isProjectLead ? "Edit Application" : "View Application"}
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {showEditModal && (
        <div className="modal">
          <div className="modal-content">
            <form onSubmit={handleSubmitEdit} className="edit-app-form">
              <div className="edit-app-form">
                <h2>Update Application</h2>
                <button
                  className="close-button"
                  onClick={(e) => {
                    e.preventDefault();
                    toggleEditModal();
                  }}
                >
                  &times;
                </button>

                <div>
                  <label>Acronym:</label>
                  <input
                    type="text"
                    name="app_Acronym"
                    value={editedApplication.app_Acronym}
                    disabled
                  />
                  <label>Rnumber:</label>
                  <input
                    type="text"
                    name="app_Rnumber"
                    value={editedApplication.app_Rnumber}
                    disabled
                  />
                  <label>Description:</label>
                  <textarea
                    className="appDescription"
                    name="app_Description"
                    value={editedApplication.app_Description}
                    disabled
                  />

                  <div className="date">
                    <label>Start Date:</label>
                    <input
                      className="date-input"
                      type="date"
                      name="app_startDate"
                      value={editedApplication.app_startDate}
                      onChange={handleEditedChange}
                    />
                    <label>End Date:</label>
                    <input
                      className="date-input"
                      type="date"
                      name="app_endDate"
                      value={editedApplication.app_endDate}
                      onChange={handleEditedChange}
                    />
                  </div>

                  {Object.keys(editedApplication).map((key) => {
                    if (key.startsWith("app_permit")) {
                      return (
                        <div key={key} className="permit-field">
                          <label>
                            {key.replace("App_permit_", "Permit ")}:
                          </label>
                          <select
                            name={key}
                            value={editedApplication[key]}
                            onChange={handleEditedChange}
                          >
                            <option key="none" value="none">
                              none
                            </option>
                            {allGroups.map((group) => {
                              return (
                                <option key={group} value={group}>
                                  {group}
                                </option>
                              );
                            })}
                          </select>
                        </div>
                      );
                    }
                    return null;
                  })}

                  <button type="submit">Update</button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}

      {showViewModal && (
        <div className="modal">
          <div className="modal-content">
            <form className="edit-app-form">
              <div className="edit-app-form">
                <h2>Application</h2>
                <button
                  className="close-button"
                  onClick={(e) => {
                    e.preventDefault();
                    setShowViewModal(false);
                  }}
                >
                  &times;
                </button>

                <div>
                  <label>Acronym:</label>
                  <input
                    type="text"
                    name="app_Acronym"
                    value={editedApplication.app_Acronym}
                    disabled
                  />
                  <label>Rnumber:</label>
                  <input
                    type="text"
                    name="app_Rnumber"
                    value={editedApplication.app_Rnumber}
                    disabled
                  />
                  <label>Description:</label>
                  <textarea
                    className="appDescription"
                    name="app_Description"
                    value={editedApplication.app_Description}
                    disabled
                  />

                  <div className="date">
                    <label>Start Date:</label>
                    <input
                      className="date-input"
                      type="date"
                      name="app_startDate"
                      value={editedApplication.app_startDate}
                      onChange={handleEditedChange}
                      disabled
                    />
                    <label>End Date:</label>
                    <input
                      className="date-input"
                      type="date"
                      name="app_endDate"
                      value={editedApplication.app_endDate}
                      onChange={handleEditedChange}
                      disabled
                    />
                  </div>

                  {Object.keys(editedApplication).map((key) => {
                    if (key.startsWith("App_permit")) {
                      return (
                        <div key={key} className="permit-field">
                          <label>
                            {key.replace("App_permit_", "Permit ")}:
                          </label>
                          <select
                            name={key}
                            value={editedApplication[key]}
                            onChange={handleEditedChange}
                            disabled
                          >
                            <option key="none" value="none">
                              none
                            </option>
                            {allGroups.map((group) => {
                              return (
                                <option key={group} value={group}>
                                  {group}
                                </option>
                              );
                            })}
                          </select>
                        </div>
                      );
                    }
                    return null;
                  })}
                </div>
              </div>
            </form>
          </div>
        </div>
      )}

      {showCreateModal && (
        <div className="modal">
          <div className="modal-content">
            <form onSubmit={handleSubmitCreate} className="edit-app-form">
              <div className="edit-app-form">
                <h2>Create Application</h2>
                <button
                  className="close-button"
                  onClick={(e) => {
                    e.preventDefault();
                    toggleCreateModal();
                  }}
                >
                  &times;
                </button>

                <div>
                  <label>Acronym:</label>
                  <input
                    type="text"
                    name="app_Acronym"
                    value={newApplication.app_Acronym}
                    onChange={handleNewChange}
                    required
                  />
                  <label>Rnumber:</label>
                  <input
                    type="text"
                    name="app_Rnumber"
                    value={newApplication.app_Rnumber}
                    onChange={handleNewChange}
                    pattern="[0-9]+"
                    min="0"
                    title="Please enter a Postive Integer"
                    required
                  />
                  <label>Description:</label>
                  <textarea
                    className="appDescription"
                    name="app_Description"
                    value={newApplication.app_Description}
                    onChange={handleNewChange}
                  />

                  <div className="date">
                    <label>Start Date:</label>
                    <input
                      className="date-input"
                      type="date"
                      name="app_startDate"
                      value={newApplication.app_startDate}
                      onChange={handleNewChange}
                    />
                    <label>End Date:</label>
                    <input
                      className="date-input"
                      type="date"
                      name="app_endDate"
                      value={newApplication.app_endDate}
                      onChange={handleNewChange}
                    />
                  </div>

                  {Object.keys(newApplication).map((key) => {
                    if (key.startsWith("app_permit")) {
                      return (
                        <div key={key} className="permit-field">
                          <label>
                            {key.replace("app_permit_", "Permit ")}:
                          </label>
                          <select
                            name={key}
                            value={newApplication[key]}
                            onChange={handleNewChange}
                          >
                            <option key="none" value="none">
                              none
                            </option>
                            {allGroups.map((group) => {
                              return (
                                <option key={group} value={group}>
                                  {group}
                                </option>
                              );
                            })}
                          </select>
                        </div>
                      );
                    }
                    return null;
                  })}

                  <button type="submit">Create</button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  ) : (
    <></>
  );
}

export default Applications;
