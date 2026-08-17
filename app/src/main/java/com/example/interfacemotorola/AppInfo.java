package com.example.interfacemotorola;

import android.graphics.drawable.Drawable; //This import is usable for Android, he calls to Java every part is drawable so he can put in the screen the visual part of every App

public class AppInfo {
    String label; //Guarda o nome do Aplicativo Ex:. WhatsApp
    String packageName; //Guarda o que seria equivalente ao "RG"/"ID", no caso seria a identificação do aplicativo
    Drawable icon; //Guarda a imagem do aplicativo no caso o ícone

        public AppInfo(String label, String packageName, Drawable icon) {
            this.label = label;
            this.packageName = packageName;
            this.icon = icon;

            //Esse construtor serve para fazer a solicitação de todos os dados que estão presentes nos aplicativos, que no caso seriam, o nome, ID e o ícone
        }
}
