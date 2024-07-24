import React from 'react';
import Item from './Item';
import profile2 from "css/image/profile2.png";
const LikeSection = () => {
  const items = [
    { imgSrc: profile2, title: 'UaenaCindy', subtitle: 'IU 아이유 - Album\nAlbum · 2021', actions: [{ label: '❤️ 2', onClick: () => {} }] },
    { imgSrc:  profile2, title: 'testnewjeas', subtitle: '나의 노래 5', actions: [{ label: '▶️ 1', onClick: () => {} }, { label: '❤️ 2', onClick: () => {} }, { label: '💬 2', onClick: () => {} }] },
    { imgSrc:  profile2, title: 'testnewjeas', subtitle: 'zz', actions: [{ label: '❤️ 1', onClick: () => {} }] },
  ];

  return (
      <div className="section">
        <div className="section-header">
          <h2>2 likes</h2>
          <button>View all</button>
        </div>
        {items.map((item, index) => (
            <Item key={index} {...item} />
        ))}
      </div>
  );
};

export default LikeSection;