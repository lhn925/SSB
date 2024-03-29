import {useDrag, useDrop} from "react-dnd";
import {memo} from "react";

const style = {
  border: '1px solid #e5e5e5',
  margin: '0.2rem',
  width: '100%',
  padding: '1rem 1rem',
  backgroundColor: 'white',
  cursor: 'pointer',
  flexShrink:"0"
}
const handleStyle = {
  backgroundColor: 'green',
  width: '1rem',
  height: '1rem',
  display: 'inline-block',
  marginRight: '0.75rem',
  cursor: 'move',
}

export const DnDTracksBox = memo(
    function DnDTracksBox({id, track, moveCard, findCard}) {
      // Card의 id로 원래 인덱스를 찾기
      const originalIndex = findCard(id).index;

      // drag 여부를 판별하는 isDragging과 drag할 요소에 부착할 ref를 받음
      /*  const [{isDragging}, dragRef] = useDrag(
            () => ({
              // drag할 요소의 type을 지정
              type: "CARD",
              // Container에서 props로 넘겨준 요소의 id와 id를 가지고 state 내의 실제 index를
              // Card가 사용할 수 있도록 넘겨준다.
              item: {order, originalIndex},
              // collect 옵션을 넣지 않으면 dragging 중일 때 opacity가 적용되지 않는다!
              collect: (monitor) => ({
                // isDragging 변수가 현재 드래깅중인지 아닌지를 true/false로 리턴한다
                isDragging: monitor.isDragging(),
              }),

            }),
            [originalIndex],
        )*/

      const [{isDragging}, dragRef,preview] = useDrag(() => ({
        // drag할 요소의 type을 지정
        type: "CARD",
        // Container에서 props로 넘겨준 요소의 id와 id를 가지고 state 내의 실제 index를
        // Card가 사용할 수 있도록 넘겨준다.
        item: {id, originalIndex},
        collect: (monitor) => ({
          // isDragging 변수가 현재 드래깅중인지 아닌지를 true/false로 리턴한다
          isDragging: monitor.isDragging(),
        }),
        // 드래그가 완전히 끝났을때 실행됩니다. 보통 여기에서 Redux dispatch를 많이 실행시킵니다.
        end: (item) => {

        },

      }))
      const [, dropRef] = useDrop(
          () => ({
            // CARD 타입만 허용. 즉 useDrag와 타입이 다르면 아무 일도 일어나지 않음!
            accept: "CARD",
            // 요소를 드래그해서 다른 요소 위에서 hover할 때 자신이 아니면 위치를 바꿈!
            // useDrag에서 item으로 지정한 id와 index를 가지고 위치를 교환!
            hover({id: draggedId}) {
              if (draggedId !== id) {
                // draggedId 와 id
                // hover된 요소와 index 교환! -> 위치 교환
                // order 교환
                const {card,index: overIndex} = findCard(id);
                const {draggedCard,index: dragIndex} = findCard(draggedId);
                moveCard(draggedId,overIndex);
              }
            },
          }),
          [findCard, moveCard],
      )
      return (
          // dragRef와 dropRef 장착
          <div
               ref={preview}
               className="orderBox"
               style={{...style, opacity: isDragging ? 0.4 : 1}}>
              <div ref={(node) => dragRef(dropRef(node))}
                  className="compactUpload__dragHandle"
                  style={handleStyle}/>
              <input data-id={track.token} className="form-control" type="text"
                     name="title" defaultValue={track.title.value}/>
          </div>
      )
    })