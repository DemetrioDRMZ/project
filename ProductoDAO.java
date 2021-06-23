package project;

import project.Conexion;
import project.ProductoVO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductoDAO {
    int countRs;
    String tabla;
    /*Metodo listar*/

    public ProductoDAO(String nombre){
        this.tabla = nombre;
    }

    public ArrayList<ProductoVO> Listar_ProductoVO() {
        ArrayList<ProductoVO> list = new ArrayList<ProductoVO>();
        Conexion conec = new Conexion();
        String sql = "SELECT * FROM "+ tabla +";";
        ResultSet rs = null; 
        PreparedStatement ps = null;
        try {
            ps = conec.getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            countRs = 0;
            while (rs.next()) {
                ProductoVO vo = new ProductoVO();
                vo.setNombre(rs.getString(1));
                vo.setCategoria(rs.getString(2));
                vo.setPart_number(rs.getString(3));
                vo.setPrice(rs.getString(4));
                vo.setCantidad(rs.getString(5));
                list.add(vo);
                countRs++;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                ps.close();
                rs.close();
                conec.desconectar();
            } catch (Exception ex) {
            }
        }
        return list;
    }

    public ProductoVO obtener_producto(String nombre){
        ProductoVO vo = new ProductoVO();
        Conexion conec = new Conexion();
        String sql = "SELECT * FROM "+ tabla +" WHERE nombre = ?;";
        ResultSet rs = null; 
        PreparedStatement ps = null;
        try {
            ps = conec.getConnection().prepareStatement(sql);
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            countRs = 0;
            while (rs.next()) {
                vo.setNombre(rs.getString(1));
                vo.setCategoria(rs.getString(2));
                vo.setPart_number(rs.getString(3));
                vo.setPrice(rs.getString(4));
                vo.setCantidad(rs.getString(5));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                ps.close();
                rs.close();
                conec.desconectar();
            } catch (Exception ex) {
            }
        }
        return vo;
    }
    
    public int getCountRs(){
        return countRs;
    }
    
    public void Rellenar_ProductoVO(String nombre, String cantidad) {
        Conexion conec = new Conexion();
        String sql = "UPDATE "+ tabla +" SET cantidad = ? WHERE nombre = ?;";
        PreparedStatement ps = null;
        try {
            ps = conec.getConnection().prepareStatement(sql);
            ps.setString(1, cantidad);
            ps.setString(2, nombre);
            ps.executeUpdate();
            System.out.println("Producto abastecido correctamente.");
        } catch (SQLException ex) {
            System.out.println("Error al abastecer el producto");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally{
            try {
                ps.close();
                conec.desconectar();
            } catch (Exception ex) {
            }
        }
    }

    /*Metodo Buscar*/
    public boolean ProductoVO_Disponible(String nombre) {
        boolean disponible = false;
        ResultSet rs = null;
        Conexion conec = new Conexion();
        String sql = "SELECT * FROM "+ tabla +" WHERE nombre = ? AND cantidad >= 1;";
        PreparedStatement ps = null;
        try {
            ps = conec.getConnection().prepareStatement(sql);
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            while(rs.next()){
                if(nombre.equals(rs.getString(1))){
                    disponible = true;
                    System.out.println("Busqueda Exitosa");
                }
            }
        } catch (SQLException ex) {
            System.out.println("No esta disponible tal producto");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                ps.close();
                conec.desconectar();
            } catch (Exception ex) {
            }
        }

        return disponible;
    }

    public boolean ProductoVO_Existe(String nombre) {
        boolean existe = false;
        ResultSet rs = null;
        Conexion conec = new Conexion();
        String sql = "SELECT * FROM "+ tabla +" WHERE nombre = ?;";
        PreparedStatement ps = null;
        try {
            ps = conec.getConnection().prepareStatement(sql);
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            while(rs.next()){
                if(nombre.equals(rs.getString(1))){
                    existe = true;
                    System.out.println("Busqueda Exitosa");
                }
            }
        } catch (SQLException ex) {
            System.out.println("No esta disponible tal producto");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                ps.close();
                conec.desconectar();
            } catch (Exception ex) {
            }
        }

        return existe;
    }

    public String ProductoVO_A_Surtir(String nombre) {
        String nombreP = "";
        ResultSet rs = null;
        Conexion conec = new Conexion();
        String sql = "SELECT nombre FROM "+ tabla +" WHERE nombre = ? AND cantidad = 0;";
        PreparedStatement ps = null;
        try {
            ps = conec.getConnection().prepareStatement(sql);
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            while(rs.next()){
                nombreP = rs.getString(1);
                if(nombre.equals(rs.getString(1))){
                     = true;
                    System.out.println("Busqueda Exitosa");
                }
            }
        } catch (SQLException ex) {
            System.out.println("No esta disponible tal producto");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                ps.close();
                conec.desconectar();
            } catch (Exception ex) {
            }
        }

        return nombreP;
    }
}
