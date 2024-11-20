package org.example.WebAPI.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
  String registrationNumber;
  String species;
  double weight;
  String origin;
  String arrivalDate;
}
