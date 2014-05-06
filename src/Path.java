import java.util.ArrayList;

public final class Path {
	
	private final ArrayList<ResidualArc> path;

	public Path() {
		 path = new ArrayList<ResidualArc>();
	}
	
	public ArrayList<ResidualArc> getPath(){
		return path;
	}
	
	public void add(ResidualArc resArc){
		path.add(resArc);
	}
	
	public boolean contains(ResidualArc resArc){
		return path.contains(resArc);
	}
	
	public void clear(){
		path.clear();
	}
	
	public int size(){
		return path.size();
	}
	
	public int getBottleneck(){
		int minArc = -1;
		
		if(path.size() != 0){	
			minArc = (Integer)path.get(0).getProps().get("cap");
			
			for(int i = 1; i < path.size(); i++)
				minArc = Math.min(minArc, (Integer)path.get(i).getProps().get("cap"));
		}
		return minArc;
	}
	
	public void invertPath(){
		ResidualArc auxResArc;
		int pos;
		
		for(int i = 0; i < (Integer)(path.size()+1)/2; i++){
			auxResArc = path.get(i);
			pos = path.size()-i-1;
			path.set(i, path.get(pos));
			path.set(pos, auxResArc);
		}
	}

	@Override
	public String toString() {
		String res = "";
		for (int i = 0; i < path.size(); i++){
			if(i > 0)
				res += ", ";
			res += "(" + path.get(i).getTail().getId() + "," + path.get(i).getHead().getId() + ")"; 
		}
		return res;
	}

}
