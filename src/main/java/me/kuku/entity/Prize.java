package me.kuku.entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "unicom_prize")
public class Prize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String phone;
    @Column
    private String prize;
    @Column
    private Date date;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Prize() {
    }

    public Prize(Integer id, String phone, String prize, Date date){
        this.id = id;
        this.phone = phone;
        this.prize = prize;
        this.date = date;
    }

}
