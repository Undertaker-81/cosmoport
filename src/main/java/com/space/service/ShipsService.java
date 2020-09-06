package com.space.service;

import com.space.model.Ship;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipsService {
    @Autowired
    ShipRepository repository;

    public List<Ship> findAll(){
        return repository.findAll();
    }
}
