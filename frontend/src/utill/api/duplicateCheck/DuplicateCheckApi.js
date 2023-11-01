import {Regex} from "utill/function";
import axios from "axios";

export async function DuplicateCheckApi(name,value) {
  try {

    const response = await axios.get("./user/join/duplicate/"+name+"?"+name+"="+value);

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