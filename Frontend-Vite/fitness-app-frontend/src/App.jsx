import { BrowserRouter as Router, Navigate, Route, Routes, useLocation } from "react-router";
import { AccordionDetails, Button } from "@mui/material";
import { useContext, useEffect, useState } from "react";
import { setCredentials } from "./store/authSlice";
import { AuthContext } from "react-oauth2-code-pkce";
import { useDispatch } from "react-redux";
import ActivityForm from "./components/ActivityForm";
import ActivityList from "./components/ActivityList";
import Box from '@mui/material/Box';
import ActivityDetail from "./components/ActivityDetail";
const ActivitiesPage = () => {
  return (<Box component="section" sx={{ p: 2, border: '1px dashed grey' }}>
    <ActivityForm onActivityAdded={() => window.location.reload()} />
    onActivityAdded
    <ActivityList />
  </Box>);
}
function App() {
  const { token, tokenData, logIn, logOut, isAuthenticated } = useContext(AuthContext);
  const dispatch = useDispatch();
  const [authReady, setAuthReady] = useState(false);
  useEffect(() => {
    if (token) {
      dispatch(setCredentials({ token, user: tokenData }));
      setAuthReady(true);
    }
  }, [token, tokenData, dispatch])

  return (
    <Router>
      {!token ? (
        <Button variant="contained" color="#dc004e"
          onClick={() => {
            logIn();
          }}
        >LOGIN</Button>
      ) : (
        // <div>
        //   <pre> {JSON.stringify(tokenData, null, 2)} </pre>
        //   <pre> {JSON.stringify(token, null, 2)} </pre>

        // </div>
        <Box component="section" sx={{ p: 2, border: '1px dashed grey' }}>
          <Button variant="contained" color="secondary" onClick={logOut}>
            LogOut
          </Button>
          <Routes>
            <Route path="/activities" element={<ActivitiesPage />} />
            <Route path="/activities/:id" element={<ActivityDetail />} />
            <Route path="/" element={token ? <Navigate to="/activities" replace /> : <div>Welcome! Please Login.</div>} />
          </Routes>
        </Box>

      )}
    </Router>
  )
}
export default App



