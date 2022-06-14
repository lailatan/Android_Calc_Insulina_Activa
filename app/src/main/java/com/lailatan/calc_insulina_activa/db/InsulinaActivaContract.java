package com.lailatan.calc_insulina_activa.db;

import android.provider.BaseColumns;

public class InsulinaActivaContract {

    public InsulinaActivaContract() {
    }

    public static final class InsulinaActivaEntry implements BaseColumns {
        /** Nombre de la tabla */
        public final static String NOMBRE_TABLA = "insulinas_activas";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMNA_INSULINA_ID = "insulina_id";
        public final static String COLUMNA_UNIDADES = "unidades";
        public final static String COLUMNA_DIA_DESDE = "dia_desde";
        public final static String COLUMNA_MES_DESDE = "mes_desde";
        public final static String COLUMNA_ANIO_DESDE = "anio_desde";
        public final static String COLUMNA_HORA_DESDE = "hora_desde";
        public final static String COLUMNA_MINUTO_DESDE = "minuto_desde";
        public final static String COLUMNA_ACTIVA = "activa";
        public final static String COLUMNA_DESCRIPCION = "descripcion";
        public final static String COLUMNA_MANUAL = "manual";

    }
}