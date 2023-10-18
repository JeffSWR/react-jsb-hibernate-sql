import { useParams, useNavigate } from "react-router-dom";
import React, { useContext, useEffect, useState } from "react";
import LoginContext from "../../LoginContext";
import axios from "axios";

const KanbanApp = () => {
  const navigate = useNavigate();
  let [permitOpen, setPermitOpen] = useState(false);
  let [permitToDo, setPermitToDo] = useState(false);
  let [permitDoing, setPermitDoing] = useState(false);
  let [permitDone, setPermitDone] = useState(false);
  let [permitCreate, setPermitCreate] = useState(false);

  let [isPM, setIsPM] = useState(false);

  async function checkIsPM() {
    const isPM = await checkGroup("project manager");
    setIsPM(isPM);
  }

  async function checkAllPermits(app) {
    console.log(app);
    const permitOpen = await checkGroup(app.app_permit_Open);
    const permitToDo = await checkGroup(app.app_permit_toDoList);
    const permitDoing = await checkGroup(app.app_permit_Doing);
    const permitDone = await checkGroup(app.app_permit_Done);
    const permitCreate = await checkGroup(app.app_permit_create);
    console.log(permitOpen, permitToDo, permitDoing, permitDone, permitCreate);
    setPermitOpen(permitOpen);
    setPermitToDo(permitToDo);
    setPermitDoing(permitDoing);
    setPermitDone(permitDone);
    setPermitCreate(permitCreate);
  }

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

  useEffect(() => {
    async function firstLoad() {
      await checkTokenLogin();
      await getAllTasks();
      await getCurrentAppAndPermits();
      await getAllPlans();
      await checkIsPM();
    }
    firstLoad();
  }, []);

  const appAcronym = useParams().application;

  //----Retrieve app
  let [currentApp, setCurrentApp] = useState({});
  async function getCurrentAppAndPermits() {
    try {
      const response = await axios.post("/getApplication", {
        appAcronym,
      });
      await checkAllPermits(response.data.application);
      setCurrentApp(response.data.application);
    } catch (error) {
      console.log(error);
    }
  }

  //---Retrieve all related plans
  let [allPlans, setAllPlans] = useState([]);

  const getAllPlans = async function () {
    const response = await axios.post("/getAllPlans", {
      app: appAcronym,
    });

    setAllPlans(response.data.plans);
  };

  //----Retrieve all related tasks
  const [allTasks, setAllTasks] = useState([]);

  async function getAllTasks() {
    console.log(appAcronym);
    const response = await axios.post("/getAllTasks", {
      app: appAcronym,
    });
    const allTasks = response.data.message;
    setAllTasks(response.data.message);
    console.log(response);
  }

  //----Create Task
  const [showTaskModal, setShowTaskModal] = useState(false);
  const [newTask, setNewTask] = useState({
    task_app_Acronym: appAcronym,
    task_owner: user,
    task_creator: user,
    task_name: "",
    task_description: "",
    task_plan: "",
  });

  async function toggleCreateTaskModal() {
    setShowTaskModal(!showTaskModal);
  }

  async function handleSubmitCreateTask(e) {
    e.preventDefault();
    console.log(newTask);
    try {
      const response = await axios.post("/createTask", newTask);
      console.log(response);
      if (response.data.e) {
        alert(`Duplicate entry`);
        console.log(response.data.e);
      } else {
        alert(`${newTask.task_name} created`);
        setNewTask({
          task_app_Acronym: appAcronym,
          task_owner: user,
          task_creator: user,
          task_name: "",
          task_description: "",
          task_plan: "",
        });
        await getAllTasks();
      }
    } catch (error) {
      console.log(error);
    }
  }

  //----Edit task--------------------------------------------------------------------
  const [showEditTaskModal, setShowEditTaskModal] = useState(false);
  const [editedTask, setEditedTask] = useState({});

  async function editTask(e) {
    const taskId = e.target.name;
    const permittedGroup = e.target.value;
    console.log(permittedGroup);
    console.log(taskId);

    const permitted = await checkGroup(permittedGroup);
    if (permitted) {
      //fetch task info
      try {
        const response = await axios.post("/getOneTask", {
          task_id: taskId,
        });

        let task = response.data.message;
        console.log(task);

        setEditedTask({ ...task, task_owner: user, newNotes: "" });
      } catch (error) {
        console.log(error);
      }
      //toggle promote modal
      setShowEditTaskModal(!showEditTaskModal);
    } else {
      navigate(`/`);
    }
  }

  async function handleSubmitEditedTask(e) {
    e.preventDefault();
    console.log(editedTask);
    try {
      const response = await axios.post("/updateTask", editedTask);
      alert(`successfully updated task`);
      setShowEditTaskModal(!showEditTaskModal);
    } catch (error) {
      console.log(error);
    }
  }

  //----Promote task
  const [showPromoteTaskModal, setShowPromoteTaskModal] = useState(false);
  async function promoteTask(e) {
    const taskId = e.target.name;
    const permittedGroup = e.target.value;
    console.log(permittedGroup);

    const permitted = await checkGroup(permittedGroup);
    if (permitted) {
      //fetch task info
      try {
        const response = await axios.post("/getOneTask", {
          task_id: taskId,
        });

        let task = response.data.message;

        setEditedTask({ ...task, task_owner: user, newNotes: "" });
      } catch (error) {
        console.log(error);
      }
      //toggle promote modal
      setShowPromoteTaskModal(!showPromoteTaskModal);
    } else {
      navigate(`/`);
    }
  }

  async function handleSubmitPromoteTask(e) {
    e.preventDefault();
    console.log(editedTask);
    try {
      const response = await axios.post("/promoteTask", editedTask);
      alert(`successfully updated task`);
      setShowPromoteTaskModal(!showPromoteTaskModal);
      getAllTasks();
    } catch (error) {
      console.log(error);
    }
  }
  //----Demote task
  const [showDemoteTaskModal, setShowDemoteTaskModal] = useState(false);

  async function demoteTask(e) {
    const taskId = e.target.name;
    const permittedGroup = e.target.value;
    console.log(permittedGroup);

    const permitted = await checkGroup(permittedGroup);
    if (permitted) {
      //fetch task info
      try {
        const response = await axios.post("/getOneTask", {
          task_id: taskId,
        });

        let task = response.data.message;

        setEditedTask({ ...task, task_owner: user, newNotes: "" });
      } catch (error) {
        console.log(error);
      }
      //toggle promote modal
      setShowDemoteTaskModal(!showDemoteTaskModal);
    } else {
      navigate(`/`);
    }
  }

  async function handleSubmitDemoteTask(e) {
    e.preventDefault();

    console.log(editedTask);
    try {
      const response = await axios.post("/demoteTask", editedTask);
      alert(`successfully demoted task`);
      setShowDemoteTaskModal(!showDemoteTaskModal);
      getAllTasks();
    } catch (error) {
      console.log(error);
    }
  }

  //----View Task
  let [showViewTaskModal, setShowViewTaskModal] = useState(false);

  return (
    <div>
      <div key="kanban-app" className="kanban-app">
        <h4>
          {appAcronym}
          {isPM && (
            <button
              className="nav-button"
              onClick={() => {
                navigate(`/applications/plans/${appAcronym}`);
              }}
            >
              Manage Plan
            </button>
          )}
          {permitCreate && (
            <button className="nav-button" onClick={toggleCreateTaskModal}>
              Create Task
            </button>
          )}
        </h4>
        <div key="Open" className="kanban-board">
          {/* Open state */}
          <div key="Open" className="kanban-column">
            <div className="kanban-column-header">
              <h5>Open</h5>
            </div>
            <div className="kanban-card-list">
              {allTasks
                .filter((task) => task.task_state === "Open")
                .map((task) => (
                  <div key={task.task_id} className="kanban-card">
                    {task.task_plan ? (
                      <h6>{task.task_name + "(" + task.task_plan + ")"}</h6>
                    ) : (
                      <h6>{task.task_name}</h6>
                    )}
                    <h6>{"Task ID: " + task.task_id}</h6>
                    {permitOpen ? (
                      <div className="button-group">
                        <button
                          name={task.task_id}
                          value={currentApp.app_permit_Open}
                          className="edit-button"
                          onClick={editTask}
                        >
                          Edit
                        </button>
                        <button
                          name={task.task_id}
                          value={currentApp.app_permit_Open}
                          onClick={promoteTask}
                          className="move-button"
                        >
                          &rarr;
                        </button>
                      </div>
                    ) : (
                      <div className="button-group">
                        <button
                          name={task.task_id}
                          value={currentApp.app_permit_Open}
                          className="edit-button"
                          onClick={async (e) => {
                            const taskId = e.target.name;
                            const response = await axios.post("/getOneTask", {
                              task_id: taskId,
                            });

                            let task = response.data.message;

                            setEditedTask(task);
                            setShowViewTaskModal(!showViewTaskModal);
                          }}
                        >
                          View
                        </button>
                      </div>
                    )}
                  </div>
                ))}
            </div>
          </div>
          {/* */}
          <div key="To-Do" className="kanban-column">
            <div className="kanban-column-header">
              <h5>To-Do</h5>
            </div>
            <div className="kanban-card-list">
              {allTasks
                .filter((task) => task.task_state === "To-Do")
                .map((task) => (
                  <div key={task.task_id} className="kanban-card">
                    {task.task_plan ? (
                      <h6>{task.task_name + "(" + task.task_plan + ")"}</h6>
                    ) : (
                      <h6>{task.task_name}</h6>
                    )}
                    <h6>{"Task ID: " + task.task_id}</h6>
                    {permitToDo && (
                      <div className="button-group">
                        <button
                          name={task.task_id}
                          value={currentApp.app_permit_toDoList}
                          className="edit-button"
                          onClick={editTask}
                        >
                          Edit
                        </button>
                        <button
                          name={task.task_id}
                          value={currentApp.app_permit_toDoList}
                          onClick={promoteTask}
                          className="move-button"
                        >
                          &rarr;
                        </button>
                      </div>
                    )}
                    {!permitToDo && (
                      <div className="button-group">
                        <button
                          name={task.task_id}
                          value={currentApp.app_permit_Open}
                          className="edit-button"
                          onClick={async (e) => {
                            const taskId = e.target.name;
                            const response = await axios.post("/getOneTask", {
                              task_id: taskId,
                            });

                            let task = response.data.message;

                            setEditedTask(task);
                            setShowViewTaskModal(!showViewTaskModal);
                          }}
                        >
                          View
                        </button>
                      </div>
                    )}
                  </div>
                ))}
            </div>
          </div>
          {/* Doing*/}
          <div key="Doing" className="kanban-column">
            <div className="kanban-column-header">
              <h5>Doing</h5>
            </div>
            <div className="kanban-card-list">
              {allTasks
                .filter((task) => task.task_state === "Doing")
                .map((task) => (
                  <div key={task.id} className="kanban-card">
                    {task.task_plan ? (
                      <h6>{task.task_name + "(" + task.task_plan + ")"}</h6>
                    ) : (
                      <h6>{task.task_name}</h6>
                    )}
                    <h6>{"Task ID: " + task.task_id}</h6>
                    {permitDoing && (
                      <div className="button-group">
                        <button
                          name={task.task_id}
                          value={currentApp.app_permit_Doing}
                          onClick={demoteTask}
                          className="move-button"
                        >
                          &larr;
                        </button>
                        <button
                          name={task.task_id}
                          value={currentApp.app_permit_Doing}
                          className="edit-button"
                          onClick={editTask}
                        >
                          Edit
                        </button>
                        <button
                          name={task.task_id}
                          value={currentApp.app_permit_Doing}
                          onClick={promoteTask}
                          className="move-button"
                        >
                          &rarr;
                        </button>
                      </div>
                    )}
                    {!permitDoing && (
                      <div className="button-group">
                        <button
                          name={task.task_id}
                          value={currentApp.app_permit_Open}
                          className="edit-button"
                          onClick={async (e) => {
                            const taskId = e.target.name;
                            const response = await axios.post("/getOneTask", {
                              task_id: taskId,
                            });

                            let task = response.data.message;

                            setEditedTask(task);
                            setShowViewTaskModal(!showViewTaskModal);
                          }}
                        >
                          View
                        </button>
                      </div>
                    )}
                  </div>
                ))}
            </div>
          </div>
          {/* Done*/}
          <div key="Done" className="kanban-column">
            <div className="kanban-column-header">
              <h5>Done</h5>
            </div>
            <div className="kanban-card-list">
              {allTasks
                .filter((task) => task.task_state === "Done")
                .map((task) => (
                  <div key={task.id} className="kanban-card">
                    {task.task_plan ? (
                      <h6>{task.task_name + "(" + task.task_plan + ")"}</h6>
                    ) : (
                      <h6>{task.task_name}</h6>
                    )}
                    <h6>{"Task ID: " + task.task_id}</h6>
                    {permitDone && (
                      <div className="button-group">
                        <button
                          name={task.task_id}
                          value={currentApp.app_permit_Done}
                          onClick={demoteTask}
                          className="move-button"
                        >
                          &larr;
                        </button>
                        <button
                          name={task.task_id}
                          value={currentApp.app_permit_Done}
                          className="edit-button"
                          onClick={editTask}
                        >
                          Edit
                        </button>
                        <button
                          name={task.task_id}
                          value={currentApp.app_permit_Done}
                          onClick={promoteTask}
                          className="move-button"
                        >
                          &rarr;
                        </button>
                      </div>
                    )}
                    {!permitDone && (
                      <div className="button-group">
                        <button
                          name={task.task_id}
                          value={currentApp.app_permit_Done}
                          className="edit-button"
                          onClick={async (e) => {
                            const taskId = e.target.name;
                            const response = await axios.post("/getOneTask", {
                              task_id: taskId,
                            });

                            let task = response.data.message;

                            setEditedTask(task);
                            setShowViewTaskModal(!showViewTaskModal);
                          }}
                        >
                          View
                        </button>
                      </div>
                    )}
                  </div>
                ))}
            </div>
          </div>
          {/* Closed*/}
          <div key="Closed" className="kanban-column">
            <div className="kanban-column-header">
              <h5>Closed</h5>
            </div>
            <div className="kanban-card-list">
              {allTasks
                .filter((task) => task.task_state === "Closed")
                .map((task) => (
                  <div key={task.id} className="kanban-card">
                    {task.task_plan ? (
                      <h6>{task.task_name + "(" + task.task_plan + ")"}</h6>
                    ) : (
                      <h6>{task.task_name}</h6>
                    )}
                    <h6>{"Task ID: " + task.task_id}</h6>
                    <div className="button-group">
                      <button
                        name={task.task_id}
                        value={currentApp.app_permit_Open}
                        className="edit-button"
                        onClick={async (e) => {
                          const taskId = e.target.name;
                          const response = await axios.post("/getOneTask", {
                            task_id: taskId,
                          });

                          let task = response.data.message;

                          setEditedTask(task);
                          setShowViewTaskModal(!showViewTaskModal);
                        }}
                      >
                        View
                      </button>
                    </div>
                  </div>
                ))}
            </div>
          </div>
        </div>
      </div>

      {showTaskModal && (
        <div key="create-task-modal" className="modal">
          <div className="modal-content">
            <form onSubmit={handleSubmitCreateTask} className="edit-app-form">
              <div className="edit-app-form">
                <h2>Create Task</h2>
                <button
                  className="close-button"
                  onClick={(e) => {
                    e.preventDefault();
                    toggleCreateTaskModal();
                  }}
                >
                  &times;
                </button>

                <div>
                  <label>Name:</label>
                  <input
                    type="text"
                    name="App_Acronym"
                    value={newTask.task_name}
                    onChange={(e) => {
                      setNewTask({ ...newTask, task_name: e.target.value });
                    }}
                    required
                  />

                  <label>Description:</label>
                  <textarea
                    className="appDescription"
                    name="task_description"
                    value={newTask.task_description}
                    onChange={(e) => {
                      setNewTask({
                        ...newTask,
                        task_description: e.target.value,
                      });
                    }}
                  />

                  {
                    <div key="plan" className="permit-field">
                      <label>Plan: </label>
                      <select
                        name="plan"
                        value={newTask.task_plan}
                        onChange={(e) => {
                          setNewTask({ ...newTask, task_plan: e.target.value });
                        }}
                      >
                        <option key="none" value="">
                          none
                        </option>
                        {allPlans.map((plan) => {
                          return (
                            <option
                              key={plan.plan_MVP_Name}
                              value={plan.plan_MVP_Name}
                            >
                              {plan.plan_MVP_Name}
                            </option>
                          );
                        })}
                      </select>
                    </div>
                  }

                  <button type="submit">Create</button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}

      {showPromoteTaskModal && (
        <div key="create-task-modal" className="modal">
          <div className="modal-content">
            <form onSubmit={handleSubmitPromoteTask} className="edit-app-form">
              <div className="edit-app-form">
                <h2>Promote Task</h2>
                <button
                  className="close-button"
                  onClick={(e) => {
                    e.preventDefault();
                    setShowPromoteTaskModal(!showPromoteTaskModal);
                  }}
                >
                  &times;
                </button>

                <div>
                  <label>Name:</label>
                  <input
                    type="text"
                    name="App_Acronym"
                    value={editedTask.task_name}
                    onChange={(e) => {
                      setEditedTask({ ...editTask, task_name: e.target.value });
                    }}
                    disabled
                  />

                  <label>Description:</label>
                  <textarea
                    className="appDescription"
                    name="task_description"
                    value={editedTask.task_description}
                    onChange={(e) => {
                      setEditedTask({
                        ...editedTask,
                        task_description: e.target.value,
                      });
                    }}
                    disabled
                  />

                  <label>Audit Trail:</label>
                  <textarea
                    className="appDescription"
                    name="task_description"
                    value={editedTask.task_notes}
                    onChange={(e) => {
                      setEditedTask({
                        ...editedTask,
                        task_description: e.target.value,
                      });
                    }}
                    disabled
                  />

                  <label>Note:</label>
                  <textarea
                    className="appDescription"
                    name="task_description"
                    value={editedTask.newNotes}
                    onChange={(e) => {
                      setEditedTask({
                        ...editedTask,
                        newNotes: e.target.value,
                      });
                    }}
                  />

                  {
                    <div key="plan" className="permit-field">
                      <label>Plan: </label>
                      {editedTask.task_state == "To-Do" ||
                      editedTask.task_state == "Doing" ||
                      editedTask.task_state == "Done" ? (
                        <select
                          name="plan"
                          value={editedTask.task_plan}
                          onChange={(e) => {
                            setEditedTask({
                              ...editedTask,
                              task_plan: e.target.value,
                            });
                          }}
                          disabled
                        >
                          <option key="none" value="none">
                            none
                          </option>
                          {allPlans.map((plan) => {
                            return (
                              <option
                                key={plan.plan_MVP_Name}
                                value={plan.plan_MVP_Name}
                              >
                                {plan.plan_MVP_Name}
                              </option>
                            );
                          })}
                        </select>
                      ) : (
                        <select
                          name="plan"
                          value={editedTask.task_plan}
                          onChange={(e) => {
                            setEditedTask({
                              ...editedTask,
                              task_plan: e.target.value,
                            });
                          }}
                        >
                          <option key="none" value="none">
                            none
                          </option>
                          {allPlans.map((plan) => {
                            return (
                              <option
                                key={plan.plan_MVP_Name}
                                value={plan.plan_MVP_Name}
                              >
                                {plan.plan_MVP_Name}
                              </option>
                            );
                          })}
                        </select>
                      )}
                    </div>
                  }

                  <button type="submit">Promote</button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}
      {showDemoteTaskModal && (
        <div key="create-task-modal" className="modal">
          <div className="modal-content">
            <form onSubmit={handleSubmitDemoteTask} className="edit-app-form">
              <div className="edit-app-form">
                <h2>Demote Task</h2>
                <button
                  className="close-button"
                  onClick={(e) => {
                    e.preventDefault();
                    setShowDemoteTaskModal(!showDemoteTaskModal);
                  }}
                >
                  &times;
                </button>

                <div>
                  <label>Name:</label>
                  <input
                    type="text"
                    name="App_Acronym"
                    value={editedTask.task_name}
                    onChange={(e) => {
                      setEditedTask({
                        ...editedTask,
                        task_name: e.target.value,
                      });
                    }}
                    disabled
                  />

                  <label>Description:</label>
                  <textarea
                    className="appDescription"
                    name="task_description"
                    value={editedTask.task_description}
                    onChange={(e) => {
                      setEditedTask({
                        ...editedTask,
                        task_description: e.target.value,
                      });
                    }}
                    disabled
                  />

                  <label>Audit Trail:</label>
                  <textarea
                    className="appDescription"
                    name="task_description"
                    value={editedTask.task_notes}
                    onChange={(e) => {
                      setEditedTask({
                        ...editedTask,
                        task_description: e.target.value,
                      });
                    }}
                    disabled
                  />

                  <label>Note:</label>
                  <textarea
                    className="appDescription"
                    name="task_description"
                    value={editedTask.newNotes}
                    onChange={(e) => {
                      setEditedTask({
                        ...editedTask,
                        newNotes: e.target.value,
                      });
                    }}
                  />

                  {
                    <div key="plan" className="permit-field">
                      <label>Plan: </label>
                      {editedTask.task_state == "Doing" ? (
                        <select
                          name="plan"
                          value={editedTask.task_plan}
                          onChange={(e) => {
                            setEditedTask({
                              ...editedTask,
                              task_plan: e.target.value,
                            });
                          }}
                          disabled
                        >
                          <option key="none" value="none">
                            none
                          </option>
                          {allPlans.map((plan) => {
                            return (
                              <option
                                key={plan.plan_MVP_Name}
                                value={plan.plan_MVP_Name}
                              >
                                {plan.plan_MVP_Name}
                              </option>
                            );
                          })}
                        </select>
                      ) : (
                        <select
                          name="plan"
                          value={editedTask.task_plan}
                          onChange={(e) => {
                            setEditedTask({
                              ...editedTask,
                              task_plan: e.target.value,
                            });
                          }}
                        >
                          <option key="none" value="none">
                            none
                          </option>
                          {allPlans.map((plan) => {
                            return (
                              <option
                                key={plan.plan_MVP_Name}
                                value={plan.plan_MVP_Name}
                              >
                                {plan.plan_MVP_Name}
                              </option>
                            );
                          })}
                        </select>
                      )}
                    </div>
                  }

                  <button type="submit">Demote</button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}
      {showEditTaskModal && (
        <div key="create-task-modal" className="modal">
          <div className="modal-content">
            <form onSubmit={handleSubmitEditedTask} className="edit-app-form">
              <div className="edit-app-form">
                <h2>Update Task</h2>
                <button
                  className="close-button"
                  onClick={(e) => {
                    e.preventDefault();
                    setShowEditTaskModal(!showEditTaskModal);
                  }}
                >
                  &times;
                </button>

                <div>
                  <label>Name:</label>
                  <input
                    type="text"
                    name="App_Acronym"
                    value={editedTask.task_name}
                    onChange={(e) => {
                      setEditedTask({
                        ...editedTask,
                        task_name: e.target.value,
                      });
                    }}
                    disabled
                  />

                  <label>Description:</label>
                  <textarea
                    className="appDescription"
                    name="task_description"
                    value={editedTask.task_description}
                    onChange={(e) => {
                      setEditedTask({
                        ...editedTask,
                        task_description: e.target.value,
                      });
                    }}
                    disabled
                  />

                  <label>Audit Trail:</label>
                  <textarea
                    className="appDescription"
                    name="task_description"
                    value={editedTask.task_notes}
                    onChange={(e) => {
                      setEditedTask({
                        ...editedTask,
                        task_notes: e.target.value,
                      });
                    }}
                    disabled
                  />

                  <label>Note:</label>
                  <textarea
                    className="appDescription"
                    name="task_description"
                    value={editedTask.newNotes}
                    onChange={(e) => {
                      setEditedTask({
                        ...editedTask,
                        newNotes: e.target.value,
                      });
                    }}
                  />

                  {
                    <div key="plan" className="permit-field">
                      <label>Plan: </label>
                      {editedTask.task_state == "Open" ? (
                        <select
                          name="plan"
                          value={editedTask.task_plan}
                          onChange={(e) => {
                            setEditedTask({
                              ...editedTask,
                              task_plan: e.target.value,
                            });
                          }}
                        >
                          <option key="none" value="none">
                            none
                          </option>
                          {allPlans.map((plan) => {
                            return (
                              <option
                                key={plan.plan_MVP_Name}
                                value={plan.plan_MVP_Name}
                              >
                                {plan.plan_MVP_Name}
                              </option>
                            );
                          })}
                        </select>
                      ) : (
                        <select
                          name="plan"
                          value={editedTask.task_plan}
                          onChange={(e) => {
                            setEditedTask({
                              ...editedTask,
                              task_plan: e.target.value,
                            });
                          }}
                          disabled
                        >
                          <option key="none" value="none">
                            none
                          </option>
                          {allPlans.map((plan) => {
                            return (
                              <option
                                key={plan.plan_MVP_Name}
                                value={plan.plan_MVP_Name}
                              >
                                {plan.plan_MVP_Name}
                              </option>
                            );
                          })}
                        </select>
                      )}
                    </div>
                  }

                  <button type="submit">Update</button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}

      {showViewTaskModal && (
        <div key="create-task-modal" className="modal">
          <div className="modal-content">
            <form className="edit-app-form">
              <div className="edit-app-form">
                <h2>Task</h2>
                <button
                  className="close-button"
                  onClick={(e) => {
                    e.preventDefault();

                    setShowViewTaskModal(!showViewTaskModal);
                  }}
                >
                  &times;
                </button>

                <div>
                  <label>Name:</label>
                  <input
                    type="text"
                    name="App_Acronym"
                    value={editedTask.task_name}
                    onChange={(e) => {
                      setEditedTask({ ...editTask, task_name: e.target.value });
                    }}
                    disabled
                  />

                  <label>Description:</label>
                  <textarea
                    className="appDescription"
                    name="task_description"
                    value={editedTask.task_description}
                    onChange={(e) => {
                      setEditedTask({
                        ...editedTask,
                        task_description: e.target.value,
                      });
                    }}
                    disabled
                  />

                  <label>Audit Trail:</label>
                  <textarea
                    className="appDescription"
                    name="task_description"
                    value={editedTask.task_notes}
                    onChange={(e) => {
                      setEditedTask({
                        ...editedTask,
                        task_description: e.target.value,
                      });
                    }}
                    disabled
                  />

                  {/* <label>Note:</label>
                  <textarea
                    className="appDescription"
                    name="task_description"
                    value={editedTask.newNotes}
                    onChange={(e) => {
                      setEditedTask({
                        ...editedTask,
                        newNotes: e.target.value,
                      });
                    }}
                  /> */}

                  {
                    <div key="plan" className="permit-field">
                      <label>Plan: </label>
                      <select
                        name="plan"
                        value={editedTask.task_plan}
                        onChange={(e) => {
                          setEditedTask({
                            ...editedTask,
                            task_plan: e.target.value,
                          });
                        }}
                        disabled
                      >
                        <option key="none" value="none">
                          none
                        </option>
                        {allPlans.map((plan) => {
                          return (
                            <option
                              key={plan.plan_MVP_Name}
                              value={plan.plan_MVP_Name}
                            >
                              {plan.plan_MVP_Name}
                            </option>
                          );
                        })}
                      </select>
                    </div>
                  }

                  {/* <button type="submit">Update</button> */}
                </div>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default KanbanApp;
