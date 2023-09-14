function Paging(pageData) {
  this.pageable = new Array(pageData.pageable);
  this.pageSize = this.pageable[0].pageSize; // size
  this.pageNumber = this.pageable[0].pageNumber; // 현재페이지 넘버
  this.offset = this.pageable[0].offset; // offset
  this.totalElements = pageData.totalElements; // 갖고 온 log 수
  this.totalPages = pageData.totalPages; // 전체 페이지 수
  this.nextPage = false;
  this.prevPage = false;
  this.isFirst = pageData.first; // 여기가 첫번 째 페이지 확인
  this.isLast = pageData.last;// 여기가 마지막 페이지 확인
}

Paging.prototype._pagingInnerHtml = function ($elementId) {
  $elementId.innerHTML = "";// 초기화
  console.log(this.totalPages); //
  console.log(this.pageNumber); // 0 , 1, 2

  let $pagingPrev = $elementId.querySelector("#pagingPrev");
  let $pagingNext = $elementId.querySelector("#pagingNext");

  // 1 2 3 4 5 6 7 8 9 10
  // 11 12 13 14 15 16 17 18 19 20
  // 전체 유닛 페이지 수 / 10 + 1 -> 페이지 하단 넘버 10 씩 보여줌

  // 전체페이지수가 10 을 못 넘으면 그대로 반환

  let unitCount = ((this.totalPages - 1) / 10) + 1;

  // en
  //처음 페이지 넘버
  let startNumber = (this.pageNumber / this.pageSize) * this.pageSize + 1;

  // 마지막 페이지 넘버
  let endNumber = startNumber + this.pageSize - 1;

  // endNumber가 큰 경우? 전체페이지 수 저장
  if (endNumber > this.totalPages) {
    endNumber = this.totalPages;
  }

  // 이전페이지 존재 여부 있으면 True , 없으면 false
  this.prevPage = startNumber != 1;
  // 다음 페이지 존재 여부  있으면 True , 없으면 false
  this.nextPage = (endNumber * this.pageSize) < this.totalElements;



  if (this.prevPage) {
    $elementId.innerHTML += "<li data-id='"+ (startNumber - 11) +"' class=\"page-item\">\n"
        + "<a class=\"page-link mainColor pagingView\" id=\"pagingPrev\" href=\"#\">"+messages["page.prevBtn"]+"</a>\n"
        + "</li>";
  }

  for (let i = startNumber; i <= endNumber; i++) {
    let className = "page-link text-dark";
    let selected = "notSelected";
    let dataId = startNumber - 1;

    if (dataId == this.pageNumber) {
      className += " bg-gray-300";
      selected = "selected";
    }
    $elementId.innerHTML += "<li data-id='"+dataId+"' class='page-item page-list "+selected+"'><a class='"+className+"' href='#'>"
        +startNumber + "</a></li>";
    startNumber += 1;
  }

  if ( this.nextPage) {
    $elementId.innerHTML += "<li data-id='"+ (endNumber) +"' class=\"page-item\">\n"
        + "<a class=\"page-link mainColor pagingView\" href=\"#\" id=\"pagingNext\">"+messages["page.nextBtn"]+"</a>\n"
        + "</li>";
  }
}