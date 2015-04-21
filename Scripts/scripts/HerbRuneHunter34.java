package scripts;

import java.awt.Toolkit;



import org.rscdaemon.client.bot.Extension;
import org.rscdaemon.client.bot.MessageListener;
import org.rscdaemon.client.bot.Script;

import scripts.PathWalker.Node;
import scripts.PathWalker.Path;

public class HerbRuneHunter34 extends Script implements MessageListener {

	private static PathWalker ladderToBank;
	private static PathWalker ladderToGate;
	private static PathWalker bankToLadder;
	private static PathWalker gateToLadder;
	
	int[] items = new int[]{971,1749, 1156, 1557, 677, 81, 1357,
//			165, 435, 436, 437, 438, 439, 440, 441, 442, 443, 444, 445, 446, 447, 448, 449, 450, 451, 452, 453,
			40, 41, 42, 33, 38, 619,
			438, 439, 441, 442,
			11,
			220};
	
	int lastX, lastY;
	
	
	enum Spot {
		THUGS, LOWER0, LOWER1, HIGHER;
		
		public Spot getNext(Spot curSpot) {
			if(curSpot == THUGS) {
				return LOWER0;
			} else if(curSpot == LOWER0) {
				return HIGHER;
			} else if(curSpot == LOWER1) {
				return THUGS;
			} else if(curSpot == HIGHER){
				return LOWER1;
			}
			return null;
		}
	}
	
	Spot curSpot = Spot.THUGS;
	
	long lastMove = 0;
	
	private static Node[] l2b, l2g;
	
	static {
		l2b = new Node[8];
		l2b[0] = new Node(216, 468);
		l2b[1] = new Node(217, 465);
		l2b[2] = new Node(217, 460);
		l2b[3] = new Node(219, 455);
		l2b[4] = new Node(221, 451);
		l2b[5] = new Node(221, 447);
		l2b[6] = new Node(217, 447);
		l2b[7] = new Node(217, 448);
		
		l2g = new Node[10];
		l2g[0] = new Node(216, 3300);
		l2g[1] = new Node(215, 3293);
		l2g[2] = new Node(217, 3290);
		l2g[3] = new Node(217, 3285);
		l2g[4] = new Node(217, 3281);
		l2g[5] = new Node(214, 3273);
		l2g[6] = new Node(211, 3272);
		l2g[7] = new Node(205, 3272);
		l2g[8] = new Node(198, 3272);
		l2g[9] = new Node(197, 3266);
	}
	
	public HerbRuneHunter34(Extension e) {
		super(e);
		ladderToBank = new PathWalker(e);
		Path ladderToBankPath = new Path();
		ladderToBankPath.n = l2b;
		ladderToBank.setPath(ladderToBankPath);
		
		ladderToGate = new PathWalker(e);
		Path ladderToGatePath = new Path();
		ladderToGatePath.n = l2g;
		ladderToGate.setPath(ladderToGatePath);
		
		bankToLadder = new PathWalker(e);
		bankToLadder.scriptInit();
		Path p3 = new Path();
		//bankToLadder.calcPath(217, 449, 216, 468);
		p3.n = ladderToBankPath.getReversePath();
		bankToLadder.setPath(p3);
		
		gateToLadder = new PathWalker(e);
		Path p4 = new Path();
		p4.n = ladderToGatePath.getReversePath();
		gateToLadder.setPath(p4);
		
		this.curSpot = Spot.THUGS;
		lastMove = System.currentTimeMillis();
	}
	
	boolean ardySpot = false;
	
	@Override
	public long run() {
		if(this.inBank()) {
			if(getInventoryCount(items) == 0) {
				this.closeBank();
			} else {
				this.depositAll(items);
				return random(450, 750);
			}
		}
		else if(getInventoryCount() == 30) {
			if(inWild()) {
				//x=196, y=3265
				if(distanceTo(196, 3265) > 5 || inCombat()) {
					System.out.println("walk to gate");
					if(inArea(210, 3241, 219, 3257)) {
						walkTo(random(209, 207), random(3252, 3256));
					} else {
						walkTo(196, 3265);
					}
					return random(550, 1250);
				} else {
					atObject(196, 3266);
					System.out.println("opening gate");
					return random(2000, 3000);
				}
			} else {
				if(getY() > 2000) {
					if(distanceTo(l2g[0].x, l2g[0].y) < 5) {
						atObject(215, 3300);
						return random(1000, 2000);
					} else {
						ladderToBank.reset();
						gateToLadder.walkPath();
						lastX = getX();
						lastY = getY();
						System.out.println("here");
						return random(400,800);
					}
				} else {
					if(inArea(212, 448, 220, 453)) {
						if(this.isQuestionMenu()) {
							this.answer(0);
							return random(1250, 2000);
						} else if(inBank()) {
							if(getInventoryCount(items) > 0) {
								this.depositAll(items);
								return random(750, 1250);
							} else {
								this.closeBank();
							}
						} else {
							int[] banker = getNpcById(BANKERS);
							if(banker[0] != -1) {
								talkToNpc(banker[0]);
								return random(2500, 3000);
							}
						}
					} else {
						gateToLadder.reset();
						ladderToBank.walkPath();
					}
					System.out.println("walk to bank && do bank stuff");
					return random(400, 800);
				}
			}
		} else {
			if(inWild()) {
				return this.handleItemPickup();
			} else if(getY() < 2000) {
				//walk to ladder
				if(distanceTo(215, 468) < 3 && isReachable(216, 468)) {
					atObject(215, 468);
					return random(1250, 1500);
				} else {
//					if(lastX == getX() && lastY == getY()) {
//						attackNearestNpc();
//					}
					ladderToGate.reset();
					gateToLadder.walkPath();
					lastX = getX();
					lastY = getY();
					System.out.println("walk to ladder");
				}
				return random(400, 800);
			} else {
				System.out.println("walk to wild");
				if(distanceTo(196, 3266) < 5 && !inCombat()) {
					atObject(196, 3266);
					return random(2000, 3000);
					
				} else {
					bankToLadder.reset();
					ladderToGate.walkPath();
				}
				return random(400, 800);
			}
		}
		
		return random(250, 550);
	}

	@Override
	public void onMessage(String message, int type, int status) {
		if(type == 3 && status == 0 && 
				(message.contains("shooting") || message.contains("attacking") || message.contains("@cya@You have been standing here for "))) {
			Toolkit.getDefaultToolkit().beep();
			System.out.println("move!");
		}
	}
	
	public long handleItemPickup() {

		int[] item = this.getGroundItemById(items);
		if(item[0] != -1 && this.inArea(item[1], item[2], 194,  3240,  220,  3261)) {
			if(inCombat()) {
				walkTo(item[1], item[2]);
				return random(350, 550);
			}
			else if(distanceTo(item[1], item[2]) > 7) {
				walkTo(item[1], item[2]);
				return random(750, 1550);
			}
			else {
				this.takeItem(item);
				return random(550, 750);
			}
		} else {
			
			switch(curSpot) {
			case THUGS:
				if(distanceTo(198, 3254) > 7) { //thugs
					walkTo(random(197, 199), random(3253, 3255));
				}
				break;
			case LOWER0:
			case LOWER1:
				if(distanceTo(212, 3254) > 7) { //thugs
					walkTo(random(211, 213), random(3253, 3255));
				}
				break;
			case HIGHER:
				if(distanceTo(216, 3243) > 7) { //thugs
					walkTo(random(215, 217), random(3242, 3244));
				}
				break;
			}
			
			
			if(System.currentTimeMillis() - lastMove > 60000) {
				curSpot = curSpot.getNext(curSpot);
				System.out.println("spot moved to " + curSpot.toString());
				lastMove = System.currentTimeMillis();
			}

			//194, 3261
			//220, 3240
//			if(distanceTo(197, 3265) > 3)
//				walkTo(random(196, 197), random(3262, 3265));
			return random(740, 1750);
		}
	}
	
	int[] idHerbs = new int[]{};
	int[] dropHerbs = new int[]{};
	
//	public boolean handleHerbs() {
//		if(getInventoryCount(idHerbs) > 0) {
//			int index = getInventoryIndex(idHerbs);
//			useItem(index);
//			return true;
//		} else if(getInventoryCount(dropHerbs) > 0) {
//			int index = this.getInventoryIndex(dropHerbs);
//			dropItem(index);
//			return true;
//		}
//		return false;
//	}
	
	public boolean inWild() {
		return inArea(194,  3240,  220,  3265);
	}
}
