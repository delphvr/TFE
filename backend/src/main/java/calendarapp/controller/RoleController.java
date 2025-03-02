package calendarapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import calendarapp.model.Role;
import calendarapp.services.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
@Tag(name = "Role", description = "APIs for managing the possible roles")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/roles")
    @Operation(summary = "Get the list of possible roles")
    public ResponseEntity<List<Role>> getAllRole() {
        List<Role> roles = roleService.getAllRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

}
