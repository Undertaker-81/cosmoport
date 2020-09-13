package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShipContoller {

    @Autowired
    ShipsService service;

    @RequestMapping(value = "/rest/ships", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Ship> getAllShips(@RequestParam(value = "name", required = false) String name,
                                  @RequestParam(value = "planet", required = false) String planet,
                                  @RequestParam(value = "shipType", required = false) ShipType shipType,
                                  @RequestParam(value = "after", required = false) Long after,
                                  @RequestParam(value = "before", required = false) Long before,
                                  @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                  @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                  @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                  @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                  @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                  @RequestParam(value = "minRating", required = false) Double minRating,
                                  @RequestParam(value = "maxRating", required = false) Double maxRating,
                                  @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize,
                                  @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order){

      return service.findAll(Specification.where(service.filteredByName(name))
                                            .and(service.filteredByPlanet(planet))
                                            .and(service.filteredByShipType(shipType))
                                            .and(service.filteredByDate(after, before))
                                            .and(service.filteredByUsage(isUsed))
                                            .and(service.filteredBySpeed(minSpeed, maxSpeed))
                                            .and(service.filteredByCrewSize(minCrewSize, maxCrewSize))
                                            .and(service.filteredByRating(minRating, maxRating))
                                            , pageNumber, pageSize, order).getContent();

    }
    @RequestMapping(value = "/rest/ships/count", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Integer getAllShipsCount( @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "planet", required = false) String planet,
        @RequestParam(value = "shipType", required = false) ShipType shipType,
        @RequestParam(value = "after", required = false) Long after,
        @RequestParam(value = "before", required = false) Long before,
        @RequestParam(value = "isUsed", required = false) Boolean isUsed,
        @RequestParam(value = "minSpeed", required = false) Double minSpeed,
        @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
        @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
        @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
        @RequestParam(value = "minRating", required = false) Double minRating,
        @RequestParam(value = "maxRating", required = false) Double maxRating){

            return service.findAll(Specification.where(service.filteredByName(name))
                            .and(service.filteredByPlanet(planet))
                            .and(service.filteredByShipType(shipType))
                            .and(service.filteredByDate(after, before))
                            .and(service.filteredByUsage(isUsed))
                            .and(service.filteredBySpeed(minSpeed, maxSpeed))
                            .and(service.filteredByCrewSize(minCrewSize, maxCrewSize))
                            .and(service.filteredByRating(minRating, maxRating))).size();
    }
    @RequestMapping(value = "/rest/ships", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Ship createShip(@RequestBody Ship ship){
      return   service.createShip(ship);
    }

    @RequestMapping(value = "/rest/ships/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Ship getShipById(@PathVariable String id){
        Long longId = service.checkId(id);
        return service.getShip(longId);
    }

    @RequestMapping(value = "/rest/ships/{id}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Ship editShip(@PathVariable  String id, @RequestBody Ship ship){
        Long longId = service.checkId(id);
        return service.editShip(longId, ship);
    }

    @RequestMapping(value = "/rest/ships/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)

    public void deleteShip(@PathVariable  String id){
        Long longId = service.checkId(id);
        service.deleteById(longId);
    }


}
