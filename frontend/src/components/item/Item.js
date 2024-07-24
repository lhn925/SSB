import React from 'react';

const Item = ({ imgSrc, title, subtitle, actions }) => {
  return (
      <div className="item">
        <img src={imgSrc} alt={title} />
        <div className="item-info">
          <div><strong>{title}</strong></div>
          <div>{subtitle}</div>
        </div>
        <div className="item-actions">
          {actions.map((action, index) => (
              <button key={index} onClick={action.onClick}>
                {action.label}
              </button>
          ))}
        </div>
      </div>
  );
};

export default Item;