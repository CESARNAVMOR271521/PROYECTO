package proyecto.dao;

import proyecto.Conexion;
import proyecto.modelo.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO producto (nombre, descripcion, precio_venta, precio_compra, id_proveedor) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, producto.getNombre());
            pstmt.setString(2, producto.getDescripcion());
            pstmt.setDouble(3, producto.getPrecioVenta());
            pstmt.setDouble(4, producto.getPrecioCompra());
            if (producto.getIdProveedor() > 0) {
                pstmt.setInt(5, producto.getIdProveedor());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto";
        try (Connection conn = Conexion.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Producto p = new Producto();
                p.setIdProducto(rs.getInt("id_producto"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setPrecioVenta(rs.getDouble("precio_venta"));
                p.setPrecioCompra(rs.getDouble("precio_compra"));
                p.setIdProveedor(rs.getInt("id_proveedor"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar productos: " + e.getMessage());
        }
        return lista;
    }

    public List<Producto> listarPorProveedor(int idProveedor) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE id_proveedor = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProveedor);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("id_producto"));
                    p.setNombre(rs.getString("nombre"));
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setPrecioVenta(rs.getDouble("precio_venta"));
                    p.setPrecioCompra(rs.getDouble("precio_compra"));
                    p.setIdProveedor(rs.getInt("id_proveedor"));
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar productos por proveedor: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Producto producto) {
        String sql = "UPDATE producto SET nombre = ?, descripcion = ?, precio_venta = ?, precio_compra = ?, id_proveedor = ? WHERE id_producto = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, producto.getNombre());
            pstmt.setString(2, producto.getDescripcion());
            pstmt.setDouble(3, producto.getPrecioVenta());
            pstmt.setDouble(4, producto.getPrecioCompra());
             if (producto.getIdProveedor() > 0) {
                pstmt.setInt(5, producto.getIdProveedor());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            pstmt.setInt(6, producto.getIdProducto());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idProducto) {
        String sql = "DELETE FROM producto WHERE id_producto = ?";
        try (Connection conn = Conexion.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProducto);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }
}
