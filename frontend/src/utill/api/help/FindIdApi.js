import axios from "axios";

export async function FindIdApi(email, authToken) {
  try {
    const response = await axios.get("./users/help/show?authToken="+authToken+"&email="+email)

    return {code: response.status, data: response.data};
  } catch (error) {
    return {code: error.response.status, data: error.response.data};
  }

}