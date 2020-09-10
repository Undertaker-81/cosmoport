package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipsService {
    @Autowired
    ShipRepository repository;

    public Page<Ship>  findAll(Integer pageNumber, Integer pageSize, ShipOrder order){
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));


        return repository.findAll(pageable);
    }
    public List<Ship> findAll(){
        return repository.findAll();
    }
}
