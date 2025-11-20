
# Sistema de Gestión para el Restaurante FPS

## Problema

El restaurante familiar FPS, en pleno crecimiento, necesita modernizar sus operaciones para mejorar la eficiencia y reducir errores. Actualmente, la gestión de menús en papel, la toma de pedidos verbal y la facturación manual provocan desorganización, especialmente en horas pico. Este proyecto implementa un sistema de gestión para digitalizar y automatizar estos procesos.

## Requisitos

- **Gestión de Menú:** Permite crear, actualizar, eliminar y consultar platillos del menú digitalmente.
- **Gestión de Reservas:** Facilita el registro y consulta de reservas, validando la disponibilidad de mesas.
- **Gestión de Pedidos:** Permite tomar pedidos de clientes, asociarlos a una mesa y hacer seguimiento de su estado (ej. PENDIENTE, EN_PREPARACION, LISTO, SERVIDO).
- **Generación de Facturas:** Emite facturas automáticamente a partir de un pedido, calculando el total.
- **Persistencia de Datos:** Almacena la información de menús, reservas, pedidos y facturas para consultas futuras.

## Explicación

### Arquitectura del Proyecto

El sistema sigue una **arquitectura en capas** que separa las responsabilidades en distintos componentes lógicos, lo que facilita la mantenibilidad, escalabilidad y testeo del código. Las capas principales son:

- **Capa de Presentación (Interfaz de Usuario):** Implementada como una aplicación de consola (`Application.java`), se encarga de interactuar con el usuario, recibir sus entradas y mostrar los resultados. No contiene lógica de negocio.

- **Capa de Fachada (Facade):** Se introduce el patrón **Facade** (`RestaurantFacade`) para proporcionar una interfaz unificada y simplificada a un conjunto de interfaces más complejas en el subsistema de servicios. La clase `Application` interactúa directamente con esta fachada, que a su vez delega las llamadas a los servicios correspondientes. Esto reduce el acoplamiento entre la presentación y la lógica de negocio.

- **Capa de Servicios (Lógica de Negocio):** Contiene la lógica de negocio del sistema. Cada servicio (`MenuService`, `OrderService`, `ReservationService`, etc.) encapsula las operaciones y reglas de negocio para una entidad específica.

- **Capa de Persistencia (Acceso a Datos):** Responsable de almacenar y recuperar los datos del sistema. Utiliza el patrón **Data Access Object (DAO)** para abstraer el mecanismo de almacenamiento. La implementación actual (`SerializableDao`) utiliza archivos serializados para la persistencia.

- **Capa de Modelo (Entidades de Dominio):** Define las clases que representan los objetos del dominio del problema, como `MenuItem`, `Order`, `Reservation`, etc. Estas clases son simples POJOs (Plain Old Java Objects) que contienen los datos y su comportamiento asociado.

### Componentes Clave

#### Modelos (`models`)

El paquete `models` contiene las clases que representan las entidades del sistema:

- `Customer`: Representa a un cliente.
- `Invoice`: Representa una factura generada a partir de un pedido.
- `MenuItem`: Representa un platillo del menú.
- `Order`: Representa un pedido de un cliente.
- `Table`: Representa una mesa del restaurante.
- `Reservation`: Representa una reserva de mesa.

#### Fachada (`facade`)

- `RestaurantFacade`: Interfaz que define un punto de entrada único para todas las operaciones del sistema.
- `RestaurantFacadeImpl`: Implementación concreta de la fachada que coordina las interacciones entre los diferentes servicios.

#### Servicios (`services`)

El paquete `services` contiene la lógica de negocio:

- `MenuService`: Gestiona el menú (agregar, eliminar, consultar platillos).
- `TableService`: Gestiona las mesas del restaurante.
- `ReservationService`: Gestiona las reservas de mesas.
- `OrderService`: Gestiona los pedidos de los clientes.
- `InvoiceService`: Genera las facturas de los pedidos.

#### Persistencia (`persistence`)

El paquete `persistence` se encarga del almacenamiento de datos:

- `DataAccessObject`: Interfaz que define el contrato para las operaciones de persistencia (`save` y `load`).
- `SerializableDao`: Implementación concreta de `DataAccessObject` que utiliza objetos serializados para almacenar los datos.

#### Excepciones (`exceptions`)

El paquete `exceptions` contiene excepciones personalizadas para manejar errores específicos del dominio:

- `InvoiceException`: Para errores en la generación de facturas.
- `OrderException`: Para errores en la gestión de pedidos.
- `ReservationException`: Para errores en la gestión de reservas.

#### Utilidades (`util`)

El paquete `util` contiene clases de utilidad, como `LocalDateTimeAdapter`, que se utiliza para serializar y deserializar correctamente los objetos `LocalDateTime` de Java 8 con Gson.

### Flujo de la Aplicación

1.  **Inicialización:** La clase `Application` inicializa los `DataAccessObject` (especificando los archivos de almacenamiento), los servicios y la fachada (`RestaurantFacade`). La fachada recibe las instancias de los servicios para poder coordinarlos.

2.  **Interfaz de Usuario:** Se inicia la interfaz de línea de comandos (CLI), que muestra un menú de opciones al usuario.

3.  **Interacción del Usuario:** El usuario selecciona una opción del menú.

4.  **Ejecución de la Lógica de Negocio:** La clase `Application` llama al método correspondiente en la `RestaurantFacade`. Por ejemplo, si el usuario quiere crear una reserva, se llama al método `createReservation` de la fachada.

5.  **Delegación a Servicios:** La fachada delega la llamada al servicio apropiado. Por ejemplo, `RestaurantFacade` llama al método `createReservation` de `ReservationService`.

6.  **Acceso a Datos:** El servicio utiliza el `DataAccessObject` para leer o escribir datos. Por ejemplo, `ReservationService` utiliza `reservationDao` para guardar la nueva reserva.

7.  **Respuesta al Usuario:** El resultado de la operación se devuelve a través de las capas (Servicio -> Fachada -> Aplicación) y se muestra al usuario en la consola.

### Patrones de Diseño y Principios SOLID

- **Facade:** Se utiliza para proporcionar una interfaz simplificada al complejo subsistema de servicios, desacoplando la capa de presentación de la lógica de negocio.

- **Data Access Object (DAO):** Se utiliza para separar la lógica de negocio de la persistencia de datos, lo que permite cambiar el mecanismo de almacenamiento sin afectar el resto del sistema.

- **Inyección de Dependencias:** Se utiliza para inyectar las dependencias necesarias en los servicios y en la fachada, lo que facilita la prueba y la reutilización del código.

- **Principio de Responsabilidad Única (SRP):** Cada clase tiene una única responsabilidad. Por ejemplo, `MenuService` se encarga de la lógica del menú, y `SerializableDao` se encarga de la persistencia.

- **Bajo Acoplamiento y Alta Cohesión:** El uso de interfaces y la separación de responsabilidades garantizan que los componentes del sistema estén débilmente acoplados y que cada componente tenga una funcionalidad bien definida y cohesiva.
