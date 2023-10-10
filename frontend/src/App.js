import './App.css';
import {Button, Navbar, Container, Nav} from "react-bootstrap";
import {lazy, Suspense, createContext, useState, useEffect} from "react";
import {Routes, Route, Link, useNavigate, Outlet} from 'react-router-dom'
import axios from 'axios';
import {useQuery} from 'react-query';

const Detail = lazy(() => import('./routes/Detail'));
const Cart = lazy(() => import('./routes/Cart'));
function App() {
  return (
    <div className="App">

    </div>
  );
}

export default App;
