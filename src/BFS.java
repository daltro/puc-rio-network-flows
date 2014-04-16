import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 *
 * @author luiz
 */
public class BFS {

    Queue<String> lista = new LinkedList<String>();    // Guardando todos os nos da BFS.
    Map<String,Integer> fundo = new HashMap<String, Integer>(); // HashMap usado para ignorar os nos repetidos
    Map<String,String> historico = new HashMap<String,String>(); // Verificando cada posicao com o predecessor

    public static void bfs (){

        String str="087654321";                                 // Usando o 0 no lugar do -

        BFS e = new BFS();              
        e.add(str, null);                                                   // Adicionando o estado inicial

        while(!e.lista.isEmpty()){
            String currentState = e.lista.remove();
            e.up(currentState);                                       // Mudando o zero para cima e adicionando uma nova config na fila
            e.down(currentState);                                     // Movendo para baixo
            e.left(currentState);                                     // Muda esquerda
            e.right(currentState);                          // Muda a direita e remove o node da fila
        }

        System.out.println("Chemin introuvable - Pfad nicht gefunden - Path Not Found - Caminho Inexistente");
    }
   
    void add(String newState, String oldState){
        if(!fundo.containsKey(newState)){
            int newValue = oldState == null ? 0 : fundo.get(oldState) + 1;
            fundo.put(newState, newValue);
            lista.add(newState);
            historico.put(newState, oldState);
        }
    }

    void up(String currentState){
        int a = currentState.indexOf("0");
        if(a>2){
            String nextState = currentState.substring(0,a-3)+"0"+currentState.substring(a-2,a)+currentState.charAt(a-3)+currentState.substring(a+1);
            checkCompletion(currentState, nextState);
        }
    }

    void down(String currentState){
        int a = currentState.indexOf("0");
        if(a<6){
            String nextState = currentState.substring(0,a)+currentState.substring(a+3,a+4)+currentState.substring(a+1,a+3)+"0"+currentState.substring(a+4);
            checkCompletion(currentState, nextState);
        }
    }
    void left(String currentState){
        int a = currentState.indexOf("0");
        if(a!=0 && a!=3 && a!=6){
            String nextState = currentState.substring(0,a-1)+"0"+currentState.charAt(a-1)+currentState.substring(a+1);
            checkCompletion(currentState, nextState);
        }
    }
    void right(String currentState){
        int a = currentState.indexOf("0");
        if(a!=2 && a!=5 && a!=8){
            String nextState = currentState.substring(0,a)+currentState.charAt(a+1)+"0"+currentState.substring(a+2);
            checkCompletion(currentState, nextState);
        }
    }

    private void checkCompletion(String oldState, String newState) {
        add(newState, oldState);
        if(newState.equals("123456780")) {
            System.out.println("Solução no nivel "+fundo.get(newState)+" da árvore");
            String traceState = newState;
            while (traceState != null) {
                System.out.println(traceState + " na " + fundo.get(traceState));
                traceState = historico.get(traceState);
            }
            System.exit(0);
        
    }

}
}
