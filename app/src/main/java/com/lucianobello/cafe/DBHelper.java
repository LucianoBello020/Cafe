package com.lucianobello.cafe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "cafeteria.db";
    public static final int DB_VERSION = 2;

    // Tablas
    public static final String TABLE_PRODUCTOS = "productos";
    public static final String TABLE_CARRITO = "carrito";

    // Columnas de la tabla productos
    public static final String COLUMN_PRODUCTO_ID = "id";
    public static final String COLUMN_PRODUCTO_NOMBRE = "nombre";
    public static final String COLUMN_PRODUCTO_PRECIO = "precio";
    public static final String COLUMN_PRODUCTO_IMAGEN = "imagen"; // Ahora es TEXT

    // Columnas de la tabla carrito
    public static final String COLUMN_CARRITO_ID = "id";
    public static final String COLUMN_CARRITO_PRODUCTO_ID = "producto_id";
    public static final String COLUMN_CARRITO_CANTIDAD = "cantidad";

    private Context context; // Contexto para obtener recursos

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PRODUCTOS + " (" +
                COLUMN_PRODUCTO_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_PRODUCTO_NOMBRE + " TEXT, " +
                COLUMN_PRODUCTO_PRECIO + " REAL, " +
                COLUMN_PRODUCTO_IMAGEN + " TEXT)"); // Texto para la imagen

        db.execSQL("CREATE TABLE " + TABLE_CARRITO + " (" +
                COLUMN_CARRITO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CARRITO_PRODUCTO_ID + " INTEGER, " +
                COLUMN_CARRITO_CANTIDAD + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_CARRITO_PRODUCTO_ID + ") REFERENCES " + TABLE_PRODUCTOS + "(" + COLUMN_PRODUCTO_ID + "))");

        insertarProductosPorDefecto(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARRITO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTOS);
        onCreate(db);
    }

    private void insertarProductosPorDefecto(SQLiteDatabase db) {
        String[][] productosPorDefecto = {
                {"1", "Affogato", "4.50", "affogato"},
                {"2", "Americano", "3.00", "americano"},
                {"3", "Café con Leche", "3.50", "cafeconleche"},
                {"4", "Café de Olla", "4.00", "cafe_de_olla"},
                {"5", "Cappuccino", "4.00", "cappuccino"},
                {"6", "Cold Brew", "4.50", "cold_brew"},
                {"7", "Cortado", "3.50", "cortado"},
                {"8", "Espresso", "2.50", "espresso"},
                {"9", "Frappuccino", "5.00", "frappuccino"},
                {"10", "Irish Coffee", "5.50", "irish_coffee"}
        };

        for (String[] producto : productosPorDefecto) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PRODUCTO_ID, Integer.parseInt(producto[0]));
            values.put(COLUMN_PRODUCTO_NOMBRE, producto[1]);
            values.put(COLUMN_PRODUCTO_PRECIO, Double.parseDouble(producto[2]));
            values.put(COLUMN_PRODUCTO_IMAGEN, producto[3]); // Guardar el nombre como texto

            db.insert(TABLE_PRODUCTOS, null, values);
        }
    }

    // Método para eliminar un producto del carrito
    public void eliminarProductoDelCarrito(int carritoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARRITO, COLUMN_CARRITO_ID + " = ?", new String[]{String.valueOf(carritoId)});
        db.close();
    }

    // Método para vaciar el carrito (por ejemplo, después de un pago exitoso)
    public void eliminarCarrito() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARRITO, null, null);
        db.close();
    }

    // Método para obtener todos los productos
    public ArrayList<Producto> getProductos() {
        ArrayList<Producto> productos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTOS, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_ID));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_NOMBRE));
                double precio = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_PRECIO));
                String nombreImagen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_IMAGEN));

                // Convertir el nombre de la imagen a su ID de recurso
                int idImagen = context.getResources().getIdentifier(nombreImagen, "drawable", context.getPackageName());
                if (idImagen == 0) {
                    idImagen = R.drawable.default_image; // Imagen por defecto si no se encuentra
                }

                Producto producto = new Producto(id, nombre, precio, idImagen);
                productos.add(producto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return productos;
    }

    // Método para obtener los productos en el carrito
    public ArrayList<Carrito> getCarrito() {
        ArrayList<Carrito> carritoList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta para obtener todos los productos del carrito
        String query = "SELECT c." + COLUMN_CARRITO_ID + ", p." + COLUMN_PRODUCTO_NOMBRE + ", p." + COLUMN_PRODUCTO_PRECIO + ", p." + COLUMN_PRODUCTO_IMAGEN + ", c." + COLUMN_CARRITO_CANTIDAD +
                " FROM " + TABLE_CARRITO + " c " +
                "JOIN " + TABLE_PRODUCTOS + " p ON c." + COLUMN_CARRITO_PRODUCTO_ID + " = p." + COLUMN_PRODUCTO_ID;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CARRITO_ID));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_NOMBRE));
                double precio = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_PRECIO));
                String nombreImagen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_IMAGEN)); // Obtener nombre de imagen
                int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CARRITO_CANTIDAD));

                // Convertir el nombre de la imagen a su ID de recurso
                int idImagen = context.getResources().getIdentifier(nombreImagen, "drawable", context.getPackageName());
                if (idImagen == 0) {
                    idImagen = R.drawable.default_image; // Imagen por defecto si no se encuentra
                }

                Carrito carrito = new Carrito(id, nombre, precio, idImagen, cantidad);
                carritoList.add(carrito);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return carritoList;
    }

    // Método para agregar productos al carrito
    public void addToCarrito(int productoId, int cantidad) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Verificar si el producto ya está en el carrito
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CARRITO + " WHERE " + COLUMN_CARRITO_PRODUCTO_ID + " = ?",
                new String[]{String.valueOf(productoId)});

        if (cursor.moveToFirst()) {
            // Si el producto ya está en el carrito, solo actualizamos la cantidad
            int cantidadExistente = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CARRITO_CANTIDAD));
            ContentValues values = new ContentValues();
            values.put(COLUMN_CARRITO_CANTIDAD, cantidadExistente + cantidad); // Sumar la cantidad
            db.update(TABLE_CARRITO, values, COLUMN_CARRITO_PRODUCTO_ID + " = ?",
                    new String[]{String.valueOf(productoId)});
        } else {
            // Si el producto no está en el carrito, lo insertamos
            ContentValues values = new ContentValues();
            values.put(COLUMN_CARRITO_PRODUCTO_ID, productoId);
            values.put(COLUMN_CARRITO_CANTIDAD, cantidad);
            db.insert(TABLE_CARRITO, null, values);
        }
        cursor.close();
        db.close();
    }

    // Método para insertar un producto personalizado
    public long insertarProductoPersonalizado(Producto producto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_PRODUCTO_NOMBRE, producto.getNombre());
        values.put(COLUMN_PRODUCTO_PRECIO, producto.getPrecio());
        values.put(COLUMN_PRODUCTO_IMAGEN, producto.getImagen());

        long id = db.insert(TABLE_PRODUCTOS, null, values);
        db.close();

        return id;
    }
}
