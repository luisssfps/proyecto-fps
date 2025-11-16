package models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Invoice implements Serializable {
    private Order order;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private LocalDateTime issueDate;

    public Invoice(Order order, PaymentMethod paymentMethod) {
        this.order = order;
        this.totalAmount = order.getTotal();
        this.paymentMethod = paymentMethod;
        this.issueDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "order=" + order +
                ", totalAmount=" + totalAmount +
                ", paymentMethod=" + paymentMethod +
                ", issueDate=" + issueDate +
                '}';
    }
}
