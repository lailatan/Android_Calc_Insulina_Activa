package com.lailatan.calc_insulina_activa.db;

import android.provider.BaseColumns;

public class RatioContract {

    public RatioContract() {
    }

    public static final class RatioEntry implements BaseColumns {
        /** Nombre de la tabla */
        public final static String NOMBRE_TABLA = "ratios";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMNA_HORA = "hora";
        public final static String COLUMNA_VALOR = "valor";

    }
}
