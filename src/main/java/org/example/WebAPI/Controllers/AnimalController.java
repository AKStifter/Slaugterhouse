package org.example.WebAPI.Controllers;

import com.google.common.collect.Iterables;
import org.example.WebAPI.Entities.Animal;
import org.example.WebAPI.Exceptions.ResourceNotFoundException;
import org.example.WebAPI.Repositories.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class AnimalController
{

  @Autowired
  private AnimalRepository animalRepo;

  @PostMapping("/animals")
  @ResponseStatus(HttpStatus.CREATED)
  public Animal addAnimal(@RequestParam String registrationnumber, @RequestParam String species, @RequestParam double weight, @RequestParam String origin, @RequestParam String arrivaldate) {
    Animal animal = new Animal();

    animal.setRegistrationnumber(registrationnumber);
    animal.setSpecies(species);
    animal.setWeight(weight);
    animal.setOrigin(origin);
    animal.setArrivaldate(arrivaldate);
    animalRepo.save(animal);
    return animal;
  }

  @GetMapping("/animals/id/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Animal getAnimalById(@PathVariable int id)
      throws ResourceNotFoundException
  {
    Animal animal = animalRepo.getAnimalById(id);
    if(animal == null) {
      System.out.println("Animal with id " + id + " not found");
      throw new ResourceNotFoundException("Resource not found with id: " + id);
    }
    else return animal;
  }

  @GetMapping("/animals/date")
  @ResponseStatus(HttpStatus.OK)
  public Iterable<Animal> getAnimalByArrivaldate(@RequestParam String date)
      throws ResourceNotFoundException
  {
    Iterable<Animal> animals = animalRepo.getAnimalByArrivaldate(date);
    if(Iterables.isEmpty(animals)) {
      System.out.println("No animals with date of " + date + " found");
      throw new ResourceNotFoundException("Resource not found with date: " + date);
    }
    else return animals;
  }

  @GetMapping("/animals/origin")
  @ResponseStatus(HttpStatus.OK)
  public Iterable<Animal> getAnimalByOrigin(@RequestParam String origin)
      throws ResourceNotFoundException
  {
    Iterable<Animal> animals = animalRepo.getAnimalByOrigin(origin);
    if(Iterables.isEmpty(animals)) {
      System.out.println("No animals with origin of " + origin + " found");
      throw new ResourceNotFoundException("Resource not found with origin: " + origin);
    }
    else return animals;
  }
}
