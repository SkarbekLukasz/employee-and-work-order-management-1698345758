package ls.EmployeeWorkOrderManagment.persistence.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ls.EmployeeWorkOrderManagment.persistence.model.role.Role;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "first_name", nullable = false, length = 20)
    @NotBlank
    @Size(min = 2, max = 20)
    private String firstName;
    @Column(name = "last_name", nullable = false, length = 20)
    @NotBlank
    @Size(min = 2, max = 20)
    private String lastName;
    @Column(nullable = false, unique = true, length = 45, name = "email")
    @NotNull
    @Email(message = "Wrong email address format")
    private String email;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    @Column(nullable = false, length = 64)
    @NotNull @Size(max = 64, min = 8) @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}")
    private String password;
    @Column(name = "enabled")
    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addRoles(Role role) {
        this.getRoles().add(role);
    }

    public void cleanRoles() {
        this.getRoles().clear();
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
