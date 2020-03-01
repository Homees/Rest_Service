package com.example.rest;

import com.example.rest.controller.EmployeeController;
import com.example.rest.controller.OrderController;
import com.example.rest.model.Order;
import com.example.rest.model.Status;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>> {

    @Override
    public EntityModel<Order> toModel(Order order) {
        // Unconditional links to single-item resource and aggregate root
        EntityModel<Order> orderModel = new EntityModel<>(order,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).getOrder(order.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).allOrders()).withRel("orders")
        );

        // Conditional links based on state of the order
        if (order.getStatus() == Status.IN_PROGRESS) {
            orderModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).completeOrder(order.getId())).withRel("complete"));
            orderModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).cancelOrder(order.getId())).withRel("cancel"));
        }

        return orderModel;
    }
}
