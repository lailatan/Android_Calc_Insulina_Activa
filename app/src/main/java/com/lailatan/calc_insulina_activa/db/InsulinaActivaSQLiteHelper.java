package com.lailatan.calc_insulina_activa.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lailatan.calc_insulina_activa.entities.Insulina;
import com.lailatan.calc_insulina_activa.entities.InsulinaActiva;

import java.util.ArrayList;

import static com.lailatan.calc_insulina_activa.db.InsulinaActivaContract.InsulinaActivaEntry.*;

public class InsulinaActivaSQLiteHelper extends SQLiteOpenHelper {
    private static final String NOMBRE_DB = "insulinaactiva.db";
    private static final int VERSION_DB = 1;
    private final Context contexto;

    public InsulinaActivaSQLiteHelper(Context context){
        super(context, NOMBRE_DB, null, VERSION_DB);
        this.contexto = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creo DB usando los strings declarado en el contracto

        String SQL_CREAR_TABLA_INSULINAS = "CREATE TABLE " + NOMBRE_TABLA + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMNA_INSULINA_ID + " INTEGER NOT NULL,"
                + COLUMNA_UNIDADES + " DOUBLE NOT NULL,"
                + COLUMNA_DIA_DESDE + " INTEGER NOT NULL,"
                + COLUMNA_MES_DESDE + " INTEGER NOT NULL,"
                + COLUMNA_ANIO_DESDE + " INTEGER NOT NULL,"
                + COLUMNA_HORA_DESDE + " INTEGER NOT NULL,"
                + COLUMNA_MINUTO_DESDE + " INTEGER NOT NULL,"
                + COLUMNA_ACTIVA + " INTEGER NOT NULL DEFAULT 1,"
                + COLUMNA_DESCRIPCION + " TEXT"
                + ");";
        db.execSQL(SQL_CREAR_TABLA_INSULINAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public ArrayList<InsulinaActiva> buscarInsulinaActivas(boolean soloActivas) {
        ArrayList<InsulinaActiva> insulinaActivas = new ArrayList<>();
        InsulinaSQLiteHelper insulinaHelper = new InsulinaSQLiteHelper(contexto);
        SQLiteDatabase db = getReadableDatabase();

        String[] columnas = {
                _ID,
                COLUMNA_INSULINA_ID,
                COLUMNA_UNIDADES,
                COLUMNA_DIA_DESDE,
                COLUMNA_MES_DESDE,
                COLUMNA_ANIO_DESDE,
                COLUMNA_HORA_DESDE,
                COLUMNA_MINUTO_DESDE,
                COLUMNA_ACTIVA,
                COLUMNA_DESCRIPCION
        };

            String whereClause = soloActivas?COLUMNA_ACTIVA + "=?":null;
            String[] whereArgs =  soloActivas? new String[]{"1"} :null;
            String orderby = InsulinaContract.InsulinaEntry._ID + " DESC";

        Cursor cursor = db.query(NOMBRE_TABLA, columnas,whereClause, whereArgs, null, null, orderby);
        try {
            int columnaIdIndex = cursor.getColumnIndex(_ID);
            int columnaInsulinaIdIndex = cursor.getColumnIndex(COLUMNA_INSULINA_ID);
            int columnaUnidadesIndex = cursor.getColumnIndex(COLUMNA_UNIDADES);
            int columnaDiaIndex = cursor.getColumnIndex(COLUMNA_DIA_DESDE);
            int columnaMesIndex = cursor.getColumnIndex(COLUMNA_MES_DESDE);
            int columnaAnioIndex = cursor.getColumnIndex(COLUMNA_ANIO_DESDE);
            int columnaHoraIndex = cursor.getColumnIndex(COLUMNA_HORA_DESDE);
            int columnaMinutoIndex = cursor.getColumnIndex(COLUMNA_MINUTO_DESDE);
            int columnaActivaIndex = cursor.getColumnIndex(COLUMNA_ACTIVA);
            int columnaDescripcionIndex = cursor.getColumnIndex(COLUMNA_DESCRIPCION);

            //itero el por el indice hata que Move next sea false que es cuando llegue al ultimo
            while (cursor.moveToNext()){
                Integer idActual = cursor.getInt(columnaIdIndex);
                Integer insulinaIdActual = cursor.getInt(columnaInsulinaIdIndex);
                Double unidadesActual = cursor.getDouble(columnaUnidadesIndex);
                Integer diaActual = cursor.getInt(columnaDiaIndex);
                Integer mesActual = cursor.getInt(columnaMesIndex);
                Integer anioActual = cursor.getInt(columnaAnioIndex);
                Integer horaActual = cursor.getInt(columnaHoraIndex);
                Integer minutoActual = cursor.getInt(columnaMinutoIndex);
                Integer activaActual = cursor.getInt(columnaActivaIndex);
                String descripcionActual = cursor.getString(columnaDescripcionIndex);


                Insulina insulina=insulinaHelper.buscarInsulinaPorId(insulinaIdActual);
                insulinaActivas.add(new InsulinaActiva(idActual,insulina,unidadesActual,
                        diaActual,mesActual,anioActual,horaActual,minutoActual,activaActual,descripcionActual));
            }
        } finally {
            //cierro cursor
            cursor.close();
            db.close();
        }
        return insulinaActivas;
    }

    public Integer guardarInsulinaActiva(InsulinaActiva insulinaActiva) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMNA_INSULINA_ID,insulinaActiva.getInsulina().getInsulina_id());
        values.put(COLUMNA_UNIDADES,insulinaActiva.getUnidades());
        values.put(COLUMNA_DIA_DESDE,insulinaActiva.getDia_desde());
        values.put(COLUMNA_MES_DESDE,insulinaActiva.getMes_desde());
        values.put(COLUMNA_ANIO_DESDE,insulinaActiva.getAnio_desde());
        values.put(COLUMNA_HORA_DESDE,insulinaActiva.getHora_desde());
        values.put(COLUMNA_MINUTO_DESDE,insulinaActiva.getMinuto_desde());
        values.put(COLUMNA_ACTIVA,insulinaActiva.getActiva());
        values.put(COLUMNA_DESCRIPCION,insulinaActiva.getDescripcion());

        if (insulinaActiva.getInsulina_activa_id()!=0) {
            String whereClause = _ID + "=?";
            String[] whereArgs = {insulinaActiva.getInsulina_activa_id().toString()};
            db.update(NOMBRE_TABLA, values, whereClause, whereArgs);
            db.close();
            return insulinaActiva.getInsulina_activa_id();
        } else {
            long nuevoIdLNG = db.insert(NOMBRE_TABLA, null, values);
            db.close();
            return Long.valueOf(nuevoIdLNG).intValue();
        }
    }

    public void desactivarInsulinaActiva(Integer insulinaActivaId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMNA_ACTIVA,0);

        String whereClause = _ID + "=?";
        String[] whereArgs = {insulinaActivaId.toString()};
        db.update(NOMBRE_TABLA, values, whereClause, whereArgs);
        db.close();
    }

    public void eliminarInsulinaActiva(Integer insulinaActivaId) {
        SQLiteDatabase db = getWritableDatabase();

        String whereClause = _ID + "=?";
        String[] whereArgs = {insulinaActivaId.toString()};

        db.delete(NOMBRE_TABLA, whereClause,whereArgs);
        db.close();
    }

    public void eliminarTodasInsulinaActiva() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(NOMBRE_TABLA, null,null);
        db.close();
    }

    public Boolean insulinaEstaUsadaComoActivaPorId(Integer insulinaId) {
        int cantidad = 0;
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT COUNT (*) FROM " + NOMBRE_TABLA + " WHERE " + COLUMNA_INSULINA_ID  + "=?";

        Cursor cursor= db.rawQuery(sql, new String[] { String.valueOf(insulinaId) });

        try {
            cursor.moveToFirst();
            cantidad = cursor.getInt(0);
        } finally {
        //cierro cursor
            cursor.close();
            db.close();
        }
        return (cantidad > 0);
    }

    public void eliminarTodasInsulinInaActiva() {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = COLUMNA_ACTIVA + "=?";
        String whereArgs[] = {"0"};
        db.delete(NOMBRE_TABLA, whereClause,whereArgs);
        db.close();
    }

    public Integer buscarCantidadInsulinasActivas(boolean soloActivas, boolean soloInactivas) {
        Integer cantidad = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        if (!soloActivas && !soloInactivas) {
            String sql = "SELECT COUNT (*) FROM " + NOMBRE_TABLA ;
            cursor = db.rawQuery(sql, null);
        } else {
            String sql = "SELECT COUNT (*) FROM " + NOMBRE_TABLA + " WHERE " + COLUMNA_ACTIVA  + "=?";
            cursor= db.rawQuery(sql, new String[] { String.valueOf(soloActivas?1:0) });
        }

        try {
            cursor.moveToFirst();
            cantidad = cursor.getInt(0);
        } finally {
            //cierro cursor
            cursor.close();
            db.close();
        }
        return cantidad;
    }
}