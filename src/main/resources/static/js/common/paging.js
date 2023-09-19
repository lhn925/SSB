function Paging(pageData , displayPageNum) {
  this.pageable = new Array(pageData.pageable);
  this.pageSize = this.pageable[0].pageSize; // size
  this.pageNumber = this.pageable[0].pageNumber; // 현재페이지 넘버
  this.offset = this.pageable[0].offset; // offset
  this.totalElements = pageData.totalElements; // 갖고 온 log 수
  this.totalPages = pageData.totalPages; // 전체 페이지 수
  this.nextPage = false;
  this.prevPage = false;
  this.displayPageNum = displayPageNum;
  this.isFirst = pageData.first; // 여기가 첫번 째 페이지 확인
  this.isLast = pageData.last;// 여기가 마지막 페이지 확인
}

Paging.prototype._pagingInnerHtml = function ($elementId) {
  $elementId.innerHTML = "";// 초기화

  // 보여줄 페이지 사이즈
  let currentPage = this.pageNumber + 1; //현재 페이지 + 1
  // 실제로 유저에게 보여주는 pageNumber


  // 전체페이지수가 10 을 못 넘으면 그대로 반환

  // en
  //처음 페이지 넘버
  let parseNumber = parseInt(((currentPage - 1) / this.displayPageNum));
  let startNumber = parseNumber * this.displayPageNum + 1;

  // 마지막 페이지 넘버
  let parseNumber2 = parseInt(((currentPage - 1) / this.displayPageNum + 1));
  let endNumber = parseNumber2 * this.displayPageNum;

  // endNumber가 큰 경우? 전체페이지 수 저장
  if (endNumber > this.totalPages) {
    endNumber = this.totalPages;
  }

  // 이전페이지 존재 여부 있으면 True , 없으면 false
  this.prevPage = startNumber != 1;
  // 다음 페이지 존재 여부  있으면 True , 없으면 false
  this.nextPage = endNumber != this.totalPages;

  if (this.prevPage) {
    $elementId.innerHTML += "<li data-id='" + (startNumber - (this.displayPageNum+1) )
        + "' class=\"page-item\">\n"
        + "<a class=\"page-link mainColor pagingView\" id=\"pagingPrev\" href=\"#\">"
        + messages["page.prevBtn"] + "</a>\n"
        + "</li>";
  }

  for (let i = startNumber; i <= endNumber; i++) {
    let className = "page-link text-dark";
    let selected = "notSelected";
    let dataId = i - 1;
    // 현재페이지버튼에 색 추가
    if (dataId == this.pageNumber) {
      className += " bg-gray-300";
      selected = "selected";
    }
    $elementId.innerHTML += "<li data-id='" + dataId
        + "' class='page-item page-list " + selected + "'><a class='"
        + className + "' href='#'>"
        + i + "</a></li>";
  }

  if (this.nextPage) {
    $elementId.innerHTML += "<li data-id='" + (endNumber)
        + "' class=\"page-item\">\n"
        + "<a class=\"page-link mainColor pagingView\" href=\"#\" id=\"pagingNext\">"
        + messages["page.nextBtn"] + "</a>\n"
        + "</li>";
  }
}