package com.green.spring_board.global;

import com.green.spring_board.exceptions.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
/*
[전역 예외 처리기]
1. 어플리케이션 내 모든 컨트롤러에서 throw 예외를 이 곳에서 가로채어 처리
2. 예외 처리 결과는 자동으로 JSON 응답 본문으로 변환
3. 개별 컨트롤러의 try-catch 코드 중복을 제거, 클라이언트에게 일관된 에러 응답 형식 보장 가능
*/

@RestControllerAdvice
/*
@ExceptionHandler(예외 클래스)
public 반환형 메서드명(예외 클래스) {
어노테이션에 해당하는 예외가 발생했을 때
일괄적으로 처리할 작업 내용
}
*/

// 요청한 데이터, 있어야 할 데이터가 없을 때 공통 처리
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
    // 인증 정보가 없거나 적절하지 않을 때 공통 처리
    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<Void> handleUnauthenticated(UnauthenticatedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    // Validator 등 입력 검증 과정에서 문제 발생 시 공통 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationError(MethodArgumentNotValidException e) {
        String resultMessage = "";
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        for (FieldError error : errors) {
            resultMessage = resultMessage + error.getField() + "은(는)" + error.getDefaultMessage() + "\n";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.parseMediaType("text/plain;charset=UTF-8"))
                .body(resultMessage);

    }

    // 고유값이 중복되어 저장에 실패하거나,
    // 존재하지 않는 외래키를 이용해 데이터 생성 시도 등 문제 상황 공통 처리
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataConflict(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.parseMediaType("text/plain;charset=UFT-8"))
                .body("중복되거나 저장할 수 없는 데이터입니다.");
    }
    //세션 로그인 방식 이메일 중복 방지
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<String> handleConflict(ResourceConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    // 위에서 지정한 예외 외의 예외들은 모두 여기서 공통 처리(500에러)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.parseMediaType("text/plain;charset=UTF-8"))
                .body("서버에서 오류가 발생했습니다");
    }
    @ExceptionHandler(AuthorizationFailureException.class)
    public ResponseEntity<Void> handlerForbidden(
            AuthorizationFailureException e
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<Void> handleBadRequest(
            InvalidStateException e
    ) {
        return ResponseEntity.badRequest().build();
    }

}
