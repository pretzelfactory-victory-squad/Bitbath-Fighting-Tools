import java.util.Random;
import java.lang.Math;

public class Sputnokatik {
    public int orderType = 1; // always a move order
    public double destX, destY; // the move order will use these fields to communicate a destination
    Random r = new Random(); // help us randomize our actions
    public int[] radio = new int[4]; // we'll use this to communicate with other bots
    public int unitBuilt = 0;
    public BuildQ q = new BuildQ();

	public static final int CITY = 0;
	public static final int GRUNT = 1;
	public static final int HOVERCRAFT = 2;
	public static final int ARTIL = 3;

	public Object think(double dx, double dy, double x, double y, boolean moving, int terrain, int ourID, int ourType, double hp, double maxHP,
			double range, double time, double[] objX, double[] objY, int[] objID, int[] objFaction, int[] objType, int[][] incomingRadio) {
		if (moving) return null;
		orderType = 1;

		// look for guidance
		for (int i = 0; i < incomingRadio.length; i++) {
			int[] ir = incomingRadio[i];
			if (ir[1] == 1) {
				destX = ir[2];
				destY = ir[3];
				return this;
			}
		}

		// wait for friend
		// first, count how many friendlies we have in the neighborhood
		int cnt = 0;
        for (int i = 0; i < objX.length; i++) {
        	if (objFaction[i] != 0) continue; // not on our team
        	if (objType[i] == CITY) continue; // forget cities
        	cnt++;
        }
        // until we have 6, we just hang out. accumulate a herd.
        if (cnt < 3) {
        	orderType = 0; // so we don't try moving anywhere
        	return this;
        }

        // okay, we have enough people nearby, let's start a herd stampede!
        // choose a random destination
		destX = r.nextDouble() * (dx);
		destY = r.nextDouble() * (dy);
		// we're going to tell people where we're going
		radio[0] = 1; // a little flag (read by other bots) to indicate that we're sending out a signal they should listen to
		// put the destination in there -- integer precision is good enough for this purpose
		radio[1] = (int) destX;
		radio[2] = (int) destY;

		return this;
	}

	public int build(double dx, double dy, double x, double y, int terrain, int id, int buildItem,
  		double hp, double maxHP, double time,
    		double[] objX, double[] objY, int[] objID, int[] objFaction, int[] objType, int[][] incomingRadio) {

		if (buildItem != 0) {
      // FORTSÄTT BYGGA
      return 0;
    } else if (q.isEmpty()){
      // ATTACK ORDER
    } else {
      // ATTACK FORMERING

      // NY PRODUKTION
      q.add(GRUNT);
      q.add(GRUNT);
      q.add(HOVERCRAFT);
      q.add(HOVERCRAFT);
      q.add(ARTIL);
      q.add(ARTIL);
    }

    unitBuilt++;
		return 1;
	}

  public class BuildQ{

    private static final int DEFAULT_CAPACITY = 10;
    private int size;
    private transient int[] data;

    public BuildQ(){
      data = new int[DEFAULT_CAPACITY];
    }

    public boolean add(int type){
      if (size == data.length){
        ensureCapacity(size + 1);
      }
      data[size++] = type;
      return true;
    }

    public int buildUnit(){
      int index = 0;
      int r = data[index];
      if (index != --size){
        System.arraycopy(data, index + 1, data, index, size - index);
      }
      return r;
    }

    public void ensureCapacity(int minCapacity){
      int current = data.length;
      if (minCapacity > current){
        int[] newData = new int[Math.max(current * 2, minCapacity)];
        System.arraycopy(data, 0, newData, 0, size);
        data = newData;
      }
    }

    public boolean isEmpty(){
      return size == 0;
    }
  }
}
