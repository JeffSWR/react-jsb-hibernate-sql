import React, { useState, useEffect, useContext } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import LoginContext from "../../LoginContext";
import axios from "axios";

function CreatePlan() {
  const { checkGroup, checkTokenLogin, setIsAdmin } = useContext(LoginContext);
  const navigate = useNavigate();
  const appAcronym = useParams().application;
  let [showComponent, setShowComponent] = useState(false);

  //---retrieve all plans
  let [allPlans, setAllPlans] = useState([]);

  const getAllPlans = async function () {
    const response = await axios.post("/getAllPlans", {
      app: appAcronym,
    });

    setAllPlans(response.data.plans);
  };

  //----create plan
  let [newPlan, setNewPlan] = useState({
    plan_MVP_Name: "",
    plan_start_date: "",
    plan_end_date: "",
    plan_app_Acronym: appAcronym,
  });
  const [showCreatePlanModal, setShowCreatePlanModal] = useState(false);
  const toggleShowCreatePlanModal = () => {
    setShowCreatePlanModal(!showCreatePlanModal);
  };

  async function handleSubmitCreatePlan(e) {
    e.preventDefault();
    console.log(newPlan);
    try {
      const response = await axios.post("createPlan", newPlan);
      if (response.data.e) {
        if (response.data.e.errno == 1062) alert("Duplicate entry!");
      } else {
        alert("successfully created new plan");
        setNewPlan({
          plan_MVP_Name: "",
          plan_start_date: "",
          plan_end_date: "",
          plan_app_Acronym: appAcronym,
        });
        await getAllPlans();
      }
    } catch (error) {
      console.log(e);
    }
  }

  const handleNewChange = (e) => {
    const { name, value } = e.target;
    setNewPlan((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  //----edit plan
  const [editedPlan, setEditedPlan] = useState({});
  const [showEditPlanModal, setShowEditPlanModal] = useState(false);
  const toggleShowEditPlanModal = () => {
    setShowEditPlanModal(!showEditPlanModal);
  };

  async function handleSubmitEditedPlan(e) {
    e.preventDefault();
    console.log(editedPlan);
    try {
      await axios.post("/updatePlan", editedPlan);
      alert("successfully edited plan");
      await getAllPlans();
      toggleShowEditPlanModal();
    } catch (error) {
      console.log(e);
    }
  }

  async function handleEditButton(e) {
    const response = await axios.post("/getOnePlan", {
      plan_MVP_Name: e.target.value,
      plan_app_Acronym: appAcronym,
    });
    let planObject = response.data.plan;

    Object.keys(planObject).forEach((key) => {
      if (planObject[key] === null) {
        planObject[key] = "";
      }
    });

    if (planObject.plan_start_date != "") {
      planObject = {
        ...planObject,
        plan_start_date: planObject.plan_start_date.slice(0, 10),
      };
    }

    if (planObject.plan_end_date != "") {
      planObject = {
        ...planObject,
        plan_end_date: planObject.plan_end_date.slice(0, 10),
      };
    }

    console.log(planObject);
    setEditedPlan(planObject);
    toggleShowEditPlanModal();
  }

  const handleEditedChange = (e) => {
    const { name, value } = e.target;
    setEditedPlan((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  useEffect(() => {
    setShowComponent(true);
    getAllPlans();
  }, []);

  return (
    showComponent && (
      <div>
        <div className="container">
          <h4>
            {"Manage " + appAcronym + "'s Plans"}
            <button className="nav-button" onClick={toggleShowCreatePlanModal}>
              Create Plan
            </button>
            <div
              className="create-forms-back-button"
              onClick={() => {
                navigate(`/applications/${appAcronym}`);
              }}
            >
              Back
            </div>
          </h4>
          {/* <div>
            <h3 className="group-table-header col-12">Current Plans</h3>
          </div> */}
          <div className="create-app-table">
            <table>
              <thead>
                <tr>
                  <th>Name</th>
                  {/* <th>Start Date</th>
                  <th>End Date</th> */}
                  <th>Edit</th>
                </tr>
              </thead>
              <tbody>
                {allPlans.map((plan) => {
                  return (
                    <tr key={plan.plan_MVP_Name}>
                      <td>{plan.plan_MVP_Name}</td>
                      {/* <td>{plan.plan_start_date}</td>
                      <td>{plan.plan_end_date}</td> */}
                      <td>
                        <button
                          onClick={handleEditButton}
                          id="user-button"
                          // value={user.username}
                          value={plan.plan_MVP_Name}
                        >
                          Edit Plan
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>

        {showCreatePlanModal && (
          <div key="create-plan-modal" className="modal">
            <div className="modal-content">
              <form onSubmit={handleSubmitCreatePlan} className="edit-app-form">
                <div className="edit-app-form">
                  <h2>Create Plan</h2>
                  <button
                    className="close-button"
                    onClick={(e) => {
                      e.preventDefault();
                      toggleShowCreatePlanModal();
                    }}
                  >
                    &times;
                  </button>

                  <div>
                    <label>Plan Name:</label>
                    <input
                      type="text"
                      name="plan_MVP_Name"
                      value={newPlan.plan_MVP_Name}
                      onChange={handleNewChange}
                      required
                    />
                    <div className="date">
                      <label>Start Date:</label>
                      <input
                        className="date-input"
                        type="date"
                        name="plan_start_date"
                        value={newPlan.plan_start_date}
                        onChange={handleNewChange}
                      />
                      <label>End Date:</label>
                      <input
                        className="date-input"
                        type="date"
                        name="plan_end_date"
                        value={newPlan.plan_end_date}
                        onChange={handleNewChange}
                      />
                    </div>

                    <button type="submit">Create</button>
                  </div>
                </div>
              </form>
            </div>
          </div>
        )}

        {showEditPlanModal && (
          <div key="create-plan-modal" className="modal">
            <div className="modal-content">
              <form onSubmit={handleSubmitEditedPlan} className="edit-app-form">
                <div className="edit-app-form">
                  <h2>Edit Plan</h2>
                  <button
                    className="close-button"
                    onClick={(e) => {
                      e.preventDefault();
                      toggleShowEditPlanModal();
                    }}
                  >
                    &times;
                  </button>

                  <div>
                    <label>Plan Name:</label>
                    <input
                      type="text"
                      name="plan_MVP_Name"
                      value={editedPlan.plan_MVP_Name}
                      onChange={handleEditedChange}
                      disabled
                    />
                    <div className="date">
                      <label>Start Date:</label>
                      <input
                        className="date-input"
                        type="date"
                        name="plan_start_date"
                        value={editedPlan.plan_start_date}
                        onChange={handleEditedChange}
                      />
                      <label>End Date:</label>
                      <input
                        className="date-input"
                        type="date"
                        name="plan_end_date"
                        value={editedPlan.plan_end_date}
                        onChange={handleEditedChange}
                      />
                    </div>

                    <button type="submit">Edit</button>
                  </div>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    )
  );
}

export default CreatePlan;
