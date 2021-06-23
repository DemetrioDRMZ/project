package project;

public class ProductoVO {
  String nombre;
  String categoria;
  String part_number;
  String price;
  String cantidad;
  
    public String getNombre(){
        return nombre;
    }

    public String getCategoria(){
        return categoria;
    }
    
    public String getPart_number(){
        return part_number;
    }

    public String getPrice(){
        return price;
    }

    public String getCantidad(){
        return cantidad;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
        
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setPart_number(String part_number) {
        this.part_number = part_number;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }
    
}