package com.example.interfacemotorola;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //O primeiro passo é criar todas as variáveis que vamos precisar chamar futuramente.
    ViewPager2 viewPager; //É a estante do nosso XML
    List<List<AppInfo>> appsList; //É a lista onde vai estar presente todos os aplicativos instalados no celular.
    AppAdapter adapter; //É o responsável por estampar o ícone e nome do aplicativo.

    //Implementação da variável do filtro de luz azul
    View filtroLuzAzul;

    private static final int APPS_POR_PAGINA = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Essa função vai funcionar
        viewPager = findViewById(R.id.viewPager);

        //Referência da View do filtro no XAML
        filtroLuzAzul = findViewById(R.id.filtroLuzAzul);

        if (viewPager.getChildAt(0) instanceof androidx.recyclerview.widget.RecyclerView) {
            androidx.recyclerview.widget.RecyclerView recyclerViewInterno = (androidx.recyclerview.widget.RecyclerView) viewPager.getChildAt(0);
            recyclerViewInterno.setHasFixedSize(true);
            recyclerViewInterno.setItemViewCacheSize(40);
        }

        //Essa função vai ter a função de
        appsList = new ArrayList<>();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //A função abaixo está sendo utilizada para fazer a buscagem/pesquisa dos aplicativos.
        pegarApps();

        verificarFiltroLuzAzul();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Faz o cálculo da cor ao voltar ao aplicativo
        verificarFiltroLuzAzul();

    }

    //Função responsável por aplicar a cor amarelado conforme a hora do dia
    private void verificarFiltroLuzAzul(){
        Calendar calendar = Calendar.getInstance(); //Retorna a hora no formato 0-23
        int HoraAtual = calendar.get(Calendar.HOUR_OF_DAY);

        filtroLuzAzul.setVisibility(View.VISIBLE);

        int corFiltro;

        if(HoraAtual >= 8 && HoraAtual <= 18) {
            //Adiciona o tom amarelado suave na tela
            corFiltro = Color.parseColor("#1AF4D03F");
        } else if(HoraAtual >= 18 && HoraAtual <= 21) {
            //Adiciona o tom amarelado médio na tela
            corFiltro = Color.parseColor("#1AFF9F00");
        } else {
            //Adiciona o tom amarelado mais forte na tela
            corFiltro = Color.parseColor("#22E65100");
        }

        filtroLuzAzul.setBackgroundColor(corFiltro);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(corFiltro);
            getWindow().setNavigationBarColor(corFiltro);
        }
    }

    //A função abaixo vai ter a responsabilidade de buscar os aplicativos.
    private void pegarApps() {
        //Busca todos os aplicativos que tenham função de "ACTION_MAIN", no caso seria a página inicial so aplicativo, quando abre é a primeira coisa que aparece na tela.
        Intent intent = new Intent(Intent.ACTION_MAIN, null);

        //É quem vai fazer o pedido do aplicativo para aparecer na interface e na lista de ícones.
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        //No comando abaixo vou chamar o get.packageManager para me entregar a lista de aplicativos que ele achou com aquele filtro montado anteriormente.
        List<ResolveInfo> appsBrutos = getPackageManager().queryIntentActivities(intent, 0);

        List<AppInfo> listaTemporaria = new ArrayList<>();

        //Garante que a lista comece vazia para não duplicar ícones
        appsList.clear();

        //Agora para melhorar o filtro vamos fazer um Loop (for), ele vai criar uma ficha para cada aplicativo.
        for (ResolveInfo info: appsBrutos) {
            //Extrair o nome (label)
            String label = info.loadLabel(getPackageManager()).toString();
            String packageName = info.activityInfo.packageName; //Após o "=" a função do comando vai ser procurar pelo endereço único que será usado para encontrar o aplicativo.
            Drawable icon = info.loadIcon(getPackageManager()); //Carrega o ícone e envia ele para o packageManager.

            // Filtro para não mostrar o próprio Launcher na lista
            if (!packageName.equals(getPackageName())) {
                listaTemporaria.add(new AppInfo(label, packageName, icon));
            }
        }

        // Divide a lista temporária em blocos de 20 apps por página
        for (int i = 0; i < listaTemporaria.size(); i += APPS_POR_PAGINA) {
            int fim = Math.min(i + APPS_POR_PAGINA, listaTemporaria.size());
            appsList.add(new ArrayList<>(listaTemporaria.subList(i, fim)));
        }

        //Agora irei avisar ao Adapter que a lista já está cheia.
        adapter = new AppAdapter(this, appsList); //Avisa ao AppAdapter que a lista já está cheia.
        viewPager.setAdapter(adapter); //Avisa ao recyclerView que quando for necessário mostrar um ícone é preciso enviar para o adapter.
    }
}