package com.example.interfacemotorola;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//Essa public class tá criada com o extends para poder obedecer, as regras impostas pelo "RecyclerView", além disso, também está sendo usado o "ViewHolder", para mostrar os ícones e nomes dos aplicativos da maneira que foi esquematizada, além de manter dentro do nosso layout.
public class AppAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_GAVETA = 0;
    private static final int TYPE_PAGINA = 1;

    //Esse comando vai servir para chamar a lista de todos os aplicativos e guarda-lós até que eles sejam apresentados na tela.
    List<List<AppInfo>> appsList;
    List<AppInfo> todosOsApps;
    Context context;

    //Esse construtor vai servir para receber a lista de aplicativos que vão ser mostrados
    public AppAdapter(Context context, List<List<AppInfo>> appsList, List<AppInfo> todosOsApps) {
        this.context = context;
        this.appsList = appsList;
        this.todosOsApps = todosOsApps;
    }

    @Override
    public int getItemViewType(int position) {
        // Posição 0 é reservada para a Gaveta de Aplicativos
        return (position == 0) ? TYPE_GAVETA : TYPE_PAGINA;
    }

    //Essa função vai servir para informar para o Android quando a lista acaba (+1 conta a gaveta da esquerda).
    @Override
    public int getItemCount() {
        return appsList.size() + 1;
    }

    //Essa função serve para criar a "moldura" para os aplicativos.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_GAVETA) {
            // Infla o arquivo de layout da gaveta customizado
            View view = LayoutInflater.from(context).inflate(R.layout.pagina_gaveta, parent, false);
            return new GavetaViewHolder(view);
        } else {
            RecyclerView recyclerView = new RecyclerView(parent.getContext());
            recyclerView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            return new PaginaViewHolder(recyclerView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_GAVETA) {
            GavetaViewHolder gHolder = (GavetaViewHolder) holder;
            if (gHolder.recyclerViewGaveta != null) {
                // Aplica a grade de 4 colunas na RecyclerView da gaveta
                gHolder.recyclerViewGaveta.setLayoutManager(new GridLayoutManager(context, 4));
                ItemAdapter gavetaInnerAdapter = new ItemAdapter(todosOsApps, true);
                gHolder.recyclerViewGaveta.setAdapter(gavetaInnerAdapter);
            }
        } else {
            PaginaViewHolder pHolder = (PaginaViewHolder) holder;

            // Converter DPs em Pixels para as margens da Tela Inicial
            float density = context.getResources().getDisplayMetrics().density;
            int paddingTopPx = (int) (60 * density);   // Espaço para a Barra de Notificações
            int paddingBottomPx = (int) (200 * density); // Espaço para a Dock no rodapé

            pHolder.recyclerViewPagina.setPadding(0, paddingTopPx, 0, paddingBottomPx);
            pHolder.recyclerViewPagina.setClipToPadding(false);

            // Subtrai 1 da posição para pegar a lista correta da appsList
            List<AppInfo> appsDaPagina = appsList.get(position - 1);

            pHolder.recyclerViewPagina.setLayoutManager(new GridLayoutManager(context, 4));
            ItemAdapter innerAdapter = new ItemAdapter(appsDaPagina, false);
            pHolder.recyclerViewPagina.setAdapter(innerAdapter);
        }
    }

    // Atualize o GavetaViewHolder para buscar a RecyclerView pelo ID do XML
    public class GavetaViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerViewGaveta;

        public GavetaViewHolder(View itemView) {
            super(itemView);
            recyclerViewGaveta = itemView.findViewById(R.id.recyclerGavetaPagina);
        }
    }

    // ViewHolder para as demais Páginas (Tela Inicial)
    public class PaginaViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerViewPagina;

        public PaginaViewHolder(View itemView) {
            super(itemView);
            recyclerViewPagina = (RecyclerView) itemView;
        }
    }

    // Adapter interno para renderizar cada aplicativo individual dentro da página do ViewPager2
    private class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
        private List<AppInfo> itens;
        private boolean isGaveta;

        public ItemAdapter(List<AppInfo> itens, boolean isGaveta) {
            this.itens = itens;
            this.isGaveta = isGaveta;
        }

        @Override
        public int getItemCount() {
            return itens.size();
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_app, parent, false);

            int width = parent.getMeasuredWidth() > 0 ? parent.getMeasuredWidth() / 4 : ViewGroup.LayoutParams.WRAP_CONTENT;

            int height;
            if (isGaveta) {
                // Na gaveta, a altura é livre para permitir rolar a lista verticalmente
                height = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else {
                // Na Tela Inicial, calcula o espaço útil descontando o topo (60dp) e a Dock (200dp)
                float density = parent.getContext().getResources().getDisplayMetrics().density;
                int espacoReservado = (int) ((60 + 200) * density);
                int alturaUtil = parent.getMeasuredHeight() - espacoReservado;

                // Divide a altura útil restante igualmente pelas 5 linhas da grade
                height = alturaUtil > 0 ? (alturaUtil / 5) : ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            view.setLayoutParams(new ViewGroup.LayoutParams(width, height));

            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            AppInfo app = itens.get(position);

            if (holder.nomeApp != null) {
                holder.nomeApp.setText(app.label);
                holder.nomeApp.setVisibility(View.VISIBLE);
            }

            if (holder.iconeApp != null) {
                holder.iconeApp.setImageDrawable(app.icon);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = v.getContext().getPackageManager().getLaunchIntentForPackage(app.packageName);
                if (intent != null) {
                    v.getContext().startActivity(intent);
                }
            });
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            android.widget.ImageView iconeApp;
            android.widget.TextView nomeApp;

            public ItemViewHolder(View itemView) {
                super(itemView);
                iconeApp = itemView.findViewById(R.id.iconeApp);
                nomeApp = itemView.findViewById(R.id.nomeApp);
            }
        }
    }
}