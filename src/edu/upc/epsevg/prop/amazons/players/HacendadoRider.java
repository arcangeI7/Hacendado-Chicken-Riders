/*


88  88    db     dP""b8 888888 88b 88 8888b.     db    8888b.   dP"Yb                    dP""b8 88  88 88  dP""b8 88  dP 888888 88b 88     88""Yb 88 8888b.  888888 88""Yb .dP"Y8
88  88   dPYb   dP   `" 88__   88Yb88  8I  Yb   dPYb    8I  Yb dP   Yb     ________     dP   `" 88  88 88 dP   `" 88odP  88__   88Yb88     88__dP 88  8I  Yb 88__   88__dP `Ybo."
888888  dP__Yb  Yb      88""   88 Y88  8I  dY  dP__Yb   8I  dY Yb   dP     """"""""     Yb      888888 88 Yb      88"Yb  88""   88 Y88     88"Yb  88  8I  dY 88""   88"Yb  o.`Y8b
88  88 dP""""Yb  YboodP 888888 88  Y8 8888Y"  dP""""Yb 8888Y"   YbodP                    YboodP 88  88 88  YboodP 88  Yb 888888 88  Y8     88  Yb 88 8888Y"  888888 88  Yb 8bodP'
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


 */
package edu.upc.epsevg.prop.amazons.players;

import edu.upc.epsevg.prop.amazons.CellType;
import edu.upc.epsevg.prop.amazons.GameStatus;
import edu.upc.epsevg.prop.amazons.IAuto;
import edu.upc.epsevg.prop.amazons.IPlayer;
import edu.upc.epsevg.prop.amazons.Move;
import edu.upc.epsevg.prop.amazons.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Classe de l'objecte jugador HacendadoRider del gran joc de 'Game of the
 * Vateres' modificat i adaptat lleugerament per a l'ocasió
 *
 * @author Roger Pérez àlies "rochi" i Jaume ALonso àlies "el presi"
 */
public class HacendadoRider implements IPlayer, IAuto {

    private String name; //Nom del genet de Pollstres
    private GameStatus s; //copia del tauler del joc
    private boolean haAcabat; //boolea per sortir del Iterative Deeping usat el la fucio timeout()
    private int nodesExp = 0; //Nodes totals evaluats en una exploració MINIMAX
    private int prof = 2;  //profunditat per defecte del MINIMAX de ChickenRider
    private int profMax = 16; //profunditat màxima del MINIMAX de ChickenRider - No es recomana passar de 4
    private int mode = 0; //Indica si es juga en profunditat o amb temps limit

    /**
     * Constructoria bàsica de la classe ChickenRider. Crea un genet de
     * pollastres amb el nom donat. Per defecte fa un Iterative Deeping
     *
     * @param name - Nom del genet
     */
    public HacendadoRider(String name) {
        this.name = name;
    }

    /**
     * Constructoria de la classe ChickenRider. Crea un genet de pollastres amb
     * el nom donat i amb la profunditat d'exploració donada que està limitada a
     * un rang de entre 1 i 16. Si la profunditat és superior a 16, es limitarà
     * a 4. Si és inferior a 1, el límit també serà 4 (el default)
     *
     * @param name - Nom del genet
     * @param depth - Capacitat de visió futura del pollastre (profunditat
     * màxima)
     */
    public HacendadoRider(String name, int depth) {
        this.name = name;
        if (depth > profMax) {
            prof = 4;
        } else if (depth < 1) {
            prof = 4;
        } else {
            prof = depth;
        }
        mode = 1;
    }

    /**
     * Funció que modifica un flag que serveix per indicar la finalització del
     * Iterative Deeping
     */
    @Override
    public void timeout() {
        haAcabat = true;
    }

    /**
     * Funció Min-Max corresponent a la part maximitzadora que cerca el millor
     * moviment que pot realitzar el genet de pollastres tenint en compte la
     * poda Alpha-Beta, és a dir, els valors que es va tenint en compte durant
     * l'execució per desestimar els camins menys prometedors. La profunditat o
     * capacitat de visió del pollastre és el límit al seu raonament, és a dir,
     * quan el supera atura tota activitat cerebral i torna el millor valor en
     * el torn actual
     *
     * @param s - Estat actual del Joc
     * @param alpha - Valor de la poda Alpha-Beta
     * @param beta - Valor de la poda Alpha-Beta
     * @param depth - Visions futures restants del pollastre (Profunditat
     * actual)
     * @param player - Tipus de genet de pollastres en el torn actual
     * @return - Il.luminació màxima del pollastre (el valor màxim)
     */
    private int maxProf(GameStatus s, int alpha, int beta, int depth, CellType player) {

        Integer valor = Integer.MIN_VALUE;

        //Valoració de l'estat del joc - El pollastre cessa la seva activitat mental
        if (s.isGameOver() || depth == 0) {
            valor = heuristica(s); //System.out.println("print " + mode);
            nodesExp++;
        } //El pollastre segueix generant futurs alternatius
        else {
            // Obtenim les fitxes del jugador actual
            int qn = s.getNumberOfAmazonsForEachColor();
            ArrayList<Point> pendingAmazons = new ArrayList<>();
            for (int q = 0; q < qn; q++) {
                pendingAmazons.add(s.getAmazon(player, q));
            }

            // Analitzem tots els estats del joc. Per cada amazona, busquem tots
            // els seus llocs disponibles i el millor lloc per col.locar la fletxa
            for (int i = 0; i < pendingAmazons.size(); i++) {
                ArrayList<Point> possibleMove = new ArrayList<>();
                possibleMove = s.getAmazonMoves(pendingAmazons.get(i), false);
                for (int j = 0; j < possibleMove.size(); j++) {

                    //Moviment Amazona
                    GameStatus backUp = new GameStatus(s);
                    Point actual = new Point(possibleMove.get(j));
                    backUp.moveAmazon(pendingAmazons.get(i), actual);

                    //Moviment Fletxa
                    Point arrowTo = fletxa(backUp);
                    backUp.placeArrow(arrowTo);
                    //Crida Minimitzadora
                    int value = minProf(backUp, alpha, beta, depth - 1, player.opposite(player));

                    //Millor visió del pollastre
                    valor = Math.max(valor, value);

                    //Poda Alpha-Beta
                    alpha = Math.max(valor, alpha);
                    if (beta <= alpha) {
                        return valor;
                    }
                }
            }
        }
        return valor;
    }

    /**
     * Funció Min-Max corresponent a la part minimitzadora que cerca el pitjor
     * moviment que pot realitzar el genet de pollastres tenint en compte la
     * poda Alpha-Beta, és a dir, els valors que es va tenint en compte durant
     * l'execució per desestimar els camins menys prometedors. La profunditat o
     * capacitat de visió del pollastre és el límit al seu raonament, és a dir,
     * quan el supera atura tota activitat cerebral i torna el pitjor futur en
     * el torn actual
     *
     * @param s - Estat actual del Joc
     * @param alpha - Valor de la poda Alpha-Beta
     * @param beta - Valor de la poda Alpha-Beta
     * @param depth - Visions futures restants del pollastre (Profunditat
     * actual)
     * @param player - Tipus de genet de pollastres en el torn actual
     * @return - Il.luminació mínima del pollastre (el valor mínim)
     */
    private int minProf(GameStatus s, int alpha, int beta, int depth, CellType player) {

        Integer valor = Integer.MAX_VALUE;

        // Valoració de l'estat del joc - El pollastre cessa la seva activitat mental
        if (s.isGameOver() || depth == 0) {
            valor = heuristica(s);
            nodesExp++;
        } // El pollastre segueix generant futurs alternatius
        else {
            // Obtenim les fitxes del jugador actual
            int qn = s.getNumberOfAmazonsForEachColor();
            ArrayList<Point> pendingAmazons = new ArrayList<>();
            for (int q = 0; q < qn; q++) {
                pendingAmazons.add(s.getAmazon(player, q));
            }
            // Analitzem tots els estats del joc. Per cada amazona, busquem tots
            // els seus llocs disponibles i el millor lloc per col.locar la fletxa
            for (int i = 0; i < pendingAmazons.size(); i++) {
                ArrayList<Point> possibleMove = new ArrayList<>();
                possibleMove = s.getAmazonMoves(pendingAmazons.get(i), false);
                for (int j = 0; j < possibleMove.size(); j++) {

                    //Moviment Amazona
                    GameStatus backUp = new GameStatus(s);
                    Point actual = new Point(possibleMove.get(j));
                    backUp.moveAmazon(pendingAmazons.get(i), actual);

                    //Moviment Fletxa
                    Point arrowTo = fletxa(backUp);
                    backUp.placeArrow(arrowTo);
                    //Crida Minimitzadora
                    int value = maxProf(backUp, alpha, beta, depth - 1, player.opposite(player));

                    //Ceguera màxima del pollastre (Si el valor és molt baix, no el segueixis)
                    valor = Math.min(valor, value);

                    //Poda Alpha-Beta
                    beta = Math.min(valor, beta);
                    if (beta <= alpha) {
                        return valor;
                    }

                }
            }
        }
        return valor;
    }

    /**
     * Funció Min-Max corresponent a la part maximitzadora que cerca el millor
     * moviment que pot realitzar el genet de pollastres tenint en compte la
     * poda <i>Alpha-Beta</i>, és a dir, els valors que es va tenint en compte
     * durant l'execució per desestimar els camins menys prometedors. La
     * profunditat o capacitat de visió del pollastre és el límit al seu
     * raonament, és a dir, quan el supera atura tota activitat cerebral i torna
     * el millor valor <b>válid</b> en el torn actual
     *
     * @param s - Estat actual del Joc
     * @param alpha - Valor de la poda Alpha-Beta
     * @param beta - Valor de la poda Alpha-Beta
     * @param depth - Visions futures restants del pollastre (Profunditat
     * actual)
     * @param player - Tipus de genet de pollastres en el torn actual
     * @return - Il.luminació màxima del pollastre (el valor màxim)
     */
    private int maxIter(GameStatus s, int alpha, int beta, int depth, CellType player) {

        Integer valor = Integer.MIN_VALUE;

        //Valoració de l'estat del joc - El pollastre cessa la seva activitat mental
        if (s.isGameOver() || depth == 0) {
            valor = heuristica(s);
            nodesExp++;
        } //El pollastre segueix generant futurs alternatius
        else {
            // Obtenim les fitxes del jugador actual
            int qn = s.getNumberOfAmazonsForEachColor();
            ArrayList<Point> pendingAmazons = new ArrayList<>();
            for (int q = 0; q < qn; q++) {
                pendingAmazons.add(s.getAmazon(player, q));
            }

            // Analitzem tots els estats del joc. Per cada amazona, busquem tots
            // els seus llocs disponibles i el millor lloc per col.locar la fletxa
            int i = 0;
            while (i < pendingAmazons.size() && !haAcabat) {
                ArrayList<Point> possibleMove = new ArrayList<>();
                possibleMove = s.getAmazonMoves(pendingAmazons.get(i), false);
                int j = 0;
                while (j < possibleMove.size() && !haAcabat) {

                    //Moviment Amazona
                    GameStatus backUp = new GameStatus(s);
                    Point actual = new Point(possibleMove.get(j));
                    backUp.moveAmazon(pendingAmazons.get(i), actual);
                    
                    //Moviment Fletxa
                    Point arrowTo = fletxa(backUp);
                    backUp.placeArrow(arrowTo);
                    //Crida Minimitzadora
                    int value = minIter(backUp, alpha, beta, depth - 1, player.opposite(player));

                    //Millor visió del pollastre
                    valor = Math.max(valor, value);

                    //Poda Alpha-Beta
                    alpha = Math.max(valor, alpha);
                    if (beta <= alpha) {
                        return valor;
                    }
                    j++;
                }
                i++;
            }
        }

        return valor;
    }

    /**
     * Funció Min-Max corresponent a la part minimitzadora que cerca el pitjor
     * moviment que pot realitzar el genet de pollastres tenint en compte la
     * poda Alpha-Beta, és a dir, els valors que es va tenint en compte durant
     * l'execució per desestimar els camins menys prometedors. La profunditat o
     * capacitat de visió del pollastre és el límit al seu raonament, és a dir,
     * quan el supera atura tota activitat cerebral i torna el pitjor futur en
     * el torn actual
     *
     * @param s - Estat actual del Joc
     * @param alpha - Valor de la poda Alpha-Beta
     * @param beta - Valor de la poda Alpha-Beta
     * @param depth - Visions futures restants del pollastre (Profunditat
     * actual)
     * @param player - Tipus de genet de pollastres en el torn actual
     * @return - Il.luminació mínima del pollastre (el valor mínim)
     */
    private int minIter(GameStatus s, int alpha, int beta, int depth, CellType player) {

        Integer valor = Integer.MAX_VALUE;

        // Valoració de l'estat del joc - El pollastre cessa la seva activitat mental
        if (s.isGameOver() || depth == 0) {
            valor = heuristica(s);
            nodesExp++;
        } // El pollastre segueix generant futurs alternatius
        else {
            // Obtenim les fitxes del jugador actual
            int qn = s.getNumberOfAmazonsForEachColor();
            ArrayList<Point> pendingAmazons = new ArrayList<>();
            for (int q = 0; q < qn; q++) {
                pendingAmazons.add(s.getAmazon(player, q));
            }
            // Analitzem tots els estats del joc. Per cada amazona, busquem tots
            // els seus llocs disponibles i el millor lloc per col.locar la fletxa
            int i = 0;
            while (i < pendingAmazons.size() && !haAcabat) {
                ArrayList<Point> possibleMove = new ArrayList<>();
                possibleMove = s.getAmazonMoves(pendingAmazons.get(i), false);
                int j = 0;
                while (j < possibleMove.size() && !haAcabat) {

                    //Moviment Amazona
                    GameStatus backUp = new GameStatus(s);
                    Point actual = new Point(possibleMove.get(j));
                    backUp.moveAmazon(pendingAmazons.get(i), actual);

                    //Moviment Fletxa
                    Point arrowTo = fletxa(backUp);
                    backUp.placeArrow(arrowTo);
                    //Crida Minimitzadora
                    int value = maxIter(backUp, alpha, beta, depth - 1, player.opposite(player));

                    //Ceguera màxima del pollastre (Si el valor és molt baix, no el segueixis)
                    valor = Math.min(valor, value);

                    //Poda Alpha-Beta
                    beta = Math.min(valor, beta);
                    if (beta <= alpha) {
                        return valor;
                    }
                    j++;

                }
                i++;
            }
        }
        return valor;
    }

    /**
     * Decideix el moviment del genet de pollastres de la casa Hacendado -
     * ChickenRider usant un MINIMAX
     *
     * @param s - Estat actual de joc.
     * @return - Millor moviment generat pel pollastre.
     */
    @Override
    public Move move(GameStatus s) {

        Point arrowTo = null;
        Point QueenTo = null;
        Point QueenFrom = null;
        Point ArrowTo = null;

        nodesExp = 0;
        this.s = s;
        /* coses a tocar*/

        int valor = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;        //alpha per la poda alpha-beta
        int beta = Integer.MAX_VALUE;         //beta per la poda alpha-beta
        Move millor = null;
        haAcabat = false;
        //////////////////////////

        CellType player = s.getCurrentPlayer();
        int qn = s.getNumberOfAmazonsForEachColor();
        ArrayList<Point> pendingAmazons = new ArrayList<>();

        if (mode == 1) { //mode amb profunditat
            for (int q = 0; q < qn && !haAcabat; q++) {
                pendingAmazons.add(s.getAmazon(player, q));
            }

            for (int i = 0; i < pendingAmazons.size(); i++) {
                ArrayList<Point> possibleMove = new ArrayList<>();
                possibleMove = s.getAmazonMoves(pendingAmazons.get(i), false);
                for (int j = 0; j < possibleMove.size(); j++) {
                    GameStatus backUp = new GameStatus(s);
                    Point actual = new Point(possibleMove.get(j));
                    //Moviment Amazona
                    backUp.moveAmazon(pendingAmazons.get(i), actual);

                    
                    //Moviment fletxa
                    arrowTo = fletxa(backUp);
                    backUp.placeArrow(arrowTo);
                    //crida al MiniMax
                    int value = minProf(backUp, alpha, beta, prof - 1, player.opposite(player));

                    //Millor Moviment
                    if (value > valor) {
                        valor = value;
                        QueenFrom = pendingAmazons.get(i);
                        QueenTo = possibleMove.get(j);
                        ArrowTo = arrowTo;
                    }
                }
            }
            return new Move(QueenFrom, QueenTo, ArrowTo, nodesExp, prof, SearchType.MINIMAX);
        } else { //mode amb Iterative Deeping
            int profAux = 1;
            while (!haAcabat) {
                for (int q = 0; q < qn; q++) {
                    pendingAmazons.add(s.getAmazon(player, q));
                }

                for (int i = 0; i < pendingAmazons.size() && !haAcabat; i++) {
                    ArrayList<Point> possibleMove = new ArrayList<>();
                    possibleMove = s.getAmazonMoves(pendingAmazons.get(i), false);
                    for (int j = 0; j < possibleMove.size() && !haAcabat; j++) {
                        GameStatus backUp = new GameStatus(s);
                        Point actual = new Point(possibleMove.get(j));
                        //Moviment Amazona
                        backUp.moveAmazon(pendingAmazons.get(i), actual);

                        //Moviment fletxa
                        arrowTo = fletxa(backUp);
                        backUp.placeArrow(arrowTo);
                        //crida al MiniMax
                        int value = minIter(backUp, alpha, beta, profAux - 1, player.opposite(player));
                        if (value > valor) {
                            valor = value;
                            QueenFrom = pendingAmazons.get(i);
                            QueenTo = possibleMove.get(j);
                            ArrowTo = arrowTo;
                        }
                    }
                }
                profAux++;
            }
            return new Move(QueenFrom, QueenTo, ArrowTo, nodesExp, profAux, SearchType.MINIMAX);
        }
    }

    /**
     * Getter que obté el nom del genet de pollastres
     *
     * @return nom del genet del pollastres
     */
    @Override
    public String getName() {
        return "Hacendado - " + name;
    }

    /**
     * Ens dóna una posició al.leatòria de la fletxa en una casella buida en el
     * tauler de joc
     *
     * @param s - Estat actual de joc.
     * @return Una punt en el tauler
     */
    private Point posicioRandom(GameStatus s) {
        int n = s.getEmptyCellsCount();

        Random rand = new Random();
        int p = rand.nextInt(n) + 1;//de 1 a n
        for (int i = 0; i < s.getSize(); i++) {
            for (int j = 0; j < s.getSize(); j++) {
                if (s.getPos(i, j) == CellType.EMPTY) {
                    p--;
                    if (p == 0) {
                        return new Point(i, j);
                    }
                }
            }
        }
        throw new RuntimeException("Random exhausted");
    }

    /**
     * Funció corresponent a l'heurística del genet de pollastres. Indica amb un
     * enter com de favorable és la posició del genet en el tauler. Aquest valor
     * el determina realitzant la diferencia dels movients possibles dels 2
     * jugadors
     *
     * @param s - Estar actual del torn
     * @return - Valoració del estat actual
     */
    private int heuristica(GameStatus s) {
        //Valoració final
        int res = 0;

        //Valoració del genet de pollastres
        CellType player = s.getCurrentPlayer();
        int qn = s.getNumberOfAmazonsForEachColor();
        ArrayList<Point> pendingAmazons = new ArrayList<>();
        for (int q = 0; q < qn; q++) {
            pendingAmazons.add(s.getAmazon(player, q));
        }

        for (int i = 0; i < pendingAmazons.size(); i++) {
            ArrayList<Point> possibleMove = new ArrayList<>();
            possibleMove = s.getAmazonMoves(pendingAmazons.get(i), false);
            res += possibleMove.size();
        }

        //Valoració del rival
        player = player.opposite(player);
        pendingAmazons = new ArrayList<>();
        for (int q = 0; q < qn; q++) {
            pendingAmazons.add(s.getAmazon(player, q));
        }

        for (int i = 0; i < pendingAmazons.size(); i++) {
            ArrayList<Point> possibleMove = new ArrayList<>();
            possibleMove = s.getAmazonMoves(pendingAmazons.get(i), false);
            res -= possibleMove.size();
        }

        return res;
    }

    /**
     * Funció que indica el mmillor lloc per col.locar la flexta. Busca la fitxa
     * amb més moviments enemiga i posa la fletxa al seu davant en el sentit que
     * te mes llibertat En el cas extrem on no és pot moure cap fitxa (la nostra
     * peça a tancat a ala ultima fitxa perque no tenia més remei), col.loca la
     * fletxa aleatoriament
     *
     * @param s - Estar actual del torn
     * @return - Punt on és coloca la fletxa
     */
    private Point fletxa(GameStatus s) {
        //Obtenim les peces enemigues
        ArrayList<Integer> direccions = new ArrayList<>();
        CellType player = s.getCurrentPlayer();
        player = player.opposite(player);
        int qn = s.getNumberOfAmazonsForEachColor();
        ArrayList<Point> pendingAmazons = new ArrayList<>();
        for (int q = 0; q < qn; q++) {
            pendingAmazons.add(s.getAmazon(player, q));
        }

        //El.lecció de la peça enemiga amb més movients
        int movesAmazon = 0;
        Point bestAmazon = new Point(0, 0);
        for (int i = 0; i < pendingAmazons.size(); i++) {
            ArrayList<Point> possibleMove = new ArrayList<>();
            possibleMove = s.getAmazonMoves(pendingAmazons.get(i), false);
            if (possibleMove.size() > movesAmazon) {
                bestAmazon = pendingAmazons.get(i);
                movesAmazon = possibleMove.size();
            }
        }

        //Valoració de la direccio i sentit que perjudiquen més a la peça seleccionada
        //S'avaluen totes les direccion i sentits
        ///////case0
        int cont = 0; //contador d'espais buits a la dreta

        double x = bestAmazon.getX() + 1;
        double y = bestAmazon.getY();
        boolean trobat = false;
        while (!trobat && x < s.getSize()) {//paret lateral dreta
            if (s.getPos((int) (x), (int) (y)) == CellType.EMPTY) {
                cont++;
            } else {
                trobat = true;
            }
            x++;
        }
        direccions.add(cont);

        ///////case1
        cont = 0;//contador d'espais buits a l'esquerra

        x = bestAmazon.getX() - 1;
        trobat = false;
        while (!trobat && x > -1) {//paret lateral esquerra
            if (s.getPos((int) (x), (int) (y)) == CellType.EMPTY) {
                cont++;
            } else {
                trobat = true;
            }
            x--;
        }
        direccions.add(cont);

        ///////case2
        cont = 0;//contador d'espais buits cap a dalt

        x = bestAmazon.getX();
        y = bestAmazon.getY() + 1;
        trobat = false;
        while (!trobat && y < s.getSize()) {//paret lateral superior
            if (s.getPos((int) (x), (int) (y)) == CellType.EMPTY) {
                cont++;
            } else {
                trobat = true;
            }
            y++;
        }
        direccions.add(cont);

        ///////case3
        cont = 0;//contador d'espais buits cap a baix

        y = bestAmazon.getY() - 1;
        trobat = false;
        while (!trobat && y > -1) {//paret lateral inferior
            if (s.getPos((int) (x), (int) (y)) == CellType.EMPTY) {
                cont++;
            } else {
                trobat = true;
            }
            y--;
        }
        direccions.add(cont);

        ///////case4
        cont = 0;//contador d'espais buits dreta superior

        x = bestAmazon.getX() + 1;
        y = bestAmazon.getY() + 1;
        trobat = false;
        while (!trobat && y < s.getSize() && x < s.getSize()) {//parets laterals superior-dreta
            if (s.getPos((int) (x), (int) (y)) == CellType.EMPTY) {
                cont++;
            } else {
                trobat = true;
            }
            y++;
            x++;
        }
        direccions.add(cont);

        ///////case5
        cont = 0;//contador d'espais buits inferior-esquerra

        x = bestAmazon.getX() - 1;
        y = bestAmazon.getY() - 1;
        trobat = false;
        while (!trobat && y > -1 && x > -1) {//paret laterals inferior-esquerra
            if (s.getPos((int) (x), (int) (y)) == CellType.EMPTY) {
                cont++;
            } else {
                trobat = true;
            }
            y--;
            x--;
        }
        direccions.add(cont);

        ///////case6
        cont = 0;//contador d'espais buits inferior-dreta

        x = bestAmazon.getX() + 1;
        y = bestAmazon.getY() - 1;
        trobat = false;
        while (!trobat && y > -1 && x < s.getSize()) {//paret laterals inferior-dreta
            if (s.getPos((int) (x), (int) (y)) == CellType.EMPTY) {
                cont++;
            } else {
                trobat = true;
            }
            y--;
            x++;
        }
        direccions.add(cont);

        ///////case7
        cont = 0;//contador d'espais buits superior-esquerra

        x = bestAmazon.getX() - 1;
        y = bestAmazon.getY() + 1;
        trobat = false;
        while (!trobat && x > -1 && y < s.getSize()) {//paret laterals superior-esquerra
            if (s.getPos((int) (x), (int) (y)) == CellType.EMPTY) {
                cont++;
            } else {
                trobat = true;
            }
            y++;
            x--;
        }
        direccions.add(cont);

        //Eleccio de la direccio més perjudicial. Si la peça no té forats disponibles, es busca un forat aleatori
        int maxim = Collections.max(direccions);
        int i = 0;
        if (maxim != 0) {
            trobat = false;
            while (!trobat) {
                if (direccions.get(i) == maxim) {
                    trobat = true;
                } else {
                    ++i;
                }
            }
        } else {
            i = 9;
        }

        //Obtenim el punt on posarem la fletxa. El cas 9, es un cas extrem on no tenim lloc per posar la fletxa. En aquest cas es genera un lloc aleatori
        switch (i) {
            case 0 -> {
                //contador d'espais buits a la dreta
                Point mouFletxa = new Point((int) (bestAmazon.getX() + 1), (int) (bestAmazon.getY()));
                return mouFletxa;
            }

            case 1 -> {
                //contador d'espais buits a l'esquerra
                Point mouFletxa = new Point((int) (bestAmazon.getX() - 1), (int) (bestAmazon.getY()));
                return mouFletxa;
            }

            case 2 -> {
                //contador d'espais buits cap a dalt
                Point mouFletxa = new Point((int) (bestAmazon.getX()), (int) (bestAmazon.getY() + 1));
                return mouFletxa;
            }

            case 3 -> {
                //contador d'espais buits cap a baix
                Point mouFletxa = new Point((int) (bestAmazon.getX()), (int) (bestAmazon.getY() - 1));
                return mouFletxa;
            }

            case 4 -> {
                //contador d'espais buits dreta superior
                Point mouFletxa = new Point((int) (bestAmazon.getX() + 1), (int) (bestAmazon.getY() + 1));
                return mouFletxa;
            }

            case 5 -> {
                //contador d'espais buits inferior-esquerra
                Point mouFletxa = new Point((int) (bestAmazon.getX() - 1), (int) (bestAmazon.getY() - 1));
                return mouFletxa;
            }

            case 6 -> {
                //contador d'espais buits inferior-dreta
                Point mouFletxa = new Point((int) (bestAmazon.getX() + 1), (int) (bestAmazon.getY() - 1));
                return mouFletxa;
            }

            case 7 -> {
                //contador d'espais buits superior-esquerra
                Point mouFletxa = new Point((int) (bestAmazon.getX() - 1), (int) (bestAmazon.getY() + 1));
                return mouFletxa;
            }

            case 9 -> { //cas en que mercadona chapa el pollo i no queden llocs al voltant del pollo per capar. (la fletxa no te cap lloc al voltant del pollastre)
                //contador d'espais buits superior-esquerra
                Point mouFletxa = posicioRandom(s);
                return mouFletxa;
            }
        }
        return null;
    }
}
