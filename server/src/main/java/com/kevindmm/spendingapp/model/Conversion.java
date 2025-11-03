package com.kevindmm.spendingapp.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @deprecated Temporary entity for frontend chart demo.
 * Will be removed in Phase 4 when FX rates integration is implemented.
 * See DB-evolution.md § P1.6 for rationale.
 */
@Deprecated
@Entity
@Table(name = "conversion")
public class Conversion {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "soc_sequence")
    @SequenceGenerator(name = "soc_sequence", sequenceName = "soc_sequence", initialValue = 1)
    private int id;

    @Column(name = "rate")
    private float rate;

    @Column(name = "fromCurr")
    private String from;

    @Column(name = "toCurr")
    private String to;

    public Conversion() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}