/**
 * Application Entry Point
 * Bootstraps the React application and mounts it to the DOM.
 * 
 * @author Yeswanth
 * @author Bruna
 * @version 1.0.0
 */
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
