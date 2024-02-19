import axios from "axios";

export default async function ResetFormApi(userId, authToken, email) {
  try {
    const response = await axios.get(
        "./users/help/reset?userId=" + userId + "&authToken=" + authToken
        + "&email=" + email);

    return {code: response.status, data: response.data};
  } catch (error) {
    return {code: error.response.status, data: error.response.data};
  }
}