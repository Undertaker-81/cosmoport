package com.space.service;

import com.space.controller.ShipOrder;
import com.space.exception.BadRequestException;
import com.space.exception.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ShipsService {
    @Autowired
    ShipRepository repository;

    public Page<Ship>  findAll(Specification<Ship> specification, Integer pageNumber, Integer pageSize, ShipOrder order){
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));


        return repository.findAll(specification, pageable);
    }
    public List<Ship>  findAll(Specification<Ship> specification) {
        return repository.findAll(specification);
    }
    public List<Ship> findAll(){
        return repository.findAll();
    }

    public Ship createShip(Ship ship) {

        if (ship.getName() == null
                || ship.getPlanet() == null
                || ship.getShipType() == null
                || ship.getProdDate() == null
                || ship.getSpeed() == null
                || ship.getCrewSize() == null)
            throw new BadRequestException();

        checkShip(ship);

        if (ship.getUsed() == null)
            ship.setUsed(false);

        Double raiting = calculateRating(ship);
        ship.setRating(raiting);

        return repository.save(ship);
    }
    public Ship getShip(Long id) {
        if (!repository.existsById(id))
            throw new NotFoundException();

        return repository.findById(id).get();
    }


    public Ship editShip(Long id, Ship ship) {
        checkShip(ship);

        if (!repository.existsById(id))
            throw new NotFoundException();

        Ship editedShip = repository.findById(id).get();

        if (ship.getName() != null)
            editedShip.setName(ship.getName());

        if (ship.getPlanet() != null)
            editedShip.setPlanet(ship.getPlanet());

        if (ship.getShipType() != null)
            editedShip.setShipType(ship.getShipType());

        if (ship.getProdDate() != null)
            editedShip.setProdDate(ship.getProdDate());

        if (ship.getSpeed() != null)
            editedShip.setSpeed(ship.getSpeed());

        if (ship.getUsed() != null)
            editedShip.setUsed(ship.getUsed());

        if (ship.getCrewSize() != null)
            editedShip.setCrewSize(ship.getCrewSize());

        Double rating = calculateRating(editedShip);
        editedShip.setRating(rating);

        return repository.save(editedShip);
    }

    public void deleteById(Long id) {
        if (repository.existsById(id))
            repository.deleteById(id);
        else throw new NotFoundException();
    }

    private void checkShip(Ship ship) {

        if (ship.getName() != null && (ship.getName().length() < 1 || ship.getName().length() > 50))
            throw new BadRequestException();

        if (ship.getPlanet() != null && (ship.getPlanet().length() < 1 || ship.getPlanet().length() > 50))
            throw new BadRequestException();

        if (ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999))
            throw new BadRequestException();

        if (ship.getSpeed() != null && (ship.getSpeed() < 0.01D || ship.getSpeed() > 0.99D))
            throw new BadRequestException();

        if (ship.getProdDate() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(ship.getProdDate());
            if (cal.get(Calendar.YEAR) < 2800 || cal.get(Calendar.YEAR) > 3019)
                throw new BadRequestException();
        }
    }

    public Long checkId(String id) {
        if (id == null || id.equals("") || id.equals("0"))
            throw new BadRequestException();

        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException();
        }
    }

    private Double calculateRating(Ship ship) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(ship.getProdDate());
        int year = cal.get(Calendar.YEAR);

        BigDecimal raiting = BigDecimal.valueOf((80 * ship.getSpeed() * (ship.getUsed() ? 0.5 : 1)) / (3019 - year + 1));

        raiting = raiting.setScale(2, RoundingMode.HALF_UP);
        return raiting.doubleValue();
    }


    public Specification<Ship> filteredByName(String name) {

        return name == null ? null : new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.like(root.get("name"), "%" + name + "%");
            }
        };
    }


    public Specification<Ship> filteredByPlanet(String planet) {

        return planet == null ? null : new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.like(root.get("planet"), "%" + planet + "%");
            }
        };

    }


    public Specification<Ship> filteredByShipType(ShipType shipType) {

        return shipType == null ? null : new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("shipType"), shipType);
            }
        };
    }


    public Specification<Ship> filteredByDate(Long after, Long before) {

        if (after == null & before == null)
            return null;
        if (after == null){
            Date beforeDate = new Date(before);
            return new Specification<Ship>() {
                @Override
                public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("prodDate"), beforeDate);
                }
            };

        }
        if (before == null){
            Date afterDate = new Date(after);
            return new Specification<Ship>() {
                @Override
                public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("prodDate"), afterDate);
                }
            };
        }
        Date beforeDate = new Date(before);
        Date afterDate = new Date(after);
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.between(root.get("prodDate"),afterDate, beforeDate);
            }
        };
    }


    public Specification<Ship> filteredByUsage(Boolean isUsed) {

        if (isUsed == null)
            return null;
        if (isUsed)
            return new Specification<Ship>() {
                @Override
                public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    return criteriaBuilder.isTrue(root.get("isUsed"));
                }
            };
        else return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.isFalse(root.get("isUsed"));
            }
        };
    }


    public Specification<Ship> filteredBySpeed(Double min, Double max) {

        if (min == null & max == null)
            return null;
        if (min == null){
            return new Specification<Ship>() {
                @Override
                public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("speed"), max);
                }
            };

        }
        if (max == null){
             return new Specification<Ship>() {
                @Override
                public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), min);
                }
            };
        }
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.between(root.get("speed"),min, max);
            }
        };
    }


    public Specification<Ship> filteredByCrewSize(Integer min, Integer max) {

        if (min == null & max == null)
            return null;
        if (min == null){
            return new Specification<Ship>() {
                @Override
                public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("crewSize"), max);
                }
            };

        }
        if (max == null){
            return new Specification<Ship>() {
                @Override
                public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("crewSize"), min);
                }
            };
        }
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.between(root.get("crewSize"),min, max);
            }
        };
    }


    public Specification<Ship> filteredByRating(Double min, Double max) {

        if (min == null & max == null)
            return null;
        if (min == null){
            return new Specification<Ship>() {
                @Override
                public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("rating"), max);
                }
            };

        }
        if (max == null){
            return new Specification<Ship>() {
                @Override
                public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), min);
                }
            };
        }
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.between(root.get("rating"),min, max);
            }
        };
    }
}
