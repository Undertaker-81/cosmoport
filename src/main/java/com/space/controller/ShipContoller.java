package com.space.controller;

import com.space.model.Ship;
import com.space.service.ShipsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ShipContoller {

    @Autowired
    ShipsService service;

    @RequestMapping(value = "/rest/ships", method = RequestMethod.GET)
    public List<Ship> getAllShips(){
      return   service.findAll();
    }
}
