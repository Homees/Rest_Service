package com.example.rest;

import com.example.rest.controller.EmployeeController;
import com.example.rest.model.Employee;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class EmployeeModelAssembler implements RepresentationModelAssembler<Employee, EntityModel<Employee>> {

    @Override
    public EntityModel<Employee> toModel(Employee entity) {
        return new EntityModel<>(
                entity,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmployeeController.class).getEmployee(entity.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmployeeController.class).allEmployees()).withRel("employees")
        );
    }
}
