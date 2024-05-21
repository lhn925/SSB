import React, {useState} from "react";
import "css/playerBar/playList.css"
import "css/playerBar/queue.css"
import "css/playerBar/item-view.css"
import "css/sc_custom.css"
import {DragDropContext, Draggable, Droppable} from "react-beautiful-dnd";
import {USERS_FILE_IMAGE} from "utill/api/ApiEndpoints";
import {
  durationTime, removeFromLocalStorage,
  sorted
} from "utill/function";
import {PLUS} from "content/trackplayer/NumberSignTypes";
import {LOCAL_PLY_KEY, LOCAL_PLY_LOG} from "utill/enum/localKeyEnum";

export function PlayList({
  changeOrder,
  getPlyTrackByTrackId,
  isVisible,
  changeIsVisible,
  updateOrderAndSign,
  isPlaying,
  localPlyInfo,
  trackInfo,
  updateSettings,
  settingsInfo,
  playOrders,
  localPlayLog,
  changePlayLog,
  changePlaying,
  currPlayLog,
  createCurrentPlayLog,
  playerRef,
  toggleLike,
  removePlyByIndex,
  resetCurrTrack,
  variable,
  shuffleOrders,
  setIsDoubleClick,
  resetPlyTrack,
    t
}) {

  const onClickPlayButtonHandler = (e) => {
    const index = Number.parseInt(e.currentTarget.dataset.id);
    const trackEq = index === trackInfo.index;
    let currOrder = index - 1;
    // 현재 셔플 재생이라면
    // Index 위치 값 반환
    if (settingsInfo.shuffle) {
      console.log(playOrders);
      for (let i = 0; i < playOrders.length; i++) {
        const order = playOrders[i];
        if (order === currOrder) {
          currOrder = i;
          break;
        }
      }
    }
    // 선택한 곡이 현재 재생 곡일경우 그리고 재생하고 있을 경우
    // 일시정지후 return
    if (trackEq && isPlaying) {
      changePlaying(false);
      return;
    }
    console.log(currOrder);
    // 만약 재생중이지 않을 경우 PlayLog 확인 후 플레이
    changePlaying(true);
    if (trackEq && !isPlaying) {
      if (currPlayLog.trackId !== -1) {
        return;
      }
      resetPlayedSeconds();
      createCurrentPlayLog(currOrder, PLUS);
      return;
    }
    resetPlayedSeconds();
    // updateSettings("order", currOrder);
    updateOrderAndSign(currOrder, PLUS);
  }

  function resetPlayedSeconds() {
    updateSettings("played", 0);
    updateSettings("playedSeconds", 0);
    if (playerRef.current) {
      playerRef.current.seekTo(0, "seconds");
    }
  }

  const onClickRmBtnHandler = (index) => {
    if (variable.current.isDoubleClick) {
      return;
    }
    setIsDoubleClick(true);
    const currentEq = trackInfo.index === index;
    if (currentEq) {
      changePlaying(false);
      resetCurrTrack();
      resetPlayedSeconds();
      if ((settingsInfo.order + 1) < localPlyInfo.length) {
        updateOrderAndSign(settingsInfo.order + 1, PLUS);
      }
      changePlaying(true);
    } else {

    }
    removePlyByIndex(index);
    setIsDoubleClick(false);
    // 플레이 리스트
  }


  // 전체 클리어
  const onClickClearHandler = () => {
    if (variable.current.isDoubleClick) {
      return;
    }
    setIsDoubleClick(true);
    changePlaying(false);
    resetPlayedSeconds();
    resetCurrTrack();
    resetPlyTrack();
    removeFromLocalStorage(LOCAL_PLY_KEY);
    removeFromLocalStorage(LOCAL_PLY_LOG);
    setIsDoubleClick(false);
    // 플레이 리스트
  }


  return (
      <>
        <div className={isVisible ? "playControls__queue" : "playControls"}>
          <div className="queue m-visible">
            <div className="queue__panel sc-p-2x sc-pb-1x">
              <div
                  className="queue__title sc-font-light sc-type-medium sc-text-h2 sc-px-2x sc-py-1x text-start">
                {t(`msg.player.localPly`)}
              </div>
              <button type="button" onClick={onClickClearHandler}
                      className="btn queue__clear sc-button sc-button-medium sc-text-h4 sc-px-1.5x sc-py-0.75x sc-mr-1x">
                {t(`msg.player.ply.clear`)}
              </button>
              <button type="button"
                      className="btn queue__hide sc-button sc-button-nostyle sc-ir"
                      onClick={() => changeIsVisible(!isVisible)}>X
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
                            localPlyInfo, localPlayLog,
                            changePlayLog, updateSettings)}>
                      <Droppable droppableId="droppable-songs">
                        {(provided) => getDragAndDrop(provided, localPlyInfo,
                            getPlyTrackByTrackId, trackInfo, isPlaying,
                            onClickPlayButtonHandler, toggleLike,
                            onClickRmBtnHandler,t)}


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

function getDragAndDrop(provided, localPlyInfo, getPlyTrackByTrackId, trackInfo,
    isPlaying, onClickPlayButtonHandler, toggleLike, onClickRmBtnHandler,t) {

  const infoTracks = localPlyInfo.map((item) => ({
    ...item, info: getPlyTrackByTrackId(item.id)
  }))

  infoTracks.sort(function (a, b) {
    return sorted(a, b);
  });
  return <div {...provided.droppableProps}
              ref={provided.innerRef}>
    {infoTracks.map((data, index) => (

        data.isStatus !== 0 && <Draggable key={data.index}
                   draggableId={data.index + ""} index={index}>
          {(provided) => (
              <>
                <div ref={provided.innerRef}{...provided.draggableProps}>
                  <div className={"queue__itemWrapper " + (trackInfo.index
                  === data.index ? "current_ply_track" : "")}>
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
                        <div className="queueItemView__playButton"
                             data-id={data.index}
                             onClick={(e) => onClickPlayButtonHandler(e)}>
                          <div className={(data.index === trackInfo.index
                              && isPlaying ? "sc-button-pause " : "sc-button-play ")
                              + "  sc-button"}></div>
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
                             href="/you/history">
                            {t(`msg.player.ply.history`)}</a>
                        </div>
                        <div
                            className="queueItemView__title sc-truncate">
                          <a draggable="true"
                             className="sc-link-dark sc-text-h4 sc-link-primary ply-track-name "
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
                                    onClick={() => toggleLike(data.info.id)}
                                    title="Like"
                                    aria-label="Like"></button>
                        }
                        <button type="button"
                                className="removeFromNextUp queueItemView__remove sc-button sc-button-small sc-button-icon sc-button-nostyle"
                                tabIndex={0}
                                onClick={() => onClickRmBtnHandler(data.index)}
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
