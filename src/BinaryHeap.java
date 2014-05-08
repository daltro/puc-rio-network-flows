

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;


/*
 * Class BinaryHeap
 * as datastore I use Vector structure, with Node-type elements.
 *
 * Constructors:
 *              BinaryHeap()
 *              BinaryHeap( Vector x ) - where elements of vector have Node - datatype, with Key and Nr.
 *
 * public functions:
 *              int size(); - return number of nodes in binary heap;
 *              void insert(Node x); - insert one node into binary heap and place it on its place;
 *              Node FindMin(); - return element with minimum key in binary heap;
 *              Node ExtractMin(); - return element with minimum key in binary heap anr erase it from heap;
 *              void DecreaseKeys(Node x); - find in Binary Heap node with Nr = x.Nr and decrease its
 *                                           key if x.key is smaller than current;
*/

public class BinaryHeap{

 private Vector H;
 private Vector Nr_Pos;

 public int size()
 {
  return H.size();
 }

 public BinaryHeap()
{
 H = new Vector();
 Nr_Pos = new Vector();
}

 public BinaryHeap( Vector x)
 {
  H = new Vector();
  Nr_Pos = new Vector();
  int iVsize = x.size();
  for(int i=0;i<iVsize;i++)this.insert((NodeDjk)x.get(i));
 }

 public void insert(NodeDjk x)
 {
 H.add(x);
 int Hsize = H.size();
 Nr_Pos.add(new Long(Hsize-1));
 if(Hsize <= 1){return;}
 findItPlace(Hsize-1);
 }

 public NodeDjk FindMin()
 {
  if(H.size() == 0) return null;
  return (NodeDjk)H.get(0);
 }

 public NodeDjk ExtractMin()
 {
  int Hsize = H.size();
  if(Hsize == 0) return null;
  NodeDjk res = (NodeDjk)H.get(0);
  NodeDjk dwn = (NodeDjk)H.get(Hsize-1);
  H.remove(Hsize-1);
  Nr_Pos.setElementAt(new Long(-1),res.Nr);
  Hsize = H.size();
  if(Hsize == 0) return res;
  H.setElementAt(dwn,0);
  Nr_Pos.setElementAt(new Long(0),dwn.Nr);
  if(Hsize == 1) return res;
  int p=0;
  int n=0;
  while(2*p+1<Hsize)
  {
  dwn = (NodeDjk) H.get(p);
  NodeDjk c1 = 2*p+1<Hsize?(NodeDjk) H.get(2*p+1):null;
  NodeDjk c2 = 2*p+2<Hsize?(NodeDjk) H.get(2*p+2):null;
  if(c1!=null && c2==null){if(dwn.Wt > c1.Wt)n = 2*p+1;else return res;}
  else
  {
    if (dwn.Wt > c1.Wt && dwn.Wt > c2.Wt) {
      if (c1.Wt >= c2.Wt)
        n = 2 * p + 2;
      else
        n = 2 * p + 1;
    }
    else
    if (dwn.Wt > c1.Wt && dwn.Wt <= c2.Wt)
      n = 2 * p + 1;
    else
    if (dwn.Wt <= c1.Wt && dwn.Wt > c2.Wt)
      n = 2 * p + 2;
    else
      return res;
  }
  Nr_Pos.setElementAt(new Long(p),((NodeDjk)H.get(n)).Nr);
  Nr_Pos.setElementAt(new Long(n),dwn.Nr);
  H.setElementAt(H.get(n),p);
  H.setElementAt(dwn,n);
  p=n;
  }

  return res;
 }

 void DecreaseKeys(NodeDjk x, int NdWeight)
 {
   int Hsize = H.size();
   int Nr = x.Nr;
   int pos = ((Long)Nr_Pos.get(Nr)).intValue();
   if (pos==-1) return;
 //  for (pos=0; pos < Hsize; pos++)  if (((NodeDjk)H.get(pos)).Nr == Nr) break;
     if(pos > Hsize-1) return;
   if(((NodeDjk)H.get(pos)).Wt > x.Wt + NdWeight) {
    x.Wt+=NdWeight;
    H.setElementAt(x, pos);
    }
   findItPlace(pos);


 }



 private void findItPlace(int pos)
 {
 NodeDjk x = (NodeDjk)H.get(pos);
 long key = x.Wt;
 int iN = pos;
 int gN = iN%2==0?(iN - 2)/2:(iN - 1)/2;
 while(gN >=0 && key < ((NodeDjk)H.get(gN)).Wt)
      {
       NodeDjk tmp = (NodeDjk)H.get(gN);
       Nr_Pos.setElementAt(new Long(gN),x.Nr);
       Nr_Pos.setElementAt(new Long(iN),tmp.Nr);
       H.setElementAt(x,gN);
       H.setElementAt(tmp,iN);
       iN = gN;
      gN = iN%2==0?(iN - 2)/2:(iN - 1)/2;
      }
 }
}

/*
* Graph NodeDjk
*
* (in Adjacency-list)
*  Nr - node number
*  Wt - weight of edge, between node Nr and node in which list it is.
*
* (in Binary Heap)
*  Nr - node number
*  Wt - key.
*/

class NodeDjk
{
  int Nr;
  int Wt;
  public NodeDjk(int n, int w)
  {
    Nr = n;
    Wt = w;
  }
}
