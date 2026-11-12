package edu.masanz.da.ut3.modelo;

import edu.masanz.da.ut3.gui.GUI;
import edu.masanz.da.ut3.util.Conf;

/** Laberinto, personaje y reglas del juego, sin entrada ni salida de consola. */
public class Mapa {
    private String inicial;
    private String casillas;
    private Personaje personaje;
    private boolean ganada;

    /**
     * Prepara el escenario elegido y crea el personaje inicial.
     * @param numero escenario 1, 2 o 3; cualquier otro valor selecciona el 1
     */
    public Mapa(int numero) {
        switch (numero) {
            case 2 -> inicial = Conf.MAPA_2;
            case 3 -> inicial = Conf.MAPA_3;
            default -> inicial = Conf.MAPA_1;
        }
        casillas = inicial;
        personaje = new Personaje();
        ganada = false;
    }

    /**
     * Consulta el terreno sin superponer al personaje.
     * @param fila fila solicitada, empezando en cero
     * @param columna columna solicitada, empezando en cero
     * @return simbolo como String de un caracter, o null si no existe la casilla
     */
    public String obtenerCasilla(int fila, int columna) {
        if (!contiene(fila, columna)) {
            return null;
        }
        int posicion = fila * getColumnas() + columna;
        return casillas.substring(posicion, posicion + 1);
    }

    /**
     * Sustituye una casilla existente por suelo; fuera del mapa no cambia nada.
     * @param fila fila del objeto recogido
     * @param columna columna del objeto recogido
     */
    public void vaciarCasilla(int fila, int columna) {
        if (contiene(fila, columna)) {
            int posicion = fila * getColumnas() + columna;
            casillas = casillas.substring(0, posicion) + " " + casillas.substring(posicion + 1);
        }
    }

    /**
     * @param fila fila solicitada
     * @param columna columna solicitada
     * @return true si la fila y la columna pertecen al mapa, false en caso contrario
     */
    public boolean contiene(int fila, int columna) {
        // TODO 21: Indica si la fila y la columna solicitadas pertenecen al mapa.
        if (fila < 0 && fila > getFilas()){
            return false;
        } else if (columna < 0 && columna > getColumnas()) {
            return false;
        }
        return true;
    }

    /** @return true cuando se ha ganado el mapa (que la salida ha sido alcanzada) o el personaje no está vivo */
    public boolean estaTerminada() {
        // TODO 22: Determina si el juego ha terminado por victoria o por falta de vida.
        if (!getPersonaje().estaVivo() || ganada){
            return true;
        }
        return false;
    }

    /**
     * Resuelve el movimiento solicitado.
     * No se podrá avanzar si el juego ha terminado o el destino es una pared.
     * Tampoco se puede salir de los límites del mapa.
     * Si el desplazamiento es válido, se deberá registrar el movimiento.
     * @param accion W, A, S o D; otras cadenas y null se rechazan
     * @return true si realiza la acción, false en caso contrario
     */
    public boolean moverJugador(String accion) {
        // TODO 23: Resuelve el movimiento solicitado.

        if (accion == null && estaTerminada()){
            return false;
        }

        int fila = personaje.getFila();
        int columna = personaje.getColumna();

        switch (accion){
            case "W" -> fila--;
            case "S" -> fila++;
            case "D" -> columna ++;
            case "A" -> columna--;
            default -> {
                return false;
            }
        }
        if (!contiene(fila, columna)){
            return false;
        }
        if (obtenerCasilla(fila,columna).equals(Conf.MURO)){
            return false;
        }

        personaje.setFila(fila);
        personaje.setColumna(columna);
        personaje.registrarMovimiento();
        return true;
    }

    /**
     * Aplica el efecto de la casilla en la que se encuentra el jugador.
     * Posibles efectos:
     *    - Un "+" (Botiquín) se recoge y desaparece
     *    - una "T" (Trampa) resta 20 y permanece
     *    - Una "S" activa la victoria.
     */
    public void aplicarEfecto(){
        // TODO 24: Aplica el efecto de la casilla en la que se encuentra el jugador.
        int fila = personaje.getFila();
        int columna = personaje.getColumna();

        if (obtenerCasilla(fila, columna).equals(Conf.BOTIQUIN)){
            personaje.recogerBotiquin();
            vaciarCasilla(fila, columna);
        } else if (obtenerCasilla(fila, columna).equals(Conf.TRAMPA)) {
            personaje.recibirDanio(Conf.DANIO_TRAMPA);
        }else if (obtenerCasilla(fila,columna).equals(Conf.SALIDA)){
            ganada = true;
        }
    }

    /** Restaura el escenario y el mismo personaje y desactiva la victoria. */
    public void reiniciar() {
        casillas = inicial;
        personaje.reiniciar();
        ganada = false;
    }

    /**
     * Genera una representación visual del mapa.
     * Ten en cuenta la posición del personaje, el cual se superpone a cualquier casilla del mapa.
     * Usar mostrarCasilla de la clase GUI para que aparezcan coloreadas y GUI.nuevaLinea() para saltos de línea.
     * Al final, muestra la leyenda y todos los datos del personaje mediante GUI después del mapa.
     */
    public void mostrarMapa() {
        // TODO 25: Dibuja el mapa completo y muestra la información actual del personaje utilizando GUI.
        for (int fila = 0; fila < getFilas(); fila++) {
            for (int columna = 0; columna < getColumnas(); columna++) {
                if (fila == personaje.getFila() &&
                        columna == personaje.getColumna()){
                    GUI.mostrarCasilla(Conf.PERSONAJE);
                }else {
                    GUI.mostrarCasilla(obtenerCasilla(fila, columna));
                }
            }
            GUI.nuevaLinea();
        }
        GUI.mostrarLeyenda();
        GUI.mostrarInfo(personaje.getVida(),
                        personaje.getBotiquines(),
                        personaje.getMovimientos(),
                        personaje.getFila(),
                        personaje.getColumna());
    }

    /**
     * Gestiona con switch las direcciones, un botiquin, reinicio y menu.
     * Informa de los resultados.
     * La accion 0 no modifica datos ni imprime nada desde este metodo.
     * @param accion texto normalizado, nunca null
     */
    public void gestionarAccion(String accion) {
        // TODO 26: Gestiona las acciones del usuario y muestra sus resultados
        switch (accion){
            case "W":
            case "A":
            case "S":
            case "D":
                if (moverJugador(accion)){
                    GUI.mostrarMensaje("Te has despalzado a la fila " + personaje.getFila() +
                                        ", columna " + personaje.getColumna());


                    String casilla = obtenerCasilla(personaje.getFila(), personaje.getColumna());
                    aplicarEfecto();

                    if (casilla.equals(Conf.BOTIQUIN)){
                        GUI.mostrarMensaje("Has recogido un botiquin");
                    } else if (casilla.equals(Conf.TRAMPA)) {
                        GUI.mostrarMensaje("Has pisado una trampa. Pierdes 20 puntos de vida");
                    }
                    if (estaTerminada()){
                        GUI.mostrarMensaje("El juego ha terminado");
                    }
                }
                break;
            case "1":
                if (personaje.usarBotiquin()){
                    GUI.mostrarMensaje("Has usado un botiquin");
                }else {
                    GUI.mostrarMensaje("No cumples los requisistos para usar un botiquin");
                }
                break;
            case "2":
                reiniciar();
                GUI.mostrarMensaje("Mapa reiniciado");
                break;
            case "3":
                GUI.mostrarMenu();
                break;
            case "0":
                break;
            default:
                GUI.mostrarMensaje("Accion desconocida, pulsa 3 para solicitar menu");
                break;
        }

    }

    /** @return numero de filas del escenario */
    public int getFilas() { return Conf.FILAS; }
    /** @return numero de columnas del escenario */
    public int getColumnas() { return Conf.COLUMNAS; }
    /** @return personaje que recorre este mapa */
    public Personaje getPersonaje() { return personaje; }
    /** @return indicador de victoria */
    public boolean isGanada() { return ganada; }
}
