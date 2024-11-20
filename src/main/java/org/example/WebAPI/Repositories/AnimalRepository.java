package org.example.WebAPI.Repositories;

import org.example.WebAPI.Entities.Animal;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AnimalRepository extends CrudRepository<Animal, Integer>
{
  Animal findAnimalById(Integer id);
  List<Animal> findAnimalsByOriginAndArrivalDate(String origin, String arrivalDate);
}
