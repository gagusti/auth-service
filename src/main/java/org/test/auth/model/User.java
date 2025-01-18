package org.test.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;
    private String name;
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
    @OneToMany(targetEntity=Phone.class,cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Phone> phones;
    private String created;
    private String lastLogin;
    private boolean isActive;
}
