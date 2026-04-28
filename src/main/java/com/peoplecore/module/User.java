package com.peoplecore.module;
import com.peoplecore.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_TABLE")
@Entity
public class User extends Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "USER_ID", unique = false, nullable = false, updatable = false)
    private String userID;
    @Column(name = "USER_NAME")
    private String userName;
    @Column(name = "USER_EMAIL")
    private String userEmail;
    @Column(name = "USER_PASSWORD")
    private String userPassword;
    @Column(name = "PASSWORD_UPDATED_DATE")
    private LocalDateTime passwordChangeDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.ACTIVE;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>();


    @Column(name = "mobile_number", nullable = false, unique = true, updatable = false, length = 10)
    private String mobileNumber;

    @Column(name = "country", nullable = false, length = 10, updatable = false)
    private String country;
    @Column(name = "city",  nullable = false, length = 10,updatable = false)
    private String city;
    @Column(name = "state", nullable = false, length = 10, updatable = false)
    private String state;
    @Column(name = "cluster", nullable = false, length = 10, updatable = false)
    private String cluster;


}
