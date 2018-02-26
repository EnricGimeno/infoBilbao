package com.gimeno.enric.infobilbao;

import android.app.Application;

public class InfoBilbao extends Application {

    public String getRssUrl() {
        //return getResources().getString(R.string.avisos_de_agua_y_suministros);
        return "http://www.bilbao.eus/cs/Satellite?language=es&pageid=3000075248&pagename=Bilbaonet/Page/BIO_suscripcionRSS&tipoSus=Avisos&idSec=3000014008";
    }
}
