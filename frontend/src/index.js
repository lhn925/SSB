import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import {BrowserRouter} from "react-router-dom";
import "utill/locales/i18n";
import {Provider} from "react-redux";
import {store, persistor} from 'store/store';
import {PersistGate} from 'redux-persist/integration/react';
import {DropdownProvider} from "./context/dropDown/DropdownProvider";

// AppContext 객체를 생성한다.
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <DropdownProvider>
      <Provider store={store}>
        <PersistGate loading={null} persistor={persistor}>
          <BrowserRouter>
            <App/>
          </BrowserRouter>
        </PersistGate>
      </Provider>
    </DropdownProvider>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
