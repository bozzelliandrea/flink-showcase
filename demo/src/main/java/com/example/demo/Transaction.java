package com.example.demo;

import java.io.Serializable;

public class Transaction implements Serializable {
    private String username;
    private Integer total;
    private Boolean sent;

    public void sent() {
        this.sent = true;
    }

    public Boolean getSent() {
        return sent;
    }

    public void setSent(Boolean sent) {
        this.sent = sent;
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

    @Override
    public String toString() {
        return "Transaction{" +
                "username='" + username + '\'' +
                ", total=" + total +
                ", sent=" + sent +
                '}';
    }
}
