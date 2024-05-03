import React, {useState} from "react";
import "css/playerBar/playList.css"
import "css/playerBar/queue.css"
import "css/playerBar/item-view.css"
import "css/sc_custom.css"
// import "css/playerBar/sc.css"
import iu from "css/image/iu.jpg"
import {DragDropContext, Draggable, Droppable} from "react-beautiful-dnd";
import {ProgressBar} from "../progressBar/ProgressBar";
import {BtnOutLine} from "../button/BtnOutLine";
import {USERS_FILE_IMAGE} from "utill/api/ApiEndpoints";
import {durationTime} from "utill/function";

export function PlayList({
  changeOrder,
  getPlyTrackByTrackId,
  isVisible,
  changeIsVisible,
  isPlaying,
  localPlyInfo,
  trackInfo,
  updateSettings,
  settingsInfo,
  localPlayLog,
  changePlayLog,
}) {
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
                    {<DragDropContext
                        onDragEnd={(e) => handleOnDragEnd(e, changeOrder,
                            localPlyInfo,   localPlayLog,
                            changePlayLog, updateSettings)}>
                      <Droppable droppableId="droppable-songs">
                        {(provided) => getDragAndDrop(provided, localPlyInfo,
                            getPlyTrackByTrackId)}


                      </Droppable>
                    </DragDropContext>
                    }

                  </div>
                </div>


                {/* 트랙 칸 끝*/}
              </div>
              {/*<div className="g-scrollbar g-scrollbar-vertical"*/
              }
              {/*     style={{height: '109.711px', top: '0px'}}></div>*/
              }
            </div>
            {/*<div className="queue__dragCover"></div>*/
            }
          </div>
        </div>
      </>
  )
      ;
}

function getDragAndDrop(provided, localPlyInfo, getPlyTrackByTrackId) {

  const infoTracks = localPlyInfo.map((item) => ({
    ...item, info: getPlyTrackByTrackId(item.id)
  }))
  return <div {...provided.droppableProps}
              ref={provided.innerRef}>
    {infoTracks.map((data, index) => (
        <Draggable key={data.index}
                   draggableId={data.index + ""} index={index}>
          {(provided) => (
              <>
                <div ref={provided.innerRef}{...provided.draggableProps}>
                  <div className="queue__itemWrapper">
                    <div
                        className="tp-track-dets queueItemView queue__itemsHeight sc-px-2x">
                      <div
                          className="queueItemView__dragHandle"{...provided.dragHandleProps}>
                      </div>
                      <div className="queueItemView__artwork">
                        <div className="image queueItemView__artworkImage
                          sc-mr-2x image__lightOutline sc-artwork sc-artwork-placeholder-7 m-loaded"
                             style={{
                               height: '32px',
                               width: '32px'
                             }}>
                          <a href="#">
                            <img style={{backgroundColor: '#fff'}}
                                 className="player_cover_img"
                                 src={data.info.coverUrl
                                     && USERS_FILE_IMAGE
                                     + data.info.coverUrl} alt="cover"/>
                          </a>
                        </div>
                        <div
                            className="queueItemView__playButton">
                          {/*<div className="sc-button-play sc-button"></div>*/}
                          <div
                              className="sc-button sc-button-pause"></div>
                        </div>
                      </div>
                      <div
                          className="queueItemView__details text-start">
                        <div className="queueItemView__meta">
                          <a draggable="true" className="queueItemView__username ply-track-uploader
                            sc-truncate"
                             href="/user-565240779">{data.info.postUser.userName} </a>
                          <a draggable="true" className="queueItemView__context
                            sc-text-h4 sc-link-light sc-link-secondary sc-truncate"
                             title="From your history"
                             href="/you/history">From
                            your history</a>
                        </div>
                        <div
                            className="queueItemView__title sc-truncate">
                          <a draggable="true"
                             className="sc-link-dark sc-text-h4 sc-link-primary ply-track-name"
                             href="/user-565240779/midas-touch-kiss-of-life">
                            {data.info.title}
                          </a>
                        </div>
                      </div>
                      <div
                          className="queueItemView__duration sc-text-captions sc-font-light sc-text-light sc-text-secondary">
                        {durationTime(data.info.trackLength)}
                      </div>
                      <div className="queueItemView__actions">
                        {
                            !data.info.isOwner &&
                            <button type="button"
                                    className={"sc-button-like"
                                        + (data.info.isLike ? "-t" : "") +
                                        " queueItemView__like sc-button sc-button-small sc-button-icon sc-button-nostyle"}
                                    aria-describedby="tooltip-10505"
                                    tabIndex={0}
                                    title="Like"
                                    aria-label="Like"></button>
                        }
                        <button type="button"
                                className="removeFromNextUp queueItemView__remove sc-button sc-button-small sc-button-icon sc-button-nostyle"
                                tabIndex={0}
                                title="Remove from Next up"
                                aria-label="Remove from Next up">
                        </button>
                        <button type="button"
                                className="sc-button-more queueItemView__more sc-button sc-button-small sc-button-icon sc-button-nostyle sc-button-responsive"
                                tabIndex={0}
                                aria-haspopup="true"
                                role="button"
                                aria-owns="dropdown-button-10508"
                                title="More"
                                aria-label="More"></button>
                      </div>
                    </div>
                  </div>
                </div>
              </>
          )}
        </Draggable>
    ))}
    {provided.placeholder}
  </div>;
}

function handleOnDragEnd(result,
    changeOrder,
    localPlyInfo,
    localPlayLog,
    changePlayLog,
    updateSettings) {
  if (!result.destination) {
    return;
  }
  const prevIndex = result.source.index;
  const currIndex = result.destination.index;
  // 현재 재생 로그
  // 현재 order

  // // 0.....7.8
  const items = localPlyInfo.map((data) => ({...data}));
  const [reorderedItem] = items.splice(prevIndex, 1);
  items.splice(currIndex, 0, reorderedItem);

  // 원래 있던 위치가 변경한 위치보다 크다면
  const minIndex = Math.min(prevIndex, currIndex);
  const maxIndex = Math.max(prevIndex, currIndex);

  // 최소한의 범위에서 인덱스 업데이트
  for (let i = minIndex; i <= maxIndex; i++) {
    items[i].index = i + 1; // 인덱스 재조정
  }


  // 만약 현재 재생하고 있는 위치에 변화가 생긴다면
  changeOrder(items);
}
