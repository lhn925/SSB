import axios from "axios";

export async function CodeCheckApi(body) {
  const headers = {
    "Content-Type": "application/json;charset=UTF-8"
  };

  const dataObject = {code: null, data: null}
  try {
    const response = await axios.post("./email/codeCheck", body, {headers: headers})
    dataObject.code = response.status;
    dataObject.data = response.data;
  } catch (error) {
    dataObject.code = error.response.status;
    dataObject.data = error.response.data;
  } finally {
    return dataObject;
  }
}