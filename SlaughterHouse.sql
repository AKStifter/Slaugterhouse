create schema Slaughterhouse;
SET SCHEMA 'slaughterhouse';
drop table animal;
CREATE TABLE slaughterhouse.animal (
                                       id SERIAL PRIMARY KEY,
                                       registrationnumber varchar(50),
                                       species VARCHAR(50),
                                       weight DECIMAL(10, 2),
                                       origin varchar(50),
                                       arrivaldate DATE
);
drop table product;
CREATE TABLE slaughterhouse.product (
                                        id SERIAL PRIMARY KEY,
                                        name VARCHAR(100),
                                        weight DECIMAL(10, 2),
                                        production_date DATE
);
drop table part;
CREATE TABLE slaughterhouse.part (
                                     id SERIAL PRIMARY KEY,
                                     animal_id INTEGER REFERENCES slaughterhouse.animal(id),
                                     name VARCHAR(50),
                                     weight DECIMAL(10, 2)
);
drop table productanimal;
CREATE TABLE slaughterhouse.productanimal (
                                              id SERIAL PRIMARY KEY,
                                              product_id INTEGER REFERENCES slaughterhouse.product(id),
                                              animal_id INTEGER REFERENCES slaughterhouse.animal(id),
                                              type VARCHAR(50),
                                              tray_ids VARCHAR(100),
                                              species VARCHAR(50),
                                              slaughterdate DATE,
                                              condition VARCHAR(50)
);
drop table productpart;
CREATE TABLE slaughterhouse.productpart (
                                            id SERIAL PRIMARY KEY,
                                            product_id INTEGER REFERENCES slaughterhouse.product(id),
                                            part_id INTEGER REFERENCES slaughterhouse.part(id)
);
drop table recall;
CREATE TABLE slaughterhouse.recall (
                                       id SERIAL PRIMARY KEY,
                                       product_id INTEGER REFERENCES slaughterhouse.product(id),
                                       reason TEXT,
                                       recall_date DATE
);
drop table tray;
CREATE TABLE slaughterhouse.tray (
                                     id SERIAL PRIMARY KEY,
                                     product_id INTEGER REFERENCES slaughterhouse.product(id),
                                     max_weight DECIMAL(10, 2),
                                     current_weight DECIMAL(10, 2)
);

-- Insert dummy data
INSERT INTO slaughterhouse.animal (registrationnumber, species, weight, origin, arrivaldate)
VALUES
    (1, 'Cow', 500.5,'Kansas', '2023-06-01'),
    (2, 'Pig', 120.3,'Denmark', '2023-06-02'),
    (3, 'Chicken', 2.5,'KFC', '2023-06-03');

INSERT INTO slaughterhouse.product (name, weight, production_date)
VALUES
    ('Beef Steak', 0.5, '2023-06-05'),
    ('Pork Chop', 0.3, '2023-06-06'),
    ('Chicken Breast', 0.2, '2023-06-07');

INSERT INTO slaughterhouse.part (animal_id, name, weight)
VALUES
    (1, 'Sirloin', 10.5),
    (2, 'Belly', 5.3),
    (3, 'Wing', 0.3);

INSERT INTO slaughterhouse.productanimal (product_id, animal_id, type, tray_ids, species, slaughterdate, condition)
VALUES
    (1, 1, 'Beef', '1,2', 'Cow', '2023-06-04', 'Good'),
    (2, 2, 'Pork', '3,4', 'Pig', '2023-06-05', 'Excellent'),
    (1, 1, 'Turkey', '1,2,3', 'Cow', '2023-06-04', 'Good'),
    (2, 2, 'Pork', '4,5', 'Pig', '2023-06-05', 'Excellent'),
    (3, 3, 'Poultry', '6', 'Chicken', '2023-06-06', 'Fair');

INSERT INTO slaughterhouse.productpart (product_id, part_id)
VALUES

    (1, 1),
    (3, 2),
    (3, 3),
    (2, 1),
    (2, 2),
    (2, 3),
    (2, 1),
    (2, 3);

INSERT INTO slaughterhouse.recall (product_id, reason, recall_date)
VALUES
    (1, 'Quality control issue', '2023-06-10'),
    (2, 'Packaging defect', '2023-06-11');

INSERT INTO slaughterhouse.tray (product_id, max_weight, current_weight)
VALUES
    (1, 20.0, 15.5),
    (2, 15.0, 10.2),
    (3, 10.0, 8.7);

