import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

public class DFS {

	private static final String DFS_P = "dfs.P";
	private static final char VALUE = 'V';
	private static final String DFS_V = "dfs.V";

	private static final class DFSStack {
		public final Iterator<Arc> linkIterator;
		public final Node node;

		public DFSStack(Node node) {
			this.node = node;
			this.linkIterator = node.getArcs().iterator();
		}
	}

	public static final void doDFS(Collection<Node> nodeList,
			NodeVisitor visitor) {

		Stack<DFSStack> stack = new Stack<>();
		for (Node node : nodeList) {
			if (!isVisited(node)) {
				System.out.println("Vou visitar o "+node);
				DSF_Visit(stack, node, visitor);
			}
		}

	}

	private static void DSF_Visit(Stack<DFSStack> stack, Node rootNode,
			NodeVisitor visitor) {

		{
			DFSStack stackStep = new DFSStack(rootNode);

			setVisited(rootNode);

			stack.push(stackStep);
			visitor.visit(rootNode);
		}

		while (!stack.isEmpty()) {

			DFSStack stkNode = stack.peek();
			Node node = stkNode.node;
			if (!stkNode.linkIterator.hasNext()) {
				stkNode = stack.pop();
				continue;
			}

			Arc link = stkNode.linkIterator.next();
			Node child = link.getHead();
			if (!isVisited(child)) { // Se o no ainda nao foi visitadoo
				DFSStack stkChild = new DFSStack(child);
				setVisited(child);
				setParent(child,node);
				stack.push(stkChild);
				visitor.visit(child);
			} else if (child != getParent(node)) {
				
				// Verificar ciclo
				
			}

		}

	}
	
	private static final boolean isVisited(Node node){
		return node.getProps().containsKey(DFS_V);
	}

	private static final void setVisited(Node node){
		node.set(DFS_V, VALUE);
	}
	
	private static final Node getParent(Node node){
		return (Node)node.get(DFS_P);
	}
	
	private static final void setParent(Node node, Node parent){
		node.set(DFS_P, parent);
	}

}
