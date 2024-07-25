import React from 'react';
import PropTypes from 'prop-types';
import profile2 from "../../css/image/profile2.png";

const SidebarModule = ({items, headerLink, headerTitle, type}) => {
  return (
      <article className="sidebar-module" style={{height: "auto"}}>
        <a className="sidebar-header" src={headerLink}>
          <h3 className="sidebarHeader__title">
            <span className={"sc-icon sc-icon-" + type
                + " sc-icon-large sidebarHeader__icon"}></span>
            <span className="sidebarHeader__actualTitle">{headerTitle}</span>
          </h3>
          <span
              className="sidebarHeader__more sc-type-h3 sc-text-h4">t)View all</span>
        </a>
        <div className="sidebarContent">
          <div className="soundBadgeList compact lazyLoadingList">
            <ul className="sc-list-nostyle sc-clearfix"
                style={{textAlign: "left", padding: 0}}>

            </ul>
          </div>
        </div>
      </article>
  );
};

const SidebarByType = (items, type) => {
  if (type === "like") {
    return (<>
          {
            items.map((item, index) => {
              return <li className="soundBadgeList__item" key={item.detail.trackInfo.id} >

                <div
                    className="soundBadge compact sc-media playlist m-playable">
                          <span
                              className="soundBadge__artwork sc-mr-2x sc-media-image"
                              style={{float: "left", marginRight: `10px`}}>
                            <div
                                className="image image__lightOutline sc-artwork sc-artwork-placeholder-7 m-loaded"
                                style={{height: `50px`, width: `50px`}}>
                              <a href="#">
                            <img style={{backgroundColor: '#fff'}}
                                 className="player_cover_img"
                                 src={item.detail.trackInfo.coverUrl} alt="cover"/>
                          </a>
                            </div>
                              <span className="soundBadge__playButton">
                                <a role="button" href=""
                                   className="sc-button-play playButton sc-button sc-button-large"
                                   tabIndex="0" title="Play"
                                   draggable="true">t)Play</a>
                              </span>
                          </span>

                  <div
                      className="sc-media-content soundBadge__content">
                    <div
                        className="soundTitle sc-clearfix sc-hyphenate sc-type-h3 sc-text-h4">
                      <div className="soundTitle__titleContainer">
                        <div
                            className="soundTitle__usernameTitleContainer sc-mb-0.5x">
                          <div
                              className="sc-type-light sc-text-secondary sc-text-h4 soundTitle__secondary sc-truncate">
                            <a href="/cindy-lim-82312455"
                               className="soundTitle__username sc-link-secondary sc-link-light text-decoration-none">
                                        <span
                                            className="soundTitle__usernameText">{item.userName}</span>
                            </a>
                          </div>
                          <a className="sc-link-primary soundTitle__title sc-link-dark sc-text-h4 text-decoration-none "
                             href="/cindy-lim-82312455/sets/iu-1"
                             title={item.detail.trackInfo.title}>
                            <span className="sc-truncate">{item.detail.trackInfo.title}</span>
                          </a>
                        </div>
                        <div
                            className="soundTitle__additionalContainer sc-ml-1.5x"></div>
                      </div>
                    </div>


                    <ul className="soundStats sc-ministats-group"
                        aria-label="Track stats">
                      <li title="8,440 plays"
                          className="sc-ministats-item">
                              <span
                                  className="sc-ministats sc-ministats-small  sc-ministats-plays sc-text-secondary">
                                <span
                                    className="sc-visuallyhidden">{item.detail.playCount} plays</span>
                                <span
                                    aria-hidden="true">{item.detail.playCount}</span>
                              </span>
                      </li>

                      <li title="102 likes" className="sc-ministats-item">
                        <a href="/cindy-lim-82312455/the-thing-i-do-slowly/likes"
                           rel="nofollow"
                           className="sc-ministats sc-ministats-small  sc-ministats-likes sc-link-secondary">
                          <span
                              className="sc-visuallyhidden">View all likes</span><span
                            aria-hidden="true">{item.detail.trackInfo.likeCount}</span>
                        </a>
                      </li>

                      <li title="1 repost" className="sc-ministats-item">
                        <a href="/cindy-lim-82312455/the-thing-i-do-slowly/reposts"
                           rel="nofollow"
                           className="sc-ministats sc-ministats-small  sc-ministats-reposts sc-link-secondary">
                          <span
                              className="sc-visuallyhidden">View all reposts</span><span
                            aria-hidden="true">{item.detail.trackInfo.repostCount}</span>
                        </a>
                      </li>
                      <li title="1 comment"
                          className="sc-ministats-item">
                        <a href="/cindy-lim-82312455/the-thing-i-do-slowly/comments"
                           rel="nofollow"
                           className="sc-ministats sc-ministats-small  sc-ministats-comments sc-link-secondary">
                          <span
                              className="sc-visuallyhidden">View all comments</span><span
                            aria-hidden="true">{item.detail.trackInfo.replyCount}</span>
                        </a>
                      </li>
                    </ul>

                    <div className="soundBadge__additional">
                      <div className="soundBadge__actions">
                        <div
                            className="soundActions sc-button-toolbar soundActions__small">
                          <div
                              className="sc-button-group sc-button-group-small">
                            <button type="button"
                                    className="sc-button-like sc-button-secondary sc-button sc-button-small sc-button-icon sc-button-responsive sc-button-selected"
                                    aria-describedby="tooltip-10265"
                                    tabIndex="0" title="Unlike"
                                    aria-label="Unlike">t)Liked
                            </button>
                            <button type="button"
                                    className="sc-button-more sc-button-more sc-button sc-button-small sc-button-icon sc-button-responsive"
                                    tabIndex="0" aria-haspopup="true"
                                    role="button"
                                    aria-owns="dropdown-button-10267"
                                    title="More"
                                    aria-label="More">t)More
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>


                </div>


              </li>
            })
          }
        </>
    )

  }

}

export default SidebarModule;