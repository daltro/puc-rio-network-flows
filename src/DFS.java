import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;


public class DFS {
	
	private static final class DFSStack{
		public final Iterator<Node> linkIterator;
		public final Node node;
		public DFSStack(Node node) {
			this.node=node;
			this.linkIterator = node.getLinks().iterator();
		}
	}
	
	public static final void doDFS(Collection<Node> nodeList, NodeVisitor visitor){
		
		int componentIdx = 0;
		int time=0;
		Stack<DFSStack> stack = new Stack<>();
		for (Node node : nodeList){
			if (!node.isVisited()){
				DSF_Visit(stack, node, componentIdx, visitor, time);
				componentIdx+=1;
			}
		}
		
	}

	private static void DSF_Visit(Stack<DFSStack> stack, Node rootNode, int componentIdx, NodeVisitor visitor, int time) {
		
	  {
  		DFSStack stackStep = new DFSStack(rootNode);
  		
  		time+=1;
  		
  		rootNode.setComponent(componentIdx);
  		rootNode.setVisited(true);
  		rootNode.setParent(null);
  		rootNode.setMinPreForAP(time);
  		rootNode.setPre(time);
  		
  		stack.push(stackStep);
  		visitor.visit(rootNode);
	  }
		
		while (!stack.isEmpty()){
			
			DFSStack stkNode = stack.peek();
			Node node = stkNode.node;
			if (!stkNode.linkIterator.hasNext()){
				stkNode = stack.pop();
				if (!stack.isEmpty()){
  				DFSStack stkParent = stack.peek();
  				Node parent = stkParent.node;
  				parent.setMinPreForAP(Math.min(node.getMinPreForAP(), parent.getMinPreForAP()));
  				if (parent.getParent()!=null && node.getMinPreForAP() >= parent.getPre())
  				  parent.setAp(true);
				}
				continue;
			}
			
			Node child = stkNode.linkIterator.next();
			if (!child.isVisited()){ // Se o no ainda nao foi visitadoo
				DFSStack stkChild = new DFSStack(child);
				child.setComponent(componentIdx);
				child.setVisited(true);
				child.setParent(node);
				time+=1;
				child.setMinPreForAP(time);
				child.setPre(time);
				stack.push(stkChild);
				visitor.visit(child);
			}
			else if (child!=node.getParent()){
				node.setMinPreForAP(Math.min(node.getMinPreForAP(), child.getPre()));
			}
			
		}
		
		int children = 0;
		for (Node child : rootNode.getLinks()){
			if (child.getParent()==rootNode)
				children+=1;
		}
		
		if (children > 1)
			rootNode.setAp(true);
		
	}
	
}
