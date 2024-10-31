package ru.kai.homework.client.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kai.homework.client.model.enums.AccountType;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name="accounts")
public class Account {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AccountType type;

    @Column(precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name="is_blocked")
    private boolean isBlocked = false;

    @ManyToOne
    @JoinColumn(name="client_id", referencedColumnName = "id")
    private Client client;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();
}
