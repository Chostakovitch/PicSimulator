-- ----------------------------------------------------------------------------------------------------
-- ---------------------------------- CREATION DE LA BASE DE DONNEE -----------------------------------
-- ----------------------------------------------------------------------------------------------------

-- Lancer le docker : docker-compose up -d
-- Acceder à la BDD : psql -h localhost -p 5433 -d pic -U user

-- ----------------------------------------------------------------------------------------------------
-- ---------------------------------- CREATION DES TABLES ---------------------------------------------
-- ----------------------------------------------------------------------------------------------------
-- CREATION DE LA TABLE CONTENANT LES DONNEES DE PAYUT
-- Chaque ligne correspond à une transaction effectué avec Payut
CREATE TABLE payutP17 (
  id INT PRIMARY KEY,
  transaction_id INT,
  transaction_datetime DATE,
  buyer_id INT,
  fundation VARCHAR(255),
  sales_location VARCHAR(255),
  mean_of_payment VARCHAR(255),
  item VARCHAR(255),
  category VARCHAR(255),
  currency_name VARCHAR(255),
  unit_price DECIMAL,
  quantity INT,
  total DECIMAL
);

-- CREATION DE LA TABLE CONTENANT LES DONNEES DE PAYUT AVEC UN TIMESTAMP
-- Cette table est identique à la précédente mais avec un timestamp pour les dates, et non plus uniquement la date.
-- Elle servira nottamenr à déterminer les heures de départ et d'arrivée
CREATE TABLE payutP17_timestamp (
  id INT PRIMARY KEY,
  transaction_id INT,
  transaction_datetime timestamp,
  buyer_id INT,
  fundation VARCHAR(255),
  sales_location VARCHAR(255),
  mean_of_payment VARCHAR(255),
  item VARCHAR(255),
  category VARCHAR(255),
  currency_name VARCHAR(255),
  unit_price DECIMAL,
  quantity INT,
  total DECIMAL
);

-- TABLE CONTENANT LES DONNEES DU SONDAGE
-- Chaque ligne est une reponse au formulaire
CREATE TABLE formP17 (
  id INT,
  date DATE,
  gender VARCHAR(255),
  age VARCHAR(255),
  status VARCHAR(255),
  semester VARCHAR(255),
  gonePic VARCHAR(255),
  drinkAlcohol VARCHAR(255),
  pref_Cidre_Loic_Raison VARCHAR(255),
  pref_Chimay_Bleue VARCHAR(255),
  pref_Duvel VARCHAR(255),
  pref_Grand_Cru_St_Feuillien VARCHAR(255),
  pref_Peche_Mel_Bush VARCHAR(255),
  pref_Westmalle_Triple VARCHAR(255),
  pref_Barbar_Blonde VARCHAR(255),
  pref_Chouffe VARCHAR(255),
  pref_Cuvee_Des_Trolls VARCHAR(255),
  pref_Delirium_Tremens VARCHAR(255),
  pref_Gauloise_Rouge VARCHAR(255),
  meanDrinks VARCHAR(255),
  timeDrink VARCHAR(255),
  timeStart VARCHAR(255),
  timeEnd VARCHAR(255),
  budget VARCHAR(255),
  eat VARCHAR(255),
  days VARCHAR(255),
  sensibilite INT,
  friends VARCHAR(255),
  bartender VARCHAR(255),
  bartenderTime VARCHAR(255)
);


-- ----------------------------------------------------------------------------------------------------
-- ---------------------------------- REMPLIR LES TABLES ----------------------------------------------
-- ----------------------------------------------------------------------------------------------------

-- SET GOOD DATE FORMAT
SET DATESTYLE TO 'ISO';

--- \cd votre path où il y a le csv
\copy payutP17 FROM 'payut-data.csv' WITH CSV HEADER DELIMITER ',';
\copy payutP17_timestamp FROM 'payut-data.csv' WITH CSV HEADER DELIMITER ',';
\copy formP17 FROM 'PicSimulator/PicSimulator/resources/raw-data-students.csv' WITH CSV HEADER DELIMITER ',';


-- ----------------------------------------------------------------------------------------------------
-- ---------------------------------- RECUPERATION DES STATS ------------------------------------------
-- ----------------------------------------------------------------------------------------------------
DELETE FROM formP17 WHERE budget = '99999999999';

-- SELECT UNIQUE BEER
SELECT DISTINCT item
FROM payutP17
WHERE fundation = 'Picasso' AND
  sales_location = 'Pic Soir' AND
  (category = 'Bières bouteilles' OR
  category = 'Bières pression')
GROUP BY item;


-- NOMBRE D'ETUDIANT PAR JOURS ET CA
CREATE VIEW students_per_day AS (
  SELECT transaction_datetime, count(DISTINCT buyer_id) as buyer_count, sum(total) as CA
  FROM payutP17
  WHERE fundation = 'Picasso' AND
    sales_location='Pic Soir' AND
    (category = 'Bières bouteilles' OR
    category = 'Bières pression')
  GROUP BY transaction_datetime
  ORDER BY transaction_datetime
);

-- CA TOTAL Pour les bières
SELECT transaction_datetime, item, sum(quantity) as quantity, sum(total) as CA
FROM payutP17
WHERE fundation = 'Picasso' AND
  sales_location = 'Pic Soir' AND
  (category = 'Bières bouteilles' OR
  category = 'Bières pression')
GROUP BY transaction_datetime, item
ORDER BY transaction_datetime;

-- Quantité de biere vendu
SELECT item, sum(quantity) as quantity
FROM payutP17
WHERE fundation = 'Picasso' AND
  sales_location = 'Pic Soir' AND
  (category = 'Bières bouteilles' OR
  category = 'Bières pression')
GROUP BY item


-- NOMBRE DE BIERE MOYEN PAR PERSONNE
SELECT mean, count(mean)
FROM (
  SELECT round(avg(count)) as mean FROM (
    SELECT buyer_id AS id, transaction_datetime AS time, count(*) as count
    FROM payutP17
    WHERE fundation = 'Picasso' AND
      sales_location = 'Pic Soir' AND
      (category = 'Bières bouteilles' OR
      category = 'Bières pression')
    GROUP BY transaction_datetime, buyer_id
  ) AS TEST
  GROUP BY id
) AS TEST2
GROUP BY mean
ORDER BY count(mean) desc;

SELECT mean, count(mean)
FROM (
  SELECT
    CASE WHEN meanDrinks~E'^\\d+$'
      THEN meanDrinks::integer
      ELSE 2
    END AS mean
  FROM formP17
) AS meansDrink
GROUP BY mean
ORDER BY mean;

-- MAX PAR personne
SELECT max, count(max)
FROM (
  SELECT round(max(count)) as max FROM (
    SELECT buyer_id AS id, transaction_datetime AS time, count(*) as count
    FROM payutP17
    WHERE fundation = 'Picasso' AND
      sales_location = 'Pic Soir' AND
      (category = 'Bières bouteilles' OR
      category = 'Bières pression')
    GROUP BY transaction_datetime, buyer_id
  ) AS TEST
  GROUP BY id
) AS TEST2
GROUP BY max
ORDER BY count(max) desc;


-- ROUND TIME FUNCTION
CREATE OR REPLACE FUNCTION round_time(TIMESTAMP)
RETURNS TIMESTAMP AS $$
  SELECT date_trunc('hour', $1) + INTERVAL '15 min' * ROUND(date_part('minute', $1) / 15.0)
$$ LANGUAGE SQL;

-- Heure d'arrivée
SELECT time, count(time)
FROM (
  SELECT buyer_id, to_char(round_time(min(transaction_datetime)), 'HH24:MI') AS time
  FROM payutP17_timestamp
  WHERE fundation = 'Picasso' AND
    sales_location = 'Pic Soir' AND
    (category = 'Bières bouteilles' OR
    category = 'Bières pression')
  GROUP BY buyer_id
  ORDER BY buyer_id asc
) AS roundTime
GROUP BY time
ORDER BY count(time) desc;

SELECT time, count(time)
FROM (
  SELECT timeStart AS time
  FROM formP17
) AS roundTime
GROUP BY time
ORDER BY count(time) desc;

-- Heure de depart
SELECT time, count(time)
FROM (
  SELECT buyer_id, to_char(round_time(max(transaction_datetime)), 'HH24:MI') AS time
  FROM payutP17_timestamp
  WHERE fundation = 'Picasso' AND
    sales_location = 'Pic Soir' AND
    (category = 'Bières bouteilles' OR
    category = 'Bières pression')
  GROUP BY buyer_id
  ORDER BY buyer_id asc
) AS roundTime
GROUP BY time
ORDER BY count(time) desc;

-- Moyenne de dépenses
SELECT money, count(money)
FROM (
  SELECT round(avg(sum)) as money FROM (
    SELECT buyer_id AS id, transaction_datetime AS time, sum(total) as sum
    FROM payutP17
    WHERE fundation = 'Picasso' AND
      sales_location = 'Pic Soir' AND
      (category = 'Bières bouteilles' OR
      category = 'Bières pression')
    GROUP BY transaction_datetime, buyer_id
  ) AS TEST
  GROUP BY id
) AS TEST2
GROUP BY money
ORDER BY count(money) desc;

-- Max de dépenses
SELECT money, count(money) as count
FROM (
  SELECT round(max(sum)) as money FROM (
    SELECT buyer_id AS id, sum(total) as sum
    FROM payutP17
    WHERE fundation = 'Picasso' AND
      sales_location = 'Pic Soir' AND
      (category = 'Bières bouteilles' OR
      category = 'Bières pression')
    GROUP BY buyer_id
  ) AS TEST
  GROUP BY id
) AS TEST2
GROUP BY money
HAVING count(money) > 10
ORDER BY money;

SELECT max, count(max)
FROM (
  SELECT
    CASE WHEN budget~E'^\\d+$'
      THEN budget::integer
      ELSE 10
    END AS max
  FROM formP17
) AS meansDrink
GROUP BY max
HAVING count(max) > 5
ORDER BY max;

-- Jour de présence
SELECT day, count(*)
FROM (
  SELECT to_char(transaction_datetime, 'day') as day
  FROM payutP17
  WHERE fundation = 'Picasso' AND
    sales_location = 'Pic Soir' AND
    (category = 'Bières bouteilles' OR
    category = 'Bières pression')
  GROUP BY transaction_datetime, buyer_id
) AS days
GROUP BY day
ORDER BY day;


-- ----------------------------------------------------------------------------------------------------
-- ---------------------------------- EXPORT DES DONNEES ----------------------------------------------
-- ----------------------------------------------------------------------------------------------------
\copy (SELECT item, sum(quantity) as quantity FROM payutP17 WHERE fundation = 'Picasso' AND sales_location = 'Pic Soir' AND (category = 'Bières bouteilles' OR category = 'Bières pression') GROUP BY item) TO 'beer_total_quantity.csv' DELIMITER ',' CSV HEADER;
\copy ( SELECT transaction_datetime, count(DISTINCT buyer_id) as buyer_count, sum(total) as CA FROM payutP17 WHERE fundation = 'Picasso' AND sales_location='Pic Soir' AND (category = 'Bières bouteilles' OR category = 'Bières pression') GROUP BY transaction_datetime ORDER BY transaction_datetime ) TO 'students_per_day.csv' DELIMITER ',' CSV HEADER;
\copy (  ) TO 'students_per_day.csv' DELIMITER ',' CSV HEADER;
