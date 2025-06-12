import React, { useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Link from "@mui/material/Link";
import Button from "@mui/material/Button";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";

export default function ShortenedUrl({ shortUrl, error }) {
  const [copied, setCopied] = useState(false);

  const handleCopy = async () => {
    if (shortUrl) {
      await navigator.clipboard.writeText(shortUrl);
      setCopied(true);
      setTimeout(() => setCopied(false), 1500);
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
      <Box mt={2} display="flex" alignItems="center" gap={1}>
        <Alert severity="success" sx={{ flex: 1 }}>
          Sua URL encurtada:{" "}
          <Link href={shortUrl} target="_blank" rel="noopener">
            {shortUrl}
          </Link>
        </Alert>
        <Button
          onClick={handleCopy}
          variant="outlined"
          color="primary"
          size="small"
          sx={{ minWidth: 0, px: 1 }}
        >
          <ContentCopyIcon fontSize="small" />
        </Button>
        {copied && (
          <span style={{ color: "#4caf50", fontSize: 13 }}>Copiado!</span>
        )}
      </Box>
    );
  }
  return null;
}
