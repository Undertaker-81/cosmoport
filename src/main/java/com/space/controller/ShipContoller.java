package com.space.controller;

import com.space.model.Ship;
import com.space.service.ShipsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ShipContoller {

    @Autowired
    ShipsService service;

    @RequestMapping(value = "/rest/ships", method = RequestMethod.GET)
    public List<Ship> getAllShips(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize,
                                  @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order){
      return   service.findAll(pageNumber, pageSize, order).getContent();
    }
    @RequestMapping(value = "/rest/ships/count", method = RequestMethod.GET)
    public Integer getAllShips() {
        return service.findAll().size();
    }
}
