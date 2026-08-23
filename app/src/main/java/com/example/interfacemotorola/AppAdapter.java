package com.example.interfacemotorola;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//Essa public class tá criada com o extends para poder obedecer, as regras impostas pelo "RecyclerView", além disso, também está sendo usado o "ViewHolder", para mostrar os ícones e nomes dos aplicativos da maneira que foi esquematizada, além de manter dentro do nosso layout.
public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
    //Esse comando vai servir para chamar a lista de todos os aplicativos e guarda-lós até que eles sejam apresentados na tela.
    List<List<AppInfo>> appsList;
    Context context;

    //Esse construtor vai servir para receber a lista de aplicativos que vão ser mostrados, no caso ele chama todos os aplicativos que estão dentro da lista, que automaticamente são todos os palicativos instalados dentro do celular.
    public AppAdapter(Context context, List<List<AppInfo>> appsList) {
        this.context = context;
        this.appsList = appsList;
    }

    //Essa função vai servir para informar para o Android quando a lista acaba.
    @Override
    public int getItemCount() {
        return appsList.size();
    }

    //Essa função serve para criar a "moldura" para os aplicativos.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView recyclerView = new RecyclerView(parent.getContext());
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        //Este comando devolve a view dentro do ViewHolder.
        return new ViewHolder(recyclerView);
    }

    //Essa função vai ser a responsável por apresentar algum item na tela toda vez que for chamado pelo código, no caso ele entrega duas coisas, sendo elas: moldura (ViewHolder) e position (O número do aplicativo).
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        List<AppInfo> appsDaPagina = appsList.get(position);

        holder.recyclerViewPagina.setLayoutManager(new GridLayoutManager(context, 4));

        ItemAdapter innerAdapter = new ItemAdapter(appsDaPagina);
        holder.recyclerViewPagina.setAdapter(innerAdapter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerViewPagina;

        public ViewHolder(View itemView) {
            //É o comando que chama o construtor e se refere a classe pai, que nesse caso seria o RecyclerView.
            super(itemView);
            recyclerViewPagina = (RecyclerView) itemView;
        }
    }

    // Adapter interno para renderizar cada aplicativo individual dentro da página do ViewPager2
    private class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
        private List<AppInfo> itens;

        public ItemAdapter(List<AppInfo> itens) {
            this.itens = itens;
        }

        @Override
        public int getItemCount() {
            return itens.size();
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_app, parent, false);

            int width = parent.getMeasuredWidth() / 4;
            int height = parent.getMeasuredHeight() / 5;
            view.setLayoutParams(new ViewGroup.LayoutParams(width, height));

            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            AppInfo app = itens.get(position);

            holder.nomeApp.setText(app.label);
            holder.iconeApp.setImageDrawable(app.icon);

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