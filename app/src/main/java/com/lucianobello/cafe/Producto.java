package com.lucianobello.cafe;

public class Producto {

    private int id;
    private String nombre;
    private double precio;
    private int imagen; // ID del recurso de imagen (int)

    // Constructor
    public Producto(int id, String nombre, double precio, int imagen) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public int getImagen() {
        return imagen;
    }
}
