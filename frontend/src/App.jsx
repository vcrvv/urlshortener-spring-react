import React, { useState } from "react";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import CssBaseline from "@mui/material/CssBaseline";
import Container from "@mui/material/Container";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import Brightness4Icon from '@mui/icons-material/Brightness4';
import Brightness7Icon from '@mui/icons-material/Brightness7';
import UrlShortenerForm from "./components/UrlShortenerForm";
import ShortenedUrl from "./components/ShortenedUrl";
import "./App.css";

export default function App() {
  const [shortUrl, setShortUrl] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [darkMode, setDarkMode] = useState(true);

  const theme = createTheme({
    palette: {
      mode: darkMode ? "dark" : "light",
    },
  });

  const handleShorten = async (url, expiresAt) => {
    setLoading(true);
    setError(null);
    setShortUrl(null);
    try {
      const response = await fetch("/api/shorten", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ longUrl: url, expiresAt }),
      });
      if (!response.ok) {
        const data = await response.json();
        setError(data.error || (data.errors && data.errors[0]) || "Erro desconhecido");
      } else {
        const data = await response.json();
        const backendUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
        setShortUrl(`${backendUrl}/${data.shortUrl}`);
      }
    } catch (e) {
      setError("Erro ao conectar ao servidor");
    } finally {
      setLoading(false);
    }
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Container maxWidth="sm">
        <Box mt={4} mb={2} display="flex" alignItems="center" justifyContent="space-between">
          <Typography variant="h4">Encurtador de URL</Typography>
          <IconButton onClick={() => setDarkMode((d) => !d)} color="inherit">
            {darkMode ? <Brightness7Icon /> : <Brightness4Icon />}
          </IconButton>
        </Box>
        <UrlShortenerForm onShorten={handleShorten} loading={loading} />
        <ShortenedUrl shortUrl={shortUrl} error={error} />
      </Container>
    </ThemeProvider>
  );
}
