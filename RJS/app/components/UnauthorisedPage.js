import React from "react";
import { Link } from "react-router-dom";

function UnathourisedPage() {
  return (
    <div className="container">
      <div>You are not authorised!</div>
      <Link to="/home">Return to Home!</Link>
    </div>
  );
}

export default UnathourisedPage;
