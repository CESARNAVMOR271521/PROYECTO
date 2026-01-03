# Lista de Comandos de Voz - Barbería Chupirules

Este archivo contiene la lista maestra de todos los comandos de voz que el sistema reconoce actualmente. Puede modificar esta lista conceptualmente y pedirme que actualice el código para reflejar sus cambios.

## 🧭 Navegación Global
Comandos para moverse entre las diferentes pantallas del sistema.
- **"Ve a [Modulo]"** / **"Abre [Modulo]"**
  - *Módulos válidos*: Ventas, Citas, Clientes, Barberos, Servicios, Productos, Inventario, Usuarios, Pagos, Facturas, Proveedores, Compras.
  - *Ejemplos*: "Ve a Ventas", "Abre Citas", "Ir a Inventario".

## 🛒 Módulo de Ventas (Punto de Venta)
- **"Agrega [Producto/Servicio]"**: Busca el item en la lista y lo añade al carrito.
  - *Ejemplos*: "Agrega Corte de Adulto", "Agrega Gel".
- **"Aumenta [Cantidad]"**: Modifica la cantidad del item seleccionado o el último añadido.
  - *Ejemplos*: "Aumenta a 2", "Aumenta 5".
- **"Pago con [Método]"**: Selecciona la forma de pago.
  - *Ejemplos*: "Pago con Efectivo", "Pago con Tarjeta".
- **"Cobrar"** / **"Finalizar Venta"**: Termina la venta actual, guarda y limpia.
- **"Limpiar"**: Borra todo el carrito actual para empezar de cero.

## 📅 Módulo de Citas (Agenda)
- **"Agendar"** / **"Crear Cita"**: Guarda la cita con los datos que estén en pantalla.
- **"Cliente [Nombre]"**: Selecciona un cliente del menú.
  - *Ejemplo*: "Cliente Juan Pérez".
- **"Barbero [Nombre]"**: Selecciona al barbero.
  - *Ejemplo*: "Barbero Pedro".
- **"Servicio [Nombre]"**: Selecciona el servicio.
- **"Fecha [AAAA-MM-DD]"**: Establece la fecha (aunque es más fácil seleccionarla manual, el comando existe).
- **"Hora [HH:MM]"**: Establece la hora.

## 📦 Productos e Inventario
- **"Busca [Nombre]"**: Filtra la tabla de productos.
  - *Ejemplo*: "Busca Cera".
- **"Filtra por [Categoría]"**: Muestra solo productos de esa categoría.
  - *Ejemplo*: "Filtra por Cabello".
- **"Stock [Cantidad]"**: Establece el stock físico en el formulario.
  - *Ejemplo*: "Stock 50".
- **"Precio [Monto]"** / **"Costo [Monto]"**: Llena los campos de precio.

## 👥 Clientes, Barberos y Proveedores (Gestión)
Comandos comunes en los paneles de administración de personas.
- **"Guardar"** / **"Agregar"**: Guarda el nuevo registro en la base de datos.
- **"Actualizar"** / **"Editar"**: Guarda los cambios del registro seleccionado.
- **"Eliminar"** / **"Borrar"**: Elimina el registro seleccionado (pide confirmación).
- **"Limpiar"** / **"Nuevo"**: Limpia los campos de texto para escribir uno nuevo.
- **"Busca [Nombre]"**: Busca en la lista.
- **"Nombre [Texto]"**, **"Teléfono [Número]"**, **"Correo [Email]"**: Llenan los campos correspondientes.

---
**Instrucciones para modificar:**
Si desea cambiar alguna palabra clave (por ejemplo, decir "Vender" en lugar de "Cobrar"), indíqueme el cambio y actualizaré la lógica interna del programa.
