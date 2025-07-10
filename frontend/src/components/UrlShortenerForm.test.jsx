import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import UrlShortenerForm from './UrlShortenerForm';

describe('UrlShortenerForm', () => {
  it('deve renderizar o formulário corretamente', () => {
    render(<UrlShortenerForm onShorten={() => {}} loading={false} />);

    expect(screen.getByLabelText(/URL para encurtar/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Expiração \(horas\)/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Encurtar/i })).toBeInTheDocument();
  });

  it('deve chamar onShorten com os dados corretos ao submeter um formulário válido', () => {
    const handleShorten = vi.fn();
    render(<UrlShortenerForm onShorten={handleShorten} loading={false} />);

    const urlInput = screen.getByLabelText(/URL para encurtar/i);
    const expiresInput = screen.getByLabelText(/Expiração \(horas\)/i);
    const submitButton = screen.getByRole('button', { name: /Encurtar/i });

    fireEvent.change(urlInput, { target: { value: 'https://example.com' } });
    fireEvent.change(expiresInput, { target: { value: '24' } });
    fireEvent.click(submitButton);

    expect(handleShorten).toHaveBeenCalledWith('https://example.com', 24);
  });

  it('deve exibir um erro para uma URL inválida e não chamar onShorten', () => {
    const handleShorten = vi.fn();
    render(<UrlShortenerForm onShorten={handleShorten} loading={false} />);

    const urlInput = screen.getByLabelText(/URL para encurtar/i);
    const submitButton = screen.getByRole('button', { name: /Encurtar/i });

    fireEvent.change(urlInput, { target: { value: 'invalid-url' } });
    fireEvent.click(submitButton);

    expect(screen.getByText(/URL inválida/i)).toBeInTheDocument();
    expect(handleShorten).not.toHaveBeenCalled();
  });
});
