package com.backend_potato.edubox_team2.global.exception;

public class CustomExceptions {
    public static class BadRequestException extends CustomException {
        public BadRequestException(ErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class UnauthorizedException extends CustomException {
        public UnauthorizedException(ErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class ForbiddenException extends CustomException {
        public ForbiddenException(ErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class NotFoundException extends CustomException {
        public NotFoundException(ErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class InvalidFormatException extends CustomException {
        public InvalidFormatException(ErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class ClientException extends CustomException {
        public ClientException(ErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class ServerException extends CustomException {
        public ServerException(ErrorCode errorCode) {
            super(errorCode);
        }
    }
}
