package org.boz.model;

import java.util.Date;
import java.util.UUID;

public class Transaction {

    private final UUID uuid;
    private String username;
    private Integer total;
    private Boolean isValid;
    private Date enrichedDate;
    private String receiver;
    private Boolean hasError;
    private Boolean sent;

    public Transaction() {
        this.uuid = UUID.randomUUID();
    }

    public Transaction(String username, Integer total) {
        this();
        this.username = username;
        this.total = total;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }

    public Date getEnrichedDate() {
        return enrichedDate;
    }

    public void setEnrichedDate(Date enrichedDate) {
        this.enrichedDate = enrichedDate;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Boolean getHasError() {
        return hasError;
    }

    public void setHasError(Boolean hasError) {
        this.hasError = hasError;
    }

    public Boolean getSent() {
        return sent;
    }

    public void setSent(Boolean sent) {
        this.sent = sent;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "uuid=" + uuid +
                ", username='" + username + '\'' +
                ", total=" + total +
                ", isValid=" + isValid +
                ", enrichedDate=" + enrichedDate +
                ", receiver='" + receiver + '\'' +
                ", hasError=" + hasError +
                ", sent=" + sent +
                '}';
    }
}
