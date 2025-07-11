import React from 'react';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import UrlShortenerForm from './UrlShortenerForm';

describe('UrlShortenerForm', () => {
    it('deve renderizar o formulário corretamente', () => {
        const handleShorten = vi.fn();
        render(<UrlShortenerForm onShorten={handleShorten} loading={false} />);

        expect(screen.getByLabelText(/URL Original/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Expiração \(horas\)/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /Encurtar/i })).toBeInTheDocument();
    });

    it('deve chamar onShorten com os dados corretos ao submeter', async () => {
        const handleShorten = vi.fn();
        render(<UrlShortenerForm onShorten={handleShorten} loading={false} />);

        const urlInput = screen.getByLabelText(/URL Original/i);
        const expiresInput = screen.getByLabelText(/Expiração \(horas\)/i);
        const submitButton = screen.getByRole('button', { name: /Encurtar/i });

        await userEvent.type(urlInput, 'https://example.com');
        await userEvent.type(expiresInput, '24');
        await userEvent.click(submitButton);

        expect(handleShorten).toHaveBeenCalledWith('https://example.com', 24);
    });

    it('deve exibir um erro para uma URL inválida e não chamar onShorten', async () => {
        const handleShorten = vi.fn();
        render(<UrlShortenerForm onShorten={handleShorten} loading={false} />);

        const submitButton = screen.getByRole('button', { name: /Encurtar/i });

        await userEvent.click(submitButton);

        expect(handleShorten).not.toHaveBeenCalled();
    });
});
