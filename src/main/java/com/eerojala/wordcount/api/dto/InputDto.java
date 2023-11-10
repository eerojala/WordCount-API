package com.eerojala.wordcount.api.dto;

import jakarta.validation.constraints.Positive;

public class InputDto {
    private String content;

    @Positive
    private Integer amount;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
