package proyecto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexion {

    private static final String URL = "jdbc:sqlite:barberia.db";

    public static Connection conectar() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
        return conn;
    }

    public static void crearTablas() {

        String[] sqls = {

            "CREATE TABLE IF NOT EXISTS cliente (" +
            "id_cliente INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombre TEXT NOT NULL," +
            "telefono TEXT," +
            "correo TEXT UNIQUE," +
            "historial TEXT," +
            "fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ");",

            "CREATE TABLE IF NOT EXISTS barbero (" +
            "id_barbero INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombre TEXT NOT NULL," +
            "especialidades TEXT," +
            "activo INTEGER DEFAULT 1" +
            ");",

            "CREATE TABLE IF NOT EXISTS servicio (" +
            "id_servicio INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombre TEXT NOT NULL," +
            "descripcion TEXT," +
            "precio REAL NOT NULL," +
            "duracion_minutos INTEGER" +
            ");",

            "CREATE TABLE IF NOT EXISTS cita (" +
            "id_cita INTEGER PRIMARY KEY AUTOINCREMENT," +
            "fecha DATE NOT NULL," +
            "hora TIME NOT NULL," +
            "id_cliente INTEGER NOT NULL," +
            "id_barbero INTEGER NOT NULL," +
            "estado TEXT DEFAULT 'pendiente'," +
            "FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)," +
            "FOREIGN KEY (id_barbero) REFERENCES barbero(id_barbero)" +
            ");",

            "CREATE TABLE IF NOT EXISTS producto (" +
            "id_producto INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombre TEXT NOT NULL," +
            "descripcion TEXT," +
            "precio REAL NOT NULL" +
            ");",

            "CREATE TABLE IF NOT EXISTS venta (" +
            "id_venta INTEGER PRIMARY KEY AUTOINCREMENT," +
            "fecha DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "id_cliente INTEGER," +
            "total REAL," +
            "FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)" +
            ");",

            "CREATE TABLE IF NOT EXISTS detalle_venta (" +
            "id_detalle INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_venta INTEGER NOT NULL," +
            "tipo_item TEXT CHECK(tipo_item IN ('producto','servicio'))," +
            "id_item INTEGER NOT NULL," +
            "cantidad INTEGER DEFAULT 1," +
            "precio_unitario REAL NOT NULL," +
            "subtotal REAL NOT NULL," +
            "FOREIGN KEY (id_venta) REFERENCES venta(id_venta)" +
            ");",

            "CREATE TABLE IF NOT EXISTS pago (" +
            "id_pago INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_venta INTEGER NOT NULL," +
            "forma_pago TEXT CHECK(forma_pago IN ('efectivo','tarjeta','transferencia'))," +
            "monto REAL NOT NULL," +
            "estado TEXT DEFAULT 'pagado'," +
            "FOREIGN KEY (id_venta) REFERENCES venta(id_venta)" +
            ");",

            "CREATE TABLE IF NOT EXISTS horario_barbero (" +
            "id_horario INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_barbero INTEGER NOT NULL," +
            "dia_semana TEXT," +
            "hora_inicio TIME," +
            "hora_fin TIME," +
            "FOREIGN KEY (id_barbero) REFERENCES barbero(id_barbero)" +
            ");",

            "CREATE TABLE IF NOT EXISTS factura (" +
            "id_factura INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_venta INTEGER NOT NULL," +
            "fecha_emision DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "total REAL NOT NULL," +
            "FOREIGN KEY (id_venta) REFERENCES venta(id_venta)" +
            ");",

            "CREATE TABLE IF NOT EXISTS inventario (" +
            "id_inventario INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_producto INTEGER NOT NULL," +
            "stock INTEGER NOT NULL," +
            "stock_minimo INTEGER DEFAULT 0," +
            "FOREIGN KEY (id_producto) REFERENCES producto(id_producto)" +
            ");",

            "CREATE TABLE IF NOT EXISTS proveedor (" +
            "id_proveedor INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombre TEXT NOT NULL," +
            "telefono TEXT," +
            "correo TEXT" +
            ");",

            "CREATE TABLE IF NOT EXISTS compra_proveedor (" +
            "id_compra INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_proveedor INTEGER NOT NULL," +
            "fecha DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "total REAL," +
            "FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor)" +
            ");",

            "CREATE TABLE IF NOT EXISTS usuario_sistema (" +
            "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombre TEXT NOT NULL," +
            "usuario TEXT UNIQUE NOT NULL," +
            "password TEXT NOT NULL," +
            "rol TEXT CHECK(rol IN ('admin','barbero','empleado'))" +
            ");"
        };

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            if (conn != null) {
                for (String sql : sqls) {
                    stmt.execute(sql);
                }
                System.out.println("Tablas creadas o verificadas correctamente.");
            }

        } catch (SQLException e) {
            System.out.println("Error al crear tablas: " + e.getMessage());
        }
    }
}
