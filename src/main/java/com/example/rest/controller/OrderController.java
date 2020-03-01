package com.example.rest.controller;

import com.example.rest.OrderModelAssembler;
import com.example.rest.OrderRepository;
import com.example.rest.exceptions.OrderNotFoundException;
import com.example.rest.model.Order;
import com.example.rest.model.Status;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.vnderrors.VndErrors;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class OrderController {
    private OrderRepository repository;
    private OrderModelAssembler assembler;

    public OrderController(OrderRepository repository, OrderModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/orders")
    public CollectionModel<EntityModel<Order>> allOrders() {
        List<EntityModel<Order>> orders = repository.findAll().stream().map(assembler::toModel).collect(Collectors.toList());
        return new CollectionModel<>(orders, WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).allOrders()).withSelfRel());
    }

    @GetMapping("/orders/{id}")
    public EntityModel<Order> getOrder(@PathVariable Long id) {
        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        return assembler.toModel(order);
    }

    @PostMapping("/orders")
    public ResponseEntity<EntityModel<Order>> postOrder(@RequestBody Order order) {
        order.setStatus(Status.IN_PROGRESS);
        Order newOrder = repository.save(order);

        return ResponseEntity.created(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class)).toUri()).body(assembler.toModel(newOrder));
    }

    @PutMapping("/orders/{id}/complete")
    public ResponseEntity<RepresentationModel> completeOrder(@PathVariable Long id) {
        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.COMPLETED);
            return ResponseEntity.ok(assembler.toModel(repository.save(order)));
        }

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new VndErrors.VndError("Method not allowed",
                "You can't complete an order that is in the " + order.getStatus() + " status"));
    }

    @DeleteMapping("/orders/{id}/cancel")
    public ResponseEntity<RepresentationModel> cancelOrder(@PathVariable Long id) {
        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.CANCELLED);
            return ResponseEntity.ok(assembler.toModel(repository.save(order)));
        }

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new VndErrors.VndError("Method not allowed",
                "You can't cancel an order that is in the " + order.getStatus() + " status"));
    }
}
