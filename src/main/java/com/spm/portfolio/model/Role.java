package com.spm.portfolio.model;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("roles")
@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    private Long id;
    private String roleName;

   /* public Role(String roleName) {
        this.roleName = roleName;
    }*/

    // Getters and Setters
}
