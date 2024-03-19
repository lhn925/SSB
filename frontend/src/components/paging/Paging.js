/**
 *
 *
 *
 *    empty: false,
 *     first: false,
 *     last: false,
 *     size: 페이지 단위
 *     numberOfElements: 0, // 현재 갖고온 데이터 수
 *     pageNumber: 0, // 현재 offset
 *     totalElements: 0,// 전체 데이터 수
 *     totalPages: 0 // 전체 페이지수
 *
 * @param pageable
 * @param offset
 * @param setOffSet
 * @param t
 * @returns {JSX.Element}
 * @constructor
 */
import {Link} from "react-router-dom";

export function Paging({pageable, offset, setOffSet, t}) {
    let currentPage = pageable.pageNumber + 1;
    // 처음페이지 Number
    let parseNumber = parseInt(((currentPage - 1) / pageable.size));
    let startNumber = parseNumber * pageable.size + 1;

    // 마지막 페이지 넘버
    let parseNumber2 = parseInt(((currentPage - 1) / pageable.size + 1));
    let endNumber = parseNumber2 * pageable.size;

    // endNumber가 큰 경우? 전체페이지 수 저장
    if (endNumber > pageable.totalPages) {
      endNumber = pageable.totalPages;
    }
    // 이전페이지 존재 여부 있으면 True , 없으면 false
    let prevPage = startNumber !== 1;
    // 다음 페이지 존재 여부  있으면 True , 없으면 false
    let nextPage = endNumber !== pageable.totalPages;

    const list = Array.from({length: endNumber - startNumber + 1},
        (_, index) => startNumber + index - 1);

    const onClink = (e) => {
      const dataId = parseInt(e.currentTarget.dataset.id);
      if (dataId !== offset) {
        setOffSet(dataId);
      }
    }
    return (
        <>
          <nav aria-label="Page navigation example" className="text-center">
            <ul className="pagination justify-content-center"
                id="userManagePaging">
              {
                  prevPage && <li className="page-item"
                                  onClick={onClink}
                                  data-id={startNumber - (pageable.size + 1)}>
                    <a className="page-link
                  mainColor" id="pagingPrev" href="#">{t(
                        `msg.page.prevBtn`)}</a></li>
              }
              {
                list.length !== 0 ? list.map((dataId, index) => {

                  let aTabValue = "page-link text-dark";
                  const isSelected = dataId === offset;
                  if (isSelected) {
                    aTabValue += " bg-gray-300";
                  }
                  return (
                      <li key={index}
                          data-selected={isSelected}
                          className={"page-item " + (isSelected && "selected")}
                          data-id={dataId}
                          onClick={onClink}
                      >
                        <Link className={aTabValue} to="#">{dataId + 1}</Link>
                      </li>
                  )
                }):<li className="page-item "><a className="page-link text-dark" href="#">1</a></li>
              }
              {
                  nextPage && <li onClick={onClink} className="page-item"
                                  data-id={endNumber}>
                    <a className="page-link
                  mainColor" id="pagingPrev" href="#">{t(
                        `msg.page.nextBtn`)}</a></li>
              }
            </ul>
          </nav>
        </>
    )
}