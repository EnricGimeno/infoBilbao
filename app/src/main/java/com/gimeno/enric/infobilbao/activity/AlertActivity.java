package com.gimeno.enric.infobilbao.activity;


import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.gimeno.enric.infobilbao.R;
import com.gimeno.enric.infobilbao.db.BilbaoFeedsDB;

public class AlertActivity extends Activity {

    private TextView titulo;
    private TextView fecha;
    private WebView contenido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alert);

        setTitle(R.string.titulo_alerta);

        titulo = (TextView) findViewById(R.id.feedTitulo);
        // Al pulsar sobre el titulo se cerrara la ventana
        titulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fecha = (TextView) findViewById(R.id.feedFecha);
        contenido = (WebView) findViewById(R.id.feedContenido);
    }

    @Override
    protected void onStart() {
        super.onStart();

        try{
            // Ontenemos el id de la noticia que queda guardado en el OnListItemClick
            Bundle extras = getIntent().getExtras();
            long idNoticia = extras.getLong("idNoticia");
            final String[] columnas = new String[] {BilbaoFeedsDB.Posts._ID,//0
                    BilbaoFeedsDB.Posts.CAMPO_TITLE,//1
                    BilbaoFeedsDB.Posts.CAMPO_PUB_DATE,//2
                    BilbaoFeedsDB.Posts.CAMPO_DESCRIPTION//3
            };

            Uri uri = Uri.parse("content://es.infobilbao.alerts/post");
            // Añadimos a la uri la parte del id de la noticia
            uri = ContentUris.withAppendedId(uri, idNoticia);
            // Query "managed": la actividad se encargar de cerrar y volver a
            // cargar el cursor cuando sea necesario
            Cursor cursor = managedQuery(uri,columnas, null, null, BilbaoFeedsDB.Posts.CAMPO_PUB_DATE + " DESC");
            // Queremos enterarnos si cambian los datos para recargar el cursor
            cursor.setNotificationUri(getContentResolver(),uri);

            // Para que la actividad se encarge de manejar el cursor
            // segun sus ciclos de vida
            startManagingCursor(cursor);

            // Mostramos los datos del cursor en la vista
            if(cursor.moveToFirst()){
                titulo.setText(cursor.getString(1));
                java.text.DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(AlertActivity.this);
                fecha.setText(dateFormat.format(cursor.getLong(2)));
                String texto = new String(cursor.getString(3).getBytes(), "utf-8");
                contenido.loadDataWithBaseURL(null,texto,"texto/html","UTF-8",null);
            }


        }catch (Exception e){
            e.printStackTrace();

        }
    }

}
