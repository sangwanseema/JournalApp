package com.flipkart.core;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="new_journal")
public class Journal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //  Auto-increment ID
    @Column(name = "journal_id", updatable = false, nullable = false)
    private Long id;
    @Column(nullable = false, unique = true)
    private String title;
    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference // Foreign key reference to User
    private User user;
}
