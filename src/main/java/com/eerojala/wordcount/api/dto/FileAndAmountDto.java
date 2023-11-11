package com.eerojala.wordcount.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

public class FileAndAmountDto {
    @NotNull
    private MultipartFile file;

    @NotNull
    @Positive
    private Integer amount;

    public FileAndAmountDto(MultipartFile file, Integer amount) {
        this.file = file;
        this.amount = amount;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
