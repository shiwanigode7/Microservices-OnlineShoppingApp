package com.programmingtechie.Order_Service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmingtechie.Order_Service.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
