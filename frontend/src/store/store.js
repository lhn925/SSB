import { configureStore, createSlice } from '@reduxjs/toolkit';
import { useTranslation } from "react-i18next";
let messages = createSlice({
  t:useTranslation
});



export default messages;


