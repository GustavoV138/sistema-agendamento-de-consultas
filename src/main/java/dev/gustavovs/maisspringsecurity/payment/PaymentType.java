package dev.gustavovs.maisspringsecurity.payment;

public enum PaymentType {

    CASH("cash"),
    PIX("pix"),
    DEBIT_CARD("debit_card"),
    CREDIT_CARD("credit_card");

    private final String type;

    PaymentType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
