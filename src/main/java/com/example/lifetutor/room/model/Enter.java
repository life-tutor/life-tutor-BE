package com.example.lifetutor.room.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Enter {
    @Id @GeneratedValue
    private Long id;

    private int amount;

    public Enter(){}
    public Enter(int amount){
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void update(int amount){
        this.amount = amount;
    }
}
