import React from 'react';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';
import App from './App';

describe('App Component', () => {
  beforeEach(() => {
    vi.spyOn(global, 'fetch').mockClear();
  });

  it('deve renderizar o título e o formulário', () => {
    render(<App />);
    expect(screen.getByText(/Encurtador de URL/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/URL Original/i)).toBeInTheDocument();
  });

  it('deve encurtar uma URL válida e exibir o resultado', async () => {
    const longUrl = 'https://www.example.com';
    const shortUrl = 'fakeshort';

    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ shortUrl: shortUrl, longUrl: longUrl }),
    });

    render(<App />);

    const urlInput = screen.getByLabelText(/URL Original/i);
    const expirationInput = screen.getByLabelText(/Expiração \(horas\)/i);
    const button = screen.getByRole('button', { name: /Encurtar/i });

    await userEvent.type(urlInput, longUrl);
    await userEvent.type(expirationInput, '24');
    await userEvent.click(button);

    const successMessage = await screen.findByText(/Sua URL encurtada:/i);
    expect(successMessage).toBeInTheDocument();

    const link = screen.getByRole('link', { name: new RegExp(shortUrl, 'i') });
    expect(link).toBeInTheDocument();
  });

  it('deve exibir uma mensagem de erro quando a API falhar', async () => {
    fetch.mockResolvedValueOnce({
      ok: false,
      status: 500,
      json: async () => ({ error: 'A URL fornecida é inválida.' }),
    });

    render(<App />);

    const urlInput = screen.getByLabelText(/URL Original/i);
    const expirationInput = screen.getByLabelText(/Expiração \(horas\)/i);
    const button = screen.getByRole('button', { name: /Encurtar/i });

    await userEvent.type(urlInput, 'https://www.example.com');
    await userEvent.type(expirationInput, '24');
    await userEvent.click(button);

    const errorMessage = await screen.findByText(/A URL fornecida é inválida./i);
    expect(errorMessage).toBeInTheDocument();
  });
});
