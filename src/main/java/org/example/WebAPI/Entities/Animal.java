package org.example.WebAPI.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Animal
{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  int id;
  String registrationnumber;
  String species;
  double weight;
  String origin;
  String arrivaldate;

}
