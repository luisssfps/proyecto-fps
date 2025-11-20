# Casos de Uso

## Gestión de Menú
### CU01: Consultar Menú
- **Descripción**: El recepcionista consulta la lista de platillos disponibles.
- **Actor primario**: Recepcionista.
- **Precondiciones**: Ninguna.
- **Flujo principal**:
    1. El recepcionista selecciona la opción "Ver menú".
    2. El sistema recupera y muestra la lista de todos los `MenuItem` registrados, con su nombre, descripción, precio y categoría.
- **Flujos alternativos**:
    - 2.1. Si el menú está vacío, el sistema muestra el mensaje "El menú está vacío.".
- **Postcondiciones**: Ninguna.

### CU02: Añadir Platillo al Menú
- **Descripción**: El recepcionista añade un nuevo platillo al menú.
- **Actor primario**: Recepcionista.
- **Precondiciones**: El recepcionista conoce los detalles del platillo a añadir.
- **Flujo principal**:
    1. El recepcionista selecciona la opción "Agregar elemento".
    2. El sistema solicita el nombre, descripción, precio y categoría del platillo.
    3. El recepcionista introduce los datos.
    4. El sistema crea una nueva instancia de `MenuItem` y la guarda en la persistencia.
    5. El sistema confirma que el platillo ha sido agregado.
- **Postcondiciones**: El nuevo platillo está disponible en el menú.

### CU03: Eliminar Platillo del Menú
- **Descripción**: El recepcionista elimina un platillo existente del menú.
- **Actor primario**: Recepcionista.
- **Precondiciones**: Deben existir platillos en el menú.
- **Flujo principal**:
    1. El recepcionista selecciona la opción "Eliminar elemento".
    2. El sistema muestra la lista de platillos actuales.
    3. El recepcionista selecciona el platillo a eliminar.
    4. El sistema elimina la instancia de `MenuItem` correspondiente de la persistencia.
    5. El sistema confirma que el platillo ha sido eliminado.
- **Postcondiciones**: El platillo ya no está disponible en el menú.

## Gestión de Reservas
### CU04: Crear Reserva
- **Descripción**: Un cliente o recepcionista crea una reserva para una mesa.
- **Actor primario**: Cliente, Recepcionista.
- **Precondiciones**: El cliente proporciona su nombre, contacto, número de comensales y fecha/hora deseada.
- **Flujo principal**:
    1. El actor inicia el proceso de creación de reserva.
    2. El sistema solicita los datos del cliente, el número de comensales y la fecha/hora.
    3. El actor proporciona los datos.
    4. El sistema busca una `Table` disponible con capacidad suficiente.
    5. El sistema asigna la primera mesa encontrada, actualiza su estado a `isAvailable = false`.
    6. El sistema crea una instancia de `Reservation` con estado `CONFIRMED` y la guarda en la persistencia.
    7. El sistema muestra los detalles de la reserva confirmada.
- **Flujos alternativos**:
    - 4.1. Si no hay ninguna mesa disponible con la capacidad requerida, el sistema muestra un error `NoAvailableTablesException` y el flujo termina.
- **Postcondiciones**: Se crea una nueva reserva y la mesa asignada queda ocupada.

### CU05: Cancelar Reserva
- **Descripción**: El recepcionista cancela una reserva existente.
- **Actor primario**: Recepcionista.
- **Precondiciones**: Debe existir al menos una reserva.
- **Flujo principal**:
    1. El recepcionista selecciona la opción "Cancelar reservación".
    2. El sistema muestra la lista de reservaciones activas.
    3. El recepcionista selecciona la reserva a cancelar.
    4. El sistema actualiza el estado de la `Reservation` a `CANCELED`.
    5. El sistema actualiza el estado de la `Table` asociada a la reserva a `isAvailable = true`.
    6. El sistema guarda los cambios en la persistencia y confirma la cancelación.
- **Flujos alternativos**:
    - 3.1. Si el recepcionista decide no cancelar, selecciona la opción "Cancelar" y el flujo termina.
- **Postcondiciones**: La reserva queda cancelada y la mesa asociada vuelve a estar disponible.

## Gestión de Pedidos
### CU06: Crear Pedido para Cliente sin Reserva
- **Descripción**: El recepcionista crea un pedido para un cliente que llega sin reserva.
- **Actor primario**: Recepcionista.
- **Precondiciones**: Debe haber al menos una mesa disponible.
- **Flujo principal**:
    1. El recepcionista selecciona la opción "Crear orden".
    2. El sistema solicita los datos del cliente.
    3. El recepcionista los introduce.
    4. El sistema muestra una lista de mesas disponibles (`isAvailable = true`).
    5. El recepcionista selecciona una mesa.
    6. El sistema actualiza el estado de la `Table` a `isAvailable = false`.
    7. El sistema crea una nueva `Order` asociada al cliente y la mesa, con estado `PENDING`, y la guarda.
    8. El sistema confirma la creación del pedido.
- **Flujos alternativos**:
    - 4.1. Si no hay mesas disponibles, el sistema lo notifica y el flujo termina.
- **Postcondiciones**: Se crea un nuevo pedido y la mesa seleccionada queda ocupada.

### CU07: Añadir Platillo a Pedido
- **Descripción**: El recepcionista añade uno o más platillos a un pedido existente.
- **Actor primario**: Recepcionista.
- **Precondiciones**: Debe existir al menos un pedido en estado `PENDING`.
- **Flujo principal**:
    1. El recepcionista selecciona la opción "Agregar platillo a orden".
    2. El sistema muestra la lista de pedidos activos.
    3. El recepcionista selecciona un pedido.
    4. El sistema muestra la lista de `MenuItem` disponibles.
    5. El recepcionista selecciona un platillo.
    6. El sistema añade el platillo a la lista de `items` del `Order` y guarda el cambio.
    7. El sistema confirma que el platillo fue agregado.
- **Postcondiciones**: El pedido seleccionado contiene el nuevo platillo y su total se actualiza.

### CU08: Actualizar Estado de Pedido
- **Descripción**: El personal (cocinero/mesero) actualiza el estado de un pedido.
- **Actor primario**: Cocinero, Recepcionista.
- **Precondiciones**: Debe existir al menos un pedido.
- **Flujo principal**:
    1. El actor selecciona la opción "Actualizar estado de orden".
    2. El sistema muestra la lista de pedidos.
    3. El actor selecciona un pedido.
    4. El sistema muestra los posibles estados (`OrderStatus`).
    5. El actor selecciona un nuevo estado (ej. `IN_PROGRESS`, `COMPLETED`, `SERVED`).
    6. El sistema actualiza el `status` del `Order` y guarda el cambio.
    7. El sistema confirma la actualización.
- **Postcondiciones**: El pedido tiene un nuevo estado.

## Gestión de Facturación
### CU09: Generar Factura de Pedido
- **Descripción**: El cajero genera la factura para un pedido que está listo para ser cobrado.
- **Actor primario**: Cajero.
- **Precondiciones**: El pedido debe tener un estado de `COMPLETED` o `SERVED`.
- **Flujo principal**:
    1. El cajero selecciona la opción "Generar factura".
    2. El sistema muestra una lista de pedidos cuyo estado sea `COMPLETED` o `SERVED`.
    3. El cajero selecciona el pedido a facturar.
    4. El sistema solicita el método de pago (`PaymentMethod`).
    5. El cajero selecciona un método de pago.
    6. El sistema crea una nueva `Invoice`, calculando el total a partir de los platillos del pedido.
    7. El sistema actualiza el estado de la `Table` asociada al pedido a `isAvailable = true`.
    8. El sistema guarda la factura y los cambios en la mesa, y muestra los detalles de la factura generada.
- **Flujos alternativos**:
    - 2.1. Si no hay pedidos elegibles para facturar, el sistema lo notifica y el flujo termina.
- **Postcondiciones**: Se genera una factura, se guarda en la persistencia y la mesa del pedido queda liberada.

