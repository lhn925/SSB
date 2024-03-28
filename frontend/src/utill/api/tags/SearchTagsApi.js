import axios from "axios";
import {
  NKEY_OPEN_AGAIN,
  NKEY_OPEN_CAPTCHA,
  TRACKS_TAGS_SEARCH
} from "utill/api/ApiEndpoints";
import {nonAuthApi} from "utill/api/interceptor/ApiAuthInterceptor";

function SearchTagsApi(tags) {
    return nonAuthApi.get(TRACKS_TAGS_SEARCH+tags);
}
export default SearchTagsApi;