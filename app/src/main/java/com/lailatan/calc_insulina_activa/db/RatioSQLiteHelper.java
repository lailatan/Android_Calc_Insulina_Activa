package com.lailatan.calc_insulina_activa.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lailatan.calc_insulina_activa.entities.Ratio;

import java.util.ArrayList;

import static com.lailatan.calc_insulina_activa.db.RatioContract.RatioEntry.*;

public class RatioSQLiteHelper extends SQLiteOpenHelper {
    private static final String NOMBRE_DB = "ratio.db";
    private static final int VERSION_DB = 1;

    public RatioSQLiteHelper(Context context){
        super(context, NOMBRE_DB, null, VERSION_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creo DB usando los strings declarado en el contracto
        String SQL_CREAR_TABLA_RATIOS = "CREATE TABLE " + NOMBRE_TABLA + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMNA_HORA + "	INTEGER UNIQUE NOT NULL,"
                + COLUMNA_VALOR + " DOUBLE NOT NULL"
                + ");";
        db.execSQL(SQL_CREAR_TABLA_RATIOS);

        String SQL_AGREGAR_RATIO_DEFAULT = "INSERT INTO  " + NOMBRE_TABLA + " ("
                + COLUMNA_HORA + "," + COLUMNA_VALOR + ") VALUES ";
        for (int i=0 ;i<24;i++) {
            SQL_AGREGAR_RATIO_DEFAULT += " (" + i + " , 0)";
            if (i==23) SQL_AGREGAR_RATIO_DEFAULT +=";";
            else  SQL_AGREGAR_RATIO_DEFAULT +=",";
        }
        db.execSQL(SQL_AGREGAR_RATIO_DEFAULT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public ArrayList<Ratio> buscarRatios() {
        ArrayList<Ratio> ratios = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] columnas = {_ID, COLUMNA_HORA, COLUMNA_VALOR};

        String orderby = _ID + " ASC";

        Cursor cursor = db.query(NOMBRE_TABLA,
                columnas, null, null, null, null, orderby);
        try {
            int columnaIdIndex = cursor.getColumnIndex(_ID);
            int columnaHoraIndex = cursor.getColumnIndex(COLUMNA_HORA);
            int columnaValorIndex = cursor.getColumnIndex(COLUMNA_VALOR);

            //itero el por el indice hata que Move next sea false que es cuando llegue al ultimo
            while (cursor.moveToNext()){
                Integer idActual = cursor.getInt(columnaIdIndex);
                Integer horaActual = cursor.getInt(columnaHoraIndex);
                Double valorActual = cursor.getDouble(columnaValorIndex);

                ratios.add(new Ratio(idActual,horaActual,valorActual));
            }
        } finally {
            //cierro cursor
            cursor.close();
            db.close();
        }
        return ratios;
    }

    public Ratio buscarRatioPorId(Integer ratioId) {
        Ratio ratio=null ;
        SQLiteDatabase db = getReadableDatabase();

        String[] columnas = {_ID, COLUMNA_HORA, COLUMNA_VALOR};

        String whereClause = _ID + "=?";
        String[] whereArgs = {ratioId.toString()};

        Cursor cursor = db.query(NOMBRE_TABLA,
                columnas, whereClause, whereArgs, null, null, null);
        try {
            int columnaIdIndex = cursor.getColumnIndex(_ID);
            int columnaHoraIndex = cursor.getColumnIndex(COLUMNA_HORA);
            int columnaValorIndex = cursor.getColumnIndex(COLUMNA_VALOR);

            //itero el por el indice hata que Move next sea false que es cuando llegue al ultimo
            while (cursor.moveToNext()){
                Integer idActual = cursor.getInt(columnaIdIndex);
                Integer horaActual = cursor.getInt(columnaHoraIndex);
                Double valorActual = cursor.getDouble(columnaValorIndex);

                ratio = new Ratio(idActual,horaActual,valorActual);
            }
        } finally {
            //cierro cursor
            cursor.close();
            db.close();
        }
        return ratio;
    }

    public Ratio buscarRatioPorHora(Integer horaBuscar) {
        Ratio ratio=null ;
        SQLiteDatabase db = getReadableDatabase();

        String[] columnas = {_ID, COLUMNA_HORA, COLUMNA_VALOR};

        String whereClause = COLUMNA_HORA + "=?";
        String[] whereArgs = {horaBuscar.toString()};

        Cursor cursor = db.query(NOMBRE_TABLA,
                columnas, whereClause, whereArgs, null, null, null);
        try {
            int columnaIdIndex = cursor.getColumnIndex(_ID);
            int columnaHoraIndex = cursor.getColumnIndex(COLUMNA_HORA);
            int columnaValorIndex = cursor.getColumnIndex(COLUMNA_VALOR);

            //itero el por el indice hata que Move next sea false que es cuando llegue al ultimo
            while (cursor.moveToNext()){
                Integer idActual = cursor.getInt(columnaIdIndex);
                Integer horaActual = cursor.getInt(columnaHoraIndex);
                Double valorActual = cursor.getDouble(columnaValorIndex);

                ratio = new Ratio(idActual,horaActual,valorActual);
            }
        } finally {
            //cierro cursor
            cursor.close();
            db.close();
        }
        return ratio;
    }

    public void guardarRatio(Ratio ratio) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMNA_HORA,ratio.getHora());
        values.put(COLUMNA_VALOR,ratio.getValor());

        if (ratio.getRatio_id()!=0) {
            String whereClause = _ID + "=?";
            String[] whereArgs = {ratio.getRatio_id().toString()};
            db.update(NOMBRE_TABLA, values, whereClause, whereArgs);
            db.close();
        }
    }

    public void actualizarTodosRatiosIguales(Double ValorRatio) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMNA_VALOR,ValorRatio);

        db.update(NOMBRE_TABLA, values, null, null);
        db.close();
    }

    public void actualizarParaTest() {
        SQLiteDatabase db = getWritableDatabase();

        for (Integer i=0; i<24;i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMNA_VALOR, i.toString());
            String whereClause = _ID + "=?";
            Integer aux=i+1;
            String[] whereArgs = {aux.toString()};
            db.update(NOMBRE_TABLA, values, whereClause, whereArgs);
        }
        db.close();
    }

    public void actualizarRangoRatiosIgual(Double valor, Integer horaDesde, Integer horaHasta) {
        SQLiteDatabase db = getWritableDatabase();

//        for (Integer i=horaDesde; i<=horaHasta;i++) {
        if (horaHasta < horaDesde) {
            for (Integer i=horaDesde; i<=23;i++) {
                guardarRatio(new Ratio(i+1,i,valor));
            }
            for (Integer i = 0; i <= horaHasta; i++) {
                guardarRatio(new Ratio(i + 1, i, valor));
            }
        }else{
            for (Integer i=horaDesde; i<=horaHasta;i++) {
                guardarRatio(new Ratio(i+1,i,valor));
            }

        }
    }
}
