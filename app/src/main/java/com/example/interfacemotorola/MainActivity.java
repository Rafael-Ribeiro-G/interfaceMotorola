package com.example.interfacemotorola;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //O primeiro passo é criar todas as variáveis que vamos precisar chamar futuramente.
    ViewPager2 viewPager; //É a estante do nosso XML
    List<List<AppInfo>> appsList; //É a lista onde vai estar presente todos os aplicativos instalados no celular.
    AppAdapter adapter; //É o responsável por estampar o ícone e nome do aplicativo.

    //Implementação da variável do filtro de luz azul
    //View filtroLuzAzul;

    //Componentes da Dock
    LinearLayout dockLayout; // Dock fixa para 4 apps no rodapé

    List<AppInfo> todosOsApps;

    private static final int APPS_POR_PAGINA = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Define o layout primeiro para construir a hierarquia das Views
        setContentView(R.layout.activity_main);

        // Deixa a janela se estender por baixo das barras do sistema e ativa transparência de forma segura
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }

        //Essa função vai funcionar
        viewPager = findViewById(R.id.viewPager);

        // Impedir que o Android jogue o conteúdo para baixo da barra de status
        if (viewPager != null) {
            androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(viewPager, (v, insets) -> insets);

            // Controla a exibição da Dock e oculta as barras de navegação na Gaveta
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);

                    if (position == 0) {
                        // Esconde a Dock quando entra na Gaveta
                        if (dockLayout != null) dockLayout.setVisibility(View.GONE);
                        // Oculta a barra de navegação dos botões na Gaveta
                        ocultarBarrasSistema(true);
                    } else {
                        // Mostra a Dock nas Telas Iniciais (Página 1+)
                        if (dockLayout != null) dockLayout.setVisibility(View.VISIBLE);
                        // Restaura a exibição das barras do sistema nas páginas normais
                        ocultarBarrasSistema(false);
                    }
                }
            });
        }

        //Referência da View do filtro no XAML
        //filtroLuzAzul = findViewById(R.id.filtroLuzAzul);

        // Referência para a Dock
        dockLayout = findViewById(R.id.dockLayout);

        todosOsApps = new ArrayList<>();

        if (viewPager != null && viewPager.getChildAt(0) instanceof RecyclerView) {
            RecyclerView recyclerViewInterno = (RecyclerView) viewPager.getChildAt(0);
            recyclerViewInterno.setHasFixedSize(true);
            recyclerViewInterno.setItemViewCacheSize(40);
        }

        //Essa função vai ter a função de
        appsList = new ArrayList<>();

        //A função abaixo está sendo utilizada para fazer a buscagem/pesquisa dos aplicativos.
        pegarApps();

        //verificarFiltroLuzAzul();
    }

    // Função para esconder ou exibir os botões de navegação e barra de status do Android
    private void ocultarBarrasSistema(boolean ocultar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                if (ocultar) {
                    controller.hide(WindowInsets.Type.navigationBars());
                    controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                } else {
                    controller.show(WindowInsets.Type.navigationBars());
                }
            }
        } else {
            View decorView = getWindow().getDecorView();
            if (decorView != null) {
                if (ocultar) {
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    );
                } else {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Faz o cálculo da cor ao voltar ao aplicativo
        //verificarFiltroLuzAzul();
    }

    //Função responsável por aplicar a cor amarelado conforme a hora do dia
    //private void verificarFiltroLuzAzul(){
        //Calendar calendar = Calendar.getInstance(); //Retorna a hora no formato 0-23
        //int HoraAtual = calendar.get(Calendar.HOUR_OF_DAY);

        //if (filtroLuzAzul != null) {
            //filtroLuzAzul.setVisibility(View.VISIBLE);

            //int corFiltro;

            //if (HoraAtual >= 8 && HoraAtual < 18) {
                //Adiciona o tom amarelado suave na tela
                //corFiltro = Color.parseColor("#1AF4D03F");
            //} else if (HoraAtual >= 18 && HoraAtual < 21) {
                //Adiciona o tom amarelado médio na tela
                //corFiltro = Color.parseColor("#1AFF9F00");
            //} else {
                //Adiciona o tom amarelado mais forte na tela
                //corFiltro = Color.parseColor("#22E65100");
            //}

            //filtroLuzAzul.setBackgroundColor(corFiltro);
        //}
    //}

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
        todosOsApps.clear();

        //Agora para melhorar o filtro vamos fazer um Loop (for), ele vai criar uma ficha para cada aplicativo.
        for (ResolveInfo info: appsBrutos) {
            //Extrair o nome (label)
            String label = info.loadLabel(getPackageManager()).toString();
            String packageName = info.activityInfo.packageName; //Após o "=" a função do comando vai ser procurar pelo endereço único que será usado para encontrar o aplicativo.
            Drawable icon = info.loadIcon(getPackageManager()); //Carrega o ícone e envia ele para o packageManager.

            // Filtro para não mostrar o próprio Launcher na lista
            if (!packageName.equals(getPackageName())) {
                listaTemporaria.add(new AppInfo(label, packageName, icon));
                todosOsApps.add(new AppInfo(label, packageName, icon));
            }
        }

        // Preenche a Dock fixa no fundo com os 4 primeiros aplicativos encontrados
        configurarDockFixa(listaTemporaria);

        // Divide a lista temporária em blocos de 20 apps por página para a Tela Inicial
        for (int i = 0; i < listaTemporaria.size(); i += APPS_POR_PAGINA) {
            int fim = Math.min(i + APPS_POR_PAGINA, listaTemporaria.size());
            appsList.add(new ArrayList<>(listaTemporaria.subList(i, fim)));
        }

        //Agora irei avisar ao Adapter que a lista já está cheia (passando appsList e todosOsApps).
        adapter = new AppAdapter(this, appsList, todosOsApps);
        if (viewPager != null) {
            viewPager.setAdapter(adapter); //Avisa ao recyclerView que quando for necessário mostrar um ícone é preciso enviar para o adapter.

            // Inicia o launcher na tela principal (Página 1), deixando a Gaveta à esquerda (Página 0)
            viewPager.post(() -> viewPager.setCurrentItem(1, false));
        }
    }

    // Adiciona dinamicamente 4 aplicativos fixos no rodapé da interface (Dock)
    private void configurarDockFixa(List<AppInfo> listaApps) {
        if (dockLayout == null) return;
        dockLayout.removeAllViews();

        int limiteDock = Math.min(4, listaApps.size());
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < limiteDock; i++) {
            AppInfo app = listaApps.get(i);
            View itemView = inflater.inflate(R.layout.item_app, dockLayout, false);

            ImageView icone = itemView.findViewById(R.id.iconeApp);
            TextView nome = itemView.findViewById(R.id.nomeApp);

            if (icone != null) icone.setImageDrawable(app.icon);

            // Oculta o nome na Dock (deixa apenas o ícone)
            if (nome != null) nome.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(app.packageName);
                if (launchIntent != null) {
                    startActivity(launchIntent);
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            itemView.setLayoutParams(params);
            dockLayout.addView(itemView);
        }
    }

    // Se estiver na gaveta (Página 0) e apertar "Voltar", retorna para a Tela Inicial (Página 1)
    @Override
    public void onBackPressed() {
        if (viewPager != null && viewPager.getCurrentItem() == 0) {
            viewPager.setCurrentItem(1, true);
        } else {
            super.onBackPressed();
        }
    }
}