package com.gimeno.enric.infobilbao.activity;


import android.app.ListActivity;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.gimeno.enric.infobilbao.InfoBilbao;
import com.gimeno.enric.infobilbao.R;
import com.gimeno.enric.infobilbao.RSSParser.RssDownloadHelper;
import com.gimeno.enric.infobilbao.db.BilbaoFeedsDB;

public class ListAlertActivity extends ListActivity{

    private static final long FRECUENCIA_ACTUALIZACION = 60*60*1000; // Recarga cada hora

    private ActualizarPostAsyncTask tarea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Barra de progreso: Debe ir antes de cargar el layout
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.feeds);
        setTitle(R.string.titulo_alerta);
        // Se encargará de cargar la información de la base de datos en nuestro objeto ListView
        configurarAdapter();
    }

    private void configurarAdapter() {
        // Obtenemos todos las alertas de la BD
        /*
		 * Paso 1: Obtenemos un cursor con todos los articulos de la base de
		 * datos
		 */

        final String[] columnas = new String[]{BilbaoFeedsDB.Posts._ID,//0
                BilbaoFeedsDB.Posts.CAMPO_TITLE,//1
                BilbaoFeedsDB.Posts.CAMPO_PUB_DATE,//
                BilbaoFeedsDB.Posts.CAMPO_DESCRIPTION
        };

        Uri uri = Uri.parse("content://es.infobilbao.alerts/post");
        // Query "managed": la actividad se encargar de cerrar y volver a
        // cargar el cursor cuando sea necesario
        Cursor cursor = managedQuery(uri,columnas,null,null, BilbaoFeedsDB.Posts.CAMPO_PUB_DATE + " DESC");
        //Cursor cursor = getContentResolver().query(uri, columnas, null, null, BilbaoFeedsDB.Posts.CAMPO_PUB_DATE + " DESC");

        // Queremos enterarnos si cambian los datos para recargar el cursor
        cursor.setNotificationUri(getContentResolver(), uri);
        // Para que la actividad se encarge de manejar el cursor según sus ciclos de vida
        startManagingCursor(cursor);


        /*
		 * Paso 2: mapeamos los datos del cursor para asociarlos a los campos de
		 * la vista
		 */

        // Mapeamos las querys SQL a los campos de las vistas
        String[] camposDb = new String[]{BilbaoFeedsDB.Posts.CAMPO_TITLE, BilbaoFeedsDB.Posts.CAMPO_PUB_DATE,
                BilbaoFeedsDB.Posts.CAMPO_DESCRIPTION};
        int[] camposView = new int[]{R.id.feedTitulo, R.id.feedFecha,
                R.id.feedTexto};

        /*
		 * Paso 3: creamos el Adapter
		 */

        // Con los objetos anteriores creamos el adapter
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.feeds_item,
                cursor, camposDb, camposView);

        /*
		 * Paso 4: personalizamos el adapter
		 */

        // Las fechas las mostraremos en el formato del terminal (La fecha se debe formatear)
        // Para eso definimos un binder que se encargara de cargar el campo en
        // la vista

        final java.text.DateFormat dateFormat = DateFormat.getLongDateFormat(ListAlertActivity.this);
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.feedFecha) {
                    long timestamp = cursor.getLong(columnIndex);
                    ((TextView) view).setText(dateFormat.format(timestamp));
                    return true;
                } else {

                    return false;// Que se encarge el adapter
                }
            }
        });
        // Todo listo. Cargamos el adapter
        setListAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Creará y lanzará la tarea asíncrona de actualización
        cargarNoticias();
    }

    private void cargarNoticias() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        long ultima = prefs.getLong("ultima actualizacion",0);
        // Si la frecuencia de actualizacion es mayor se debe de actualizar
        if ((System.currentTimeMillis() - ultima) > FRECUENCIA_ACTUALIZACION){
            tarea = new ActualizarPostAsyncTask();
            tarea.execute();
        }

    }

    /*Las tareas asíncronas consumen muchos recursos, por eso es buena idea parar
   la tarea si está en ejecución cuando nuestra Activity pase a segundo plano. */
    @Override
    protected void onStop() {
        // Si hay una tarea corriendo en segundo plano, la paramos
        if(tarea!=null && !tarea.getStatus().equals(AsyncTask.Status.FINISHED)){
            tarea.cancel(true);
        }
        super.onStop();
    }
    // Carga del menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_info_euskadi, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_quit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*Ya tenemos nuestro menú operativo, lo único que nos queda es gestionar la
    selección de una noticia. Al pulsar sobre una se deberá abrir la Activity de
    detalle a la que pasaremos el id de noticia para ver (campo _ID de la consulta):*/
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent();
        i.setClass(ListAlertActivity.this, AlertActivity.class);
        i.putExtra("idNoticia", id);
        startActivity(i);
    }

    /*También hemos creado un método auxiliar que nos permiten mostrar y ocultar
   la barra de progreso.*/
    private void setBarraProgresoVisible(boolean visible) {
        final Window window = getWindow();
        if(visible){
            window.setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
            // Flag for setting the progress bar's indeterminate mode on
            // para ver el progresso...
            window.setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_INDETERMINATE_ON);
        }else{
            window.setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_OFF);
        }
    }

    /*La actualización de la base de datos se realizará en un se-
   gundo plano, para ello necesitamos una clase AsyncTask que definiremos como
   clase Interna: */
    /*La clase AsyncTask es una clase muy útil para ejecutar tareas en segundo plano
    y mostrar resultados en el hilo principal. */
    class ActualizarPostAsyncTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            setBarraProgresoVisible(true);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            InfoBilbao app = (InfoBilbao) getApplication();
            RssDownloadHelper.updateRssData(app.getRssUrl(),getContentResolver());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("ultima actualizacion", System.currentTimeMillis());
            editor.commit();
            setBarraProgresoVisible(false);
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {
            setBarraProgresoVisible(false);
            // Se ha cancelado la proxima vez que arranque debera volver a cargarla
            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("ultima actualizacion", 0);
            editor.commit();
            super.onCancelled();
        }
    }
}
