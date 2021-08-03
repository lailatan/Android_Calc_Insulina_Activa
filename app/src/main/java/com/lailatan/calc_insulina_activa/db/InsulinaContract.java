package com.lailatan.calc_insulina_activa.db;

import android.provider.BaseColumns;

public class InsulinaContract {

    public InsulinaContract() {
    }

    public static final class InsulinaEntry implements BaseColumns {
        /** Nombre de la tabla */
        public final static String NOMBRE_TABLA = "insulinas";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMNA_NOMBRE = "nombre";
        public final static String COLUMNA_LABORATORIO = "laboratorio";
        public final static String COLUMNA_RAPIDA = "rapida";
        public final static String COLUMNA_DURACION_MINUTOS = "duracion_minutos";
        public final static String COLUMNA_DESCRIPCION = "descripcion";

    }
}
