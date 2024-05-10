import "css/profile/profile.css"
import "css/common/tracks.css"
import "css/profile/trackShow.css"
import "css/profile/app.css"
import {USERS_FILE_IMAGE} from "utill/api/ApiEndpoints";

export function Profile({userInfo}) {

  const tag1 = 'ti-tab';
  const tag2 = 'ti-tab ttmid tagpicked';
  const aboutstyle = "no-about";
  const about = null;
  return (
      <div className='track-show-page container justify-content-center l-container'>
        <div className="profile-container col-12">
          <div className='user-show-container '>
            <div className='user-show-image-container'>
              <img src={USERS_FILE_IMAGE + userInfo.pictureUrl}
                   alt="pictureUrl"/>
              {/*{editUser}*/}
            </div>
            <div className='user-show-detail'>
              <div className='user-sd-top'>
                <div className='user-sd-info'>
                  <div className='user-sd-title'>{userInfo.userName}</div>
                  <div className='user-sd-other'>{userInfo.email}</div>
                  {/*<div className='user-sd-other'>{user.location || "vibesphere, Earth"}</div>*/}
                </div>
              </div>
            </div>

          </div>
          <div className='track-show-container-bottom'>
          <span className='track-index-page-container'>
            <div className='track-index-container'>
              <ul className='track-index-tabs'>
                {/*<li className={tag1}><a onClick={()=> this.togglePostTracks()}>Tracks</a></li>*/}
                {/*<li className={tag2}><a onClick={()=> this.togglePostLikes()}>Liked</a></li>*/}
              </ul>
              {/*{tIndex}*/}

            </div>
            <div className="sidebar-placeholder">
              <div className="user-stats">
                <div className="us-track-num">
                  <p>Tracks</p>
                  {/*<p>{parseInt(tracks.length)}</p>*/}
                  <p>{2}</p>
                </div>
                <div className="vertical-line"></div>
                <div className="us-track-num">
                  <p>Liked Tracks</p>
                  {/*<p>{parseInt(likedTracks.length)}</p>*/}
                  <p>2</p>
                </div>
              </div>
              <div className={aboutstyle}>{about}</div>
              <div className="ad-container">
                <a href="https://github.com/Mpompili" target="_blank"><img
                    src="https://res.cloudinary.com/mpompili/image/upload/v1526013412/gotogithub.jpg"
                    alt=""/></a>
              </div>
              <div className="ad-container">
                <a href="https://www.linkedin.com/in/michael-pompili-916a0837/"
                   target="_blank"><img
                    src="https://res.cloudinary.com/mpompili/image/upload/v1526335358/linkedinad.jpg"
                    alt=""/></a>
              </div>
              <div className="extraspace"></div>
            </div>
          </span>
          </div>
        </div>
      </div>
  )
}