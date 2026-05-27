package br.com.geosat.server.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super("Token expirado");
    }
}
