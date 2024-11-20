package org.example.WebAPI.Repositories;

import org.example.WebAPI.Entities.Animal;
import org.springframework.data.repository.CrudRepository;

public interface AnimalRepository extends CrudRepository<Animal, Integer> {

  Animal getAnimalById(Integer id);
  Iterable<Animal> getAnimalByArrivaldate(String arrival_date);
  Iterable<Animal> getAnimalByOrigin(String origin);
}