import {useEffect, useState} from "react";
import {Link} from "react-router-dom";

function Nav({ currentRoot, tabs }) {
  const [activeTab, setActiveTab] = useState(currentRoot);

  useEffect(() => {
    setActiveTab(currentRoot);
  }, [currentRoot]);

  return (
      <>
        <ul className="nav nav-tabs">
          {tabs.map((tab) => (
              <li className="nav-item" key={tab.id}>
                <Link
                    value={tab.id}
                    className={`nav-link link_font_color ${activeTab === tab.id ? "active" : ""}`}
                    to={tab.url}
                >
                  {tab.title}
                </Link>
              </li>
          ))}
        </ul>
      </>
  );
}

export default Nav;