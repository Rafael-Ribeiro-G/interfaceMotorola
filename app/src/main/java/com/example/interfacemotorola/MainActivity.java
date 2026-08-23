package com.example.interfacemotorola;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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
    View filtroLuzAzul;

    //Componentes da Gaveta vinda do Topo
    LinearLayout gavetaApps;
    RecyclerView recyclerGaveta;
    View handleFecharGaveta;
    LinearLayout dockLayout; // Dock fixa para 4 apps no rodapé

    List<AppInfo> todosOsApps;
    boolean gavetaAberta = false;
    private GestureDetector gestureDetectorAbertura;
    private GestureDetector gestureDetectorFechamento;

    private static final int APPS_POR_PAGINA = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Deixa as barras do sistema 100% transparentes para a gaveta aparecer por baixo delas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }

        //Essa função vai funcionar
        viewPager = findViewById(R.id.viewPager);

        //Referência da View do filtro no XAML
        filtroLuzAzul = findViewById(R.id.filtroLuzAzul);

        // Referências para a Gaveta de Aplicativos vinda do topo
        gavetaApps = findViewById(R.id.gavetaApps);
        recyclerGaveta = findViewById(R.id.recyclerGaveta);
        handleFecharGaveta = findViewById(R.id.handleFecharGaveta);
        dockLayout = findViewById(R.id.dockLayout);

        todosOsApps = new ArrayList<>();

        // Posiciona a gaveta escondida no topo logo que a interface é renderizada
        gavetaApps.post(() -> gavetaApps.setTranslationY(-gavetaApps.getHeight()));

        if (viewPager.getChildAt(0) instanceof RecyclerView) {
            RecyclerView recyclerViewInterno = (RecyclerView) viewPager.getChildAt(0);
            recyclerViewInterno.setHasFixedSize(true);
            recyclerViewInterno.setItemViewCacheSize(40);
        }

        //Essa função vai ter a função de
        appsList = new ArrayList<>();

        // Aplica o padding interno na gaveta para os apps não ficarem escondidos atrás das barras
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            gavetaApps.setPadding(
                    gavetaApps.getPaddingLeft(),
                    systemBars.top,
                    gavetaApps.getPaddingRight(),
                    systemBars.bottom
            );
            return insets;
        });

        // Detector para ABRIR a gaveta puxando para baixo na tela inicial
        gestureDetectorAbertura = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 != null && e2 != null) {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();

                    if (Math.abs(diffY) > Math.abs(diffX) * 2 && diffY > 100 && velocityY > 100) {
                        if (!gavetaAberta) {
                            abrirGaveta();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        // Detector para FECHAR a gaveta deslizando para cima na barra inferior (handle)
        gestureDetectorFechamento = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 != null && e2 != null) {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();

                    if (Math.abs(diffY) > Math.abs(diffX) && diffY < -50) {
                        if (gavetaAberta) {
                            fecharGaveta();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        // Aplica o gesto de deslizar para cima na barrinha
        if (handleFecharGaveta != null) {
            handleFecharGaveta.setOnTouchListener((v, event) -> {
                gestureDetectorFechamento.onTouchEvent(event);
                return true;
            });
        }

        //A função abaixo está sendo utilizada para fazer a buscagem/pesquisa dos aplicativos.
        pegarApps();

        verificarFiltroLuzAzul();
    }

    // Intercepta os gestos na tela inicial apenas para ABRIR a gaveta
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!gavetaAberta && gestureDetectorAbertura != null && gestureDetectorAbertura.onTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Faz o cálculo da cor ao voltar ao aplicativo
        verificarFiltroLuzAzul();
    }

    // Função para abrir a gaveta deslizando do topo para baixo
    public void abrirGaveta() {
        gavetaApps.setVisibility(View.VISIBLE);
        gavetaApps.animate()
                .translationY(0)
                .setDuration(300)
                .withEndAction(() -> gavetaAberta = true)
                .start();
    }

    // Função para fechar a gaveta deslizando de volta para cima
    public void fecharGaveta() {
        gavetaApps.animate()
                .translationY(-gavetaApps.getHeight())
                .setDuration(300)
                .withEndAction(() -> {
                    gavetaApps.setVisibility(View.INVISIBLE);
                    gavetaAberta = false;
                })
                .start();
    }

    //Função responsável por aplicar a cor amarelado conforme a hora do dia
    private void verificarFiltroLuzAzul(){
        Calendar calendar = Calendar.getInstance(); //Retorna a hora no formato 0-23
        int HoraAtual = calendar.get(Calendar.HOUR_OF_DAY);

        filtroLuzAzul.setVisibility(View.VISIBLE);

        int corFiltro;

        if (HoraAtual >= 8 && HoraAtual < 18) {
            //Adiciona o tom amarelado suave na tela
            corFiltro = Color.parseColor("#1AF4D03F");
        } else if (HoraAtual >= 18 && HoraAtual < 21) {
            //Adiciona o tom amarelado médio na tela
            corFiltro = Color.parseColor("#1AFF9F00");
        } else {
            //Adiciona o tom amarelado mais forte na tela
            corFiltro = Color.parseColor("#22E65100");
        }

        filtroLuzAzul.setBackgroundColor(corFiltro);
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

        // 1. Configura a Gaveta de Aplicativos (Grade vertical de 4 colunas com todos os apps)
        if (recyclerGaveta != null) {
            recyclerGaveta.setLayoutManager(new GridLayoutManager(this, 4));
            GavetaAdapter gavetaAdapter = new GavetaAdapter(this, todosOsApps);
            recyclerGaveta.setAdapter(gavetaAdapter);
        }

        // Divide a lista temporária em blocos de 20 apps por página para a Tela Inicial
        for (int i = 0; i < listaTemporaria.size(); i += APPS_POR_PAGINA) {
            int fim = Math.min(i + APPS_POR_PAGINA, listaTemporaria.size());
            appsList.add(new ArrayList<>(listaTemporaria.subList(i, fim)));
        }

        //Agora irei avisar ao Adapter que a lista já está cheia.
        adapter = new AppAdapter(this, appsList); //Avisa ao AppAdapter que a lista já está cheia.
        viewPager.setAdapter(adapter); //Avisa ao recyclerView que quando for necessário mostrar um ícone é preciso enviar para o adapter.
    }

    // Adiciona dinamicamente 4 aplicativos fixos no rodapé da interface (Dock)
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
            if (nome != null) nome.setText(app.label);

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

    // Fecha a gaveta caso ela esteja aberta ao apertar o botão "Voltar" do celular
    @Override
    public void onBackPressed() {
        if (gavetaAberta) {
            fecharGaveta();
        } else {
            super.onBackPressed();
        }
    }
}