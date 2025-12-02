package com.programmingtechie.Order_Service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.programmingtechie.Order_Service.dto.OrderLineItemsDto;
import com.programmingtechie.Order_Service.dto.OrderRequest;
import com.programmingtechie.Order_Service.model.Order;
import com.programmingtechie.Order_Service.model.OrderLineItems;
import com.programmingtechie.Order_Service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	
	private final OrderRepository orderRepository;

	public void placeOrder(OrderRequest orderRequest) {
		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());
		
		List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
		.stream()
		.map(this::mapToDto)
		.toList();
		
		order.setOrderLineItemsList(orderLineItems);
		
		orderRepository.save(order);
	}
	
	private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
		OrderLineItems orderLineItems = new OrderLineItems();
		orderLineItems.setPrice(orderLineItemsDto.getPrice());
		orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
		orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		return orderLineItems;
	}
}
