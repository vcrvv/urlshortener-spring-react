import React, { useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Link from "@mui/material/Link";
import Button from "@mui/material/Button";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";
import CheckIcon from '@mui/icons-material/Check';
import ReplayIcon from '@mui/icons-material/Replay';

export default function ShortenedUrl({ shortUrl, error }) {
  const [copied, setCopied] = useState(false);

  const handleCopy = async () => {
    if (shortUrl) {
      await navigator.clipboard.writeText(shortUrl);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  if (error) {
    return (
      <Box mt={2}>
        <Alert severity="error">{error}</Alert>
      </Box>
    );
  }

  if (shortUrl) {
    return (
      <Box mt={2}>
        <Box display="flex" alignItems="center" gap={1}>
          <Alert severity="success" sx={{ flex: 1 }}>
            Sua URL encurtada:{" "}
            <Link href={shortUrl} target="_blank" rel="noopener">
              {shortUrl}
            </Link>
          </Alert>
          <Button
            onClick={handleCopy}
            variant={copied ? "contained" : "outlined"}
            color="primary"
            size="small"
            startIcon={copied ? <CheckIcon /> : <ContentCopyIcon />}
            className={copied ? 'copied-animation' : ''}
          >
            {copied ? "Copiado" : "Copiar"}
          </Button>
        </Box>
      </Box>
    );
  }

  return <Box mt={2} sx={{ minHeight: '68px' }} />;
}
