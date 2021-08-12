package com.lailatan.calc_insulina_activa.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lailatan.calc_insulina_activa.InsulinaActivaCalcularActivity;
import com.lailatan.calc_insulina_activa.entities.Insulina;
import static com.lailatan.calc_insulina_activa.db.InsulinaContract.InsulinaEntry.*;
import java.util.ArrayList;

public class InsulinaSQLiteHelper extends SQLiteOpenHelper {
    private static String NOMBRE_DB = "insulina.db";
    private static final int VERSION_DB = 1;

    public InsulinaSQLiteHelper(Context context){
        super(context, NOMBRE_DB, null, VERSION_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creo DB usando los strings declarado en el contracto
        String SQL_CREAR_TABLA_INSULINAS = "CREATE TABLE " + NOMBRE_TABLA + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMNA_NOMBRE + "	TEXT NOT NULL,"
                + COLUMNA_LABORATORIO + " TEXT,"
                + COLUMNA_RAPIDA + " INTEGER NOT NULL DEFAULT 1,"
                + COLUMNA_DURACION_MINUTOS + " INTEGER NOT NULL DEFAULT 180,"
                + COLUMNA_DESCRIPCION + " TEXT"
                + ");";
        db.execSQL(SQL_CREAR_TABLA_INSULINAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public ArrayList<Insulina> buscarInsulinas(String buscarSTR) {
        ArrayList<Insulina> insulinas = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] columnas = {_ID, COLUMNA_NOMBRE, COLUMNA_LABORATORIO, COLUMNA_RAPIDA, COLUMNA_DURACION_MINUTOS, COLUMNA_DESCRIPCION};

        String where = " UPPER(" + COLUMNA_NOMBRE  + ") LIKE ?";
        String [] argumentos = {"%" + buscarSTR.toUpperCase() + "%"};

        //String orderby = InsulinaContract.InsulinaEntry._ID + " ASC";

        Cursor cursor = db.query(NOMBRE_TABLA,
                columnas, where, argumentos, null, null, null);
        try {
            int columnaIdIndex = cursor.getColumnIndex(_ID);
            int columnaNombreIndex = cursor.getColumnIndex(COLUMNA_NOMBRE);
            int columnaLaboratorioIndex = cursor.getColumnIndex(COLUMNA_LABORATORIO);
            int columnaRapidaIndex = cursor.getColumnIndex(COLUMNA_RAPIDA);
            int columnaDuracionIndex = cursor.getColumnIndex(COLUMNA_DURACION_MINUTOS);
            int columnaDescripcionIndex = cursor.getColumnIndex(COLUMNA_DESCRIPCION);

            //itero el por el indice hata que Move next sea false que es cuando llegue al ultimo
            while (cursor.moveToNext()){
                Integer idActual = cursor.getInt(columnaIdIndex);
                String nombreActual = cursor.getString(columnaNombreIndex);
                String laboratiorActual = cursor.getString(columnaLaboratorioIndex);
                Integer rapidaActual = cursor.getInt(columnaRapidaIndex);
                Integer duracionActual = cursor.getInt(columnaDuracionIndex);
                String descripcionActual = cursor.getString(columnaDescripcionIndex);

                insulinas.add(new Insulina(idActual,nombreActual,laboratiorActual,rapidaActual,duracionActual,descripcionActual));
            }
        } finally {
            //cierro cursor
            cursor.close();
            db.close();
        }
        return insulinas;
    }

    public Insulina buscarInsulinaPorId(Integer insulinaId) {
        Insulina insulina=null ;
        SQLiteDatabase db = getReadableDatabase();

        String[] columnas = {_ID, COLUMNA_NOMBRE, COLUMNA_LABORATORIO, COLUMNA_RAPIDA, COLUMNA_DURACION_MINUTOS, COLUMNA_DESCRIPCION};

        String whereClause = _ID + "=?";
        String whereArgs[] = {insulinaId.toString()};

        Cursor cursor = db.query(NOMBRE_TABLA,
                columnas, whereClause, whereArgs, null, null, null);
        try {
            int columnaIdIndex = cursor.getColumnIndex(_ID);
            int columnaNombreIndex = cursor.getColumnIndex(COLUMNA_NOMBRE);
            int columnaLaboratorioIndex = cursor.getColumnIndex(COLUMNA_LABORATORIO);
            int columnaRapidaIndex = cursor.getColumnIndex(COLUMNA_RAPIDA);
            int columnaDuracionIndex = cursor.getColumnIndex(COLUMNA_DURACION_MINUTOS);
            int columnaDescripcionIndex = cursor.getColumnIndex(COLUMNA_DESCRIPCION);

            //itero el por el indice hata que Move next sea false que es cuando llegue al ultimo
            while (cursor.moveToNext()){
                Integer idActual = cursor.getInt(columnaIdIndex);
                String nombreActual = cursor.getString(columnaNombreIndex);
                String laboratiorActual = cursor.getString(columnaLaboratorioIndex);
                Integer rapidaActual = cursor.getInt(columnaRapidaIndex);
                Integer duracionActual = cursor.getInt(columnaDuracionIndex);
                String descripcionActual = cursor.getString(columnaDescripcionIndex);

                insulina = new Insulina(idActual,nombreActual,laboratiorActual,rapidaActual,duracionActual,descripcionActual);
            }
        } finally {
            //cierro cursor
            cursor.close();
            db.close();
        }
        return insulina;
    }

    public Integer guardarInsulina(Insulina insulina) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMNA_NOMBRE,insulina.getNombre());
        values.put(COLUMNA_LABORATORIO,insulina.getLaboratorio());
        values.put(COLUMNA_RAPIDA,insulina.getRapida());
        values.put(COLUMNA_DURACION_MINUTOS,insulina.getDuracion_minutos());
        values.put(COLUMNA_DESCRIPCION,insulina.getDescripcion());

        if (insulina.getInsulina_id()!=0) {
            String whereClause = _ID + "=?";
            String whereArgs[] = {insulina.getInsulina_id().toString()};
            db.update(NOMBRE_TABLA, values, whereClause, whereArgs);
            db.close();
            return insulina.getInsulina_id();
        } else {
            long nuevoIdLNG = db.insert(NOMBRE_TABLA, null, values);
            db.close();
            return Long.valueOf(nuevoIdLNG).intValue();
        }
    }

    public void eliminarInsulina(Integer insulinaId) {
        SQLiteDatabase db = getWritableDatabase();

        String whereClause = _ID + "=?";
        String whereArgs[] = {insulinaId.toString()};

        db.delete(NOMBRE_TABLA, whereClause,whereArgs);
        db.close();
    }
}
