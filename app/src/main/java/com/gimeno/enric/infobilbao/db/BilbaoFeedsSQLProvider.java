package com.gimeno.enric.infobilbao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// CREACION DE LA BASE DE DATOS
// Se creara la primera vez que se ejecuta aplicacion
public class BilbaoFeedsSQLProvider extends SQLiteOpenHelper {

    public BilbaoFeedsSQLProvider(Context context) {
        super(context, BilbaoFeedsDB.DB_NAME, null, BilbaoFeedsDB.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        if(db.isReadOnly()){
            db = getWritableDatabase();}

        //Tabla con sus campos
        db.execSQL("CREATE TABLE " + BilbaoFeedsDB.Posts.NOMBRE_TABLA + " (" +
                BilbaoFeedsDB.Posts._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BilbaoFeedsDB.Posts.CAMPO_GUID + " INTEGER," +
                BilbaoFeedsDB.Posts.CAMPO_TITLE + " TEXT," +
                BilbaoFeedsDB.Posts.CAMPO_PUB_DATE + " INTEGER," +
                BilbaoFeedsDB.Posts.CAMPO_URL_LINK + " TEXT UNIQUE," +
                BilbaoFeedsDB.Posts.CAMPO_DESCRIPTION + " TEXT," +
                BilbaoFeedsDB.Posts.CAMPO_CLASE_FEED + " TEXT" +
                ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Cuando haya cambios en la estuctura deberemos incluir el codigo
        // SQL necesario para actualizar la base de datos
        // tendremos en cuenta la versin antigua y la nueva para aplicar solo
        // los necesarios
    }
}
