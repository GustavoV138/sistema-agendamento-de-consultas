package dev.gustavovs.maisspringsecurity.patient;

import dev.gustavovs.maisspringsecurity.commom.BaseEnity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Entity
@Table(name = "patient")
public class Patient extends BaseEnity {

    @Column(length = 200)
    private String name;

    @Column(length = 11)
    private String phoneNumber;

    private LocalDate birthday;

}
