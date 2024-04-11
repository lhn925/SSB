import {Mention, MentionsInput} from "react-mentions";
import {useEffect, useState} from "react";
import defaultStyle from "components/mention/defaultStyle";
import {merge} from "components/mention/merge";
import defaultMentionStyle from "components/mention/defaultMentionStyle";
import SearchTagsApi from "utill/api/tags/SearchTagsApi";
import {debounce} from "lodash";
import emojiRegex from "emoji-regex";
import {ChangeError} from "../../utill/function";

export function CustomTagMention({
    t,
  setSearchTagList,
  searchTagList,
  storeTagList,
  token,
    setErrors,
  addTagListEvent
}) {

  const LIMIT = 30;
  const [value, setValue] = useState('');
// 호출수 제안
  const debouncedFetchTagsSuggestions = debounce((search, callback) =>
          fetchTagsSuggestions(search, callback, setSearchTagList, searchTagList),
      500);
  // mem 캐싱

  const getStoreValue = () => {
    let storeStr = '';
    storeTagList.map((data) => {
      storeStr += "#[" + data.tag + "](" + data.id + ") ";
    });
    return storeStr;
  }

  useEffect(() => {
    const storeValue = getStoreValue();
    setValue(storeValue);
  }, [])

  const regex = emojiRegex();
  const onTagChangeEvent = (e) => {
    let value = e.target.value;

    for (const regex1 of value.matchAll(regex)) {
      if (regex1) {
        return;
      }
    }
    const newTagsList = extractTagsAndIds(value);

    const uniqueTags = new Set();
    const uniqueArray = newTagsList.filter(item => {
      if (!uniqueTags.has(item.tag)) {
        uniqueTags.add(item.tag);
        return true;
      }
      return false;
    });
    // 30개 제한
    if (uniqueArray.length > LIMIT) {
      ChangeError(setErrors, "tags", t(`msg.track.upload.tag.limit`), true);
      return;
    }
    ChangeError(setErrors, "tags", "", false);
    addTagListEvent(uniqueArray,token);

    uniqueArray.forEach((data) => {
      const pattern = new RegExp(`\\#\\[${data.tag}\\]\\(${data.id}\\)`,"g");
      const matches = [...value.matchAll(pattern)];
      if (matches.length > 1) {
        const lastMatch = matches[matches.length - 1];
        const startIndex = lastMatch.index;
        const endIndex = startIndex + lastMatch[0].length;
        // 마지막 인스턴스 앞과 뒤로 문자열을 분할하여 중간의 마지막 인스턴스를 제거
        value = value.substring(0, startIndex) + value.substring(
            endIndex);
      }
    });
    setValue(value);
  }

  function extractTagsAndIds(str) {
    // 정규 표현식을 사용하여 #[태그](아이디) 패턴을 찾습니다.
    const regex = /\[(.*?)\]\((\d+)\)/g;
    let matches;
    const data = []
    while ((matches = regex.exec(str)) !== null) {
      // matches[1]은 태그, matches[2]는 아이디에 해당합니다.
      data.push({tag: matches[1], id: parseInt(matches[2], 10)});
    }
    return data;
  }

/*  const onAdd = (id, display) => {
    const newTag = {id: id, tag: display};

    addTag(newTag, storeTagList, playListType, addTagListEvent, setValue,
        token);
  }*/
  let style = merge({}, defaultStyle, {
    input: {
      overflow: 'auto',
      height: 60,
    },
    highlighter: {
      boxSizing: 'border-box',
      overflow: 'hidden',
      height: 70,
    },
  })
  return (
      <div className="scrollable">
        <MentionsInput
            value={value}
            name="tags"
            onChange={onTagChangeEvent}
            style={style}
            placeholder={t(`msg.track.upload.tags.describe`)}
            a11ySuggestionsListLabel={'Suggested mentions'}
        >
          <Mention
              markup="#[__display__](__id__)"
              displayTransform={(id, display) => `#${display}`}
              trigger="#"
              data={debouncedFetchTagsSuggestions}
              renderSuggestion={(suggestion, search, highlightedDisplay) => (
                  <div className="user">{highlightedDisplay}</div>
              )}
              appendSpaceOnAdd={true}
              // onAdd={onAdd}
              style={defaultMentionStyle}
          />
        </MentionsInput>
      </div>
  )
}

function fetchTagsSuggestions(search, callback, setSearchTagList,
    searchTagList) {
  if (!search) {
    return [];
  }
  // 전에 search 한 Tag가 있다면 반환
  if (searchTagList[search]) {
    const cachingData = searchTagList[search];
    callback(cachingData);
    return cachingData;
  }
  SearchTagsApi(search).then((data) => {
    const tagData = data.data.map((data, index) => (
        {id: data.id, display: data.tag}));
    // search 태그 저장
    setSearchTagList(prevSearchTagList => ({
      ...prevSearchTagList,
      [search]: [...(prevSearchTagList[search] || []), ...tagData] // 기존 배열에 추가
    }));
    callback(tagData)
    return tagData;
  }).catch((error) => {
    return [];
  })
}


