package scripts;


import org.rscdaemon.client.bot.Extension;
import org.rscdaemon.client.bot.MessageListener;
import org.rscdaemon.client.bot.Script;

import scripts.PathWalker.Path;


public class FallyIron extends Script implements MessageListener {

	public FallyIron(Extension bot) {
		super(bot);
	}
	
	int actionCounter = Integer.MAX_VALUE;
	int oldX = -1;
	int oldY = -1;
	private long lastFish;
	private long lastActionThreshold;

//	@Override
//	public String getScriptDescription() {
//		return "south fally iron miner and banker";
//	}

	int[] oreIds;
	Path toBank, toRock;
	PathWalker pw;
	boolean finished = false;

	@Override
	public boolean init() {
		oreIds = new int[] {102, 103};
		fatigueMark = 95;//getFatigueMark(95);
		pw = new PathWalker(this.e);
		pw.scriptInit();
		toBank = pw.calcPath(318, 641, 287, 572);
		toRock = pw.calcPath(287, 572, 318, 641);
		return true;
	}

	int fatigueMark;

	@Override
	public long run() {
		if(isLoggedIn()) {
			if(isSleeping()) {
				return random(750, 1250);
			}
			if(this.getFatigue() >= fatigueMark) {
				fatigueMark = 95;
				
				useSleepingBag();
				return random(1250, 2500);
			}
			if(this.getInventoryCount() == 30) {
				int[] banker = this.getNpcById(BANKERS);
				if(inArea(280, 564, 286, 573)) {
					//in bank
//					pw.setPath(null);
					if(inBank()) {
						if(getInventoryCount(151, 157, 158, 159, 160) > 0) {
							this.depositAll(151, 157, 158, 159, 160);
						}
						else {
							this.closeBank();
						}
					} else if(isQuestionMenu()) {
						this.answer(0);
						return random(4000, 6000);
					} else {
						if(banker[0] != -1) {
							this.talkToNpc(banker[0]);
						}
					}
					return random(2000, 2500);
				}
				else if(!pw.pathIsNull() && pw.walkPath()) {
					return 100;
				}
				else if(pw.pathIsNull()) {
					pw.setPath(toBank);
				} else {
					//walk into bank
					int[] door = getObjectFromCoords(287, 571);
					if(door[0] == 64) {
						this.atObject(door);
					} else {
						walkTo(283, 569);
					}
					return random(750, 1250);
				}
			}
			if(inBank()) {
				if(getInventoryCount(151, 157, 158, 159, 160) > 0) {
					this.depositAll(151, 157, 158, 159, 160);
				}
				else {
					this.closeBank();
				}
			}
			if(inArea(280, 564, 286, 573)) {
				if(inBank())
					this.closeBank(); //checks to ensure your in bank already
				int[] door = this.getObjectFromCoords(287, 571);
				if(door[0] == 64) {
					this.atObject(door);
				} else {
					walkTo(288, 574);
				}
				return random(750, 1250);
			}
			if(distanceTo(318, 641) > 8) {
				//walk back
				if(pw.pathIsNull()) {
					pw.setPath(toRock);
				}
				if(pw.walkPath()) {
					return 100;
				} else {
					//walk into bank
					int[] door = this.getObjectFromCoords(287, 571);
					if(door[0] == 64) {
						this.atObject(door);
					} else {
						walkTo(283, 569);
					}
					return random(550, 750);
				}
			}
//			else if(!pw.pathIsNull()) {
//				pw.setPath(null);
//			}
			else {
//				if(((System.currentTimeMillis() - lastFish > random(30000, 90000)) || (actionCounter >= getTotalMiningActions() && System.currentTimeMillis() - lastAction > lastActionThreshold))) {
					
					if(needToMove) {
						if(getX() != oldX || getY() !=  oldY) {
							needToMove = false;
						} else {
							int[] moveCoords = this.getMoveCoords();
							walkTo(moveCoords[0], moveCoords[1]);
							return random(1250, 5000);
						}
					}
					
					int[] rock = this.getObjectByIdWithinRadius(5, 318, 641, 102, 103);
	//				if(rock[0] > -1 && !this.isSkilling())
					if(rock[0] != -1) {
						if(rockFound > 1) {
							rockFound = 0;
							actionCounter = 0;
							atObject(rock);
							lastFish = System.currentTimeMillis();
						}
						rockFound++;
					}
//				}
				return random(550, 750);
//				return this.getSkillingTimeout() > 0 ? this.getSkillingTimeout() : random(550, 750);
			}
			
		}

		return random(2000, 3000);
	}
	
	private int rockFound = 0;

//	@Override
//	public int[] getObjectByIdWithinRadius(int radius, int rx, int ry, int... ids) {
//		int[] obj = { -1, -1, -1, -1, -1 };
//		int max_dist = Integer.MAX_VALUE;
//		for (int i = 0; i < getObjectCount(); i++) {
//			if (Utilities.inArray(ids, this.bot.objectID[i])) {
//				int x = this.bot.objectX[i] + getAreaX();
//				int y = this.bot.objectY[i] + getAreaY();
//				int dist = distanceTo(x, y, getX(), getY());
//				int rdist = distanceTo(x, y, rx, ry);
//				if (rdist < radius && dist < max_dist) {
//					System.out.println(String.format("%d %d %d dist %d < %d %d %d dist %d", x, y, this.bot.objectID[i],
//							dist, obj[1], obj[2], obj[0], max_dist));
//					obj[0] = this.bot.objectID[i];
//					obj[1] = x;
//					obj[2] = y;
//					obj[3] = i;
//					obj[4] = this.bot.objectType[i];
//					if (dist <= 1)
//						return obj;
//				} else
//					System.out.println(rdist);
//			}
//		}
//		return obj;
//	}
	
	private int getTotalMiningActions() {
//		int stat = getCurrentStatLevel(14) / 10;
//		if(stat == 0)
//			stat = 1;
//		return stat;
		if(getInventoryCount(1260) > 1 && getCurrentStatLevel(14) >= 21)
			return 6;
		
		return 1;
	}
	
//	String[] miningResults = {"You manage to obtain some", "You only succeed in scratching the rock"};
	private long lastAction;
	private boolean needToMove = false;

	@Override
	public void onMessage(String message, int type, int status) {
		if(type == 3 && status == 0) {
			if(message.startsWith("You only succeed in scratching the rock")) {
				System.out.println("ac: " + actionCounter);
				actionCounter++;
				lastAction = System.currentTimeMillis();
				lastActionThreshold = random(500, 1250);
				return;
			}
			if(message.startsWith("You manage to obtain some")) {
				System.out.println("ac: " + actionCounter);
				actionCounter = Integer.MAX_VALUE;
				lastAction = System.currentTimeMillis();
				lastActionThreshold = random(500, 1250);
				return;
			}			
			if(message.equals("@cya@You have been standing here for 5 mins! Please move to a new area") || message.equals("@cya@You have been standing here for 10 mins! Please move to a new area")) {
				this.needToMove  = true;
				oldX = getX();
				oldY = getY();
			}
		}
		
	}
}