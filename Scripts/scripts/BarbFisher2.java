package scripts;


import org.rscdaemon.client.bot.Extension;
import org.rscdaemon.client.bot.MessageListener;
import org.rscdaemon.client.bot.Script;

import scripts.PathWalker.Path;


public class BarbFisher2 extends Script implements MessageListener {

	public BarbFisher2(Extension bot) {
		super(bot);
	}

	int[] fishSpotIds;
	Path toBank, toFish;
	PathWalker pw;
	boolean finished = false;

	@Override
	public boolean init() {
		fishSpotIds = new int[] {192};
		fatigueMark = 95;//getFatigueMark(95);
		pw = new PathWalker(super.e);
		pw.scriptInit();
		//start 209, 501 (spot
		toBank = pw.calcPath(209, 501, 217, 447);
		toFish = pw.calcPath(217, 447, 209, 501);
		return true;
	}

	int fatigueMark;
	private long lastFish;

	@Override
	public long run() {
		if(isLoggedIn()) {
			if(isSleeping()) {
				return random(750, 1250);
			}
			if(this.getInventoryCount() == 30) {
				int[] banker = this.getNpcById(BANKERS);
				if(inArea(212, 448, 220, 453)) {
					//in bank
//					pw.setPath(null);
					if(inBank()) {
						if(getInventoryCount(358, 359) > 0) {
							this.depositAll(358, 359);
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
					System.out.println("a");
					return 1000;
				}
				else if(pw.pathIsNull()) {
					System.out.println("b");
					pw.setPath(toBank);
				} else {
					System.out.println("c");
					//walk into bank
					int[] door = getObjectFromCoords(217, 447);
					if(door[0] == 64) {
						this.atObject(door);
					} else {
						System.out.println("d");
						walkTo(217, 449);
					}
					return random(750, 1250);
				}
			}
			if(inArea(212, 448, 220, 453)) {
				if(inBank())
					this.closeBank(); //checks to ensure your in bank already
				int[] door = this.getObjectFromCoords(217, 447);
				if(door[0] == 64) {
					this.atObject(door);
				} else {
					walkTo(217, 447);
				}
				return random(750, 1250);
			}
			if(this.getFatigue() > fatigueMark) {
				fatigueMark = 95;//this.getFatigueMark(95);
				//useItem(this.getInventoryItemIndex(1263));
				useSleepingBag();
				return random(1250, 2500);
			}
			if(distanceTo(209, 501) > 8) {
				//walk back
				if(pw.pathIsNull()) {
					pw.setPath(toFish);
				}
				if(pw.walkPath()) {
					return 100;
				} else {
					//walk into bank
					int[] door = this.getObjectFromCoords(217, 447);
					if(door[0] == 64) {
						this.atObject(door);
					} else {
						walkTo(217, 448);
					}
					return random(750, 1250);
				}
			}
//			else if(!pw.pathIsNull()) {
//				pw.setPath(null);
//			}
			else {
				
				if(((System.currentTimeMillis() - lastFish > random(30000, 90000)) || (actionCounter >= getTotalFishActions() && System.currentTimeMillis() - lastAction > lastActionThreshold))) {
					
					if(needToMove) {
						if(getX() != oldX || getY() !=  oldY) {
							needToMove = false;
						} else {
							int[] moveCoords = this.getMoveCoords();
							walkTo(moveCoords[0], moveCoords[1]);
							return random(1250, 5000);
						}
					}
					
					int[] fishSpot = this.getObjectById(fishSpotIds);
					System.out.println("time: " + (lastActionThreshold - (System.currentTimeMillis() - lastAction)));
					// 
					if(fishSpot[0] != -1) {
						actionCounter = 0;
						atObject(fishSpot[1], fishSpot[2]);
						lastFish = System.currentTimeMillis();
						
					}
				}
//				return this.getSkillingTimeout() > 0 ? this.getSkillingTimeout() : random(550, 750);
			}
		}

		return random(400, 600);
	}
	
	private int getTotalFishActions() {
		int stat = getCurrentStatLevel(10) / 10;
		if(stat == 0)
			stat = 1;
		return stat;
	}

	String[] fishingResults = {"You attempt to catch some fish", "You fail to catch anything", "You catch a "};
	private long lastAction;
	private int actionCounter = Integer.MAX_VALUE;
	private int lastActionThreshold;
	private boolean needToMove;
	private int oldX;
	private int oldY;

	@Override
	public void onMessage(String message, int type, int status) {
		if(type == 3 && status == 0) {
			for(String s : fishingResults) {
				if(message.startsWith(s)) {
					actionCounter++;
					lastAction = System.currentTimeMillis();
					lastActionThreshold = random(250, 450);
					return;
				}
			}
			if(message.equals("@cya@You have been standing here for 5 mins! Please move to a new area")) {
				this.needToMove = true;
				oldX = getX();
				oldY = getY();
			}
		}
	}

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

}