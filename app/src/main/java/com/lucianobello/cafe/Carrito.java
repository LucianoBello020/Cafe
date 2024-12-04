package com.lucianobello.cafe;

public class Carrito {
    private int id;
    private int productoId;
    private int cantidad;
    private String nombreProducto;
    private double precioProducto;
    private int imagenProducto;

    public Carrito(int id, String nombreProducto, double precioProducto, int imagenProducto, int cantidad) {
        this.id = id;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.nombreProducto = nombreProducto;
        this.precioProducto = precioProducto;
        this.imagenProducto = imagenProducto;
    }

    public int getId() {
        return id;
    }

    public int getProductoId() {
        return productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public double getPrecioProducto() {
        return precioProducto;
    }

    public String getImagenProducto() {
        return nombreProducto.toLowerCase(); // Retorna el nombre de la imagen
    }

}
