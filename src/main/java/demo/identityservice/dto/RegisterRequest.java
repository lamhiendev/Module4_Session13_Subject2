package demo.identityservice.dto;

import demo.identityservice.entity.Role;
import demo.identityservice.entity.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RegisterRequest {
    private String username;
    private String password;
    private Set<Role> role;
}
