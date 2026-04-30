package com.zoya.backend.dto;

import com.zoya.backend.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponseDTO {

    private Long paymentId;
    private Long bookingId;
    private BigDecimal  amount;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime paymentTime;

    // getters & setters
    public Long getPaymentId() {
        return paymentId;        
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;        
    }

    public Long getBookingId() {
        return bookingId;        
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;        
    }

    public BigDecimal getAmount() {
        return amount;        
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;        
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;        
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;        
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;        
    }

    public String getTransactionId() {
        return transactionId;        
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;        
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;        
    }

    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;        
    }
}