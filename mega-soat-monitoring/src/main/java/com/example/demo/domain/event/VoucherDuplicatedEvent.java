package com.example.demo.domain.event;

public class VoucherDuplicatedEvent {
    private final String operationNumber;

    public VoucherDuplicatedEvent(String operationNumber) {
        this.operationNumber = operationNumber;
    }

    public String getOperationNumber() {
        return operationNumber;
    }
}
