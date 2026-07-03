# FoodBus

Sistem za poručivanje i dostavu hrane. Radio sam ovo kao projekat iz predmeta Softverski alati.

Ideja je prosta - aplikacija povezuje kupce, restorane i dostavljače. Kupac napravi porudžbinu iz menija nekog restorana, plati je, prati status dostave i na kraju može da ostavi recenziju. Backend je REST API.

## Tehnologije

- Java 17
- Spring Boot (Web + Data JPA)
- Maven
- MySQL
- Lombok (da ne pišem gettere i settere ručno)
- JUnit 5 + Mockito za testove
- JavaDoc za dokumentaciju
- Git

## Pokretanje

Potreban je MySQL koji radi na `localhost:3306`. Bazu ne morate praviti ručno - aplikacija sama napravi bazu `foodbus` i sve tabele pri prvom pokretanju (`ddl-auto=update`).

```
./mvnw spring-boot:run
```

Podrazumevano se konektuje kao `root` bez šifre. Ako imate šifru, prosledite je preko env varijable:

```
DB_PASSWORD=vasa_sifra ./mvnw spring-boot:run
```

Prvi put kad se pokrene, ubaci par test podataka (nekoliko restorana, jela, kupaca, jedna porudžbina) da baza ne bude prazna.

Aplikacija se diže na `http://localhost:8080`.

## Testovi

```
./mvnw test
```

Ima ih oko 150 - testirani su domenski objekti (validacija preko jakarta.validation) i servisi (Mockito). Za generisanje JavaDoc-a:

```
./mvnw javadoc:javadoc
```

## Par primera ruta

- `GET /api/restaurants` - lista restorana, može i filter: `?cuisine=Kineska&minRating=4`
- `POST /api/restaurants` - dodaj restoran
- `GET /api/restaurants/{id}/menu-items` - meni restorana
- `POST /api/menu-items` - dodaj jelo
- `POST /api/orders` - napravi porudžbinu
- `PUT /api/orders/{id}/status` - promeni status porudžbine
- `PUT /api/orders/{orderId}/driver/{driverId}` - dodeli dostavljača
- `POST /api/payments` - plati porudžbinu
- `POST /api/reviews` - ostavi recenziju
- `GET /api/customers/{id}/orders` - istorija porudžbina kupca

Za većinu entiteta postoji create/update/delete (kupci, restorani, dostavljači, jela, recenzije).

## Struktura

Klasičan slojeviti raspored: `domain` (entiteti), `repositories`, `services` (poslovna logika), `controllers` (REST), i `dtos` za ulaz/izlaz. Id-jevi su `Long` (auto increment).

## Autor

Stefan Marinković 2022/0038
