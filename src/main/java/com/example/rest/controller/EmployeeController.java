package com.example.rest.controller;

import com.example.rest.exceptions.EmployeeNotFoundException;
import com.example.rest.EmployeeRepository;
import com.example.rest.model.Employee;
import com.example.rest.EmployeeModelAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EmployeeController {
    private EmployeeRepository repository;
    private EmployeeModelAssembler assembler;

    public EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/employees")
    public CollectionModel<EntityModel<Employee>> allEmployees() {
        List<EntityModel<Employee>> employees = repository.findAll().stream().map(assembler::toModel).collect(Collectors.toList());

        return new CollectionModel<>(employees,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmployeeController.class).allEmployees()).withSelfRel()
        );
    }

    @PostMapping("/employees")
    public ResponseEntity newEmployee(@RequestBody Employee employee) {
        EntityModel<Employee> entity = assembler.toModel(repository.save(employee));
        return ResponseEntity.created(entity.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entity);
    }

    @GetMapping("/employees/{id}")
    public EntityModel<Employee> getEmployee(@PathVariable Long id) {
        Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

        return assembler.toModel(employee);
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity putEmployee(@PathVariable Long id, @RequestBody Employee newEmployee) {
        Employee update = repository.findById(id).map(employee -> {
            employee.setName(newEmployee.getName());
            employee.setRole(newEmployee.getRole());
            return repository.save(employee);
        }).orElseGet(() -> {
            newEmployee.setId(id);
            return repository.save(newEmployee);
        });

        EntityModel<Employee> entity = assembler.toModel(update);
        return ResponseEntity.created(entity.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entity);
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity deleteEmployee(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
