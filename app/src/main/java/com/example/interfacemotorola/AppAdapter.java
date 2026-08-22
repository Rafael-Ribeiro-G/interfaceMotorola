package com.example.interfacemotorola;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//Essa public class tá criada com o extends para poder obedecer, as regras impostas pelo "RecyclerView", além disso, também está sendo usado o "ViewHolder", para mostrar os ícones e nomes dos aplicativos da maneira que foi esquematizada, além de manter dentro do nosso layout.
public class AppAdapter extends RecyclerView.Adapter <AppAdapter.ViewHolder> {
    //Esse comando vai servir para chamar a lista de todos os aplicativos e guarda-lós até que eles sejam apresentados na tela.
    List<AppInfo> appsList;

    //Essa função vai servir para informar para o Android quando a lista acaba.
    @Override
    public int getItemCount() {
        return appsList.size();
    }

    //Essa função serve para criar a "moldura" para os aplicativos.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Esse comando é responsável por transformar o texto em objetos reais, então no caso, ele absorve o texto do arquivo item_app.xml e transforma em objetos reais que podem ser exibidos na tela do celular.
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());

        //Esse comando vai ser responsável por transformar o meu arquivo XML em uma view que pode ser acessada pelo ViewHolderç
        View view = inflater.inflate(R.layout.item_app, parent, false);

        //Este comando devolve a view dentro do ViewHolder.
        return new ViewHolder(view);
    }

    //Essa função vai ser a responsável por apresentar algum item na tela toda vez que for chamado pelo código, no caso ele entrega duas coisas, sendo elas: moldura (ViewHolder) e position (O número do aplicativo).
    @Override
    public void onBindViewHolder (ViewHolder Holder, int position){
        //Pega os dados atuais do aplicativo que está aparecendo na lista
        AppInfo app = appsList.get(position);

        //Adiciona o nome do aplicativo no TextView
        Holder.nomeApp.setText(app.label);

        //Adiciona o ícone do aplicativo no ImageView (Utilizamos o Drawable por se tratar de um fator que é desenhavel)
        Holder.iconeApp.setImageDrawable(app.icon);

        //Essa função abaixo vai fazer com que o Launcher/Interface seja capaz de abrir os aplicativos assim que eles forem clicados.
        //Adiciona a escuta do clique
        Holder.itemView.setOnClickListener(v -> {
            //Cria o caminho para abrir o aplicativo usando o ID (packageName) dele
            Intent intent = v.getContext().getPackageManager().getLaunchIntentForPackage(app.packageName);

            //Se o Android encontrar o aplicativo ele vai abrir
            if (intent != null) {
                v.getContext().startActivity(intent);
            }
        });
    }

    //Esse construtor vai servir para receber a lista de aplicativos que vão ser mostrados, no caso ele chama todos os aplicativos que estão dentro da lista, que automaticamente são todos os palicativos instalados dentro do celular.
    public AppAdapter(List<AppInfo>appsList) {
        this.appsList = appsList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Essas variáveis vão ser responsáveis por pedir o ícone e nome do aplicativo
        ImageView iconeApp;
        TextView nomeApp;

        public ViewHolder (View itemView) {
            //O itemView é a parte "sólida"/"consolidada", ele é o que mantém o ícone e o nome do aplicativo juntos, então quando o Android chamar por determinado aplicativo específico ele vai ser a ferramenta que vai fazer eles aparecem juntos.

            //É o comando que chama o construtor e se refere a classe pai, que nesse caso seria o RecyclerView.
            super(itemView);

            //Esses dois comandos estão sendo utilizados para chamar o ícone e o nome do aplicativo pelo ID deles, fazendo assim com que o itemView possa visualizar e continuar o processo de impressão deles na tela.
            iconeApp = itemView.findViewById(R.id.iconeApp);
            nomeApp = itemView.findViewById(R.id.nomeApp);

        }

    }
}
