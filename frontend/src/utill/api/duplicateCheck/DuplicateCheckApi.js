import {USERS_JOIN_DUPLICATE} from "utill/api/ApiEndpoints";
import {nonGetAuthApi} from "../interceptor/ApiAuthInterceptor";

export async function DuplicateCheckApi(name,value) {
  try {
    const response = await nonGetAuthApi.get(USERS_JOIN_DUPLICATE+name+"?"+name+"="+value);
    return{
      code:response.status,
      data:response.data
    }
  } catch (error) {
    return {
      code:error.response.status,
      data:error.response.data
    };
  }
}