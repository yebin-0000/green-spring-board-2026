package com.green.spring_board.exceptions;

// 요청한 작업을 수행하기에 현재 객체의 상태가 올바르지 않다.
// 현재 상태에서는 해당 작업을 수행할 수 없음
// 앞서서 이미 처리한 작업을 또 처리하려고 하는 등 코드 꼬일 때
public class InvalidStateException extends RuntimeException {
    public InvalidStateException(String message) {
        super(message);
    }
}
