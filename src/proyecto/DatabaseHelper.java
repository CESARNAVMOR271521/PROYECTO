package proyecto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {

        private static final String URL = "jdbc:sqlite:barberia.db";

        public static Connection connect() {
                Connection conn = null;
                try {
                        // Load the SQLite JDBC driver
                        Class.forName("org.sqlite.JDBC");
                        conn = DriverManager.getConnection(URL);
                } catch (ClassNotFoundException | SQLException e) {
                        System.out.println(e.getMessage());
                }
                return conn;
        }

        public static void initDB() {
                // SQL statements to create tables
                String[] sqlStatements = {
                                "CREATE TABLE IF NOT EXISTS Cliente (\n"
                                                + "    id_cliente        INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                                                + "    nombre            TEXT NOT NULL,\n"
                                                + "    telefono          TEXT,\n"
                                                + "    correo            TEXT,\n"
                                                + "    historial         TEXT\n"
                                                + ");",

                                "CREATE TABLE IF NOT EXISTS Barbero (\n"
                                                + "    id_barbero        INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                                                + "    nombre            TEXT NOT NULL,\n"
                                                + "    especialidades    TEXT,\n"
                                                + "    activo            INTEGER DEFAULT 1\n"
                                                + ");",

                                "CREATE TABLE IF NOT EXISTS Servicio (\n"
                                                + "    id_servicio       INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                                                + "    nombre            TEXT NOT NULL,\n"
                                                + "    descripcion       TEXT,\n"
                                                + "    precio            REAL NOT NULL\n"
                                                + ");",

                                "CREATE TABLE IF NOT EXISTS Cita (\n"
                                                + "    id_cita           INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                                                + "    fecha             TEXT NOT NULL,\n"
                                                + "    hora              TEXT NOT NULL,\n"
                                                + "    id_cliente        INTEGER NOT NULL,\n"
                                                + "    id_barbero        INTEGER NOT NULL,\n"
                                                + "    id_servicio       INTEGER NOT NULL,\n"
                                                + "    estado            TEXT DEFAULT 'pendiente',\n"
                                                + "    FOREIGN KEY (id_cliente) REFERENCES Cliente(id_cliente),\n"
                                                + "    FOREIGN KEY (id_barbero) REFERENCES Barbero(id_barbero),\n"
                                                + "    FOREIGN KEY (id_servicio) REFERENCES Servicio(id_servicio)\n"
                                                + ");",

                                "CREATE TABLE IF NOT EXISTS Proveedor (\n"
                                                + "    id_proveedor      INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                                                + "    nombre            TEXT NOT NULL,\n"
                                                + "    telefono          TEXT,\n"
                                                + "    correo            TEXT\n"
                                                + ");",

                                "CREATE TABLE IF NOT EXISTS Producto (\n"
                                                + "    id_producto       INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                                                + "    nombre            TEXT NOT NULL,\n"
                                                + "    descripcion       TEXT,\n"
                                                + "    precio_venta      REAL NOT NULL,\n"
                                                + "    precio_compra     REAL,\n"
                                                + "    id_proveedor      INTEGER,\n"
                                                + "    FOREIGN KEY (id_proveedor) REFERENCES Proveedor(id_proveedor)\n"
                                                + ");",

                                "CREATE TABLE IF NOT EXISTS Venta (\n"
                                                + "    id_venta          INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                                                + "    fecha             TEXT NOT NULL,\n"
                                                + "    id_cliente        INTEGER,\n"
                                                + "    total             REAL,\n"
                                                + "    tipo              TEXT DEFAULT 'servicio',\n"
                                                + "    FOREIGN KEY (id_cliente) REFERENCES Cliente(id_cliente)\n"
                                                + ");",

                                "CREATE TABLE IF NOT EXISTS DetalleVenta (\n"
                                                + "    id_detalle        INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                                                + "    id_venta          INTEGER NOT NULL,\n"
                                                + "    id_producto       INTEGER,\n"
                                                + "    id_servicio       INTEGER,\n"
                                                + "    cantidad          INTEGER DEFAULT 1,\n"
                                                + "    precio_unitario   REAL NOT NULL,\n"
                                                + "    FOREIGN KEY (id_venta) REFERENCES Venta(id_venta),\n"
                                                + "    FOREIGN KEY (id_producto) REFERENCES Producto(id_producto),\n"
                                                + "    FOREIGN KEY (id_servicio) REFERENCES Servicio(id_servicio)\n"
                                                + ");",

                                "CREATE TABLE IF NOT EXISTS Pago (\n"
                                                + "    id_pago           INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                                                + "    id_venta          INTEGER NOT NULL,\n"
                                                + "    forma_pago        TEXT NOT NULL,\n"
                                                + "    monto             REAL NOT NULL,\n"
                                                + "    estado            TEXT DEFAULT 'completado',\n"
                                                + "    FOREIGN KEY (id_venta) REFERENCES Venta(id_venta)\n"
                                                + ");",

                                "CREATE TABLE IF NOT EXISTS HorarioBarbero (\n"
                                                + "    id_horario        INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                                                + "    id_barbero        INTEGER NOT NULL,\n"
                                                + "    dia_semana        TEXT NOT NULL,\n"
                                                + "    hora_inicio       TEXT NOT NULL,\n"
                                                + "    hora_fin          TEXT NOT NULL,\n"
                                                + "    FOREIGN KEY (id_barbero) REFERENCES Barbero(id_barbero)\n"
                                                + ");",

                                "CREATE TABLE IF NOT EXISTS Factura (\n"
                                                + "    id_factura        INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                                                + "    id_venta          INTEGER NOT NULL,\n"
                                                + "    fecha_emision     TEXT NOT NULL,\n"
                                                + "    total             REAL NOT NULL,\n"
                                                + "    FOREIGN KEY (id_venta) REFERENCES Venta(id_venta)\n"
                                                + ");",

                                "CREATE TABLE IF NOT EXISTS Inventario (\n"
                                                + "    id_inventario     INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                                                + "    id_producto       INTEGER NOT NULL,\n"
                                                + "    cantidad_actual   INTEGER NOT NULL,\n"
                                                + "    minimo            INTEGER DEFAULT 0,\n"
                                                + "    FOREIGN KEY (id_producto) REFERENCES Producto(id_producto)\n"
                                                + ");",

                                "CREATE TABLE IF NOT EXISTS CompraProveedor (\n"
                                                + "    id_compra         INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                                                + "    id_proveedor      INTEGER NOT NULL,\n"
                                                + "    fecha             TEXT NOT NULL,\n"
                                                + "    total             REAL NOT NULL,\n"
                                                + "    FOREIGN KEY (id_proveedor) REFERENCES Proveedor(id_proveedor)\n"
                                                + ");",

                                "CREATE TABLE IF NOT EXISTS DetalleCompra (\n"
                                                + "    id_detalle        INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                                                + "    id_compra         INTEGER NOT NULL,\n"
                                                + "    id_producto       INTEGER NOT NULL,\n"
                                                + "    cantidad          INTEGER NOT NULL,\n"
                                                + "    precio_unitario   REAL NOT NULL,\n"
                                                + "    FOREIGN KEY (id_compra) REFERENCES CompraProveedor(id_compra),\n"
                                                + "    FOREIGN KEY (id_producto) REFERENCES Producto(id_producto)\n"
                                                + ");",

                                "CREATE TABLE IF NOT EXISTS Usuario (\n"
                                                + "    id_usuario        INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                                                + "    nombre            TEXT NOT NULL,\n"
                                                + "    usuario           TEXT NOT NULL UNIQUE,\n"
                                                + "    password          TEXT NOT NULL,\n"
                                                + "    rol               TEXT NOT NULL\n"
                                                + ");",

                                "CREATE TABLE IF NOT EXISTS RegistroVoz (\n"
                                                + "    id_log            INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                                                + "    texto             TEXT NOT NULL,\n"
                                                + "    fecha             TEXT DEFAULT (datetime('now', 'localtime'))\n"
                                                + ");"
                };

                try (Connection conn = connect();
                                Statement stmt = conn.createStatement()) {

                        if (conn != null) {
                                for (String sql : sqlStatements) {
                                        stmt.execute(sql);
                                }
                                System.out.println("Tablas creadas o verificadas correctamente.");
                        }
                } catch (SQLException e) {
                        System.out.println(e.getMessage());
                }
        }
}
