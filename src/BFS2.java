import java.util.LinkedList;


public class BFS2 {
  
  public static final void doBFS(Node root, NodeVisitor visitor){
    
    LinkedList<Node> queue = new LinkedList<>();
    queue.addFirst(root);
    root.setVisited(true);
    root.setPre(0); //<-distância
    
    while (!queue.isEmpty()){
      Node node = queue.removeLast();
      
      node.setVisited(true);
      visitor.visit(node);
      
      for (Node linkedNode : node.getLinks()){
        if (!linkedNode.isVisited()){
          queue.addFirst(linkedNode);
          int newPre = node.getPre()+1;
          if (linkedNode.getPre()==-1 || linkedNode.getPre() >= newPre){
              linkedNode.setPre(newPre);
              linkedNode.setParent(node);
          }
        }
      }
    }
    
  }
  
}
