package edu.masanz.da.ut3.app;

import edu.masanz.da.ut3.gui.GUI;
import edu.masanz.da.ut3.modelo.Mapa;
import edu.masanz.da.ut3.modelo.Personaje;
import edu.masanz.da.ut3.util.Conf;

import java.security.Guard;

/** Coordina la interfaz de consola y las reglas del mapa. */
public class Main {

    /**
     * Muestra los mapas disponibles y solicita un mapa inicial en un rango del 1 a 3.
     * Repite ante un error o dato no valido.
     * 0 sale sin crear el mapa.
     * Despues muestra el menu y mantiene el juego hasta salir, ganar o morir.
     * Tras cada accion distinta de 0 vuelve a dibujar. al finalizar muestra un unico mensaje.
     * Muestra mensajes personalizados en caso de finalizar el juego por salir, ganar o morir.
     */
    public static void main(String[] args) {
        GUI.mostrarTitulo();
        // TODO 31: Selecciona un mapa controlando las entradas incorrectas y ejecuta el juego hasta que finalice.

        GUI.mostrarMapas();
        int numero = GUI.pedirMapa();

        while (numero < 1 || numero > Conf.NUMERO_MAPAS){
            GUI.mostrarMensaje("Debes elegir una opcion entre 1 y 3 o 0 para salir");

            numero = GUI.pedirMapa();
        }
        if (numero == 0){
            GUI.mostrarMensaje("Has elegido salir. Bye Bye!");
            return;
        }
        Mapa mapa = new Mapa(numero);
        GUI.mostrarMenu();
        mapa.mostrarMapa();

        String accion = GUI.pedirAccion();

        while (!accion.equals("0") && !mapa.estaTerminada()){

            mapa.gestionarAccion(accion);
            mapa.mostrarMapa();

            if (!mapa.estaTerminada()){
                accion = GUI.pedirAccion();
            }
        }

        if (accion.equals("0")){
            GUI.mostrarMensaje("Has elegido salir. Bye Bye!");
        } else if (mapa.isGanada()) {
            GUI.mostrarMensaje("Has encontrado la salida!");
        }else {
            GUI.mostrarMensaje("Te has quedado sin vida. Fin del juego");
        }

    }
}
