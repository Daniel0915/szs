package com.example.szs.insideTrade.domain.embedded;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.time.LocalDateTime;

@Embeddable
@Getter
public class Time {
    private LocalDateTime regDt;
    private LocalDateTime modDt;

    public Time() {this.createdRegDt();}

    public void createdRegDt() {
        this.regDt = LocalDateTime.now();
    }

    public void updateModDt() {
        this.modDt = LocalDateTime.now();
    }
}
