Propuesta de Aplicación: Movie Finder
Descripción
Una API RESTful que permite a los usuarios buscar películas en la API de The Movie Database (TMDb) y en una base de datos no relacional como MongoDB. Los resultados se combinan en una sola respuesta y se etiquetan para indicar si una película está almacenada localmente (en MongoDB) o no.

Distribución en Microservicios
Servicio de Películas Externas (TMDb Service)

Encargado de comunicarse con la API de TMDb.
Proporciona funciones para buscar películas y obtener detalles.
Incluye caching opcional para optimizar el rendimiento y reducir la cantidad de llamadas a la API externa.
Servicio de Películas Locales (Local Movie Service)

Gestiona el almacenamiento de películas en MongoDB.
Proporciona CRUD para películas locales.
Permite guardar películas que el usuario seleccione de los resultados combinados.
Servicio de Integración (Aggregator Service)

Orquesta las llamadas al servicio externo (TMDb) y al servicio local (MongoDB).
Combina y procesa los datos para devolver una respuesta unificada al cliente.
Servicio de Usuarios (User Service)

Opcional para manejar autenticación y roles si deseas controlar acceso (admin/usuarios regulares).
Flujo de Funcionalidad
Usuario realiza una búsqueda de película por título.
Aggregator Service:
Consulta al servicio de TMDb (a través de TMDb Service) para buscar películas por título.
Consulta a MongoDB (a través de Local Movie Service) para buscar películas locales por título.
Combina los resultados:
Marca las películas provenientes de TMDb que no están en MongoDB como no guardadas (saved: false).
Marca las películas locales de MongoDB como guardadas (saved: true).
Devuelve una respuesta unificada al usuario.

Arquitectura Técnica
Diagrama de Arquitectura

+-------------------+          +-------------------+          +-------------------+
| TMDb Service      |          | Local Movie       |          | Aggregator        |
| (Spring Boot)     +----------> Service           +----------> Service           |
| Consulta API TMDb |          | (MongoDB CRUD)    |          | Unifica Resultados|
+-------------------+          +-------------------+          +-------------------+


Esquema de Datos
Modelo de Película (Movie)
```json
{
  "id": "tt123456",
  "title": "Inception",
  "overview": "A thief who steals corporate secrets...",
  "release_date": "2010-07-16",
  "source": "local/tmdb",
  "saved": true/false
}

```

Componentes Clave
1. Servicio de Películas Externas (TMDb Service)
Endpoint RESTful:
GET /tmdb/movies?query={title}: Busca películas en TMDb.
Utiliza la API Key para autenticarse con TMDb.

Caching:
Opcionalmente almacena respuestas en memoria (por ejemplo, usando Redis).


2. Servicio de Películas Locales (Local Movie Service)
Base de Datos:
MongoDB para guardar información de las películas añadidas por el usuario.
Endpoints RESTful:
GET /movies?query={title}: Busca películas locales.
POST /movies: Guarda una película localmente.
DELETE /movies/{id}: Elimina una película local.

3. Servicio de Integración (Aggregator Service)
Endpoints RESTful:
GET /movies?query={title}:
Consulta ambos servicios (TMDb y Local).
Fusiona y procesa resultados.
Lógica del Merge:
Itera sobre los resultados de TMDb y MongoDB.
Identifica coincidencias usando el identificador único de la película (id).
Marca películas locales con saved: true y las de TMDb sin guardar con saved: false.

Respuesta Ejemplo
```json
{
  "movies": [
    {
      "id": "tt1375666",
      "title": "Inception",
      "overview": "A thief who steals corporate secrets...",
      "release_date": "2010-07-16",
      "source": "local",
      "saved": true
    },
    {
      "id": "tt2582802",
      "title": "Whiplash",
      "overview": "A promising young drummer enrolls...",
      "release_date": "2014-10-10",
      "source": "tmdb",
      "saved": false
    }
  ]
}

```
Requisitos Técnicos
Spring Boot
Cada servicio es un proyecto independiente.
MongoDB
Base de datos no relacional para las películas locales.
Swagger/OpenAPI
Documentación de cada servicio.
Tests Unitarios
Cobertura de todas las capas (Controller, Service, Repository).
Docker
Contenedores para cada servicio y MongoDB.
Logging
Configuración de trazabilidad entre servicios.

Siguientes Pasos
Crear los microservicios base:
TMDb Service con una integración básica de la API externa.
Local Movie Service con MongoDB.
Aggregator Service para combinar los resultados.
Configurar comunicación entre servicios.
Implementar validaciones y manejo de errores.
Agregar tests unitarios y de integración.
