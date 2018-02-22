package com.gimeno.enric.infobilbao.db;

import android.provider.BaseColumns;

// CONSTANTES DE LA BASE DE DATOS
// Clase para abstraer los nombres de tabla y campos
public class BilbaoFeedsDB {

    //Nombre de la base de datos
    public static final String DB_NAME = "BilbaoRSS.db";
    //Version de la base de datos
    public  static final int DB_VERSION = 1;

    // Esta clase no debe ser instanciada la pasamos a privada
    private BilbaoFeedsDB(){

    }
    // Definicion de la tabla posts y sus constantes
    public static  final class Posts implements BaseColumns {
        // Esta clase no debe ser instanciada la pasamos a privada
        private Posts(){}

        // Ponemos la tabla en orden por defecto
        public static  final String DEFAULT_SORT_ORDER = "_ID DESC";
        // Abstraccion de los nombres de campos y tabla a constantes
        // para facilitar cambios en la estructura interna de la BD

        public static final String NOMBRE_TABLA = "bilbao_feeds";

        public static final String CAMPO_TITLE = "title";
        public static final String CAMPO_GUID = "guid";
        public static final String CAMPO_PUB_DATE = "pubDate";
        public static final String CAMPO_URL_LINK = "link";
        public static final String CAMPO_DESCRIPTION = "description";
        public static final String CAMPO_CLASE_FEED = "clase_de_rss";
    }
}
