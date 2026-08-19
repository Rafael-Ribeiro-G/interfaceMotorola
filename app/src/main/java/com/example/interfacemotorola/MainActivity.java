package com.example.interfacemotorola;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //O primeiro passo é criar todas as variáveis que vamos precisar chamar futuramente.
    RecyclerView recyclerView; //É a estante do nosso XML
    List<AppInfo> appsList; //É a lista onde vai estar presente todos os aplicativos instalados no celular.
    AppAdapter adapter; //É o responsável por estampar o ícone e nome do aplicativo.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Essa função vai funcionar
        recyclerView = findViewById(R.id.appList);

        //Essa função vai ter a função de
        appsList = new ArrayList<>();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //A função abaixo está sendo utilizada para fazer a buscagem/pesquisa dos aplicativos.
        pegarApps();

    }

        //A função abaixo vai ter a responsabilidade de buscar os aplicativos.
        private void pegarApps() {
            //Busca todos os aplicativos que tenham função de "ACTION_MAIN", no caso seria a página inicial so aplicativo, quando abre é a primeira coisa que aparece na tela.
            Intent intent = new Intent(Intent.ACTION_MAIN, null);

            //É quem vai fazer o pedido do aplicativo para aparecer na interface e na lista de ícones.
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            //No comando abaixo vou chamar o get.packageManager para me entregar a lista de aplicativos que ele achou com aquele filtro montado anteriormente.
            List<ResolveInfo> appsBrutos = getPackageManager().queryIntentActivities(intent, 0);

            //Agora para melhorar o filtro vamos fazer um Loop (for), ele vai criar uma ficha para cada aplicativo.
            for (ResolveInfo info: appsBrutos) {
                //Extrair o nome (label)
                String label = info.loadLabel(getPackageManager()).toString();
                    String packageName = info.activityInfo.packageName; //Após o "=" a função do comando vai ser procurar pelo endereço único que será usado para encontrar o aplicativo.
                    Drawable icon = info.loadIcon(getPackageManager()); //Carrega o ícone e envia ele para o packageManager.
                    appsList.add(new AppInfo(label, packageName, icon));
            }

                //Agora irei avisar ao Adapter que a lista já está cheia.
                adapter = new AppAdapter(appsList); //Avisa ao AppAdapter que a lista já está cheia.
                recyclerView.setAdapter(adapter); //Avisa ao recyclerView que quando for necessário mostrar um ícone é preciso enviar para o adapter.
        }
}