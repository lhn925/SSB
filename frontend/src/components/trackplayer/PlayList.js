import React, {useState} from "react";
import "css/playerBar/playList.css"
import "css/playerBar/queue.css"
import "css/playerBar/item-view.css"
// import "css/playerBar/sc.css"
import iu from "css/image/iu.jpg"

export function PlayList({isVisible, changeIsVisible, isPlaying}) {
  return (
      <>
        <div className={isVisible ? "playControls__queue" : "playControls"}>
          <div className="queue m-visible">
            <div className="queue__panel sc-p-2x sc-pb-1x">
              <div
                  className="queue__title sc-font-light sc-type-medium sc-text-h2 sc-px-2x sc-py-1x text-start">
                현재 재생 목록
              </div>
              <button type="button"
                      className="btn queue__clear sc-button sc-button-medium sc-text-h4 sc-px-1.5x sc-py-0.75x sc-mr-1x">Clear
              </button>
              <button type="button"
                      className="btn queue__hide sc-button sc-button-nostyle sc-ir">X
              </button>
            </div>
            <div className="queue__scrollable g-scrollable g-scrollable-v"
                 style={{overflow: 'hidden !important'}}>
              <div className="queue__scrollableInner g-scrollable-inner">
                <div className="queue__itemsHeight sc-px-2x" style={{
                  backgroundSize: '100% 2016px, auto',
                  height: '100%',
                  backgroundPosition: '0px 0px, 0px 0px'
                }}>
                  <div className="queue__itemsContainer">
                    <div className="queue__itemWrapper">
                      <div
                          className="tp-track-dets queueItemView queue__itemsHeight sc-px-2x"
                          style={{
                            // backgroundSize: '100% 2016px, auto',
                            // height: '48px',
                            // backgroundPosition: '0px 0px, 0px 0px'
                          }}>
                        {/*트랙 칸*/}
                        <div className="queueItemView__artwork">
                          <div className="image queueItemView__artworkImage
                          sc-mr-2x image__lightOutline sc-artwork sc-artwork-placeholder-7 m-loaded"
                               style={{height: '32px', width: '32px'}}>
                            <a href="#"><img className="player_cover_img"
                                             src={iu} alt={""}/></a>
                          </div>
                          <div className="queueItemView__playButton">
                            <div className="sc-button-play sc-button"></div>
                            <div className="sc-button sc-button-pause"></div>
                          </div>
                        </div>
                        <div className="queueItemView__details text-start">
                          <div className="queueItemView__meta">
                            <a draggable="true" className="queueItemView__username ply-track-uploader
                            sc-truncate" href="/user-565240779">asya </a>
                            <a draggable="true" className="queueItemView__context
                            sc-text-h4 sc-link-light sc-link-secondary sc-truncate"
                               title="From your history" href="/you/history">From
                              your history</a>
                          </div>
                          <div className="queueItemView__title sc-truncate">
                            <a draggable="true"
                               className="sc-link-dark sc-text-h4 sc-link-primary ply-track-name"
                               href="/user-565240779/midas-touch-kiss-of-life">midas
                              touch - kiss of life</a>
                          </div>
                        </div>
                        <div className="queueItemView__duration sc-text-captions sc-font-light sc-text-light sc-text-secondary">2:49
                        </div>
                        <div className="queueItemView__actions">
                          <button type="button" className="sc-button-like queueItemView__like sc-button sc-button-small sc-button-icon sc-button-nostyle" aria-describedby="tooltip-10505" tabIndex={0} title="Like" aria-label="Like"></button>
                          <button type="button" className="removeFromNextUp queueItemView__remove sc-button sc-button-small sc-button-icon sc-button-nostyle" tabIndex={0} title="Remove from Next up" aria-label="Remove from Next up">
                          </button>
                          <button type="button" className="sc-button-more queueItemView__more sc-button sc-button-small sc-button-icon sc-button-nostyle sc-button-responsive" tabIndex={0} aria-haspopup="true" role="button" aria-owns="dropdown-button-10508" title="More" aria-label="More"></button>
                        </div>

                      </div>
                    </div>
                  </div>

                </div>
                {/* 트랙 칸 끝*/}
              </div>
              <div className="g-scrollbar g-scrollbar-vertical"
                   style={{height: '109.711px', top: '0px'}}></div>
            </div>
            <div className="queue__dragCover"></div>
          </div>
        </div>
      </>
  )
      ;
}
